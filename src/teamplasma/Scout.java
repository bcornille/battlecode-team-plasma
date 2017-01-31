package teamplasma;

import java.util.Arrays;
import java.util.Comparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static java.util.Comparator.nullsLast;

import battlecode.common.*;

public class Scout {
		
	static RobotController rc = RobotPlayer.rc;
	
    /**
     * Comparator for sorting TreeInfo arrays by bullet amounts
     */
    static Comparator<TreeInfo> compareBullets = comparing(TreeInfo::getContainedBullets, reverseOrder());
    
    /**
     * Comparator for sorting TreeInfo arrays by ContaintedRobotType (VERY EXPENSIVE) 
     */
    static Comparator<TreeInfo> compareRobots = comparing(TreeInfo::getContainedRobot,nullsLast(naturalOrder()));

    /**
     * run():
     * 		Main control method for RobotType Scout
     * 
     * @param rc
     * @throws GameActionException
     */
    static void run(RobotController rc) throws GameActionException {
        // Code to run every turn
        while (true) {
            try {
            	// Check in every turn
            	RobotPlayer.checkIn();
            	// Check scout spacing, update direction if necessary:
            	RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
             	// Check for enemies, performs movements if engaged, does not update myDirection:
            	checkEnemyEngage();            	
            	// Check for units in neutral trees (expensive)
            		// checkForUnitTrees();
            	// Check for bullets in neutral trees, update direction if necessary:
            	RobotPlayer.myDirection = checkForBulletTrees(RobotPlayer.myDirection);
                // Adjust movement direction to dodge bullets
                RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
             	// Complete movement
             	RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection,10,15);
             	
             	// End Turn:
                RobotPlayer.endTurn();
            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
    
   
    /**
     * checkEnemyEngage():
     * 		Determines if there are nearby enemy units and modifies behavior
     * 		according to the enemy's RobotType. 
     * 
     * @throws GameActionException
     */
    static void checkEnemyEngage() throws GameActionException { 
     	RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().sensorRadius,rc.getTeam().opponent());
     	for (RobotInfo bot : enemies){					
     		switch(bot.getType()){
			case ARCHON:
				// ignore for now
				System.out.println("Enemy Archon  spotted: " + bot.location);
    			Communication.spotEnemyArchon(bot);
				break;
			case GARDENER:
				harassClose(bot);
				break;
			case LUMBERJACK:
				// ignore for now
				break;
			case SCOUT:
				// harassClose(bot);
				break;
			case SOLDIER:
				// ignore for now
				break;
			case TANK:
				// ignore for now
				break;
			default:
				break;
     		}//end switch
     	}//end for
    }//end method
    
    /**
     * harassClose():
     * 		Tells scout to engage in close combat battle with an enemy
     * 		unit. This means getting into close proximity and attacking
     * 		while following the unit. Attacks are randomly spread a small
     * 		account to try to confuse dodge routines and correct for 
     * 		spiral motion. 
     * 
     * @param target
     * @throws GameActionException
     */
    static void harassClose(RobotInfo target) throws GameActionException {
    	// Get attacking robot information
    	RobotType myType = rc.getType();
    	MapLocation myLocation = rc.getLocation();
    	// Get target robot information
    	RobotType targetType = rc.getType();
    	MapLocation targetLocation = target.getLocation();
    	// Movement section:
    	if (!rc.hasMoved()) {
    		// set movement information
    		float distance = myLocation.distanceTo(targetLocation);
			float stepsize = myType.strideRadius;
			// If close enough, reduce step size			
			if ( distance <= myType.strideRadius+myType.bodyRadius+targetType.bodyRadius) {
				stepsize = distance - myType.bodyRadius - targetType.bodyRadius;
			}//end if
			// Move in desired direction or similar direction.			
			Direction dir = myLocation.directionTo(targetLocation);
			if (rc.canMove(dir,stepsize)) {
	        	Movement.tryMove(dir,10,10);
	        }//end if			
		}
    	// Shooting section
    	if ( rc.canFireSingleShot() ) {
    		// fire at target with small spread (helps counter dodging and move error)
    		float scatter = (float)(0.25*Math.cos(Math.random()*Math.PI));
    		MapLocation fireLocation = targetLocation.add((float)(Math.PI/2.0),scatter*targetType.bodyRadius);
    		Direction firDir = myLocation.directionTo(fireLocation);
    		rc.fireSingleShot(firDir);    		
    	}//end if
    }//end method

    /**
     * checkForBulletTrees():
     * 		A given robot checks for nearby trees that contain bullets. It
     * 		checks if there are close trees, and if there are if they have 
     * 		bullets. If there are no close trees or the close trees do not
     *  	contain any bullets, it looks if it can see any trees that do
     *      contain bullets and moves toward the closest one. If there were 
     *      close trees with bullets, it sorts all close trees by the
     *      number of bullets and shakes the tree with the most bullets. 
     * 
     * @param myDirection: direction the robot is set to move in
     * @return myDirection: updated direction the robot is set to move in
     * @throws GameActionException
     */
    static Direction checkForBulletTrees(Direction myDirection) throws GameActionException {
    	
    	MapLocation myLocation = rc.getLocation();
    	    	
    	TreeInfo[] visibleTrees = rc.senseNearbyTrees(rc.getType().sensorRadius,Team.NEUTRAL);
    	TreeInfo[] closeTrees = rc.senseNearbyTrees(RobotPlayer.myType.bodyRadius + GameConstants.INTERACTION_DIST_FROM_EDGE,Team.NEUTRAL);
    	
    	if (closeTrees.length == 0) {
    		// no shakeable trees, look for visible trees
    		if (visibleTrees.length == 0) {
    			// no visible trees, do nothing
    		} else {
    			// visible trees out of interaction range
    			// move to nearest tree with bullets
    			for (TreeInfo tree : visibleTrees) {
    				if (tree.getContainedBullets()>0) {
    					MapLocation treeLocation = tree.getLocation();
    					myDirection = myLocation.directionTo(treeLocation);
    					rc.setIndicatorLine(myLocation, treeLocation, 0, 0, 255);    					
    					break;
    				}
    			}
    		}
    	} else {
    		Arrays.sort(closeTrees,compareBullets);
    		TreeInfo targetTree = closeTrees[0];
    		if (targetTree.getContainedBullets() == 0) {
    			// no nearby trees have bullets
    			// move to nearest tree with bullets
    			for (TreeInfo tree : visibleTrees) {
    				if (tree.getContainedBullets()>0) {
    					MapLocation treeLocation = tree.getLocation();
    					myDirection = myLocation.directionTo(treeLocation);
    					rc.setIndicatorLine(myLocation, treeLocation, 0, 0, 255);    					
    					break;
    				}
    			}
    		} else {
    			// targetTree is tree with most bullets, shake it
    			if (rc.canShake(targetTree.getLocation())) {
					MapLocation treeLocation = targetTree.getLocation();
					myDirection = myLocation.directionTo(treeLocation);
					rc.setIndicatorLine(myLocation, treeLocation, 0, 255, 0);
					rc.shake(treeLocation);
    			}
    		}
    	}
    	return myDirection;
    }
       
    /**
     * checkForUnitTrees(): 
     * 		A given robot checks for nearby trees that contain robots. If a tree with
     *  	a robot is found, it checks to see if it has been found before, 
     *  	and if not, it adds it to the data base of found trees. 
     *  
     *  TODO: Add this to Communications, so all robots can use it
     *  TODO: Add method for setting tree check to false if the tree is no longer there
     *  TODO: Add set tree check to false in chopping method after killing tree
     *  
     *  TODO: CHECK AND REDUCE BYTECOST
     *  
     *  Currently has indicators for testing
     * 
     * 
     * 
     * @throws GameActionException
     */
    static void checkForUnitTrees() throws GameActionException {
    	// Check for neutral trees
    	TreeInfo[] trees = rc.senseNearbyTrees(rc.getType().sensorRadius,Team.NEUTRAL);
    	// Check if there were trees
    	if (trees.length == 0) {
    		// No trees, do nothing
    	} else {
      		// Find all trees with units
    		for (TreeInfo tree : trees) {
    			// See if tree has unit
    			if (tree.containedRobot==null) {
        			// No robot, next tree
    			} else {
    				// Tree has unit, check if it has been recorded
	    			for (int i=TreeConstants.CHANNEL_TREE_UNIT_DATA; 
							 i<TreeConstants.CHANNEL_TREE_UNIT_STOP; 
							 i+=TreeConstants.TREE_UNIT_CHANNELS) {
						if (rc.readBroadcast(i)==tree.ID) {
							// Tree already recorded
							rc.setIndicatorDot(tree.location, 255, 255, 0);
							break;
						} else if (rc.readBroadcast(i)==0) { 
							// Tree not recorded, record it
							rc.setIndicatorDot(tree.location, 0, 255, 0);
							processUnitTrees(tree, i);			    			
			    			break;
						} else {
							// Out of unit tree storage
						}//end if
					}//end for
    			}//end if
	    	}//end for
    	}//end if
    }//end method

    /**
     * processUnitTrees():
     * 		Store all of the desired information for a newly found tree 
     * 		containing a unit, based on the type of unit in the tree.
     * 		Also updates the count for the unit-trees of this type.
     * 
     * Store:
     * 		1) ID of the tree
     *  	2) type of robot (as an int: 1 = ARCHON, 2 = GARDENER, etc)
     *  	3) boolean for tree presence (true if there, false if cleared)
     *  	4) x-position of tree
     *  	5) y-position of tree
     *  
     * @param tree: the TreeInfo object being processed
     * @param channel: the communications channel label for the tree
     * @throws GameActionException
     */
    static void processUnitTrees(TreeInfo tree, int channel) throws GameActionException {
		switch(tree.containedRobot){
		case ARCHON:
	    	rc.broadcast(channel,tree.ID);
			rc.broadcast(channel+1, 1);
			rc.broadcastBoolean(channel+2, true);
			rc.broadcastFloat(channel+3,tree.location.x);
			rc.broadcastFloat(channel+4,tree.location.y);
			rc.broadcast(
				TreeConstants.CHANNEL_TREE_ARCHON_COUNT,
				rc.readBroadcast(TreeConstants.CHANNEL_TREE_ARCHON_COUNT)+1
			);
			break;
		case GARDENER:
	    	rc.broadcast(channel,tree.ID);
			rc.broadcast(channel+1, 2);
			rc.broadcastBoolean(channel+2, true);
			rc.broadcastFloat(channel+3,tree.location.x);
			rc.broadcastFloat(channel+4,tree.location.y);
			rc.broadcast(
				TreeConstants.CHANNEL_TREE_GARDENER_COUNT,
				rc.readBroadcast(TreeConstants.CHANNEL_TREE_GARDENER_COUNT)+1
			);
			break;
		case LUMBERJACK:
	    	rc.broadcast(channel,tree.ID);
			rc.broadcast(channel+1, 3);
			rc.broadcastBoolean(channel+2, true);
			rc.broadcastFloat(channel+3,tree.location.x);
			rc.broadcastFloat(channel+4,tree.location.y);
			rc.broadcast(
				TreeConstants.CHANNEL_TREE_LUMBERJACK_COUNT,
				rc.readBroadcast(TreeConstants.CHANNEL_TREE_LUMBERJACK_COUNT)+1
			);
			break;
		case SCOUT:
	    	rc.broadcast(channel,tree.ID);
			rc.broadcast(channel+1, 4);
			rc.broadcastBoolean(channel+2, true);
			rc.broadcastFloat(channel+3,tree.location.x);
			rc.broadcastFloat(channel+4,tree.location.y);
			rc.broadcast(
				TreeConstants.CHANNEL_TREE_SCOUT_COUNT,
				rc.readBroadcast(TreeConstants.CHANNEL_TREE_SCOUT_COUNT)+1
			);
			break;
		case SOLDIER:
	    	rc.broadcast(channel,tree.ID);
			rc.broadcast(channel+1, 5);
			rc.broadcastBoolean(channel+2, true);
			rc.broadcastFloat(channel+3,tree.location.x);
			rc.broadcastFloat(channel+4,tree.location.y);
			rc.broadcast(
				TreeConstants.CHANNEL_TREE_SOLDIER_COUNT,
				rc.readBroadcast(TreeConstants.CHANNEL_TREE_SOLDIER_COUNT)+1
			);
			break;
		case TANK:
	    	rc.broadcast(channel,tree.ID);
			rc.broadcast(channel+1, 6);
			rc.broadcastBoolean(channel+2, true);
			rc.broadcastFloat(channel+3,tree.location.x);
			rc.broadcastFloat(channel+4,tree.location.y);
			rc.broadcast(
				TreeConstants.CHANNEL_TREE_TANK_COUNT,
				rc.readBroadcast(TreeConstants.CHANNEL_TREE_TANK_COUNT)+1
			);
			break;
		default:
			break;						
		}//end switch
    }//end method
    
    /**
     * Constants being used for the unit tree stuff.  
     */
    static public interface TreeConstants{
    	// Channels for counting units
    	int CHANNEL_TREE_ARCHON_COUNT = 1001;
    	int CHANNEL_TREE_GARDENER_COUNT = 1002;
    	int CHANNEL_TREE_LUMBERJACK_COUNT = 1003;
    	int CHANNEL_TREE_SCOUT_COUNT = 1004;
    	int CHANNEL_TREE_SOLDIER_COUNT = 1005;
    	int CHANNEL_TREE_TANK_COUNT = 1006;
    	// Channels for storying map locations of trees
    	int CHANNEL_TREE_UNIT_DATA = 1100;
    	int CHANNEL_TREE_UNIT_STOP = 2000;
    	int TREE_UNIT_CHANNELS = 5; 
    }	
 	
}
