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
                RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.enemyTeam);
                TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, Team.NEUTRAL);
                TreeInfo[] enemyTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, RobotPlayer.enemyTeam);
                
                for (TreeInfo tree : neutralTrees) {
                	System.out.println(tree.toString());
                	System.out.println("Can chop: " + rc.canChop(tree.ID));
                }
                for (TreeInfo tree : enemyTrees) {
                	System.out.println(tree.toString());
                	System.out.println("Can chop: " + rc.canChop(tree.ID));
                }
                
                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else if (neutralTrees.length > 0 && rc.canChop(neutralTrees[0].ID)) {
                	rc.chop(neutralTrees[0].ID);
                	System.out.println("Chop tree" + neutralTrees[0].ID);
                } else if (enemyTrees.length > 0 && rc.canChop(enemyTrees[0].ID)) {
                	rc.chop(enemyTrees[0].ID);
                	System.out.println("Chop tree" + enemyTrees[0].ID);
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,RobotPlayer.enemyTeam);
                    neutralTrees  = rc.senseNearbyTrees(-1, Team.NEUTRAL);
                    enemyTrees = rc.senseNearbyTrees(-1, RobotPlayer.enemyTeam);
                    
                    MapLocation myLocation = rc.getLocation();

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        Movement.tryMove(toEnemy);
                    } else if (enemyTrees.length > 0) {
                    	
                    	MapLocation treeLocation = enemyTrees[0].getLocation();
                    	Direction toTree = myLocation.directionTo(treeLocation);
                    	
                    	Movement.tryMove(toTree);
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