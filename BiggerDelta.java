import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiggerDelta extends DeltaCalculate{
    int magnetRowRandom;
    int magnetColRandom;

    public BiggerDelta(Random random, int magnetsCount, int magnetRowRandom, int magnetColRandom, MCSimulation.LatticeParametersImpl latticeParametersImpl, List<Double> cn, double ce, int[][] newLattice, double externalFieldAngle) {
        this.random = random;
        this.magnetsCount = magnetsCount;
        this.magnetRowRandom = magnetRowRandom;
        this.magnetColRandom = magnetColRandom;
        this.latticeParametersImpl = latticeParametersImpl;
        Cn = cn;
        Ce = ce;
        this.newLattice = newLattice;
        this.externalFieldAngle = externalFieldAngle;
    }

    MCSimulation.LatticeParametersImpl latticeParametersImpl;
    List<Double> Cn;
    double Ce;
    int[][] newLattice;
    double externalFieldAngle;


    @Override
    double calculate() {
        double deltaE;
        double totalEnergy = latticeParametersImpl.totalEnergy();
        random = new Random();
        int magnetRowRandom2 = random.nextInt((int) Math.sqrt(magnetsCount));
        int magnetColRandom2 = random.nextInt((int) Math.sqrt(magnetsCount));
        while (magnetRowRandom == magnetRowRandom2 && magnetColRandom == magnetColRandom2) {
            magnetRowRandom2 = random.nextInt((int) Math.sqrt(magnetsCount));
            magnetColRandom2 = random.nextInt((int) Math.sqrt(magnetsCount));
        }
        int magnetStateChange = random.nextBoolean() ? 1 : -1;
        int magnetStateChange2 = random.nextBoolean() ? 1 : -1;
        changeMagnetState(newLattice, magnetRowRandom, magnetColRandom, magnetStateChange);
        changeMagnetState(newLattice, magnetRowRandom2, magnetColRandom2, magnetStateChange2);
        deltaE = calculateTotalEnergy(newLattice) - totalEnergy;
        return deltaE;
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

    private double calculateTotalEnergy(int[][] lattice) {
        NeighboursStates neighboursStatesObject = new NeighboursStates();
        AngleInRadians angleInRadiansObject = new AngleInRadians();

        double totalEnergy = -0.5;
        double nSum = 0;
        for (int n = 1; n < Cn.size(); n++) {
            double iSum = 0;
            for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
                for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                    neighboursStatesObject.setParameters(lattice, i_row, i_col, n);
                    ArrayList<Integer> neighboursStates = neighboursStatesObject.getNeighboursStates();
                    double jSum = 0;
                    for (Integer neighboursState : neighboursStates) {
                        angleInRadiansObject.setAngle(lattice[i_row][i_col], latticeParametersImpl.states());
                        double alphaI = angleInRadiansObject.calculate();
                        angleInRadiansObject.setAngle(neighboursState, latticeParametersImpl.states());
                        double alphaJ = angleInRadiansObject.calculate();
                        jSum += Math.cos(alphaI - alphaJ);
                    }
                    iSum += jSum;
                }
            }
            iSum *= Cn.get(n);
            nSum += iSum;
        }
        totalEnergy *= nSum;
        double subtract = Ce;
        double iSum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                angleInRadiansObject.setAngle(lattice[i_row][i_col],latticeParametersImpl.states());
                double alphaI = angleInRadiansObject.calculate();
                iSum += Math.cos(alphaI - externalFieldAngle);
            }
        }
        subtract *= iSum;
        totalEnergy -= subtract;
        return totalEnergy;
    }

}
