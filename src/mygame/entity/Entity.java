package mygame.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {

    public int x, y;
    public int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
//    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;
    public String direction;

    public Rectangle solidArea; 
    public boolean collisionOn = false;
    public int solidAreaDefaultX;
    public int solidAreaDefaultY;
    public Entity() {
        // Mặc định vùng va chạm là toàn bộ ô gạch 48x48
        solidArea = new Rectangle(0, 0, 48, 48);
    }
}