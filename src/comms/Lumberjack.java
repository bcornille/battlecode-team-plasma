package comms;

import static comms.Constants.CHANNEL_COUNT_SCOUT;
import static comms.Constants.CHANNEL_MAX_SCOUT;
import static comms.Constants.CHANNEL_MIN_SCOUT;

import static comms.Constants.*;

import battlecode.common.*;

public class Lumberjack {
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();
        
		Communications.setupMyComms(CHANNEL_MIN_LUMBERJACK, CHANNEL_MAX_LUMBERJACK);


        while (true) {
 
            try {

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,enemy);

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        MapLocation myLocation = rc.getLocation();
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        Movement.tryMove(toEnemy);
                    } else {
                    	// Try to dodge
                    	BulletInfo[] bullets = rc.senseNearbyBullets();
                    	for (BulletInfo bullet : bullets) {
                    		if (Movement.willCollideWithMe(bullet))
                    			Movement.tryMove(Movement.dodge(bullet));
                    	}
                        // Move Randomly
                        Movement.tryMove(Movement.randomDirection());
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
}