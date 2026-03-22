package mygame.main;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import mygame.tile.TileManager;
import mygame.entity.Entity;
import mygame.entity.Player;
import mygame.entity.Chicken;
import mygame.tile.CollisionChecker;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;

    // THIẾT LẬP MÀN HÌNH
    public final int tileSize = 64;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    // THIẾT LẬP THẾ GIỚI
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;

    // HỆ THỐNG
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH = new KeyHandler();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui = new UI(this); 
    public PathFinder pFinder = new PathFinder(this);
    Thread gameThread;

    // THỰC THỂ
    public String playerName = "Player";
    public Player player;
    public Entity monster[] = new Entity[10]; 
    
    // Danh sách hỗ trợ vẽ theo thứ tự chiều sâu (Y-Sorting)
    ArrayList<Entity> entityList = new ArrayList<>();

    public GamePanel(Main main) {
        this.main = main;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        // Khởi tạo Player
        player = new Player(this, keyH);
        setupGame();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (player != null) player.name = playerName;
    }

    public void setupGame() {
        tileM.resetMapObjects();

        if (player != null) {
            player.setDefaultValues();
            player.name = this.playerName;
            
            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }
        spawnInitialChickens();
    }

    private void spawnInitialChickens() {
        for(int i = 0; i < monster.length; i++) {
            monster[i] = null;
        }
        // Đặt gà tại các vị trí bạn muốn
        monster[0] = new Chicken(this, 500, 400);
        monster[1] = new Chicken(this, 70, 400);
        monster[2] = new Chicken(this, 200, 430);
        monster[3] = new Chicken(this, 200, 100);
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
            tileM.checkItemCollisions(player.getBounds());
        }

        // 3. Cập nhật Gà & Xử lý va chạm gây sát thương
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                if (monster[i].alive) {
                    monster[i].update();
                    
                    // --- QUAN TRỌNG: KIỂM TRA VA CHẠM GÂY SÁT THƯƠNG ---
                    // Nếu khung va chạm của Player giao với khung va chạm của Gà
                    if (player != null && player.getBounds().intersects(monster[i].getBounds())) {
                        player.takeDamage(10); // Trừ 10 máu và kích hoạt nhấp nháy
                    }
                } else {
                    monster[i] = null; // Gà chết thì biến mất
                }
            }
        }

        // 4. Kiểm tra Game Over
        if (player != null && player.health <= 0) {
            player.triggerGameOver();
        }
        
        tileM.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // BƯỚC 1: Vẽ Map (Lớp dưới cùng)
        tileM.draw(g2);

        // BƯỚC 2: Vẽ các thực thể với Y-Sorting (Chiều sâu)
        entityList.add(player); // Thêm player vào danh sách vẽ
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                entityList.add(monster[i]); // Thêm quái vật vào danh sách vẽ
            }
        }

        // Sắp xếp: Thực thể nào có Y nhỏ hơn (đứng cao hơn) sẽ được vẽ trước
        Collections.sort(entityList, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
                return Integer.compare(e1.y, e2.y);
            }
        });

        // Vẽ từng thực thể sau khi đã sắp xếp thứ tự
        for (int i = 0; i < entityList.size(); i++) {
            entityList.get(i).draw(g2);
        }

        // Xóa danh sách để frame sau tính toán lại từ đầu
        entityList.clear();

        // BƯỚC 3: Vẽ tiền cảnh (Foreground)
        tileM.drawForeground(g2);

        // BƯỚC 4: Vẽ giao diện (UI)
        if (ui != null) {
            ui.draw(g2);
        }

        g2.dispose();
    }
}