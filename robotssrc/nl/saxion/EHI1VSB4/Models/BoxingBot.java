package nl.saxion.EHI1VSB4.Models;

import nl.saxion.EHI1VSB4.EnemyPosition;
import nl.saxion.EHI1VSB4.TeamColors;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.io.IOException;

public class BoxingBot extends TeamRobot {
    private RobotStatus robotStatus;
    private boolean enemyRobotLocked = false;

    public BoxingBot() {
    }

    //region Override methods
    @Override
    public void run() {
        super.run();
        setRobotColors();

        while (!enemyRobotLocked) {
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
        if (!enemyRobotLocked) {
            if (!isTeammate(e.getName())) {
                enemyRobotLocked = true;
                sendBroadcastMessage(e);
            }
        } else  {
            moveToEnemyPos(e);
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);

        if (!isTeammate(event.getName())) {
            enemyRobotLocked = false;
        }

    }

    //endregion


    //region Communication methods
    private void sendBroadcastMessage(ScannedRobotEvent latestScanEvent) {
        try {
            broadcastMessage(latestScanEvent);
        } catch (IOException e) {
            out.println("Unable to send order: ");
            e.printStackTrace(out);
        }
    }


    @Override
    public void onMessageReceived(MessageEvent event) {
        super.onMessageReceived(event);
        ScannedRobotEvent scannedRobotEvent = (ScannedRobotEvent) event.getMessage();
        moveToEnemyPos(scannedRobotEvent);
    }
    //endregion


    //region Movement and fire methods
    private void moveToEnemyPos(ScannedRobotEvent e) {
        setTurnRightRadians(Utils.normalRelativeAngle(getAngleOfScannedRobot(e) - getHeadingRadians()));
        setTurnRadarLeftRadians(Utils.normalRelativeAngle(getAngleOfScannedRobot(e) - getHeadingRadians()));
        setAhead(100);
        fireByEnergy();
    }


    private void fireByEnergy(){
        double currentHP = this.getEnergy();

        if(currentHP >= 75){
            fire(3);
        }
        else if(currentHP > 35 && currentHP < 75){
            fire(2);
        }
        else{
            fire(1);
        }
    }
    //endregion


    //region Robot colors
    private void setRobotColors() {
        // Prepare RobotColors object
        TeamColors c = new TeamColors();

        // Create custom bulletColor
        Color brown = new Color(139,69,19);

        // Set the Teamcolors to the right color
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
