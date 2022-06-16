import java.util.ArrayList;

public class Strategy {
    private int[][] lattice;
    private int magnetRow;
    private int magnetCol;

    private NeighboursStatesStrategy strategy;

    public void setLattice(int[][] lattice) {
        this.lattice = lattice;
    }

    public void setMagnetRow(int magnetRow) {
        this.magnetRow = magnetRow;
    }

    public void setMagnetCol(int magnetCol) {
        this.magnetCol = magnetCol;
    }

    public void setStrategy(NeighboursStatesStrategy strategy) {
        this.strategy = strategy;
    }

    public ArrayList<Integer> executeStrategy(){
        return strategy.getNeighboursStates(lattice,magnetRow,magnetCol);
    }
}
