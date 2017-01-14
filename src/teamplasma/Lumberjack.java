package teamplasma;

import battlecode.common.*;

public class Lumberjack {
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a lumberjack!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	RobotPlayer.checkIn();

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.enemyTeam);
                TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+Constants.STRIDE_RADIUS_LUMBERJACK, Team.NEUTRAL);
                TreeInfo[] enemyTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.enemyTeam);
                
                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else if (neutralTrees.length > 0 && !rc.hasAttacked()) {
                	rc.chop(neutralTrees[0].ID);
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,RobotPlayer.enemyTeam);
                    neutralTrees  = rc.senseNearbyTrees(-1, Team.NEUTRAL);
                    
                    MapLocation myLocation = rc.getLocation();

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        Movement.tryMove(toEnemy);
                    } else if (neutralTrees.length > 0 ) {
                    	
                    	MapLocation treeLocation = neutralTrees[0].getLocation();
                    	Direction toTree = myLocation.directionTo(treeLocation);
                    	
                    	Movement.tryMove(toTree);
                    } else {
                    	// Try to dodge and if not continue moving.
                    	if (!Movement.dodgeBullets()) {
                    		if (!Movement.tryMove(RobotPlayer.myDirection)) {
                    			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
                    			Movement.tryMove(RobotPlayer.myDirection);
                    		}
                    	}
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
}