public class Cell {
 
    public final int row;
    public final int col;
 
    public boolean wall;
 
    public int cost = 1;
 
    public boolean visited;
 
    public int g = Integer.MAX_VALUE;
 
    public int f = Integer.MAX_VALUE;
 
    public Cell parent;
 
    public Cell(int row, int col, boolean wall) {
        this.row = row;
        this.col = col;
        this.wall = wall;
    }
 
    public boolean isOpen() {
        return !wall;
    }
 
    public void resetSearchState() {
        visited = false;
        g = Integer.MAX_VALUE;
        f = Integer.MAX_VALUE;
        parent = null;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell other = (Cell) o;
        return row == other.row && col == other.col;
    }
 
    @Override
    public int hashCode() {
        return row * 31 + col;
    }
 
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
 
