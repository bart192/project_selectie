package nl.saxion.EHI1VSB4.Attributes;

import nl.saxion.EHI1VSB4.Models.EnemyBot;
import robocode.util.Utils;

import java.awt.geom.Point2D;

public class Calculations {
    public Calculations() {
    }

    public double absoluteBearing(double x1, double y1, double x2, double y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        double coordinate = Point2D.distance(x1, y1, x2, y2);
        double arcSine = Math.toDegrees(Math.asin(x / coordinate));
        double bearing = 0.0D;
        if (x > 0.0D && y > 0.0D) {
            bearing = arcSine;
        } else if (x < 0.0D && y > 0.0D) {
            bearing = 360.0D + arcSine;
        } else if (x > 0.0D && y < 0.0D) {
            bearing = 180.0D - arcSine;
        } else if (x < 0.0D && y < 0.0D) {
            bearing = 180.0D - arcSine;
        }

        return bearing;
    }

    public double normalizeBearing(double angle) {
        while(angle > 180.0D) {
            angle -= 360.0D;
        }

        while(angle < -180.0D) {
            angle += 360.0D;
        }

        return angle;
    }

    public int[] getEnemyPosition(EnemyBot enemy, double headingRobot, double robotX, double robotY) {
        double angle = Math.toRadians((headingRobot + enemy.getBearing()) % 360.0D);
        int scannedX = (int)(robotX + Math.sin(angle) * enemy.getDistance());
        int scannedY = (int)(robotY + Math.cos(angle) * enemy.getDistance());
        int[] result = new int[]{scannedX, scannedY};
        return result;
    }

    public double turnDegreesToPredictedPosition(EnemyBot enemy, double ownHeadingInDegrees) {
        double absBearing = enemy.getBearing() + ownHeadingInDegrees;
        double latVel = enemy.getVelocity() * Math.sin(enemy.getHeading() - absBearing);
        double degsToTurn;
        if (enemy.getDistance() > 150.0D) {
            degsToTurn = Utils.normalRelativeAngle(absBearing - ownHeadingInDegrees + latVel / 10.0D);
        } else {
            degsToTurn = Utils.normalRelativeAngle(absBearing - ownHeadingInDegrees + latVel / 15.0D);
        }

        return this.normalizeBearing(degsToTurn);
    }
}
