package mygame.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import mygame.main.GamePanel;

public class Chicken extends Entity {
    
    GamePanel gp;
    
    // --- Hệ thống HP & Trạng thái ---
    private boolean hpBarOn = false;
    private int hpBarCounter = 0;

    // --- AI DI CHUYỂN & NÉ VẬT CẢN ---
    private int stuckCooldown = 0;
    private String lastBlockedDirection = "";
    private int avoidTimer = 0;
    private String avoidDirection = null;

    // Ảnh animation
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;

    public Chicken(GamePanel gp, int startX, int startY) {
        super(gp);
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.speed = 2;
        this.direction = "down";
        
        this.maxLife = 100;
        this.life = maxLife;
        this.alive = true;

        // Hitbox nhỏ gọn (28x28) giúp chui lọt khe hẹp và né nhau mượt hơn
        solidArea = new Rectangle(18, 18, 28, 28); 
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getChickenImage();
    }

    public void getChickenImage() {
        try {
            // Ảnh trạng thái bình thường (Ngủ)
            up1 = setup("/res/tiles/chicken_ngu.png");
            down1 = up1; left1 = up1; right1 = up1;

            // Ảnh trạng thái đuổi theo (Angry)
            up1_egg = setup("/res/tiles/chicken_up1.png");
            up2_egg = setup("/res/tiles/chicken_up2.png");
            down1_egg = setup("/res/tiles/chicken_down1.png");
            down2_egg = setup("/res/tiles/chicken_down2.png");
            left1_egg = setup("/res/tiles/chicken_left1.png");
            left2_egg = setup("/res/tiles/chicken_left2.png");
            right1_egg = setup("/res/tiles/chicken_right1.png");
            right2_egg = setup("/res/tiles/chicken_right2.png");
        } catch (IOException e) {
            System.out.println("Lỗi tải ảnh gà!");
        }
    }

    public BufferedImage setup(String path) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(path));
    }

    @Override
    public void update() {
        // 1. Xử lý thời gian bất tử và nhấp nháy
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }

        if (stuckCooldown > 0) stuckCooldown--;

        // 2. Tính khoảng cách đến Player
        int diffX = (gp.player.x + gp.tileSize/2) - (this.x + gp.tileSize/2);
        int diffY = (gp.player.y + gp.tileSize/2) - (this.y + gp.tileSize/2);
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        // 3. AI đuổi theo khi Player có trứng
        if (gp.player.hasEgg && distance < 350) {
            attacking = true; 
            moveTowardPlayer(diffX, diffY);
        } else {
            attacking = false;
            spriteNum = 1;
            return;
        }

        // 4. Animation chân chạy
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private void moveTowardPlayer(int diffX, int diffY) {
        if (avoidTimer > 0 && avoidDirection != null) {
            if (tryMove(avoidDirection)) {
                avoidTimer--;
                return;
            } else {
                avoidTimer = 0;
            }
        }

        String primaryDir, secondaryDir;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            primaryDir = (diffX > 0) ? "right" : "left";
            secondaryDir = (diffY > 0) ? "down" : "up";
        } else {
            primaryDir = (diffY > 0) ? "down" : "up";
            secondaryDir = (diffX > 0) ? "right" : "left";
        }

        // Thử hướng chính, nếu kẹt (tường hoặc gà khác) thì thử hướng phụ
        if (!tryMove(primaryDir)) {
            if (tryMove(secondaryDir)) {
                avoidDirection = secondaryDir;
                avoidTimer = 15;
            }
        }
    }

    private boolean tryMove(String dir) {
        if (dir.equals(lastBlockedDirection) && stuckCooldown > 0) return false;

        String oldDir = this.direction;
        this.direction = dir;
        collisionOn = false;

        // KIỂM TRA VA CHẠM (3 lớp chặn)
        gp.cChecker.checkTile(this);                // 1. Chạm tường
        gp.cChecker.checkEntityCollision(this, gp.monster); // 2. Chạm con gà khác
        gp.cChecker.checkPlayer(this);              // 3. Chạm người chơi

        if (!collisionOn) {
            switch (dir) {
                case "up": y -= speed; break;
                case "down": y += speed; break;
                case "left": x -= speed; break;
                case "right": x += speed; break;
            }
            return true;
        } else {
            lastBlockedDirection = dir;
            stuckCooldown = 15;
            this.direction = oldDir;
            return false;
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (!invincible) {
            life -= damage;
            hpBarOn = true;
            hpBarCounter = 0;
            
            // Kích hoạt nhấp nháy (Không đẩy lùi)
            invincible = true;
            invincibleCounter = 0;

            if (life <= 0) {
                life = 0;
                alive = false; 
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (!attacking) {
            image = up1; 
        } else {
            switch (direction) {
                case "up": image = (spriteNum == 1) ? up1_egg : up2_egg; break;
                case "down": image = (spriteNum == 1) ? down1_egg : down2_egg; break;
                case "left": image = (spriteNum == 1) ? left1_egg : left2_egg; break;
                case "right": image = (spriteNum == 1) ? right1_egg : right2_egg; break;
            }
        }

        // --- HIỆU ỨNG NHẤP NHÁY ---
        // Nếu đang trong trạng thái bất tử, cứ mỗi 10 frame sẽ ẩn ảnh trong 5 frame
        if (invincible && invincibleCounter % 10 < 5) {
            // Không vẽ gì (tạo khoảng trống nhấp nháy)
        } else {
            if (image != null) {
                g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
            }
        }

        // --- THANH MÁU ---
        if (hpBarOn) {
            double oneScale = (double) gp.tileSize / maxLife;
            double hpBarValue = oneScale * life;
            g2.setColor(new Color(35, 35, 35));
            g2.fillRect(x - 1, y - 11, gp.tileSize + 2, 10);
            g2.setColor(new Color(255, 0, 30));
            g2.fillRect(x, y - 10, (int) hpBarValue, 8);
            
            hpBarCounter++;
            if (hpBarCounter > 300) hpBarOn = false;
        }
    }
} 