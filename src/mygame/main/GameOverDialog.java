package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;

public class GameOverDialog extends JDialog {
     public interface DialogListener {
        void onDialogClosed();
    }

    private DialogListener listener;

    public void addDialogListener(DialogListener listener) {
        this.listener = listener;
    }
    private Font gameFont;

    public GameOverDialog(JFrame parent, String playerName, Main main) {
        super(parent, "Game Over", true);

        setSize(420, 260);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        gameFont = loadGameFont(18f);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(90, 30, 30),
                        0, getHeight(), new Color(40, 10, 10)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(new Color(255, 170, 170));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(null);
        setContentPane(panel);

        JLabel title = new JLabel("💀 GAME OVER 💀", SwingConstants.CENTER);
        title.setFont(gameFont.deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(255, 230, 230));
        title.setBounds(40, 20, 340, 40);
        panel.add(title);
        // SỬA
//        JLabel nameLabel = new JLabel("Player: " + playerName, SwingConstants.CENTER);
//        nameLabel.setFont(gameFont.deriveFont(Font.PLAIN, 18f));
//        nameLabel.setForeground(new Color(255, 210, 210));
//        nameLabel.setBounds(40, 70, 340, 30);
//        panel.add(nameLabel);
        
        // --- DÒNG 1: THÔNG BÁO NHIỆM VỤ (In đậm, màu Vàng rực rỡ) ---
        JLabel failTitle = new JLabel("NHIỆM VỤ LẤY TRỨNG THẤT BẠI!", SwingConstants.CENTER);
        failTitle.setFont(new Font("Arial", Font.BOLD, 22)); 
        // Gợi ý màu: Vàng sáng (255, 255, 0) để nổi bật nhất trên nền đỏ sẫm
        failTitle.setForeground(new Color(255, 255, 0)); 
        failTitle.setBounds(0, 80, 420, 30); // Căn giữa toàn bộ chiều rộng (420)
        panel.add(failTitle);

        // --- DÒNG 2: THÔNG BÁO CHI TIẾT (Màu trắng bạc, tạo khoảng cách xa hơn) ---
        JLabel failDetail = new JLabel(playerName + " đã bị gà mổ vào đầu!", SwingConstants.CENTER);
        failDetail.setFont(new Font("Arial", Font.ITALIC, 16)); // Chữ nghiêng cho nghệ thuật
        failDetail.setForeground(new Color(255, 255, 255)); // Màu đỏ hồng mờ ảo
        failDetail.setBounds(0, 130, 420, 30); 
        panel.add(failDetail);

        // --- KHỞI TẠO NÚT BẤM (Sửa lỗi "variable replayBtn" trong ảnh của bạn) ---
        JButton replayBtn = createButton("REPLAY");
        replayBtn.setBounds(85, 175, 110, 40); // Đẩy nút xuống Y=175 để không đè chữ
        panel.add(replayBtn);

        JButton menuBtn = createButton("MENU");
        menuBtn.setBounds(225, 175, 110, 40);
        panel.add(menuBtn);

        // --- GÁN SỰ KIỆN CHO NÚT ---
        replayBtn.addActionListener(e -> {
            dispose();
            main.startGame(playerName);
            if (listener != null) listener.onDialogClosed();
        });

        menuBtn.addActionListener(e -> {
            dispose();
            main.showMenu();
            if (listener != null) listener.onDialogClosed();
        });

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");

        panel.getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private Font loadGameFont(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");
            if (is == null) throw new RuntimeException("Font not found");

            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(gameFont.deriveFont(Font.BOLD, 16f));
        button.setBackground(new Color(230, 150, 150));
        button.setForeground(new Color(70, 20, 20));
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 40, 40), 3));
        button.setFocusPainted(false);
        return button;
    }

    public void showDialog() {
        setVisible(true);
    }
}