public class AngleInRadians implements ProxyInterface{
    int magnetState;
    int states;

    public void setAngle(int magnetState,int states) {
        this.magnetState = magnetState;
        this.states = states;
    }
    public AngleInRadians() {
    }

    @Override
    public double calculate() {
        return 2 * Math.PI * magnetState / states;
    }
}
