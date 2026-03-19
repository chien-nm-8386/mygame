package mygame.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import mygame.main.GamePanel;

public class Chicken extends Entity {
    
    GamePanel gp;
    public boolean isAngry = false;

    // Biến hoạt ảnh
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // Ảnh gà nổi điên
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;

    public Chicken(GamePanel gp, int startX, int startY) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.speed = 0; // Đứng yên ban đầu
        this.direction = "down";

        // Hitbox: 24x24 (nhỏ hơn tileSize 48 để gà lách mê cung mượt)
        solidArea = new Rectangle(12, 12, 24, 24); 
        
        getChickenImage();
    }

    public void getChickenImage() {
        try {
            // Ảnh bình thường
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_ngu.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_ngu.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_ngu.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_ngu.png"));

            // Ảnh nổi điên (chạy)
            up1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_up1.png"));
            up2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_up2.png"));
            down1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_down1.png"));
            down2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_down2.png"));
            left1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_left1.png"));
            left2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_left2.png"));
            right1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_right1.png"));
            right2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_right2.png"));
            
        } catch (IOException | NullPointerException e) {
            System.out.println("Lỗi tải ảnh gà. Kiểm tra lại thư mục res!");
        }
    }

    public void update() {
        // --- BƯỚC 1: TÍNH KHOẢNG CÁCH ---
        double diffX_val = gp.player.x - this.x;
        double diffY_val = gp.player.y - this.y;
        double distance = Math.sqrt(Math.pow(diffX_val, 2) + Math.pow(diffY_val, 2));

        // --- BƯỚC 2: KIỂM TRA ĐIỀU KIỆN (CÓ TRỨNG VÀ GẦN 200PX) ---
        if (gp.player.hasEgg && distance < 200) {
            if (!isAngry) {
                isAngry = true;
                this.speed = 2; // Tốc độ đuổi
                System.out.println("Gà: Thấy trộm rồi! Đuổi thôiiii!");
            }
        } else {
            // Nếu mất dấu (xa hơn 200px) hoặc chưa có trứng thì đứng im
            isAngry = false;
            this.speed = 0;
        }

        // --- BƯỚC 3: XỬ LÝ ĐUỔI THEO ---
        if (isAngry) {
            // Xác định hướng dựa trên vị trí Player
            int diffX = gp.player.x - this.x;
            int diffY = gp.player.y - this.y;

            if (Math.abs(diffX) > Math.abs(diffY)) {
                direction = (diffX > 0) ? "right" : "left";
            } else {
                direction = (diffY > 0) ? "down" : "up";
            }

            // Kiểm tra va chạm Tile (Tiled Map)
            collisionOn = false;
            gp.cChecker.checkTile(this);

            if (!collisionOn) {
                switch (direction) {
                    case "up":    this.y -= speed; break;
                    case "down":  this.y += speed; break;
                    case "left":  this.x -= speed; break;
                    case "right": this.x += speed; break;
                }
            } else {
                // AI lách vật cản cơ bản
                if (direction.equals("up") || direction.equals("down")) {
                    this.x += (diffX > 0) ? speed : -speed;
                } else {
                    this.y += (diffY > 0) ? speed : -speed;
                }
            }

            // Hoạt ảnh
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        if (!isAngry) {
            switch (direction) {
                case "up":    image = up1; break;
                case "down":  image = down1; break;
                case "left":  image = left1; break;
                case "right": image = right1; break;
            }
        } else {
            switch (direction) {
                case "up":    image = (spriteNum == 1) ? up1_egg : up2_egg; break;
                case "down":  image = (spriteNum == 1) ? down1_egg : down2_egg; break;
                case "left":  image = (spriteNum == 1) ? left1_egg : left2_egg; break;
                case "right": image = (spriteNum == 1) ? right1_egg : right2_egg; break;
            }
        }
        
        if (image != null) {
            g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        }
        
        // --- DEBUG (Vòng tròn tầm nhìn 200px) ---
        
        g2.setColor(new java.awt.Color(255, 0, 0, 50));
        g2.drawOval(x - 200 + gp.tileSize/2, y - 200 + gp.tileSize/2, 400, 400);
        
    }
}