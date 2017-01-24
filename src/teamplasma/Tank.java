package teamplasma;

import battlecode.common.*;

public class Tank {
	
	
	static void run(RobotController rc) throws GameActionException {
		// Code to run every turn
		while (true) {
			try {
            	// Check in every turn
				RobotPlayer.checkIn();
				
				// See if there are any nearby enemy robots
				RobotInfo[] robots = rc.senseNearbyRobots(-1, RobotPlayer.enemyTeam);

				// If there are some...
				if (robots.length > 0) {
					// And we have enough bullets, and haven't attacked yet this
					// turn...
					if (rc.canFireSingleShot()) {
						// ...Then fire a bullet in the direction of the enemy.
						rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
					}
				}

            	// Check scout spacing, update direction if necessary:
            	RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
            	// Adjust movement direction to dodge bullets
            	RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
            	// Move
            	RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);

            	// End Turn
				RobotPlayer.endTurn();

			} catch (Exception e) {
				System.out.println("Tank Exception");
				e.printStackTrace();
			}
		}
	}
}