package mygame.ai;

import java.awt.Rectangle;
import java.util.ArrayList;
import mygame.main.GamePanel;

public class PathFinder {
    GamePanel gp;
    public Node[][] node;
    ArrayList<Node> openList = new ArrayList<>();
    public ArrayList<Node> pathList = new ArrayList<>();
    Node startNode, goalNode, currentNode;
    boolean goalReached = false;
    int step = 0;

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        instantiateNodes();
        updateSolidNodes();
    }

    public void instantiateNodes() {
        node = new Node[gp.maxScreenCol][gp.maxScreenRow];
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {
                node[col][row] = new Node(col, row);
            }
        }
    }

   public void updateSolidNodes() {
    for (int col = 0; col < gp.maxScreenCol; col++) {
        for (int row = 0; row < gp.maxScreenRow; row++) {

            node[col][row].solid = false;

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

        startCol = Math.max(0, Math.min(startCol, gp.maxScreenCol - 1));
        startRow = Math.max(0, Math.min(startRow, gp.maxScreenRow - 1));
        goalCol = Math.max(0, Math.min(goalCol, gp.maxScreenCol - 1));
        goalRow = Math.max(0, Math.min(goalRow, gp.maxScreenRow - 1));

        startNode = node[startCol][startRow];
        currentNode = startNode;
        goalNode = node[goalCol][goalRow];

        if (startNode.solid || goalNode.solid) {
            return false;
        }

        openList.add(startNode);
        return true;
    }

    public boolean search() {
        while (!goalReached && step < 1000) {
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.checked = true;
            openList.remove(currentNode);

            if (row - 1 >= 0) openNode(node[col][row - 1]);
            if (col - 1 >= 0) openNode(node[col - 1][row]);
            if (row + 1 < gp.maxScreenRow) openNode(node[col][row + 1]);
            if (col + 1 < gp.maxScreenCol) openNode(node[col + 1][row]);

            if (openList.isEmpty()) break;

            int bestNodeIndex = 0;
            int bestNodefCost = Integer.MAX_VALUE;

            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).fCost < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).fCost;
                } else if (openList.get(i).fCost == bestNodefCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
            step++;
        }
        return goalReached;
    }

    public void openNode(Node node) {
        if (!node.open && !node.checked && !node.solid) {
            node.open = true;
            node.parent = currentNode;
            getCosts(node);
            openList.add(node);
        }
    }

    private void getCosts(Node node) {
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;

        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;

        node.fCost = node.gCost + node.hCost;
    }

    public void trackThePath() {
        Node current = goalNode;
        while (current != startNode) {
            pathList.add(0, current);
            current = current.parent;
        }
    }
}