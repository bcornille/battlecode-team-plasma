package nothing;

import battlecode.common.*;

import static comms.Constants.*;

public class Archon {
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
        
        while (true) {

            try {
            	if (rc.getRoundNum() > 100)
            		rc.resign();
            	Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
            
        }
    }
}
