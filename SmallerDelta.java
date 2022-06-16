import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SmallerDelta extends DeltaCalculate{


    public SmallerDelta(Random random, int magnetsCount, List<Double> cn, MCSimulation.LatticeParametersImpl latticeParametersImpl) {
        this.random = random;
        this.magnetsCount = magnetsCount;
        Cn = cn;
        this.latticeParametersImpl = latticeParametersImpl;
    }

    private List<Double> Cn;

    MCSimulation.LatticeParametersImpl latticeParametersImpl;

    @Override
    double calculate() {
        double deltaE;
        int magnetRowRandom = random.nextInt((int) Math.sqrt(magnetsCount));
        int magnetColRandom = random.nextInt((int) Math.sqrt(magnetsCount));
        int[][] newLattice = generateLatticeCopy(latticeParametersImpl.lattice());
        int magnetStateChange = random.nextBoolean() ? 1 : -1;
        changeMagnetState(newLattice, magnetRowRandom, magnetColRandom, magnetStateChange);
        deltaE = calculateEi(newLattice, magnetRowRandom, magnetColRandom) - calculateEi(latticeParametersImpl.lattice(), magnetRowRandom, magnetColRandom);
        return deltaE;
    }

    private int[][] generateLatticeCopy(int[][] lattice) {
        int[][] latticeCopy = new int[lattice.length][lattice.length];
        for (int i = 0; i < lattice.length; i++) {
            for (int j = 0; j < lattice.length; j++) {
                latticeCopy[i][j] = lattice[i][j];
            }
        }
        return latticeCopy;
    }

    private void changeMagnetState(int[][] lattice, int magnetRow, int magnetCol, int change) {
        int availableStates = latticeParametersImpl.states();
        int currentState = lattice[magnetRow][magnetCol];
        if (currentState + change >= availableStates || currentState + change < 0) {
            if (currentState + change >= availableStates) {
                lattice[magnetRow][magnetCol] = currentState + change - availableStates;
            } else {
                lattice[magnetRow][magnetCol] = availableStates + change;
            }
        } else {
            lattice[magnetRow][magnetCol] += change;
        }
    }

    private double calculateEi(int[][] lattice, int i_row, int i_col) {
        double Ei = 0;
        NeighboursStates neighboursStatesObject = new NeighboursStates();
        AngleInRadians angleInRadiansObject = new AngleInRadians();
        for (int n = 1; n < Cn.size(); n++) {
            neighboursStatesObject.setParameters(lattice, i_row, i_col, n);
            ArrayList<Integer> neighboursStates = neighboursStatesObject.getNeighboursStates();
            for (int j = 0; j < neighboursStates.size(); j++) {
                angleInRadiansObject.setAngle(lattice[i_row][i_col], latticeParametersImpl.states());
                double alphaI = angleInRadiansObject.calculate();
                angleInRadiansObject.setAngle(neighboursStates.get(j), latticeParametersImpl.states());
                double alphaJ = angleInRadiansObject.calculate();
                Ei -= Cn.get(n) * Math.cos(alphaI - alphaJ);
            }
        }
        return Ei;
    }
}
