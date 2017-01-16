package comms;

import static comms.Constants.*;

import battlecode.common.*;

public class Tank {
	static void run(RobotController rc) throws GameActionException {
		System.out.println("I'm an tank!");
		Team enemy = rc.getTeam().opponent();

    	// Setup comms channel
		Communications.setupMyComms(CHANNEL_MIN_TANK, CHANNEL_MAX_TANK);

		while (true) {
				
			try {	
			
				MapLocation myLocation = rc.getLocation();

				// See if there are any nearby enemy robots
				RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

				// If there are some...
				if (robots.length > 0) {
		 			// And we have enough bullets, and haven't attacked yet this
					// turn...
					if (rc.canFireSingleShot()) {
						// ...Then fire a bullet in the direction of the enemy.
						rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
					}
				}

				// Try to dodge
				BulletInfo[] bullets = rc.senseNearbyBullets();
				for (BulletInfo bullet : bullets) {
					if (Movement.willCollideWithMe(bullet))
						Movement.tryMove(Movement.dodge(bullet));
				}

				// Move randomly
				Movement.tryMove(Movement.randomDirection());

				// Clock.yield() makes the robot wait until the next turn, then
				// it will perform this loop again
				// endTurn() implements Clock.yield() with extra information
				// such as age
				Clock.yield();

			} catch (Exception e) {
				System.out.println("Tank Exception");
				e.printStackTrace();
			}
		}
	}
}