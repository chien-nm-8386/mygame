package mygame.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import mygame.main.GamePanel;
import mygame.main.KeyHandler;

public class Player extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;
    public boolean hasEgg = false; 

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        // --- SỬA LẠI VÙNG VA CHẠM (Dành cho tileSize = 48) ---
        // Chúng ta đặt nó ở giữa chân nhân vật để trông tự nhiên nhất
        solidArea = new Rectangle();
        
        // Căn giữa X: (48 - 24) / 2 = 12
        solidArea.x = 12; 
        // Đặt Y ở nửa dưới nhân vật để tạo hiệu ứng 2.5D (đầu đè lên cỏ)
        solidArea.y = 25; 
        // Độ rộng vùng va chạm (chiếm khoảng 50% chiều rộng nhân vật)
        solidArea.width = 24; 
        // Chiều cao vùng va chạm
        solidArea.height = 20; 

        // Lưu lại tọa độ mặc định của vùng va chạm để dùng trong CollisionChecker
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        // Đảm bảo gp.tileM.playerStartX/Y đã được tính toán theo pixel
        x = gp.tileM.playerStartX; 
        y = gp.tileM.playerStartY;
        speed = 4; // Tăng nhẹ tốc độ nếu thấy chậm
        direction = "down"; 
    }

    public void getPlayerImage() {
        try {
            // Sau này bạn nên thêm up1, down1, left1, right1 ở đây
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01.png")); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            // 1. KIỂM TRA VA CHẠM TƯỜNG/CỎ
            collisionOn = false;
            gp.cChecker.checkTile(this);

            // 2. KIỂM TRA VA CHẠM VẬT PHẨM
            checkObjectInteraction();

            // 3. DI CHUYỂN NẾU KHÔNG CÓ VA CHẠM
            if (!collisionOn) {
                switch (direction) {
                    case "up":    y -= speed; break;
                    case "down":  y += speed; break;
                    case "left":  x -= speed; break;
                    case "right": x += speed; break;
                }
            }
        }
    }

    public void checkObjectInteraction() {
        // Kiểm tra Trứng (Chỉ kiểm tra nếu trứng còn tồn tại)
        if (!hasEgg && gp.tileM.eggRect != null) {
            // Sử dụng checkEntity để xem solidArea của Player có giao với eggRect không
            String object = gp.cChecker.checkEntity(this, gp.tileM.eggRect, "Egg");
            if (object.equals("Egg")) {
                hasEgg = true;
                gp.tileM.eggRect = null; 
                System.out.println("Bạn đã nhặt được trứng!");
            }
        }

        // Kiểm tra Nhà
        if (gp.tileM.houseRect != null) {
            String reachHome = gp.cChecker.checkEntity(this, gp.tileM.houseRect, "House");
            if (reachHome.equals("House")) {
                if (hasEgg) {
                    System.out.println("CHIẾN THẮNG!");
                } else {
                    // Thêm cơ chế để tin nhắn không bị spam liên tục khi đứng ở nhà
                    System.out.println("Tìm trứng đã!");
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = down1; 
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
        
        if(hasEgg) {
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.drawString("Về nhà mau!", x, y - 10);
        }
    }
}