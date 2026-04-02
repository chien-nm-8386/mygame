package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {

    JFrame window;
    CardLayout cardLayout;
    JPanel container;

    GamePanel gamePanel;
    MenuPanel menuPanel;

    public Main() {

        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Golden Egg");

        window.getRootPane().setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));

        try {
            ImageIcon logoIcon = new ImageIcon(
                    Objects.requireNonNull(getClass().getResource("/res/tiles/logo.png"))
            );
            window.setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.err.println("Không thể tải logo");
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        gamePanel = new GamePanel(this);
        menuPanel = new MenuPanel(this);

        container.add(menuPanel, "menu");
        container.add(gamePanel, "game");

        window.add(container);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        showMenu();
    }

    public void showMenu() {
        // Chỉ chuyển về menu để giữ lại progress đang chơi dở.
        // KHÔNG reset game và KHÔNG dừng game thread ở đây.
        cardLayout.show(container, "menu");
        menuPanel.requestFocusInWindow();
        menuPanel.playMenuMusic();
    }

    public void showGame() {
        cardLayout.show(container, "game");
        gamePanel.requestFocusInWindow();
    }

    public void startGame(String playerName) {
        gamePanel.setPlayerName(playerName);
        gamePanel.startNewGame();
        cardLayout.show(container, "game");
        gamePanel.requestFocusInWindow();
        gamePanel.startGameThread();
    }

    public static void main(String[] args) {
        new Main();
    }
}