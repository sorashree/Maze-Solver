import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Maze_Solver_A_Star {
    private Maze_Solver_A_Star() {
    }

    public static MazeSolverMetrics solve(Cell[][] grid, Cell start, Cell goal) {
        MazeSolverMetrics metrics = new MazeSolverMetrics("A*");
        Maze_Utils.resetSearchState(grid);

        long memoryBefore = Maze_Utils.memoryProbeStart();
        long startTime = System.nanoTime();

        List<Cell> trace = new ArrayList<>();
        int explored = 0;
        int peakFrontier = 0;
        boolean found = false;

        if (start.isOpen() && goal.isOpen()) {
            Comparator<Cell> byF = Comparator
                    .comparingInt((Cell cell) -> cell.f)
                    .thenComparingInt(cell -> Maze_Utils.manhattan(cell, goal));

            PriorityQueue<Cell> openSet = new PriorityQueue<>(byF);
            start.g = 0;
            start.f = Maze_Utils.manhattan(start, goal);
            openSet.add(start);
            peakFrontier = 1;

            while (!openSet.isEmpty()) {
                Cell current = openSet.poll();
                if (current.visited) {
                    continue; // stale entry
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
                        neighbor.f = tentative + Maze_Utils.manhattan(neighbor, goal);
                        neighbor.parent = current;
                        openSet.add(neighbor);
                    }
                }
                peakFrontier = Math.max(peakFrontier, openSet.size());
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