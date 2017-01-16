package comms;

import battlecode.common.*;

import static comms.Constants.*;
import comms.Movement;

public class MovementScout {
	static RobotController rc = RobotPlayer.rc;
	
	/**
	 * General movement ideas for scouts:
	 * 1) Map out the boundaries of the board
	 * 2) 
	 *
	 * 
	 * 
	 */
	
	// 0) Initial Archon Locations
	
	
	
	// 1) Map out the boundaries of the board
	
    /**
     * scoutSpiral: max 94 bytecodes
     * 
     * Movements on the edge of the map.
     * 
     * @throws GameActionException
     */
	static Direction scoutSpiralRight(Direction dir) throws GameActionException {
		
		boolean hasMoved = false;
		
		int checkCount = 0;
		
		while (hasMoved == false && checkCount < 4) {
		
			int flag = scoutMove(dir); // 23
			
			System.out.println(flag);
			
			if (flag==-2) {
				// Already moved, set to true
				hasMoved = true;
			} else if (flag==-1) {
				// Could not move, obstructed
				dir = dir.rotateRightDegrees(90); // 1
				
			} else if (flag==0) {
				// Could not move, edge of map
				dir = dir.rotateRightDegrees(90); // 1
				MapLocation myLocation = rc.getLocation(); //1
				if ((int)myLocation.x < rc.readBroadcast(CHANNEL_MAP_XMIN)) {
					rc.broadcast(CHANNEL_MAP_XMIN, (int)myLocation.x);
				} 
				if ((int)myLocation.x > rc.readBroadcast(CHANNEL_MAP_XMAX)) {
					rc.broadcast(CHANNEL_MAP_XMAX, (int)myLocation.x);
				}
				if ((int)myLocation.y < rc.readBroadcast(CHANNEL_MAP_YMIN)) {
					rc.broadcast(CHANNEL_MAP_YMIN, (int)myLocation.y);
				} 
				if ((int)myLocation.y > rc.readBroadcast(CHANNEL_MAP_YMAX)) {
					rc.broadcast(CHANNEL_MAP_YMAX, (int)myLocation.y);
				} 
				
				System.out.println( "(" +(int)myLocation.x + "," + (int)myLocation.y + ")" );
				
			} else {
				// Movement successful, set to true
				hasMoved = true;
			}
			
			checkCount++;
		
		}
		
		return dir;
		
		
	}
	
	
    /**
     * scoutMove: max 23 bytecodes
     * 
     * Attempts to move as a scout, provided the scout has not moved yet this
     * 	turn, and the movement location is valid (unoccupied, on the map). 
     * 
     * NOTE: Currently, rc.canMove() also includes and edge check, so our edge check has to go first.
     * 	Might be cheaper to not use canMove, instead search for obstruction on our own. (can easily
     *  work in dodging if we do this as well, ie if bullet is in move location, don't do it). 
     * 
     * List of exit flags:
     * 	 1: Movement was allowed and performed
     *   0: Movement failed, edge of map
     *  -1: Movement failed, movement is obstructed
     *  -2: Movement failed, already moved this turn
     * 
     * @return (int)flag. See flag numbers for details.
     * @throws GameActionException
     */
	static int scoutMove(Direction dir) throws GameActionException {
		
    	// See if robot has moved this turn, return flag=-1 if it has moved
    	if (rc.hasMoved()) { //1
    		return -2;
    	}
		
        // See if movement location is on map, return flag=0 if it is not (accounts for robot size)
        RobotType myType = rc.getType(); //1
        float lengthCheck = myType.strideRadius+myType.bodyRadius; //0
        MapLocation myLocation = rc.getLocation(); //1
        MapLocation checkLocation = myLocation.add(dir,lengthCheck); //5
        if (!rc.onTheMap(checkLocation)) { //5
            return 0;
        } 
        
        // See if movement direction is allowed, return flag=-2 if it is not
        if (!rc.canMove(dir)) { //10
            return -1;
        }

        // Passes all movement checks, move to new location. 
        rc.move(dir); //0
        return 1;
		
	}
		
    /**
     * tryMove: 11 bytecodes
     * 
     * Attempts to move in a given direction, provided robot has not moved yet this turn.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {

    	// See if robot has moved this turn, return false if it has moved
    	if ( rc.hasMoved() ) {
    		return false;
    	}
    	
        // See if movement direction is allowed (unobstructed), and move if it is 
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }
       
        // A move never happened, so return false.
        return false;
    }
	
	
}