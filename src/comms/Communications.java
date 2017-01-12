package comms;

import battlecode.common.*;

public class Communications {

	static RobotController rc = RobotPlayer.rc;
	
	  /**
     * Setups a check-in function for all robots at the start of each turn. Checking in 
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static int robotCheckIn() throws GameActionException {
    	
    	int myID = rc.getID();
    	RobotType myType = rc.getType();
    	
    	// indexCount - channel for counting number of robots of each type
    	// indexIdMin - first ID channel for robots of a certain type 
    	// indexIdMax - last ID channel for robots of a certain type
    	int indexCount, indexIdMin, indexIdMax;
    	switch(myType){
    		case ARCHON: {
    			indexCount = 1;
    			indexIdMin = 10;
    			indexIdMax = 12;
    			break;
    		}	
    		case GARDENER: {
    			indexCount = 2;
    			indexIdMin = 13;
    			indexIdMax = 15;
    			break;
    		}	
    		case LUMBERJACK: {
    			indexCount = 3;
    			indexIdMin = 40;
    			indexIdMax = 59;
    			break;
    		}	
    		case SOLDIER: {
    			indexCount = 4;
    			indexIdMin = 60;
    			indexIdMax = 79;
    			break;
    		}	
    		case TANK: {
    			indexCount = 5;
    			indexIdMin = 80;
    			indexIdMax = 89;
    			break;
    		}	
    		case SCOUT: {
    			indexCount = 6;
    			indexIdMin = 10;
    			indexIdMax = 99;
    			break;  
    		}	
    		default:
    			indexCount = 0;
    			indexIdMin = 16;
    			indexIdMax = 99;
    			break;
    	}
    	
    	int myIndex = -1;    	
    	    	
    	for (int i=indexIdMin; i<=indexIdMax; i++){
    		
    		int currentID = rc.readBroadcast(i);
    		    		
    		if(currentID==0){
    			
    			int cnt = rc.readBroadcast(indexCount);
    			rc.broadcast(indexCount, cnt+1);
    			rc.broadcast(i, myID);
    			
    			MapLocation myLocation = rc.getLocation();
    			rc.broadcast(i*10+0,(int)myLocation.x);
    			rc.broadcast(i*10+1,(int)myLocation.x);
    			
    			myIndex = i;
    			
    			break;
    			
    		} else if(currentID==myID) {
    			
    			MapLocation myLocation = rc.getLocation();
    			rc.broadcast(i*10+0,(int)myLocation.x);
    			rc.broadcast(i*10+1,(int)myLocation.x);
    			
    			myIndex = i;
    			
    			break;
    			
    		} else if (i==indexIdMax) {
    			
    			System.out.println("Out of storage");
    			
    			break;
    			
    		}	else {

    		}
    		
   		
    	}
    	
    	
    	int x = rc.readBroadcast(6);
    	System.out.println(x);
    	
    	return myIndex;
	
    	
    	
    }
    
}
