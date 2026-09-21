import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Maze_Solver_DFS {

    private Maze_Solver_DFS() {
    }

    public static MazeSolverMetrics solve(Cell[][] grid, Cell start, Cell goal) {
        MazeSolverMetrics metrics = new MazeSolverMetrics("DFS");
        Maze_Utils.resetSearchState(grid);

        long memoryBefore = Maze_Utils.memoryProbeStart();
        long startTime = System.nanoTime();

        List<Cell> trace = new ArrayList<>();
        int explored = 0;
        int peakFrontier = 0;
        boolean found = false;

        if (start.isOpen() && goal.isOpen()) {
            Deque<Cell> stack = new ArrayDeque<>();
            start.visited = true;
            start.g = 0;
            stack.push(start);
            peakFrontier = 1;

            while (!stack.isEmpty()) {
                Cell current = stack.pop();
                explored++;
                trace.add(current);

                if (current.equals(goal)) {
                    found = true;
                    break;
                }

                for (Cell neighbor : Maze_Utils.openNeighbors(grid, current)) {
                    if (!neighbor.visited) {
                        neighbor.visited = true;
                        neighbor.parent = current;
                        neighbor.g = current.g + 1;
                        stack.push(neighbor);
                    }
                }
                peakFrontier = Math.max(peakFrontier, stack.size());
            }
        }

        long elapsed = System.nanoTime() - startTime;
        long memoryAfter = Maze_Utils.memoryProbeEnd();

        metrics.setElapsedNanos(elapsed);
        metrics.setMemoryBytes(Math.max(0L, memoryAfter - memoryBefore));
        metrics.setNodesExplored(explored);
        metrics.setPeakFrontier(peakFrontier);
        metrics.setExplorationOrder(trace);
        metrics.setSolved(found);
        if (found) {
            metrics.setPath(Maze_Utils.reconstructPath(goal));
        }
        return metrics;
    }
}