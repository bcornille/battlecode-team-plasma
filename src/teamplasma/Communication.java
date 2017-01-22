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
	
	static void countMe(int channel) throws GameActionException {
		int count = rc.readBroadcast(channel);
		System.out.println("Print channel:  " + channel);
		rc.broadcast(channel, ++count);
		System.out.println("Robot count:  " + count);
	}
	
	static void countMe(RobotType type) throws GameActionException {
		
		switch (type) {
		case ARCHON:
			System.out.println("New Archon");
            countMe(Constants.CHANNEL_COUNT_ARCHON);
            break;
        case GARDENER:
        	System.out.println("New Gardener");
        	countMe(Constants.CHANNEL_COUNT_GARDENER);
            break;
        case SOLDIER:
        	System.out.println("New Soldier");
        	countMe(Constants.CHANNEL_COUNT_SOLDIER);
            break;
        case TANK:
        	System.out.println("New Tank");
        	countMe(Constants.CHANNEL_COUNT_TANK);
        	break;
        case LUMBERJACK:
        	System.out.println("New Lumberjack");
        	countMe(Constants.CHANNEL_COUNT_LUMBERJACK);
            break;
        case SCOUT:
        	System.out.println("New Scout");
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

		if (i >= Constants.CHANNEL_MIN_ARCHON && i <= Constants.CHANNEL_MAX_ARCHON) {
			return Constants.CHANNEL_COUNT_ARCHON;
		} else if (i >= Constants.CHANNEL_MIN_GARDENER && i <= Constants.CHANNEL_MAX_GARDENER) {
			return Constants.CHANNEL_COUNT_GARDENER;
		} else if (i >= Constants.CHANNEL_MIN_LUMBERJACK && i <= Constants.CHANNEL_MAX_LUMBERJACK) {
			return Constants.CHANNEL_COUNT_LUMBERJACK;
		} else if (i >= Constants.CHANNEL_MIN_SOLDIER && i <= Constants.CHANNEL_MAX_SOLDIER) {
			return Constants.CHANNEL_COUNT_SOLDIER;
		} else if (i >= Constants.CHANNEL_MIN_TANK && i <= Constants.CHANNEL_MAX_TANK) {
			return Constants.CHANNEL_COUNT_TANK;
		} else if (i >= Constants.CHANNEL_MIN_SCOUT && i <= Constants.CHANNEL_MAX_SCOUT) {
			return Constants.CHANNEL_COUNT_SCOUT;
		} else {
			System.out.println("Invalid check-in channel option");
			return -1;
		}

	}
	
	static void broadcastFloat(int channel, float value) throws GameActionException {
		rc.broadcast(channel, Float.floatToRawIntBits(value));
	}
	
	static float readBroadcastFloat(int channel) throws GameActionException{
		return Float.intBitsToFloat(rc.readBroadcast(channel));
	}

	static void setMapEdge(float xmin, float xmax, float ymin, float ymax) throws GameActionException {
		float xcen = (xmin+xmax)/2;
        float ycen = (ymin+ymax)/2;
        RobotPlayer.mapCenter = new MapLocation(xcen, ycen);
        broadcastFloat(Constants.CHANNEL_MAP_XMIN, xmin);
		broadcastFloat(Constants.CHANNEL_MAP_XMAX, xmax);
		broadcastFloat(Constants.CHANNEL_MAP_YMIN, ymin);
		broadcastFloat(Constants.CHANNEL_MAP_YMAX, ymax);
        broadcastFloat(Constants.CHANNEL_MAP_XCEN, xcen);
        broadcastFloat(Constants.CHANNEL_MAP_YCEN, ycen);
	}
	
	static void updateMapEdge(MapLocation position) throws GameActionException {
		float xmin = Math.min(position.x,readBroadcastFloat(Constants.CHANNEL_MAP_XMIN));
		float xmax = Math.max(position.x,readBroadcastFloat(Constants.CHANNEL_MAP_XMAX));
		float ymin = Math.min(position.y,readBroadcastFloat(Constants.CHANNEL_MAP_YMIN));
		float ymax = Math.max(position.y,readBroadcastFloat(Constants.CHANNEL_MAP_YMAX));
		Communication.setMapEdge(xmin,xmax,ymin,ymax); 
	}
	
	static void broadcastMapCenter(MapLocation center) throws GameActionException {
		broadcastFloat(Constants.CHANNEL_MAP_XCEN, center.x);
		broadcastFloat(Constants.CHANNEL_MAP_YCEN, center.y);
	}
	
	static MapLocation readMapCenter() throws GameActionException {
		return new MapLocation(
				readBroadcastFloat(Constants.CHANNEL_MAP_XCEN),
				readBroadcastFloat(Constants.CHANNEL_MAP_YCEN)
				);
	}
	
}