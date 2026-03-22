package mygame.tile;

import mygame.entity.Entity;
import mygame.main.GamePanel;
import java.awt.Rectangle;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        // Logic kiểm tra va chạm với tường (Giữ nguyên của bạn)
        int entityLeftX = entity.x + entity.solidArea.x;
        int entityRightX = entityLeftX + entity.solidArea.width;
        int entityTopY = entity.y + entity.solidArea.y;
        int entityBottomY = entityTopY + entity.solidArea.height;

        int nextLeftX = entityLeftX;
        int nextTopY = entityTopY;

        switch (entity.direction) {
            case "up": nextTopY -= entity.speed; break;
            case "down": nextTopY += entity.speed; break;
            case "left": nextLeftX -= entity.speed; break;
            case "right": nextLeftX += entity.speed; break;
        }

        Rectangle futureBounds = new Rectangle(nextLeftX, nextTopY, entity.solidArea.width, entity.solidArea.height);

        for (Rectangle wall : gp.tileM.collisionRects) {
            if (futureBounds.intersects(wall)) {
                entity.collisionOn = true;
                break;
            }
        }
    }

    // --- HÀM MỚI: KIỂM TRA VA CHẠM GIỮA CÁC THỰC THỂ (GÀ VS GÀ) ---
    public int checkEntityCollision(Entity entity, Entity[] target) {
        int index = 999;

        // Tạo khung va chạm dự kiến cho thực thể đang di chuyển
        Rectangle futureBounds = new Rectangle(entity.x + entity.solidArea.x, entity.y + entity.solidArea.y, 
                                               entity.solidArea.width, entity.solidArea.height);

        switch (entity.direction) {
            case "up": futureBounds.y -= entity.speed; break;
            case "down": futureBounds.y += entity.speed; break;
            case "left": futureBounds.x -= entity.speed; break;
            case "right": futureBounds.x += entity.speed; break;
        }

        for (int i = 0; i < target.length; i++) {
            if (target[i] != null && target[i] != entity) { // Không tự check với chính mình
                
                // Khung va chạm của thực thể mục tiêu
                Rectangle targetBounds = new Rectangle(target[i].x + target[i].solidArea.x, 
                                                       target[i].y + target[i].solidArea.y, 
                                                       target[i].solidArea.width, target[i].solidArea.height);

                if (futureBounds.intersects(targetBounds)) {
                    entity.collisionOn = true;
                    index = i; // Trả về index của thực thể bị chạm
                }
            }
        }
        return index;
    }

    // Hàm kiểm tra va chạm với Player (Để gà không dẵm lên người chơi)
    public boolean checkPlayer(Entity entity) {
        boolean contactPlayer = false;

        Rectangle futureBounds = new Rectangle(entity.x + entity.solidArea.x, entity.y + entity.solidArea.y, 
                                               entity.solidArea.width, entity.solidArea.height);

        switch (entity.direction) {
            case "up": futureBounds.y -= entity.speed; break;
            case "down": futureBounds.y += entity.speed; break;
            case "left": futureBounds.x -= entity.speed; break;
            case "right": futureBounds.x += entity.speed; break;
        }

        Rectangle playerBounds = new Rectangle(gp.player.x + gp.player.solidArea.x, 
                                               gp.player.y + gp.player.solidArea.y, 
                                               gp.player.solidArea.width, gp.player.solidArea.height);

        if (futureBounds.intersects(playerBounds)) {
            entity.collisionOn = true;
            contactPlayer = true;
        }

        return contactPlayer;
    }

    public String checkEntity(Entity entity, Rectangle targetRect, String targetName) {
        if (targetRect == null) return "";
        Rectangle entityBounds = new Rectangle(entity.x + entity.solidArea.x, entity.y + entity.solidArea.y, 
                                               entity.solidArea.width, entity.solidArea.height);
        if (entityBounds.intersects(targetRect)) return targetName;
        return "";
    }
}