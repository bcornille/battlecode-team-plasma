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
        Gardener.rc = rc;
        // Code to run every turn
        while (true) {
            try {
            	// Check in every turn
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

            	// Check scout spacing, update direction if necessary:
            	RobotPlayer.myDirection = Movement.checkFriendlySpacing(RobotPlayer.myDirection);
            	// Adjust movement direction to dodge bullets
            	RobotPlayer.myDirection = Movement.dodge(RobotPlayer.myDirection);
            	// Move
            	RobotPlayer.myDirection = Movement.tryMove(RobotPlayer.myDirection);
            	
                // End Turn
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
		if (rc.canBuildRobot(RobotType.SOLDIER, towardCenter) && rc.readBroadcast(Constants.CHANNEL_COUNT_SOLDIER) < 1) {
			rc.buildRobot(RobotType.SOLDIER, towardCenter);
			Communication.countMe(RobotType.SOLDIER);
		} else if (rc.canBuildRobot(RobotType.SCOUT, towardCenter) && rc.readBroadcast(Constants.CHANNEL_COUNT_SCOUT) < Constants.MAX_COUNT_SCOUT) {
        	rc.buildRobot(RobotType.SCOUT, towardCenter);
        	Communication.countMe(RobotType.SCOUT);
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