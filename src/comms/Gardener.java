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

                // Randomly attempt to build a scout in this direction
                if (rc.canBuildRobot(RobotType.SCOUT, dir) && Math.random() < .1) {
                    rc.buildRobot(RobotType.SCOUT, dir);
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