package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.tile.CollisionChecker;
import java.util.ArrayList;
import mygame.entity.Chicken;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;

    // THIẾT LẬP MÀN HÌNH
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // THIẾT LẬP THẾ GIỚI (Mặc định bằng màn hình, có thể mở rộng sau này)
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;

    // KHỞI TẠO HỆ THỐNG
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui;
    public PathFinder pFinder = new PathFinder(this);

    Thread gameThread;

    // THÔNG TIN NGƯỜI CHƠI
    public String playerName = "Player";
    public Player player;
    public ArrayList<Chicken> chickens = new ArrayList<>();

    public GamePanel(Main main) {
        this.main = main;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        player = new Player(this, keyH);
        ui = new UI(this);
        setupGame();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (player != null) {
            player.name = playerName;
        }
    }

    public void setupGame() {
        // Reset dữ liệu từ file map Tiled
        tileM.resetMapObjects();

        if (player != null) {
            player.setDefaultValues();
            // Đưa player về vị trí Start đã đọc từ Tiled (nếu có)
            if(tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        // Reset các phím bấm
        keyH.upPressed = false;
        keyH.downPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;
        keyH.escapePressed = false;

        // Khởi tạo gà
        chickens.clear();
        int centerX = screenWidth / 2 - tileSize / 2;
        int centerY = screenHeight / 2 - tileSize / 2;
        chickens.add(new Chicken(this, centerX, centerY));
    }

    public void startGameThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameThread() {
        gameThread = null;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        // 1. Thoát về Menu
        if (keyH.escapePressed) {
            keyH.escapePressed = false;
            stopGameThread();
            main.showMenu();
            return;
        }

        // 2. Cập nhật Player
        if (player != null) {
            player.update();
            
            // --- THÊM LOGIC KIỂM TRA NHẶT ĐỒ Ở ĐÂY ---
            // Truyền vùng va chạm của Player vào TileManager để check Rìu/Trứng
            tileM.checkItemCollisions(player.getBounds());
        }

        // 3. Cập nhật Gà và va chạm với gà
        for (Chicken chicken : chickens) {
            chicken.update();

            if (player != null && player.getBounds().intersects(chicken.getBounds())) {
                player.takeDamage(10);
            }
        }

        // 4. Kiểm tra Game Over
        if (player != null && player.health <= 0) {
            player.triggerGameOver();
            return;
        }
        
        tileM.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Lớp nền (Background)
        tileM.draw(g2);

        // Các thực thể (Entities)
        for (Chicken chicken : chickens) {
            chicken.draw(g2);
        }

        if (player != null) {
            player.draw(g2);
        }

        // Lớp phủ (Foreground - ví dụ ngọn cây)
        tileM.drawForeground(g2);

        // Giao diện người dùng (UI - Máu, Tên, Thông báo)
        if (ui != null) {
            ui.draw(g2);
        }

        g2.dispose();
    }
}