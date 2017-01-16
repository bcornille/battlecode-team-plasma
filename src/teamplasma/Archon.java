package teamplasma;

import battlecode.common.*;

public class Archon {
	static RobotController rc;
	static boolean amLeader = false;
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
        
        Archon.rc = rc;

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
            	
            	RobotPlayer.checkIn();
            	
            	amLeader = electLeader();
            	
            	if (amLeader) {
            		bringOutYourDead();
            	}
            	
            	// Try to dodge and if not continue moving.
            	if (!Movement.dodgeBullets()) {
            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
            			Movement.tryMove(RobotPlayer.myDirection);
            		}
            	}

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(RobotPlayer.myDirection.opposite()) && rc.readBroadcast(Constants.CHANNEL_COUNT_GARDENER) < Constants.MAX_COUNT_GARDENER) {
                    rc.hireGardener(RobotPlayer.myDirection.opposite());
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
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