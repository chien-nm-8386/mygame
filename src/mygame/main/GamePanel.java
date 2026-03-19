package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.entity.Chicken; // Nhớ import lớp Chicken
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

    // TÊN NHÂN VẬT & THỰC THỂ
    public String playerName = "Player";
    public Player player;
    
    // KHỞI TẠO ĐÀN GÀ (QUÁI VẬT)
    public Chicken[] chickens = new Chicken[6]; // Tạo mảng chứa 6 con gà

    public GamePanel(Main main) {
        this.main = main;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Khởi tạo Player
        player = new Player(this, keyH);

        // Thiết lập ban đầu
        setupGame();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (player != null) {
            player.name = playerName;
        }
    }

    // Hàm thiết lập ban đầu cho game (Vị trí gà, vị trí trứng...)
    public void setupGame() {
        if (player != null) {
            player.setDefaultValues();
        }

        // ĐẶT VỊ TRÍ CHO LŨ GÀ (Tọa độ này bạn có thể chỉnh theo Map của mình)
        chickens[0] = new Chicken(this, 5 * tileSize, 3 * tileSize);
        chickens[1] = new Chicken(this, 10 * tileSize, 2 * tileSize);
        chickens[2] = new Chicken(this, 13 * tileSize, 8 * tileSize);
        chickens[3] = new Chicken(this, 2 * tileSize, 9 * tileSize);
        chickens[4] = new Chicken(this, 8 * tileSize, 10 * tileSize);
        chickens[5] = new Chicken(this, 14 * tileSize, 4 * tileSize);
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
        // 1. Nhấn ESC để quay lại menu
        if (keyH.escapePressed) {
            keyH.escapePressed = false;
            stopGameThread();
            main.showMenu();
            return;
        }

        // 2. Cập nhật Player
        if (player != null) {
            player.update();
        }

        // 3. Cập nhật đàn Gà
        for (int i = 0; i < chickens.length; i++) {
            if (chickens[i] != null) {
                chickens[i].update();

                // KIỂM TRA VA CHẠM: Nếu gà chạm Player khi đang nổi điên
                if (chickens[i].isAngry) {
                    // Check khoảng cách va chạm đơn giản giữa gà và player
                    if (Math.abs(player.x - chickens[i].x) < tileSize/1.5 && 
                        Math.abs(player.y - chickens[i].y) < tileSize/1.5) {
                        
                        System.out.println("BẠN ĐÃ BỊ GÀ MỔ TRÚNG!");
                        // Bạn có thể thêm lệnh Game Over ở đây nếu muốn
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. VẼ MAP (Lớp nền bên dưới)
        tileM.draw(g2);

        // 2. VẼ ĐÀN GÀ (Vẽ gà trước khi vẽ player để player đè lên gà nếu cần)
        for (int i = 0; i < chickens.length; i++) {
            if (chickens[i] != null) {
                chickens[i].draw(g2);
            }
        }

        // 3. VẼ NHÂN VẬT (Player)
        if (player != null) {
            player.draw(g2);
        }

        // 4. VẼ LỚP FOREGROUND (Lớp phủ trên cùng, ví dụ ngọn cỏ, tán cây che đầu)
        tileM.drawForeground(g2);

        // 5. HIỂN THỊ THÔNG BÁO UI
        if (player.hasEgg) {
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.setColor(Color.RED);
            String msg = "CẢNH BÁO: GÀ ĐANG ĐUỔI THEO!!!";
            int x = (screenWidth - (int)g2.getFontMetrics().getStringBounds(msg, g2).getWidth()) / 2;
            g2.drawString(msg, x, 50);
        }

        g2.dispose();
    }
}