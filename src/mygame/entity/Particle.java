package mygame.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;
import mygame.main.GamePanel;

public class Particle {
    GamePanel gp;
    double x, y;          // Vị trí
    double velX, velY;    // Tốc độ di chuyển
    double gravity;       // Trọng lực (để pháo rơi xuống)
    int size;             // Kích thước
    Color color;          // Màu sắc
    double angle;         // Góc xoay hiện tại
    double rotationSpeed; // Tốc độ xoay
    boolean alive = true; // Trạng thái mảnh pháo

    public Particle(GamePanel gp, int startX, int startY) {
        this.gp = gp;
        Random rand = new Random();

        // Vị trí bắt đầu (thường là từ đỉnh màn hình)
        this.x = startX;
        this.y = startY;

        // Tốc độ ngẫu nhiên (tạo độ lan tỏa)
        this.velX = (rand.nextDouble() - 0.5) * 8; // Lan sang trái/phải
        this.velY = (rand.nextDouble() * -5) - 2;  // Bắn lên trên một chút trước khi rơi

        // Cấu hình vật lý
        this.gravity = 0.15; // Trọng lực nhẹ để pháo rơi chậm
        this.size = rand.nextInt(6) + 4; // Kích thước từ 4-10 pixel

        // Màu sắc ngẫu nhiên rực rỡ (Rainbow)
        float hue = rand.nextFloat();
        this.color = Color.getHSBColor(hue, 0.9f, 1.0f);

        // Hiệu ứng xoay
        this.angle = rand.nextDouble() * Math.PI * 2;
        this.rotationSpeed = (rand.nextDouble() - 0.5) * 0.2;
    }

    public void update() {
        // Áp dụng vật lý
        velY += gravity; // Trọng lực kéo xuống
        x += velX;       // Di chuyển ngang
        y += velY;       // Di chuyển dọc

        // Lực cản không khí nhẹ (giảm tốc độ ngang)
        velX *= 0.98;

        // Xoay mảnh pháo
        angle += rotationSpeed;

        // Kiểm tra nếu pháo rơi ra ngoài màn hình
        if (y > gp.screenHeight) {
            alive = false; // Đánh dấu để xóa
        }
    }

    public void draw(Graphics2D g2) {
        if (!alive) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);

        // Lưu trạng thái Graphics hiện tại để xoay
        java.awt.geom.AffineTransform oldTransform = g2.getTransform();

        // Di chuyển tâm đến vị trí mảnh pháo và xoay
        g2.translate(x, y);
        g2.rotate(angle);

        // Vẽ mảnh pháo (hình chữ nhật nhỏ)
        // Vẽ lệch tâm để hiệu ứng xoay đẹp hơn
        g2.fillRect(-size / 2, -size / 2, size, size);

        // Khôi phục trạng thái Graphics cũ
        g2.setTransform(oldTransform);
    }

    public boolean isAlive() {
        return alive;
    }
}