package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

public class MenuPanel extends JPanel {

    Main main;
    Image background;

    Rectangle startButton, guideButton, exitButton;
    String hoveredButton = "";

    // Khai báo biến Font toàn cục
    Font buttonFont;

    Sound menuMusic = new Sound();
    Sound clickSound = new Sound();

    public MenuPanel(Main main) {
        this.main = main;
        setPreferredSize(new Dimension(1024, 768));
        setFocusable(true);

        // 1. Tải Font Pixel từ file
        loadCustomFont();

        // 2. Tải ảnh nền
        try {
            background = new ImageIcon(getClass().getResource("/res/ui/menu_bg.png")).getImage();
        } catch (Exception e) {
            System.err.println("Không thể tải menu_bg.png");
        }

        // 3. Khởi tạo kích thước nút và căn giữa
        int btnW = 220;
        int btnH = 50;
        int centerX = (1024 - btnW) / 2;

        startButton = new Rectangle(centerX, 320, btnW, btnH);
        guideButton = new Rectangle(centerX, 400, btnW, btnH);
        exitButton  = new Rectangle(centerX, 480, btnW, btnH);

        setupMouseEvents();
        playMenuMusic();
    }

    private void loadCustomFont() {
        try {
            // ĐỔI TÊN FILE .ttf CỦA BẠN TẠI ĐÂY
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");
            buttonFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(32f);
            
            // Đăng ký font với hệ thống
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(buttonFont);
        } catch (Exception e) {
            System.err.println("Không load được font, dùng font mặc định.");
            buttonFont = new Font("Arial", Font.BOLD, 26);
        }
    }

    private void setupMouseEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (startButton.contains(p)) {
                    playClickSound();
                    handleStartGame();
                } else if (guideButton.contains(p)) {
                    playClickSound();
                    showGuide();
                } else if (exitButton.contains(p)) {
                    playClickSound();
                    System.exit(0);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                String lastHover = hoveredButton;
                
                if (startButton.contains(p)) hoveredButton = "start";
                else if (guideButton.contains(p)) hoveredButton = "guide";
                else if (exitButton.contains(p)) hoveredButton = "exit";
                else hoveredButton = "";

                if (!lastHover.equals(hoveredButton)) {
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // Bật khử răng cưa cho hình khối nhưng TẮT cho chữ Pixel để sắc nét
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }

        drawGameButton(g2, startButton, "PLAY GAME", hoveredButton.equals("start"));
        drawGameButton(g2, guideButton, "HOW TO PLAY", hoveredButton.equals("guide"));
        drawGameButton(g2, exitButton, "QUIT", hoveredButton.equals("exit"));

        g2.dispose();
    }

   private void drawGameButton(Graphics2D g2, Rectangle rect, String text, boolean isHovered) {
    int yOffset = isHovered ? -4 : 0;
    
    // 1. Đổ bóng nút (Shadow) - Làm đậm hơn để tăng độ tách biệt
    g2.setColor(new Color(0, 0, 0, 120));
    g2.fillRoundRect(rect.x + 3, rect.y + 7, rect.width, rect.height, 12, 12);

    // 2. Màu sắc Gradient cho Nút
    Color colorTop = isHovered ? new Color(255, 225, 120) : new Color(170, 110, 45);
    Color colorBottom = isHovered ? new Color(210, 130, 35) : new Color(110, 65, 15);
    
    GradientPaint gp = new GradientPaint(
        rect.x, rect.y + yOffset, colorTop, 
        rect.x, rect.y + rect.height + yOffset, colorBottom
    );
    g2.setPaint(gp);
    g2.fillRoundRect(rect.x, rect.y + yOffset, rect.width, rect.height, 12, 12);

    // 3. Viền nút sắc nét hơn
    g2.setStroke(new BasicStroke(2.5f));
    g2.setColor(new Color(50, 25, 5));
    g2.drawRoundRect(rect.x, rect.y + yOffset, rect.width, rect.height, 12, 12);

    // 4. Vẽ Text (Font ThaleahFat cần cỡ lớn mới đẹp)
    g2.setFont(buttonFont.deriveFont(32f)); // Tăng size lên 32
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    FontMetrics fm = g2.getFontMetrics();
    int textX = rect.x + (rect.width - fm.stringWidth(text)) / 2;
    int textY = rect.y + yOffset + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();

    // Bóng đổ cho chữ (Style Pixel - dịch chuyển 2px)
    g2.setColor(new Color(45, 25, 10));
    g2.drawString(text, textX + 2, textY + 2);
    
    // Màu chữ chính
    g2.setColor(isHovered ? Color.WHITE : new Color(255, 240, 200));
    g2.drawString(text, textX, textY);
    
    // 5. GIẢI QUYẾT CẢM GIÁC CỤT NGỦN: Vẽ họa tiết 2 bên
    if (isHovered) {
        g2.setColor(new Color(255, 255, 255, 180));
        // Vẽ 2 hình vuông Pixel nhỏ ở 2 đầu chữ để "kéo dài" nội dung ra
        int decorSize = 8;
        // Bên trái
        g2.fillRect(textX - 25, textY - 14, decorSize, decorSize); 
        // Bên phải
        g2.fillRect(textX + fm.stringWidth(text) + 15, textY - 14, decorSize, decorSize);
        
        // Vẽ thêm 1 đường line trắng mờ cực nhỏ ở đỉnh nút (Highlight)
        g2.setColor(new Color(255, 255, 255, 100));
        g2.fillRect(rect.x + 10, rect.y + yOffset + 5, rect.width - 20, 2);
    }
}

    private void handleStartGame() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        NameInputDialog dialog = new NameInputDialog(parent);
        String name = dialog.showDialog();
        if (name != null && !name.trim().isEmpty()) {
            stopMenuMusic();
            main.startGame(name.trim());
        }
    }

    private void showGuide() {
        GuideDialog guide = new GuideDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        guide.showDialog();
    }

    public void playMenuMusic() {
        menuMusic.setFile("/res/audio/menu_music.wav");
        menuMusic.play();
        menuMusic.loop();
    }

    public void stopMenuMusic() { menuMusic.stop(); }
    public void playClickSound() {
        clickSound.setFile("/res/audio/click.wav");
        clickSound.play();
    }
}