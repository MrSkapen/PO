import java.util.ArrayList;

public class NeighboursStates {
    int[][] lattice; int magnetRow;

    public void setParameters(int[][] lattice, int magnetRow, int magnetCol, int level) {
        this.lattice = lattice;
        this.magnetRow = magnetRow;
        this.magnetCol = magnetCol;
        this.level = level;
    }

    int magnetCol; int level;

    public NeighboursStates() {}

    public ArrayList<Integer> getNeighboursStates() {
        final Strategy strategy = new Strategy();
        strategy.setLattice(lattice);
        strategy.setMagnetRow(magnetRow);
        strategy.setMagnetCol(magnetCol);
        if (level == 1) {
            strategy.setStrategy(new Level1Strategy());
        } else if (level == 2) {
            strategy.setStrategy(new Level2Strategy());
        } else if (level == 3) {
            strategy.setStrategy(new Level3Strategy());
        } else if (level == 4) {
            strategy.setStrategy(new Level4Strategy());
        } else if (level == 5) {
            strategy.setStrategy(new Level5Strategy());
        }
        return strategy.executeStrategy();
    }
}
