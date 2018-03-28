package nl.saxion.EHI1VSB4.Models;

import nl.saxion.EHI1VSB4.Attributes.BasicsBot;
import nl.saxion.EHI1VSB4.Attributes.Calculations;
import nl.saxion.EHI1VSB4.Attributes.Communication;
import nl.saxion.EHI1VSB4.Attributes.TeamCommunication;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;

public class SquadBot extends TeamRobot{
    private int wallMargin = 45;
    private int tooCloseToWall = 0;
    private byte moveDirection = 1;
    private ScannedRobotEvent enemyOfLeader;
    private EnemyBot enemy = new EnemyBot();
    private Calculations calculate = new Calculations();
    public TeamCommunication team;
    private Communication communication;

    public SquadBot() {
    }

    public void run() {
        this.team = new TeamCommunication();
        this.communication = new Communication();
        this.team.teammates.add(new BasicsBot(this.getName(), this.getX(), this.getY()));
        this.broadcastMessage(this.communication.Message(this.getX(), this.getY()));
        this.setAllColors(Color.CYAN);
        this.enemy.reset();
        this.addCustomEvent(new Condition("wallAvoidance") {
            public boolean test() {
                return SquadBot.this.getX() <= (double)SquadBot.this.wallMargin || SquadBot.this.getX() >= SquadBot.this.getBattleFieldWidth() - (double)SquadBot.this.wallMargin || SquadBot.this.getY() <= (double)SquadBot.this.wallMargin || SquadBot.this.getY() >= SquadBot.this.getBattleFieldHeight() - (double)SquadBot.this.wallMargin;
            }
        });

        while(true) {
            try {
                this.execute();
            } catch (Exception var2) {
                System.out.println("Exception thrown by execution: " + var2.toString());
            }

            this.turnRadarRightRadians(360.0D);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (!this.isTeammate(e.getName())) {
            this.enemy.update(e, this);
            if (this.communication.checkScannedEnemy(this.team, new BasicsBot(this.enemy.getName(), this.enemy.getX(), this.enemy.getY()))) {
                this.broadcastMessage(this.communication.Message(this.enemy.getName(), this.enemy.getX(), this.enemy.getY()));
                System.out.println("Message sent: " + this.enemy.getName() + this.enemy.getX() + this.enemy.getY());
            }

            if (this.team.getTeammate() != null && this.communication.getEnemy() != null && this.enemy.getName().equals(this.communication.getEnemy().getName())) {
                e.getBearing();
                if (this.enemy.none().booleanValue() || e.getDistance() < this.enemy.getDistance() - 120.0D || e.getName().equals(this.enemy.getName())) {
                    this.enemyOfLeader = e;
                }

                double absBearing = e.getBearingRadians() + this.getHeadingRadians();
                double latVel = e.getVelocity() * Math.sin(e.getHeadingRadians() - absBearing);
                this.setTurnRadarLeftRadians(this.getRadarTurnRemainingRadians());
                double gunTurnAmt;
                if (e.getDistance() > 150.0D) {
                    gunTurnAmt = Utils.normalRelativeAngle(absBearing - this.getGunHeadingRadians() + latVel / 10.0D);
                    this.setTurnGunRightRadians(gunTurnAmt);
                    this.fireWeapon();
                } else {
                    gunTurnAmt = Utils.normalRelativeAngle(absBearing - this.getGunHeadingRadians() + latVel / 15.0D);
                    this.setTurnGunRightRadians(gunTurnAmt);
                    this.fire(3.0D);
                }

                this.squadMovement();
            }
        }

    }

    public void onCustomEvent(CustomEvent e) {
        if (e.getCondition().getName().equals("wallAvoidance") && this.tooCloseToWall <= 0) {
            this.tooCloseToWall += this.wallMargin;
            this.setMaxVelocity(0.0D);
        }

    }

    public void onRobotDeath(RobotDeathEvent e) {
        System.out.println(e.getName() + " died");
        BasicsBot enemybot = this.communication.getEnemy();
        BasicsBot otherTeamEnemy = this.communication.getOtherTeamEnemy();
        if (enemybot != null && e.getName().equals(enemybot.getName())) {
            this.communication.resetEnemy();
        } else if (otherTeamEnemy != null && e.getName().equals(otherTeamEnemy.getName())) {
            this.communication.resetOtherTeamEnemy();
        } else {
            this.team.update(e.getName());
            this.team.setTeammate();
            this.communication.resetOtherTeamEnemy();
            this.communication.resetEnemy();
        }
    }

    public void onHitWall(HitWallEvent e) {
        this.tooCloseToWall = 0;
    }

    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);
    }

    public void onHitRobot(HitRobotEvent e) {
        this.tooCloseToWall = 0;
    }

    private void fireWeapon() {
        double firePower = Math.min(400.0D / this.enemy.getDistance(), 3.0D);
        double bulletSpeed = 20.0D - firePower * 3.0D;
        long time = (long)(this.enemy.getDistance() / bulletSpeed);
        double futureX = this.enemy.getFutureX(time);
        double futureY = this.enemy.getFutureY(time);
        double absDeg = this.calculate.absoluteBearing(this.getX(), this.getY(), futureX, futureY);
        this.setTurnGunRight(this.calculate.normalizeBearing(absDeg - this.getGunHeading()));
        if (!this.enemy.none().booleanValue() && this.getGunHeat() == 0.0D && Math.abs(this.getGunTurnRemaining()) < 10.0D) {
            this.setFire(firePower);
        }

    }

    private void squadMovement() {
        if (Math.random() > 0.9D) {
            this.setMaxVelocity(12.0D * Math.random() + 12.0D);
        }

        if (this.tooCloseToWall > 0) {
            --this.tooCloseToWall;
        }

        this.setTurnRight(this.enemy.getBearing() + 90.0D);
        if (this.getTime() % 20L == 0L || this.getVelocity() == 0.0D) {
            this.moveDirection *= -1;
            this.setAhead((double)(150 * this.moveDirection));
        }

        this.broadcastMessage(this.communication.Message(this.getX(), this.getY()));
        this.team.updateBots(this.getName(), this.getX(), this.getY());
        double enemyBearing = this.calculate.absoluteBearing(this.getX(), this.getY(), this.enemy.getX(), this.enemy.getY());
        if (45.0D < enemyBearing && enemyBearing < 135.0D || 225.0D < enemyBearing && enemyBearing < 315.0D) {
            if (this.getY() > this.team.getTeammate().getY()) {
                this.goTo((int)this.enemy.getX(), (int)this.enemy.getY() + 60);
            } else {
                this.goTo((int)this.enemy.getX(), (int)this.enemy.getY() - 60);
            }
        } else if (this.getX() > this.team.getTeammate().getX()) {
            this.goTo((int)this.enemy.getX() + 60, (int)this.enemy.getY());
        } else {
            this.goTo((int)this.enemy.getX() - 60, (int)this.enemy.getY());
        }

    }

    private void goTo(int x, int y) {
        double a;
        this.setTurnRightRadians(Math.tan(a = Math.atan2((double)(x -= (int)this.getX()), (double)(y -= (int)this.getY())) - this.getHeadingRadians()));
        this.setAhead(Math.hypot((double)x, (double)y) * Math.cos(a));
    }

    public ScannedRobotEvent getEnemy() {
        return this.enemyOfLeader;
    }

    public void broadcastMessage(Serializable message) {
        try {
            super.broadcastMessage(message);
        } catch (IOException var3) {
            System.out.println("Broadcast message exception: " + var3.toString());
        }

    }

    public void onMessageReceived(MessageEvent event) {
        this.communication.processmessage(event, this.team);
        if (this.communication.getmessageNumber() == 3) {
            this.team.createTeam();
        }

    }

    public void onWin(WinEvent e) {
        for(int i = 0; i < 50; ++i) {
            this.turnRight(30.0D);
            this.turnLeft(30.0D);
        }

    }
}
