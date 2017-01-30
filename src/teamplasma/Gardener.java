package teamplasma;

import java.util.Arrays;
import java.util.Comparator;

// import java.util.Arrays;
// import java.util.Comparator;

import battlecode.common.*;

public class Gardener {

	/*---------------------------*
	 * GARDENER GLOBAL VARIABLES *
	 *---------------------------*/

	static RobotController rc = RobotPlayer.rc;

	static Strategy myStrategy;

	static MapLocation myLocation;
	static MapLocation buildLocation;
	static MapLocation moveLocation;
	static MapLocation groveCenter;

	static Direction buildDirection;
	static Direction moveDirection;

	static boolean onMap = false;
	static boolean inGrove = false;
	static boolean foundGrove = false;
	static boolean assignedGrove = false;
	static boolean callHelp = false;

	static int groveChannel;
	static int myParent;
	static int myNumber;
	static int myID;

	static int CHANNEL_GROVE_LOCATIONS;
	static int CHANNEL_GROVE_ASSIGNED;
	static int CHANNEL_GROVE_X;
	static int CHANNEL_GROVE_Y;

	static float sqrt2 = (float) Math.sqrt(2);

	static int treeCount = 0;

	/**
	 * Main control method for RobotType Gardener
	 * 
	 * @param rc
	 * @throws GameActionException
	 */
	static void run(RobotController rc) throws GameActionException {

		// First Gardener has additional startup
		if (rc.readBroadcast(Channels.COUNT_GARDENER) == 1) {
			firstGardenerSetup();
		}

		// Setup for all Gardeners
		initialize();

		// Code to run every turn
		while (true) {
			try {
				// Check in every turn
				RobotPlayer.checkIn();
				// Update my location
				myLocation = rc.getLocation();
				// get strategy for turn
				setStrategy();

				switch (myStrategy) {
				case MOVING:
					moving();
					break;
				case DEFENDING:
					defending();
					break;
				case ATTACKING:
					attacking();
					break;
				default:
					break;
				}

				// Check grove status
				if (assignedGrove) {
					// Grove is assigned
					if (inGrove) {

						// Check for threats
						RobotInfo[] robots = rc.senseNearbyRobots(RobotType.GARDENER.sensorRadius,
								RobotPlayer.enemyTeam);
						if (robots.length > 0) {
							System.out.println("omg enemies!");
							callHelp = true;
						} else {
							callHelp = false;
						}

						// Do grove things
						maintainGrove();

					} else {
						// Not in grove, move to it
						moveToGrove();
					}

					// If low health, un-assign your grove
					// TODO: add constant
					float healthFraction = 0.3f;
					if (rc.getHealth() < RobotType.GARDENER.maxHealth * healthFraction) {
						rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + groveChannel, false);
					}

				} else {
					// We need a home!
					findGrove();
				}

				// End Turn
				RobotPlayer.shakeNearbyTree();
				RobotPlayer.endTurn();

			} catch (Exception e) {
				System.out.println("Gardener Exception");
				e.printStackTrace();
			}
		}

	}

	static void firstGardenerSetup() throws GameActionException {
		// placeholder for now. Will add some extra logic for first gardener
		// to avoid early game pitfalls

	}

	static void initialize() throws GameActionException {

		// Identify Gardener
		for (int i = 0; i <= Constants.MAX_COUNT_GARDENER; i++) {
			myID = rc.readBroadcast(Channels.GARDENER_ID + i);
			if (myID == rc.getID()) {
				myNumber = rc.readBroadcast(Channels.GARDENER_NUMBER + i);
				myParent = rc.readBroadcast(Channels.GARDENER_PARENT + i);
				break;
			}
		}

		// get Grove channels
		switch (myParent) {
		case 1:
			CHANNEL_GROVE_LOCATIONS = Channels.GROVE1_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE1_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE1_X;
			CHANNEL_GROVE_Y = Channels.GROVE1_Y;
			break;
		case 2:
			CHANNEL_GROVE_LOCATIONS = Channels.GROVE2_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE2_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE2_X;
			CHANNEL_GROVE_Y = Channels.GROVE2_Y;
			break;
		case 3:
			CHANNEL_GROVE_LOCATIONS = Channels.GROVE3_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE3_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE3_X;
			CHANNEL_GROVE_Y = Channels.GROVE3_Y;
			break;
		default:
			CHANNEL_GROVE_LOCATIONS = Channels.GROVE1_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE1_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE1_X;
			CHANNEL_GROVE_Y = Channels.GROVE1_Y;
			break;
		}

		// get starting value
		int start = rc.readBroadcast(Channels.BUILD_DIRECTION);

		// get build direction
		if (start == 1) {
			buildDirection = Direction.EAST;
		} else if (start == 2) {
			buildDirection = Direction.WEST;
		} else if (start == 3) {
			buildDirection = Direction.NORTH;
		} else if (start == 4) {
			buildDirection = Direction.SOUTH;
		} else {

		}

	}

	static void findGrove() throws GameActionException {

		for (int i = 0; i < Constants.MAX_COUNT_GROVE; i++) {

			foundGrove = rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i);
			assignedGrove = rc.readBroadcastBoolean(CHANNEL_GROVE_ASSIGNED + i);

			if (foundGrove && !assignedGrove) {

				assignedGrove = true;

				rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + i, assignedGrove);

				float groveX = rc.readBroadcastFloat(CHANNEL_GROVE_X + i);
				float groveY = rc.readBroadcastFloat(CHANNEL_GROVE_Y + i);

				groveCenter = new MapLocation(groveX, groveY);
				groveChannel = i;

				break;

			} else if (foundGrove && assignedGrove) {

			} else if (!foundGrove) {

				break;

			} else {

				// error

			}

		}

	}

	static void moveToGrove() throws GameActionException {

		if (myLocation.distanceTo(groveCenter) < rc.getType().strideRadius / 10) {
			// in grove, no more moving
			inGrove = true;
			// look for neighbor groves
			newGroves();

		} else if (myLocation.distanceTo(groveCenter) < rc.getType().strideRadius) {
			// grove is within one step
			moveDirection = rc.getLocation().directionTo(groveCenter);
			// check if move is on the map
			onMap = rc.onTheMap(myLocation.add(moveDirection, rc.getType().strideRadius), rc.getType().bodyRadius);
			if (onMap) {
				if (rc.canMove(myLocation.directionTo(groveCenter), myLocation.distanceTo(groveCenter))) {
					rc.move(myLocation.directionTo(groveCenter), myLocation.distanceTo(groveCenter));
				}
			} else {
				assignedGrove = false;
			}

			rc.setIndicatorLine(myLocation, groveCenter, 200, 200, 0);

		} else {
			// go to grove
			moveDirection = rc.getLocation().directionTo(groveCenter);
			// check if move is on the map
			boolean onMap = true;
			if (rc.getLocation().distanceTo(groveCenter) < rc.getType().sensorRadius - rc.getType().bodyRadius) {
				onMap = rc.onTheMap(groveCenter, rc.getType().bodyRadius);
			}
			if (onMap) {
				moveDirection = Movement.tryMove(moveDirection, 60, 3);
			} else {
				assignedGrove = false;
			}

			rc.setIndicatorLine(myLocation, groveCenter, 200, 0, 0);
		}

	}

	static void newGroves() throws GameActionException {

		for (int check = 0; check < 4; check++) {

			MapLocation newGroveCenter = myLocation.add(buildDirection.rotateRightDegrees(check * 90),
					Constants.GROVE_SPACING);

			for (int i = 0; i < Constants.MAX_COUNT_GROVE; i++) {

				if (rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i)) {

					float oldX = rc.readBroadcastFloat(CHANNEL_GROVE_X + i);
					float oldY = rc.readBroadcastFloat(CHANNEL_GROVE_Y + i);

					MapLocation oldGrove = new MapLocation(oldX, oldY);

					float diff = oldGrove.distanceTo(newGroveCenter);

					if (diff < 1) {
						break;
					}

				} else if (!rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i)) {

					if (!rc.onTheMap(myLocation.add(buildDirection.rotateRightDegrees(check * 90),
							RobotType.GARDENER.sensorRadius))) {
						break;
					} else {
						rc.broadcastBoolean(CHANNEL_GROVE_LOCATIONS + i, true);
						rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + i, false);
						rc.broadcastFloat(CHANNEL_GROVE_X + i, newGroveCenter.x);
						rc.broadcastFloat(CHANNEL_GROVE_Y + i, newGroveCenter.y);
						break;
					}
				}
			}
		}
	}

	static void maintainGrove() throws GameActionException {

		// Tree Locations
		MapLocation[] groveTrees = new MapLocation[5];

		float treeSep = RobotType.GARDENER.bodyRadius + GameConstants.BULLET_TREE_RADIUS
				+ GameConstants.GENERAL_SPAWN_OFFSET;

		groveTrees[0] = groveCenter.add(buildDirection.rotateLeftDegrees(90), treeSep);
		groveTrees[1] = groveCenter.add(buildDirection.rotateLeftDegrees(30), treeSep);
		groveTrees[2] = groveCenter.add(buildDirection.rotateRightDegrees(30), treeSep);
		groveTrees[3] = groveCenter.add(buildDirection.rotateRightDegrees(90), (treeSep) * sqrt2);

		// groveTrees[0] = groveCenter.add(buildDirection, treeSep);
		// groveTrees[1] = groveCenter.add(buildDirection.rotateLeftDegrees(90),
		// treeSep);
		// groveTrees[2] =
		// groveCenter.add(buildDirection.rotateRightDegrees(90), treeSep);
		// groveTrees[3] = groveCenter.add(buildDirection.rotateLeftDegrees(45),
		// (treeSep)*sqrt2);
		// groveTrees[4] =
		// groveCenter.add(buildDirection.rotateRightDegrees(45),
		// (treeSep)*sqrt2);

		// Show grove position
		rc.setIndicatorDot(groveCenter, 250, 0, 0);
		rc.setIndicatorDot(groveTrees[0], 0, 250, 0);
		rc.setIndicatorDot(groveTrees[1], 0, 250, 0);
		rc.setIndicatorDot(groveTrees[2], 0, 250, 0);
		rc.setIndicatorDot(groveTrees[3], 0, 250, 0);
		// rc.setIndicatorDot(groveTrees[4], 0, 250, 0);

		TreeInfo[] trees = rc.senseNearbyTrees(RobotType.GARDENER.bodyRadius + GameConstants.BULLET_TREE_RADIUS,
				rc.getTeam());
		Arrays.sort(trees, compareHP);

		treeCount = trees.length;
		if (treeCount < 5) {
			for (int i = 0; i < 4; i++) {
				MapLocation treeLocation = groveTrees[i];
				if (rc.canPlantTree(myLocation.directionTo(treeLocation))) {
					rc.plantTree(myLocation.directionTo(treeLocation));
					break;
				}
			}
		}

		for (TreeInfo tree : trees) {
			if (rc.canWater(tree.ID))
				rc.water(tree.ID);
		}

	}

	static Comparator<TreeInfo> compareHP = new Comparator<TreeInfo>() {
		public int compare(TreeInfo tree1, TreeInfo tree2) {
			return Float.compare(tree1.health, tree2.health);
		}
	};

	// -------------------------------------------------------------------------

	enum Strategy {
		MOVING, DEFENDING, ATTACKING
	}

	static void setStrategy() throws GameActionException {

		if (!inGrove) {
			myStrategy = Strategy.MOVING;
			System.out.println("Strategy: Moving");
		} else if (inGrove && callHelp) {
			myStrategy = Strategy.DEFENDING;
			System.out.println("Stategy: Defence");
		} else if (inGrove && treeCount == 4) {
			myStrategy = Strategy.ATTACKING;
			System.out.println("ATTACKING");
		}
	}

	static void moving() throws GameActionException {

		TreeInfo[] trees = rc.senseNearbyTrees(RobotType.GARDENER.sensorRadius, Team.NEUTRAL);

		if (trees.length > 0) {
			System.out.println("omg trees!");
			for (int check = 0; check < 360; check++) {
				if (rc.canBuildRobot(RobotType.LUMBERJACK, buildDirection.rotateLeftDegrees(check))) {
					rc.buildRobot(RobotType.LUMBERJACK, buildDirection.rotateLeftDegrees(check));
					Communication.countMe(RobotType.LUMBERJACK);
					return;
				}
			}
		} else if (rc.canBuildRobot(RobotType.SCOUT, buildDirection) && rc.readBroadcast(Channels.COUNT_SCOUT) < 1) {
			rc.buildRobot(RobotType.SCOUT, buildDirection);
			Communication.countMe(RobotType.SCOUT);
		} else if (rc.canBuildRobot(RobotType.SOLDIER, buildDirection)
				&& rc.readBroadcast(Channels.COUNT_SOLDIER) < Constants.MAX_COUNT_SOLDIER) {
			rc.buildRobot(RobotType.SOLDIER, buildDirection);
			Communication.countMe(RobotType.SOLDIER);
		}

	}

	static void defending() throws GameActionException {

		if (rc.canBuildRobot(RobotType.TANK, buildDirection)
				&& rc.readBroadcast(Channels.COUNT_TANK) < Constants.MAX_COUNT_TANK) {
			rc.buildRobot(RobotType.TANK, buildDirection);
			Communication.countMe(RobotType.TANK);
		} else if (rc.canBuildRobot(RobotType.SOLDIER, buildDirection)
				&& rc.readBroadcast(Channels.COUNT_SOLDIER) < Constants.MAX_COUNT_SOLDIER) {
			rc.buildRobot(RobotType.SOLDIER, buildDirection);
			Communication.countMe(RobotType.SOLDIER);
		}

	}

	static void attacking() throws GameActionException {
		// TODO: ADD THIS

	}

}