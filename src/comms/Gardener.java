package comms;

import battlecode.common.*;

import static comms.Constants.*;

public class Gardener {
	static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a gardener!");
        Team myTeam = rc.getTeam();
        
    	// Setup comms channel
    	Communications.setupMyComms(CHANNEL_MIN_GARDENER, CHANNEL_MAX_GARDENER);

        while (true) { 

            try {
            	
        		// Check in
            	rc.broadcast(RobotPlayer.channel, rc.getRoundNum());

                // Generate a random direction
                Direction dir = Movement.randomDirection();

                // If we need Scouts, hire them
                int numScout = rc.readBroadcast(CHANNEL_COUNT_SCOUT);
                
//                System.out.println(numScout + "/" + MAX_COUNT_SCOUT);
                
                
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