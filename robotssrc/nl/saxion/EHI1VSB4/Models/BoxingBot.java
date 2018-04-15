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
    double moveAmount;



    //region Override methods
    @Override
    public void run() {
        super.run();
        setRobotColors();

        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        // Initialize moveAmount to the maximum possible for this battlefield.
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());

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

    /*
        Event that launches everytime a robot is scanned
     */
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
    public void onHitRobot(HitRobotEvent e) {
        if(isTeammate(e.getName())){
            // If he's in front of us, set back up a bit.
            if (e.getBearing() > -90 && e.getBearing() < 90) {
                back(100);
            } // else he's in back of us, so set ahead a bit.
            else {
                ahead(100);
            }
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        setTurnRight(180);
    }

    /*
        Event that launches when a bot dies
     */
    @Override
    public void onDeath(DeathEvent event) {
        super.onDeath(event);
        // Scans again to look for another bot
        turnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    @Override
    public void onWin(WinEvent e) {
        for (int i = 0; i < 50; i++) {
            turnRight(30);
            turnLeft(30);
        }
    }
    //endregion


    //region Movement methods
    private void moveToEnemyPos(ScannedRobotEvent e) {
        setTurnRightRadians(Utils.normalRelativeAngle(getAngleOfScannedRobot(e) - getHeadingRadians()));
        setAhead(moveAmount);
        fireByEnergy();
    }
    //endregion


    // region Fire
    /*
        Changes firepower based on remaining energy.
     */
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


    //region Communication
    /*
        Sends broadcast message
     */
    private void sendBroadcastMessage(ScannedRobotEvent p) {
        try {
            broadcastMessage("The enemy is coming!");
        } catch (IOException e) {
            out.println("Unable to send order: ");
            e.printStackTrace(out);
        }
    }

    /*
        Receives message and scans again
     */
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
    /*
        Gets angle of the scanned robot
     */
    private double getAngleOfScannedRobot(ScannedRobotEvent e) {
        return Math.toRadians((robotStatus.getHeading() + e.getBearing() % 360));
    }


    /*
        Get enemy's X position
     */
    private double getEnemyX(ScannedRobotEvent e, double angle) {
        return (robotStatus.getX() + Math.sin(angle) * e.getDistance());
    }

    /*
        Get enemy's Y position
     */
    private double getEnemyY(ScannedRobotEvent e, double angle) {
        return (robotStatus.getY() + Math.cos(angle) * e.getDistance());
    }
    //endregion
}
