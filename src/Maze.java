import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[w.cells() - 1];
        int count = 0;
        for (int i = 0; i < w.count(); i++){
            int a = w.a()[i];
            int b = w.b()[i];

            if (dsu.union(a,b)){
                broken[count] = i;
                count++;
            }
        }
        return broken;
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] order = new Integer[w.count()];

        for (int i = 0; i < w.count(); i++){
            order[i] = i;
        }

        Arrays.sort(order,Comparator.comparingInt(i -> w.cost()[i]));
        DisjointSet dsu = new DisjointSet(w.cells());

        int[] broken = new int[w.cells() - 1];
        int count = 0;

        for (int i : order){
            if (dsu.union(w.a()[i],w.b()[i])){
                broken[count] = i;
                count++;
            }
        }
        return broken;
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        boolean[] visited = new boolean[w.cells()];
        long totalCost = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<>(
                Comparator.comparingInt(i -> w.cost()[i])
        );

        int[][] wallsAt = new int[w.cells()][4];
        int[] wallsCount = new int[w.cells()];

        for (int i = 0; i < w.count(); i++) {
            int a = w.a()[i];
            int b = w.b()[i];

            wallsAt[a][wallsCount[a]] = i;
            wallsCount[a]++;

            wallsAt[b][wallsCount[b]] = i;
            wallsCount[b]++;
        }

        visited[0] = true;

        for (int j = 0; j < wallsCount[0]; j++) {
            queue.add(wallsAt[0][j]);
        }

        while (!queue.isEmpty()) {
            int wall = queue.poll();

            int a = w.a()[wall];
            int b = w.b()[wall];

            if (visited[a] && visited[b]) {
                continue;
            }

            int next;
            if (visited[a]) {
                next = b;
            } else {
                next = a;
            }

            visited[next] = true;
            totalCost += w.cost()[wall];

            for (int j = 0; j < wallsCount[next]; j++) {
                queue.add(wallsAt[next][j]);
            }
        }

        return totalCost;
    }
}
