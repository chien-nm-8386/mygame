package mygame.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp; // THÊM DÒNG NÀY: Khai báo biến gp
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean escapePressed;
    public boolean pPressed;
    public boolean spacePressed;
    public boolean spaceJustPressed;
    
    // THÊM CONSTRUCTOR NÀY: Để nhận gp từ GamePanel
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
         int code = e.getKeyCode();
        
        // Kiểm tra trạng thái thắng cuộc
        if (gp.gameState == gp.STATE_GAME_WIN || gp.gameState == gp.STATE_GAME_COMPLETED) {
            if (!gp.ui.inputFinished) {
                // Nhập số từ 0-9
                if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
                    if (gp.ui.inputNumber.length() < 2) { 
                        gp.ui.inputNumber += e.getKeyChar();
                    }
                }
                // Xóa (BackSpace)
                if (code == KeyEvent.VK_BACK_SPACE && gp.ui.inputNumber.length() > 0) {
                    gp.ui.inputNumber = gp.ui.inputNumber.substring(0, gp.ui.inputNumber.length() - 1);
                }
                // Xác nhận (Enter)
                if (code == KeyEvent.VK_ENTER && !gp.ui.inputNumber.isEmpty()) {
                    try {
                        int num = Integer.parseInt(gp.ui.inputNumber);
                        if (num >= 1 && num <= 10) {
                            gp.ui.finalDish = getDishName(num);
                            gp.ui.inputFinished = true;
                        } else {
                            gp.ui.inputNumber = ""; // Reset nếu nhập ngoài khoảng 1-10
                        }
                    } catch (NumberFormatException ex) {
                        gp.ui.inputNumber = "";
                    }
                }
                return; // Ngăn người chơi di chuyển khi đang nhập số
            }
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_ESCAPE) escapePressed = true;
        if (code == KeyEvent.VK_P) pPressed = true;
        if (code == KeyEvent.VK_SPACE) {
            if (!spacePressed) {
                spacePressed = true;
                spaceJustPressed = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_SPACE) spacePressed = false;
        if (code == KeyEvent.VK_P) pPressed = false;
    }
    
    public void resetKeys() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        escapePressed = false;
        pPressed = false;
        spacePressed = false;
        spaceJustPressed = false;
    }
    private String getDishName(int num) {
        switch (num) {
            case 1: return "Trứng luộc lòng đào";
            case 2: return "Bánh Flan caramen";
            case 3: return "Trứng cuộn Nhật Bản";
            case 4: return "Khổ qua xào trứng";
            case 5: return "Trứng hấp vân";
            case 6: return "Bánh mì ốp la";
            case 7: return "Cơm chiên trứng";
            case 8: return "Trứng nướng";
            case 9: return "Salad trứng";
            case 10: return "Trứng kho tàu";
            default: return "Món trứng đặc biệt";
        }
    }
    
    public boolean consumeSpaceJustPressed() {
        if (spaceJustPressed) {
            spaceJustPressed = false;
            return true;
        }
        return false;
    }
}