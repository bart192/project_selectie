package nl.saxion.EHI1VSB4;

import robocode.AdvancedRobot;
import robocode.RobotStatus;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class BasicBot extends TeamRobot{

    private RobotStatus robotStatus;

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Calculate the angle to the scanned robot
        double angle = getAngleOfScannedRobot(e);

        // Calculate the coordinates of the robot
        double enemyX = getEnemyX(e, angle);
        double enemyY = getEnemyY(e, angle);


    }

    private double getAngleOfScannedRobot(ScannedRobotEvent e) {
        return Math.toRadians((robotStatus.getHeading() + e.getBearing() % 360));
    }

    private double getEnemyX(ScannedRobotEvent e, double angle) {
        return (robotStatus.getX() + Math.sin(angle) * e.getDistance());
    }

    private double getEnemyY(ScannedRobotEvent e, double angle) {
        return (robotStatus.getY() + Math.cos(angle) * e.getDistance());
    }
}
