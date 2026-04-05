package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;

public class GameOverDialog extends JDialog {

    // --- INTERFACE & LISTENERS ---
    public interface DialogListener {
        void onDialogClosed();
    }
    private DialogListener listener;
    public void addDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    // --- THÀNH PHẦN GIAO DIỆN ---
    private Font gameFont;
    private final String playerName;
    private final Main main;

    public GameOverDialog(JFrame parent, String playerName, Main main) {
        super(parent, "Game Over", true);
        this.playerName = playerName;
        this.main = main;

        // 1. Cấu hình cửa sổ (Window Setup)
        setupWindow(parent);

        // 2. Khởi tạo Font
        this.gameFont = loadGameFont(18f);

        // 3. Tạo Panel chính với hiệu ứng vẽ tùy chỉnh
        JPanel mainPanel = createMainPanel();
        setContentPane(mainPanel);

        // 4. Thêm các thành phần nội dung
        addLabels(mainPanel);
        addButtons(mainPanel);

        // 5. Thiết lập phím tắt (Hotkeys)
        setupHotkeys(mainPanel);
    }

    // =========================================================================
    // 1. CẤU HÌNH CỬA SỔ & PANEL CHÍNH
    // =========================================================================

    private void setupWindow(JFrame parent) {
        setSize(420, 260);
        setLocationRelativeTo(parent);
        setUndecorated(true); // Xóa thanh tiêu đề mặc định của Windows
        setBackground(new Color(0, 0, 0, 0)); // Cho phép bo góc trong suốt
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền Gradient (Đỏ sẫm đến Đen sẫm)
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(90, 30, 30),
                        0, getHeight(), new Color(40, 10, 10)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Vẽ viền (Border) màu hồng nhạt
                g2.setColor(new Color(255, 170, 170));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(null); // Dùng Absolute Layout để dễ chỉnh tọa độ thủ công
        return panel;
    }

    // =========================================================================
    // 2. THÀNH PHẦN VĂN BẢN (LABELS)
    // =========================================================================

    private void addLabels(JPanel panel) {
        // Tiêu đề chính
        JLabel title = new JLabel("💀 GAME OVER 💀", SwingConstants.CENTER);
        title.setFont(gameFont.deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(255, 230, 230));
        title.setBounds(0, 20, 420, 40);
        panel.add(title);

        // Dòng 1: Thông báo thất bại (Màu vàng rực rỡ)
        JLabel failTitle = new JLabel("NHIỆM VỤ LẤY TRỨNG THẤT BẠI!", SwingConstants.CENTER);
        failTitle.setFont(new Font("Arial", Font.BOLD, 22));
        failTitle.setForeground(new Color(255, 255, 0));
        failTitle.setBounds(0, 80, 420, 30);
        panel.add(failTitle);

        // Dòng 2: Chi tiết (Màu trắng bạc)
        JLabel failDetail = new JLabel(playerName + " đã bị gà mổ vào đầu!", SwingConstants.CENTER);
        failDetail.setFont(new Font("Arial", Font.ITALIC, 16));
        failDetail.setForeground(Color.WHITE);
        failDetail.setBounds(0, 130, 420, 30);
        panel.add(failDetail);
    }

    // =========================================================================
    // 3. NÚT BẤM & SỰ KIỆN (BUTTONS & ACTIONS)
    // =========================================================================

    private void addButtons(JPanel panel) {
        // Nút Replay
        JButton replayBtn = createButton("REPLAY");
        replayBtn.setBounds(85, 185, 110, 40);
        replayBtn.addActionListener(e -> {
            dispose();
            main.startGame(playerName);
            if (listener != null) listener.onDialogClosed();
        });
        panel.add(replayBtn);

        // Nút Menu
        JButton menuBtn = createButton("MENU");
        menuBtn.setBounds(225, 185, 110, 40);
        menuBtn.addActionListener(e -> {
            dispose();
            main.showMenu();
            if (listener != null) listener.onDialogClosed();
        });
        panel.add(menuBtn);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(gameFont.deriveFont(Font.BOLD, 16f));
        button.setBackground(new Color(230, 150, 150));
        button.setForeground(new Color(70, 20, 20));
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 40, 40), 3));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Hiệu ứng bàn tay khi di chuột vào
        return button;
    }

    private void setupHotkeys(JPanel panel) {
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");
        panel.getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    // =========================================================================
    // 4. TIỆN ÍCH (UTILITIES)
    // =========================================================================

    private Font loadGameFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");
            if (is == null) throw new RuntimeException("Font not found");
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }

    public void showDialog() {
        setVisible(true);
    }
}