package comms;

import battlecode.common.*;

public class Archon {
	
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an archon!");
        
        while (true) {

            try {
            	
            	Communications.robotCheckIn();

                Direction dir = Movement.randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(dir) && Math.random() < .1) {
                    rc.hireGardener(dir);
                }

                // Move randomly
                Movement.tryMove(Movement.randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
            
        }
    }
}
