package nl.saxion;

import robocode.MessageEvent;
import robocode.TeamRobot;
import sampleteam.Point;
import sampleteam.RobotColors;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class MyRobot extends TeamRobot {

    public void run(){

    }

    public void onMessageReceived(MessageEvent e) {
        // Fire at a point
        if (e.getMessage() != null) {
            circulate();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void circulate() {
        while(true){
            setTurnRight(360);
            setAhead(10);
        }
    }

}
