package teamplasma;

import battlecode.common.*;

public class Archon {
	
	/*-------------------------*
	 * ARCHON GLOBAL VARIABLES *
	 *-------------------------*/
	
	static RobotController rc = RobotPlayer.rc;
	
	static MapLocation myLocation;
	static MapLocation groveCenter;

	static Direction buildDirection;
	static Direction moveDirection;
	
	static boolean onMap = false;
	static boolean amLeader = false;

	
    /**
     * Main control method for RobotType Archon
     * 
     * @param rc
     * @throws GameActionException
     */
    static void run(RobotController rc) throws GameActionException {

    	// First Archon has additional startup
    	if (rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON)==0) {
    		firstArchonSetup();
    	}
    	
    	// Setup for all Archons
    	initialize();
    	        
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
            		
            		// Check grove mesh
            		checkGroves();

            	}
                    
                // Attempt to build a gardener in this direction
            	int numArchons = rc.readBroadcast(Constants.CHANNEL_COUNT_ARCHON);
            	int maxGardeners = Math.round((Constants.MAX_COUNT_GARDENER - numArchons) * rc.getRoundNum() / rc.getRoundLimit()+1);
                if (rc.canHireGardener(buildDirection) && rc.readBroadcast(Constants.CHANNEL_COUNT_GARDENER) < maxGardeners) {
                    rc.hireGardener(buildDirection);
                    Communication.countMe(Constants.CHANNEL_COUNT_GARDENER);
                }
                
                // Stay in box
                myLocation = rc.getLocation();
                
                MapLocation futureLocation = myLocation.add(moveDirection, RobotType.ARCHON.strideRadius);
                
                if (
                	futureLocation.x < rc.readBroadcastFloat(Constants.CHANNEL_GROVE_XMIN) ||
                	futureLocation.x > rc.readBroadcastFloat(Constants.CHANNEL_GROVE_XMAX) ||
                	futureLocation.y < rc.readBroadcastFloat(Constants.CHANNEL_GROVE_YMIN) ||
                	futureLocation.y > rc.readBroadcastFloat(Constants.CHANNEL_GROVE_YMAX)                	
                	){
                	moveDirection = moveDirection.opposite();
                }
                
            	// Move
            	moveDirection = Movement.tryMove(moveDirection,90,1);
                
                
                // End Turn
                RobotPlayer.endTurn();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }   
    }
    
    static void firstArchonSetup() throws GameActionException {
       
    	/*----------------*
    	 * MAP BOUNDARIES *
    	 *----------------*/
    	
    	System.out.println("Mappping Boundaries");
    	
        myLocation = rc.getLocation();

        MapLocation[] enemyArchons = rc.getInitialArchonLocations(RobotPlayer.enemyTeam);
        MapLocation[] myArchons = rc.getInitialArchonLocations(RobotPlayer.myTeam);
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
           	
    	/*------------------------*
    	 * MAP STARTING POSITIONS *
    	 *------------------------*/
        
        System.out.println("Start Locations");
    	
        float toxmin = Math.abs(myLocation.x - xmin);
        float toxmax = Math.abs(myLocation.x - xmax);
        float toymin = Math.abs(myLocation.y - ymin);
        float toymax = Math.abs(myLocation.y - ymax);
        
        // find closes edge
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
    	
    	/*-------------------*
    	 * FIRST GROVE SETUP *
    	 *-------------------*/
        
        System.out.println("Grove Setup");
        
        for (int i = 0; i < Constants.NUM_GROVE_MAX; i++) {
        	rc.broadcastBoolean(Constants.CHANNEL_GROVE_LOCATIONS+i, false);
        	rc.broadcastBoolean(Constants.CHANNEL_GROVE_ASSIGNED+i, false);
        }
        
    	myLocation = rc.getLocation();

    	groveCenter = myLocation.add(buildDirection, RobotType.ARCHON.bodyRadius+RobotType.GARDENER.bodyRadius);
    	
    	// setup first grove location
    	rc.broadcastBoolean(Constants.CHANNEL_GROVE_LOCATIONS, true);
    	rc.broadcastBoolean(Constants.CHANNEL_GROVE_ASSIGNED, false);
    	rc.broadcastFloat(Constants.CHANNEL_GROVE_X, groveCenter.x);
    	rc.broadcastFloat(Constants.CHANNEL_GROVE_Y, groveCenter.y);
    	
        // Save the grove information 
        Communication.setGroveEdge(
        		groveCenter.x-RobotType.ARCHON.sensorRadius, 
        		groveCenter.x+RobotType.ARCHON.sensorRadius,
        		groveCenter.y-RobotType.ARCHON.sensorRadius,
        		groveCenter.y+RobotType.ARCHON.sensorRadius);

    }
    
    static void initialize() throws GameActionException {
    	
        // Archons count themselves
        Communication.countMe(Constants.CHANNEL_COUNT_ARCHON);
       
        // get starting value
        int start = rc.readBroadcast(Constants.CHANNEL_BUILD_DIRECTION);
        
        // get build direction
        if (start == 1) {
        	buildDirection = Direction.EAST;
        } else if (start == 2) {
        	buildDirection = Direction.WEST;
        } else if (start == 3) {
        	buildDirection = Direction.NORTH;
        } else if (start == 4) {
        	buildDirection = Direction.SOUTH;
        } else {
        	
        }
        
        // get move direction
        moveDirection = buildDirection.opposite();
        
        // TODO: Also check for tree density, adjust strategies
        
        // TODO: Add early game decisions based off map size


        
        
        
    }
    
    static void checkGroves() throws GameActionException {
    	
    	float xmin = rc.readBroadcastFloat(Constants.CHANNEL_GROVE_XMIN); 
    	float xmax = rc.readBroadcastFloat(Constants.CHANNEL_GROVE_XMAX); 
    	float ymin = rc.readBroadcastFloat(Constants.CHANNEL_GROVE_YMIN); 
    	float ymax = rc.readBroadcastFloat(Constants.CHANNEL_GROVE_YMAX);
    	
        for (int i = 0; i < Constants.NUM_GROVE_MAX; i++) {
        	if(rc.readBroadcastBoolean(Constants.CHANNEL_GROVE_LOCATIONS+i)) {	
            	
				float groveX = rc.readBroadcastFloat(Constants.CHANNEL_GROVE_X+i);
				float groveY = rc.readBroadcastFloat(Constants.CHANNEL_GROVE_Y+i);
        		
            	xmin = Math.min(xmin, groveX);
            	xmax = Math.max(xmax, groveX);
            	ymin = Math.min(ymin, groveY);
            	ymax = Math.max(ymax, groveY);

            	Communication.setGroveEdge(xmin, xmax, ymin, ymax);
            	
				groveCenter = new MapLocation(groveX,groveY);
				rc.setIndicatorDot(groveCenter, 0, 0, 0);
				
				MapLocation grovePt1 = new MapLocation(xmin,ymin);
				MapLocation grovePt2 = new MapLocation(xmin,ymax);
				MapLocation grovePt3 = new MapLocation(xmax,ymin);
				MapLocation grovePt4 = new MapLocation(xmax,ymax);
				
				rc.setIndicatorLine(grovePt1, grovePt2, 0, 0, 0);
				rc.setIndicatorLine(grovePt1, grovePt3, 0, 0, 0);
				rc.setIndicatorLine(grovePt4, grovePt2, 0, 0, 0);
				rc.setIndicatorLine(grovePt4, grovePt3, 0, 0, 0);
				
				
				
        	} else {
        		break;
        	}
        }
        
        
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