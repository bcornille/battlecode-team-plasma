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

				// Adjust movement direction to dodge bullets
				// RobotPlayer.myDirection =
				// Movement.dodge(RobotPlayer.myDirection);

				// See if there are any nearby enemy robots
				RobotInfo[] robots = rc.senseNearbyRobots(-1, RobotPlayer.enemyTeam);
				if (robots.length > 0) {
					System.out.println("attacking target");
					// Movement.attackTarget(robots[0]);
					RobotPlayer.myDirection = Movement.pathing(RobotPlayer.myDirection, robots[0].location);
					// Move
					RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);
					// And we have enough bullets, and haven't attacked yet this
					// turn...
					Shooting.shoot(robots[0]);
				} else {
					System.out.println("searching for target");
					MapLocation goal = Communication.getEnemyArchonLocation();
					if (goal != rc.getLocation()){					
						RobotPlayer.myDirection = Movement.pathing(RobotPlayer.myDirection,goal);
					} else { 
						RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
					}
					// Move
					RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);
				}

				// End Turn
				RobotPlayer.shakeNearbyTree();
				RobotPlayer.endTurn();

			} catch (Exception e) {
				System.out.println("Tank Exception");
				e.printStackTrace();
			}
		}
	}
}