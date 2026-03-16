package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.tile.CollisionChecker;

public class GamePanel extends JPanel implements Runnable {

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
    
    // KHỞI TẠO THỰC THỂ (Chuyển xuống dưới để đảm bảo load map xong mới tạo Player)
    public Player player;

    public GamePanel() {
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

    // Hàm thiết lập ban đầu cho game
    public void setupGame() {
        // Đảm bảo player nhảy đúng vào vị trí PlayerStart lấy từ file .tmx
        player.setDefaultValues();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS; 
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
        }
    }

    public void update() {
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
        // Ví dụ: for(int i=0; i<obj.length; i++) { if(obj[i]!=null) obj[i].draw(g2, this); }

        // 3. VẼ NHÂN VẬT (Player)
        player.draw(g2);

        // 4. VẼ LỚP FOREGROUND (Lớp phủ trên cùng)
        // Đây là nơi bạn gọi hàm vẽ lớp Foreground từ TileManager
        tileM.drawForeground(g2); 

        g2.dispose();
    }
}