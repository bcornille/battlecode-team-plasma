package teamplasma;

import battlecode.common.*;

public class Communication {
	static RobotController rc = RobotPlayer.rc;
	
	static int getOpenChannel(int channelMin, int channelMax) throws GameActionException {
		
    	for (int i=channelMin; i<=channelMax; i++){
    		int channelContents = rc.readBroadcast(i);
    		if(channelContents==0){
    			rc.broadcast(i, rc.getRoundNum());
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
            return getOpenChannel(Channels.MIN_ARCHON, Channels.MAX_ARCHON);
        case GARDENER:
        	return getOpenChannel(Channels.MIN_GARDENER, Channels.MAX_GARDENER);
        case SOLDIER:
        	return getOpenChannel(Channels.MIN_SOLDIER, Channels.MAX_SOLDIER);
        case TANK:
        	return getOpenChannel(Channels.MIN_TANK, Channels.MAX_TANK);
        case LUMBERJACK:
        	return getOpenChannel(Channels.MIN_LUMBERJACK, Channels.MAX_LUMBERJACK);
        case SCOUT:
        	return getOpenChannel(Channels.MIN_SCOUT, Channels.MAX_SCOUT);
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
			System.out.println("New Archon");
            countMe(Channels.COUNT_ARCHON);
            break;
        case GARDENER:
        	System.out.println("New Gardener");
        	countMe(Channels.COUNT_GARDENER);
            break;
        case SOLDIER:
        	System.out.println("New Soldier");
        	countMe(Channels.COUNT_SOLDIER);
            break;
        case TANK:
        	System.out.println("New Tank");
        	countMe(Channels.COUNT_TANK);
        	break;
        case LUMBERJACK:
        	System.out.println("New Lumberjack");
        	countMe(Channels.COUNT_LUMBERJACK);
            break;
        case SCOUT:
        	System.out.println("New Scout");
        	countMe(Channels.COUNT_SCOUT);
        	break;
        default:
        	System.out.println("An unknown RobotType has appeared!");
		}
	}
	
	static void zeroComms(int idChannel) throws GameActionException {
		int start = Channels.MAX_ROBOT + Constants.NUM_MESSAGE_CHANNELS*(idChannel - Channels.MIN_ROBOT) + 1;
		int end = start + Constants.NUM_MESSAGE_CHANNELS - 1;
		for (int channel = start; channel <= end; channel++) {
			rc.broadcast(channel, 0);
		}
	}
	
	// Get the channel for the type of robot for the counter
	static int getCountChannel(int i) {

		if (i >= Channels.MIN_ARCHON && i <= Channels.MAX_ARCHON) {
			return Channels.COUNT_ARCHON;
		} else if (i >= Channels.MIN_GARDENER && i <= Channels.MAX_GARDENER) {
			return Channels.COUNT_GARDENER;
		} else if (i >= Channels.MIN_LUMBERJACK && i <= Channels.MAX_LUMBERJACK) {
			return Channels.COUNT_LUMBERJACK;
		} else if (i >= Channels.MIN_SOLDIER && i <= Channels.MAX_SOLDIER) {
			return Channels.COUNT_SOLDIER;
		} else if (i >= Channels.MIN_TANK && i <= Channels.MAX_TANK) {
			return Channels.COUNT_TANK;
		} else if (i >= Channels.MIN_SCOUT && i <= Channels.MAX_SCOUT) {
			return Channels.COUNT_SCOUT;
		} else {
			System.out.println("Invalid check-in channel option");
			return -1;
		}

	}

	static void setMapEdge(float xmin, float xmax, float ymin, float ymax) throws GameActionException {
		float xcen = (xmin+xmax)/2;
        float ycen = (ymin+ymax)/2;
        RobotPlayer.mapCenter = new MapLocation(xcen, ycen);
        rc.broadcastFloat(Channels.MAP_XMIN, xmin);
		rc.broadcastFloat(Channels.MAP_XMAX, xmax);
		rc.broadcastFloat(Channels.MAP_YMIN, ymin);
		rc.broadcastFloat(Channels.MAP_YMAX, ymax);
        rc.broadcastFloat(Channels.MAP_XCEN, xcen);
        rc.broadcastFloat(Channels.MAP_YCEN, ycen);
	}
	
	static void updateMapEdge(MapLocation position) throws GameActionException {
		float xmin = Math.min(position.x,rc.readBroadcastFloat(Channels.MAP_XMIN));
		float xmax = Math.max(position.x,rc.readBroadcastFloat(Channels.MAP_XMAX));
		float ymin = Math.min(position.y,rc.readBroadcastFloat(Channels.MAP_YMIN));
		float ymax = Math.max(position.y,rc.readBroadcastFloat(Channels.MAP_YMAX));
		Communication.setMapEdge(xmin,xmax,ymin,ymax); 
	}
	
	static void broadcastMapCenter(MapLocation center) throws GameActionException {
		rc.broadcastFloat(Channels.MAP_XCEN, center.x);
		rc.broadcastFloat(Channels.MAP_YCEN, center.y);
	}
	
	static MapLocation readMapCenter() throws GameActionException {
		return new MapLocation(
				rc.readBroadcastFloat(Channels.MAP_XCEN),
				rc.readBroadcastFloat(Channels.MAP_YCEN)
				);
	}
	

	static void setGroveEdge(int myNumber, float xmin, float xmax, float ymin, float ymax) throws GameActionException {
		float xcen = (xmin+xmax)/2;
        float ycen = (ymin+ymax)/2;
        RobotPlayer.mapCenter = new MapLocation(xcen, ycen);
        
        
        int CHANNEL_GROVE_XMIN;
        int CHANNEL_GROVE_XMAX;
        int CHANNEL_GROVE_XCEN;
        int CHANNEL_GROVE_YMIN;
        int CHANNEL_GROVE_YMAX;
        int CHANNEL_GROVE_YCEN;
        
    	switch(myNumber) {
    	case 1: 
    		CHANNEL_GROVE_XMIN = Channels.GROVE1_XMIN;
    		CHANNEL_GROVE_XMAX = Channels.GROVE1_XMAX;
    		CHANNEL_GROVE_XCEN = Channels.GROVE1_XCEN;
    		CHANNEL_GROVE_YMIN = Channels.GROVE1_YMIN;
    		CHANNEL_GROVE_YMAX = Channels.GROVE1_YMAX;
    		CHANNEL_GROVE_YCEN = Channels.GROVE1_YCEN;
    		break;
    	case 2:
    		CHANNEL_GROVE_XMIN = Channels.GROVE2_XMIN;
    		CHANNEL_GROVE_XMAX = Channels.GROVE2_XMAX;
    		CHANNEL_GROVE_XCEN = Channels.GROVE2_XCEN;
    		CHANNEL_GROVE_YMIN = Channels.GROVE2_YMIN;
    		CHANNEL_GROVE_YMAX = Channels.GROVE2_YMAX;
    		CHANNEL_GROVE_YCEN = Channels.GROVE2_YCEN;
    		break;
    	case 3:
    		CHANNEL_GROVE_XMIN = Channels.GROVE3_XMIN;
    		CHANNEL_GROVE_XMAX = Channels.GROVE3_XMAX;
    		CHANNEL_GROVE_XCEN = Channels.GROVE3_XCEN;
    		CHANNEL_GROVE_YMIN = Channels.GROVE3_YMIN;
    		CHANNEL_GROVE_YMAX = Channels.GROVE3_YMAX;
    		CHANNEL_GROVE_YCEN = Channels.GROVE3_YCEN;
    		break;
    	default:
    		CHANNEL_GROVE_XMIN = Channels.GROVE1_XMIN;
    		CHANNEL_GROVE_XMAX = Channels.GROVE1_XMAX;
    		CHANNEL_GROVE_XCEN = Channels.GROVE1_XCEN;
    		CHANNEL_GROVE_YMIN = Channels.GROVE1_YMIN;
    		CHANNEL_GROVE_YMAX = Channels.GROVE1_YMAX;
    		CHANNEL_GROVE_YCEN = Channels.GROVE1_YCEN;
    		break;
    	}
        
        rc.broadcastFloat(CHANNEL_GROVE_XMIN, xmin);
		rc.broadcastFloat(CHANNEL_GROVE_XMAX, xmax);
		rc.broadcastFloat(CHANNEL_GROVE_YMIN, ymin);
		rc.broadcastFloat(CHANNEL_GROVE_YMAX, ymax);
		rc.broadcastFloat(CHANNEL_GROVE_XCEN, xcen);
		rc.broadcastFloat(CHANNEL_GROVE_YCEN, ycen);
	}

	static MapLocation getEnemyArchonLocation() throws GameActionException {
		for (int i = Channels.ENEMY_ARCHON1_START; i <= Channels.ENEMY_ARCHON_END; i += Channels.ENEMY_ARCHON_OFFSET) {
			System.out.println(rc.readBroadcast(i));
			System.out.println(rc.readBroadcastFloat(i + 1));
			System.out.println(rc.readBroadcastFloat(i + 2));
			System.out.println(rc.readBroadcast(i + 3));
		}
		if (rc.readBroadcast(Channels.ENEMY_ARCHON1_START) != 0
				&& rc.readBroadcast(Channels.ENEMY_ARCHON1_START + 3) > rc.getRoundNum() - 2) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 2));
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON2_START) != 0
				&& rc.readBroadcast(Channels.ENEMY_ARCHON2_START + 3) > rc.getRoundNum() - 2) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 2));
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON3_START) != 0
				&& rc.readBroadcast(Channels.ENEMY_ARCHON3_START + 3) > rc.getRoundNum() - 2) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 2));
		} else if (rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START) == 0
				&& rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 1) > 0) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 2));
		} else if (rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START) == 0
				&& rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 1) > 0) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 2));
		} else if (rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START) == 0
				&& rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 1) > 0) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 2));
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON1_START) != 0
				&& rc.readBroadcast(Channels.ENEMY_ARCHON1_START + 3) > rc.readBroadcast(Channels.ENEMY_ARCHON2_START + 3)
				&& rc.readBroadcast(Channels.ENEMY_ARCHON1_START + 3) > rc.readBroadcast(Channels.ENEMY_ARCHON3_START + 3)) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 2));
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON2_START) != 0
				&& rc.readBroadcast(Channels.ENEMY_ARCHON2_START + 3) > rc.readBroadcast(Channels.ENEMY_ARCHON3_START + 3)) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 2));
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON3_START) != 0) {
			return new MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 1),
					rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 2));
		} // else if (rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 1) >
			// 0) {
			// return new
			// MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START +
			// 1),
			// rc.readBroadcastFloat(Channels.ENEMY_ARCHON1_START + 2));
			// } else if (rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START +
			// 1) >
			// 0) {
			// return new
			// MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START +
			// 1),
			// rc.readBroadcastFloat(Channels.ENEMY_ARCHON2_START + 2));
			// } else if (rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START +
			// 1) >
			// 0) {
			// return new
			// MapLocation(rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START +
			// 1),
			// rc.readBroadcastFloat(Channels.ENEMY_ARCHON3_START + 2));
			// }
		return RobotPlayer.mapCenter;
	}

	static void spotEnemyArchon(RobotInfo Archon) throws GameActionException {
		if (rc.readBroadcast(Channels.ENEMY_ARCHON1_START) == 0) {
			rc.broadcast(Channels.ENEMY_ARCHON1_START, Archon.ID);
			rc.broadcastFloat(Channels.ENEMY_ARCHON1_START + 1, Archon.location.x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON1_START + 2, Archon.location.y);
			rc.broadcast(Channels.ENEMY_ARCHON1_START + 3, rc.getRoundNum());
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON1_START) == Archon.ID) {
			rc.broadcastFloat(Channels.ENEMY_ARCHON1_START + 1, Archon.location.x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON1_START + 2, Archon.location.y);
			rc.broadcast(Channels.ENEMY_ARCHON1_START + 3, rc.getRoundNum());
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON2_START) == 0) {
			rc.broadcast(Channels.ENEMY_ARCHON2_START, Archon.ID);
			rc.broadcastFloat(Channels.ENEMY_ARCHON2_START + 1, Archon.location.x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON2_START + 2, Archon.location.y);
			rc.broadcast(Channels.ENEMY_ARCHON2_START + 3, rc.getRoundNum());
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON2_START) == Archon.ID) {
			rc.broadcastFloat(Channels.ENEMY_ARCHON2_START + 1, Archon.location.x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON2_START + 2, Archon.location.y);
			rc.broadcast(Channels.ENEMY_ARCHON2_START + 3, rc.getRoundNum());
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON3_START) == 0) {
			rc.broadcast(Channels.ENEMY_ARCHON3_START, Archon.ID);
			rc.broadcastFloat(Channels.ENEMY_ARCHON3_START + 1, Archon.location.x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON3_START + 2, Archon.location.y);
			rc.broadcast(Channels.ENEMY_ARCHON3_START + 3, rc.getRoundNum());
		} else if (rc.readBroadcast(Channels.ENEMY_ARCHON3_START) == Archon.ID) {
			rc.broadcastFloat(Channels.ENEMY_ARCHON3_START + 1, Archon.location.x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON3_START + 2, Archon.location.y);
			rc.broadcast(Channels.ENEMY_ARCHON3_START + 3, rc.getRoundNum());
		}
		return;
	}
}