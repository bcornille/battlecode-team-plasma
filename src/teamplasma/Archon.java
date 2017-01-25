package teamplasma;

import battlecode.common.*;

public class Archon {
	
	static RobotController rc = RobotPlayer.rc;
	
	static boolean amLeader = false;
	
    /**
     * run():
     * 		Main control method for RobotType Archon
     * 
     * @param rc
     * @throws GameActionException
     */
    static void run(RobotController rc) throws GameActionException {
        Archon.rc = rc;
        // Archons count themselves on turn 1
        Communication.countMe(Constants.CHANNEL_COUNT_ARCHON);
        // First Archon scouts map        
        if(rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON) == 1) {
        	mapGuess();
        }
        // get starting value
        int start = rc.readBroadcast(Constants.CHANNEL_BUILD_DIRECTION);
        // get build direction
        if (start == 1) {
        	Direction buildDirection = Direction.EAST;
        } else if (start == 2) {
        	Direction buildDirection = Direction.WEST;
        } else if (start == 3) {
        	Direction buildDirection = Direction.NORTH;
        } else if (start == 4) {
        	Direction buildDirection = Direction.SOUTH;
        } else {
        	
        }
        // get move direction
        if (start == 1) {
        	RobotPlayer.myDirection = Direction.WEST;
        } else if (start == 2) {
        	RobotPlayer.myDirection  = Direction.EAST;
        } else if (start == 3) {
        	RobotPlayer.myDirection = Direction.SOUTH;
        } else if (start == 4) {
        	RobotPlayer.myDirection = Direction.NORTH;
        } else {
        	
        }
        
        // TODO: Also check for tree density, adjust strategies
        
        // Code to run every turn
        while (true) {
            try {
            	// Check in every turn    	
            	RobotPlayer.checkIn();
            	// Check if this Archon is the leader
            	amLeader = electLeader();
            	if (amLeader) {
            		// Leader Archon checks for dead robots
            		bringOutYourDead();
            	}

            	// Check scout spacing, update direction if necessary:
            	RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
            	// Adjust movement direction to dodge bullets
            	RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
            	// Move
            	RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection,30,5);

                // Randomly attempt to build a gardener in this direction
            	int numArchons = rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON);
            	int maxGardeners = Math.round((Constants.MAX_COUNT_GARDENER - numArchons) * rc.getRoundNum() / rc.getRoundLimit() + numArchons);
                if (rc.canHireGardener(RobotPlayer.myDirection.opposite()) && rc.readBroadcast(Constants.CHANNEL_COUNT_GARDENER) < maxGardeners) {
                    rc.hireGardener(RobotPlayer.myDirection.opposite());
                    Communication.countMe(Constants.CHANNEL_COUNT_GARDENER);
                }
                // End Turn
                RobotPlayer.shakeNearbyTree();
                RobotPlayer.endTurn();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * mapGuess():
     * 		Uses initial Archon locations to setup a rough size of the
     * 		map. This information can be used for early game decisions.
     * 
     * TODO: Add early game decisions based off map size
     * 
     * @throws GameActionException
     */
    static void mapGuess() throws GameActionException {      
        // Get necessary Archon information
        MapLocation myLocation = rc.getLocation();
        MapLocation[] myArchons = rc.getInitialArchonLocations(RobotPlayer.myTeam);
        MapLocation[] enemyArchons = rc.getInitialArchonLocations(RobotPlayer.enemyTeam);
        int numArchons = myArchons.length;
        // Initialize map variable with Leader location
        float xmin = myLocation.x;
        float xmax = myLocation.x;
        float ymin = myLocation.y;
        float ymax = myLocation.y;
        // Locate all Archons to get rough outline of map and map center
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
        // Save the map information 
        Communication.setMapEdge(xmin, xmax, ymin, ymax);
        
        // Determine starting position
        float toxmin = Math.abs(myLocation.x - xmin);
        float toxmax = Math.abs(myLocation.x - xmax);
        float toymin = Math.abs(myLocation.y - ymin);
        float toymax = Math.abs(myLocation.y - ymax);
        
        float nearest = Math.min(Math.min(toxmin, toxmax), Math.min(toymin, toymax));

        if ( nearest == toxmin ) {
        	// Left Side
    		rc.broadcast(Constants.CHANNEL_BUILD_DIRECTION, 1);
        } else  if ( nearest == toxmax ) {
        	// Right Side
    		rc.broadcast(Constants.CHANNEL_BUILD_DIRECTION, 2);
        } else  if ( nearest == toymin ) {
        	// Bottom Side
    		rc.broadcast(Constants.CHANNEL_BUILD_DIRECTION, 3);
        } else  if ( nearest == toymax ) {
        	// Top Side
    		rc.broadcast(Constants.CHANNEL_BUILD_DIRECTION, 4);
        } else {
        	// error
        }
        
    }
    
    static enum startPosition {
    	BotLeft, BotRight, TopLeft, TopRight
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
}