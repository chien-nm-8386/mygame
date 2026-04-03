package mygame.tile;

import mygame.main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public class TileManager {
    GamePanel gp;
    BufferedImage mazeBackground; 
    BufferedImage foregroundImage;
    BufferedImage eggImage;
    BufferedImage weaponImage;

    public ArrayList<Rectangle> collisionRects = new ArrayList<>();
    public int playerStartX, playerStartY;
    
    // --- THAY ĐỔI: Dùng danh sách để chứa nhiều trứng ---
    public ArrayList<Rectangle> eggList = new ArrayList<>();
    public Rectangle eggRect; // Giữ lại để không lỗi code cũ nếu có class khác gọi
    public boolean eggCollected = false;

    public Rectangle weaponRect;
    public boolean weaponCollected = false;

    public Rectangle houseRect;

    private int animationCounter = 0;
    public String currentMapPath = "src/res/maps/map_level1.tmx";

    public TileManager(GamePanel gp) {
        this.gp = gp;
        loadImages();
        loadTiledXML("src/res/maps/map_level1.tmx");
    }

    private void loadImages() {
        mazeBackground = setupImage("/res/maps/map_level1.png");
        foregroundImage = setupImage("/res/maps/map_foreground_level1.png");
        eggImage = setupImage("/res/tiles/egg.png");
        weaponImage = setupImage("/res/tiles/Weapons.png"); 
    }

    private BufferedImage setupImage(String path) {
        BufferedImage image = null;
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                image = ImageIO.read(is);
            } else {
                System.out.println("Lỗi: Không tìm thấy file tại " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public void resetMapObjects() {
        collisionRects.clear();
        eggList.clear(); // Xóa danh sách trứng cũ
        playerStartX = 0;
        playerStartY = 0;
        eggRect = null;
        weaponRect = null;
        houseRect = null;
        eggCollected = false;
        weaponCollected = false;
        loadTiledXML(currentMapPath);
    }

    public void update() {
        animationCounter++;
    }

    public void draw(Graphics2D g2) {
        // 1. Vẽ nền map
        if (mazeBackground != null) {
            g2.drawImage(mazeBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }

        // 2. Vẽ TRỨNG (Vẽ tất cả trứng trong danh sách)
        if (eggImage != null) {
            for (Rectangle r : eggList) {
                int eggOffset = (int) (Math.sin(animationCounter * 0.05) * 10); 
                g2.drawImage(eggImage, r.x, r.y + eggOffset, 48, 48, null);
            }
            
            // Giữ logic cũ cho eggRect đơn lẻ nếu vẫn còn tồn tại
            if (eggRect != null && !eggCollected) {
                int eggOffset = (int) (Math.sin(animationCounter * 0.05) * 10); 
                g2.drawImage(eggImage, eggRect.x, eggRect.y + eggOffset, 48, 48, null);
            }
        }

        // 3. Vẽ VŨ KHÍ 
        // ĐIỀU KIỆN QUAN TRỌNG: Chỉ vẽ khi đã nhặt ít nhất 1 quả trứng (hasEgg)
        if (gp.player.hasEgg && weaponImage != null && weaponRect != null && !weaponCollected) {
            int weaponOffset = (int) (Math.sin(animationCounter * 0.06) * 8);
            g2.drawImage(weaponImage, weaponRect.x, weaponRect.y + weaponOffset, 110, 60, null);
        }
    }

    public void drawForeground(Graphics2D g2) {
        if (foregroundImage != null) {
            g2.drawImage(foregroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }
    }

    /**
     * Kiểm tra va chạm giữa Player và các vật phẩm
     */
    public void checkItemCollisions(Rectangle playerRect) {
        // Xử lý nhặt Trứng từ danh sách (ArrayList)
        for (int i = 0; i < eggList.size(); i++) {
            Rectangle r = eggList.get(i);
            if (playerRect.intersects(r)) {
                eggList.remove(i); // Xóa quả trứng khỏi map
                gp.eggsCollected++; // Tăng biến đếm trong GamePanel
                gp.player.hasEgg = true; // Kích hoạt trạng thái có trứng
                eggCollected = true; // Bật flag để hiện vũ khí (giữ logic cũ)
                gp.eggSound.play();
                System.out.println("Bạn đã nhặt được Trứng! Tổng số: " + gp.eggsCollected);
                i--; // Giảm chỉ số sau khi xóa
            }
        }

        // Logic cũ cho eggRect đơn (nếu có)
        if (!eggCollected && eggRect != null && playerRect.intersects(eggRect)) {
            eggCollected = true;
            gp.eggsCollected++;
            gp.player.hasEgg = true; 
            gp.eggSound.play();
            System.out.println("Bạn đã nhặt được Trứng! Vũ khí đã xuất hiện.");
        }

        // Xử lý nhặt Vũ khí
        // Chỉ cho nhặt khi đã có ít nhất 1 trứng
        if (gp.player.hasEgg && !weaponCollected && weaponRect != null && playerRect.intersects(weaponRect)) {
            weaponCollected = true;
            gp.player.hasWeapon = true; 
            weaponRect = null; 
            System.out.println("Bạn đã nhặt được Vũ khí! (Player03 đã kích hoạt)");
        }
    }

    public void loadTiledXML(String filePath) {
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("objectgroup");
            for (int i = 0; i < nList.getLength(); i++) {
                Element group = (Element) nList.item(i);
                String groupName = group.getAttribute("name");
                NodeList objectList = group.getElementsByTagName("object");
                
                for (int j = 0; j < objectList.getLength(); j++) {
                    Element obj = (Element) objectList.item(j);
                    int x = (int) Double.parseDouble(obj.getAttribute("x"));
                    int y = (int) Double.parseDouble(obj.getAttribute("y"));
                    int width = obj.hasAttribute("width") ? (int) Double.parseDouble(obj.getAttribute("width")) : 48;
                    int height = obj.hasAttribute("height") ? (int) Double.parseDouble(obj.getAttribute("height")) : 48;

                    if (groupName.equalsIgnoreCase("collision")) {
                        collisionRects.add(new Rectangle(x, y, width, height));
                    } else if (groupName.equalsIgnoreCase("Entities")) {
                        String name = obj.getAttribute("name");
                        if (name.equalsIgnoreCase("PlayerStart")) {
                            playerStartX = x; playerStartY = y;
                        } else if (name.equalsIgnoreCase("Eggs") || name.equalsIgnoreCase("Egg")) {
                            // THÊM VÀO DANH SÁCH (Hỗ trợ nhiều trứng)
                            eggList.add(new Rectangle(x, y, 64, 64));
                            // Giữ lại eggRect cho logic cũ
                            eggRect = new Rectangle(x, y, 64, 64);
                        } else if (name.equalsIgnoreCase("Weapons")) {
                            weaponRect = new Rectangle(x, y, 64, 64); 
                        } else if (name.equalsIgnoreCase("House")) {
                            houseRect = new Rectangle(x, y, width, height);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Lỗi đọc XML Tiled: " + e.getMessage());
        }
    }
    
    public void loadLevelMap(int level) {
        if (level == 1) {
            currentMapPath = "src/res/maps/map_level1.tmx";
            mazeBackground = setupImage("/res/maps/map_level1.png");
            foregroundImage = setupImage("/res/maps/map_foreground_level1.png");
        } else if (level == 2) {
            currentMapPath = "src/res/maps/map_level2.tmx";
            mazeBackground = setupImage("/res/maps/map_level2.png");
            foregroundImage = setupImage("/res/maps/map_foreground_level2.png");
        }

        resetMapObjects();
    }
}