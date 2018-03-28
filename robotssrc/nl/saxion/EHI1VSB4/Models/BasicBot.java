package nl.saxion.EHI1VSB4.Models;

import nl.saxion.EHI1VSB4.EnemyPosition;
import nl.saxion.EHI1VSB4.TeamColors;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.io.IOException;

public class BasicBot extends TeamRobot{
    private int wallMargin = 45;
    private int tooCloseToWall = 0;
    private RobotStatus robotStatus;
    boolean isLeader = false;

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        super.run();
        setLeader();
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
        if (isLeader) {
            // Calculate the angle to the scanned robot
            double angle = getAngleOfScannedRobot(e);

            // Calculate the coordinates of the robot
            double enemyX = getEnemyX(e, angle);
            double enemyY = getEnemyY(e, angle);

            // Send enemy position to teammates
            sendBroadcastMessage(new EnemyPosition(enemyX, enemyY));

            if (isTeammate(e.getName())) {
                setTurnRight(180);
                return;
            }

            moveToEnemyPos(e);
        }
    }

    private void moveToEnemyPos(ScannedRobotEvent e) {
        setTurnRightRadians(Utils.normalRelativeAngle(getAngleOfScannedRobot(e) - getHeadingRadians()));
        setAhead(100);
        fire(100);
    }

    public void setLeader() {
        String[] teammates = getTeammates();
        if (teammates != null) {
            for (int i = 0; i < teammates.length; i++) {
                switch (i) {
                    case 0: {
                        isLeader = true;
                        break;
                    }
                }
            }
        }
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
