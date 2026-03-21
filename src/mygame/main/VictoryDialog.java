package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.InputStream;

public class VictoryDialog extends JDialog {

    private Font gameFont;

    public VictoryDialog(JFrame parent, String playerName, Main main) {
        super(parent, "Victory", true);

        setSize(420, 260);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // remove white border

        gameFont = loadGameFont(18f);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // background
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(85, 55, 25),
                        0, getHeight(), new Color(45, 28, 12)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // border
                g2.setColor(new Color(255, 220, 150));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(null);
        setContentPane(panel);

        // ===== TITLE =====
        JLabel title = new JLabel("🎉 VICTORY 🎉", SwingConstants.CENTER);
        title.setFont(gameFont.deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(255, 245, 220));
        title.setBounds(40, 20, 340, 40);
        panel.add(title);

        // ===== PLAYER NAME =====
        JLabel nameLabel = new JLabel("Player: " + playerName, SwingConstants.CENTER);
        nameLabel.setFont(gameFont.deriveFont(Font.PLAIN, 18f));
        nameLabel.setForeground(new Color(255, 230, 180));
        nameLabel.setBounds(40, 70, 340, 30);
        panel.add(nameLabel);

        // ===== BUTTONS =====
        JButton replayBtn = createButton("REPLAY");
        replayBtn.setBounds(70, 150, 120, 40);
        panel.add(replayBtn);

        JButton menuBtn = createButton("MENU");
        menuBtn.setBounds(230, 150, 120, 40);
        panel.add(menuBtn);

        // Actions
        replayBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
            main.startGame(playerName);
            if (main.gamePanel != null) {
                main.gamePanel.requestFocusInWindow();
            }
        });
    });

        menuBtn.addActionListener(e -> {
            dispose();
            main.showMenu();
        });

        // ESC key
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
        button.setBackground(new Color(240, 190, 105));
        button.setForeground(new Color(70, 40, 15));
        button.setBorder(BorderFactory.createLineBorder(new Color(110, 70, 30), 3));
        button.setFocusPainted(false);
        return button;
    }

    public void showDialog() {
        setVisible(true);
    }
}