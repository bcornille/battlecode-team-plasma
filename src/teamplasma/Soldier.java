package teamplasma;

import battlecode.common.*;

public class Soldier {

	static void run(RobotController rc) throws GameActionException {
		// Code to run every turn
		while (true) {
			try {
				// Check in every turn
				RobotPlayer.checkIn();
				
				// See if there are any nearby enemy robots
				RobotInfo[] robots = rc.senseNearbyRobots(-1, RobotPlayer.enemyTeam);
				if (robots.length > 0) {
					
					// prioratize attacking gardeners > soldiers > non-archon > archon
					RobotInfo target = robots[0];
					for (RobotInfo robot : robots) {
						if (robot.type == RobotType.GARDENER){
							target = robot;
							break;
						} else if (robot.type == RobotType.SOLDIER){
							target = robot;
							break;
						} else if (robot.type != RobotType.ARCHON){
							target = robot;
							break;
						}
					}
													
					System.out.println("attacking target");
					// Movement.attackTarget(robots[0]);
					RobotPlayer.myDirection = Movement.pathing(RobotPlayer.myDirection, target.location);
					// Adjust movement direction to dodge bullets
					RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
					// Move
					RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);
					// And we have enough bullets, and haven't attacked yet this
					// turn...
					Shooting.shoot(target);

					for (RobotInfo isArchon : robots) {
						if (isArchon.type == RobotType.ARCHON) {
							System.out.println("Enemy Archon  spotted: " + isArchon.location);
							Communication.spotEnemyArchon(isArchon);
						}
					}
					
				} else {
					System.out.println("searching for target");
					MapLocation goal = Communication.getEnemyArchonLocation();
					if (goal != rc.getLocation()){					
						RobotPlayer.myDirection = Movement.pathing(RobotPlayer.myDirection,goal);
					} else { 
						RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
					}
					// Adjust movement direction to dodge bullets
					RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
					// Move
					RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);
				}

				// End Turn
				RobotPlayer.shakeNearbyTree();
				RobotPlayer.endTurn();

			} catch (Exception e) {
				System.out.println("Soldier Exception");
				e.printStackTrace();
			}
		}
	}
}