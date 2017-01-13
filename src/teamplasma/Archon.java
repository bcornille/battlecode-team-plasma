package teamplasma;

import battlecode.common.*;

public class Archon {
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	RobotPlayer.checkIn();
            	
            	// Try to dodge and if not continue moving.
            	if (!Movement.dodgeBullets()) {
            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
            			Movement.tryMove(RobotPlayer.myDirection);
            		}
            	}

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(RobotPlayer.myDirection.opposite()) && rc.readBroadcast(Constants.CHANNEL_COUNT_GARDENER) < Constants.MAX_COUNT_GARDENER) {
                    rc.hireGardener(RobotPlayer.myDirection.opposite());
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
}