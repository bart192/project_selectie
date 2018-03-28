package nl.saxion.EHI1VSB4.Models;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class EnemyBot {
    private double x;
    private double y;
    private double bearing;
    private double distance;
    private double energy;
    private double heading;
    private double velocity;
    String name;

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public EnemyBot() {
        this.reset();
    }

    public void update(ScannedRobotEvent e, Robot robot) {
        this.update(e);
        double absBearingDeg = robot.getHeading() + e.getBearing();
        if (absBearingDeg < 0.0D) {
            absBearingDeg += 360.0D;
        }

        this.x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
        this.y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
    }

    public double getFutureX(long when) {
        return this.x + Math.sin(Math.toRadians(this.getHeading())) * this.getVelocity() * (double)when;
    }

    public double getFutureY(long when) {
        return this.y + Math.cos(Math.toRadians(this.getHeading())) * this.getVelocity() * (double)when;
    }

    public double getBearing() {
        return this.bearing;
    }

    public double getDistance() {
        return this.distance;
    }

    public double getEnergy() {
        return this.energy;
    }

    public double getHeading() {
        return this.heading;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public String getName() {
        return this.name;
    }

    public void update(ScannedRobotEvent bot) {
        this.bearing = bot.getBearing();
        this.distance = bot.getDistance();
        this.energy = bot.getEnergy();
        this.heading = bot.getHeading();
        this.velocity = bot.getVelocity();
        this.name = bot.getName();
    }

    public void reset() {
        this.bearing = 0.0D;
        this.distance = 0.0D;
        this.energy = 0.0D;
        this.heading = 0.0D;
        this.velocity = 0.0D;
        this.name = null;
    }

    public Boolean none() {
        return this.name != null && this.name != "" ? false : true;
    }
}
