package teamplasma;

import battlecode.common.*;

public class Scout {
		
	static RobotController rc = RobotPlayer.rc;

    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an scout!");
        Team enemy = rc.getTeam().opponent();
    	
    	MapLocation startLoc = Communication.readMapCenter();
    	
    	Direction moveDir = rc.getLocation().directionTo(startLoc);
        
        int lastMove=0;

        while (true) {
            try {
            	
            	// Check in every turn
            	RobotPlayer.checkIn();
            	                
//                // Try to dodge and if not continue moving.
//            	if (!Movement.dodgeBullets()) {
//            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
//            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
//            			Movement.tryMove(RobotPlayer.myDirection);
//            		}
//            	}
               
            	// If nearby Gardener or Lumberjack, harass them
                RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, enemy);
            	for (RobotInfo robot : robots) {
           		 
            		switch(robot.getType()) {
            		case ARCHON:
//            			lastMove = attackHelpless(robot,lastMove);
            			break;
            		case GARDENER:
            			lastMove = attackHelpless(robot,lastMove);
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
           
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * treeScout is a sub-type of Scout
     * 
     * treeScouts perform three primary tasks:
     * 		1) Locate trees with bullets and shake them
     * 		2) Identify trees with key units for Lumberjacks
     * 		3) Decide if Lumberjacks are needed for key map objective
     * 
     * Additionally, they constantly monitor the known map boundaries
     * 
     * 
     * @throws GameActionException
     */
    static void treeScout() throws GameActionException {
    	
    	MapLocation mapCenter = Communication.readMapCenter();
    	
    	Direction moveDir = rc.getLocation().directionTo(mapCenter);

    	
    	
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
	
		int moved = 0;
		
		MapLocation myLocation = rc.getLocation();
		MapLocation targetLocation = target.location;
		
		// Movement section:
		//		0) Check if already moved.
		//		1) Check distance from target. 
		//		2) If too close, move back. Adjust step size based on how close to not overshoot range. 
		//		3) If in proper range, move left/right, if possible. Else move forward.  
		//
		// TODO: Improve decision making when standard move options fail
		
		if (rc.hasMoved()) {
			moved = 0;
		} else {
			
			float distance =  myLocation.distanceTo(targetLocation);
			float stepsize = target.getType().strideRadius;
			
			if ( distance > 9.0f ) {
		    	Direction dir = myLocation.directionTo(targetLocation);
		        if (rc.canMove(dir,stepsize)) {
		            rc.move(dir,stepsize);
		            moved =  1; // moved towards (1)
		        }
		    } else if ( distance > 7.0f ) {
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
		myLocation = rc.getLocation();
		if (rc.canFireSingleShot()) {
			rc.fireSingleShot(myLocation.directionTo(targetLocation));
		} else {
			// Do Nothing
		}
		
		// Return 
		return moved;
	
	}
	
	static int attackHelpless(RobotInfo target, int last) throws GameActionException {
		int moved = 0;
		
		MapLocation myLocation = rc.getLocation();
		MapLocation targetLocation = target.location;
		
		if (rc.hasMoved()) {
			moved = 0;
		} else {
			if (Movement.tryMove(myLocation.directionTo(targetLocation),15,1))
				moved = 1;
		}
		
		// Attack section: 
		//		1) Check if can shoot.
		//		2) Shoot at target.
		//		3) Potentially add prediction for shooting.
		myLocation = rc.getLocation();
		if (rc.canFireSingleShot()) {
			rc.fireSingleShot(myLocation.directionTo(targetLocation));
		} else {
			// Do Nothing
		}
				
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
		
		// Make checkCount and the movement change angle constants?
		while (hasMoved == false && checkCount < 18) {
		
			MoveStatus moveflag = scoutMove(dir);
						
			switch(moveflag) {
			case FAILED_ALREADY_MOVED:
				hasMoved = true;
			case FAILED_OBSTRUCTION:
				dir = dir.rotateRightDegrees(20);
			case FAILED_MAP_EDGE:
				dir = dir.rotateRightDegrees(20); 
				
				MapLocation myLocation = rc.getLocation(); 
				if ((int)myLocation.x < rc.readBroadcast(Constants.CHANNEL_MAP_XMIN)) {
					rc.broadcast(Constants.CHANNEL_MAP_XMIN, (int)myLocation.x);
				} 
				if ((int)myLocation.x > rc.readBroadcast(Constants.CHANNEL_MAP_XMAX)) {
					rc.broadcast(Constants.CHANNEL_MAP_XMAX, (int)myLocation.x);
				}
				if ((int)myLocation.y < rc.readBroadcast(Constants.CHANNEL_MAP_YMIN)) {
					rc.broadcast(Constants.CHANNEL_MAP_YMIN, (int)myLocation.y);
				} 
				if ((int)myLocation.y > rc.readBroadcast(Constants.CHANNEL_MAP_YMAX)) {
					rc.broadcast(Constants.CHANNEL_MAP_YMAX, (int)myLocation.y);
				} 
			case SUCCESS_MOVED_FORWARD:
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
	 * @return MoveStatus 
	 * @throws GameActionException
	 */
	static MoveStatus scoutMove(Direction dir) throws GameActionException {
		
		// See if robot has moved this turn
		if (rc.hasMoved()) { 
			return MoveStatus.FAILED_ALREADY_MOVED;
		}
		
	    // See if movement location is on map
	    RobotType myType = rc.getType(); 
	    float lengthCheck = rc.getType().strideRadius+myType.bodyRadius; 
	    MapLocation myLocation = rc.getLocation();
	    MapLocation checkLocation = myLocation.add(dir,lengthCheck); 
	    if (!rc.onTheMap(checkLocation)) { 
	        return MoveStatus.FAILED_MAP_EDGE;
	    } 
	    
	    // See if movement direction is allowed
	    if (!rc.canMove(dir)) { 
	        return MoveStatus.FAILED_OBSTRUCTION;
	    }
	
	    // Passes all movement checks, move to new location. 
	    rc.move(dir);
	    return MoveStatus.SUCCESS_MOVED_FORWARD;
		
	}
	
	/**
	 * MoveStatus
	 * 
	 * Flags for scout movement
	 *
	 */
	static public enum MoveStatus {
		SUCCESS_MOVED_FORWARD,
		SUCCESS_MOVED_RIGHT,
		SUCCESS_MOVED_LEFT,
		SUCCESS_MOVED_BACK,
		FAILED_MAP_EDGE,
		FAILED_OBSTRUCTION,
		FAILED_ALREADY_MOVED
	}	
	
}
	
	
	
	
	
	
