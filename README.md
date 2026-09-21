# 🧩 Maze Solver

> **Interactive Java application for generating, visualizing, and comparing maze pathfinding algorithms.**

Maze Solver is a Java-based interactive application that generates customizable random mazes and visualizes multiple pathfinding algorithms through a graphical user interface.

The project is designed to demonstrate the practical application of **Data Structures and Algorithms**, while providing performance metrics that allow different algorithms to be observed and compared on the same maze.

---

## ✨ Features

### 🗺️ Maze Generation

- Generate random mazes
- Customize maze size
- Control blocked cells and maze complexity
- Support terrain-based cell costs
- Start and Goal node visualization
- Regenerate mazes instantly

### 🔎 Pathfinding Algorithms

The application supports four major pathfinding algorithms:

- **A\* Search**
- **Breadth-First Search (BFS)**
- **Depth-First Search (DFS)**
- **Dijkstra's Algorithm**

### 📊 Visualization & Analysis

- Animated algorithm execution
- Adjustable search speed
- Visual exploration of nodes
- Solution-path visualization
- Unsolvable-maze detection
- Compare multiple algorithms
- Clear and regenerate paths
- Algorithm execution history

### 📈 Performance Metrics

For each algorithm, the application tracks:

- Execution time
- Path length / steps
- Nodes explored
- Path cost
- Memory usage
- Solution status

---

## 🖥️ Application Preview

![Maze Solver GUI](assets/maze-solver-gui.png)

The GUI provides controls for maze generation, algorithm selection, animation speed, terrain configuration, and algorithm comparison.

---

## 🧠 Algorithms

### A* Search

A* combines the actual cost of reaching a node with a heuristic estimate of the remaining distance to the goal.

It uses:

```text
f(n) = g(n) + h(n)
