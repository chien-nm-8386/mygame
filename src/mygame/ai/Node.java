package mygame.ai;

import mygame.entity.*;

public class Node {
    public Node parent;
    public int col, row;
    public int gCost; // Khoảng cách từ điểm bắt đầu
    public int hCost; // Khoảng cách đến điểm đích (ước tính)
    public int fCost; // Tổng chi phí (gCost + hCost)
    public boolean solid;
    public boolean open;
    public boolean checked;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }
}