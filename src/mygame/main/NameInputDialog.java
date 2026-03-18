package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NameInputDialog extends JDialog {

    private String playerName = null;
    private JTextField nameField;

    public NameInputDialog(JFrame parent) {
        super(parent, "Nhập tên nhân vật", true);

        setSize(420, 240);
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

                g2.setColor(new Color(255, 230, 170, 80));
                g2.fillRoundRect(12, 12, getWidth() - 24, 45, 20, 20);

                g2.setColor(new Color(255, 220, 150));
                g2.setStroke(new BasicStroke(4f));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 30, 30);

                g2.dispose();
            }
        };

        panel.setLayout(null);
        panel.setOpaque(false);
        setContentPane(panel);

        JLabel titleLabel = new JLabel("NHẬP TÊN NHÂN VẬT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(255, 245, 220));
        titleLabel.setBounds(40, 22, 340, 30);
        panel.add(titleLabel);

        JLabel subLabel = new JLabel("Hãy đặt tên cho người chơi của bạn", SwingConstants.CENTER);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subLabel.setForeground(new Color(255, 235, 200));
        subLabel.setBounds(50, 62, 320, 22);
        panel.add(subLabel);

        nameField = new JTextField();
        nameField.setBounds(75, 105, 270, 40);
        nameField.setFont(new Font("Arial", Font.PLAIN, 20));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setBackground(new Color(255, 248, 235));
        nameField.setForeground(new Color(70, 40, 15));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(140, 95, 45), 3));
        panel.add(nameField);

        JButton okButton = createButton("OK");
        okButton.setBounds(85, 165, 110, 42);
        panel.add(okButton);

        JButton cancelButton = createButton("HỦY");
        cancelButton.setBounds(225, 165, 110, 42);
        panel.add(cancelButton);

        okButton.addActionListener(e -> confirmName());
        cancelButton.addActionListener(e -> {
            playerName = null;
            dispose();
        });

        nameField.addActionListener(e -> confirmName());

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("ESCAPE"), "closeDialog"
        );
        panel.getActionMap().put("closeDialog", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = null;
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

    private void confirmName() {
        String input = nameField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng nhập tên nhân vật!",
                    "Thiếu tên",
                    JOptionPane.WARNING_MESSAGE
            );
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