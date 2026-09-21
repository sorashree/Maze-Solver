import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
public class Maze_GUI extends JFrame {

    //colours 
    private static final Color WALL_COLOR = new Color(38, 44, 58);
    private static final Color OPEN_COLOR = new Color(249, 250, 252);
    private static final Color TERRAIN_COLOR = new Color(214, 199, 168);
    private static final Color EXPLORED_COLOR = new Color(168, 205, 235);
    private static final Color PATH_COLOR = new Color(255, 170, 60);
    private static final Color PATH_LINE_COLOR = new Color(196, 96, 0);
    private static final Color START_COLOR = new Color(46, 160, 87);
    private static final Color GOAL_COLOR = new Color(205, 58, 58);
    private static final Color GRID_COLOR = new Color(225, 228, 233);
    private static final Color OK_BG = new Color(223, 245, 228);
    private static final Color OK_FG = new Color(23, 105, 55);
    private static final Color FAIL_BG = new Color(253, 226, 226);
    private static final Color FAIL_FG = new Color(158, 32, 32);
    private static final Color IDLE_BG = new Color(236, 238, 242);
    private static final Color IDLE_FG = new Color(70, 76, 88);

    //model 
    private Cell[][] grid;
    private Cell start;
    private Cell goal;
    private boolean[][] revealed;
    private boolean[][] onPath;
    private List<Cell> pathCells = java.util.Collections.emptyList();

    //animation 
    private Timer animationTimer;
    private MazeSolverMetrics animating;
    private int revealIndex;

    // ---- widgets ---------------------------------------------------------
    private final MazePanel mazePanel = new MazePanel();
    private final JLabel statusLabel = new JLabel("Click \"New Maze\", then pick a search.", SwingConstants.CENTER);
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(31, 11, 101, 2));
    private final JSlider loopSlider = new JSlider(0, 40, 12);
    private final JSlider blockSlider = new JSlider(0, 30, 0);
    private final JCheckBox terrainCheck = new JCheckBox("Terrain costs", false);
    private final JCheckBox animateCheck = new JCheckBox("Animate search", true);
    private final JSlider speedSlider = new JSlider(1, 10, 6);
    private final JTextArea logArea = new JTextArea();

    private final JLabel timeValue = new JLabel("-");
    private final JLabel stepsValue = new JLabel("-");
    private final JLabel costValue = new JLabel("-");
    private final JLabel exploredValue = new JLabel("-");
    private final JLabel frontierValue = new JLabel("-");
    private final JLabel memoryValue = new JLabel("-");

    public Maze_GUI() {
        super("Maze Solver \u2014 A*, BFS, DFS and Dijkstra");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(buildControls(), BorderLayout.NORTH);
        add(buildMazeArea(), BorderLayout.CENTER);
        add(buildSidePanel(), BorderLayout.EAST);

        warmUp();
        newMaze();

        setMinimumSize(new Dimension(1060, 700));
        pack();
        setLocationRelativeTo(null);
    }

    // UI construction
    private JComponent buildControls() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 2, 10));

        JButton newMaze = new JButton("New Maze");
        newMaze.setToolTipText("Generate a fresh random maze using the settings below");
        newMaze.addActionListener(e -> newMaze());

        JButton aStar = new JButton("A* Search");
        aStar.addActionListener(e -> runAlgorithm("A*"));

        JButton bfs = new JButton("BFS");
        bfs.addActionListener(e -> runAlgorithm("BFS"));

        JButton dfs = new JButton("DFS");
        dfs.addActionListener(e -> runAlgorithm("DFS"));

        JButton dijkstra = new JButton("Dijkstra");
        dijkstra.addActionListener(e -> runAlgorithm("Dijkstra"));

        JButton runAll = new JButton("Compare All");
        runAll.setToolTipText("Run all four searches on this maze and list the results");
        runAll.addActionListener(e -> runAll());

        JButton clear = new JButton("Clear Path");
        clear.setToolTipText("Keep the maze, wipe the drawn search");
        clear.addActionListener(e -> {
            stopAnimation();
            clearOverlays();
            resetStats();
            setStatus("Cleared. Pick a search to run again.", IDLE_BG, IDLE_FG);
            mazePanel.repaint();
        });

        buttons.add(newMaze);
        buttons.add(Box.createHorizontalStrut(18));
        buttons.add(aStar);
        buttons.add(Box.createHorizontalStrut(6));
        buttons.add(bfs);
        buttons.add(Box.createHorizontalStrut(6));
        buttons.add(dfs);
        buttons.add(Box.createHorizontalStrut(6));
        buttons.add(dijkstra);
        buttons.add(Box.createHorizontalStrut(18));
        buttons.add(runAll);
        buttons.add(Box.createHorizontalStrut(6));
        buttons.add(clear);
        buttons.add(Box.createHorizontalGlue());

        JPanel settings = new JPanel();
        settings.setLayout(new BoxLayout(settings, BoxLayout.X_AXIS));
        settings.setBorder(BorderFactory.createEmptyBorder(2, 10, 8, 10));

        sizeSpinner.setToolTipText("Grid size in cells (kept odd so the walls line up)");
        ((JSpinner.DefaultEditor) sizeSpinner.getEditor()).getTextField().setColumns(3);
        sizeSpinner.setMaximumSize(sizeSpinner.getPreferredSize());

        configureSlider(loopSlider, "Extra openings: more loops mean more than one possible route");
        configureSlider(blockSlider, "Random blocking walls. Turn this up to make a maze unsolvable");
        configureSlider(speedSlider, "Animation speed");

        terrainCheck.setToolTipText("Give open cells a step cost of 1-9. Only Dijkstra and A* pay attention to it");

        settings.add(new JLabel("Size:"));
        settings.add(Box.createHorizontalStrut(4));
        settings.add(sizeSpinner);
        settings.add(Box.createHorizontalStrut(16));
        settings.add(new JLabel("Loops:"));
        settings.add(loopSlider);
        settings.add(Box.createHorizontalStrut(16));
        settings.add(new JLabel("Blocked cells:"));
        settings.add(blockSlider);
        settings.add(Box.createHorizontalStrut(16));
        settings.add(terrainCheck);
        settings.add(Box.createHorizontalStrut(8));
        settings.add(animateCheck);
        settings.add(Box.createHorizontalStrut(8));
        settings.add(new JLabel("Speed:"));
        settings.add(speedSlider);
        settings.add(Box.createHorizontalGlue());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(buttons, BorderLayout.NORTH);
        wrapper.add(settings, BorderLayout.CENTER);
        return wrapper;
    }

    private void configureSlider(JSlider slider, String tooltip) {
        slider.setToolTipText(tooltip);
        slider.setPreferredSize(new Dimension(120, 24));
        slider.setMaximumSize(new Dimension(120, 24));
    }

    private JComponent buildMazeArea() {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        holder.add(mazePanel, BorderLayout.CENTER);
        return holder;
    }

    private JComponent buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        side.setPreferredSize(new Dimension(400, 100));

        statusLabel.setOpaque(true);
        statusLabel.setBackground(IDLE_BG);
        statusLabel.setForeground(IDLE_FG);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        side.add(statusLabel);
        side.add(Box.createVerticalStrut(10));

        JPanel stats = new JPanel(new GridLayout(6, 2, 6, 6));
        stats.setBorder(BorderFactory.createTitledBorder("Last run"));
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        addStatRow(stats, "Time taken", timeValue);
        addStatRow(stats, "Steps in path", stepsValue);
        addStatRow(stats, "Path cost", costValue);
        addStatRow(stats, "Nodes explored", exploredValue);
        addStatRow(stats, "Peak frontier", frontierValue);
        addStatRow(stats, "Memory used", memoryValue);
        side.add(stats);
        side.add(Box.createVerticalStrut(10));

        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBorder(BorderFactory.createTitledBorder("Legend"));
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);
        legend.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        legend.add(legendItem(START_COLOR, "Start (top left)"));
        legend.add(legendItem(GOAL_COLOR, "Goal (bottom right)"));
        legend.add(legendItem(EXPLORED_COLOR, "Cells the search looked at"));
        legend.add(legendItem(PATH_COLOR, "Solution path"));
        legend.add(legendItem(WALL_COLOR, "Wall (click a cell to toggle)"));
        side.add(legend);
        side.add(Box.createVerticalStrut(10));

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        logArea.setText(MazeSolverMetrics.tableHeader() + "\n");
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("History"));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(scroll);

        return side;
    }

    private void addStatRow(JPanel panel, String label, JLabel value) {
        JLabel name = new JLabel(label);
        name.setForeground(IDLE_FG);
        value.setFont(value.getFont().deriveFont(Font.BOLD));
        panel.add(name);
        panel.add(value);
    }

    private JComponent legendItem(Color color, String text) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel swatch = new JPanel();
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(GRID_COLOR));
        swatch.setPreferredSize(new Dimension(14, 14));
        swatch.setMaximumSize(new Dimension(14, 14));
        row.add(swatch);
        row.add(Box.createHorizontalStrut(8));
        row.add(new JLabel(text));
        row.add(Box.createHorizontalGlue());
        row.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        return row;
    }

    // Actions
    private static void warmUp() {
        for (int i = 0; i < 40; i++) {
            Cell[][] scratch = Random_Maze_Generator.generate(15, 15, 0.1, 0.0, false, (long) i);
            Cell s = Maze_Utils.defaultStart(scratch);
            Cell t = Maze_Utils.defaultGoal(scratch);
            Maze_Solver_A_Star.solve(scratch, s, t);
            Maze_Solver_BFS.solve(scratch, s, t);
            Maze_Solver_DFS.solve(scratch, s, t);
            Maze_Solver_Dijkstras.solve(scratch, s, t);
        }
    }

    private void newMaze() {
        stopAnimation();
        int size = (Integer) sizeSpinner.getValue();
        grid = Random_Maze_Generator.generate(
                size, size,
                loopSlider.getValue() / 100.0,
                blockSlider.getValue() / 100.0,
                terrainCheck.isSelected(),
                null);
        start = Maze_Utils.defaultStart(grid);
        goal = Maze_Utils.defaultGoal(grid);
        clearOverlays();
        resetStats();
        logArea.setText(MazeSolverMetrics.tableHeader() + "\n");
        setStatus("New " + grid.length + " x " + grid[0].length + " maze ready. Pick a search.",
                IDLE_BG, IDLE_FG);
        mazePanel.repaint();
    }

    private void runAlgorithm(String algorithm) {
        stopAnimation();
        clearOverlays();

        MazeSolverMetrics result = dispatch(algorithm);
        showStats(result);
        appendLog(result);

        if (!result.isSolved()) {
            setStatus("<html>No path found with " + escape(algorithm)
                    + ".<br>This maze cannot be solved.</html>", FAIL_BG, FAIL_FG);
        } else {
            setStatus(algorithm + " found a path in " + result.getSteps() + " steps.", OK_BG, OK_FG);
        }

        if (animateCheck.isSelected() && !result.getExplorationOrder().isEmpty()) {
            startAnimation(result);
        } else {
            revealAll(result);
            applyPath(result);
            mazePanel.repaint();
        }
    }

    private void runAll() {
        stopAnimation();
        clearOverlays();

        String[] names = { "A*", "BFS", "DFS", "Dijkstra" };
        logArea.setText(MazeSolverMetrics.tableHeader() + "\n");

        MazeSolverMetrics shown = null;
        boolean anySolved = false;
        for (String name : names) {
            MazeSolverMetrics result = dispatch(name);
            appendLog(result);
            anySolved |= result.isSolved();
            if (shown == null) {
                shown = result;
            }
        }

        if (shown != null) {
            showStats(shown);
            revealAll(shown);
            applyPath(shown);
        }
        mazePanel.repaint();

        if (anySolved) {
            setStatus("<html>All four searches finished.<br>Path drawn is A*'s. See History.</html>",
                    OK_BG, OK_FG);
        } else {
            setStatus("<html>No path found by any search.<br>This maze cannot be solved.</html>",
                    FAIL_BG, FAIL_FG);
        }
    }

    private MazeSolverMetrics dispatch(String algorithm) {
        switch (algorithm) {
            case "A*":
                return Maze_Solver_A_Star.solve(grid, start, goal);
            case "BFS":
                return Maze_Solver_BFS.solve(grid, start, goal);
            case "DFS":
                return Maze_Solver_DFS.solve(grid, start, goal);
            case "Dijkstra":
                return Maze_Solver_Dijkstras.solve(grid, start, goal);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    // Animation and overlays
    private void startAnimation(MazeSolverMetrics result) {
        animating = result;
        revealIndex = 0;

        int total = result.getExplorationOrder().size();
        int frames = Math.max(12, 110 - speedSlider.getValue() * 9);
        int perTick = Math.max(1, total / frames);

        animationTimer = new Timer(16, e -> {
            int target = Math.min(total, revealIndex + perTick);
            while (revealIndex < target) {
                Cell cell = animating.getExplorationOrder().get(revealIndex++);
                revealed[cell.row][cell.col] = true;
            }
            if (revealIndex >= total) {
                stopAnimation();
                applyPath(result);
            }
            mazePanel.repaint();
        });
        animationTimer.start();
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        animating = null;
    }

    private void revealAll(MazeSolverMetrics result) {
        for (Cell cell : result.getExplorationOrder()) {
            revealed[cell.row][cell.col] = true;
        }
    }

    private void applyPath(MazeSolverMetrics result) {
        pathCells = result.getPath();
        for (Cell cell : pathCells) {
            onPath[cell.row][cell.col] = true;
        }
        mazePanel.repaint();
    }

    private void clearOverlays() {
        revealed = new boolean[grid.length][grid[0].length];
        onPath = new boolean[grid.length][grid[0].length];
        pathCells = java.util.Collections.emptyList();
    }

    // Stats plumbing
    private void showStats(MazeSolverMetrics result) {
        timeValue.setText(Maze_Utils.formatDuration(result.getElapsedNanos()));
        stepsValue.setText(result.isSolved() ? String.valueOf(result.getSteps()) : "no path");
        costValue.setText(result.isSolved() ? String.valueOf(result.getPathCost()) : "no path");
        exploredValue.setText(String.valueOf(result.getNodesExplored()));
        frontierValue.setText(String.valueOf(result.getPeakFrontier()));
        memoryValue.setText("~" + Maze_Utils.formatBytes(result.getMemoryBytes()));
    }

    private void resetStats() {
        timeValue.setText("-");
        stepsValue.setText("-");
        costValue.setText("-");
        exploredValue.setText("-");
        frontierValue.setText("-");
        memoryValue.setText("-");
    }

    private void appendLog(MazeSolverMetrics result) {
        logArea.append(result.toTableRow() + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void setStatus(String text, Color background, Color foreground) {
        statusLabel.setText(text);
        statusLabel.setBackground(background);
        statusLabel.setForeground(foreground);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // Maze drawing
    private class MazePanel extends JPanel {

        MazePanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(660, 660));
            setBorder(BorderFactory.createLineBorder(GRID_COLOR));
            setToolTipText("Click a cell to add or remove a wall");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    toggleCellAt(e.getX(), e.getY());
                }
            });
        }

        private int cellSize() {
            return Math.max(2, Math.min(
                    (getWidth() - 2) / grid[0].length,
                    (getHeight() - 2) / grid.length));
        }

        private int offsetX(int cellSize) {
            return (getWidth() - cellSize * grid[0].length) / 2;
        }

        private int offsetY(int cellSize) {
            return (getHeight() - cellSize * grid.length) / 2;
        }

        private void toggleCellAt(int px, int py) {
            if (grid == null) {
                return;
            }
            int size = cellSize();
            int col = (px - offsetX(size)) / size;
            int row = (py - offsetY(size)) / size;
            if (!Maze_Utils.inBounds(grid, row, col)) {
                return;
            }
            Cell cell = grid[row][col];
            if (cell.equals(start) || cell.equals(goal)) {
                return;
            }
            cell.wall = !cell.wall;
            stopAnimation();
            clearOverlays();
            resetStats();
            setStatus("Maze edited. Run a search to see the effect.", IDLE_BG, IDLE_FG);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (grid == null) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = cellSize();
            int ox = offsetX(size);
            int oy = offsetY(size);
            boolean drawGrid = size >= 9;

            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[0].length; c++) {
                    Cell cell = grid[r][c];
                    int x = ox + c * size;
                    int y = oy + r * size;

                    g2.setColor(colorFor(cell, r, c));
                    g2.fillRect(x, y, size, size);

                    if (drawGrid && cell.isOpen()) {
                        g2.setColor(GRID_COLOR);
                        g2.drawRect(x, y, size, size);
                    }
                }
            }

            // Draw the route as a line on top so it reads clearly at any size.
            if (pathCells.size() > 1) {
                g2.setColor(PATH_LINE_COLOR);
                g2.setStroke(new BasicStroke(Math.max(1.6f, size * 0.22f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[] xs = new int[pathCells.size()];
                int[] ys = new int[pathCells.size()];
                for (int i = 0; i < pathCells.size(); i++) {
                    Cell cell = pathCells.get(i);
                    xs[i] = ox + cell.col * size + size / 2;
                    ys[i] = oy + cell.row * size + size / 2;
                }
                g2.drawPolyline(xs, ys, xs.length);
            }

            // Start and goal markers sit above everything else.
            drawMarker(g2, start, START_COLOR, size, ox, oy);
            drawMarker(g2, goal, GOAL_COLOR, size, ox, oy);

            g2.dispose();
        }

        private void drawMarker(Graphics2D g2, Cell cell, Color color, int size, int ox, int oy) {
            if (cell == null) {
                return;
            }
            g2.setColor(color);
            g2.fillOval(ox + cell.col * size + 1, oy + cell.row * size + 1,
                    Math.max(2, size - 2), Math.max(2, size - 2));
        }

        private Color colorFor(Cell cell, int r, int c) {
            if (cell.wall) {
                return WALL_COLOR;
            }
            if (onPath[r][c]) {
                return PATH_COLOR;
            }
            if (revealed[r][c]) {
                return EXPLORED_COLOR;
            }
            if (cell.cost > 1) {
                
                float t = (cell.cost - 1) / 8f;
                return blend(OPEN_COLOR, TERRAIN_COLOR, t);
            }
            return OPEN_COLOR;
        }

        private Color blend(Color a, Color b, float t) {
            return new Color(
                    Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                    Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                    Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                
            }
            new Maze_GUI().setVisible(true);
        });
    }
}