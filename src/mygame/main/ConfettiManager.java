package mygame.main;

import java.awt.Graphics2D;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import mygame.entity.Particle;

public class ConfettiManager {

    private final GamePanel gp;
    private final Random rand = new Random();
    
    // Sử dụng CopyOnWriteArrayList để tránh ConcurrentModificationException khi vừa vẽ vừa xóa hạt
    private final CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    
    // --- TRẠNG THÁI HIỆU ỨNG ---
    private boolean active = false;

    public ConfettiManager(GamePanel gp) {
        this.gp = gp;
    }

    // =========================================================================
    // 1. ĐIỀU KHIỂN HIỆU ỨNG (CONTROLS)
    // =========================================================================

    /**
     * Kích hoạt hiệu ứng pháo giấy.
     */
    public void start() {
        particles.clear();
        active = true;
        // Bắn một đợt pháo lớn ban đầu (Burst)
        spawnBurst(150); 
    }

    /**
     * Tắt hiệu ứng và giải phóng bộ nhớ.
     */
    public void stop() {
        active = false;
        particles.clear();
    }

    /**
     * Tạo một lượng hạt pháo tại vị trí ngẫu nhiên ở đỉnh màn hình.
     */
    private void spawnBurst(int count) {
        for (int i = 0; i < count; i++) {
            // Phân bổ hạt ngẫu nhiên theo chiều ngang màn hình
            int startX = rand.nextInt(gp.screenWidth);
            int startY = -10; // Bắt đầu từ phía trên ngoài màn hình
            
            particles.add(new Particle(gp, startX, startY));
        }
    }

    // =========================================================================
    // 2. VÒNG ĐỜI CẬP NHẬT (LIFECYCLE)
    // =========================================================================

    public void update() {
        if (!active) return;

        // A. Cập nhật vị trí từng hạt pháo
        for (Particle p : particles) {
            p.update();
        }

        // B. Dọn dẹp: Xóa các hạt đã rơi khỏi màn hình (isAlive == false)
        particles.removeIf(p -> !p.isAlive());

        // C. Duy trì: Nếu số lượng hạt quá ít, bắn thêm hạt mới để duy trì hiệu ứng
        maintainParticleCount();
    }

    private void maintainParticleCount() {
        if (active && particles.size() < 20) {
            spawnBurst(2);
        }
    }

    // =========================================================================
    // 3. HIỂN THỊ (RENDERING)
    // =========================================================================

    public void draw(Graphics2D g2) {
        if (!active || particles.isEmpty()) return;

        // Vẽ tất cả mảnh pháo hiện có
        for (Particle p : particles) {
            p.draw(g2);
        }
    }

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================

    public boolean isActive() {
        return active;
    }
}