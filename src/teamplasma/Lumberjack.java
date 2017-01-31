package teamplasma;

import static java.util.Comparator.*;

import java.util.Arrays;
import java.util.Comparator;

import battlecode.common.*;

public class Lumberjack {
	
	/*-----------------------------*
	 * LUMBERJACK GLOBAL VARIABLES *
	 *-----------------------------*/
	
	static RobotController rc = RobotPlayer.rc;
	
	static MapLocation myLocation;
	static Direction moveDirection;
	
	static Target hasTarget = Target.none;
	static int myTarget;
	
	/**
     * Comparator for sorting TreeInfo arrays by ContaintedRobotType (VERY EXPENSIVE) 
     */
    static Comparator<TreeInfo> compareRobots = comparing(TreeInfo::getContainedRobot,nullsLast(reverseOrder()));
	
    /**
     * Main control method for RobotType Lumberjack
     * 
     * @param rc
     * @throws GameActionException
     */
    static void run(RobotController rc) throws GameActionException {
    	
    	initialize();
    	
    	// Code to run every turn
        while (true) {
            try {
            	// Check in every turn
            	RobotPlayer.checkIn();
            	// Check scout spacing, update direction if necessary:
            	moveDirection = Movement.checkFriendlySpacing(moveDirection);
                
            	System.out.println(hasTarget);

                switch(hasTarget) {
                case tree:
                	
                	tryChop();
                	break;
                		
                case robot:
                	
                	trySwipe();
                	
                	// Adjust movement direction to dodge bullets
                	moveDirection = Movement.dodge(moveDirection);
                	// Move
                	moveDirection = Movement.tryMove(moveDirection,30,6);
                	break;
                	
                case none:
                	
                	getTarget();
                	
                	// Adjust movement direction to dodge bullets
                	moveDirection = Movement.dodge(moveDirection);
                	// Move
                	moveDirection = Movement.tryMove(moveDirection,30,6);
                	break;
                default:
                	
                	break;         
                	
                }
                

                


                // End Turn
                RobotPlayer.shakeNearbyTree();
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void initialize() throws GameActionException {
    	
    	// get starting value
		int start = rc.readBroadcast(Channels.BUILD_DIRECTION);
	        
        // get move direction
        if (start == 1) {
        	moveDirection = Direction.WEST;
        } else if (start == 2) {
        	moveDirection = Direction.EAST;
        } else if (start == 3) {
        	moveDirection = Direction.SOUTH;
        } else if (start == 4) {
        	moveDirection = Direction.NORTH;
        } else {
        	
        }
    	
    }
    
    
    static enum Target{
    	none, robot, tree
    }
    
    static void getTarget() throws GameActionException {
    	
    	// Collect info on potential nearby targets
        RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.enemyTeam);
//        TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, Team.NEUTRAL);
        TreeInfo[] enemyTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, RobotPlayer.enemyTeam);
            	
        // Select target from nearby targets
//    	if ( neutralTrees.length > 0 ) {
//    		hasTarget = Target.tree;
//    		myTarget = neutralTrees[0].ID;
//        } else 
        if ( enemyTrees.length > 0 ) {
    		hasTarget = Target.tree;
    		myTarget = enemyTrees[0].ID;
        } else if( robots.length > 0 ) {
        	hasTarget = Target.robot;
        	myTarget = robots[0].ID;
        } else {
        	
        	// No close targets, so search for targets within sight radius
        	robots = rc.senseNearbyRobots(-1,RobotPlayer.enemyTeam);
        	TreeInfo[] neutralTrees  = rc.senseNearbyTrees(-1, Team.NEUTRAL);
        	enemyTrees = rc.senseNearbyTrees(-1, RobotPlayer.enemyTeam);
        
        	myLocation = rc.getLocation();

        	// If there is a target, move towards it
            if (neutralTrees.length > 0 ) {
            	MapLocation treeLocation = neutralTrees[0].getLocation();
            	Direction toTree = myLocation.directionTo(treeLocation);
            	moveDirection = Movement.tryMove(toTree);
            	hasTarget = Target.tree;
        		myTarget = neutralTrees[0].ID;
            } else if(robots.length > 0) {
                MapLocation enemyLocation = robots[0].getLocation();
                Direction toEnemy = myLocation.directionTo(enemyLocation);
                moveDirection = Movement.tryMove(toEnemy);
                hasTarget = Target.robot;
            	myTarget = robots[0].ID;
            } else if (enemyTrees.length > 0) {
            	MapLocation treeLocation = enemyTrees[0].getLocation();
            	Direction toTree = myLocation.directionTo(treeLocation);
            	moveDirection = Movement.tryMove(toTree);
            	hasTarget = Target.tree;
        		myTarget = enemyTrees[0].ID;
            } else {
            	// No nearby targets
            }
        }
    }
    
    
    static void trySwipe() throws GameActionException {
    	
    	RobotInfo[] friends = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.myTeam);
        RobotInfo[] targets = rc.senseNearbyRobots(GameConstants.LUMBERJACK_STRIKE_RADIUS, RobotPlayer.enemyTeam);
                
        boolean attack = false;
        int index = 0;

        if ( !rc.hasAttacked() ) {
        	// already attacked
        	attack = false;
        } else if ( friends.length >= targets.length ){
        	// too much friendly fire
        	attack = false;
        } else if (targets.length > 0) {
        	// okay to attack, make sure we still have a target
	        for ( RobotInfo target : targets) {
	        	if (target.ID == myTarget) {
	        		attack = true;
	        		index++;
	        		break;
	        	}
	        }
        } else {
        	// better safe than sorry
        	attack = false;
        }
                
        if (attack) {
    		moveDirection = myLocation.directionTo(rc.senseRobot(myTarget).location);
    		rc.strike();
    	} else {
    		hasTarget = Target.none;
    		myTarget = 0;
    	}
    }
    
    static void tryChop() throws GameActionException {
    	
        TreeInfo[] neutralTrees = rc.senseNearbyTrees(RobotType.LUMBERJACK.bodyRadius+GameConstants.INTERACTION_DIST_FROM_EDGE, Team.NEUTRAL);
        Arrays.sort(neutralTrees, compareRobots);
    	
    	if (rc.canChop(myTarget)){
//    		moveDirection = myLocation.directionTo(rc.senseTree(myTarget).location);
    		rc.chop(myTarget);
    	} else if (neutralTrees.length > 0) {
    		if (rc.canChop(neutralTrees[0].ID))
    			rc.chop(neutralTrees[0].ID);
    	} else {
    		hasTarget = Target.none;
    		myTarget = 0;
    	}
    }
    
    
    
}