public class AngleInRadians {
    int magnetState;
    int states;

    public void setAngle(int magnetState,int states) {
        this.magnetState = magnetState;
        this.states = states;
    }
    public AngleInRadians() {
    }
    public double calculate() {
        return 2 * Math.PI * magnetState / states;
    }
}
