package mygame.ai;

import java.awt.Rectangle;
import java.util.ArrayList;
import mygame.main.GamePanel;

public class PathFinder {

    // --- CẤU HÌNH & THÀNH PHẦN HỆ THỐNG ---
    private final GamePanel gp;
    public Node[][] node;
    
    // --- DANH SÁCH ĐIỀU HƯỚNG ---
    private final ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    
    // --- TRẠNG THÁI TÌM KIẾM ---
    private Node startNode, goalNode, currentNode;
    private boolean goalReached = false;
    private int step = 0;
    private final int MAX_STEPS = 1000; // Giới hạn để tránh vòng lặp vô tận

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
        updateSolidNodes();
    }

    // =========================================================================
    // 1. KHỞI TẠO & CẬP NHẬT DỮ LIỆU TĨNH (INITIALIZATION)
    // =========================================================================

    public void instantiateNodes() {
        node = new Node[gp.maxScreenCol][gp.maxScreenRow];
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                node[col][row] = new Node(col, row);
            }
        }
    }

    /**
     * Cập nhật trạng thái vật cản. 
     * Gọi hàm này khi Map thay đổi hoặc khi bắt đầu Game.
     */
    public void updateSolidNodes() {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                
                node[col][row].solid = false;

                // Tính toán tọa độ thực tế của ô
                int centerX = col * gp.tileSize + gp.tileSize / 2;
                int centerY = row * gp.tileSize + gp.tileSize / 2;

                for (Rectangle rect : gp.tileM.collisionRects) {
                    if (rect.contains(centerX, centerY)) {
                        node[col][row].solid = true;
                        break;
                    }
                }
            }
        }
    }

    // =========================================================================
    // 2. THIẾT LẬP TRƯỚC KHI TÌM KIẾM (SETUP)
    // =========================================================================

    public void resetNodes() {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                node[col][row].open = false;
                node[col][row].checked = false;
                node[col][row].parent = null;
                node[col][row].gCost = 0;
                node[col][row].hCost = 0;
                node[col][row].fCost = 0;
            }
        }
        openList.clear();
        pathList.clear();
        goalReached = false;
        step = 0;
    }

    public boolean setNodes(int startCol, int startRow, int goalCol, int goalRow) {
        resetNodes();

        // Kiểm tra biên để tránh IndexOutOfBoundsException
        if (isOutOfBounds(startCol, startRow) || isOutOfBounds(goalCol, goalRow)) {
            return false;
        }

        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];

        // Nếu điểm bắt đầu hoặc đích nằm trong vật cản
        if (startNode.solid || goalNode.solid) {
            return false;
        }

        openList.add(startNode);
        return true;
    }

    private boolean isOutOfBounds(int col, int row) {
        return col < 0 || col >= gp.maxScreenCol || row < 0 || row >= gp.maxScreenRow;
    }

    // =========================================================================
    // 3. LOGIC THUẬT TOÁN A* (SEARCH CORE)
    // =========================================================================

    public boolean search() {
        while (!goalReached && step < MAX_STEPS) {
            int col = currentNode.col;
            int row = currentNode.row;

            // Đánh dấu Node hiện tại đã xử lý
            currentNode.checked = true;
            openList.remove(currentNode);

            // Kiểm tra 4 hướng xung quanh
            exploreNeighbors(col, row);

            // Nếu không còn Node nào để đi tiếp
            if (openList.isEmpty()) {
                break;
            }

            // Tìm Node tối ưu nhất trong Open List
            currentNode = getBestNodeFromOpenList();

            // Kiểm tra đích
            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
            step++;
        }
        return goalReached;
    }

    private void exploreNeighbors(int col, int row) {
        if (row - 1 >= 0) openNode(node[col][row - 1]); // Lên
        if (col - 1 >= 0) openNode(node[col - 1][row]); // Trái
        if (row + 1 < gp.maxScreenRow) openNode(node[col][row + 1]); // Xuống
        if (col + 1 < gp.maxScreenCol) openNode(node[col + 1][row]); // Phải
    }

    private Node getBestNodeFromOpenList() {
        int bestNodeIndex = 0;
        int bestNodefCost = Integer.MAX_VALUE;

        for (int i = 0; i < openList.size(); i++) {
            Node checkNode = openList.get(i);
            
            // Ưu tiên fCost thấp nhất
            if (checkNode.fCost < bestNodefCost) {
                bestNodeIndex = i;
                bestNodefCost = checkNode.fCost;
            } 
            // Nếu fCost bằng nhau, chọn Node có gCost (quãng đường đã đi) ngắn hơn
            else if (checkNode.fCost == bestNodefCost) {
                if (checkNode.gCost < openList.get(bestNodeIndex).gCost) {
                    bestNodeIndex = i;
                }
            }
        }
        return openList.get(bestNodeIndex);
    }

    public void openNode(Node node) {
        if (!node.open && !node.checked && !node.solid) {
            node.open = true;
            node.parent = currentNode;
            calculateCosts(node);
            openList.add(node);
        }
    }

    private void calculateCosts(Node node) {
        // Manhattan Distance: |x1 - x2| + |y1 - y2|
        node.gCost = Math.abs(node.col - startNode.col) + Math.abs(node.row - startNode.row);
        node.hCost = Math.abs(node.col - goalNode.col) + Math.abs(node.row - goalNode.row);
        node.fCost = node.gCost + node.hCost;
    }

    // =========================================================================
    // 4. KẾT QUẢ (RESULTS)
    // =========================================================================

    public void trackThePath() {
        Node current = goalNode;
        while (current != startNode) {
            pathList.add(0, current); // Luôn thêm vào đầu để có thứ tự Start -> Goal
            current = current.parent;
        }
    }
}