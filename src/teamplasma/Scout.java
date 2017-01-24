package teamplasma;

import java.util.Comparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.reverseOrder;
import static java.util.Comparator.nullsLast;

import java.lang.reflect.Array;
import java.util.Arrays;

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
             		float seperation = rc.getLocation().distanceTo(bot.getLocation());
	         		switch(bot.getType()){
					case ARCHON:
						// ignore Archons for now
						break;
					case GARDENER:
						myDirection = rc.getLocation().directionTo(bot.getLocation());
						if ( rc.canFireSingleShot() ) {
							rc.fireSingleShot(rc.getLocation().directionTo(bot.location));
						}
						break;
					case LUMBERJACK:
						if( seperation < Constants.SCOUT_SPACING_ENEMY_LUMBERJACK ) {
							myDirection = rc.getLocation().directionTo(bot.getLocation()).opposite();
						}
						break;
					case SCOUT:
						if( seperation < Constants.SCOUT_SPACING_ENEMY_SCOUT ) {
							myDirection = rc.getLocation().directionTo(bot.getLocation()).opposite();
						}
						if ( rc.canFireSingleShot() ) {
							
							rc.fireSingleShot(rc.getLocation().directionTo(bot.getLocation()));
						}
						break;
					case SOLDIER:
						if( seperation < Constants.SCOUT_SPACING_ENEMY_SOLDIER ) {
							myDirection = rc.getLocation().directionTo(bot.getLocation()).opposite();
						}
						break;
					case TANK:
						if( seperation < Constants.SCOUT_SPACING_ENEMY_TANK ) {
							myDirection = rc.getLocation().directionTo(bot.getLocation()).opposite();
						}
						break;
					default:
						break;
	         		}
	         	}
            	
            	// Check for units in neutral trees
            	// checkForUnitTrees();
            	
            	// Check for bullets in neutral trees:
             	myDirection = checkForBulletTrees(myDirection);
             	
             	
             	
             	// move
             	myDirection = tryMove(myDirection,10,15);


                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
    
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
    				if( seperation < Constants.SCOUT_SPACING_FRIEND_ARCHON ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case GARDENER:
    				if( seperation < Constants.SCOUT_SPACING_FRIEND_GARDENER ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case LUMBERJACK:
    				if( seperation < Constants.SCOUT_SPACING_FRIEND_LUMBERJACK) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case SCOUT:
    				if( seperation < Constants.SCOUT_SPACING_FRIEND_SCOUT ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case SOLDIER:
    				if( seperation < Constants.SCOUT_SPACING_FRIEND_SOLDIER ) {
    					targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
    	         		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    				}
    				break;
    			case TANK:
    				if( seperation < Constants.SCOUT_SPACING_FRIEND_TANK ) {
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
    
    
    
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    static Direction scouting(Direction dir) throws GameActionException {
//	
//		// Movement section:
//		// 		0) Check if can move.
//		//		1) Get target location from archons. TODO: add archon control of scouts
//		//		2) Move toward target location.
//				
//		if (!rc.hasMoved()) {
//			
//			dir = spiralMove(dir);
//		
//		} 
//		
//		return dir;
//		
//		// Scouting section:
//		//		1) Look for enemy robots
//		//		2) Look for enemy trees
//		//		3) Look for neutral trees
//		//			a) Check trees for resources
//		//			b) Shake trees if available
//		//		4) Communicate findings. 
//		//
//		// TODO: Need to add this still, but have to figure out a good way to do it. 
//		
//		
//    }
//
//
//	static int harassing(RobotInfo target, int last) throws GameActionException { 
//	
//		int moved = 0;
//		
//		MapLocation myLocation = rc.getLocation();
//		MapLocation targetLocation = target.location;
//		
//		// Movement section:
//		//		0) Check if already moved.
//		//		1) Check distance from target. 
//		//		2) If too close, move back. Adjust step size based on how close to not overshoot range. 
//		//		3) If in proper range, move left/right, if possible. Else move forward.  
//		//
//		// TODO: Improve decision making when standard move options fail
//		
//		if (rc.hasMoved()) {
//			moved = 0;
//		} else {
//			
//			float distance =  myLocation.distanceTo(targetLocation);
//			float stepsize = target.getType().strideRadius;
//			
//			if ( distance > 9.0f ) {
//		    	Direction dir = myLocation.directionTo(targetLocation);
//		        if (rc.canMove(dir,stepsize)) {
//		        	Movement.tryMove(dir,10,9);
////		            rc.move(dir,stepsize);
//		            moved =  1; // moved towards (1)
//		        }
//		    } else if ( distance > 7.0f ) {
//		    	// If last move was left (2), go left
//		    	if (last==2) {
//		    		Direction dir = myLocation.directionTo(targetLocation).rotateLeftDegrees(90);
//			        if (rc.canMove(dir,stepsize)) {
//			        	Movement.tryMove(dir,10,9);
////			            rc.move(dir,stepsize);
//			            moved = 2;
//			        } else if (rc.canMove(dir.opposite(),stepsize)) {
//			        	Movement.tryMove(dir,10,9);
////			            rc.move(dir.opposite(),stepsize);
//			        	moved = 3;
//			        } else {
//			        	moved = 0;
//			        }			        
//		    	} else { // Else go right (3)
//		    		Direction dir = myLocation.directionTo(targetLocation).rotateRightDegrees(90);
//			        if (rc.canMove(dir,stepsize)) {
//			        	Movement.tryMove(dir,10,9);
////			            rc.move(dir,stepsize);
//			            moved = 3;
//			        } else if (rc.canMove(dir.opposite(),stepsize)) {
//			        	Movement.tryMove(dir,10,9);
////			            rc.move(dir.opposite(),stepsize);
//			        	moved = 2;
//			        } else {
//			        	moved = 0;
//			        }		
//		    	}
//	    	} else {
//		    	Direction dir = myLocation.directionTo(targetLocation).opposite();
//		        if (rc.canMove(dir,stepsize)) {
//		        	Movement.tryMove(dir,10,9);
////		            rc.move(dir,stepsize);
//		            moved = 4;
//		        } else {
//		        	moved = 0;
//		        }   
//	    	}	
//		}
//		
//
//		// Attack section: 
//		//		1) Check if can shoot.
//		//		2) Shoot at target.
//		//		3) Potentially add prediction for shooting.
//		myLocation = rc.getLocation();
//		if (rc.canFireSingleShot()) {
//			rc.fireSingleShot(myLocation.directionTo(targetLocation));
//		} else {
//			// Do Nothing
//		}
//		
//		// Return 
//		return moved;
//	
//	}
//	
//	static int attackHelpless(RobotInfo target, int last) throws GameActionException {
//		int moved = 0;
//		
//		MapLocation myLocation = rc.getLocation();
//		MapLocation targetLocation = target.location;
//		
//		if (rc.hasMoved()) {
//			moved = 0;
//		} else {
//			if (Movement.tryMove(myLocation.directionTo(targetLocation),15,1))
//				moved = 1;
//		}
//		
//		// Attack section: 
//		//		1) Check if can shoot.
//		//		2) Shoot at target.
//		//		3) Potentially add prediction for shooting.
//		myLocation = rc.getLocation();
//		if (rc.canFireSingleShot()) {
//			rc.fireSingleShot(myLocation.directionTo(targetLocation));
//		} else {
//			// Do Nothing
//		}
//				
//		return moved;
//	}
//
//
//	/**
//	 * spiralMove
//	 * 
//	 * Movements on the edge of the map.
//	 * 
//	 * @throws GameActionException
//	 */
//	static Direction spiralMove(Direction dir) throws GameActionException {
//		
//		boolean hasMoved = false;
//		
//		int checkCount = 0;
//		
//		// Make checkCount and the movement change angle constants?
//		while (hasMoved == false && checkCount < 18) {
//		
//			MoveStatus moveflag = scoutMove(dir);
//						
//			switch(moveflag) {
//			case FAILED_ALREADY_MOVED:
//				hasMoved = true;
//			case FAILED_OBSTRUCTION:
//				dir = dir.rotateRightDegrees(20);
//			case FAILED_MAP_EDGE:
//				dir = dir.rotateRightDegrees(20); 
//				
//				MapLocation myLocation = rc.getLocation(); 
//				if ((int)myLocation.x < rc.readBroadcast(Constants.CHANNEL_MAP_XMIN)) {
//					rc.broadcast(Constants.CHANNEL_MAP_XMIN, (int)myLocation.x);
//				} 
//				if ((int)myLocation.x > rc.readBroadcast(Constants.CHANNEL_MAP_XMAX)) {
//					rc.broadcast(Constants.CHANNEL_MAP_XMAX, (int)myLocation.x);
//				}
//				if ((int)myLocation.y < rc.readBroadcast(Constants.CHANNEL_MAP_YMIN)) {
//					rc.broadcast(Constants.CHANNEL_MAP_YMIN, (int)myLocation.y);
//				} 
//				if ((int)myLocation.y > rc.readBroadcast(Constants.CHANNEL_MAP_YMAX)) {
//					rc.broadcast(Constants.CHANNEL_MAP_YMAX, (int)myLocation.y);
//				} 
//			case SUCCESS_MOVED_FORWARD:
//				hasMoved = true;				
//			}
//			checkCount++;
//		}	
//		return dir;
//	}
//	
//	
//	/**
//	 * scoutMove
//	 * 
//	 * Attempts to move as a scout, provided the scout has not moved yet this
//	 * 	turn, and the movement location is valid (unoccupied, on the map). 
//	 * 
//	 * NOTE: Currently, rc.canMove() also includes an edge check, so our edge check has to go first.
//	 * 	Might be cheaper to not use canMove, instead search for obstruction on our own. (can easily
//	 *  work in dodging if we do this as well, ie if bullet is in move location, don't do it). 
//	 * 
//	 * @return MoveStatus 
//	 * @throws GameActionException
//	 */
//	static MoveStatus scoutMove(Direction dir) throws GameActionException {
//		
//		// See if robot has moved this turn
//		if (rc.hasMoved()) { 
//			return MoveStatus.FAILED_ALREADY_MOVED;
//		}
//		
//	    // See if movement location is on map
//	    RobotType myType = rc.getType(); 
//	    float lengthCheck = rc.getType().strideRadius+myType.bodyRadius; 
//	    MapLocation myLocation = rc.getLocation();
//	    MapLocation checkLocation = myLocation.add(dir,lengthCheck); 
//	    if (!rc.onTheMap(checkLocation)) { 
//	        return MoveStatus.FAILED_MAP_EDGE;
//	    } 
//	    
//	    // See if movement direction is allowed
//	    if (!rc.canMove(dir)) { 
//	        return MoveStatus.FAILED_OBSTRUCTION;
//	    }
//	
//	    // Passes all movement checks, move to new location. 
//	    rc.move(dir);
//	    return MoveStatus.SUCCESS_MOVED_FORWARD;
//		
//	}
//	
//	/**
//	 * MoveStatus
//	 * 
//	 * Flags for scout movement
//	 *
//	 */
//	static public enum MoveStatus {
//		SUCCESS_MOVED_FORWARD,
//		SUCCESS_MOVED_RIGHT,
//		SUCCESS_MOVED_LEFT,
//		SUCCESS_MOVED_BACK,
//		FAILED_MAP_EDGE,
//		FAILED_OBSTRUCTION,
//		FAILED_ALREADY_MOVED
//	}	
	
}
	
	
	
	
	
	
