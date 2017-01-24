package teamplasma;

import java.util.Arrays;
import java.util.Comparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static java.util.Comparator.nullsLast;

import battlecode.common.*;
import teamplasma.Constants;

public class Scout {
		
	static RobotController rc = RobotPlayer.rc;

    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an scout!");

        // Initialize movement routine
        Direction myDirection = rc.getLocation().directionTo(Communication.readMapCenter());
        
        while (true) {
            try {
            	
            	// Check in every turn
            	RobotPlayer.checkIn();
            	
             	// Check scout spacing:
            	myDirection = checkFriendlySpacing(myDirection);

             	// Check for enemies:
             	RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().sensorRadius,rc.getTeam().opponent());
             	for (RobotInfo bot : enemies){					
	         		switch(bot.getType()){
					case ARCHON:
						// ignore Archons for now
						break;
					case GARDENER:
						harassScout(bot);
						break;
					case LUMBERJACK:
						// ignore for now
						break;
					case SCOUT:
						harassScout(bot);
						break;
					case SOLDIER:
						// ignore for now
						break;
					case TANK:
						// ignore for now
						break;
					default:
						break;
	         		}
	         	}
            	
            	// Check for units in neutral trees
            	// checkForUnitTrees();
            	
            	// Check for bullets in neutral trees:
             	myDirection = checkForBulletTrees(myDirection);
             	             	
             	// Move
             	if (!rc.hasMoved()) {
                 	myDirection = tryMove(myDirection,10,15);
             	}

             	// End Turn
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
//********************************************************************************************************************************************************************//
    
    static void harassScout(RobotInfo target) throws GameActionException {
    	    	
    	RobotType myType = rc.getType();
    	MapLocation myLocation = rc.getLocation();
    	
    	RobotType targetType = rc.getType();
    	MapLocation targetLocation = target.getLocation();
    	
    	
    	// Movement section:
    	
    	boolean moved = false;
    	
    	if (rc.hasMoved()) {
			moved = true;
		} else {
			
			float distance = myLocation.distanceTo(targetLocation);
			float stepsize = myType.strideRadius;
						
			if ( distance <= myType.strideRadius+myType.bodyRadius+targetType.bodyRadius) {
				stepsize = distance - myType.bodyRadius - targetType.bodyRadius;
			}
			
			Direction dir = myLocation.directionTo(targetLocation);
			
	        if (rc.canMove(dir,stepsize)) {
	        	moved = Movement.tryMove(dir,10,9);
	        }			
		}
    	
    	// Shooting section
    
    	if ( rc.canFireSingleShot() ) {
    		// fire at target with random spread
    		float scatter = (float) (0.25*Math.cos(Math.random()*Math.PI));
    		MapLocation fireLocation = targetLocation.add((float)(Math.PI/2.0), scatter*targetType.bodyRadius);
    		Direction firDir = myLocation.directionTo(fireLocation);
    		
    		rc.fireSingleShot(firDir);
    		
    	}
    	
    	
    }
    
    /**
     * checkFriendlySpacing():
     * 		Determines if there are nearby friendly units and attempts to move
     * 		away from them if too close.  		
     * 		
     * @param myDirection
     * @return
     */
    static Direction checkFriendlySpacing(Direction myDirection) {
    	// Get all friendly robots in sensorRadius
     	RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().sensorRadius,rc.getTeam());
     	if (friends.length==0){
     		// You have no friends! How sad. :(
     		return myDirection;
     	} else {
     		// Scan all friendly robots, separation based on average distance from other robots
         	MapLocation myLocation = rc.getLocation();
         	MapLocation targetLocation = myLocation;
         	for (RobotInfo bot : friends){
         		MapLocation botLocation = bot.getLocation();
    			float seperation = myLocation.distanceTo(botLocation);
         		switch(bot.getType()){
    			case ARCHON:
    				if( seperation < Constants.SCOUT_SPACING_FROM_ARCHON ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case GARDENER:
    				if( seperation < Constants.SCOUT_SPACING_FROM_GARDENER ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case LUMBERJACK:
    				if( seperation < Constants.SCOUT_SPACING_FROM_LUMBERJACK) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case SCOUT:
    				if( seperation < Constants.SCOUT_SPACING_FROM_SCOUT ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case SOLDIER:
    				if( seperation < Constants.SCOUT_SPACING_FROM_SOLDIER ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case TANK:
    				if( seperation < Constants.SCOUT_SPACING_FROM_TANK ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			default:
    				break;
         		}//end switch
         	}//end for         	
         	if(myLocation==targetLocation){
         		return myDirection;
         	} else {
				rc.setIndicatorLine(myLocation, targetLocation, 0, 150, 150);
	         	myDirection = myLocation.directionTo(targetLocation);
	         	return myDirection;
         	}
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
    	TreeInfo[] closeTrees = rc.senseNearbyTrees(GameConstants.INTERACTION_DIST_FROM_EDGE,Team.NEUTRAL);
    	
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

    /**
     * Comparator for sorting TreeInfo arrays by bullet amounts
     */
    static Comparator<TreeInfo> compareBullets = comparing(TreeInfo::getContainedBullets, reverseOrder());
    
    
    /**
     * Comparator for sorting TreeInfo arrays by ContaintedRobotType 
     * 
     * VERY EXPENSIVE (~35000 bytecode)
     */
    static Comparator<TreeInfo> compareRobots = comparing(TreeInfo::getContainedRobot,nullsLast(naturalOrder()));
    
    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return Direction of the move, unchanged if no move performed
     * @throws GameActionException
     */
    static Direction tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
    	boolean safe = rc.senseNearbyBullets(rc.getLocation().add(dir, RobotPlayer.myType.strideRadius), RobotPlayer.myType.bodyRadius).length == 0;
        if (rc.canMove(dir) && !rc.hasMoved() && safe) {
            rc.move(dir);
            return dir;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
        	Direction testDir = dir.rotateLeftDegrees(degreeOffset*currentCheck);
        	safe = rc.senseNearbyBullets(rc.getLocation().add(testDir, RobotPlayer.myType.strideRadius), RobotPlayer.myType.bodyRadius).length == 0;
            if(rc.canMove(testDir) && !rc.hasMoved() && safe) {
                rc.move(testDir);
                return testDir;
            }
            // Try the offset on the right side
            testDir = dir.rotateRightDegrees(degreeOffset*currentCheck);
            safe = rc.senseNearbyBullets(rc.getLocation().add(testDir, RobotPlayer.myType.strideRadius), RobotPlayer.myType.bodyRadius).length == 0;
            if(rc.canMove(testDir) && !rc.hasMoved() && safe) {
                rc.move(testDir);
                return testDir;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return dir;
    }
    
   	
}
