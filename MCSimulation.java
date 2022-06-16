import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.Random;

public class MCSimulation implements main.Simulation {
    private LatticeParametersImpl latticeParametersImpl = new LatticeParametersImpl();
    private NeighboursStates neighboursStatesObject = new NeighboursStates();
    private ProbabilityFormula formula;
    private double TkB;
    private double Ce;
    private List<Double> Cn;
    private double externalFieldAngle;
    private int magnetsCount;

    public MCSimulation() {
    }

    @Override
    public void setLattice(int[][] lattice, int states) {
        latticeParametersImpl.setStates(states);
        latticeParametersImpl.setLattice(lattice);
        magnetsCount = lattice.length * lattice.length;
    }

    @Override
    public void setEnergyParameters(List<Double> parameters, double externaFieldAngle) {
        Ce = parameters.get(0);
        Cn = parameters;
        externalFieldAngle = externaFieldAngle;
    }

    @Override
    public void setProbabilityFormula(ProbabilityFormula formula) {
        this.formula = formula;
    }

    @Override
    public void setTkB(double TkB) {
        this.TkB = TkB;
    }

    @Override
    public LatticeParameters getState() {
        return latticeParametersImpl;
    }

    @Override
    public void executeMCSteps(int steps) {
        double totalEnergy = latticeParametersImpl.totalEnergy();
        latticeParametersImpl.setTotalEnergy(totalEnergy);
        double acceptances = 0;
        double acceptanceRatio = 0;
        Random random;
        for (int step = 0; step < steps; step++) {
            if (step > 0) {
                acceptanceRatio = acceptances / step;
            }
            double deltaE = 0;
            random = new Random();
            int magnetRowRandom = random.nextInt((int) Math.sqrt(magnetsCount));
            int magnetColRandom = random.nextInt((int) Math.sqrt(magnetsCount));
            int[][] newLattice = generateLatticeCopy(latticeParametersImpl.lattice());
            DeltaCalculate deltaCalculate = null;
            if (acceptanceRatio > 0.5) {
                deltaCalculate = new BiggerDelta(random,magnetsCount,magnetRowRandom,magnetColRandom,latticeParametersImpl,Cn,Ce,newLattice,externalFieldAngle);
            } else {
               deltaCalculate = new SmallerDelta(random,magnetsCount,Cn,latticeParametersImpl,newLattice);
            }
            deltaE = deltaCalculate.calculate();
            double R = random.nextDouble();
            double P = calculateP(formula, deltaE, TkB);
            if (R < P) {
                totalEnergy += deltaE;
                latticeParametersImpl.setTotalEnergy(totalEnergy);
                latticeParametersImpl.setLattice(newLattice);
                acceptances++;
            }
        }
    }

    private double calculateEi(int[][] lattice, int i_row, int i_col) {
        double Ei = 0;
        for (int n = 1; n < Cn.size(); n++) {
            neighboursStatesObject.setParameters(lattice, i_row, i_col, n);
            ArrayList<Integer> neighboursStates = neighboursStatesObject.getNeighboursStates();
            for (int j = 0; j < neighboursStates.size(); j++) {
                double alphaI = getAngleInRadians(lattice[i_row][i_col]);
                double alphaJ = getAngleInRadians(neighboursStates.get(j));
                Ei -= Cn.get(n) * Math.cos(alphaI - alphaJ);
            }
        }
        return Ei;
    }

    private double calculateTotalEnergy(int[][] lattice) {
        double totalEnergy = -0.5;
        double nSum = 0;
        for (int n = 1; n < Cn.size(); n++) {
            double iSum = 0;
            for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
                for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                    neighboursStatesObject.setParameters(lattice, i_row, i_col, n);
                    ArrayList<Integer> neighboursStates = neighboursStatesObject.getNeighboursStates();
                    double jSum = 0;
                    for (int j = 0; j < neighboursStates.size(); j++) {
                        double alphaI = getAngleInRadians(lattice[i_row][i_col]);
                        double alphaJ = getAngleInRadians(neighboursStates.get(j));
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
                double alphaI = getAngleInRadians(lattice[i_row][i_col]);
                iSum += Math.cos(alphaI - externalFieldAngle);
            }
        }
        subtract *= iSum;
        totalEnergy -= subtract;
        return totalEnergy;
    }

    private double getAngleInRadians(int magnetState) {
        return 2 * Math.PI * magnetState / latticeParametersImpl.states();
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

    private double calculateOrderParameter() {
        int[][] lattice = latticeParametersImpl.lattice();
        double xAvg = 1. / magnetsCount;
        double sum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                sum += Math.cos(getAngleInRadians(lattice[i_row][i_col]));
            }
        }
        xAvg *= sum;
        double yAvg = 1. / magnetsCount;
        sum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                sum += Math.sin(getAngleInRadians(lattice[i_row][i_col]));
            }
        }
        yAvg *= sum;
        return Math.sqrt(xAvg * xAvg + yAvg * yAvg);
    }

    private double calculateNearestNeighbourOrder() {
        int[][] lattice = latticeParametersImpl.lattice();
        neighboursStatesObject.setParameters(lattice, 0, 0, 1);
        ArrayList<Integer> neighboursStates = neighboursStatesObject.getNeighboursStates();
        double onn = 1. / (double) (magnetsCount * neighboursStates.size());
        double iSum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                double jSum = 0;
                neighboursStatesObject.setParameters(lattice, i_row, i_col, 1);
                neighboursStates = neighboursStatesObject.getNeighboursStates();
                for (int j = 0; j < neighboursStates.size(); j++) {
                    double alphaI = getAngleInRadians(lattice[i_row][i_col]);
                    double alphaJ = getAngleInRadians(neighboursStates.get(j));
                    jSum += Math.cos(alphaI - alphaJ);
                }
                iSum += jSum;
            }
        }
        onn *= iSum;
        return onn;
    }

    private double calculateP(ProbabilityFormula formula, double deltaE, double kBT) {
        if (formula == ProbabilityFormula.GLAUBER) {
            return Math.exp(-deltaE / kBT) / (1 + Math.exp(-deltaE / kBT));
        } else {
            if (deltaE > 0) {
                return Math.exp(-deltaE / kBT);
            } else {
                return 1;
            }
        }
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

    public class LatticeParametersImpl implements LatticeParameters {

        private double _totalEnergy = 0;
        private int[][] _lattice;
        private int _states;

        @Override
        public double totalEnergy() {
            if (_totalEnergy == 0) {
                _totalEnergy = calculateTotalEnergy(_lattice);
            }
            return _totalEnergy;
        }

        @Override
        public double orderParameter() {
            return calculateOrderParameter();
        }

        @Override
        public double nearestNeighbourOrder() {
            return calculateNearestNeighbourOrder();
        }

        @Override
        public int[][] lattice() {
            return _lattice;
        }

        protected int states() {
            return _states;
        }

        protected void setLattice(int[][] lattice) {
            _lattice = new int[lattice.length][lattice.length];
            for (int i = 0; i < lattice.length; i++) {
                for (int j = 0; j < lattice.length; j++) {
                    _lattice[i][j] = lattice[i][j];
                }
            }
        }

        protected void setStates(int states) {
            _states = states;
        }

        protected void setTotalEnergy(double totalEnergy) {
            _totalEnergy = totalEnergy;
        }
    }
}