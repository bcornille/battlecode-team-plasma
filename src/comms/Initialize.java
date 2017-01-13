package comms;

import battlecode.common.*;

public class Initialize {

	static RobotController rc = RobotPlayer.rc;
	
	// Add one to the count for a robot of a given type
	static void cntRobot(int channel) throws GameActionException {
		
    	int cnt = rc.readBroadcast(channel);
		rc.broadcast(channel, cnt+1);
		
	}
	
	// Initialize comms channels for a new robot
	static void setupMyComms( int channel_min, int channel_max ) throws GameActionException {    	
		
		RobotType myType = rc.getType();
		int myID = rc.getID();
    	
		int myIndex = -1;    	
     
    	for (int i=channel_min; i<=channel_max; i++){
    		int currentID = rc.readBroadcast(i);
    		if(currentID==0){
    			rc.broadcast(i, rc.getRoundNum());
    			rc.broadcast(i*10, myID);
    			myIndex = i;
    			break;
    		} else if (i==channel_max) {
    			System.out.println("Out of storage");
    			break;
    		} else {
    			// do nothing
    		}   	
    	}
    	
		RobotPlayer.channel = myIndex;
		RobotPlayer.id = myID;
		
	}	
 
}
