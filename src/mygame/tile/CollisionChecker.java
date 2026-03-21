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
        // Tạo một hình chữ nhật giả định cho vị trí TIẾP THEO của nhân vật
        int entityLeftX = entity.x + entity.solidArea.x;
        int entityRightX = entity.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopY = entity.y + entity.solidArea.y;
        int entityBottomY = entity.y + entity.solidArea.y + entity.solidArea.height;

        int nextLeftX = entityLeftX;
        int nextRightX = entityRightX;
        int nextTopY = entityTopY;
        int nextBottomY = entityBottomY;

        // Dự đoán vị trí dựa trên hướng di chuyển
        switch (entity.direction) {
            case "up": nextTopY -= entity.speed; nextBottomY -= entity.speed; break;
            case "down": nextTopY += entity.speed; nextBottomY += entity.speed; break;
            case "left": nextLeftX -= entity.speed; nextRightX -= entity.speed; break;
            case "right": nextLeftX += entity.speed; nextRightX += entity.speed; break;
        }

        // Tạo khung va chạm dự kiến (Future Bounds)
        Rectangle futureBounds = new Rectangle(nextLeftX, nextTopY, entity.solidArea.width, entity.solidArea.height);

        // Duyệt qua tất cả các khối va chạm từ Tiled
        entity.collisionOn = false;
        for (Rectangle wall : gp.tileM.collisionRects) {
            if (futureBounds.intersects(wall)) {
                entity.collisionOn = true;
                break;
            }
        }
    }
    
    // Hàm kiểm tra xem đã chạm vào Trứng hay Nhà chưa
    public String checkEntity(Entity entity, Rectangle targetRect, String targetName) {
        if (targetRect == null) return "";
        
        Rectangle entityBounds = new Rectangle(entity.x + entity.solidArea.x, entity.y + entity.solidArea.y, 
                                               entity.solidArea.width, entity.solidArea.height);
        
        if (entityBounds.intersects(targetRect)) {
            return targetName;
        }
        return "";
    }
}