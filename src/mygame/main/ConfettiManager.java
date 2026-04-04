package mygame.main;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import mygame.entity.Particle;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfettiManager {
    GamePanel gp;
    CopyOnWriteArrayList<Particle> particles = new CopyOnWriteArrayList<>();
    boolean active = false; // Trạng thái hiệu ứng

    public ConfettiManager(GamePanel gp) {
        this.gp = gp;
    }

    // Kích hoạt hiệu ứng pháo giấy
    public void start() {
        particles.clear();
        active = true;
        // Bắn một đợt pháo ban đầu
        spawnBurst(150); 
    }

    // Tắt hiệu ứng
    public void stop() {
        active = false;
        particles.clear();
    }

    // Tạo một vụ nổ pháo tại vị trí ngẫu nhiên ở đỉnh màn hình
    private void spawnBurst(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            // Tạo pháo dọc theo chiều rộng màn hình, ở phía trên cùng
            int startX = rand.nextInt(gp.screenWidth);
            int startY = -10; // Bắt đầu ở ngoài màn hình phía trên
            particles.add(new Particle(gp, startX, startY));
        }
    }

    public void update() {
        if (!active) return;

            // Duyệt và cập nhật vị trí từng hạt pháo
        for (Particle p : particles) {
            p.update();
        }

        // Xóa tất cả các hạt đã "chết" (rơi khỏi màn hình) một cách an toàn
        particles.removeIf(p -> !p.isAlive());

        // Logic bắn thêm pháo nếu cần...
        if (active && particles.size() < 20) {
            spawnBurst(2);
        }
    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        // Vẽ tất cả mảnh pháo
        for (Particle p : particles) {
            p.draw(g2);
        }
    }
}