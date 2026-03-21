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
    BufferedImage weaponImage; // Chỉ sử dụng 1 ảnh Weapons

    public ArrayList<Rectangle> collisionRects = new ArrayList<>();
    public int playerStartX, playerStartY;
    
    public Rectangle eggRect;
    public boolean eggCollected = false;

    public Rectangle weaponRect;
    public boolean weaponCollected = false;

    public Rectangle houseRect;

    // Biến điều khiển hiệu ứng lơ lửng
    private int animationCounter = 0;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        loadImages();
        loadTiledXML("src/res/maps/map_level1.tmx");
    }

    private void loadImages() {
        mazeBackground = setupImage("/res/maps/map_level1.png");
        foregroundImage = setupImage("/res/maps/map_foreground_level1.png");
        eggImage = setupImage("/res/tiles/egg.png");
        // Load đúng 1 file Weapons.png
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
        playerStartX = 0;
        playerStartY = 0;
        eggRect = null;
        weaponRect = null;
        houseRect = null;
        eggCollected = false;
        weaponCollected = false;
        loadTiledXML("src/res/maps/map_level1.tmx");
    }

    // Hàm update để tăng bộ đếm thời gian
    public void update() {
        animationCounter++;
    }

    public void draw(Graphics2D g2) {
        // 1. Vẽ nền map
        if (mazeBackground != null) {
            g2.drawImage(mazeBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }

        // Tự động tăng counter nếu bạn quên gọi update() trong GamePanel
        animationCounter++;

        // 2. Vẽ TRỨNG (Lơ lửng chậm)
        if (eggImage != null && eggRect != null && !eggCollected) {
            // Biên độ 10px, tốc độ 0.07
            int eggOffset = (int) (Math.sin(animationCounter * 0.05) * 10); 
            g2.drawImage(eggImage, eggRect.x, eggRect.y + eggOffset, 64, 64, null);
        }

        // 3. Vẽ VŨ KHÍ (Lơ lửng nhanh hơn một chút)
        if (weaponImage != null && weaponRect != null && !weaponCollected) {
            // Biên độ 8px, tốc độ 0.1
            int weaponOffset = (int) (Math.sin(animationCounter * 0.05) * 8);
            
            g2.drawImage(weaponImage, 
                         weaponRect.x, 
                         weaponRect.y + weaponOffset, 
                         weaponRect.width, 
                         weaponRect.height, 
                         null);
        }
    }

    public void drawForeground(Graphics2D g2) {
        if (foregroundImage != null) {
            g2.drawImage(foregroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }
    }

    public void checkItemCollisions(Rectangle playerRect) {
        if (!eggCollected && eggRect != null && playerRect.intersects(eggRect)) {
            eggCollected = true;
            System.out.println("Chúc mừng! Bạn đã nhặt được trứng.");
        }
        if (!weaponCollected && weaponRect != null && playerRect.intersects(weaponRect)) {
            weaponCollected = true;
            System.out.println("Tuyệt vời! Bạn đã có Rìu để làm việc.");
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
                            eggRect = new Rectangle(x, y, 64, 64);
                        } else if (name.equalsIgnoreCase("Weapons")) {
                            weaponRect = new Rectangle(x, y, 110, 60);
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
}