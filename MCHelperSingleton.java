public class MCHelperSingleton {
    private static MCHelperSingleton instance;
    private MCHelperSingleton() {};
    public static MCHelperSingleton getInstance() {
        if (instance == null) {
            instance = new MCHelperSingleton();
        }
        return instance;
    }

    public double getAngleInRadians(int magnetState, int states) {
        return 2 * Math.PI * magnetState / states;
    }

    public int[][] generateLatticeCopy(int[][] lattice) {
        int[][] latticeCopy = new int[lattice.length][lattice.length];
        for (int i = 0; i < lattice.length; i++) {
            for (int j = 0; j < lattice.length; j++) {
                latticeCopy[i][j] = lattice[i][j];
            }
        }
        return latticeCopy;
    }
}
