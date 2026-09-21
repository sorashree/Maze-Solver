import java.util.ArrayList;
import java.util.List;

/**
 * Everything one solver run produced: the answer (path), the trace the GUI
 * animates (exploration order) and the numbers shown in the stats panel.
 */
public class MazeSolverMetrics {

    private final String algorithm;

    private boolean solved;
    private long elapsedNanos;
    private long memoryBytes;
    private int nodesExplored;
    private int peakFrontier;
    private int pathCost;

    private List<Cell> path = new ArrayList<>();
    private List<Cell> explorationOrder = new ArrayList<>();

    public MazeSolverMetrics(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public long getElapsedNanos() {
        return elapsedNanos;
    }

    public void setElapsedNanos(long elapsedNanos) {
        this.elapsedNanos = elapsedNanos;
    }

    public long getMemoryBytes() {
        return memoryBytes;
    }

    public void setMemoryBytes(long memoryBytes) {
        this.memoryBytes = memoryBytes;
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

    public void setNodesExplored(int nodesExplored) {
        this.nodesExplored = nodesExplored;
    }

    public int getPeakFrontier() {
        return peakFrontier;
    }

    public void setPeakFrontier(int peakFrontier) {
        this.peakFrontier = peakFrontier;
    }

    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }

    public List<Cell> getPath() {
        return path;
    }

    public void setPath(List<Cell> path) {
        this.path = path;
        this.pathCost = Maze_Utils.pathCost(path);
    }

    public List<Cell> getExplorationOrder() {
        return explorationOrder;
    }

    public void setExplorationOrder(List<Cell> explorationOrder) {
        this.explorationOrder = explorationOrder;
    }

    public int getSteps() {
        return Math.max(0, path.size() - 1);
    }

    public static String tableHeader() {
        return String.format("%-9s %-7s %9s %6s %5s %7s %9s",
                "ALGO", "RESULT", "TIME", "STEPS", "COST", "SEEN", "MEMORY");
    }

    public String toTableRow() {
        return String.format("%-9s %-7s %9s %6s %5s %7d %9s",
                algorithm,
                solved ? "solved" : "NO PATH",
                Maze_Utils.formatDuration(elapsedNanos),
                solved ? String.valueOf(getSteps()) : "-",
                solved ? String.valueOf(pathCost) : "-",
                nodesExplored,
                Maze_Utils.formatBytes(memoryBytes));
    }

    @Override
    public String toString() {
        return toTableRow();
    }
}