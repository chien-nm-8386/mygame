package mygame.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import mygame.main.GamePanel;

public class Entity {

    protected GamePanel gp;

    // --- TỌA ĐỘ & DI CHUYỂN ---
    public int x, y;
    public int speed;
    public String direction = "down";

    // --- HÌNH ẢNH & ANIMATION ---
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2, angry;
    public BufferedImage attackUp, attackDown, attackLeft, attackRight;
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // --- HỆ THỐNG VA CHẠM (COLLISION) ---
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    // --- CHỈ SỐ SINH TỒN (STATS) ---
    public int maxLife;
    public int life;
    public boolean alive = true;

    // --- TRẠNG THÁI CHIẾN ĐẤU ---
    public boolean attacking = false;
    public boolean invincible = false;
    public int invincibleCounter = 0;

    public Entity(GamePanel gp) {
        this.gp = gp;
        // Lưu giá trị mặc định để reset khi cần (vd: sau khi va chạm)
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    // =========================================================================
    // LOGIC CẬP NHẬT CHUNG
    // =========================================================================

    public void update() {
        // Xử lý thời gian bất tử (dùng chung cho cả Player và Monster)
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    // =========================================================================
    // HỆ THỐNG TƯƠNG TÁC & CHIẾN ĐẤU
    // =========================================================================

    /**
     * Lấy vùng va chạm thực tế dựa trên vị trí hiện tại của Entity.
     */
    public Rectangle getBounds() {
        return new Rectangle(x + solidArea.x, y + solidArea.y, solidArea.width, solidArea.height);
    }

    /**
     * Xử lý nhận sát thương.
     * @param damage Lượng máu bị trừ
     */
    public void takeDamage(int damage) {
        if (!invincible) {
            life -= damage;
            invincible = true;
            invincibleCounter = 0;

            if (life <= 0) {
                life = 0;
                alive = false;
                handleDeath();
            }
        }
    }

    /**
     * Hàm bổ trợ để các lớp con override nếu cần xử lý đặc biệt khi chết
     */
    protected void handleDeath() {
        // Mặc định không làm gì, lớp con sẽ ghi đè nếu cần (vd: rơi đồ, hiệu ứng nổ)
    }

    // =========================================================================
    // CÁC HÀM TRỐNG ĐỂ LỚP CON OVERRIDE (POLYMORPHISM)
    // =========================================================================

    public void draw(Graphics2D g2) {
        // Sẽ được định nghĩa chi tiết ở Player.java, Chicken.java...
    }

    public void stopFootstepSound() {
        // Thường chỉ dùng cho Player, để ở đây để tránh lỗi gọi hàm từ lớp cha
    }
}