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
	static Direction robotDirection;
	static Direction moveDirection;

	static boolean onMap = false;
	static boolean inGrove = false;
	static boolean foundGrove = false;
	static boolean assignedGrove = false;
	static boolean callHelp = false;
	static boolean amFirst = false;
	static boolean canBuild = false;

	static int groveChannel;
	static int myParent;
	static int myNumber;
	static int myID;

	static int CHANNEL_GARDENER_COUNT;
	static int CHANNEL_GROVE_COUNT;

	static int CHANNEL_GROVE_LOCATIONS;
	static int CHANNEL_GROVE_ASSIGNED;
	static int CHANNEL_GROVE_X;
	static int CHANNEL_GROVE_Y;

	static int CHANNEL_GROVE_XMIN;
	static int CHANNEL_GROVE_XMAX;
	static int CHANNEL_GROVE_YMIN;
	static int CHANNEL_GROVE_YMAX;

	static int maxTreeCount = Constants.MAX_COUNT_TREE;
	static int lumberjackCount = 0;
	static int treeCount = 0;

	static boolean[] planted = new boolean[maxTreeCount];

	/**
	 * Main control method for RobotType Gardener
	 * 
	 * @param rc
	 * @throws GameActionException
	 */
	static void run(RobotController rc) throws GameActionException {

		// Setup for all Gardeners
		initialize();

		// Identify first Gardener
		if (myNumber == 1) {
			amFirst = true;
		}

		System.out.println(Clock.getBytecodesLeft());

		// Code to run every turn
		while (true) {
			try {
				// Check in every turn
				RobotPlayer.checkIn();
				// Update my location
				myLocation = rc.getLocation();
				// get strategy for turn
				setStrategy();

				// Build units based on strategy
				switch (myStrategy) {
				case FIRST:
					first();
					break;
				case MOVING:
					moving();
					break;
				case DEFENDING:
					defending();
					break;
				case ATTACKING:
					attacking();
					break;
				case ECONOMY:
					economy();
				default:
					break;
				}

				// Check for obstructing trees
				TreeInfo[] trees = rc.senseNearbyTrees(
						RobotType.GARDENER.bodyRadius + 3.0f * GameConstants.BULLET_TREE_RADIUS, Team.NEUTRAL);
				if (trees.length > 0) {
					Communication.callForChop(trees[0]);
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
							Communication.callForHelp(robots[0]);
							callHelp = true;
						} else {
							callHelp = false;
						}

						if (!amFirst) {
							// Do grove things
							maintainGrove();
						}

					} else {
						// Not in grove, move to it
						moveToGrove();
					}

					// If low health, un-assign your grove
					float healthFraction = 0.3f;
					if (rc.getHealth() < RobotType.GARDENER.maxHealth * healthFraction) {
						rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + groveChannel, false);
						if (inGrove) {
							int numGrove = rc.readBroadcast(CHANNEL_GROVE_COUNT);
							rc.broadcast(CHANNEL_GROVE_COUNT, --numGrove);
							rc.disintegrate();
						}
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

	static void initialize() throws GameActionException {

		for (int i = 0; i < planted.length; i++) {
			planted[i] = false;
		}

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
		groveChannels();

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

		robotDirection = buildDirection.opposite();

	}

	static void findGrove() throws GameActionException {

		myLocation = rc.getLocation();

		boolean tempFound = false;

		float groveX = 0;
		float groveY = 0;

		MapLocation tempGrove = new MapLocation(groveX, groveY);
		MapLocation prevGrove = tempGrove;

		for (int i = 0; i < Constants.MAX_COUNT_GROVE; i++) {

			foundGrove = rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i);
			assignedGrove = rc.readBroadcastBoolean(CHANNEL_GROVE_ASSIGNED + i);

			if (foundGrove && !assignedGrove) {

				tempFound = true;

				groveX = rc.readBroadcastFloat(CHANNEL_GROVE_X + i);
				groveY = rc.readBroadcastFloat(CHANNEL_GROVE_Y + i);

				tempGrove = new MapLocation(groveX, groveY);

				if (myLocation.distanceTo(tempGrove) < myLocation.distanceTo(prevGrove)) {

					prevGrove = tempGrove;

					groveCenter = tempGrove;
					groveChannel = i;

				}

			} else if (!foundGrove) {
				break;
			}
		}

		if (tempFound) {
			assignedGrove = true;
			rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + groveChannel, assignedGrove);
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
			onMap = rc.onTheMap(myLocation.add(moveDirection, rc.getType().strideRadius * 2.0f));
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
			// check if move is on the map or obstructed

			onMap = true;
			boolean open = true;
			TreeInfo[] trees = null;

			if (rc.getLocation().distanceTo(groveCenter) < rc.getType().sensorRadius - rc.getType().bodyRadius) {
				onMap = rc.onTheMap(groveCenter, rc.getType().bodyRadius);
				if (onMap) {
					trees = rc.senseNearbyTrees(groveCenter, rc.getType().bodyRadius, Team.NEUTRAL);
					open = trees.length == 0;
				}
			}
			if (onMap && open) {
				moveDirection = Movement.pathing(moveDirection, groveCenter);
				moveDirection = Movement.tryMove(moveDirection, 60, 3);
			} else if (!open) {
				rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + groveChannel, false);
				if (trees != null)
					Communication.callForChop(trees[0]);
				assignedGrove = false;
			} else if (!onMap) {
				int numGroves = rc.readBroadcast(CHANNEL_GROVE_COUNT);
				rc.broadcast(CHANNEL_GROVE_COUNT, --numGroves);
				assignedGrove = false;
			}

			rc.setIndicatorLine(myLocation, groveCenter, 200, 0, 0);
		}

	}

	static void newGroves() throws GameActionException {

		int numGroves = rc.readBroadcast(CHANNEL_GROVE_COUNT);
		float groveSpacing = Constants.GROVE_SPACING_V;

		for (int check = 0; check < 4; check++) {

			if (check % 2 == 0) {
				groveSpacing = Constants.GROVE_SPACING_V;
			} else {
				groveSpacing = Constants.GROVE_SPACING_H;
			}

			MapLocation newGroveCenter = myLocation.add(buildDirection.rotateRightDegrees(check * 90), groveSpacing);

			for (int i = 0; i < Constants.MAX_COUNT_GROVE; i++) {

				if (rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i)) {

					float oldX = rc.readBroadcastFloat(CHANNEL_GROVE_X + i);
					float oldY = rc.readBroadcastFloat(CHANNEL_GROVE_Y + i);

					MapLocation oldGrove = new MapLocation(oldX, oldY);

					float diff = oldGrove.distanceTo(newGroveCenter);

					if (diff < 0.9f * groveSpacing) {
						break;
					}

				} else if (!rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i)) {

					if (!rc.onTheMap(myLocation.add(buildDirection.rotateRightDegrees(check * 90),
							RobotType.GARDENER.sensorRadius))) {
						break;
					} else {
						rc.broadcast(CHANNEL_GROVE_COUNT, ++numGroves);
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
		MapLocation[] groveTrees = new MapLocation[4];

		float treeSep = RobotType.GARDENER.bodyRadius + GameConstants.BULLET_TREE_RADIUS
				+ GameConstants.GENERAL_SPAWN_OFFSET;

		groveTrees[0] = groveCenter.add(buildDirection.rotateLeftDegrees(90), treeSep);
		groveTrees[1] = groveCenter.add(buildDirection.rotateLeftDegrees(30), treeSep);
		groveTrees[2] = groveCenter.add(buildDirection.rotateRightDegrees(30), treeSep);
		groveTrees[3] = groveCenter.add(buildDirection.rotateRightDegrees(90), treeSep);

		TreeInfo[] trees = rc.senseNearbyTrees(RobotType.GARDENER.bodyRadius + 1.0f * GameConstants.BULLET_TREE_RADIUS,
				rc.getTeam());
		Arrays.sort(trees, compareHP);
		treeCount = trees.length;

		for (TreeInfo tree : trees) {
			if (rc.canWater(tree.ID))
				rc.water(tree.ID);
		}

		canBuild = ((rc.readBroadcast(Channels.COUNT_SOLDIER) > 0) && (rc.readBroadcast(Channels.COUNT_SCOUT) > 0)
				|| canBuild);

		if (treeCount <= Constants.MAX_COUNT_TREE && canBuild) {
			maxTreeCount = Constants.MAX_COUNT_TREE;
			for (int i = 0; i < Constants.MAX_COUNT_TREE; i++) {
				MapLocation treeLocation = groveTrees[i];
				Direction treeDirection = myLocation.directionTo(treeLocation);
				if (rc.canPlantTree(treeDirection)) {
					rc.plantTree(myLocation.directionTo(treeLocation));
					planted[i] = true;
				} else if (!rc.onTheMap(treeLocation, GameConstants.BULLET_TREE_RADIUS)) {
					maxTreeCount--;
				} else {
					continue;
				}
			}
		}

		// Show grove position
		rc.setIndicatorDot(groveCenter, 250, 0, 0);
		for (int i = 0; i < planted.length; i++) {
			if (planted[i]) {
				rc.setIndicatorDot(groveTrees[i], 0, 250, 0);
			} else {
				rc.setIndicatorDot(groveTrees[i], 0, 0, 250);
			}
		}

	}

	static Comparator<TreeInfo> compareHP = new Comparator<TreeInfo>() {
		public int compare(TreeInfo tree1, TreeInfo tree2) {
			return Float.compare(tree1.health, tree2.health);
		}
	};

	// -------------------------------------------------------------------------

	enum Strategy {
		FIRST, MOVING, DEFENDING, ATTACKING, ECONOMY
	}

	static void checkLumberjack() throws GameActionException {

		TreeInfo[] trees = rc.senseNearbyTrees(RobotType.GARDENER.bodyRadius + 3.0f * GameConstants.BULLET_TREE_RADIUS,
				Team.NEUTRAL);

		int maxLumberjacks = Constants.MAX_COUNT_LUMBERJACK;
		int numLumberjacks = rc.readBroadcast(Channels.COUNT_LUMBERJACK);
		int numGardeners = rc.readBroadcast(Channels.COUNT_GARDENER);
		int numTrees = trees.length;

		boolean check1 = numTrees > 0;
		boolean check2 = lumberjackCount < 2;
		boolean check3 = numLumberjacks < maxLumberjacks;
		boolean check4 = numLumberjacks < 2 * numGardeners;

		if (check1 && check2 && check3 && check4) {
			for (int check = 0; check < 360; check++) {
				if (rc.canBuildRobot(RobotType.LUMBERJACK, robotDirection.rotateLeftDegrees(check))) {
					rc.buildRobot(RobotType.LUMBERJACK, robotDirection.rotateLeftDegrees(check));
					Communication.countMe(RobotType.LUMBERJACK);
					lumberjackCount++;
					return;
				}
			}
		}

	}

	static void setStrategy() throws GameActionException {

		if (amFirst) {
			myStrategy = Strategy.FIRST;
			System.out.println("FIRST!");
		} else if (!inGrove) {
			myStrategy = Strategy.MOVING;
			System.out.println("Strategy: Moving");
		} else if (inGrove && callHelp) {
			myStrategy = Strategy.DEFENDING;
			System.out.println("Stategy: Defence");
		} else if (inGrove && treeCount == maxTreeCount && rc.getTeamBullets() > Constants.ATTACK_BULLET_BANK) {
			myStrategy = Strategy.ATTACKING;
			System.out.println("Strategy: Attacking");
		} else {
			myStrategy = Strategy.ECONOMY;
			System.out.println("Strategy: Economy");
		}

	}

	static void first() throws GameActionException {

		TreeInfo[] trees = rc.senseNearbyTrees(RobotType.GARDENER.sensorRadius, Team.NEUTRAL);

		System.out.println(Clock.getBytecodesLeft());

		int numTree = trees.length;
		int numLumberjack = rc.readBroadcast(Channels.COUNT_LUMBERJACK);
		int numSoldier = rc.readBroadcast(Channels.COUNT_SOLDIER);
		int numScout = rc.readBroadcast(Channels.COUNT_SCOUT);

		boolean condition1 = ((numLumberjack < 1) && (rc.getTeamBullets() > RobotType.LUMBERJACK.bulletCost));
		boolean condition2 = ((numSoldier < 2) && (rc.getTeamBullets() > RobotType.SOLDIER.bulletCost));
		boolean condition3 = ((numScout < 1) && (rc.getTeamBullets() > RobotType.SCOUT.bulletCost));

		if (numTree > 0 && condition1) {
			for (int check = 0; check < 360; check++) {
				if (rc.canBuildRobot(RobotType.LUMBERJACK, buildDirection.rotateLeftDegrees(check)) && condition1) {
					rc.buildRobot(RobotType.LUMBERJACK, buildDirection.rotateLeftDegrees(check));
					Communication.countMe(RobotType.LUMBERJACK);
					lumberjackCount++;
					return;
				}
			}
		} else if (condition2) {
			for (int check = 0; check < 8; check++) {
				if (rc.canBuildRobot(RobotType.SOLDIER, robotDirection.rotateLeftDegrees(check * 45)) && condition2) {
					rc.buildRobot(RobotType.SOLDIER, robotDirection.rotateLeftDegrees(check * 45));
					Communication.countMe(RobotType.SOLDIER);
					return;
				}
			}
		} else if (condition3) {
			for (int check = 0; check < 8; check++) {
				if (rc.canBuildRobot(RobotType.SCOUT, robotDirection.rotateLeftDegrees(check * 45)) && condition3) {
					rc.buildRobot(RobotType.SCOUT, robotDirection.rotateLeftDegrees(check * 45));
					Communication.countMe(RobotType.SCOUT);
					return;
				}
			}
		} else if (numScout > 0 && numSoldier > 1) {
			amFirst = false;
			return;
		} else {
			// move on
		}

	}

	/**
	 * Unit making strategy when in moving phase
	 */
	static void moving() throws GameActionException {

		checkLumberjack();

		rc.setIndicatorLine(myLocation, myLocation.add(robotDirection), 0, 0, 0);

		if (rc.canBuildRobot(RobotType.SCOUT, robotDirection)
				&& rc.readBroadcast(Channels.COUNT_SCOUT) < Constants.MAX_COUNT_SCOUT) {
			rc.buildRobot(RobotType.SCOUT, robotDirection);
			Communication.countMe(RobotType.SCOUT);
			return;
		}
		// } else if (rc.canBuildRobot(RobotType.SOLDIER, buildDirection) &&
		// rc.readBroadcast(Channels.COUNT_SOLDIER) <
		// Constants.MAX_COUNT_SOLDIER) {
		// rc.buildRobot(RobotType.SOLDIER, buildDirection);
		// Communication.countMe(RobotType.SOLDIER);
		// return;
		// }

	}

	/**
	 * Unit making strategy when in defending phase
	 */
	static void defending() throws GameActionException {

		rc.setIndicatorLine(myLocation, myLocation.add(robotDirection), 0, 0, 0);

		if (rc.canBuildRobot(RobotType.SOLDIER, robotDirection)
				&& rc.readBroadcast(Channels.COUNT_SOLDIER) < Constants.MAX_COUNT_SOLDIER) {
			rc.buildRobot(RobotType.SOLDIER, robotDirection);
			Communication.countMe(RobotType.SOLDIER);
		}

	}

	/**
	 * Unit making strategy when in attacking phase
	 */
	static void attacking() throws GameActionException {

		rc.setIndicatorLine(myLocation, myLocation.add(robotDirection), 0, 0, 0);

		checkLumberjack();

		int maxSoldier = Constants.MAX_COUNT_SOLDIER;
		int maxTank = Constants.MAX_COUNT_TANK;

		int numSoldier = rc.readBroadcast(Channels.COUNT_SOLDIER);
		int numTank = rc.readBroadcast(Channels.COUNT_TANK);

		float maxRatio = (float) (maxSoldier) / (maxTank + 1);
		float numRatio = (float) (numSoldier) / (numTank + 1);

		boolean canBuildTank = rc.canBuildRobot(RobotType.TANK, robotDirection);
		boolean canBuildSoldier = rc.canBuildRobot(RobotType.SOLDIER, robotDirection);

		System.out.println(numRatio + ">" + maxRatio);

		if (canBuildSoldier && numSoldier < maxSoldier && numRatio < maxRatio) {
			rc.buildRobot(RobotType.SOLDIER, robotDirection);
			Communication.countMe(RobotType.SOLDIER);
		} else if (canBuildTank && numTank < maxTank) {
			rc.buildRobot(RobotType.TANK, robotDirection);
			Communication.countMe(RobotType.TANK);
		}

	}

	/**
	 * Unit making strategy when in economy phase
	 */
	static void economy() throws GameActionException {

		checkLumberjack();

		if (rc.canBuildRobot(RobotType.SCOUT, robotDirection)
				&& rc.readBroadcast(Channels.COUNT_SCOUT) < Constants.MAX_COUNT_SCOUT) {
			rc.buildRobot(RobotType.SCOUT, robotDirection);
			Communication.countMe(RobotType.SCOUT);
			return;
		}

	}

	/**
	 * Gets the correct channels for all of the gardener/grove management.
	 * Definitely not the best way to do this stuff, but fuck it. We are almost
	 * out of time.
	 */
	static void groveChannels() {

		switch (myParent) {
		case 1:

			CHANNEL_GARDENER_COUNT = Channels.GARDENER1_COUNT;
			CHANNEL_GROVE_COUNT = Channels.GROVE1_COUNT;

			CHANNEL_GROVE_LOCATIONS = Channels.GROVE1_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE1_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE1_X;
			CHANNEL_GROVE_Y = Channels.GROVE1_Y;

			CHANNEL_GROVE_XMIN = Channels.GROVE1_XMIN;
			CHANNEL_GROVE_XMAX = Channels.GROVE1_XMAX;
			CHANNEL_GROVE_YMIN = Channels.GROVE1_YMIN;
			CHANNEL_GROVE_YMAX = Channels.GROVE1_YMAX;

			break;
		case 2:

			CHANNEL_GARDENER_COUNT = Channels.GARDENER2_COUNT;
			CHANNEL_GROVE_COUNT = Channels.GROVE2_COUNT;

			CHANNEL_GROVE_LOCATIONS = Channels.GROVE2_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE2_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE2_X;
			CHANNEL_GROVE_Y = Channels.GROVE2_Y;

			CHANNEL_GROVE_XMIN = Channels.GROVE2_XMIN;
			CHANNEL_GROVE_XMAX = Channels.GROVE2_XMAX;
			CHANNEL_GROVE_YMIN = Channels.GROVE2_YMIN;
			CHANNEL_GROVE_YMAX = Channels.GROVE2_YMAX;

			break;
		case 3:

			CHANNEL_GARDENER_COUNT = Channels.GARDENER3_COUNT;
			CHANNEL_GROVE_COUNT = Channels.GROVE3_COUNT;

			CHANNEL_GROVE_LOCATIONS = Channels.GROVE3_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE3_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE3_X;
			CHANNEL_GROVE_Y = Channels.GROVE3_Y;

			CHANNEL_GROVE_XMIN = Channels.GROVE3_XMIN;
			CHANNEL_GROVE_XMAX = Channels.GROVE3_XMAX;
			CHANNEL_GROVE_YMIN = Channels.GROVE3_YMIN;
			CHANNEL_GROVE_YMAX = Channels.GROVE3_YMAX;

			break;
		default:

			CHANNEL_GARDENER_COUNT = Channels.GARDENER1_COUNT;
			CHANNEL_GROVE_COUNT = Channels.GROVE1_COUNT;

			CHANNEL_GROVE_LOCATIONS = Channels.GROVE1_LOCATIONS;
			CHANNEL_GROVE_ASSIGNED = Channels.GROVE1_ASSIGNED;
			CHANNEL_GROVE_X = Channels.GROVE1_X;
			CHANNEL_GROVE_Y = Channels.GROVE1_Y;

			CHANNEL_GROVE_XMIN = Channels.GROVE1_XMIN;
			CHANNEL_GROVE_XMAX = Channels.GROVE1_XMAX;
			CHANNEL_GROVE_YMIN = Channels.GROVE1_YMIN;
			CHANNEL_GROVE_YMAX = Channels.GROVE1_YMAX;

			break;
		}
	}

}