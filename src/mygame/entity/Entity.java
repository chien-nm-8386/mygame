package mygame.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mygame.main.GamePanel;

public class Entity {

    protected GamePanel gp;
    
    public int x, y;
    public int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2, angry;
    public BufferedImage attackUp, attackDown, attackLeft, attackRight;
    public String direction = "down";

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    public boolean attacking = false;
    public boolean alive = true;
    public boolean invincible = false;

    public int invincibleCounter = 0;

    public int maxLife;
    public int life;

    public Entity(GamePanel gp) {
        this.gp = gp;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void update() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
    }

    public Rectangle getBounds() {
        return new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
    }

    public void takeDamage(int damage) {
        if (!invincible) {
            life -= damage;
            invincible = true;
            invincibleCounter = 0;

            if (life <= 0) {
                life = 0;
                alive = false;
            }
        }
    }

    public void stopFootstepSound() {
        // Player sẽ override nếu có footstep sound
    }
}