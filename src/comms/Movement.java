package comms;

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

    static Direction dirLeftRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionToRobot = myLocation.directionTo(robotLocation);
        
    	Direction directitonLeftRobot = directionToRobot.rotateLeftRads( (float)Math.PI / 2 );	
    			
    	return directitonLeftRobot;
    	
    }
    
    static Direction dirRightRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionToRobot = myLocation.directionTo(robotLocation);
        
    	Direction directitonRightRobot = directionToRobot.rotateLeftRads( - (float)Math.PI / 2 );	
    			
    	return directitonRightRobot;
    	
    }

    static Direction dirToRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionToRobot = myLocation.directionTo(robotLocation);
    			
    	return directionToRobot;
    	
    }   
    
    static Direction dirFromRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionFromRobot = robotLocation.directionTo(myLocation);
            			
    	return directionFromRobot;
    	
    }
   
    /**
     * Get the distance between robot and self
     * @return float distance - distance between robot and self
     */    
    static float checkDistance(RobotInfo robot) {
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	float distance = myLocation.distanceTo(robotLocation);
    	
    	return distance;
    }
    
    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
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
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }
    
    // TODO: Combine willCollideWithMe with dodge to reduce bytecode usage!
    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }
    
    /**
     * @param bullet The bullet in question.
     * @return Direction away from the bullet.
     */
    static Direction dodge(BulletInfo bullet) {
    	MapLocation myLocation = rc.getLocation();
    	
    	// Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);
        
        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)
        
        MapLocation intersection = bulletLocation.add(propagationDirection, perpendicularDist);
        Direction towardBullet = new Direction(myLocation, intersection);
        
        return towardBullet.opposite();
    }
}