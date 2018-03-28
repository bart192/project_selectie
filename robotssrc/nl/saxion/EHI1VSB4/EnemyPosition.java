package nl.saxion.EHI1VSB4;

public class EnemyPosition implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private double x = 0.0;
    private double y = 0.0;

    public EnemyPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

