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
    
    // Bộ đếm thời gian hồi sinh
    public int defaultX, defaultY;
    public int respawnCounter = 0;
    public int shrinkCounter = 0; // Bộ đếm hiệu ứng thu nhỏ vòng tròn sau khi hồi sinh

    // --- AI DI CHUYỂN & NÉ VẬT CẢN ---
    private int stuckCooldown = 0;
    private String lastBlockedDirection = "";
    private int avoidTimer = 0;
    private String avoidDirection = null;

    // --- BIẾN INSTANCE (Dùng để vẽ cho từng con gà) ---
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;

    // --- KHO CHỨA ẢNH DÙNG CHUNG (STATIC) ---
    private static BufferedImage up1_s, angry_s;
    private static BufferedImage up1_egg_s, up2_egg_s, down1_egg_s, down2_egg_s;
    private static BufferedImage left1_egg_s, left2_egg_s, right1_egg_s, right2_egg_s;
    private static boolean imagesLoaded = false;

    public Chicken(GamePanel gp, int startX, int startY) {
        super(gp);
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.defaultX = startX;
        this.defaultY = startY;
        this.speed = 1;
        this.direction = "down";

        this.maxLife = 100;
        this.life = maxLife;
        this.alive = true;

        solidArea = new Rectangle(18, 18, 28, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        if (!imagesLoaded) {
            getChickenImage();
        }
        
        assignStaticImages();
    }

    public void getChickenImage() {
        try {
            System.out.println("Dang nap tai nguyen Ga vao Static (Chi chay 1 lan)...");
            
            up1_s = setup("/res/tiles/chicken_ngu.png");
            angry_s = setup("/res/tiles/chicken_gian.png");
            
            up1_egg_s = setup("/res/tiles/chicken_up1.png");
            up2_egg_s = setup("/res/tiles/chicken_up2.png");
            down1_egg_s = setup("/res/tiles/chicken_down1.png");
            down2_egg_s = setup("/res/tiles/chicken_down2.png");
            left1_egg_s = setup("/res/tiles/chicken_left1.png");
            left2_egg_s = setup("/res/tiles/chicken_left2.png");
            right1_egg_s = setup("/res/tiles/chicken_right1.png");
            right2_egg_s = setup("/res/tiles/chicken_right2.png");

            imagesLoaded = true;
        } catch (IOException e) {
            System.out.println("Loi tai anh ga!");
        }
    }

    private void assignStaticImages() {
        this.up1 = up1_s;
        this.down1 = up1_s;
        this.left1 = up1_s;
        this.right1 = up1_s;
        this.angry = angry_s;

        this.up1_egg = up1_egg_s;
        this.up2_egg = up2_egg_s;
        this.down1_egg = down1_egg_s;
        this.down2_egg = down2_egg_s;
        this.left1_egg = left1_egg_s;
        this.left2_egg = left2_egg_s;
        this.right1_egg = right1_egg_s;
        this.right2_egg = right2_egg_s;
    }

    public BufferedImage setup(String path) throws IOException {
        return ImageIO.read(getClass().getResourceAsStream(path));
    }

    @Override
    public void update() {
        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 40) {
                invincible = false;
                invincibleCounter = 0;
            }
        }

        if (stuckCooldown > 0) stuckCooldown--;

        int diffX = (gp.player.x + gp.tileSize / 2) - (this.x + gp.tileSize / 2);
        int diffY = (gp.player.y + gp.tileSize / 2) - (this.y + gp.tileSize / 2);
        double distance = Math.sqrt(diffX * diffX + diffY * diffY);

        if (gp.player.hasEgg && distance < 250) { 
            attacking = true;
            moveTowardPlayer(diffX, diffY);

            if (distance <= gp.tileSize) {
                if (!invincible) {
                    attackCounter++; 
                    if (attackCounter >= 120) {
                        gp.player.takeDamage(10); 
                        System.out.println("Ga da chu dong mo Player!");
                        attackCounter = 0; 
                    }
                } else {
                    attackCounter = 0; 
                }
            } else {
                attackCounter = 0; 
            }
        } else {
            attacking = false;
            attackCounter = 0;
            spriteNum = 1;
            return;
        }

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
        if (!invincible && alive) {
            life -= damage;
            hpBarOn = true;
            hpBarCounter = 0;
            invincible = true;
            invincibleCounter = 0;
            
            // Đẩy lùi
            if (gp.player.direction.equals("up")) y -= 10;
            if (gp.player.direction.equals("down")) y += 10;
            if (gp.player.direction.equals("left")) x -= 10;
            if (gp.player.direction.equals("right")) x += 10;
            
            if (life <= 0) {
                life = 0;
                alive = false;
                hpBarOn = false;
                
                // Gọi hàm phát âm thanh chết từ GamePanel
                gp.playDeathSound();
                
                int healBonus = 10; 
                gp.player.health += healBonus;
                if (gp.player.health > gp.player.maxHealth) gp.player.health = gp.player.maxHealth;
                System.out.println("Ga da chet! Player duoc hoi " + healBonus + " HP.");
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
        this.shrinkCounter = 20;
    }

    @Override
    public void draw(Graphics2D g2) {
        if ((!alive && respawnCounter > 180) || (alive && shrinkCounter > 0)) {
            drawMagicCircle(g2);
        }

        if (alive) {
            BufferedImage image = null;
            int diffX = (gp.player.x + gp.tileSize / 2) - (this.x + gp.tileSize / 2);
            int diffY = (gp.player.y + gp.tileSize / 2) - (this.y + gp.tileSize / 2);
            double distance = Math.sqrt(diffX * diffX + diffY * diffY);

            if (!attacking) {
                image = up1;
            } else {
                if (distance <= gp.tileSize + 5) { 
                    image = angry; 
                } else {
                    switch (direction) {
                        case "up": image = (spriteNum == 1) ? up1_egg : up2_egg; break;
                        case "down": image = (spriteNum == 1) ? down1_egg : down2_egg; break;
                        case "left": image = (spriteNum == 1) ? left1_egg : left2_egg; break;
                        case "right": image = (spriteNum == 1) ? right1_egg : right2_egg; break;
                    }
                }
            }

            if (!(invincible && invincibleCounter % 10 < 5)) {
                if (image != null) {
                    g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
                }
            }

            if (shrinkCounter > 0) shrinkCounter--;
            if (hpBarOn) drawHPBar(g2);
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
        int drawX = (!alive) ? defaultX : x;
        int drawY = (!alive) ? defaultY : y;
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (!alive) {
            alpha = Math.min(1f, (respawnCounter - 180) / 120f);
            currentRadius = baseRadius;
        } else {
            alpha = shrinkCounter / 20f;
            currentRadius = (int) (baseRadius * (shrinkCounter / 20f));
        }
        if (alpha < 0) alpha = 0; 
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        float hue = (respawnCounter % 100) / 100f;
        g2.setColor(Color.getHSBColor(hue, 0.8f, 1.0f));
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(drawX + gp.tileSize/2 - currentRadius/2, drawY + gp.tileSize/2 - currentRadius/2, currentRadius, currentRadius);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}