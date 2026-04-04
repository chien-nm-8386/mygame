package mygame.main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {
    private GamePanel gp;
    public int mouseX, mouseY;
    public boolean pressed = false;
    public boolean clicked = false;
    
    public MouseHandler(GamePanel gp) {
            this.gp = gp;
    }  

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
        clicked = true;
        mouseX = e.getX();
        mouseY = e.getY();
        if (gp.gameState == gp.STATE_PAUSE) {
            handlePauseMenuClicks();
        }
    }
      private void handlePauseMenuClicks() {
        // --- LOGIC CHO GAME MUSIC ---
        if (gp.ui.musicPlusBtn.contains(mouseX, mouseY)) {
            gp.setGameMusicVolume(gp.gameMusicVolume + 5);
        }
        if (gp.ui.musicMinusBtn.contains(mouseX, mouseY)) {
            gp.setGameMusicVolume(gp.gameMusicVolume - 5);
        }
        
        // --- LOGIC CHO FOOTSTEP ---
        if (gp.ui.footPlusBtn.contains(mouseX, mouseY)) {
            gp.setFootstepVolume(gp.footstepVolume + 5);
        }
        if (gp.ui.footMinusBtn.contains(mouseX, mouseY)) {
            gp.setFootstepVolume(gp.footstepVolume - 5);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void resetClick() {
        clicked = false;
    }
}