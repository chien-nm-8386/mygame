package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.tile.CollisionChecker;

public class GamePanel extends JPanel implements Runnable {

    public Main main;

    // THIẾT LẬP MÀN HÌNH
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    int FPS = 60;

    // KHỞI TẠO HỆ THỐNG
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    Thread gameThread;

    // TÊN NHÂN VẬT
    public String playerName = "Player";

    // KHỞI TẠO THỰC THỂ
    public Player player;

    public GamePanel(Main main) {
        this.main = main;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // QUAN TRỌNG: Khởi tạo Player sau khi TileManager đã load xong tọa độ từ Tiled
        player = new Player(this, keyH);

        // Gọi lệnh setup để gán tọa độ PlayerStart từ map vào player
        setupGame();
    }

   public void setPlayerName(String playerName) {
        this.playerName = playerName;

        if (player != null) {
            player.name = playerName;
        }
    }

    // Hàm thiết lập ban đầu cho game
    public void setupGame() {
        if (player != null) {
            player.setDefaultValues();
        }
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

        // Nhấn ESC để quay lại menu
        if (keyH.escapePressed) {
            keyH.escapePressed = false;
            stopGameThread();
            main.showMenu();
            return;
        }

        // Chỉ cập nhật nếu player đã được khởi tạo
        if (player != null) {
            player.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. VẼ MAP (Lớp nền bên dưới)
        tileM.draw(g2);

        // 2. VẼ CÁC VẬT PHẨM (Nếu có)

        // 3. VẼ NHÂN VẬT (Player)
        if (player != null) {
            player.draw(g2);
        }

        // 4. VẼ LỚP FOREGROUND (Lớp phủ trên cùng)
        tileM.drawForeground(g2);
        g2.dispose();
    }
}