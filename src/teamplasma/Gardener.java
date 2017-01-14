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
		//MapLocation archonLoc = new MapLocation(xPos,yPos); // doesn't seem to be working?
                
                // Sense trees for watering
                TreeInfo[] trees = rc.senseNearbyTrees(-1);
		float lumberjackWeight = 1;
                for(TreeInfo tree : trees) {
                	if (tree.team == myTeam && rc.canWater(tree.ID))
                		rc.water(tree.ID);
			if (tree.team == Team.NEUTRAL)
			    lumberjackWeight += 0.5; //more likely to build lumberjacks w/ neutral trees nearby
                }

		// 		//if too close to an archon, move away a bit
		// if(!Movement.dodgeBullets()){
		//     for(MapLocation archonLoc : archonLocs) {
		// 	if( Constants.GARDENER_RAD_SQR > myLoc.distanceSquaredTo(archonLoc) ) {
		// 	    Direction awayDir =  new Direction( archonLoc, myLoc );
		// 	    RobotPlayer.myDirection = awayDir;
		// 	    Movement.tryMove(RobotPlayer.myDirection);
		// 	}
		//     }
		// }

		MapLocation[] archonLocs = rc.getInitialArchonLocations(myTeam);
		MapLocation myLoc = rc.getLocation();
		
                // Try to dodge and if not continue moving.
            	if (!Movement.dodgeBullets()) {
		    boolean headed_home = false;
		    for(MapLocation archonLoc : archonLocs) {
			if( Constants.GARDENER_RAD_SQR < myLoc.distanceSquaredTo(archonLoc) ) {
			    Direction awayDir =  new Direction( myLoc, archonLoc );
			    RobotPlayer.myDirection = awayDir;
			    Movement.tryMove(RobotPlayer.myDirection);
			    headed_home = true;
			}
		    }

		    if (!headed_home && !Movement.tryMove(RobotPlayer.myDirection)) {
			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
			Movement.tryMove(RobotPlayer.myDirection);
			Movement.tryMove(Movement.randomDirection());
		    }
            	}

                // Generate a random direction
                Direction dir = RobotPlayer.myDirection.opposite();
		
                // Randomly attempt to build a soldier or lumberjack in this direction
                if (rc.canPlantTree(dir) && Math.random() < .01) {
                	rc.plantTree(dir);
            	} else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < Constants.SOLDIER_BUILD_PROB) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < Constants.TANK_BUILD_PROB) {
                    rc.buildRobot(RobotType.TANK, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < lumberjackWeight*Constants.LUMBERJACK_BUILD_PROB) {
                	rc.buildRobot(RobotType.LUMBERJACK, dir);
                } else if (rc.canBuildRobot(RobotType.SCOUT, dir) && Math.random() < Constants.SCOUT_BUILD_PROB) {
                	rc.buildRobot(RobotType.SCOUT, dir);
                }


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
