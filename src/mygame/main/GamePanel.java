package mygame.main;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import mygame.tile.TileManager;
import mygame.entity.Player;
import mygame.entity.Chicken;
import mygame.tile.CollisionChecker;
import mygame.ai.PathFinder;

public class GamePanel extends JPanel implements Runnable {

    public Main main;
    private boolean hasSavedProgress = false;
    private int eggEffectTick = 0;

    // ===== GAME STATE =====
    public final int STATE_PLAY = 0;
    public final int STATE_LEVEL_COMPLETE = 1;
    public final int STATE_LEVEL2_PLAY = 2;
    public final int STATE_GAME_WIN = 3;
    public final int STATE_PAUSE = 4;
    public final int STATE_GAME_COMPLETED = 5;
    public final int STATE_GAME_OVER = 6;
    public final int STATE_LEVEL_TRANSITION = 7;

    public int gameState = STATE_PLAY;

    // SCREEN SETTINGS
    public final int originalTileSize = 16;
    public int scale = 4;
    public int tileSize = originalTileSize * scale;

    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public int screenWidth = tileSize * maxScreenCol;
    public int screenHeight = tileSize * maxScreenRow;

    // WORLD
    public final int maxWorldCol = 16;
    public final int maxWorldRow = 12;

    int FPS = 60;

    // SYSTEM
    public TileManager tileM = new TileManager(this);

    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public UI ui;
    public ConfettiManager confettiM = new ConfettiManager(this);
    public PathFinder pFinder = new PathFinder(this);
    public MouseHandler mouseH = new MouseHandler(this);

    public Sound gameOverMusic = new Sound();
    public Sound victoryMusic = new Sound();
    public Sound eggSound = new Sound();
    public Sound weaponSound = new Sound();
    public Sound slashSound = new Sound();
    public Sound tensionMusic = new Sound();
    public Sound gameplayMusic = new Sound();
    
    // Biến âm thanh cho gà chết
    public Sound deathSound = new Sound();
    // Biến âm thanh khi người chơi bị đánh
    public Sound hurtSound = new Sound();

    public int gameMusicVolume = 70;
    private int lastMusicVolume = 70;
    public boolean gameMusicMuted = false;

    public int footstepVolume = 80;
    private int lastFootstepVolume = 80;
    public boolean footstepMuted = false;

    // Tránh stop/play nhạc liên tục mỗi frame
    private boolean isTensionPlaying = false;

    public int currentLevel = 1;
    Thread gameThread;

    // ENTITIES
    public String playerName = "Player";
    public Player player;
    public ArrayList<Chicken> chickens = new ArrayList<>();

    // LOGIC NHẶT NHIỀU TRỨNG
    public int eggsCollected = 0;

    public GamePanel(Main main) {
        this.main = main;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseH);

        player = new Player(this, keyH);
        ui = new UI(this);
        confettiM = new ConfettiManager(this);

        setupGame();
    }

    public void setWindowSize(int newScale) {
        this.scale = newScale;
        this.tileSize = originalTileSize * scale;
        this.screenWidth = tileSize * maxScreenCol;
        this.screenHeight = tileSize * maxScreenRow;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.pack();
            frame.setLocationRelativeTo(null);
        }

        System.out.println("Scale hien tai: " + scale + " | TileSize: " + tileSize);
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        if (player != null) {
            player.name = playerName;
        }
    }

    public boolean hasSavedProgress() {
        return hasSavedProgress;
    }

    public void saveProgressAndBackToMenu() {
        hasSavedProgress = true;
        gameState = STATE_PAUSE;
        keyH.escapePressed = false;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        stopAllSounds();
        confettiM.stop();
        main.showMenu();
    }

    public void resumeSavedGame() {
        hasSavedProgress = false;
        keyH.escapePressed = false;
        mouseH.resetClick();
        gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
        refreshAudioSettings();
        requestFocusInWindow();
    }

    public void clearSavedProgress() {
        hasSavedProgress = false;
    }

    // ===== SETTINGS LINK =====
    private MenuPanel getMenuPanel() {
        return (main != null) ? main.menuPanel : null;
    }

    public int getMusicVolume() {
        MenuPanel mp = getMenuPanel();
        return (mp != null) ? mp.getMusicVolume() : 70;
    }

    public int getSfxVolume() {
        MenuPanel mp = getMenuPanel();
        return (mp != null) ? mp.getSfxVolume() : 80;
    }

    public boolean isMusicMuted() {
        MenuPanel mp = getMenuPanel();
        return mp != null && mp.isMusicMuted();
    }

    public boolean isSfxMuted() {
        MenuPanel mp = getMenuPanel();
        return mp != null && mp.isSfxMuted();
    }

    public int getGameMusicVolume() {
        return gameMusicVolume;
    }

    public boolean isGameMusicMuted() {
        return gameMusicMuted;
    }

    public int getFootstepVolume() {
        return footstepVolume;
    }

    public boolean isFootstepMuted() {
        return footstepMuted;
    }

    public void setGameMusicVolume(int volume) {
        gameMusicVolume = Math.max(0, Math.min(100, volume));

        if (gameMusicVolume == 0) {
            gameMusicMuted = true;
        } else {
            gameMusicMuted = false;
        }

        refreshPauseAudio();
    }

    public void toggleGameMusicMute() {
        gameMusicMuted = !gameMusicMuted;

        if (gameMusicMuted) {
            lastMusicVolume = gameMusicVolume;
            gameMusicVolume = 0;

            if (gameplayMusic != null) gameplayMusic.setVolume(0);
            if (tensionMusic != null) tensionMusic.setVolume(0);
            if (victoryMusic != null) victoryMusic.setVolume(0);
            if (gameOverMusic != null) gameOverMusic.setVolume(0);
        } else {
            gameMusicVolume = (lastMusicVolume <= 0) ? 70 : lastMusicVolume;
            refreshPauseAudio();
        }
    }

    public void setFootstepVolume(int volume) {
        this.footstepVolume = Math.max(0, Math.min(100, volume));
        if (footstepVolume > 0) {
            footstepMuted = false;
        }
        if (player != null) {
            player.refreshFootstepVolume();
        }
    }

    public void toggleFootstepMute() {
        footstepMuted = !footstepMuted;

        if (footstepMuted) {
            lastFootstepVolume = footstepVolume;
            footstepVolume = 0;

            if (player != null) {
                player.refreshFootstepVolume();
                player.stopFootstepSound();
            }
        } else {
            footstepVolume = (lastFootstepVolume <= 0) ? 80 : lastFootstepVolume;

            if (player != null) {
                player.refreshFootstepVolume();
            }
        }
    }

    public void refreshPauseAudio() {
        int gameVol = gameMusicMuted ? 0 : gameMusicVolume;

        if (gameplayMusic != null && gameplayMusic.isLoaded()) {
            gameplayMusic.setVolume(gameVol);
        }

        if (tensionMusic != null && tensionMusic.isLoaded()) {
            tensionMusic.setVolume(gameVol);
        }

        if (victoryMusic != null && victoryMusic.isLoaded()) {
            victoryMusic.setVolume(gameVol);
        }

        if (gameOverMusic != null && gameOverMusic.isLoaded()) {
            gameOverMusic.setVolume(gameVol);
        }

        if (player != null) {
            player.refreshFootstepVolume();
        }
    }

    public void refreshAudioSettings() {
        int gameVol = gameMusicMuted ? 0 : gameMusicVolume;
        int sfxVol = isSfxMuted() ? 0 : getSfxVolume();

        if (victoryMusic != null && victoryMusic.isLoaded()) {
            victoryMusic.setVolume(gameVol);
        }

        if (gameOverMusic != null && gameOverMusic.isLoaded()) {
            gameOverMusic.setVolume(gameVol);
        }

        if (tensionMusic != null && tensionMusic.isLoaded()) {
            tensionMusic.setVolume(gameVol);
        }

        if (gameplayMusic != null && gameplayMusic.isLoaded()) {
            gameplayMusic.setVolume(gameVol);
        }

        if (eggSound != null && eggSound.isLoaded()) {
            eggSound.setVolume(sfxVol);
        }

        if (weaponSound != null && weaponSound.isLoaded()) {
            weaponSound.setVolume(sfxVol);
        }

        if (slashSound != null && slashSound.isLoaded()) {
            slashSound.setVolume(sfxVol);
        }

        if (deathSound != null && deathSound.isLoaded()) {
            deathSound.setVolume(sfxVol);
        }

        if (hurtSound != null && hurtSound.isLoaded()) {
            hurtSound.setVolume(sfxVol);
        }

        if (player != null) {
            player.refreshFootstepVolume();
        }
    }

    public void setupGame() {
        gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
        hasSavedProgress = false;
        eggsCollected = 0;
        eggEffectTick = 0;
        isTensionPlaying = false;

        stopAllSounds();
        confettiM.stop();
        tileM.resetMapObjects();

        if (player != null) {
            player.setDefaultValues();
            player.name = this.playerName;
            player.hasEgg = false;

            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        chickens.clear();
        spawnInitialChickens();

        // Nạp sẵn các file âm thanh vào bộ nhớ
        eggSound.setFile("/res/audio/egg.wav");
        eggSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());

        weaponSound.setFile("/res/audio/weapon.wav");
        weaponSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());

        slashSound.setFile("/res/audio/slash.wav");
        slashSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());

        deathSound.setFile("/res/audio/death.wav");
        deathSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());

        // Nạp file danh.wav cho hành động bị gà đánh
        hurtSound.setFile("/res/audio/danh.wav");
        hurtSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());

        tensionMusic.setFile("/res/audio/tension.wav");
        tensionMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);

        gameplayMusic.setFile("/res/audio/gameplay.wav");
        gameplayMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);

        victoryMusic.setFile("/res/audio/victory.wav");
        victoryMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);

        gameOverMusic.setFile("/res/audio/gameover.wav");
        gameOverMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);

        playGameplayMusic();
    }

    // Menu music do MenuPanel quản lý
    public void playMenuMusic() {
        if (main != null && main.menuPanel != null) {
            main.menuPanel.playMenuMusic();
        }
    }

    public void stopMenuMusic() {
        if (main != null && main.menuPanel != null) {
            main.menuPanel.stopMenuMusic();
        }
    }

    public void playVictoryMusic() {
        stopAllSounds();

        if (!victoryMusic.isLoaded()) {
            victoryMusic.setFile("/res/audio/victory.wav");
        }

        if (victoryMusic.isLoaded()) {
            victoryMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);
            victoryMusic.play();
            System.out.println("Da phat victory music");
        } else {
            System.out.println("Khong load duoc victory.wav");
        }
    }

    public void showLevel1WinScreen() {
        clearSavedProgress();
        gameState = STATE_LEVEL_COMPLETE;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        playVictoryMusic();
    }

    public void playGameOverMusic() {
        stopAllSounds();

        if (!gameOverMusic.isLoaded()) {
            gameOverMusic.setFile("/res/audio/gameover.wav");
        }

        if (gameOverMusic.isLoaded()) {
            gameOverMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);
            gameOverMusic.play();
            System.out.println("Da phat game over music");
        } else {
            System.out.println("Khong load duoc gameover.wav");
        }
    }

    public void playGameplayMusic() {
        if (!gameplayMusic.isLoaded()) {
            gameplayMusic.setFile("/res/audio/gameplay.wav");
        }
        gameplayMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);
        if (!gameplayMusic.isRunning()) {
            gameplayMusic.loop();
        }
    }

    public void stopGameplayMusic() {
        if (gameplayMusic != null) {
            gameplayMusic.stop();
        }
    }

    public void playEggSound() {
        if (!eggSound.isLoaded()) {
            eggSound.setFile("/res/audio/egg.wav");
        }

        eggSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());
        eggSound.play();
    }

    public void playWeaponSound() {
        if (!weaponSound.isLoaded()) {
            weaponSound.setFile("/res/audio/weapon.wav");
        }

        weaponSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());
        weaponSound.play();
    }

    public void playSlashSound() {
        if (!slashSound.isLoaded()) {
            slashSound.setFile("/res/audio/slash.wav");
        }

        slashSound.setVolume(isSfxMuted() ? 0 : getSfxVolume());
        slashSound.playOnceFromStart();
    }

    // Hàm phát âm thanh khi gà chết (Cho phép phát nhiều tiếng kêu chồng lên nhau qua đa luồng)
    public void playDeathSound() {
        if (isSfxMuted()) return;

        new Thread(() -> {
            try {
                Sound tempDeath = new Sound();
                tempDeath.setFile("/res/audio/death.wav");
                tempDeath.setVolume(getSfxVolume());
                tempDeath.play();
            } catch (Exception e) {
                System.err.println("Loi khi phat am thanh chet!");
            }
        }).start();
    }

    // Hàm phát âm thanh khi người chơi bị gà đánh
    public void playHurtSound() {
        if (isSfxMuted()) return;

        new Thread(() -> {
            try {
                // Tạo mới hoặc dừng và reset đối tượng cũ để phản hồi tức thì
                Sound tempHurt = new Sound();
                tempHurt.setFile("/res/audio/danh.wav");
                tempHurt.setVolume(getSfxVolume());
                tempHurt.play();
            } catch (Exception e) {
                System.err.println("Loi khi phat am thanh bi danh!");
            }
        }).start();
    }

    public void playTensionMusic() {
        if (!tensionMusic.isLoaded()) {
            tensionMusic.setFile("/res/audio/tension.wav");
        }

        tensionMusic.setVolume(gameMusicMuted ? 0 : gameMusicVolume);

        if (!tensionMusic.isRunning()) {
            tensionMusic.loop();
        }
    }

    public void stopTensionMusic() {
        if (tensionMusic != null) {
            tensionMusic.stop();
        }
    }

    private void spawnInitialChickens() {
        chickens.clear();

        switch (currentLevel) {
            case 1:
                chickens.add(new Chicken(this, 198, 128));
                chickens.add(new Chicken(this, 480, 64));
                chickens.add(new Chicken(this, 896, 192));
                chickens.add(new Chicken(this, 695, 185));
                chickens.add(new Chicken(this, 64, 304));
                chickens.add(new Chicken(this, 512, 384));
                chickens.add(new Chicken(this, 896, 384));
                chickens.add(new Chicken(this, 624, 544));
                chickens.add(new Chicken(this, 448, 608));
                chickens.add(new Chicken(this, 896, 640));
                chickens.add(new Chicken(this, 700, 635));
                chickens.add(new Chicken(this, 74, 60));
                chickens.add(new Chicken(this, 284, 360));
                chickens.add(new Chicken(this, 624, 635));
                chickens.add(new Chicken(this, 700, 500));
                break;

            case 2:
                chickens.add(new Chicken(this, 78, 185));
                chickens.add(new Chicken(this, 120, 430));
                chickens.add(new Chicken(this, 180, 189));
                chickens.add(new Chicken(this, 70, 347));
                chickens.add(new Chicken(this, 350, 118));
                chickens.add(new Chicken(this, 300, 189));
                chickens.add(new Chicken(this, 450, 347));
                chickens.add(new Chicken(this, 335, 335));
                chickens.add(new Chicken(this, 600, 47));
                chickens.add(new Chicken(this, 800, 47));
                chickens.add(new Chicken(this, 740, 117));
                chickens.add(new Chicken(this, 680, 185));
                chickens.add(new Chicken(this, 820, 340));
                chickens.add(new Chicken(this, 885, 100));
                chickens.add(new Chicken(this, 885, 370));
                chickens.add(new Chicken(this, 400, 420));
                chickens.add(new Chicken(this, 600, 360));
                chickens.add(new Chicken(this, 100, 630));
                chickens.add(new Chicken(this, 250, 640));
                chickens.add(new Chicken(this, 500, 610));
                break;
        }
    }

    public void startGameThread() {
        stopMenuMusic();

        if (gameThread != null) {
            gameThread = null;
        }

        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameThread() {
        gameThread = null;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        Thread currentThread = Thread.currentThread();

        while (gameThread == currentThread) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        confettiM.update();

        if (gameState == STATE_LEVEL_COMPLETE) {
            if (mouseH.clicked && ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                mouseH.resetClick();
                gameState = STATE_LEVEL_TRANSITION;
                confettiM.stop();
            }
            return;
        }

        if (gameState == STATE_LEVEL_TRANSITION) {
            if (mouseH.clicked) {
                if (ui.readyBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    startLevel2();
                } else if (ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    restartGame();
                }
            }
            return;
        }

        if (gameState == STATE_GAME_WIN || gameState == STATE_GAME_COMPLETED) {
            if (mouseH.clicked) {
                if (ui.nextLevelBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    ui.inputFinished = false;
                    ui.inputNumber = "";
                    restartGame();
                } else if (ui.backBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    clearSavedProgress();
                    stopAllSounds();
                    stopGameThread();
                    confettiM.stop();
                    ui.inputFinished = false;
                    ui.inputNumber = "";
                    main.showMenu();
                }
            }
            return;
        }

        if (gameState == STATE_GAME_OVER) {
            return;
        }

        if (keyH.escapePressed) {
            keyH.escapePressed = false;

            if (gameState == STATE_PLAY || gameState == STATE_LEVEL2_PLAY) {
                gameState = STATE_PAUSE;
                if (player != null) {
                    player.stopFootstepSound();
                }
                mouseH.resetClick();
                return;
            } else if (gameState == STATE_PAUSE) {
                hasSavedProgress = false;
                gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
                mouseH.resetClick();
                refreshAudioSettings();
                return;
            }
        }

        if (gameState == STATE_PAUSE) {
            if (mouseH.clicked) {
                if (ui.musicMinusBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    setGameMusicVolume(gameMusicVolume - 10);
                    return;
                }
                if (ui.musicPlusBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    setGameMusicVolume(gameMusicVolume + 10);
                    return;
                }
                if (ui.footMinusBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    setFootstepVolume(footstepVolume - 10);
                    return;
                }
                if (ui.footPlusBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    setFootstepVolume(footstepVolume + 10);
                    return;
                }
                if (ui.continueBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    hasSavedProgress = false;
                    gameState = (currentLevel == 2) ? STATE_LEVEL2_PLAY : STATE_PLAY;
                    refreshPauseAudio();
                    return;
                }
                if (ui.menuBtn.contains(mouseH.mouseX, mouseH.mouseY)) {
                    mouseH.resetClick();
                    saveProgressAndBackToMenu();
                    return;
                }
            }
            return;
        }

        if (gameState != STATE_PLAY && gameState != STATE_LEVEL2_PLAY) {
            return;
        }

        if (player != null) {
            player.update();
            tileM.checkItemCollisions(player.getBounds());

            if (tileM.houseRect != null && player.getBounds().intersects(tileM.houseRect)) {
                if (currentLevel == 1 && player.hasEgg) {
                    showLevel1WinScreen();
                    return;
                } else if (currentLevel == 2 && eggsCollected >= 2) {
                    showGameCompletedScreen();
                    return;
                }
            }
        }

        for (int i = 0; i < chickens.size(); i++) {
            Chicken chicken = chickens.get(i);
            if (chicken == null) continue;

            if (chicken.alive) {
                chicken.update();
                if (player != null && player.getBounds().intersects(chicken.getBounds())) {
                    if (!player.invincible) {
                        player.takeDamage(10);
                    }
                }
            } else {
                chicken.respawnCounter++;
                if (chicken.respawnCounter >= 300) {
                    chicken.respawn();
                }
            }
        }

        if (player != null && player.health <= 0) {
            showGameOverScreen();
            return;
        }

        if (player != null && player.hasEgg) {
            eggEffectTick++;
            if (!isTensionPlaying) {
                stopGameplayMusic();
                playTensionMusic();
                isTensionPlaying = true;
            }
        } else {
            eggEffectTick = 0;
            if (isTensionPlaying) {
                stopTensionMusic();
                playGameplayMusic();
                isTensionPlaying = false;
            }
        }

        tileM.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileM.draw(g2);

        for (Chicken chicken : chickens) {
            chicken.draw(g2);
        }

        if (player != null) {
            player.draw(g2);
        }

        tileM.drawForeground(g2);

        if (player != null && player.hasEgg) {
            drawEggOverlay(g2);
        }

        if (ui != null) {
            ui.draw(g2);
        }

        if (confettiM != null) {
            confettiM.draw(g2);
        }

        g2.dispose();
    }

    private void drawEggOverlay(Graphics2D g2) {
        float pulse = (float) ((Math.sin(eggEffectTick * 0.08) + 1.0) / 2.0);
        float centerX = player.x + tileSize / 2f;
        float centerY = player.y + tileSize / 2f;
        float radius = 170f + pulse * 15f;

        float[] dist = {0.0f, 0.28f, 0.55f, 1.0f};
        Color[] colors = {
            new Color(255, 245, 190, 18),
            new Color(0, 0, 0, 0),
            new Color(0, 0, 0, 35),
            new Color(0, 0, 0, 72)
        };

        RadialGradientPaint paint = new RadialGradientPaint(
            new Point2D.Float(centerX, centerY),
            radius * 2.8f,
            dist,
            colors
        );

        g2.setPaint(paint);
        g2.fillRect(0, 0, screenWidth, screenHeight);
    }

    public void startNewGame() {
        clearSavedProgress();
        stopAllSounds();
        stopGameThread();
        confettiM.stop();

        currentLevel = 1;
        eggsCollected = 0;
        eggEffectTick = 0;
        isTensionPlaying = false;

        tileM.loadLevelMap(1);

        keyH.resetKeys();
        mouseH.resetClick();

        setupGame();
        startGameThread();

        requestFocusInWindow();
    }

    public void completeLevel() {
        showLevel1WinScreen();
    }

    public void startLevel2() {
        clearSavedProgress();
        stopAllSounds();
        confettiM.stop();

        currentLevel = 2;
        eggsCollected = 0;
        eggEffectTick = 0;
        isTensionPlaying = false;
        gameState = STATE_LEVEL2_PLAY;

        tileM.loadLevelMap(2);

        if (player != null) {
            player.setDefaultValues();
            player.name = this.playerName;
            player.hasEgg = false;

            if (tileM.playerStartX != 0 || tileM.playerStartY != 0) {
                player.x = tileM.playerStartX;
                player.y = tileM.playerStartY;
            }
        }

        chickens.clear();
        spawnInitialChickens();
        refreshAudioSettings();
        playGameplayMusic();
    }

    public void stopAllSounds() {
        victoryMusic.stop();
        gameOverMusic.stop();
        eggSound.stop();
        weaponSound.stop();
        slashSound.stop();
        tensionMusic.stop();
        deathSound.stop(); 
        hurtSound.stop(); // Dừng âm thanh bị đánh
        stopGameplayMusic();

        if (player != null) {
            player.stopFootstepSound();
        }
    }

    public void showGameWinScreen() {
        stopAllSounds();
        clearSavedProgress();
        gameState = STATE_GAME_WIN;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        playVictoryMusic();
        confettiM.start();
    }

    public void showGameCompletedScreen() {
        clearSavedProgress();
        gameState = STATE_GAME_COMPLETED;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        playVictoryMusic();
        confettiM.start();
        repaint();
    }
    
    public void showGameOverScreen() {
        clearSavedProgress();
        gameState = STATE_GAME_OVER;
        mouseH.resetClick();

        if (player != null) {
            player.stopFootstepSound();
        }

        playGameOverMusic();
        repaint();
    }   

    public void restartGame() {
        clearSavedProgress();
        stopAllSounds();
        confettiM.stop();
        stopGameThread();

        currentLevel = 1;
        eggsCollected = 0;
        eggEffectTick = 0;
        isTensionPlaying = false;

        tileM.loadLevelMap(1);

        keyH.resetKeys();
        mouseH.resetClick();

        setupGame();
        startGameThread();

        requestFocusInWindow();
    }
}