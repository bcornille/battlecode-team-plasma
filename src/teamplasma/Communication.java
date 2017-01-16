package teamplasma;

import static comms.Constants.CHANNEL_MAX_ARCHON;
import static comms.Constants.CHANNEL_MAX_GARDENER;
import static comms.Constants.CHANNEL_MAX_LUMBERJACK;
import static comms.Constants.CHANNEL_MAX_SCOUT;
import static comms.Constants.CHANNEL_MAX_SOLDIER;
import static comms.Constants.CHANNEL_MAX_TANK;
import static comms.Constants.CHANNEL_MIN_ARCHON;
import static comms.Constants.CHANNEL_MIN_GARDENER;
import static comms.Constants.CHANNEL_MIN_LUMBERJACK;
import static comms.Constants.CHANNEL_MIN_SCOUT;
import static comms.Constants.CHANNEL_MIN_SOLDIER;
import static comms.Constants.CHANNEL_MIN_TANK;

import battlecode.common.*;

public class Communication {
	static RobotController rc = RobotPlayer.rc;
	
	static int getOpenChannel(int channelMin, int channelMax) throws GameActionException {
		
    	for (int i=channelMin; i<=channelMax; i++){
    		int channelContents = rc.readBroadcast(i);
    		if(channelContents==0){
    			rc.broadcast(i, rc.getRoundNum());
    			rc.broadcast(i*10, RobotPlayer.myID);
    			return i;
    		} else if (i==channelMax) {
    			System.out.println("Out of storage");
    			break;
    		}
    	}
    	
    	return -1;

	}
	
	static int getOpenChannel(RobotType type) throws GameActionException {
		
		switch (type) {
		case ARCHON:
            return getOpenChannel(Constants.CHANNEL_MIN_ARCHON, Constants.CHANNEL_MAX_ARCHON);
        case GARDENER:
        	return getOpenChannel(Constants.CHANNEL_MIN_GARDENER, Constants.CHANNEL_MAX_GARDENER);
        case SOLDIER:
        	return getOpenChannel(Constants.CHANNEL_MIN_SOLDIER, Constants.CHANNEL_MAX_SOLDIER);
        case TANK:
        	return getOpenChannel(Constants.CHANNEL_MIN_TANK, Constants.CHANNEL_MAX_TANK);
        case LUMBERJACK:
        	return getOpenChannel(Constants.CHANNEL_MIN_LUMBERJACK, Constants.CHANNEL_MAX_LUMBERJACK);
        case SCOUT:
        	return getOpenChannel(Constants.CHANNEL_MIN_SCOUT, Constants.CHANNEL_MAX_SCOUT);
        default:
        	return -1;
		}
	}
	
	static void countMe(int channel) throws GameActionException {
		int count = rc.readBroadcast(channel);
		rc.broadcast(channel, ++count);
	}
	
	static void countMe(RobotType type) throws GameActionException {
		
		switch (type) {
		case ARCHON:
            countMe(Constants.CHANNEL_COUNT_ARCHON);
            break;
        case GARDENER:
        	countMe(Constants.CHANNEL_COUNT_GARDENER);
            break;
        case SOLDIER:
        	countMe(Constants.CHANNEL_COUNT_SOLDIER);
            break;
        case TANK:
        	countMe(Constants.CHANNEL_COUNT_TANK);
        	break;
        case LUMBERJACK:
        	countMe(Constants.CHANNEL_COUNT_LUMBERJACK);
            break;
        case SCOUT:
        	countMe(Constants.CHANNEL_COUNT_SCOUT);
        	break;
        default:
        	System.out.println("An unkown RobotType has appeared!");
		}
	}
	
	static void zeroComms(int id) throws GameActionException {
		int start = Constants.CHANNEL_MAX + Constants.NUM_MESSAGE_CHANNELS*(id - Constants.CHANNEL_MIN) + 1;
		int end = start + Constants.NUM_MESSAGE_CHANNELS - 1;
		for (int channel = start; channel <= end; channel++) {
			rc.broadcast(channel, 0);
		}
	}
	
	// Get the channel for the type of robot for the counter
	static int getCountChannel(int i) {

		int channel = 0;

		if (i >= CHANNEL_MIN_ARCHON && i <= CHANNEL_MAX_ARCHON) {
			channel = Constants.CHANNEL_COUNT_ARCHON;
		} else if (i >= CHANNEL_MIN_GARDENER && i <= CHANNEL_MAX_GARDENER) {
			channel = Constants.CHANNEL_COUNT_GARDENER;
		} else if (i >= CHANNEL_MIN_LUMBERJACK && i <= CHANNEL_MAX_LUMBERJACK) {
			channel = Constants.CHANNEL_COUNT_LUMBERJACK;
		} else if (i >= CHANNEL_MIN_SOLDIER && i <= CHANNEL_MAX_SOLDIER) {
			channel = Constants.CHANNEL_COUNT_SOLDIER;
		} else if (i >= CHANNEL_MIN_TANK && i <= CHANNEL_MAX_TANK) {
			channel = Constants.CHANNEL_COUNT_TANK;
		} else if (i >= CHANNEL_MIN_SCOUT && i <= CHANNEL_MAX_SCOUT) {
			channel = Constants.CHANNEL_COUNT_SCOUT;
		} else {
			System.out.println("Invalid check-in channel option");
		}

		return channel;

	}
}