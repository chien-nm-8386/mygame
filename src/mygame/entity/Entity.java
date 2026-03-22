package mygame.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mygame.main.GamePanel;

public class Entity {

    protected GamePanel gp;
    
    // VỊ TRÍ VÀ TỐC ĐỘ (Dùng để tính toán tọa độ trên màn hình)
    public int x, y;
    public int speed;

    // HÌNH ẢNH (Các lớp con sẽ load ảnh vào đây)
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public BufferedImage attackUp, attackDown, attackLeft, attackRight;
    public String direction = "down"; 

    // HOẠT ẢNH (ANIMATION)
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // VA CHẠM (COLLISION)
    // solidArea: Vùng "xác" thực tế của nhân vật (thường nằm ở chân)
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); 
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    
    // TRẠNG THÁI (STATES)
    public boolean attacking = false;
    public boolean alive = true;
    public boolean invincible = false; 
    
    // BỘ ĐẾM THỜI GIAN (COUNTERS)
    public int invincibleCounter = 0;

    // CHỈ SỐ (STATS)
    public int maxLife;
    public int life;

    public Entity(GamePanel gp) {
        this.gp = gp;
        // Lưu lại vị trí mặc định để reset sau mỗi lần check va chạm
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    /**
     * Cập nhật logic chung (như bộ đếm bất tử).
     * Các lớp con gọi super.update() để chạy phần này.
     */
    public void update() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) { // Khoảng 0.6 giây bất tử
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    /**
     * Vẽ thực thể (Sẽ được ghi đè ở Player và Chicken)
     */
    public void draw(Graphics2D g2) {
        // Logic vẽ sẽ nằm ở lớp con
    }

    /**
     * QUAN TRỌNG: Lấy vùng va chạm thực tế (World Position)
     * Dùng để check intersects giữa Player và Monster trong GamePanel.
     */
    public Rectangle getBounds() {
        return new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
    }

    /**
     * Nhận sát thương và kích hoạt trạng thái bất tử tạm thời (Nhấp nháy)
     */
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
}