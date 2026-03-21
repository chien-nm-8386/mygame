package mygame.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {

    public int x, y;
    public int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;

    public Rectangle solidArea; 
    public boolean collisionOn = false;

    public Entity() {
        // Mặc định vùng va chạm là toàn bộ ô gạch 48x48
        solidArea = new Rectangle(0, 0, 48, 48);
    }
}