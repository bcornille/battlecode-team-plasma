package nothing;

import battlecode.common.*;

public class Archon {
	
    static void run(RobotController rc) throws GameActionException {
        
        while (true) {

            try {

            	Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
            
        }
    }
}
