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

📊 Metrics

Example metrics displayed by the application:

Metric	Description
Execution Time	Time required by the algorithm to complete
Path Length	Number of steps in the resulting path
Nodes Explored	Number of nodes examined during the search
Path Cost	Total cost of traversing the resulting path
Memory Usage	Approximate memory used during execution
Status	Indicates whether a solution was found
🛠️ Technologies
Java
Java Swing
Data Structures & Algorithms
Graph Traversal
Pathfinding
Object-Oriented Programming

A* combines the actual cost of reaching a node with a heuristic estimate of the remaining distance to the goal.

💻 Requirements
JDK 8 or higher
IntelliJ IDEA, VS Code, Eclipse, or another Java IDE
Windows, Linux, or macOS
🚀 Installation
1. Clone the repository
git clone https://github.com/sorashree/Maze-Solver.git
2. Open the project

Open the cloned project folder in your preferred Java IDE.

3. Compile the project

From the project root:

javac -d out src\*.java
4. Run the application
java -cp out Maze_GUI

You can also run the Maze_GUI class directly from your IDE.

🎮 Usage
Launch Maze_GUI.
Generate a new maze.
Adjust the maze size and generation parameters.
Select a pathfinding algorithm.
Run the algorithm.
Observe the search process and solution path.
Review the performance metrics.
Use Compare All to compare the algorithms.
Use Clear Path to reset the current solution.
🎯 Learning Objectives

This project demonstrates practical implementation of:

Graph traversal algorithms
Shortest-path algorithms
Heuristic search
Queues and stacks
Priority queues
Grid-based graph representation
Path reconstruction
Algorithm visualization
Performance measurement
Java GUI development
Object-oriented programming
🔮 Future Improvements

Possible future extensions include:

Additional maze-generation algorithms
Greedy Best-First Search
Interactive Start/Goal placement
Performance graphs and charts
Maze difficulty presets
Exportable algorithm comparison results
Larger-scale maze benchmarking
Additional visualization modes

```text
f(n) = g(n) + h(n)
