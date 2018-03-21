package nl.saxion;

import robocode.TeamRobot;

import java.io.IOException;

public class MyLeader extends TeamRobot {

    public void run(){
        try{
            broadcastMessage("Hello");
            circulate();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void circulate() {
        while(true){
            setTurnLeft(360);
            setAhead(10);
        }
    }

}
