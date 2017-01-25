package teamplasma;

import battlecode.common.*;

public class Tank {
	
	
	static void run(RobotController rc) throws GameActionException {
		// Code to run every turn
		while (true) {
			try {
            	// Check in every turn
				RobotPlayer.checkIn();
            	// Check scout spacing, update direction if necessary:
            	RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
				// See if there are any nearby enemy robots
				RobotInfo[] robots = rc.senseNearbyRobots(-1, RobotPlayer.enemyTeam);
				if (robots.length > 0) {
					if (rc.canFirePentadShot()) {
						rc.firePentadShot(rc.getLocation().directionTo(robots[0].location));
					}
				}

            	// Adjust movement direction to dodge bullets
            	// RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
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