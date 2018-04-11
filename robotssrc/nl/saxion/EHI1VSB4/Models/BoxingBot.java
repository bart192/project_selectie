package nl.saxion.EHI1VSB4.Models;

import nl.saxion.EHI1VSB4.EnemyPosition;
import nl.saxion.EHI1VSB4.TeamColors;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.io.IOException;

public class BoxingBot extends TeamRobot {
    private int moveDirection=1;//which way to move
    private RobotStatus robotStatus;
    private boolean enemyLock = false;


    @Override
    public void run() {
        super.run();
        setRobotColors();

        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        turnRadarRightRadians(Double.POSITIVE_INFINITY);
        do {
            turnRadarRight(360);
            scan();
        } while (true);
    }

    @Override
    public void onStatus(StatusEvent e) {
        this.robotStatus = e.getStatus();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        double radarTurn =
                // Absolute bearing to target
                getHeadingRadians() + e.getBearingRadians()
                        // Subtract current radar heading to get turn required
                        - getRadarHeadingRadians();
        if (!isTeammate(e.getName())) {
            setTurnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

            // Send enemy position to teammates
            sendBroadcastMessage(e);
            moveToEnemyPos(e);
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        setTurnRight(180);
    }

    @Override
    public void onDeath(DeathEvent event) {
        super.onDeath(event);
        turnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    private void moveToEnemyPos(ScannedRobotEvent e) {
        setTurnRightRadians(Utils.normalRelativeAngle(getAngleOfScannedRobot(e) - getHeadingRadians()));
        setAhead(100);
        fire(100);
    }

    //region Communication
    private void sendBroadcastMessage(ScannedRobotEvent p) {
        try {
            broadcastMessage("The enemy is coming!");
        } catch (IOException e) {
            out.println("Unable to send order: ");
            e.printStackTrace(out);
        }
    }


    @Override
    public void onMessageReceived(MessageEvent event) {
        super.onMessageReceived(event);
        String message = (String) event.getMessage();
        out.println(message);

        scan();
    }
    //endregion


    //region Robot colors
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
    //endregion


    //region Helper methods
    private double getAngleOfScannedRobot(ScannedRobotEvent e) {
        return Math.toRadians((robotStatus.getHeading() + e.getBearing() % 360));
    }

    private double getEnemyX(ScannedRobotEvent e, double angle) {
        return (robotStatus.getX() + Math.sin(angle) * e.getDistance());
    }

    private double getEnemyY(ScannedRobotEvent e, double angle) {
        return (robotStatus.getY() + Math.cos(angle) * e.getDistance());
    }
    //endregion
}
