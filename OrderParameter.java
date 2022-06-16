public class OrderParameter extends Calculate{
    MCSimulation.LatticeParametersImpl latticeParametersImpl;

    public OrderParameter(MCSimulation.LatticeParametersImpl latticeParametersImpl, int magnetsCount) {
        this.latticeParametersImpl = latticeParametersImpl;
        this.magnetsCount = magnetsCount;
    }

    int magnetsCount;

    public void setLatticeParametersImpl(MCSimulation.LatticeParametersImpl latticeParametersImpl) {
        this.latticeParametersImpl = latticeParametersImpl;
    }

    public void setMagnetsCount(int magnetsCount) {
        this.magnetsCount = magnetsCount;
    }

    @Override
    double calculate() {
        AngleInRadians angleInRadians = new AngleInRadians();
        int[][] lattice = latticeParametersImpl.lattice();
        double xAvg = 1. / magnetsCount;
        double sum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                angleInRadians.setAngle(lattice[i_row][i_col],latticeParametersImpl.states());
                sum += Math.cos(angleInRadians.calculate());
            }
        }
        xAvg *= sum;
        double yAvg = 1. / magnetsCount;
        sum = 0;
        for (int i_row = 0; i_row < Math.sqrt(magnetsCount); i_row++) {
            for (int i_col = 0; i_col < Math.sqrt(magnetsCount); i_col++) {
                angleInRadians.setAngle(lattice[i_row][i_col],latticeParametersImpl.states());
                sum += Math.sin(angleInRadians.calculate());
            }
        }
        yAvg *= sum;
        return Math.sqrt(xAvg * xAvg + yAvg * yAvg);
    }
}
