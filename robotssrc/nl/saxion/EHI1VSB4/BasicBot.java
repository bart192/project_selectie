package nl.saxion.EHI1VSB4;

import org.apache.bcel.generic.NEW;
import robocode.*;
import robocode.util.Utils;
import sampleteam.Point;
import sampleteam.RobotColors;

import java.awt.*;
import java.io.IOException;

public class BasicBot extends TeamRobot{

    private RobotStatus robotStatus;

    @Override
    public void run() {
        super.run();

        setRobotColors();

        while (true) {
            turnGunRight(360);
            ahead(200);
            turnLeft(180);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        this.robotStatus = e.getStatus();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Calculate the angle to the scanned robot
        double angle = getAngleOfScannedRobot(e);

        // Calculate the coordinates of the robot
        double enemyX = getEnemyX(e, angle);
        double enemyY = getEnemyY(e, angle);

        // Send enemy position to teammates
        sendBroadcastMessage(new EnemyPosition(enemyX, enemyY));

        if (isTeammate(e.getName())) {
            return;
        }

        moveToEnemyPos(e);

    }

    private void moveToEnemyPos(ScannedRobotEvent e) {
        setTurnRightRadians(Utils.normalRelativeAngle(getAngleOfScannedRobot(e) - getHeadingRadians()));
        setAhead(100);
        fire(100);
    }

    private void setRobotColors() {
        // Prepare RobotColors object
        TeamColors c = new TeamColors();

        Color brown = new Color(139,69,19);

        c.bodyColor = Color.black;
        c.gunColor = Color.red;
        c.radarColor = Color.white;
        c.scanColor = Color.pink;
        c.bulletColor = brown;

        // Set the color of this robot containing the RobotColors
        setBodyColor(c.bodyColor);
        setGunColor(c.gunColor);
        setRadarColor(c.radarColor);
        setScanColor(c.scanColor);
        setBulletColor(c.bulletColor);
    }

    private void sendBroadcastMessage(EnemyPosition p) {
        try {
            broadcastMessage(p);
        } catch (IOException e) {
            out.println("Unable to send order: ");
            e.printStackTrace(out);
        }
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
