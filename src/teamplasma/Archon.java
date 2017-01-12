package teamplasma;

import battlecode.common.*;

public class Archon {
//	static RobotController rc;
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
        
 //       Archon.rc = rc;

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	// Try to dodge
            	BulletInfo[] bullets = rc.senseNearbyBullets();
            	for (BulletInfo bullet : bullets) {
            		if (Movement.willCollideWithMe(bullet))
            			Movement.tryMove(Movement.dodge(bullet));
            	}

                // Generate a random direction
                Direction dir = Movement.randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(dir) && Math.random() < .01) {
                    rc.hireGardener(dir);
                }

                // Move randomly
               Movement.tryMove(Movement.randomDirection());

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(0,(int)myLocation.x);
                rc.broadcast(1,(int)myLocation.y);

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