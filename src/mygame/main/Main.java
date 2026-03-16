package mygame.main;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Image;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Golden Egg"); // Tên tiêu đề cửa sổ

        // Tạo viền màu đen (bạn có thể thay đổi độ dày bằng cách thay đổi giá trị số)
        window.getRootPane().setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

        try {
            ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/res/tiles/logo.png")));
            Image logoImage = logoIcon.getImage();
            window.setIconImage(logoImage);
        } catch (Exception e) {
            System.err.println("Không thể tải logo game. Kiểm tra lại đường dẫn ảnh.");
            e.printStackTrace();
        }

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack(); // Tự động co giãn cửa sổ vừa khít với GamePanel
        window.setLocationRelativeTo(null); // Hiển thị cửa sổ ở chính giữa màn hình
        window.setVisible(true);
        gamePanel.startGameThread();
    }
}