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

    public int spriteCounter = 0;
    public int spriteNum = 1;

    // --- chống rung ---
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

        // hitbox nhỏ lại để đỡ kẹt
        solidArea = new Rectangle(28, 28, 8, 8);

        getChickenImage();
    }

    public void getChickenImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/chicken_ngu.png"));
            down1 = up1;
            left1 = up1;
            right1 = up1;

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
            e.printStackTrace();
        }
    }

    public void update() {

        if (stuckCooldown > 0) stuckCooldown--;

        int chickenCenterX = this.x + gp.tileSize / 2;
        int chickenCenterY = this.y + gp.tileSize / 2;
        int playerCenterX = gp.player.x + gp.tileSize / 2;
        int playerCenterY = gp.player.y + gp.tileSize / 2;

        int diffX = playerCenterX - chickenCenterX;
        int diffY = playerCenterY - chickenCenterY;

        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (gp.player.hasEgg && distance < 220) {
            isAngry = true;
        } else {
            isAngry = false;
            spriteNum = 1;
            spriteCounter = 0;
            return;
        }

        moveTowardPlayer(diffX, diffY);

        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private void moveTowardPlayer(int diffX, int diffY) {

        // đang né → giữ hướng
        if (avoidTimer > 0 && avoidDirection != null) {
            if (tryMove(avoidDirection)) {
                avoidTimer--;
                return;
            } else {
                avoidTimer = 0;
                avoidDirection = null;
            }
        }

        String primaryDirection;
        String secondaryDirection;

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

        if (dir.equals(lastBlockedDirection) && stuckCooldown > 0) {
            return false;
        }

        int nextX = x;
        int nextY = y;

        switch (dir) {
            case "up": nextY -= speed; break;
            case "down": nextY += speed; break;
            case "left": nextX -= speed; break;
            case "right": nextX += speed; break;
        }

        Rectangle nextBounds = new Rectangle(
            nextX + solidArea.x,
            nextY + solidArea.y,
            solidArea.width,
            solidArea.height
        );

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

    public Rectangle getBounds() {
        return new Rectangle(
            x + 18,
            y + 18,
            28,
            28
        );
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

        // debug vòng phát hiện
        g2.setColor(new java.awt.Color(255, 0, 0, 50));
        g2.drawOval(x - 220 + gp.tileSize/2, y - 220 + gp.tileSize/2, 440, 440);
    }
}