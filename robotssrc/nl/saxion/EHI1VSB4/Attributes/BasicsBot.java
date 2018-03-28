package nl.saxion.EHI1VSB4.Attributes;

public class BasicsBot {
    private String name;
    private double x;
    private double y;

    public BasicsBot(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return this.name;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getbearing(double x, double y) {
        double S = Math.sqrt(Math.pow(this.x - x, 2.0D) + Math.pow(this.y - y, 2.0D));
        double O = this.x - x;
        double angle = Math.toDegrees(Math.asin(O / S));
        if (this.x > x && this.y > y) {
            angle += 0.0D;
        } else if (this.y < y) {
            angle = 180.0D - angle;
        } else if (this.x < x && this.y > y) {
            angle += 360.0D;
        }

        return angle;
    }

    public double getDistance(double x, double y) {
        double a = Math.pow(x - this.x, 2.0D);
        double b = Math.pow(y - this.y, 2.0D);
        return Math.sqrt(a + b);
    }

    public double getTeamDistance(BasicsBot teammate, BasicsBot enemy) {
        double c1 = this.getDistance(enemy.getX(), enemy.getY());
        double c2 = teammate.getDistance(enemy.getX(), enemy.getY());
        return c1 + c2;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
