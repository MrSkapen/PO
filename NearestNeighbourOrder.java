import java.util.ArrayList;

public class NearestNeighbourOrder extends Calculate{

    MCSimulation.LatticeParametersImpl latticeParametersImpl;

    public NearestNeighbourOrder(MCSimulation.LatticeParametersImpl latticeParametersImpl, int magnetsCount) {
        this.latticeParametersImpl = latticeParametersImpl;
        this.magnetsCount = magnetsCount;
    }

    int magnetsCount;


    @Override
    double calculate() {
        int[][] lattice = latticeParametersImpl.lattice();
        NeighboursStates neighboursStatesObject = new NeighboursStates();
        AngleInRadians angleInRadians = new AngleInRadians();
        neighboursStatesObject.setParameters(lattice, 0, 0, 1);
        ArrayList<Integer> neighboursStates = neighboursStatesObject.getNeighboursStates();
        double onn = 1. / (double) (magnetsCount * neighboursStates.size());
        double iSum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                double jSum = 0;
                neighboursStatesObject.setParameters(lattice, 0, 0, 1);
                neighboursStates = neighboursStatesObject.getNeighboursStates();
                for (int j = 0; j < neighboursStates.size(); j++) {
                    angleInRadians.setAngle(lattice[i_row][i_col], latticeParametersImpl.states());
                    double alphaI = angleInRadians.calculate();
                    angleInRadians.setAngle(neighboursStates.get(j), latticeParametersImpl.states());
                    double alphaJ = angleInRadians.calculate();
                    jSum += Math.cos(alphaI - alphaJ);
                }
                iSum += jSum;
            }
        }
        onn *= iSum;
        return onn;
    }
}
