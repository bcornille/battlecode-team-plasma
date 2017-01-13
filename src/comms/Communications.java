package comms;

import battlecode.common.*;

public class Communications {

	static RobotController rc = RobotPlayer.rc;
	
	// Add one to the count for a robot of a given type
	static void cntRobot(int channel) throws GameActionException {
		
    	int cnt = rc.readBroadcast(channel);
		rc.broadcast(channel, cnt+1);
		
	}
	
	// Subtract one to the count for a robot of a given type
	// Reset comms channels for dead robot (WIP) 
	static void killRobot(int channel) throws GameActionException {
		
    	int cnt = rc.readBroadcast(channel);
		rc.broadcast(channel, cnt-1);
		
	}
	
	// Setup the comm channels for a new robot (WIP)
	static void setupComms( Direction dir, RobotType newType, int channel_min, int channel_max ) throws GameActionException {
		
//    	int myID = rc.getID(); //wrong id. need build robot id
    	
		RobotType myType = rc.getType();
    	MapLocation myLocation = rc.getLocation();
    	    	
    	float mySize = myType.bodyRadius;
    	float newSize = newType.bodyRadius;
    	float dist = mySize+newSize+GameConstants.GENERAL_SPAWN_OFFSET;
    	
    	MapLocation newLocation = myLocation.add(dir,dist);
    	
    	RobotInfo newRobot = rc.senseRobotAtLocation(newLocation);
    	int newID = newRobot.ID;
    	
		int newIndex = -1;    	
    	
    	for (int i=channel_min; i<=channel_max; i++){
    		
    		int currentID = rc.readBroadcast(i);
    		    		
    		if(currentID==0){
    			
    			rc.broadcast(i, newID);
    			
    			newIndex = i;
    			
    			break;
    			
    		} else if (i==channel_max) {
    			
    			System.out.println("Out of storage");
    			
    			break;
    			
    		}	else {

    		}   		
   		
    	}
    	
		RobotPlayer.channel = newIndex;
		RobotPlayer.id = newID;
		
	}
	

	
 
}
