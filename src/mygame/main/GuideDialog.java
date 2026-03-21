package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GuideDialog extends JDialog {

    public GuideDialog(JFrame parent) {
        super(parent, "Hướng dẫn", true);

        setSize(450, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(85, 55, 25),
                        0, getHeight(), new Color(45, 28, 12)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                g2.setColor(new Color(255, 220, 150));
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                g2.dispose();
            }
        };

        panel.setLayout(null);
        panel.setOpaque(false);
        setContentPane(panel);

        // ===== TITLE =====
        JLabel title = new JLabel("HƯỚNG DẪN CHƠI", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(255, 245, 220));
        title.setBounds(50, 20, 350, 30);
        panel.add(title);

        // ===== NỘI DUNG =====
       JTextArea guideText = new JTextArea(
        "- Dùng W A S D hoặc phím mũi tên để di chuyển\n" +
        "- Tìm đường ra khỏi mê cung\n" +
        "- Nhấn ESC để quay lại menu"
);

            guideText.setFont(new Font("Arial", Font.PLAIN, 18));
            guideText.setForeground(new Color(255, 240, 200));
            guideText.setOpaque(false);
            guideText.setEditable(false);
            guideText.setLineWrap(true);
            guideText.setWrapStyleWord(true);
            guideText.setFocusable(false);
            guideText.setBorder(null);
            guideText.setBounds(90, 80, 280, 110);
            panel.add(guideText);

        // ===== BUTTON =====
        JButton okButton = createButton("OK");
        okButton.setBounds(160, 200, 120, 40);
        panel.add(okButton);

        okButton.addActionListener(e -> dispose());

        // ESC để đóng
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");

        panel.getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(new Color(70, 40, 15));
        button.setBackground(new Color(240, 190, 105));
        button.setBorder(BorderFactory.createLineBorder(new Color(110, 70, 30), 3));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void showDialog() {
        setVisible(true);
    }
}