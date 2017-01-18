package teamplasma;

import java.util.Arrays;
import java.util.Comparator;

import battlecode.common.*;

public class Gardener {
	static RobotController rc;
	
	enum Strategy {
		EARLY_GAME, CLEAR_FOREST, PLANT_GROVE, BUILD_ARMY
	}
	
	static Strategy myStrategy;
	static Direction towardCenter;
	
	static Comparator<TreeInfo> compareHP = new Comparator<TreeInfo>() {
		public int compare(TreeInfo tree1, TreeInfo tree2) {
			return Float.compare(tree1.health, tree2.health);
		}
	};
	
	static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm a gardener!");
        Gardener.rc = rc;
//        Team myTeam = rc.getTeam();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                
            	RobotPlayer.checkIn();
            	
            	towardCenter = new Direction(rc.getLocation(), RobotPlayer.mapCenter);
            	
            	setStrategy();
            	
                // Sense trees for watering
                TreeInfo[] trees = rc.senseNearbyTrees(-1, RobotPlayer.myTeam);
                Arrays.sort(trees, compareHP);
                
                if (trees.length > 0) {
                	if (trees[0].health <= GameConstants.BULLET_TREE_MAX_HEALTH * 0.5) {
                		Direction towardTree = new Direction(rc.getLocation(), trees[0].location);
                    	RobotPlayer.myDirection = towardTree;
                	}
                }

                for(TreeInfo tree : trees) {
                	if (rc.canWater(tree.ID))
                		rc.water(tree.ID);
                }
                
                switch (myStrategy) {
                	case EARLY_GAME:
                		earlyGame();
                		break;
                	case CLEAR_FOREST:
                		clearForest();
                		break;
                	case PLANT_GROVE:
                		plantGrove(); // Note intentional fall-through
                	case BUILD_ARMY:
                		buildArmy();
                		break;
                }
                
                Movement.dodgeBullets();
//                // Try to dodge and if not continue moving.
//            	if (!Movement.dodgeBullets()) {
//            		if (!Movement.tryMove(RobotPlayer.myDirection)) {
//            			RobotPlayer.myDirection = RobotPlayer.myDirection.opposite();
//            			Movement.tryMove(RobotPlayer.myDirection);
//            		}
//            	}
//
//                // Generate a random direction
//                Direction dir = RobotPlayer.myDirection.opposite();
//
//                // Randomly attempt to build a soldier or lumberjack in this direction
//                if (rc.canPlantTree(dir) && trees.length <= Constants.MAX_COUNT_TREE) {
//                	rc.plantTree(dir);
//            	} else if (rc.canBuildRobot(RobotType.SCOUT, dir) && rc.readBroadcast(Constants.CHANNEL_COUNT_SCOUT) < Constants.MAX_COUNT_SCOUT) {
//                	rc.buildRobot(RobotType.SCOUT, dir);
//                	Communication.countMe(Constants.CHANNEL_COUNT_SCOUT);
//                } else if (rc.canBuildRobot(RobotType.TANK, dir) && rc.readBroadcast(Constants.CHANNEL_COUNT_TANK) < Constants.MAX_COUNT_TANK) {
//                    rc.buildRobot(RobotType.TANK, dir);
//                    Communication.countMe(Constants.CHANNEL_COUNT_TANK);
//                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && rc.readBroadcast(Constants.CHANNEL_COUNT_LUMBERJACK) < Constants.MAX_COUNT_LUMBERJACK) {
//                	rc.buildRobot(RobotType.LUMBERJACK, dir);
//                	Communication.countMe(Constants.CHANNEL_COUNT_LUMBERJACK);
//                } else if (rc.canBuildRobot(RobotType.SOLDIER, dir) && rc.readBroadcast(Constants.CHANNEL_COUNT_SOLDIER) < Constants.MAX_COUNT_SOLDIER) {
//                    rc.buildRobot(RobotType.SOLDIER, dir);
//                    Communication.countMe(Constants.CHANNEL_COUNT_SOLDIER);
//                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                // endTurn() implements Clock.yield() with extra information such as age
                RobotPlayer.endTurn();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
        
       
    }
	
	static void setStrategy() throws GameActionException {
		float groveRadius = RobotPlayer.myType.bodyRadius + GameConstants.GENERAL_SPAWN_OFFSET + 2.0f * GameConstants.BULLET_TREE_RADIUS;
		if (rc.getRoundNum() < Constants.EARLY_GAME_END) {
			myStrategy = Strategy.EARLY_GAME;
			System.out.println("Early game.");
		} else if (rc.senseNearbyTrees(groveRadius, Team.NEUTRAL).length > 0) {
			myStrategy = Strategy.CLEAR_FOREST;
			System.out.println("Clear forest.");
		} else if (rc.senseNearbyTrees(groveRadius, RobotPlayer.myTeam).length < Constants.MAX_COUNT_TREE) {
			myStrategy = Strategy.PLANT_GROVE;
			System.out.println("Plant grove.");
		} else {
			myStrategy = Strategy.BUILD_ARMY;
			System.out.println("Build army.");
		}
	}
	
	static void earlyGame() throws GameActionException {
		if (rc.canBuildRobot(RobotType.SCOUT, towardCenter) && rc.readBroadcast(Constants.CHANNEL_COUNT_SCOUT) < Constants.MAX_COUNT_SCOUT) {
        	rc.buildRobot(RobotType.SCOUT, towardCenter);
        	Communication.countMe(Constants.CHANNEL_COUNT_SCOUT);
        }
	}
	
	static void clearForest() throws GameActionException {
		if (rc.canBuildRobot(RobotType.LUMBERJACK, towardCenter) && rc.readBroadcast(Constants.CHANNEL_COUNT_LUMBERJACK) < Constants.MAX_COUNT_LUMBERJACK) {
        	rc.buildRobot(RobotType.LUMBERJACK, towardCenter);
        	Communication.countMe(Constants.CHANNEL_COUNT_LUMBERJACK);
        }
	}
	
	static void plantGrove() throws GameActionException {
		tryPlant(towardCenter.opposite());
	}
	
	static void buildArmy() throws GameActionException {
		if (rc.canBuildRobot(RobotType.LUMBERJACK, towardCenter) && rc.readBroadcast(Constants.CHANNEL_COUNT_LUMBERJACK) < Constants.MAX_COUNT_LUMBERJACK) {
        	rc.buildRobot(RobotType.LUMBERJACK, towardCenter);
        	Communication.countMe(Constants.CHANNEL_COUNT_LUMBERJACK);
        } else if (rc.canBuildRobot(RobotType.SOLDIER, towardCenter) && rc.readBroadcast(Constants.CHANNEL_COUNT_SOLDIER) < Constants.MAX_COUNT_SOLDIER) {
            rc.buildRobot(RobotType.SOLDIER, towardCenter);
            Communication.countMe(Constants.CHANNEL_COUNT_SOLDIER);
        }
	}
	
	static void tryPlant(Direction dir) throws GameActionException {
		if (rc.canPlantTree(dir)) {
			rc.plantTree(dir);
			return;
		}
		
		for (int check = 1; check < 3; check++) {
			if (rc.canPlantTree(dir.rotateLeftDegrees(60*check))) {
				rc.plantTree(dir.rotateLeftDegrees(60*check));
				return;
			}
			if (rc.canPlantTree(dir.rotateRightDegrees(60*check))) {
				rc.plantTree(dir.rotateRightDegrees(60*check));
				return;
			}
		}
	}
}