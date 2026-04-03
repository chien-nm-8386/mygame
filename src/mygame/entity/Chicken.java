package mygame.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import mygame.main.GamePanel;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.RenderingHints;

public class Chicken extends Entity {

    GamePanel gp;
    private int attackCounter = 0; // Bộ đếm thời gian giữa các lần mổ
    
// --- Hệ thống HP & Trạng thái ---
    private boolean hpBarOn = false;
    private int hpBarCounter = 0;
    //bộ đếm tgian hồi sinh
    public int defaultX, defaultY;
    public int respawnCounter = 0;
    public int shrinkCounter = 0;// Bộ đếm hiệu ứng thu nhỏ vòng tròn sau khi hồi sinh

    // --- AI DI CHUYỂN & NÉ VẬT CẢN ---
    private int stuckCooldown = 0;
    private String lastBlockedDirection = "";
    private int avoidTimer = 0;
    private String avoidDirection = null;
    // Ảnh animation
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;

    public Chicken(GamePanel gp, int startX, int startY) {
        super(gp);
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.defaultX = startX;
        this.defaultY = startY;
        this.speed = 2;
        this.direction = "down";

        this.maxLife = 100;
        this.life = maxLife;
        this.alive = true;

        solidArea = new Rectangle(18, 18, 28, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getChickenImage();
    }

    public void getChickenImage() {
        try {
            // Ảnh trạng thái bình thường (Ngủ)
            up1 = setup("/res/tiles/chicken_ngu.png");
            down1 = up1;
            left1 = up1;
            right1 = up1;

            angry = setup("/res/tiles/chicken_gian.png");
             // Ảnh trạng thái đuổi theo (Angry)
            up1_egg = setup("/res/tiles/chicken_up1.png");
            up2_egg = setup("/res/tiles/chicken_up2.png");
            down1_egg = setup("/res/tiles/chicken_down1.png");
            down2_egg = setup("/res/tiles/chicken_down2.png");
            left1_egg = setup("/res/tiles/chicken_left1.png");
            left2_egg = setup("/res/tiles/chicken_left2.png");
            right1_egg = setup("/res/tiles/chicken_right1.png");
            right2_egg = setup("/res/tiles/chicken_right2.png");
        } catch (IOException e) {
            System.out.println("Lỗi tải ảnh gà!");
        }
    }

    public BufferedImage setup(String path) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(path));
    }

@Override
    public void update() {
        // 1. Xử lý thời gian bất tử và nhấp nháy của con gà khi bị trúng đòn
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }

        if (stuckCooldown > 0) stuckCooldown--;

        // 2. Tính khoảng cách đến Player
        int diffX = (gp.player.x + gp.tileSize / 2) - (this.x + gp.tileSize / 2);
        int diffY = (gp.player.y + gp.tileSize / 2) - (this.y + gp.tileSize / 2);
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        // 3. AI: Đuổi theo và Tấn công khi Player cầm trứng
        if (gp.player.hasEgg && distance < 250) { // Tăng tầm nhìn lên 400 cho hung hãn
            attacking = true;
            moveTowardPlayer(diffX, diffY);

            // --- CƠ CHẾ TỰ ĐỘNG TẤN CÔNG (MỔ) ---
            // Nếu khoảng cách nhỏ hơn hoặc bằng 1 Tile (đã áp sát)
            if (distance <= gp.tileSize) {
                attackCounter++; // Tăng bộ đếm nạp đạn mổ
                
                // Sau mỗi 60 frame (khoảng 1 giây) thì mổ 1 lần
                if (attackCounter >= 30) {
                    // Gọi hàm nhận sát thương của Player
                    // Lưu ý: Đảm bảo bên Player.java đã có hàm takeDamage(int)
                    gp.player.takeDamage(10); 
                    
                    System.out.println("Gà đã chủ động mổ Player!");
                    attackCounter = 0; // Reset để chờ lần mổ tiếp theo
                }
            } else {
                attackCounter = 0; // Reset nếu Player chạy thoát ra xa
            }
        } else {
            // Nếu Player không cầm trứng hoặc ở quá xa: Gà đứng yên (Ngủ)
            attacking = false;
            attackCounter = 0;
            spriteNum = 1;
            return;
        }

        // 4. Animation chân chạy khi đang đuổi theo
        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private void moveTowardPlayer(int diffX, int diffY) {
        if (avoidTimer > 0 && avoidDirection != null) {
            if (tryMove(avoidDirection)) {
                avoidTimer--;
                return;
            } else {
                avoidTimer = 0;
            }
        }

        String primaryDir, secondaryDir;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            primaryDir = (diffX > 0) ? "right" : "left";
            secondaryDir = (diffY > 0) ? "down" : "up";
        } else {
            primaryDir = (diffY > 0) ? "down" : "up";
            secondaryDir = (diffX > 0) ? "right" : "left";
        }
// Thử hướng chính, nếu kẹt (tường hoặc gà khác) thì thử hướng phụ
        if (!tryMove(primaryDir)) {
            if (tryMove(secondaryDir)) {
                avoidDirection = secondaryDir;
                avoidTimer = 15;
            }
        }
    }

    private boolean tryMove(String dir) {
        if (dir.equals(lastBlockedDirection) && stuckCooldown > 0) return false;

        String oldDir = this.direction;
        this.direction = dir;
        collisionOn = false;
        // KIỂM TRA VA CHẠM (3 lớp chặn)
        gp.cChecker.checkTile(this);
      

        Rectangle nextBounds = new Rectangle(getBounds());
        switch (dir) {
            case "up": nextBounds.y -= speed; break;
            case "down": nextBounds.y += speed; break;
            case "left": nextBounds.x -= speed; break;
            case "right": nextBounds.x += speed; break;
        }

        for (int i = 0; i < gp.chickens.size(); i++) {
            Chicken other = gp.chickens.get(i);
            if (other != null && other != this && other.alive && other.life > 0) {
                if (nextBounds.intersects(other.getBounds())) {
                    collisionOn = true;
                    break;
                }
            }
        }
       

        gp.cChecker.checkPlayer(this);

        if (!collisionOn) {
            switch (dir) {
                case "up": y -= speed; break;
                case "down": y += speed; break;
                case "left": x -= speed; break;
                case "right": x += speed; break;
            }
            return true;
        } else {
            lastBlockedDirection = dir;
            stuckCooldown = 15;
            this.direction = oldDir;
            return false;
        }
    }

    @Override
    public void takeDamage(int damage) {
        if (!invincible) {
            life -= damage;
            hpBarOn = true;
            hpBarCounter = 0;
            // Kích hoạt nhấp nháy (Không đẩy lùi)
            invincible = true;
            invincibleCounter = 0;

            if (life <= 0) {
                life = 0;
                alive = false;
                hpBarOn = false;
            }
        }
    }

    public void respawn() {
        this.x = defaultX;
        this.y = defaultY;
        this.life = maxLife;
        this.alive = true;
        this.invincible = false;
        this.attacking = false;
        this.respawnCounter = 0;
        System.out.println("Ga da hoi sinh tai: " + x + ", " + y);
        this.shrinkCounter = 20;
    }

@Override
    public void draw(Graphics2D g2) {
        // 1. Vẽ vòng tròn ma thuật (Hiệu ứng hồi sinh)
        if ((!alive && respawnCounter > 180) || (alive && shrinkCounter > 0)) {
            drawMagicCircle(g2);
        }

        // 2. Vẽ con gà nếu còn sống
        if (alive) {
            BufferedImage image = null;

            // Tính toán lại khoảng cách để quyết định ảnh hiển thị
            int diffX = (gp.player.x + gp.tileSize / 2) - (this.x + gp.tileSize / 2);
            int diffY = (gp.player.y + gp.tileSize / 2) - (this.y + gp.tileSize / 2);
            double distance = Math.sqrt(diffX * diffX + diffY * diffY);

            if (!attacking) {
                // TRẠNG THÁI 1: Đang ngủ (Chưa thấy Player cầm trứng)
                image = up1;
            } else {
                // TRẠNG THÁI 2: Đang đuổi theo hoặc đang mổ
                if (distance <= gp.tileSize + 5) { 
                    // Nếu áp sát (khoảng cách cực gần): Hiện ảnh Giận dữ/Mổ
                    image = angry; 
                } else {
                    // Nếu đang đuổi theo từ xa: Hiện animation chân chạy
                    switch (direction) {
                        case "up": image = (spriteNum == 1) ? up1_egg : up2_egg; break;
                        case "down": image = (spriteNum == 1) ? down1_egg : down2_egg; break;
                        case "left": image = (spriteNum == 1) ? left1_egg : left2_egg; break;
                        case "right": image = (spriteNum == 1) ? right1_egg : right2_egg; break;
                    }
                }
            }

            // 3. Xử lý hiệu ứng nhấp nháy khi gà bị trúng đòn (invincible)
            if (!(invincible && invincibleCounter % 10 < 5)) {
                if (image != null) {
                    g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
                }
            }

            // Giảm bộ đếm hiệu ứng vòng tròn sau khi hồi sinh
            if (shrinkCounter > 0) shrinkCounter--;

            // 4. Vẽ thanh máu (HP Bar)
            if (hpBarOn) {
                drawHPBar(g2);
            }
        }
    }

    private void drawHPBar(Graphics2D g2) {
        double oneScale = (double) gp.tileSize / maxLife;
        double hpBarValue = oneScale * life;

        g2.setColor(new Color(35, 35, 35));
        g2.fillRect(x - 1, y - 11, gp.tileSize + 2, 10);

        g2.setColor(new Color(255, 0, 30));
        g2.fillRect(x, y - 10, (int) hpBarValue, 8);

        hpBarCounter++;
        if (hpBarCounter > 300) hpBarOn = false;
    }

    private void drawMagicCircle(Graphics2D g2) {
        float alpha;
        int currentRadius;
        int baseRadius = 30;
        //1.khử răng cưa để vòng mượt
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 2. Tính toán độ trong suốt (Alpha) tăng dần khi sắp hồi sinh
        if (!alive) {
              // Hiệu ứng hiện dần khi đang chờ hồi sinh
            alpha = Math.min(1f, (respawnCounter - 180) / 120f);
            currentRadius = baseRadius;
        } else {
             // Hiệu ứng thu nhỏ và mờ dần sau khi đã hồi sinh
            alpha = shrinkCounter / 20f;
            currentRadius = (int) (baseRadius * (shrinkCounter / 20f));
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        float hue = (respawnCounter % 100) / 100f;
        g2.setColor(Color.getHSBColor(hue, 0.8f, 1.0f));
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(
                x + gp.tileSize / 2 - currentRadius / 2,
                y + gp.tileSize / 2 - currentRadius / 2,
                currentRadius,
                currentRadius
        );

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}