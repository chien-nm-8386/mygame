
package mygame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.InputStream;

public class UI {

    GamePanel gp;

    Font titleFont;
    Font smallFont;

    public UI(GamePanel gp) {
        this.gp = gp;

        // load font pixel
        titleFont = loadFont(16f);
        smallFont = loadFont(12f);
    }

    public void draw(Graphics2D g2) {
        drawPlayerHUD(g2);
    }

    private void drawPlayerHUD(Graphics2D g2) {
        if (gp.player == null) return;

        int panelX = 16;
        int panelY = 16;
        int panelWidth = 180;
        int panelHeight = 60;

        // nền HUD
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);

        // viền pixel style
        g2.setColor(new Color(255, 240, 200));
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        // ===== NAME =====
        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.name, panelX + 10, panelY + 18);

        // ===== HP BAR =====
        int barX = panelX + 10;
        int barY = panelY + 26;
        int barWidth = 110;
        int barHeight = 10;

        // viền ngoài
        g2.setColor(Color.BLACK);
        g2.fillRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);

        // nền bar
        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(barX, barY, barWidth, barHeight);

        // lượng máu
        int currentBar = (int) ((double) gp.player.health / gp.player.maxHealth * barWidth);

        if (gp.player.health > 60) {
            g2.setColor(new Color(80, 220, 120));
        } else if (gp.player.health > 30) {
            g2.setColor(new Color(255, 190, 80));
        } else {
            g2.setColor(new Color(255, 80, 80));
        }

        g2.fillRect(barX, barY, currentBar, barHeight);

        // highlight
        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillRect(barX, barY, currentBar, 2);

        // text HP
        g2.setFont(smallFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.health + "/" + gp.player.maxHealth,
                barX + barWidth + 8, barY + 9);

        // ===== EGG STATUS =====
        String eggText = gp.player.hasEgg ? "EGG: YES" : "EGG: NO";

        g2.setColor(gp.player.hasEgg ? new Color(255, 230, 90) : Color.LIGHT_GRAY);
        g2.drawString(eggText, panelX + 10, panelY + 48);
    }

    // ===== LOAD FONT =====
    private Font loadFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");

            if (is == null) {
                throw new RuntimeException("Không tìm thấy font!");
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);

        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }
}
