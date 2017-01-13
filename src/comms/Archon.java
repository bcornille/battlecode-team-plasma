package comms;

import battlecode.common.*;

import static comms.Constants.*;

public class Archon {
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
        
        while (true) {

            try {
            	// On robots first round, count Archons and setup their comms
            	if (RobotPlayer.age==0) {
            		// Count Archon
            		Initialize.cntRobot(CHANNEL_COUNT_ARCHON);
                	// Setup comms channel
                	Initialize.setupMyComms(CHANNEL_MIN_ARCHON, CHANNEL_MAX_ARCHON);
            	}
            	
        		// Check in
            	rc.broadcast(RobotPlayer.channel, rc.getRoundNum());
            	
            	// Check for dead, clean up comms
            	for (int i=CHANNEL_MIN; i<=CHANNEL_MAX; i++) {
            		
            		int currentRound = rc.getRoundNum();            		
            		int checkRound = rc.readBroadcast(i);
            		int diffRound = currentRound - checkRound;
            		
            		if (diffRound > 5 && diffRound!=currentRound) {
            			
            			int channel = Terminate.getChannel(i);
            			
            			Terminate.cntRobot(channel);
            			
            			Terminate.clearComms(i);            
            			
            		}
            	}           	
            	
            	// If we need Gardeners, build them
                int numGardener = rc.readBroadcast(2);
                if (numGardener < MAX_COUNT_GARDENER) {                
                	Build.Gardener();
                }

                // Move randomly
                Movement.tryMove(Movement.randomDirection());

                // end turn
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
            
        }
    }
}
