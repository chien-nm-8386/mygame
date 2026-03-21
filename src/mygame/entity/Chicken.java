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
    
    // --- Hệ thống máu (HP) ---
    public int maxLife = 4;
    public int life = maxLife;
    public boolean isAngry = false;
    private boolean hpBarOn = false;
    private int hpBarCounter = 0;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    // --- Chống rung và AI né vật cản ---
    private int stuckCooldown = 0;
    private String lastBlockedDirection = "";
    private String avoidDirection = null;
    private int avoidTimer = 0;

    // Ảnh
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;

    public Chicken(GamePanel gp, int startX, int startY) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.speed = 2;
        this.direction = "down";

        // Hitbox dùng để va chạm với tường (Tile)
        solidArea = new Rectangle(20, 20, 24, 24); 

        getChickenImage();
    }

    // --- HÀM SỬA LỖI TRONG GAMEPANEL ---
    // Hàm này trả về vùng va chạm để Player có thể check intersects
    public Rectangle getBounds() {
        return new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
    }

    public void getChickenImage() {
        try {
            // Ảnh trạng thái bình thường
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_ngu.png"));
            down1 = up1; left1 = up1; right1 = up1;

            // Ảnh trạng thái đuổi theo người chơi
            up1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_up1.png"));
            up2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_up2.png"));
            down1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_down1.png"));
            down2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_down2.png"));
            left1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_left1.png"));
            left2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_left2.png"));
            right1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_right1.png"));
            right2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_right2.png"));

        } catch (IOException | NullPointerException e) {
            System.out.println("Lỗi tải ảnh gà!");
        }
    }

    public void update() {
        if (stuckCooldown > 0) stuckCooldown--;

        // Tính khoảng cách đến Player
        int chickenCenterX = this.x + gp.tileSize / 2;
        int chickenCenterY = this.y + gp.tileSize / 2;
        int playerCenterX = gp.player.x + gp.tileSize / 2;
        int playerCenterY = gp.player.y + gp.tileSize / 2;

        int diffX = playerCenterX - chickenCenterX;
        int diffY = playerCenterY - chickenCenterY;
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        // Kiểm tra điều kiện đuổi theo
        if (gp.player.hasEgg && distance < 220) {
            isAngry = true;
            hpBarOn = true;
            hpBarCounter = 0; 
        } else {
            isAngry = false;
            spriteNum = 1;
            spriteCounter = 0;
            return;
        }

        moveTowardPlayer(diffX, diffY);

        // Animation
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
                avoidDirection = null;
            }
        }

        String primaryDirection, secondaryDirection;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            primaryDirection = (diffX > 0) ? "right" : "left";
            secondaryDirection = (diffY > 0) ? "down" : "up";
        } else {
            primaryDirection = (diffY > 0) ? "down" : "up";
            secondaryDirection = (diffX > 0) ? "right" : "left";
        }

        if (!tryMove(primaryDirection)) {
            if (tryMove(secondaryDirection)) {
                avoidDirection = secondaryDirection;
                avoidTimer = 10;
            } else {
                tryAlternativeMove(primaryDirection, diffX, diffY);
            }
        }
    }

    private void tryAlternativeMove(String primaryDirection, int diffX, int diffY) {
        String alt1, alt2;
        if (primaryDirection.equals("left") || primaryDirection.equals("right")) {
            alt1 = (diffY < 0) ? "up" : "down";
            alt2 = (diffY < 0) ? "down" : "up";
        } else {
            alt1 = (diffX < 0) ? "left" : "right";
            alt2 = (diffX < 0) ? "right" : "left";
        }

        if (tryMove(alt1)) {
            avoidDirection = alt1;
            avoidTimer = 18;
        } else if (tryMove(alt2)) {
            avoidDirection = alt2;
            avoidTimer = 18;
        }
    }

    private boolean tryMove(String dir) {
        if (dir.equals(lastBlockedDirection) && stuckCooldown > 0) return false;

        int nextX = x, nextY = y;
        switch (dir) {
            case "up": nextY -= speed; break;
            case "down": nextY += speed; break;
            case "left": nextX -= speed; break;
            case "right": nextX += speed; break;
        }

        // Check va chạm tường bằng Collision Rects từ TileManager
        Rectangle nextBounds = new Rectangle(nextX + solidArea.x, nextY + solidArea.y, solidArea.width, solidArea.height);
        for (Rectangle rect : gp.tileM.collisionRects) {
            if (nextBounds.intersects(rect)) {
                lastBlockedDirection = dir;
                stuckCooldown = 12;
                return false;
            }
        }

        x = nextX;
        y = nextY;
        direction = dir;
        return true;
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (!isAngry) {
            image = up1;
        } else {
            switch (direction) {
                case "up": image = (spriteNum == 1) ? up1_egg : up2_egg; break;
                case "down": image = (spriteNum == 1) ? down1_egg : down2_egg; break;
                case "left": image = (spriteNum == 1) ? left1_egg : left2_egg; break;
                case "right": image = (spriteNum == 1) ? right1_egg : right2_egg; break;
            }
        }

        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }

        // Vẽ thanh máu
        if (hpBarOn) {
            double oneScale = (double) gp.tileSize / maxLife;
            double hpBarValue = oneScale * life;

            g2.setColor(new Color(35, 35, 35));
            g2.fillRect(x - 1, y - 11, gp.tileSize + 2, 10);
            g2.setColor(new Color(255, 0, 30));
            g2.fillRect(x, y - 10, (int) hpBarValue, 8);

            hpBarCounter++;
            if (hpBarCounter > 600) {
                hpBarCounter = 0;
                hpBarOn = false;
            }
        }
    }
}