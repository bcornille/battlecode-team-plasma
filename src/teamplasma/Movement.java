package teamplasma;

import battlecode.common.*;

public class Movement {
	
	static RobotController rc = RobotPlayer.rc;
	
    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
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
//            // Try the offset on the right side
//            testDir = dir.rotateRightDegrees(degreeOffset*currentCheck);
//            safe = rc.senseNearbyBullets(rc.getLocation().add(testDir, RobotPlayer.myType.strideRadius), RobotPlayer.myType.bodyRadius).length == 0;
//            if(rc.canMove(testDir) && !rc.hasMoved() && safe) {
//                rc.move(testDir);
//                return testDir;
//            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return dir;
    }
    
    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static Direction tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,10,10);
    }
    
    static Direction dodge(Direction myDirection) {
    	// Get all bullets in sensor distance (might change this)
    	BulletInfo[] bullets = rc.senseNearbyBullets(rc.getType().sensorRadius);
    	if (bullets.length == 0){
    		// No bullets! Look at you mister safe. 
    		return myDirection;
    	} else {
         	MapLocation myLocation = rc.getLocation();
         	MapLocation targetLocation = myLocation;
    		// Scan nearby bullets to determine how to dodge them
    		for (BulletInfo bullet : bullets) {
        		// Get relevant bullet information
                MapLocation bulletLocation = bullet.location;
                MapLocation futureLocation = bulletLocation.add(bullet.dir,bullet.speed);
	            float bulletDistance = bulletLocation.distanceTo(myLocation);
	            float futureDistance = futureLocation.distanceTo(myLocation);
	            // if distance increase, bullet is moving away, ignore bullet
	            if (futureDistance > bulletDistance){
	            	continue;
	            }
                // Adjust our location to be out of bullet path
                // TODO: add weighting based on proximity and bullet damage
	            Direction bulletDirection = myLocation.directionTo(bulletLocation);
	            Direction futureDirection = myLocation.directionTo(futureLocation);
	            float theta = bulletDirection.degreesBetween(futureDirection);
        
	            if (theta < 0) {
	                targetLocation=targetLocation.add(bulletLocation.directionTo(myLocation));
	                targetLocation=targetLocation.add(futureLocation.directionTo(myLocation).rotateRightDegrees(90));
	            } else {
	                targetLocation=targetLocation.add(bulletLocation.directionTo(myLocation));
	                targetLocation=targetLocation.add(futureLocation.directionTo(myLocation).rotateLeftDegrees(90));
	            }
         		rc.setIndicatorLine(bulletLocation, futureLocation, 250, 0, 0);
         		rc.setIndicatorLine(myLocation, targetLocation, 0, 250, 0);         
    		}//end for
    		if(myLocation==targetLocation){
         		return myDirection;
         	} else {
				rc.setIndicatorLine(myLocation, targetLocation, 0, 150, 150);
	         	myDirection = myLocation.directionTo(targetLocation);
	         	return myDirection;
         	}//end if
    	}//end if
    }// end method
    
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
    			float spacing = getSpacing(rc.getType(),bot.type);
    			if (seperation <= spacing) {
    				targetLocation=targetLocation.add(botLocation.directionTo(myLocation));
             		rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
    			}
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
     * A big heaping mess of switch statements to get the proper constant
     * for unit spacing.
     * 
     * @param me 
     * @param you
     * @return 
     */
    static float getSpacing(RobotType me, RobotType you) {
    	
    	switch(me){
		case ARCHON:
			switch(you){
			case ARCHON:
				return Constants.SPACING_ARCHON_ARCHON;
			case GARDENER:
				return Constants.SPACING_ARCHON_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_ARCHON_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_ARCHON_SCOUT;
			case SOLDIER:
				return Constants.SPACING_ARCHON_SOLDIER;
			case TANK:
				return Constants.SPACING_ARCHON_TANK;
			default:
				return 0.0f;
			}
		case GARDENER:
			switch(you){
			case ARCHON:
				return Constants.SPACING_GARDENER_ARCHON;
			case GARDENER:
				return Constants.SPACING_GARDENER_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_GARDENER_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_GARDENER_SCOUT;
			case SOLDIER:
				return Constants.SPACING_GARDENER_SOLDIER;
			case TANK:
				return Constants.SPACING_GARDENER_TANK;
			default:
				return 0.0f;
			}
		case LUMBERJACK:
			switch(you){
			case ARCHON:
				return Constants.SPACING_LUMBERJACK_ARCHON;
			case GARDENER:
				return Constants.SPACING_LUMBERJACK_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_LUMBERJACK_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_LUMBERJACK_SCOUT;
			case SOLDIER:
				return Constants.SPACING_LUMBERJACK_SOLDIER;
			case TANK:
				return Constants.SPACING_LUMBERJACK_TANK;
			default:
				return 0.0f;
			}
		case SCOUT:
			switch(you){
			case ARCHON:
				return Constants.SPACING_SCOUT_ARCHON;
			case GARDENER:
				return Constants.SPACING_SCOUT_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_SCOUT_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_SCOUT_SCOUT;
			case SOLDIER:
				return Constants.SPACING_SCOUT_SOLDIER;
			case TANK:
				return Constants.SPACING_SCOUT_TANK;
			default:
				return 0.0f;
			}
		case SOLDIER:
			switch(you){
			case ARCHON:
				return Constants.SPACING_SOLDIER_ARCHON;
			case GARDENER:
				return Constants.SPACING_SOLDIER_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_SOLDIER_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_SOLDIER_SCOUT;
			case SOLDIER:
				return Constants.SPACING_SOLDIER_SOLDIER;
			case TANK:
				return Constants.SPACING_SOLDIER_TANK;
			default:
				return 0.0f;
			}
		case TANK:
			switch(you){
			case ARCHON:
				return Constants.SPACING_TANK_ARCHON;
			case GARDENER:
				return Constants.SPACING_TANK_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_TANK_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_TANK_SCOUT;
			case SOLDIER:
				return Constants.SPACING_TANK_SOLDIER;
			case TANK:
				return Constants.SPACING_TANK_TANK;
			default:
				return 0.0f;
			}
		default:
			return 0.0f;
    	}
    }
    
    static Direction attackTarget(RobotInfo target) {
    	switch (target.type) {
		case ARCHON:
			// Move toward Archons
			return rc.getLocation().directionTo(target.location);
		case GARDENER:
			// Move toward Gardeners
			return rc.getLocation().directionTo(target.location);
		case LUMBERJACK:
			return rc.getLocation().directionTo(target.location);
		case SCOUT:
			// Move toward Scouts
			return rc.getLocation().directionTo(target.location);
		case SOLDIER:
			return rc.getLocation().directionTo(target.location);
		case TANK:
			return rc.getLocation().directionTo(target.location);
		default:
			return RobotPlayer.myDirection;
    	}
    }
}


