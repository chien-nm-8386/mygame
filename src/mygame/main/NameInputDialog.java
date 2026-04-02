package mygame.main;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;

public class NameInputDialog extends JDialog {

    private String playerName = null;
    private JTextField nameField;
    private JLabel errorLabel;
    private Font gameFont;

    public NameInputDialog(JFrame parent) {
        super(parent, "Nhap ten nhan vat", true);

        loadFont();

        setSize(420, 260);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);
        
        // Cho phép các góc bo tròn hiển thị mượt mà trên nền game
        setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền gỗ hộp thoại
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(110, 70, 35),
                        0, getHeight(), new Color(60, 35, 15)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Viền vàng bao quanh hộp thoại
                g2.setColor(new Color(255, 220, 150, 200));
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 30, 30);
                g2.dispose();
            }
        };

        panel.setLayout(null);
        panel.setOpaque(false);
        setContentPane(panel);

        JLabel titleLabel = new JLabel("NHAP TEN NHAN VAT", SwingConstants.CENTER);
        titleLabel.setFont(gameFont.deriveFont(28f));
        titleLabel.setForeground(new Color(255, 245, 200));
        titleLabel.setBounds(40, 25, 340, 40);
        panel.add(titleLabel);

        // --- Ô NHẬP TÊN (NAME FIELD) - FIX LỖI CARET NULL ---
        nameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Tự vẽ nền màu kem bo góc
                g2.setColor(new Color(255, 248, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // 2. Tự vẽ viền nâu gỗ
                g2.setColor(new Color(140, 95, 45));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                g2.dispose();

                // 3. Để Java vẽ chữ và Caret (con trỏ) lên trên
                super.paintComponent(g);
            }

            @Override
            public void setBorder(javax.swing.border.Border border) {
                // Chặn không cho hệ thống tự ý nạp lại viền trắng mặc định
            }
        };

        // KHÔNG dùng setUI(null) để tránh NullPointerException
        nameField.setOpaque(false); 
        // Dùng EmptyBorder để tạo khoảng trống cho chữ không dính sát viền
        nameField.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15)); 
        
        nameField.setBounds(75, 95, 270, 45);
        nameField.setFont(gameFont.deriveFont(24f));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setForeground(new Color(70, 40, 15));
        nameField.setCaretColor(new Color(70, 40, 15)); // Màu con trỏ nhấp nháy
        panel.add(nameField);

        // Label hiển thị lỗi
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(gameFont.deriveFont(16f));
        errorLabel.setForeground(new Color(255, 120, 120));
        errorLabel.setBounds(60, 142, 300, 25);
        errorLabel.setVisible(false);
        panel.add(errorLabel);

        // Nút bấm
        JButton okButton = createStyledButton("OK", 85, 180);
        JButton cancelButton = createStyledButton("HUY", 225, 180);
        panel.add(okButton);
        panel.add(cancelButton);

        // Xử lý sự kiện
        okButton.addActionListener(e -> confirmName());
        cancelButton.addActionListener(e -> { playerName = null; dispose(); });
        nameField.addActionListener(e -> confirmName());

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { errorLabel.setVisible(false); }
            public void removeUpdate(DocumentEvent e) { errorLabel.setVisible(false); }
            public void changedUpdate(DocumentEvent e) { errorLabel.setVisible(false); }
        });

        // ESC để đóng dialog
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "closeDialog");
        panel.getActionMap().put("closeDialog", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { playerName = null; dispose(); }
        });
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/fonts/ThaleahFat.ttf");
            gameFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            gameFont = new Font("Arial", Font.BOLD, 22);
        }
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean pressed = getModel().isPressed();
                boolean hovered = getModel().isRollover();
                int offset = pressed ? 3 : 0;

                // Nền nút bấm gỗ
                Color c1 = hovered ? new Color(255, 215, 110) : new Color(220, 165, 80);
                Color c2 = hovered ? new Color(210, 135, 40) : new Color(150, 95, 35);
                g2.setPaint(new GradientPaint(0, 0, c1, 0, getHeight(), c2));
                g2.fillRoundRect(0, offset, getWidth(), getHeight() - offset, 12, 12);

                // Viền nút bấm
                g2.setColor(new Color(75, 40, 10));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, offset, getWidth() - 1, getHeight() - offset - 1, 12, 12);

                // Chữ
                g2.setFont(gameFont.deriveFont(24f));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + offset;
                
                g2.setColor(new Color(50, 25, 5));
                g2.drawString(getText(), tx + 1, ty + 1); // Đổ bóng chữ
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        button.setBounds(x, y, 110, 45);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void confirmName() {
        String input = nameField.getText().trim();
        if (input.isEmpty()) {
            errorLabel.setText("VUI LONG NHAP TEN!");
            errorLabel.setVisible(true);
            nameField.requestFocusInWindow();
            return;
        }
        playerName = input;
        dispose();
    }

    public String showDialog() {
        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());
        setVisible(true);
        return playerName;
    }
}