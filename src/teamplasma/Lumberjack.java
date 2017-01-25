package teamplasma;

import battlecode.common.*;

public class Lumberjack {
	
	
	
    static void run(RobotController rc) throws GameActionException {
    	// Code to run every turn
        while (true) {
            try {
            	// Check in every turn
            	RobotPlayer.checkIn();
            	// Check scout spacing, update direction if necessary:
            	RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);

            	// TODO: Make Lumberjacks move to trees/robots that are in sight range but out of strike range if nothing in strike range
            	
                // See if there are any enemy robots within striking range
                RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.enemyTeam);
                TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, Team.NEUTRAL);
                TreeInfo[] enemyTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, RobotPlayer.enemyTeam);
                
                // TODO: Adjust it so only strike if spread out/will hit more enemies than friends
                
                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else if (neutralTrees.length > 0 && rc.canChop(neutralTrees[0].ID)) {
                	rc.chop(neutralTrees[0].ID);
                	//System.out.println("Chop tree" + neutralTrees[0].ID);
                } else if (enemyTrees.length > 0 && rc.canChop(enemyTrees[0].ID)) {
                	rc.chop(enemyTrees[0].ID);
                	//System.out.println("Chop tree" + enemyTrees[0].ID);
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

                    }
                }
                
            	// Adjust movement direction to dodge bullets
            	RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
            	// Move
            	RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);

                // End Turn
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
}