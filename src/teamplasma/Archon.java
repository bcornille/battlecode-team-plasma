package teamplasma;

import battlecode.common.*;

public class Archon {
	static RobotController rc;
	static boolean amLeader = false;
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
        
        Archon.rc = rc;
        
        Communication.countMe(Constants.CHANNEL_COUNT_ARCHON);
        
        if(rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON) == 1)
        	mapGuess();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	RobotPlayer.checkIn();
            	
            	amLeader = electLeader();
            	
            	if (amLeader) {
            		bringOutYourDead();
            	}
            	
            	// Try to dodge and if not continue moving.
            	if (!Movement.dodgeBullets()) {
            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
            			Movement.tryMove(RobotPlayer.myDirection);
            		}
            	}

                // Randomly attempt to build a gardener in this direction
            	int numArchons = rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON);
            	int maxGardeners = Math.round((Constants.MAX_COUNT_GARDENER - numArchons) * rc.getRoundNum() / rc.getRoundLimit() + numArchons);
//                if (rc.canHireGardener(RobotPlayer.myDirection.opposite()) && rc.readBroadcast(Constants.CHANNEL_COUNT_GARDENER) < maxGardeners) {
//                    rc.hireGardener(RobotPlayer.myDirection.opposite());
//                    Communication.countMe(Constants.CHANNEL_COUNT_GARDENER);
//                }
            	if (rc.readBroadcast(Constants.CHANNEL_COUNT_GARDENER) < maxGardeners) {
            		tryHireGardener();
            	}

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void mapGuess() throws GameActionException {      
       
        MapLocation myLocation = rc.getLocation();
        MapLocation[] myArchons = rc.getInitialArchonLocations(RobotPlayer.myTeam);
        MapLocation[] enemyArchons = rc.getInitialArchonLocations(RobotPlayer.enemyTeam);
        int numArchons = myArchons.length;
        
        // Locate all Archons to get rough outline of map and map center
        float xmin = myLocation.x;
        float xmax = myLocation.x;
        float ymin = myLocation.y;
        float ymax = myLocation.y;
        
        for ( int i = 0; i < numArchons; i++ ) {
        	
        	xmin = Math.min(myArchons[i].x,xmin);
        	xmax = Math.max(myArchons[i].x,xmax);
        	ymin = Math.min(myArchons[i].y,ymin);
        	ymax = Math.max(myArchons[i].y,ymax);

        	xmin = Math.min(enemyArchons[i].x,xmin);
        	xmax = Math.max(enemyArchons[i].x,xmax);
        	ymin = Math.min(enemyArchons[i].y,ymin);
        	ymax = Math.max(enemyArchons[i].y,ymax);
        	
        }
        Communication.setMapEdge(xmin, xmax, ymin, ymax);
    }
    
     /**
      * 
      * @return true if leader, false otherwise
      * @throws GameActionException
      */
    static boolean electLeader() throws GameActionException {
    	int currentRound = rc.getRoundNum();
    	for (int channel = Constants.CHANNEL_MIN_ARCHON; channel <= RobotPlayer.myChannel; channel++) {
    		int lastCheckIn = rc.readBroadcast(channel);
    		if ( currentRound - lastCheckIn > 0 && lastCheckIn != 0 ) {
    			rc.broadcast(channel, 0);
    			Communication.zeroComms(channel);
    			int numArchons = rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON);
    			rc.broadcast(Constants.CHANNEL_COUNT_ARCHON, --numArchons);
    		} else {
    			return channel == RobotPlayer.myChannel;
    		}
    	}
    	return false;
    }
    
    /**
     * Cleans up dead robots.
     * 
     * @throws GameActionException
     */
    static void bringOutYourDead() throws GameActionException {
    	int currentRound = rc.getRoundNum();
    	for (int channel = Constants.CHANNEL_MIN_GARDENER; channel <= Constants.CHANNEL_MAX; channel++) {
    		int lastCheckIn = rc.readBroadcast(channel);
    		if (currentRound - lastCheckIn > 1 && lastCheckIn != 0) {
    			rc.broadcast(channel, 0);
    			System.out.println("Robot died on channel " + channel);
    			Communication.zeroComms(channel);
    			int countChannel = Communication.getCountChannel(channel);
    			System.out.println("Robot count channel " + countChannel);
    			int numRobotsOfType = rc.readBroadcast(countChannel);
    			rc.broadcast(countChannel, --numRobotsOfType);
    			System.out.println("Robots left " + numRobotsOfType);
    		}
    	}
    }
    
    static void tryHireGardener() throws GameActionException {
    	int maxChecks = 9;
    	float radianOffset = Constants.TWO_PI / maxChecks;
    	for (float check = 0.0f; check < Constants.TWO_PI; check += radianOffset) {
    		Direction currentDirection = new Direction(check);
    		MapLocation targetLocation = rc.getLocation().add(currentDirection, RobotPlayer.myType.bodyRadius + GameConstants.GENERAL_SPAWN_OFFSET);
    		rc.setIndicatorDot(targetLocation, 0, 0, 0);
    		if (rc.canBuildRobot(RobotType.GARDENER, currentDirection)) {
    			rc.buildRobot(RobotType.GARDENER, currentDirection);
    		}
    	}
    }
}