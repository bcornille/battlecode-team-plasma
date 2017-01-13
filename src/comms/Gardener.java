package comms;

import battlecode.common.*;

import static comms.Constants.*;

public class Gardener {
	static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a gardener!");
        Team myTeam = rc.getTeam();

        while (true) {

            try {
            	// On robots first round, count Gardener and setup their comms
            	if (RobotPlayer.age==0) {
            		// Count Gardener
            		Initialize.cntRobot(CHANNEL_COUNT_GARDENER);
                	// Setup comms channel
                	Initialize.setupMyComms(CHANNEL_MIN_GARDENER, CHANNEL_MAX_GARDENER);
            	}
            	
        		// Check in
            	rc.broadcast(RobotPlayer.channel, rc.getRoundNum());

                // Generate a random direction
                Direction dir = Movement.randomDirection();

                // If we need Scouts, hire them
                int numScout = rc.readBroadcast(6);
                if (numScout < MAX_COUNT_SCOUT) {
                   	Build.Scout();
                }

                // Move randomly
                Movement.tryMove(Movement.randomDirection());

                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
            
        }
    }
}