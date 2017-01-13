package teamplasma;

import battlecode.common.*;

public class Gardener {
	static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a gardener!");
        Team myTeam = rc.getTeam();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                
                // Sense trees for watering
                TreeInfo[] trees = rc.senseNearbyTrees(-1, myTeam);
                for(TreeInfo tree : trees) {
                	if (rc.canWater(tree.ID))
                		rc.water(tree.ID);
                }
                
                // Try to dodge and if not continue moving.
            	if (!Movement.dodgeBullets()) {
            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
            			Movement.tryMove(RobotPlayer.myDirection);
            		}
            	}

                // Generate a random direction
                Direction dir = RobotPlayer.myDirection.opposite();

                // Randomly attempt to build a soldier or lumberjack in this direction
                if (rc.canPlantTree(dir) && Math.random() < .01) {
                	rc.plantTree(dir);
            	} else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .1) {
                    rc.buildRobot(RobotType.TANK, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01) {
                	rc.buildRobot(RobotType.LUMBERJACK, dir);
                } else if (rc.canBuildRobot(RobotType.SCOUT, dir) && Math.random() < .01) {
                	rc.buildRobot(RobotType.SCOUT, dir);
                }

                // Move randomly
                Movement.tryMove(Movement.randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
}