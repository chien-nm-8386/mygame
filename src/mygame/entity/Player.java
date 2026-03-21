
package mygame.entity;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import mygame.main.GameOverDialog;
import mygame.main.GamePanel;
import mygame.main.KeyHandler;
import mygame.main.VictoryDialog;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public boolean hasEgg = false;
    public String name = "Player";

    public int maxHealth = 100;
    public int health = 100;

    public boolean invincible = false;
    public int invincibleCounter = 0;

    private final int solidAreaDefaultX;
    private final int solidAreaDefaultY;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    private boolean victoryShown = false;
    private boolean gameOverShown = false;

    // Ảnh khi cầm trứng
    public BufferedImage up1_egg, up2_egg, down1_egg, down2_egg, left1_egg, left2_egg, right1_egg, right2_egg;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        solidArea = new Rectangle();
        solidArea.x = 12;
        solidArea.y = 24;
        solidArea.width = 24;
        solidArea.height = 20;

        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = gp.tileM.playerStartX;
        y = gp.tileM.playerStartY;
        speed = 4;
        direction = "down";

        hasEgg = false;

        maxHealth = 100;
        health = 100;

        invincible = false;
        invincibleCounter = 0;

        spriteCounter = 0;
        spriteNum = 1;

        victoryShown = false;
        gameOverShown = false;
    }

    public void getPlayerImage() {
        try {
            // player bình thường
            up1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_left2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player01_right2.png"));

            // player cầm trứng
            up1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_up1.png"));
            up2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_up2.png"));
            down1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_down1.png"));
            down2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_down2.png"));
            left1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_left1.png"));
            left2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_left2.png"));
            right1_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_right1.png"));
            right2_egg = ImageIO.read(getClass().getResourceAsStream("/res/tiles/player02_right2.png"));

        } catch (IOException e) {
            System.out.println("Lỗi tải ảnh player01/player02 trong res/tiles.");
            e.printStackTrace();
        }
    }

    public void update() {
        boolean isMoving = keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed;

        if (isMoving) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            collisionOn = false;
            gp.cChecker.checkTile(this);
            checkObjectInteraction();

            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        y -= speed;
                        break;
                    case "down":
                        y += speed;
                        break;
                    case "left":
                        x -= speed;
                        break;
                    case "right":
                        x += speed;
                        break;
                }
            }

            spriteCounter++;
            if (spriteCounter > 12) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
        }

        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 60) {
                invincible = false;
                invincibleCounter = 0;
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(
                x + solidArea.x,
                y + solidArea.y,
                solidArea.width,
                solidArea.height
        );
    }

    public void takeDamage(int damage) {
        if (!invincible && health > 0) {
            health -= damage;
            if (health < 0) {
                health = 0;
            }

            invincible = true;
            invincibleCounter = 0;

            System.out.println("Player bị mất " + damage + " máu! HP còn: " + health);

            if (health <= 0) {
                triggerGameOver();
            }
        }
    }

    public void triggerGameOver() {
        if (!gameOverShown) {
            gameOverShown = true;
            gp.stopGameThread();

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
            GameOverDialog gameOverDialog = new GameOverDialog(parentFrame, name, gp.main);
            gameOverDialog.showDialog();
        }
    }

    public void checkObjectInteraction() {
        if (!hasEgg && gp.tileM.eggRect != null) {
            String object = gp.cChecker.checkEntity(this, gp.tileM.eggRect, "Egg");
            if (object.equals("Egg")) {
                hasEgg = true;
                gp.tileM.eggCollected = true;
                gp.tileM.eggRect = null;
                System.out.println("Bạn đã nhặt được trứng!");
            }
        }

        if (gp.tileM.houseRect != null) {
            String reachHome = gp.cChecker.checkEntity(this, gp.tileM.houseRect, "House");
            if (reachHome.equals("House")) {
                if (hasEgg && !victoryShown) {
                    victoryShown = true;
                    gp.stopGameThread();

                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(gp);
                    VictoryDialog winDialog = new VictoryDialog(parentFrame, name, gp.main);
                    winDialog.showDialog();
                } else if (!hasEgg) {
                    System.out.println("Tìm trứng đã!");
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up":
                if (!hasEgg) image = (spriteNum == 1) ? up1 : up2;
                else image = (spriteNum == 1) ? up1_egg : up2_egg;
                break;

            case "down":
                if (!hasEgg) image = (spriteNum == 1) ? down1 : down2;
                else image = (spriteNum == 1) ? down1_egg : down2_egg;
                break;

            case "left":
                if (!hasEgg) image = (spriteNum == 1) ? left1 : left2;
                else image = (spriteNum == 1) ? left1_egg : left2_egg;
                break;

            case "right":
                if (!hasEgg) image = (spriteNum == 1) ? right1 : right2;
                else image = (spriteNum == 1) ? right1_egg : right2_egg;
                break;
        }

        if (!(invincible && invincibleCounter % 6 < 3)) {
            if (image != null) {
                g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
            }
        }

        drawPlayerUI(g2);
    }

    private void drawPlayerUI(Graphics2D g2) {
        g2.setFont(g2.getFont().deriveFont(18f));
        FontMetrics fm = g2.getFontMetrics();

        int textX = x + (gp.tileSize - fm.stringWidth(name)) / 2;
        int textY = y - 8;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(name, textX + 2, textY + 2);

        g2.setColor(Color.WHITE);
        g2.drawString(name, textX, textY);

        if (hasEgg) {
            g2.setFont(g2.getFont().deriveFont(12f));
            g2.setColor(new Color(0, 0, 0, 150));
            g2.drawString("GOT EGG!", x + 1, y - 21);

            g2.setColor(Color.YELLOW);
            g2.drawString("GOT EGG!", x, y - 22);
        }
    }
}
