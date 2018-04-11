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

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        super.run();
        setRobotColors();

//        this.addCustomEvent(new Condition("wallAvoidance") {
//            public boolean test() {
//                return BasicBot.this.getX() <= (double)BasicBot.this.wallMargin ||
//                        BasicBot.this.getX() >= BasicBot.this.getBattleFieldWidth() - (double)BasicBot.this.wallMargin ||
//                        BasicBot.this.getY() <= (double)BasicBot.this.wallMargin ||
//                        BasicBot.this.getY() >= BasicBot.this.getBattleFieldHeight() - (double)BasicBot.this.wallMargin;
//            }
//        });

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



        if (isTeammate(e.getName())) {
            // Send enemy position to teammates
            sendBroadcastMessage(new EnemyPosition(enemyX, enemyY));
            setTurnRight(180);
            return;
        }

        moveToEnemyPos(e);
    }

    private void moveToEnemyPos(ScannedRobotEvent e) {
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

    public void onCustomEvent(CustomEvent e) {
        if (e.getCondition().getName().equals("wallAvoidance") && this.tooCloseToWall <= 0) {
            this.tooCloseToWall += this.wallMargin;
            setTurnLeft(180);
        }
    }

    public void onHitWall(HitWallEvent e) {
        this.tooCloseToWall = 0;
    }

    private void sendBroadcastMessage(EnemyPosition p) {
        try {
            String messageString = p.getX() + "-" + p.getY();
            broadcastMessage(messageString);
        } catch (IOException e) {
            out.println("Unable to send order: ");
            e.printStackTrace(out);
        }
    }

    private void goTo(double x, double y) {
        /* Transform current coordinates into a vector */
        x -= getX();
        y -= getY();

        /* Calculate the angle to the target position */
        double angleToTarget = Math.atan2(x, y);

        /* Calculate the turn required get there */
        double targetAngle = Utils.normalRelativeAngle(angleToTarget - getHeadingRadians());

        /*
         * The Java Hypot method is a quick way of getting the length
         * of a vector. Which in this case is also the distance between
         * our robot and the target location.
         */
        double distance = Math.hypot(x, y);

        /* This is a simple method of performing set front as back */
        double turnAngle = Math.atan(Math.tan(targetAngle));
        setTurnRightRadians(turnAngle);
        if(targetAngle == turnAngle) {
            setAhead(distance);
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
