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
    BufferedImage mazeBackground, foregroundImage, eggImage, weaponImage;

    public ArrayList<Rectangle> collisionRects = new ArrayList<>();
    public int playerStartX, playerStartY;
    
    // Các vùng va chạm (Rectangles) lấy từ Tiled
    public Rectangle eggRect;
    public Rectangle weaponRect;
    public Rectangle houseRect;

    // Trạng thái logic của game
    public boolean eggCollected = false;
    public boolean weaponCollected = false;

    private int animationCounter = 0;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        loadImages();
        // Lưu ý: Kiểm tra kỹ đường dẫn này trong project của bạn
        loadTiledXML("src/res/maps/map_level1.tmx");
    }

    private void loadImages() {
        mazeBackground = setupImage("/res/maps/map_level1.png");
        foregroundImage = setupImage("/res/maps/map_foreground_level1.png");
        eggImage = setupImage("/res/tiles/egg.png");
        weaponImage = setupImage("/res/tiles/Weapons.png"); 
    }

    private BufferedImage setupImage(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) return ImageIO.read(is);
            else System.out.println("Lỗi: Không tìm thấy ảnh tại " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update() {
        animationCounter++;
    }

    public void draw(Graphics2D g2) {
        // 1. Vẽ Map nền (Background)
        if (mazeBackground != null) {
            g2.drawImage(mazeBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }

        // 2. Vẽ TRỨNG: Chỉ vẽ khi CHƯA bị nhặt
        if (!eggCollected && eggImage != null && eggRect != null) {
            int eggOffset = (int) (Math.sin(animationCounter * 0.05) * 10);
            g2.drawImage(eggImage, eggRect.x, eggRect.y + eggOffset, eggRect.width, eggRect.height, null);
        }

        // 3. Vẽ VŨ KHÍ: CHỈ vẽ khi ĐÃ nhặt trứng VÀ CHƯA nhặt vũ khí
        if (eggCollected && !weaponCollected && weaponImage != null && weaponRect != null) {
            int weaponOffset = (int) (Math.sin(animationCounter * 0.06) * 8);
            g2.drawImage(weaponImage, weaponRect.x, weaponRect.y + weaponOffset, 110, 62, null);
        }
    }

    public void drawForeground(Graphics2D g2) {
        // Vẽ các vật thể che đầu player (như ngọn cây, mái nhà)
        if (foregroundImage != null) {
            g2.drawImage(foregroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }
    }

    /**
     * KIỂM TRA VA CHẠM VẬT PHẨM
     * Đảm bảo thứ tự nhặt đồ: Trứng -> Vũ khí
     */
    public void checkItemCollisions(Rectangle playerRect) {
        
        // --- Xử lý TRỨNG ---
        if (!eggCollected && eggRect != null) {
            if (playerRect.intersects(eggRect)) {
                eggCollected = true;
                gp.player.hasEgg = true; 
                System.out.println("Hệ thống: Nhặt trứng thành công! Vũ khí đã hiện ra.");
            }
        }

        // --- Xử lý VŨ KHÍ ---
        // CHỐT CHẶN: Nếu chưa nhặt trứng (eggCollected == false), logic va chạm vũ khí sẽ bị bỏ qua hoàn toàn
        if (eggCollected && !weaponCollected && weaponRect != null) {
            if (playerRect.intersects(weaponRect)) {
                weaponCollected = true;
                gp.player.hasWeapon = true;
                System.out.println("Hệ thống: Nhặt vũ khí thành công! Bạn đã có Player03.");
                
                // Giải phóng vùng va chạm để không check lại nữa
                weaponRect = null; 
            }
        }
    }

    public void loadTiledXML(String filePath) {
        try {
            File fXmlFile = new File(filePath);
            if (!fXmlFile.exists()) {
                System.out.println("Lỗi: File XML không tồn tại tại " + filePath);
                return;
            }

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
                    int width = obj.hasAttribute("width") ? (int) Double.parseDouble(obj.getAttribute("width")) : 64;
                    int height = obj.hasAttribute("height") ? (int) Double.parseDouble(obj.getAttribute("height")) : 64;

                    if (groupName.equalsIgnoreCase("collision")) {
                        collisionRects.add(new Rectangle(x, y, width, height));
                    } else if (groupName.equalsIgnoreCase("Entities")) {
                        String name = obj.getAttribute("name");
                        if (name.equalsIgnoreCase("PlayerStart")) {
                            playerStartX = x; playerStartY = y;
                        } else if (name.equalsIgnoreCase("Egg") || name.equalsIgnoreCase("Eggs")) {
                            eggRect = new Rectangle(x, y, width, height);
                        } else if (name.equalsIgnoreCase("Weapon") || name.equalsIgnoreCase("Weapons")) {
                            weaponRect = new Rectangle(x, y, width, height);
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

    public void resetMapObjects() {
        collisionRects.clear();
        eggCollected = false;
        weaponCollected = false;
        // Load lại tọa độ từ đầu
        loadTiledXML("src/res/maps/map_level1.tmx");
    }
}