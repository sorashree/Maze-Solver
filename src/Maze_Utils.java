import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Maze_Utils {

    public static final int[][] DIRS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    private Maze_Utils() {
        
    }

    public static boolean inBounds(Cell[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }

    public static List<Cell> openNeighbors(Cell[][] grid, Cell cell) {
        List<Cell> result = new ArrayList<>(4);
        for (int[] d : DIRS) {
            int r = cell.row + d[0];
            int c = cell.col + d[1];
            if (inBounds(grid, r, c) && grid[r][c].isOpen()) {
                result.add(grid[r][c]);
            }
        }
        return result;
    }

    public static int manhattan(Cell a, Cell b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    public static List<Cell> reconstructPath(Cell goal) {
        List<Cell> path = new ArrayList<>();
        for (Cell c = goal; c != null; c = c.parent) {
            path.add(c);
        }
        Collections.reverse(path);
        return path;
    }

    public static void resetSearchState(Cell[][] grid) {
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.resetSearchState();
            }
        }
    }

    public static int pathCost(List<Cell> path) {
        int total = 0;
        for (int i = 1; i < path.size(); i++) {
            total += path.get(i).cost;
        }
        return total;
    }

    private static final com.sun.management.ThreadMXBean ALLOCATION_BEAN = resolveAllocationBean();

    private static com.sun.management.ThreadMXBean resolveAllocationBean() {
        try {
            java.lang.management.ThreadMXBean bean =
                    java.lang.management.ManagementFactory.getThreadMXBean();
            if (bean instanceof com.sun.management.ThreadMXBean) {
                com.sun.management.ThreadMXBean sunBean = (com.sun.management.ThreadMXBean) bean;
                if (sunBean.isThreadAllocatedMemorySupported()) {
                    sunBean.setThreadAllocatedMemoryEnabled(true);
                    return sunBean;
                }
            }
        } catch (Throwable ignored) {
            
        }
        return null;
    }

    public static boolean isAllocationCountingSupported() {
        return ALLOCATION_BEAN != null;
    }

    public static long memoryProbeStart() {
        if (ALLOCATION_BEAN != null) {
            return ALLOCATION_BEAN.getCurrentThreadAllocatedBytes();
        }
        Runtime rt = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) {
            rt.gc();
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return rt.totalMemory() - rt.freeMemory();
    }

    public static long memoryProbeEnd() {
        if (ALLOCATION_BEAN != null) {
            return ALLOCATION_BEAN.getCurrentThreadAllocatedBytes();
        }
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024L * 1024L) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }

    public static String formatDuration(long nanos) {
        if (nanos < 1_000L) {
            return nanos + " ns";
        }
        if (nanos < 1_000_000L) {
            return String.format("%.1f \u00b5s", nanos / 1_000.0);
        }
        return String.format("%.2f ms", nanos / 1_000_000.0);
    }

    public static Cell defaultStart(Cell[][] grid) {
        return grid[1][1];
    }

    public static Cell defaultGoal(Cell[][] grid) {
        return grid[grid.length - 2][grid[0].length - 2];
    }
}