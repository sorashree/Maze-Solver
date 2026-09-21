import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;


public class Random_Maze_Generator {

    private Random_Maze_Generator() {
        
    }

    public static Cell[][] generate(int rows, int cols, double loopChance,
                                    double blockChance, boolean terrainCosts, Long seed) {

        rows = normalize(rows);
        cols = normalize(cols);
        Random rng = (seed == null) ? new Random() : new Random(seed);

        Cell[][] grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c, true);
            }
        }

        carve(grid, rng);
        addLoops(grid, rng, loopChance);
        addBlocks(grid, rng, blockChance);

        grid[1][1].wall = false;
        grid[rows - 2][cols - 2].wall = false;

        assignCosts(grid, rng, terrainCosts);
        return grid;
    }

    private static int normalize(int n) {
        if (n < 5) {
            n = 5;
        }
        return (n % 2 == 0) ? n + 1 : n;
    }

    /** Iterative recursive backtracker over the odd-indexed cells. */
    private static void carve(Cell[][] grid, Random rng) {
        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] carved = new boolean[rows][cols];
        Deque<Cell> stack = new ArrayDeque<>();

        Cell current = grid[1][1];
        current.wall = false;
        carved[1][1] = true;
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.peek();
            List<int[]> options = new ArrayList<>(4);
            for (int[] d : Maze_Utils.DIRS) {
                int r = current.row + d[0] * 2;
                int c = current.col + d[1] * 2;
                if (r > 0 && r < rows - 1 && c > 0 && c < cols - 1 && !carved[r][c]) {
                    options.add(new int[] { r, c });
                }
            }

            if (options.isEmpty()) {
                stack.pop();
                continue;
            }

            int[] next = options.get(rng.nextInt(options.size()));
            // Open the wall between current and next, then the next cell itself.
            grid[(current.row + next[0]) / 2][(current.col + next[1]) / 2].wall = false;
            grid[next[0]][next[1]].wall = false;
            carved[next[0]][next[1]] = true;
            stack.push(grid[next[0]][next[1]]);
        }
    }

    private static void addLoops(Cell[][] grid, Random rng, double loopChance) {
        if (loopChance <= 0) {
            return;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (!grid[r][c].wall || rng.nextDouble() >= loopChance) {
                    continue;
                }
                // Only remove a wall that separates two open cells, so the
                // outer border and solid blocks stay intact.
                boolean horizontalGap = grid[r][c - 1].isOpen() && grid[r][c + 1].isOpen();
                boolean verticalGap = grid[r - 1][c].isOpen() && grid[r + 1][c].isOpen();
                if (horizontalGap || verticalGap) {
                    grid[r][c].wall = false;
                }
            }
        }
    }

    private static void addBlocks(Cell[][] grid, Random rng, double blockChance) {
        if (blockChance <= 0) {
            return;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (grid[r][c].isOpen() && rng.nextDouble() < blockChance) {
                    grid[r][c].wall = true;
                }
            }
        }
    }

    private static void assignCosts(Cell[][] grid, Random rng, boolean terrainCosts) {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.cost = (terrainCosts && cell.isOpen()) ? 1 + rng.nextInt(9) : 1;
            }
        }
        // Keep the endpoints cheap so costs never distort the start/goal squares.
        grid[1][1].cost = 1;
        grid[grid.length - 2][grid[0].length - 2].cost = 1;
    }

    public static Cell[][] generateSolvable(int rows, int cols, double loopChance,
                                            double blockChance, boolean terrainCosts) {
        for (int attempt = 0; attempt < 25; attempt++) {
            Cell[][] grid = generate(rows, cols, loopChance, blockChance, terrainCosts, null);
            MazeSolverMetrics probe = Maze_Solver_BFS.solve(
                    grid, Maze_Utils.defaultStart(grid), Maze_Utils.defaultGoal(grid));
            Maze_Utils.resetSearchState(grid);
            if (probe.isSolved()) {
                return grid;
            }
        }
        return generate(rows, cols, loopChance, 0.0, terrainCosts, null);
    }


    public static List<int[]> directions() {
        return Collections.unmodifiableList(new ArrayList<>(List.of(Maze_Utils.DIRS)));
    }
}