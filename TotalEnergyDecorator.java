public class TotalEnergyDecorator implements TotalEnergy {
    private TotalEnergy wrappee;

    public TotalEnergyDecorator(TotalEnergy totalEnergy) {
        wrappee = totalEnergy;
    }

    @Override
    public double calculate(int [][] lattice, MCSimulation.LatticeParametersImpl latticeParametersImpl,  double currentResult) {
        return wrappee.calculate(lattice, latticeParametersImpl, currentResult);
    }
}
