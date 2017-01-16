package comms;

import battlecode.common.*;
import comms.*;
import static comms.Constants.*;

public class Archon {
	
	static RobotController rc = RobotPlayer.rc;
	


	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
    	
        Start();
        
        while (true) {

            try {
   	
        		// Check in
            	rc.broadcast(RobotPlayer.channel, rc.getRoundNum());
            	
            	// Check for dead, clean up comms
            	for (int i=CHANNEL_MIN; i<=CHANNEL_MAX; i++) {
            		
            		int currentRound = rc.getRoundNum();            		
            		int checkRound = rc.readBroadcast(i);
            		int diffRound = currentRound - checkRound;
            		
            		if (diffRound > 5 && diffRound!=currentRound) {
            			
            			int channel = Communications.getChannel(i);
            			
            			Communications.delRobot(channel);
            			
            			Communications.clearComms(i);            
            			
            		}
            	}           	
            	
            	// If we need Gardeners, build them
                int numGardener = rc.readBroadcast(CHANNEL_COUNT_GARDENER);
                if (numGardener < MAX_COUNT_GARDENER) {                
                	Build.Gardener();
                }

                // Move randomly
                Movement.tryMove(Movement.randomDirection());

                
            	
            	if (rc.getRoundNum() > 5) {
            		rc.resign();
            	}
                
                // end turn
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
            
        } 
    }
    
    /**
     * Archon.Start - turn one actions for the Archons
     * 
     * Goal is to get as much information as possible from the Archons on turn one of the match.
     * 
     */
	static void Start() throws GameActionException {

		// Count Archon
		Communications.cntRobot(CHANNEL_COUNT_ARCHON);
		
		// Setup comms channel
		Communications.setupMyComms(CHANNEL_MIN_ARCHON, CHANNEL_MAX_ARCHON);
    	
    	// Only leader Archon does map setup
		if(rc.readBroadcast(CHANNEL_COUNT_ARCHON)==1) {
			
	    	Team myTeam = rc.getTeam();	
	        Team enemyTeam = rc.getTeam().opponent();        
	       
	        MapLocation myLocation = rc.getLocation();
	        MapLocation[] myArchons = rc.getInitialArchonLocations(myTeam);
	        MapLocation[] enemyArchons = rc.getInitialArchonLocations(enemyTeam);
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
	        
	        float xcen = (xmin+xmax)/2;
	        float ycen = (ymin+ymax)/2;
	       
	        
		} else {
			// Do Nothing
		}

    //	rc.getInitialArchonLocations()
        
//    	// Initialize map edges
//        MapLocation initialLocation = rc.getLocation();
//        if (rc.readBroadcast(1)==1){
//        	rc.broadcast(CHANNEL_MAP_XMIN, (int)initialLocation.x);
//        	rc.broadcast(CHANNEL_MAP_XMAX, (int)initialLocation.x);
//        	rc.broadcast(CHANNEL_MAP_YMIN, (int)initialLocation.y);
//        	rc.broadcast(CHANNEL_MAP_YMAX, (int)initialLocation.y);
//        }
        
	}    
}
