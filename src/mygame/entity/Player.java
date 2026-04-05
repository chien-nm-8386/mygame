package mygame.entity;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import mygame.main.GameOverDialog;
import mygame.main.GamePanel;
import mygame.main.KeyHandler;
import mygame.main.Sound;

public class Player extends Entity {

    KeyHandler keyH;

    // --- THÔNG TIN NGƯỜI CHƠI & TRẠNG THÁI ---
    public String name = "Player";
    public int health = 100;
    public int maxHealth = 100;
    public boolean hasEgg = false;
    public boolean hasWeapon = false;
    private boolean gameOverShown = false;

    // --- HỆ THỐNG ÂM THANH ---
    private final Sound footstepSound = new Sound();
    private boolean isFootstepPlaying = false;

    // --- LOGIC CHIẾN ĐẤU ---
    private int attackCounter = 0;
    private boolean attackHitDone = false;

    // --- HÌNH ẢNH (SPRITES) ---
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;
    public BufferedImage up1_weapon, up2_weapon, down1_weapon, down2_weapon, left1_weapon, left2_weapon, right1_weapon, right2_weapon;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        // Định nghĩa vùng va chạm cho Player
        solidArea = new Rectangle(12, 24, 24, 20);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    // =========================================================================
    // 1. KHỞI TẠO & CẤU HÌNH (SETUP)
    // =========================================================================

    public void setDefaultValues() {
        x = gp.tileM.playerStartX;
        y = gp.tileM.playerStartY;
        speed = 3;
        direction = "down";
        
        hasEgg = false;
        hasWeapon = false;
        attacking = false;
        health = 100;
        alive = true;
        invincible = false;
        invincibleCounter = 0;
        gameOverShown = false;

        stopFootstepSound();
        applyFootstepVolume();
    }

    public void getPlayerImage() {
        footstepSound.setFile("/res/audio/footstep.wav");
        applyFootstepVolume();

        try {
            // Sprites cơ bản
            up1 = setup("/res/tiles/player01_up1.png");
            up2 = setup("/res/tiles/player01_up2.png");
            down1 = setup("/res/tiles/player01_down1.png");
            down2 = setup("/res/tiles/player01_down2.png");
            left1 = setup("/res/tiles/player01_left1.png");
            left2 = setup("/res/tiles/player01_left2.png");
            right1 = setup("/res/tiles/player01_right1.png");
            right2 = setup("/res/tiles/player01_right2.png");

            // Sprites khi cầm trứng
            up1_egg = setup("/res/tiles/player02_up1.png");
            up2_egg = setup("/res/tiles/player02_up2.png");
            down1_egg = setup("/res/tiles/player02_down1.png");
            down2_egg = setup("/res/tiles/player02_down2.png");
            left1_egg = setup("/res/tiles/player02_left1.png");
            left2_egg = setup("/res/tiles/player02_left2.png");
            right1_egg = setup("/res/tiles/player02_right1.png");
            right2_egg = setup("/res/tiles/player02_right2.png");

            // Sprites khi cầm vũ khí
            up1_weapon = setup("/res/tiles/player03_up1.png");
            up2_weapon = setup("/res/tiles/player03_up2.png");
            down1_weapon = setup("/res/tiles/player03_down1.png");
            down2_weapon = setup("/res/tiles/player03_down2.png");
            left1_weapon = setup("/res/tiles/player03_left1.png");
            left2_weapon = setup("/res/tiles/player03_left2.png");
            right1_weapon = setup("/res/tiles/player03_right1.png");
            right2_weapon = setup("/res/tiles/player03_right2.png");

            // Animation tấn công
            attackUp = setup("/res/tiles/player04_up.png");
            attackDown = setup("/res/tiles/player04_down.png");
            attackLeft = setup("/res/tiles/player04_left.png");
            attackRight = setup("/res/tiles/player04_right.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage setup(String imageName) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(imageName));
    }

    // =========================================================================
    // 2. HỆ THỐNG ÂM THANH (AUDIO)
    // =========================================================================

    private void applyFootstepVolume() {
        if (footstepSound != null && footstepSound.isLoaded()) {
            footstepSound.setVolume(gp.isFootstepMuted() ? 0 : gp.getFootstepVolume());
        }
    }

    public void refreshFootstepVolume() {
        applyFootstepVolume();
    }
    
    private void playFootstepSound() {
        applyFootstepVolume();
        if (gp.isSfxMuted() || gp.getSfxVolume() <= 0) {
            stopFootstepSound();
            return;
        }
        if (!isFootstepPlaying && footstepSound.isLoaded()) {
            footstepSound.loop();
            isFootstepPlaying = true;
        }
    }

    @Override
    public void stopFootstepSound() {
        if (footstepSound != null) {
            footstepSound.stop();
        }
        isFootstepPlaying = false;
    }

    // =========================================================================
    // 3. LOGIC CẬP NHẬT (UPDATE)
    // =========================================================================

    @Override
    public void update() {
        if (attacking) {
            handleAttackingLogic();
        } else {
            handleMovementAndActions();
        }

        // Xử lý thời gian bất tử
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    private void handleMovementAndActions() {
        // Tấn công bằng phím Space
        if (keyH.consumeSpaceJustPressed() && hasWeapon) {
            stopFootstepSound();
            attacking = true;
            attackCounter = 0;
            attackHitDone = false;
            gp.playSlashSound();
            return;
        }

        // Di chuyển bằng phím mũi tên/WASD
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (!collisionOn) {
                switch (direction) {
                    case "up": y -= speed; break;
                    case "down": y += speed; break;
                    case "left": x -= speed; break;
                    case "right": x += speed; break;
                }
                playFootstepSound();
            } else {
                stopFootstepSound();
            }

            checkObjectInteraction();
            updateAnimationSprite();

        } else {
            // Đứng yên
            spriteNum = 1;
            stopFootstepSound();
            checkObjectInteraction();
        }
    }

    private void updateAnimationSprite() {
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    // =========================================================================
    // 4. CHIẾN ĐẤU & TƯƠNG TÁC (COMBAT & INTERACTION)
    // =========================================================================

    public void handleAttackingLogic() {
        attackCounter++;

        // Tại Frame thứ 4, tạo một vùng va chạm tạm thời để kiểm tra đánh trúng
        if (!attackHitDone && attackCounter == 4) {
            checkAttackMonster();
            attackHitDone = true;
        }

        // Kết thúc animation đánh (10 frames)
        if (attackCounter > 10) {
            attacking = false;
            attackCounter = 0;
            attackHitDone = false;
        }
    }

    public void checkAttackMonster() {
        Rectangle playerBody = getBounds();
        Rectangle attackBox = null;

        // Xác định vị trí vùng chém dựa trên hướng nhìn
        switch (direction) {
            case "up": attackBox = new Rectangle(playerBody.x, playerBody.y - gp.tileSize, playerBody.width, gp.tileSize); break;
            case "down": attackBox = new Rectangle(playerBody.x, playerBody.y + playerBody.height, playerBody.width, gp.tileSize); break;
            case "left": attackBox = new Rectangle(playerBody.x - gp.tileSize, playerBody.y, gp.tileSize, playerBody.height); break;
            case "right": attackBox = new Rectangle(playerBody.x + playerBody.width, playerBody.y, gp.tileSize, playerBody.height); break;
        }

        if (attackBox == null) return;

        // Kiểm tra va chạm với từng con gà trong danh sách
        for (Chicken chicken : gp.chickens) {
            if (chicken != null && chicken.alive) {
                if (attackBox.intersects(chicken.getBounds())) {
                    chicken.takeDamage(50);
                    break; // Một phát chém trúng 1 mục tiêu
                }
            }
        }
    }

    public void checkObjectInteraction() {
        Rectangle pRect = getBounds();

        // Nhặt trứng
        if (!hasEgg && gp.tileM.eggRect != null && pRect.intersects(gp.tileM.eggRect)) {
            hasEgg = true;
            gp.tileM.eggCollected = true;
            gp.tileM.eggRect = null;
            stopFootstepSound();
            gp.playEggSound();
        }

        // Nhặt vũ khí (yêu cầu phải có trứng trước)
        if (hasEgg && !hasWeapon && gp.tileM.weaponRect != null && pRect.intersects(gp.tileM.weaponRect)) {
            hasWeapon = true;
            gp.tileM.weaponRect = null;
            stopFootstepSound();
            gp.playWeaponSound();
            System.out.println("Da nhat vu khi");
        }
    }

    // =========================================================================
    // 5. NHẬN SÁT THƯƠNG & KẾT THÚC GAME
    // =========================================================================

    @Override
    public void takeDamage(int damage) {
        if (!invincible && health > 0) {
            health -= damage;
            if (health < 0) health = 0;
            invincible = true;
            invincibleCounter = 0;

            // PHÁT ÂM THANH KHI NGƯỜI CHƠI BỊ ĐÁNH
            gp.playHurtSound();

            if (health <= 0) {
                triggerGameOver();
            }
        }
    }

    public void triggerGameOver() {
        if (gameOverShown) return;
        gameOverShown = true;

        stopFootstepSound();
        gp.stopAllSounds();
        gp.playGameOverMusic();
        gp.gameState = gp.STATE_GAME_OVER;

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
        new GameOverDialog(parentFrame, name, gp.main).showDialog();
    }

    // =========================================================================
    // 6. HIỂN THỊ ĐỒ HỌA (DRAWING)
    // =========================================================================

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = determinePlayerImage();

        // Vẽ hiệu ứng nhấp nháy khi bất tử
        if (!(invincible && invincibleCounter % 6 < 3)) {
            if (image != null) {
                if (hasEgg) drawEggAura(g2); // Hiệu ứng hào quang nếu cầm trứng
                g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
            }
        }
        drawPlayerUI(g2);
    }

    private BufferedImage determinePlayerImage() {
        if (attacking) {
            stopFootstepSound();
            switch (direction) {
                case "up": return attackUp;
                case "down": return attackDown;
                case "left": return attackLeft;
                case "right": return attackRight;
                default: return attackDown;
            }
        }

        // Chọn ảnh dựa trên hướng và vật phẩm đang cầm
        switch (direction) {
            case "up":
                if (hasWeapon) return (spriteNum == 1) ? up1_weapon : up2_weapon;
                if (hasEgg) return (spriteNum == 1) ? up1_egg : up2_egg;
                return (spriteNum == 1) ? up1 : up2;
            case "down":
                if (hasWeapon) return (spriteNum == 1) ? down1_weapon : down2_weapon;
                if (hasEgg) return (spriteNum == 1) ? down1_egg : down2_egg;
                return (spriteNum == 1) ? down1 : down2;
            case "left":
                if (hasWeapon) return (spriteNum == 1) ? left1_weapon : left2_weapon;
                if (hasEgg) return (spriteNum == 1) ? left1_egg : left2_egg;
                return (spriteNum == 1) ? left1 : left2;
            case "right":
                if (hasWeapon) return (spriteNum == 1) ? right1_weapon : right2_weapon;
                if (hasEgg) return (spriteNum == 1) ? right1_egg : right2_egg;
                return (spriteNum == 1) ? right1 : right2;
            default: return down1;
        }
    }

    private void drawEggAura(Graphics2D g2) {
        int centerX = x + gp.tileSize / 2;
        int centerY = y + gp.tileSize / 2;
        // Hiệu ứng hào quang vàng mờ
        g2.setColor(new Color(255, 220, 120, 26));
        g2.fillOval(centerX - 56, centerY - 56, 112, 112);
        g2.setColor(new Color(255, 235, 150, 22));
        g2.fillOval(centerX - 40, centerY - 40, 80, 80);
        g2.setColor(new Color(255, 248, 210, 18));
        g2.fillOval(centerX - 26, centerY - 26, 52, 52);
    }

    private void drawPlayerUI(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (gp.tileSize - fm.stringWidth(name)) / 2;
        int textY = y - 10;

        // Vẽ đổ bóng cho tên
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(name, textX + 2, textY + 2);

        // Vẽ tên chính
        g2.setColor(hasEgg ? new Color(255, 245, 210) : Color.WHITE);
        g2.drawString(name, textX, textY);
    }
}