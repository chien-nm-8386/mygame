
package mygame.main;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Main {

    private static final int WINDOWED_WIDTH = 1024;
    private static final int WINDOWED_HEIGHT = 768;

    JFrame window;
    CardLayout cardLayout;
    JPanel container;

    GamePanel gamePanel;
    MenuPanel menuPanel;

    private boolean fullScreen = false;
    private GraphicsDevice graphicsDevice;
    private Rectangle windowedBounds;

    public Main() {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Golden Egg");
        window.getRootPane().setBorder(null);

        graphicsDevice = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

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

        Dimension contentSize = new Dimension(WINDOWED_WIDTH, WINDOWED_HEIGHT);
        container.setPreferredSize(contentSize);

        container.add(menuPanel, "menu");
        container.add(gamePanel, "game");

        window.setContentPane(container);
        applyWindowedSize();
        window.setVisible(true);

        windowedBounds = window.getBounds();

        showMenu();
    }

    private void applyWindowedSize() {
        Dimension contentSize = new Dimension(WINDOWED_WIDTH, WINDOWED_HEIGHT);

        container.setPreferredSize(contentSize);

        window.setContentPane(container);
        window.pack();
        window.setLocationRelativeTo(null);

        container.revalidate();
        container.repaint();
        window.revalidate();
        window.repaint();

        SwingUtilities.invokeLater(() -> {
            System.out.println("window = " + window.getWidth() + " x " + window.getHeight());
            System.out.println("contentPane = " + window.getContentPane().getWidth() + " x " + window.getContentPane().getHeight());
            System.out.println("container = " + container.getWidth() + " x " + container.getHeight());
            System.out.println("menuPanel = " + menuPanel.getWidth() + " x " + menuPanel.getHeight());
        });
    }

    public void showMenu() {
        cardLayout.show(container, "menu");
        container.revalidate();
        container.repaint();

        if (gamePanel != null) {
            gamePanel.stopAllSounds();
            try {
                gamePanel.stopGameplayMusic();
            } catch (Exception e) {
                // bỏ qua nếu chưa có gameplayMusic
            }
        }

        if (menuPanel != null) {
            menuPanel.playMenuMusic();
            menuPanel.requestFocusInWindow();
        }
    }

    public void showGame() {
        cardLayout.show(container, "game");
        container.revalidate();
        container.repaint();

        if (menuPanel != null) {
            menuPanel.stopMenuMusic();
        }

        if (gamePanel != null) {
            gamePanel.requestFocusInWindow();
        }
    }

    public void startGame(String playerName) {
        if (menuPanel != null) {
            menuPanel.stopMenuMusic();
        }

        gamePanel.setPlayerName(playerName);
        gamePanel.startNewGame();

        cardLayout.show(container, "game");
        container.revalidate();
        container.repaint();
        gamePanel.requestFocusInWindow();
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void toggleFullScreen() {
        try {
            boolean wasShowingGame = gamePanel != null && gamePanel.isShowing();

            window.dispose();

            if (!fullScreen) {
                windowedBounds = window.getBounds();

                window.setUndecorated(true);
                window.setResizable(false);
                window.getRootPane().setBorder(null);
                window.setContentPane(container);

                if (graphicsDevice.isFullScreenSupported()) {
                    graphicsDevice.setFullScreenWindow(window);
                } else {
                    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                    window.setSize(screen);
                    window.setLocation(0, 0);
                    window.setVisible(true);
                }

                fullScreen = true;

            } else {
                if (graphicsDevice.getFullScreenWindow() == window) {
                    graphicsDevice.setFullScreenWindow(null);
                }

                window.setUndecorated(false);
                window.setResizable(false);
                window.setExtendedState(JFrame.NORMAL);
                window.getRootPane().setBorder(null);
                window.setContentPane(container);

                applyWindowedSize();
                window.setVisible(true);

                if (windowedBounds != null) {
                    window.setBounds(windowedBounds);
                }

                fullScreen = false;
            }

            container.revalidate();
            container.repaint();

            if (menuPanel != null) {
                menuPanel.revalidate();
                menuPanel.repaint();
            }

            if (gamePanel != null) {
                gamePanel.revalidate();
                gamePanel.repaint();
            }

            window.revalidate();
            window.repaint();

            SwingUtilities.invokeLater(() -> {
                if (wasShowingGame) {
                    if (gamePanel != null) {
                        gamePanel.requestFocusInWindow();
                    }
                } else {
                    if (menuPanel != null) {
                        menuPanel.requestFocusInWindow();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
