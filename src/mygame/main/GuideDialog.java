package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JTextPane;

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
        JTextPane guideText = new JTextPane();
        guideText.setContentType("text/html"); // Thiết lập chế độ đọc mã HTML
        guideText.setText(
            "<html><body style='font-family:Arial; font-size:16pt; color:#FFF0C8;'>" +
           "1. DI CHUYỂN: Dùng " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;W&nbsp;</b></span> " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;A&nbsp;</b></span> " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;S&nbsp;</b></span> " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;D&nbsp;</b></span> hoặc " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;🡹&nbsp;</b></span> " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;🡸&nbsp;</b></span> " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;🡻&nbsp;</b></span> " +
            "<span style='background-color:#444; color:white;'><b>&nbsp;🡺&nbsp;</b></span>.<br><br>" +
            "2. MỤC TIÊU: Tìm và nhặt <b>TRỨNG VÀNG</b> ở góc mê cung.<br><br>" +
            "3. CẢNH BÁO: Lũ gà sẽ <b>THỨC TỈNH</b> và <b>ĐUỔI THEO</b> bạn ngay khi bạn nhặt được trứng!<br><br>" +
            "4. CHIẾN ĐẤU: Sau khi có trứng, hãy tìm <b>vŨ kHÍ</b> để tự vệ (Nhấn SPACE để tấn công).<br><br>" +
            "5. HỒI SINH: Hãy cẩn thận! Lũ gà sẽ <b>HỒI SINH</b> sau 5 giây tại vị trí cũ.<br><br>" +
            "6. Mẹo: Tiêu diệt 1 con gà sẽ hồi 10 máu.<br><br>" +
            "7. Chức năng: Nhấn ESC để tạm dừng và chỉnh sửa âm thanh.<br><br>" +
            "8. CHIẾN THẮNG: Mang trứng về <b>NHÀ</b> an toàn để thắng cuộc." +
            "</body></html>"
        );

           // Thiết lập thuộc tính cho TextArea
        guideText.setOpaque(false);
        guideText.setEditable(false);
//        guideText.setLineWrap(true);
//        guideText.setWrapStyleWord(true);
       // THÊM KHOẢNG TRỐNG bên phải để chữ không chạm thanh cuộn
        guideText.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
        // 2. Tạo JScrollPane (Thanh cuộn) và bỏ TextArea vào trong
        JScrollPane scrollPane = new JScrollPane(guideText);
        scrollPane.setBounds(50, 65, 350, 120); // Đặt vị trí và kích thước cho khung cuộn
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); // Làm nền khung cuộn trong suốt
        scrollPane.setBorder(null); // Xóa viền mặc định của ScrollPane
        // 3. Tùy chỉnh thanh cuộn (Scrollbar) cho đẹp hơn (tùy chọn)
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt
        // Ẩn các nút mũi tên lên xuống để nhìn tối giản hơn (tùy chọn)
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(240, 190, 105, 150); // Màu thanh kéo khớp với nút OK
                this.trackColor = new Color(0, 0, 0, 0); // Nền thanh cuộn trong suốt
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

            private JButton createZeroButton() {
                JButton jbutton = new JButton();
                jbutton.setPreferredSize(new Dimension(0, 0));
                return jbutton;
            }
        });
        panel.add(scrollPane);
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