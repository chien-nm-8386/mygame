package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel {

    Main main;
    Image background;

    Rectangle startButton = new Rectangle(402, 300, 220, 55);
    Rectangle guideButton = new Rectangle(402, 375, 220, 55);
    Rectangle exitButton = new Rectangle(402, 450, 220, 55);

    String hoveredButton = "";

    Font buttonFont = new Font("Arial", Font.BOLD, 24);

    Sound menuMusic = new Sound();
    Sound clickSound = new Sound();

    public MenuPanel(Main main) {
        this.main = main;

        setPreferredSize(new Dimension(1024, 768));
        setFocusable(true);

        try {
            background = new ImageIcon(getClass().getResource("/res/ui/menu_bg.png")).getImage();
        } catch (Exception e) {
            System.err.println("Không thể tải menu_bg.png");
            e.printStackTrace();
        }

//        clickSound.setFile("/res/audio/click.wav");
        playMenuMusic();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();

                if (startButton.contains(p)) {
                    playClickSound();

                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(MenuPanel.this);
                    NameInputDialog dialog = new NameInputDialog(parentFrame);
                    String playerName = dialog.showDialog();

                    if (playerName != null && !playerName.trim().isEmpty()) {
                        stopMenuMusic();
                        main.startGame(playerName.trim());
                    }

                } else if (guideButton.contains(p)) {
                    playClickSound();

                    JOptionPane.showMessageDialog(
                            MenuPanel.this,
                            "HƯỚNG DẪN CHƠI:\n"
                            + "- Dùng W A S D hoặc phím mũi tên để di chuyển\n"
                            + "- Tìm đường ra khỏi mê cung\n"
                            + "- Nhấn ESC để quay lại menu",
                            "Hướng dẫn",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } else if (exitButton.contains(p)) {
                    playClickSound();
                    stopMenuMusic();
                    System.exit(0);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();

                if (startButton.contains(p)) {
                    hoveredButton = "start";
                } else if (guideButton.contains(p)) {
                    hoveredButton = "guide";
                } else if (exitButton.contains(p)) {
                    hoveredButton = "exit";
                } else {
                    hoveredButton = "";
                }

                repaint();
            }
        });
    }

    public void playMenuMusic() {
        menuMusic.stop();
        menuMusic.setFile("/res/audio/menu_music.wav");
        if (menuMusic.isLoaded()) {
            menuMusic.reset();
            menuMusic.play();
            menuMusic.loop();
        }
    }

    public void stopMenuMusic() {
        menuMusic.stop();
    }

    public void playClickSound() {
        clickSound.setFile("/res/audio/click.wav");
        if (clickSound.isLoaded()) {
            clickSound.reset();
            clickSound.play();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(new Color(30, 30, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // làm nền sáng nhẹ
        g2.setColor(new Color(255, 255, 255, 35));
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawButton(g2, startButton, "BẮT ĐẦU", hoveredButton.equals("start"));
        drawButton(g2, guideButton, "HƯỚNG DẪN", hoveredButton.equals("guide"));
        drawButton(g2, exitButton, "THOÁT", hoveredButton.equals("exit"));

        g2.dispose();
    }

    private void drawButton(Graphics2D g2, Rectangle rect, String text, boolean hovered) {
        Color fillTop = hovered ? new Color(255, 226, 140) : new Color(215, 160, 86);
        Color fillBottom = hovered ? new Color(245, 180, 90) : new Color(168, 108, 54);
        Color borderColor = new Color(95, 60, 30);
        Color shadowColor = new Color(40, 20, 10, 130);

        g2.setColor(shadowColor);
        g2.fillRoundRect(rect.x + 4, rect.y + 5, rect.width, rect.height, 20, 20);

        GradientPaint buttonPaint = new GradientPaint(
                rect.x, rect.y, fillTop,
                rect.x, rect.y + rect.height, fillBottom
        );
        g2.setPaint(buttonPaint);
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setColor(new Color(255, 255, 255, hovered ? 90 : 55));
        g2.fillRoundRect(rect.x + 6, rect.y + 4, rect.width - 12, rect.height / 2 - 3, 16, 16);

        g2.setStroke(new BasicStroke(4));
        g2.setColor(borderColor);
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 20, 20);

        g2.setFont(buttonFont);
        FontMetrics fm = g2.getFontMetrics();

        int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
        int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

        g2.setColor(new Color(70, 40, 15));
        g2.drawString(text, textX + 2, textY + 2);

        g2.setColor(new Color(255, 245, 210));
        g2.drawString(text, textX, textY);

        if (hovered) {
            g2.setStroke(new BasicStroke(2));
            g2.setColor(new Color(255, 245, 190, 180));
            g2.drawRoundRect(rect.x - 3, rect.y - 3, rect.width + 6, rect.height + 6, 24, 24);
        }
    }
}