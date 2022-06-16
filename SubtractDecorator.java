public class SubtractDecorator extends TotalEnergyDecorator {

    public SubtractDecorator(TotalEnergy totalEnergy) {
        super(totalEnergy);
    }

    @Override
    public double calculate(int [][] lattice, MCSimulation.LatticeParametersImpl latticeParametersImpl, double currentResult) {
        return subtract(lattice, latticeParametersImpl, super.calculate(lattice, latticeParametersImpl, currentResult));
    }

    private double subtract(int [][] lattice, MCSimulation.LatticeParametersImpl latticeParametersImpl, double currentResult) {
        int magnetsCount = lattice.length * lattice.length;
        double subtract = latticeParametersImpl.Cn().get(0);
        double iSum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                double alphaI = MCHelperSingleton.getInstance().getAngleInRadians(lattice[i_row][i_col], latticeParametersImpl.states());
                iSum += Math.cos(alphaI - latticeParametersImpl.externalFieldAngle());
            }
        }
        subtract *= iSum;
        currentResult -= subtract;
        return currentResult;
    }
}
