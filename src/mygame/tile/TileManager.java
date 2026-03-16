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

    public ArrayList<Rectangle> collisionRects = new ArrayList<>();
    public int playerStartX, playerStartY;
    public Rectangle eggRect; // Vị trí và kích thước quả trứng
    public Rectangle houseRect;
    public boolean eggCollected = false;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        
        // 1. Load ảnh
        // Lưu ý: Nếu "res" đã là Resource Root, hãy bỏ chữ "/res" ở đầu
        loadMazeImage("/res/maps/map_level1.png"); 
        loadForegroundImage("/res/maps/map_foreground_level1.png"); 
        loadEggImage("/res/tiles/egg.png"); 
        
        // 2. Load dữ liệu Tiled
        loadTiledXML("src/res/maps/map_level1.tmx"); 
    }

    private void loadMazeImage(String path) {
        try {
            mazeBackground = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy ảnh nền tại " + path);
        }
    }

    private void loadForegroundImage(String path) {
        try {
            foregroundImage = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy ảnh Foreground.");
        }
    }

    private void loadEggImage(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) {
                eggImage = ImageIO.read(is);
            } else {
                System.out.println("Lỗi: Không tìm thấy file ảnh tại " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    
                    // Lấy width/height từ Tiled, nếu không có mặc định là 48
                    int width = obj.hasAttribute("width") ? (int) Double.parseDouble(obj.getAttribute("width")) : 48;
                    int height = obj.hasAttribute("height") ? (int) Double.parseDouble(obj.getAttribute("height")) : 48;

                    if (groupName.equalsIgnoreCase("collision")) {
                        collisionRects.add(new Rectangle(x, y, width, height));
                    } 
                    else if (groupName.equalsIgnoreCase("Entities")) {
                        String name = obj.getAttribute("name");
                        if (name.equalsIgnoreCase("PlayerStart")) {
                            playerStartX = x;
                            playerStartY = y;
                        } else if (name.equalsIgnoreCase("Eggs") || name.equalsIgnoreCase("Egg")) {
                            // Ép kích thước quả trứng thành 48x48 nếu bạn muốn cố định
                            eggRect = new Rectangle(x, y, 64, 64); 
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

    public void checkEggCollision(Rectangle playerRect) {
        if (!eggCollected && eggRect != null && playerRect.intersects(eggRect)) {
            eggCollected = true;
            System.out.println("Chúc mừng! Bạn đã nhặt được trứng.");
        }
    }

    public void draw(Graphics2D g2) {
        // Vẽ Nền
        if (mazeBackground != null) {
            g2.drawImage(mazeBackground, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }

        // Vẽ Trứng 48x48
        if (eggImage != null && eggRect != null && !eggCollected) {
            g2.drawImage(eggImage, eggRect.x, eggRect.y, 64, 64, null);
        }
    }

    public void drawForeground(Graphics2D g2) {
        if (foregroundImage != null) {
            g2.drawImage(foregroundImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        }
    }
}