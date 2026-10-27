/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        boolean[] visited = new boolean[n];
        int[] order = new int[n];

        int orderCount = 0;
        int[] stack = new int[n];
        int[] nextSlide = new int[n];

        int[][] outgoing = Graphs.slidesFrom(s);
        int[][] incoming = Graphs.slidesInto(s);

        for (int start = 0; start < n;start++){
            if (visited[start]){
                continue;

            }
            int top = 0;
            stack[top] = start;
            visited[start] = true;
            while (top >= 0){
                int cell = stack[top];
                if (nextSlide[cell] < outgoing[cell].length){
                    int slide = outgoing[cell][nextSlide[cell]];
                    nextSlide[cell]++;
                    int next = s.to()[slide];
                    if (!visited[next]){
                        visited[next] = true;
                        top++;
                        stack[top] = next;
                    }
                }else{
                    order[orderCount] = cell;
                    orderCount++;
                    top--;
                }
            }
        }
        int[] zoneOf = new int[n];
        java.util.Arrays.fill(zoneOf,-1);
        int zoneCount = 0;

        for (int i = orderCount - 1; i >= 0; i--){
            int start = order[i];
            if (zoneOf[start] != -1){
                continue;
            }
            int top = 0;
            stack[top] = start;
            zoneOf[start] = zoneCount;
            while (top >= 0){
                int cell = stack[top];
                top--;
                for (int j = 0; j < incoming[cell].length; j++) {
                    int slide = incoming[cell][j];
                    int next = s.from()[slide];
                    if (zoneOf[next] == -1){
                        zoneOf[next] = zoneCount;
                        top++;
                        stack[top] = next;
                    }


                }
            }
            zoneCount++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        long[] cost = new long[zoneCount];
        int[] stages = new int[zoneCount];

        java.util.Arrays.fill(cost,Long.MAX_VALUE);
        java.util.Arrays.fill(stages,1);
        cost[zoneOf[0]] = 0;
        int[] degree = new int[zoneCount];

        for (int i = 0; i < s.count(); i++){
            int from = zoneOf[s.from()[i]];
            int to = zoneOf[s.to()[i]];
            if (from != to){
                degree[from]++;
            }

        }
        int[][] zoneSlides = new int[zoneCount][];
        for (int z = 0; z < zoneCount; z++){
            zoneSlides[z] = new int[degree[z]];
        }
        int[] filled = new int[zoneCount];

        for (int i = 0; i < s.count(); i++) {
            int from = zoneOf[s.from()[i]];
            int to = zoneOf[s.to()[i]];
            if (from != to){
                zoneSlides[from][filled[from]] = i;
                filled[from]++;
            }
        }

        for (int z = 0; z < zoneCount; z++ ){
            for (int j = 0; j < zoneSlides[z].length; j++){
                int slide = zoneSlides[z][j];
                int to = zoneOf[s.to()[slide]];
                if (stages[z] + 1 > stages[to]){
                    stages[to] = stages[z] +1;
                }
                if (cost[z] != Long.MAX_VALUE){
                    long candidate = cost[z] + s.time()[slide];
                    if (candidate < cost[to]){
                        cost[to] = candidate;
                    }
                }
            }
        }
        int flowStages = 0;
        for (int z = 0; z < zoneCount; z++){
            if (stages[z] > flowStages){
                flowStages = stages[z];
            }
        }
        return new long[]{flowStages, cost[zoneOf[s.n() - 1]]};
    }
}
