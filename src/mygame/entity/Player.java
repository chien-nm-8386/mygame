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
import mygame.main.VictoryDialog;

public class Player extends Entity {

    KeyHandler keyH;

    public boolean hasEgg = false;
    public boolean hasWeapon = false; 
    public String name = "Player";

    public int health = 100;
    public int maxHealth = 100;

    private int attackCounter = 0;
    private boolean victoryShown = false;
    private boolean gameOverShown = false;

    // Ảnh di chuyển các trạng thái
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;
    public BufferedImage up1_weapon, up2_weapon, down1_weapon, down2_weapon, left1_weapon, left2_weapon, right1_weapon, right2_weapon;

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp); // Gọi constructor của Entity
        this.keyH = keyH;

        // Định dạng vùng va chạm vật lý (body)
        solidArea = new Rectangle();
        solidArea.x = 12;
        solidArea.y = 24;
        solidArea.width = 24;
        solidArea.height = 20;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = gp.tileM.playerStartX;
        y = gp.tileM.playerStartY;
        speed = 3;
        direction = "down";
        
        hasEgg = false;
        hasWeapon = false; 
        attacking = false;
        health = 100;
        invincible = false;
    }

    public void getPlayerImage() {
        try {
            // Player thường (01)
            up1 = setup("/res/tiles/player01_up1.png");
            up2 = setup("/res/tiles/player01_up2.png");
            down1 = setup("/res/tiles/player01_down1.png");
            down2 = setup("/res/tiles/player01_down2.png");
            left1 = setup("/res/tiles/player01_left1.png");
            left2 = setup("/res/tiles/player01_left2.png");
            right1 = setup("/res/tiles/player01_right1.png");
            right2 = setup("/res/tiles/player01_right2.png");

            // Player trứng (02)
            up1_egg = setup("/res/tiles/player02_up1.png");
            up2_egg = setup("/res/tiles/player02_up2.png");
            down1_egg = setup("/res/tiles/player02_down1.png");
            down2_egg = setup("/res/tiles/player02_down2.png");
            left1_egg = setup("/res/tiles/player02_left1.png");
            left2_egg = setup("/res/tiles/player02_left2.png");
            right1_egg = setup("/res/tiles/player02_right1.png");
            right2_egg = setup("/res/tiles/player02_right2.png");

            // Player cầm vũ khí di chuyển (03)
            up1_weapon = setup("/res/tiles/player03_up1.png");
            up2_weapon = setup("/res/tiles/player03_up2.png");
            down1_weapon = setup("/res/tiles/player03_down1.png");
            down2_weapon = setup("/res/tiles/player03_down2.png");
            left1_weapon = setup("/res/tiles/player03_left1.png");
            left2_weapon = setup("/res/tiles/player03_left2.png");
            right1_weapon = setup("/res/tiles/player03_right1.png");
            right2_weapon = setup("/res/tiles/player03_right2.png");

            // Player hành động chém (04)
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

    @Override
    public void update() {
        if (attacking) {
            attacking();
        } else {
            // Kiểm tra nhấn Space để chém (Nếu đã nhặt được vũ khí)
            if (keyH.spacePressed && hasWeapon) {
                attacking = true;
                attackCounter = 0;
            } 
            else if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
                if (keyH.upPressed) direction = "up";
                else if (keyH.downPressed) direction = "down";
                else if (keyH.leftPressed) direction = "left";
                else if (keyH.rightPressed) direction = "right";

                collisionOn = false;
                gp.cChecker.checkTile(this);
                checkObjectInteraction();

                if (!collisionOn) {
                    switch (direction) {
                        case "up": y -= speed; break;
                        case "down": y += speed; break;
                        case "left": x -= speed; break;
                        case "right": x += speed; break;
                    }
                }

                spriteCounter++;
                if (spriteCounter > 12) {
                    spriteNum = (spriteNum == 1) ? 2 : 1;
                    spriteCounter = 0;
                }
            } else {
                spriteNum = 1;
            }
        }

        // Xử lý thời gian bất tử khi bị quái chạm vào
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void attacking() {
        attackCounter++;

        // Hiệu ứng tạo Hitbox chém ở frame thứ 5 đến 10
        if (attackCounter <= 15) {
            int currentWorldX = x;
            int currentWorldY = y;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Đẩy hitbox ra xa theo hướng chém
            switch(direction) {
                case "up": y -= gp.tileSize; break;
                case "down": y += gp.tileSize; break;
                case "left": x -= gp.tileSize; break;
                case "right": x += gp.tileSize; break;
            }
            solidArea.width = gp.tileSize;
            solidArea.height = gp.tileSize;

            checkAttackMonster();

            // Trả lại thông số body gốc
            x = currentWorldX;
            y = currentWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }

        if (attackCounter > 10) {
            attacking = false;
            attackCounter = 0;
        }
    }

    public void checkAttackMonster() {
        for (int i = 0; i < gp.monster.length; i++) {
            if (gp.monster[i] != null) {
                if (getBounds().intersects(gp.monster[i].getBounds())) {
                    gp.monster[i].takeDamage(20); // Gà trừ 20 máu
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
        }
        // Nhặt vũ khí (yêu cầu có trứng trước)
        if (hasEgg && !hasWeapon && gp.tileM.weaponRect != null && pRect.intersects(gp.tileM.weaponRect)) {
            hasWeapon = true;
            gp.tileM.weaponRect = null;
        }
        // Về nhà chiến thắng
        if (gp.tileM.houseRect != null && pRect.intersects(gp.tileM.houseRect)) {
            if (hasEgg && !victoryShown) {
                victoryShown = true;
                gp.stopGameThread();
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
                new VictoryDialog(parentFrame, name, gp.main).showDialog();
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (attacking) {
            switch (direction) {
                case "up": image = attackUp; break;
                case "down": image = attackDown; break;
                case "left": image = attackLeft; break;
                case "right": image = attackRight; break;
            }
        } else {
            switch (direction) {
                case "up":
                    if (hasWeapon) image = (spriteNum == 1) ? up1_weapon : up2_weapon;
                    else if (hasEgg) image = (spriteNum == 1) ? up1_egg : up2_egg;
                    else image = (spriteNum == 1) ? up1 : up2;
                    break;
                case "down":
                    if (hasWeapon) image = (spriteNum == 1) ? down1_weapon : down2_weapon;
                    else if (hasEgg) image = (spriteNum == 1) ? down1_egg : down2_egg;
                    else image = (spriteNum == 1) ? down1 : down2;
                    break;
                case "left":
                    if (hasWeapon) image = (spriteNum == 1) ? left1_weapon : left2_weapon;
                    else if (hasEgg) image = (spriteNum == 1) ? left1_egg : left2_egg;
                    else image = (spriteNum == 1) ? left1 : left2;
                    break;
                case "right":
                    if (hasWeapon) image = (spriteNum == 1) ? right1_weapon : right2_weapon;
                    else if (hasEgg) image = (spriteNum == 1) ? right1_egg : right2_egg;
                    else image = (spriteNum == 1) ? right1 : right2;
                    break;
            }
        }

        // Nhấp nháy khi bất tử (vừa bị quái chạm vào)
        if (!(invincible && invincibleCounter % 6 < 3)) {
            if (image != null) {
                g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
            }
        }
        drawPlayerUI(g2);
    }

    private void drawPlayerUI(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (gp.tileSize - fm.stringWidth(name)) / 2;
        int textY = y - 10;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(name, textX + 2, textY + 2);
        g2.setColor(Color.WHITE);
        g2.drawString(name, textX, textY);
    }

    @Override
    public void takeDamage(int damage) {
        if (!invincible && health > 0) {
            health -= damage;
            if (health < 0) health = 0;
            invincible = true;
            invincibleCounter = 0;
            if (health <= 0) triggerGameOver();
        }
    }

    public void triggerGameOver() {
        if (!gameOverShown) {
            gameOverShown = true;
            gp.stopGameThread();
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
            new GameOverDialog(parentFrame, name, gp.main).showDialog();
        }
    }
}