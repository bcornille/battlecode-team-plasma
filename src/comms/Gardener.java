package comms;

import battlecode.common.*;

public class Gardener {
	static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a gardener!");
        Team myTeam = rc.getTeam();

        while (true) {

            try {

                // Generate a random direction
                Direction dir = Movement.randomDirection();

                int numScout = rc.readBroadcast(6);
                if (numScout < 5) {
                
                	Build.Scout();
            
                }

                // Move randomly
                Movement.tryMove(Movement.randomDirection());

                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
            
        }
    }
}