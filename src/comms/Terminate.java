package comms;

import battlecode.common.*;

import static comms.Constants.*;

public class Terminate {
	
	static RobotController rc = RobotPlayer.rc;
	
	
	// Get the channel for the type of robot for the counter
	static int getChannel(int i) {
		
		int channel = 0;
		
		if (i >= CHANNEL_MIN_ARCHON && i <= CHANNEL_MAX_ARCHON) {
			channel = 1;
		} else if (i >= CHANNEL_MIN_GARDENER && i <= CHANNEL_MAX_GARDENER) {
			channel = 2;
		} else if (i >= CHANNEL_MIN_LUMBERJACK && i <= CHANNEL_MAX_LUMBERJACK) {
			channel = 3;
		} else if (i >= CHANNEL_MIN_SOLDIER && i <= CHANNEL_MAX_SOLDIER) {
			channel = 4;
		} else if (i >= CHANNEL_MIN_TANK && i <= CHANNEL_MAX_TANK) {
			channel = 5;
		} else if (i >= CHANNEL_MIN_SCOUT && i <= CHANNEL_MAX_SCOUT) {
			channel = 6;
		} else {
			System.out.println("Invalid Option");
		}
		
		return channel;
		
	}
	
	// Remove one to the count for a robot of a given type
	static void cntRobot(int channel) throws GameActionException {
		
    	int cnt = rc.readBroadcast(channel);
    	
    	if (cnt > 0){
    		rc.broadcast(channel, cnt-1);
    	} else {
    		
    	}
    	
	}
	
	// Clear the dead robot's comms channels
	static void clearComms(int i) throws GameActionException {
		
		rc.broadcast(i, 0);
		
		rc.broadcast(i*10+0, 0);
		rc.broadcast(i*10+1, 0);
		rc.broadcast(i*10+2, 0);
		rc.broadcast(i*10+3, 0);
		rc.broadcast(i*10+4, 0);
		rc.broadcast(i*10+5, 0);
		rc.broadcast(i*10+6, 0);
		rc.broadcast(i*10+7, 0);
		rc.broadcast(i*10+8, 0);
		rc.broadcast(i*10+9, 0);

		
	}
	
	
}
