package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.entity.Chicken;
import mygame.tile.CollisionChecker;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;

    // THIẾT LẬP MÀN HÌNH (64x16 x 64x12)
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // THIẾT LẬP THẾ GIỚI (Cùng kích thước với màn hình trong ví dụ này)
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

    // THỰC THỂ (ENTITIES)
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
        if (player != null) player.name = playerName;
    }

    public void setupGame() {
        // 1. Reset các đối tượng trên bản đồ (Trứng, Vũ khí, Va chạm)
        tileM.resetMapObjects();

        // 2. Thiết lập lại Player
        if (player != null) {
            player.setDefaultValues();
            player.name = this.playerName;
            // Đặt vị trí xuất phát từ file XML
            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        // 3. Reset trạng thái phím điều khiển
//        keyH.resetKeys(); // Giả định bạn có hàm này, hoặc set tay như code cũ

        // 4. Khởi tạo danh sách Gà
        chickens.clear();
        spawnInitialChickens();
    }

    private void spawnInitialChickens() {
        // Thêm gà tại vị trí cụ thể hoặc ngẫu nhiên
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
        // 1. Thoát về Menu chính
        if (keyH.escapePressed) {
            keyH.escapePressed = false;
            stopGameThread();
            main.showMenu();
            return;
        }

        // 2. Cập nhật Player và Kiểm tra nhặt đồ
        if (player != null) {
            player.update();
            // Đây là nơi quan trọng: Check nhặt trứng -> Unlock Vũ khí
            tileM.checkItemCollisions(player.getBounds());
        }

        // 3. Cập nhật danh sách Gà (Dùng Iterator để xóa an toàn khi gà chết)
        Iterator<Chicken> iterator = chickens.iterator();
        while (iterator.hasNext()) {
            Chicken chicken = iterator.next();
            
            if (chicken.life <= 0) {
                iterator.remove();
                continue;
            }

            chicken.update();

            // Va chạm gây sát thương cho người chơi
            if (player != null && player.getBounds().intersects(chicken.getBounds())) {
                player.takeDamage(10); 
            }
        }

        // 4. Kiểm tra điều kiện Thất bại
        if (player != null && player.health <= 0) {
            player.triggerGameOver();
            return;
        }
        
        // 5. Cập nhật logic bản đồ (animation lơ lửng của vật phẩm)
        tileM.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // --- VẼ THEO THỨ TỰ LỚP (Z-INDEX) ---

        // Lớp 1: Bản đồ nền & Vật phẩm (Trứng/Vũ khí sẽ được TileManager quản lý ẩn/hiện)
        tileM.draw(g2);

        // Lớp 2: Các thực thể (Gà)
        for (Chicken chicken : chickens) {
            chicken.draw(g2);
        }

        // Lớp 3: Người chơi (Vẽ sau gà để đè lên gà khi đi sát)
        if (player != null) {
            player.draw(g2);
        }

        // Lớp 4: Tiền cảnh (Cây cối che đầu nhân vật)
        tileM.drawForeground(g2);

        // Lớp 5: Giao diện người dùng (UI luôn nằm trên cùng)
        if (ui != null) {
            ui.draw(g2);
        }

        g2.dispose();
    }
}