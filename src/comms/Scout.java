package comms;

import static comms.Constants.*;

import battlecode.common.*;

public class Scout {
	
	static RobotController rc = RobotPlayer.rc;

    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an scout!");
        Team enemy = rc.getTeam().opponent();

    	// Setup comms channel
    	Communications.setupMyComms(CHANNEL_MIN_SCOUT, CHANNEL_MAX_SCOUT);

    	MapLocation myLocation = rc.getLocation();
    	
    	float x = (float)rc.readBroadcast(CHANNEL_MAP_XCEN);
    	float y = (float)rc.readBroadcast(CHANNEL_MAP_YCEN);
    	
    	MapLocation startLoc = new MapLocation(x,y);
    	
    	Direction moveDir = myLocation.directionTo(startLoc);
    	
        int lastMove = 0;

        // The code you want your robot to perform every round should be in this loop
        while (true) { 

        	try {
            
        		// Check in
        		myLocation = rc.getLocation();
            	rc.broadcast(RobotPlayer.channel, rc.getRoundNum());
            	rc.broadcast(RobotPlayer.channel*10+0, (int)myLocation.x);
            	rc.broadcast(RobotPlayer.channel*10+1, (int)myLocation.y);
            	
            	
            	// Dodge bullets if necessary
            	
            	// If nearby Gardener or Lumberjack, harass them
            	RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
            	for (RobotInfo robot : robots) {
            		 
            		switch(robot.getType()) {
            		case ARCHON:
            			break;
            		case GARDENER:
            			lastMove = harassing(robot,lastMove);
            			break;
            		case LUMBERJACK:
            			lastMove = harassing(robot,lastMove);
            			break;
            		case SOLDIER:
            			break;
            		case TANK:
            			break;
            		case SCOUT:
            			break;
            		}
            	}
            	
            	// Nobody of interest nearby, keep scouting
            	moveDir = scouting(moveDir);
                
                // End turn
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
    
	static Direction scouting(Direction dir) throws GameActionException {
		
		// Movement section:
		// 		0) Check if can move.
		//		1) Get target location from archons. TODO: add archon control of scouts
		//		2) Move toward target location.
				
		if (!rc.hasMoved()) {
			
			dir = spiralMove(dir);
			
		} 
		
		return dir;
		
		// Scouting section:
		//		1) Look for enemy robots
		//		2) Look for enemy trees
		//		3) Look for neutral trees
		//			a) Check trees for resources
		//			b) Shake trees if available
		//		4) Communicate findings. 
		//
		// TODO: Need to add this still, but have to figure out a good way to do it. 
		
		
	}
	
	
	static int harassing(RobotInfo target, int last) throws GameActionException { 
		
		// Movement section:
		//		0) Check if already moved.
		//		1) Check distance from target. 
		//		2) If too close, move back. Adjust step size based on how close to not overshoot range. 
		//		3) If in proper range, move left/right, if possible. Else move forward.  
		//
		// TODO: Improve decision making when standard move options fail
		
		int moved = 0;
		
    	MapLocation myLocation = rc.getLocation();
    	MapLocation targetLocation = target.location;
		
		if (rc.hasMoved()) {
			moved = 0;
		} else {
			
			float distance =  myLocation.distanceTo(targetLocation);
			float stepsize = target.getType().strideRadius;
			
			if ( distance > 9 ) {
		    	Direction dir = myLocation.directionTo(targetLocation);
		        if (rc.canMove(dir,stepsize)) {
		            rc.move(dir,stepsize);
		            moved =  1; // moved towards (1)
		        }
		    } else if ( distance > 7 ) {
		    	// If last move was left (2), go left
		    	if (last==2) {
		    		Direction dir = myLocation.directionTo(targetLocation).rotateLeftDegrees(90);
			        if (rc.canMove(dir,stepsize)) {
			            rc.move(dir,stepsize);
			            moved = 2;
			        } else if (rc.canMove(dir.opposite(),stepsize)) {
			        	rc.move(dir.opposite(),stepsize);
			        	moved = 3;
			        } else {
			        	moved = 0;
			        }			        
		    	} else { // Else go right (3)
		    		Direction dir = myLocation.directionTo(targetLocation).rotateRightDegrees(90);
			        if (rc.canMove(dir,stepsize)) {
			            rc.move(dir,stepsize);
			            moved = 3;
			        } else if (rc.canMove(dir.opposite(),stepsize)) {
			        	rc.move(dir.opposite(),stepsize);
			        	moved = 2;
			        } else {
			        	moved = 0;
			        }		
		    	}
	    	} else {
		    	Direction dir = myLocation.directionTo(targetLocation).opposite();
		        if (rc.canMove(dir,stepsize)) {
		            rc.move(dir,stepsize);
		            moved = 4;
		        } else {
		        	moved = 0;
		        }   
	    	}	
		}

		// Attack section: 
		//		1) Check if can shoot.
		//		2) Shoot at target.
		//		3) Potentially add prediction for shooting.
		
		if (rc.canFireSingleShot()) {
			rc.fireSingleShot(myLocation.directionTo(targetLocation));
		} else {
			// Do Nothing
		}
		
		// Return 
		return moved;
		
	}
    
	
    /**
     * spiralMove
     * 
     * Movements on the edge of the map.
     * 
     * @throws GameActionException
     */
	static Direction spiralMove(Direction dir) throws GameActionException {
		
		boolean hasMoved = false;
		
		int checkCount = 0;
		
		while (hasMoved == false && checkCount < 18) {
		
			int flag = scoutMove(dir);
								
			if (flag==-2) {
				// Already moved, set to true
				hasMoved = true;
			} else if (flag==-1) {
				// Could not move, obstructed
				dir = dir.rotateRightDegrees(20); 
			} else if (flag==0) {
				// Could not move, edge of map
				dir = dir.rotateRightDegrees(20); 
				
				MapLocation myLocation = rc.getLocation(); 
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
				
			} else {
				// Movement successful, set to true
				hasMoved = true;
			}
			
			checkCount++;
		
		}
		
		return dir;
		
		
	}
	
	
    /**
     * scoutMove
     * 
     * Attempts to move as a scout, provided the scout has not moved yet this
     * 	turn, and the movement location is valid (unoccupied, on the map). 
     * 
     * NOTE: Currently, rc.canMove() also includes an edge check, so our edge check has to go first.
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
    	if (rc.hasMoved()) { 
    		return -2;
    	}
		
        // See if movement location is on map, return flag=0 if it is not (accounts for robot size)
        RobotType myType = rc.getType(); 
        float lengthCheck = myType.strideRadius+myType.bodyRadius; 
        MapLocation myLocation = rc.getLocation();
        MapLocation checkLocation = myLocation.add(dir,lengthCheck); 
        if (!rc.onTheMap(checkLocation)) { 
            return 0;
        } 
        
        // See if movement direction is allowed, return flag=-2 if it is not
        if (!rc.canMove(dir)) { 
            return -1;
        }

        // Passes all movement checks, move to new location. 
        rc.move(dir);
        return 1;
		
	}
    
    
    
    
    
    
}