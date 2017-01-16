package teamplasma;

import battlecode.common.*;

public class Scout {
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an scout!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	RobotPlayer.checkIn();
            	
                MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }
                
                // Try to dodge and if not continue moving.
            	if (!Movement.dodgeBullets()) {
            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
            			Movement.tryMove(RobotPlayer.myDirection);
            		}
            	}

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
}