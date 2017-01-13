package teamplasma;

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
}