import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Maze_Solver_Dijkstras {

    private Maze_Solver_Dijkstras() {
    }

    public static MazeSolverMetrics solve(Cell[][] grid, Cell start, Cell goal) {
        MazeSolverMetrics metrics = new MazeSolverMetrics("Dijkstra");
        Maze_Utils.resetSearchState(grid);

        long memoryBefore = Maze_Utils.memoryProbeStart();
        long startTime = System.nanoTime();

        List<Cell> trace = new ArrayList<>();
        int explored = 0;
        int peakFrontier = 0;
        boolean found = false;

        if (start.isOpen() && goal.isOpen()) {
            PriorityQueue<Cell> frontier =
                    new PriorityQueue<>(Comparator.comparingInt(cell -> cell.g));
            start.g = 0;
            frontier.add(start);
            peakFrontier = 1;

            while (!frontier.isEmpty()) {
                Cell current = frontier.poll();
                if (current.visited) {
                    continue; // stale entry left over from an improvement
                }
                current.visited = true;
                explored++;
                trace.add(current);

                if (current.equals(goal)) {
                    found = true;
                    break;
                }

                for (Cell neighbor : Maze_Utils.openNeighbors(grid, current)) {
                    if (neighbor.visited) {
                        continue;
                    }
                    int tentative = current.g + neighbor.cost;
                    if (tentative < neighbor.g) {
                        neighbor.g = tentative;
                        neighbor.parent = current;
                        frontier.add(neighbor);
                    }
                }
                peakFrontier = Math.max(peakFrontier, frontier.size());
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