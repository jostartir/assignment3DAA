/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int[][] neighbours = Graphs.open(w,broken);

        int deadEnds = 0;
        for (int cell = 0;cell < n; cell++){
            if (neighbours[cell].length == 1){
                deadEnds++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] stack = new int[n];
        int top = 0;
        stack[top] = 0;
        visited[0] = true;
        int visitedCount = 1;
        int[] rowStep = {-1,0,1,0};
        int[] colStep = {0,1,0,-1};



        while (top >= 0 && stack[top] != n-1){
            int cell = stack[top];
            boolean moved = false;
            for (int direction = 0; direction < 4; direction++){
                int row = cell / w.cols() + rowStep[direction];
                int col = cell % w.cols() + colStep[direction];

                if (row < 0 || row >= w.rows() || col < 0 || col >= w.cols()){
                    continue;
                }

                int next = row * w.cols() + col;
                if (visited[next] || !canWalk(neighbours,cell,next)){
                    continue;
                }
                top++;
                stack[top] = next;
                visited[next] = true;
                visitedCount++;
                moved = true;
                break;
            }
            if (!moved) {
                top--;
            }
        }

        int[] path = java.util.Arrays.copyOf(stack, top + 1);
        return new Result(path, visitedCount, deadEnds);
    }

    private static boolean canWalk(int[][] neighbours, int from, int to) {
        for (int neighbour : neighbours[from]) {
            if (neighbour == to) {
                return true;
            }
        }

        return false;
    }
}
