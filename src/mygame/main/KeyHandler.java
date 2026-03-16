package mygame.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // Các biến trạng thái để báo cho Player biết phím nào đang được giữ
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    @Override
    public void keyTyped(KeyEvent e) {
        // Hàm này bắt buộc phải có khi dùng KeyListener, nhưng ta không dùng đến cho di chuyển
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // Lấy mã số của phím vừa được bấm xuống

        // Nếu bấm W hoặc phím Mũi tên Lên
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = true;
        }
        // Nếu bấm S hoặc phím Mũi tên Xuống
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        // Nếu bấm A hoặc phím Mũi tên Trái
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        // Nếu bấm D hoặc phím Mũi tên Phải
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // Lấy mã số của phím vừa được nhả ra

        // Khi nhả phím ra thì phải đặt lại trạng thái là false để nhân vật dừng lại
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
    }
}