package mygame.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;
import mygame.main.GamePanel;

public class Particle {

    private final GamePanel gp;
    private static final Random rand = new Random(); // Dùng chung để tiết kiệm bộ nhớ

    // --- VỊ TRÍ & VẬT LÝ (PHYSICS) ---
    private double x, y;          
    private double velX, velY;    
    private final double gravity; 
    private final int size;       

    // --- HIỂN THỊ & HIỆU ỨNG (VISUALS) ---
    private final Color color;          
    private double angle;          
    private final double rotationSpeed; 

    // --- TRẠNG THÁI (STATUS) ---
    private boolean alive = true;

    public Particle(GamePanel gp, int startX, int startY) {
        this.gp = gp;

        // 1. Thiết lập vị trí khởi tạo
        this.x = startX;
        this.y = startY;

        // 2. Thiết lập Vector lực (Tạo độ lan tỏa ngẫu nhiên)
        this.velX = (rand.nextDouble() - 0.5) * 8; 
        this.velY = (rand.nextDouble() * -5) - 2;  

        // 3. Cấu hình vật lý cơ bản
        this.gravity = 0.15; 
        this.size = rand.nextInt(6) + 4; 

        // 4. Thiết lập màu sắc (Rainbow/HSB)
        float hue = rand.nextFloat();
        this.color = Color.getHSBColor(hue, 0.9f, 1.0f);

        // 5. Khởi tạo hiệu ứng xoay
        this.angle = rand.nextDouble() * Math.PI * 2;
        this.rotationSpeed = (rand.nextDouble() - 0.5) * 0.2;
    }

    // =========================================================================
    // LOGIC CẬP NHẬT (UPDATE)
    // =========================================================================

    public void update() {
        applyPhysics();
        checkBoundaries();
    }

    private void applyPhysics() {
        // Áp dụng trọng lực
        velY += gravity; 
        
        // Lực cản không khí nhẹ (giảm dần tốc độ ngang)
        velX *= 0.98;

        // Cập nhật vị trí
        x += velX;
        y += velY;

        // Cập nhật góc xoay
        angle += rotationSpeed;
    }

    private void checkBoundaries() {
        // Nếu rơi quá cạnh dưới màn hình thì hủy hạt
        if (y > gp.screenHeight) {
            alive = false; 
        }
    }

    // =========================================================================
    // LOGIC HIỂN THỊ (DRAW)
    // =========================================================================

    public void draw(Graphics2D g2) {
        if (!alive) return;

        // Thiết lập chế độ vẽ mượt (Anti-aliasing)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        // --- XỬ LÝ BIẾN ĐỔI GÓC XOAY (TRANSFORM) ---
        AffineTransform oldTransform = g2.getTransform();

        // Di chuyển hệ tọa độ đến tâm hạt và xoay
        g2.translate(x, y);
        g2.rotate(angle);

        // Vẽ hạt (Hình chữ nhật nhỏ tại tâm 0,0 sau khi đã translate)
        g2.fillRect(-size / 2, -size / 2, size, size);

        // Khôi phục trạng thái hệ tọa độ cũ
        g2.setTransform(oldTransform);
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================

    public boolean isAlive() {
        return alive;
    }
}