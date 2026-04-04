package mygame.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.InputStream;
import java.awt.GradientPaint;
import java.awt.BasicStroke;

public class UI {

    GamePanel gp;

    Font titleFont;
    Font smallFont;
    Font bigFont;

    // Nút qua màn / quay lại menu / tạm dừng
    public Rectangle nextLevelBtn = new Rectangle();
    public Rectangle backBtn = new Rectangle();
    public Rectangle continueBtn = new Rectangle();
    public Rectangle menuBtn = new Rectangle();
    public Rectangle musicMinusBtn = new Rectangle();
    public Rectangle musicPlusBtn = new Rectangle();

    public Rectangle readyBtn = new Rectangle();
    public Rectangle pauseBtn = new Rectangle();
    
    public Rectangle footMinusBtn = new Rectangle();
    public Rectangle footPlusBtn = new Rectangle();

    
    public String inputNumber = ""; // Lưu số người chơi nhập
    public boolean inputFinished = false; // Trạng thái đã nhấn Enter hay chưa
    public String finalDish = ""; // Tên món ăn cuối cùng

    public UI(GamePanel gp) {
        this.gp = gp;

        titleFont = loadFont(16f);
        smallFont = loadFont(12f);
        bigFont = loadFont(28f);
    }

    public void draw(Graphics2D g2) {
        drawPlayerHUD(g2);

        if (gp.gameState == gp.STATE_LEVEL_COMPLETE) {
            drawLevel1WinScreen(g2);
        } else if (gp.gameState == gp.STATE_GAME_WIN || gp.gameState == gp.STATE_GAME_COMPLETED) {
            drawGameWinScreen(g2);
        } else if (gp.gameState == gp.STATE_PAUSE) {
            drawPauseScreen(g2);
        }
        if (gp.gameState == gp.STATE_LEVEL_TRANSITION) {
            drawLevelTransitionScreen(g2);
        }
    }

    private int getUIWidth() {
        return gp.getWidth() > 0 ? gp.getWidth() : gp.screenWidth;
    }

    private int getUIHeight() {
        return gp.getHeight() > 0 ? gp.getHeight() : gp.screenHeight;
    }

    private void setCenteredButtonBounds(Rectangle btn, int y, int width, int height) {
        int w = getUIWidth();
        int x = (w - width) / 2;
        btn.setBounds(x, y, width, height);
    }

    private void updatePauseButtons() {
        int h = getUIHeight();
        int w = getUIWidth();
        int btnW = 340;
        int btnH = 80;

        setCenteredButtonBounds(continueBtn, h / 2 + 20, btnW, btnH);
        setCenteredButtonBounds(menuBtn, h / 2 + 130, btnW, btnH);
        
        int rowW = 60;
        int rowH = 40;
        int centerX = w / 2;

        int musicY = h / 2 - 40;
        int footY = h / 2 + 25;

        musicMinusBtn.setBounds(centerX - 140, musicY, rowW, rowH);
        musicPlusBtn.setBounds(centerX + 80, musicY, rowW, rowH);
       
        footMinusBtn.setBounds(centerX - 140, footY, rowW, rowH);
        footPlusBtn.setBounds(centerX + 80, footY, rowW, rowH);
       
    }

    private void updateGameWinButtons() {
        int h = getUIHeight();
        int btnW = 340;
        int btnH = 80;

        setCenteredButtonBounds(backBtn, h / 2 + 70, btnW, btnH);
    }

    private void updateGameCompletedButtons() {
        int h = getUIHeight();
        int btnW = 340;
        int btnH = 80;

        setCenteredButtonBounds(nextLevelBtn, h / 2 + 50, btnW, btnH);
        setCenteredButtonBounds(backBtn, h / 2 + 160, btnW, btnH);
    }

    private void updateLevelCompleteButton() {
        int h = getUIHeight();
        int btnW = 340;
        int btnH = 80;

        setCenteredButtonBounds(nextLevelBtn, h / 2 + 60, btnW, btnH);
    }

    private void drawPlayerHUD(Graphics2D g2) {
        if (gp.player == null) return;

        int panelX = 16;
        int panelY = 16;
        int panelWidth = 200;
        int panelHeight = 65;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(panelX, panelY, panelWidth, panelHeight);

        g2.setColor(new Color(255, 240, 200));
        g2.drawRect(panelX, panelY, panelWidth, panelHeight);

        g2.setFont(titleFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.name, panelX + 10, panelY + 18);

        int barX = panelX + 10;
        int barY = panelY + 26;
        int barWidth = 110;
        int barHeight = 10;

        g2.setColor(Color.BLACK);
        g2.fillRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4);

        g2.setColor(new Color(60, 60, 60));
        g2.fillRect(barX, barY, barWidth, barHeight);

        int currentBar = (int) ((double) gp.player.health / gp.player.maxHealth * barWidth);

        if (gp.player.health > 60) {
            g2.setColor(new Color(80, 220, 120));
        } else if (gp.player.health > 30) {
            g2.setColor(new Color(255, 190, 80));
        } else {
            g2.setColor(new Color(255, 80, 80));
        }

        g2.fillRect(barX, barY, currentBar, barHeight);

        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillRect(barX, barY, currentBar, 2);

        g2.setFont(smallFont);
        g2.setColor(Color.WHITE);
        g2.drawString(gp.player.health + "/" + gp.player.maxHealth,
                barX + barWidth + 8, barY + 9);

        g2.setFont(titleFont);

        int targetEggs = (gp.currentLevel == 2) ? 2 : 1;
        String eggText = "EGGS: " + gp.eggsCollected + "/" + targetEggs;

        if (gp.eggsCollected >= targetEggs) {
            g2.setColor(new Color(255, 230, 90));
        } else {
            g2.setColor(Color.LIGHT_GRAY);
        }

        g2.drawString(eggText, panelX + 10, panelY + 52);
    }

    private void drawLevel1WinScreen(Graphics2D g2) {
        int w = getUIWidth();
        int h = getUIHeight();

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, w, h);

        int frameW = 420;
        int frameH = 250;
        int frameX = (w - frameW) / 2;
        int frameY = (h - frameH) / 2;

        Color topColor = new Color(85, 55, 25);
        Color bottomColor = new Color(45, 28, 12);
        GradientPaint gpPaint = new GradientPaint(frameX, frameY, topColor, frameX, frameY + frameH, bottomColor);
        g2.setPaint(gpPaint);
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 30, 30);

        g2.setColor(new Color(150, 120, 70));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(frameX, frameY, frameW, frameH, 30, 30);

        int centerY = frameY + 25;

        g2.setFont(new Font("Arial", Font.BOLD, 26));
        g2.setColor(new Color(255, 230, 90));
        String title = "CHIẾN THẮNG VANG DỘI!";
        drawCenteredText(g2, title, frameX, frameW, centerY += 35);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        String sub1 = "Lũ gà đã bất lực nhìn " + gp.player.name + " mang trứng đi!";
        drawCenteredText(g2, sub1, frameX, frameW, centerY += 42);

        g2.setFont(new Font("Arial", Font.ITALIC, 15));
        g2.setColor(new Color(255, 180, 100));
        String sub2 = "Thử thách thực sự đang chờ đợi bạn ở phía trước...";
        drawCenteredText(g2, sub2, frameX, frameW, centerY += 30);

        int btnW = 260;
        int btnH = 60;
        int btnX = frameX + (frameW - btnW) / 2;
        int btnY = frameY + frameH - btnH - 25;
        nextLevelBtn.setBounds(btnX, btnY, btnW, btnH);

        boolean hover = nextLevelBtn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 20, 20);

        g2.setColor(new Color(50, 30, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(nextLevelBtn.x, nextLevelBtn.y, nextLevelBtn.width, nextLevelBtn.height, 20, 20);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(new Color(50, 30, 0));
        drawCenteredTextInButton(g2, "NEXT LEVEL", nextLevelBtn);
    }

    private void drawPauseScreen(Graphics2D g2) {
        int w = getUIWidth();
        int h = getUIHeight();

        // 1. Phủ nền đen mờ toàn màn hình
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, w, h);

        // 2. Định nghĩa Khung Pause (Tất cả tọa độ sẽ dựa vào khung này)
        int frameW = 550; // Chiều rộng khung
        int frameH = 450; // Chiều cao khung
        int frameX = (w - frameW) / 2;
        int frameY = (h - frameH) / 2;

        // Vẽ khung (Gỗ)
        g2.setColor(new Color(85, 55, 25)); // Nâu đậm
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 35, 35);
        g2.setColor(new Color(255, 240, 200)); // Viền kem
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(frameX, frameY, frameW, frameH, 35, 35);

        // 3. Tiêu đề "PAUSED" (Căn giữa khung)
        g2.setFont(loadFont(40f));
        g2.setColor(new Color(255, 230, 90));
        String title = "PAUSED";
        int titleX = frameX + (frameW - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, frameY + 60);

        // 4. Mô tả nhỏ
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(Color.WHITE);
        String sub = "Tùy chỉnh âm thanh hoặc quay về menu";
        int subX = frameX + (frameW - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, subX, frameY + 90);

        // --- PHẦN CÀI ĐẶT ÂM THANH (Căn chỉnh lại cho hết lệch) ---
        int labelX = frameX + 50;  // Lề trái cho chữ
        int valueX = frameX + 210; // Vị trí hiện số âm lượng
        int controlX = frameX + 300; // Vị trí bắt đầu các nút +, -

        g2.setFont(loadFont(20f));
        g2.setColor(Color.WHITE);

        // Dòng GAME MUSIC
       int musicY = frameY + 150;
        g2.drawString("GAME MUSIC", labelX, musicY + 25);

        drawValueBox(g2, new Rectangle(valueX, musicY, 70, 40),
                String.valueOf(gp.getGameMusicVolume()));

        musicMinusBtn.setBounds(controlX, musicY, 50, 40);
        musicPlusBtn.setBounds(controlX + 65, musicY, 50, 40);

        drawSmallButton(g2, musicMinusBtn, "-");
        drawSmallButton(g2, musicPlusBtn, "+");

        // Dòng FOOTSTEP
        int footY = frameY + 210;
        g2.setColor(Color.WHITE);
        g2.drawString("FOOTSTEP", labelX, footY + 25);

        drawValueBox(g2, new Rectangle(valueX, footY, 70, 40),
                String.valueOf(gp.getFootstepVolume()));

        footMinusBtn.setBounds(controlX, footY, 50, 40);
        footPlusBtn.setBounds(controlX + 65, footY, 50, 40);

        drawSmallButton(g2, footMinusBtn, "-");
        drawSmallButton(g2, footPlusBtn, "+");

        // --- HỆ THỐNG NÚT BẤM ĐIỀU HƯỚNG ---
        int btnW = 300;
        int btnH = 60;
        int btnX = frameX + (frameW - btnW) / 2;

        // Nút CONTINUE
        continueBtn.setBounds(btnX, frameY + 290, btnW, btnH);
        drawLargeStyledButton(g2, continueBtn, "CONTINUE");

        // Nút MAIN MENU
        menuBtn.setBounds(btnX, frameY + 365, btnW, btnH);
        drawLargeStyledButton(g2, menuBtn, "MAIN MENU");
    }

    // Hàm vẽ nút lớn cho đồng bộ
    private void drawLargeStyledButton(Graphics2D g2, Rectangle btn, String text) {
        boolean hover = btn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);

        // Đổ bóng cho nút
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(btn.x + 3, btn.y + 3, btn.width, btn.height, 20, 20);

        // Màu nút
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 20, 20);

        // Viền nút
        g2.setColor(new Color(50, 30, 0));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 20, 20);

        // Chữ trong nút
        g2.setFont(loadFont(22f));
        g2.setColor(new Color(50, 30, 0));
        drawCenteredTextInButton(g2, text, btn);
    }

    private Font loadFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");
            if (is == null) {
                return new Font("Arial", Font.BOLD, (int) size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (Exception e) {
            return new Font("Arial", Font.BOLD, (int) size);
        }
    }


    private void drawGameWinScreen(Graphics2D g2) {
        int w = getUIWidth();
        int h = getUIHeight();

        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, w, h);

        int frameW = 620;
        int frameH = 450;
        int frameX = (w - frameW) / 2;
        int frameY = (h - frameH) / 2 - 20;

        g2.setColor(new Color(85, 55, 25)); 
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 35, 35);

        g2.setStroke(new BasicStroke(5f));
        g2.setColor(new Color(255, 240, 200));  
        g2.drawRoundRect(frameX, frameY, frameW, frameH, 35, 35);

        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(new Color(255, 230, 90)); 
        String title = "GAME COMPLETED";
        int titleX = w / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, frameY + 70);

        g2.setFont(new Font("Arial", Font.BOLD, 19));
        g2.setColor(Color.WHITE);
        String sub1 = gp.player.name + " đã hoàn toàn chiến thắng trước lũ gà!";
        drawCenteredText(g2, sub1, frameX, frameW, frameY + 110);

        if (!inputFinished) {
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.setColor(Color.WHITE);
            drawCenteredText(g2, "Hãy nhập một con số may mắn (1-10):", frameX, frameW, frameY + 150);

            int boxW = 120, boxH = 50;
            int boxX = w / 2 - boxW / 2, boxY = frameY + 170;
            g2.setColor(Color.BLACK);
            g2.fillRect(boxX, boxY, boxW, boxH);
            g2.setColor(Color.WHITE);
            g2.drawRect(boxX, boxY, boxW, boxH);

            g2.setFont(bigFont.deriveFont(30f));
            String cursor = (System.currentTimeMillis() % 1000 < 500 ? "|" : "");
            drawCenteredText(g2, inputNumber + cursor, frameX, frameW, boxY + 35);
            
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            drawCenteredText(g2, "Nhấn ENTER để xác nhận", frameX, frameW, boxY + 75);
        } else {
            g2.setFont(new Font("Arial", Font.ITALIC, 17));
            g2.setColor(new Color(255, 170, 50));
            drawCenteredText(g2, "Giờ thì cùng chế biến trứng thành món ngon nhé...", frameX, frameW, frameY + 150);

            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.setColor(new Color(144, 238, 144)); 
            drawCenteredText(g2, "MÓN ĂN BẠN ĐÃ CHỌN: " + finalDish, frameX, frameW, frameY + 210);

            g2.setFont(new Font("Arial", Font.ITALIC, 18));
            g2.setColor(new Color(255, 240, 200));
            drawCenteredText(g2, "Hãy thưởng thức thật ngon miệng sau thời gian nhặt trứng vất vả nhé!", frameX, frameW, frameY + 250);
        }

        int btnW = 280, btnH = 60;
        int btnX = frameX + (frameW - btnW) / 2;
        
        int btnY1 = frameY + 280; 
        nextLevelBtn.setBounds(btnX, btnY1, btnW, btnH);
        drawStyledButton(g2, nextLevelBtn, "CHƠI LẠI");

        int btnY2 = frameY + 355; 
        backBtn.setBounds(btnX, btnY2, btnW, btnH);
        drawStyledButton(g2, backBtn, "QUAY VỀ MENU");
    }

    
    private void drawStyledButton(Graphics2D g2, Rectangle btn, String text) {
        boolean hover = btn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(btn.x + 3, btn.y + 4, btn.width, btn.height, 20, 20);
        g2.setColor(hover ? new Color(255, 210, 90) : new Color(255, 180, 50));
        g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 20, 20);
        g2.setColor(new Color(50, 30, 0));
        g2.setStroke(new BasicStroke(hover ? 3 : 2)); 
        g2.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 20, 20);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(new Color(50, 30, 0));
        drawCenteredTextInButton(g2, text, btn);
    }

    private void drawCenteredText(Graphics2D g2, String text, int areaX, int areaWidth, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = areaX + (areaWidth - fm.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    private void drawCenteredTextInButton(Graphics2D g2, String text, Rectangle btn) {
        FontMetrics fm = g2.getFontMetrics();
        int x = btn.x + (btn.width - fm.stringWidth(text)) / 2;
        int y = btn.y + ((btn.height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, x, y);
    }

    public void drawLevelTransitionScreen(Graphics2D g2) {
        int w = getUIWidth();
        int h = getUIHeight();

        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, w, h);

        int frameW = 600;
        int frameH = 350;
        int frameX = (w - frameW) / 2;
        int frameY = (h - frameH) / 2;

        g2.setColor(new Color(85, 55, 25)); 
        g2.fillRoundRect(frameX, frameY, frameW, frameH, 35, 35);
        g2.setStroke(new BasicStroke(5f));
        g2.setColor(new Color(255, 240, 200)); 
        g2.drawRoundRect(frameX, frameY, frameW, frameH, 35, 35);

        g2.setFont(new Font("Arial", Font.BOLD, 30)); 
        g2.setColor(new Color(255, 230, 90));
        drawCenteredText(g2, "CHUẨN BỊ CHO LEVEL 2", frameX, frameW, frameY + 60);

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(Color.WHITE);
        drawCenteredText(g2, "LƯU Ý:", frameX, frameW, frameY + 110);

        g2.setFont(new Font("Arial", Font.PLAIN, 17));
        drawCenteredText(g2, "Tại level 2, bạn cần nhặt đủ 2 quả trứng liên tiếp", frameX, frameW, frameY + 145);
        drawCenteredText(g2, "trước khi về nhà để hoàn thành trò chơi.", frameX, frameW, frameY + 175);

        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        g2.setColor(new Color(255, 170, 50));
        drawCenteredText(g2, "Bạn đã sẵn sàng chưa?", frameX, frameW, frameY + 220);

        int btnW = 220;
        int btnH = 55;

        readyBtn.setBounds(w / 2 - btnW - 15, frameY + 260, btnW, btnH);
        drawStyledButton(g2, readyBtn, "SẴN SÀNG");

        nextLevelBtn.setBounds(w / 2 + 15, frameY + 260, btnW, btnH);
        drawStyledButton(g2, nextLevelBtn, "CHƠI LẠI");
    }

    private void drawSmallButton(Graphics2D g2, Rectangle btn, String text) {
        boolean hover = btn.contains(gp.mouseH.mouseX, gp.mouseH.mouseY);
        g2.setColor(hover ? new Color(255, 220, 120) : new Color(255, 180, 70));
        g2.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 16, 16);
        g2.setColor(new Color(60, 30, 10));
        g2.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 16, 16);
        g2.setFont(loadFont(16f));
        g2.setColor(Color.BLACK);
        drawCenteredTextInButton(g2, text, btn);
    }
    private void drawValueBox(Graphics2D g2, Rectangle box, String text) {
        g2.setColor(new Color(70, 40, 15));
        g2.fillRoundRect(box.x, box.y, box.width, box.height, 12, 12);

        g2.setColor(new Color(255, 240, 200));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(box.x, box.y, box.width, box.height, 12, 12);

        g2.setFont(loadFont(18f));
        g2.setColor(new Color(255, 230, 120));

        FontMetrics fm = g2.getFontMetrics();
        int tx = box.x + (box.width - fm.stringWidth(text)) / 2;
        int ty = box.y + ((box.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(text, tx, ty);
    }
}