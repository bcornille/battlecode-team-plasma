package teamplasma;

import battlecode.common.*;

public class Archon {

	/*-------------------------*
	 * ARCHON GLOBAL VARIABLES *
	 *-------------------------*/

	static RobotController rc = RobotPlayer.rc;

	static int archonNumber = 0;
	static int gardenerNumber = 0;

	static MapLocation myLocation;
	static MapLocation groveCenter;

	static Direction buildDirection;
	static Direction moveDirection;

	static boolean amLeader = false;
	static boolean onMap = false;
	static boolean canBuild = false;

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

	/**
	 * Main control method for RobotType Archon
	 * 
	 * @param rc
	 * @throws GameActionException
	 */
	static void run(RobotController rc) throws GameActionException {

		// First Archon has additional startup
		if (rc.readBroadcast(Channels.COUNT_ARCHON) == 0) {
			firstArchonSetup();
		}

		// Setup for all Archons
		initialize();

		// Code to run every turn
		while (true) {
			try {
				// Check in every turn
				RobotPlayer.checkIn();

				// Check if this Archon is the leader
				amLeader = electLeader();

				if (amLeader) {
					// Leader Archon checks for dead robots
					bringOutYourDead();
				}

				// Check grove mesh
				checkGroves();
				// Attempt to build a gardener
				tryHireGardener();

				// Check for threats
				RobotInfo[] robots = rc.senseNearbyRobots(RobotType.GARDENER.sensorRadius, RobotPlayer.enemyTeam);
				if (robots.length > 0) {
					System.out.println("omg enemies!");
					Communication.callForHelp(robots[0]);
				}

				// Stay in box
				myLocation = rc.getLocation();

				MapLocation futureLocation = myLocation.add(moveDirection, RobotType.ARCHON.strideRadius);

				if (futureLocation.x < rc.readBroadcastFloat(CHANNEL_GROVE_XMIN)
						|| futureLocation.x > rc.readBroadcastFloat(CHANNEL_GROVE_XMAX)
						|| futureLocation.y < rc.readBroadcastFloat(CHANNEL_GROVE_YMIN)
						|| futureLocation.y > rc.readBroadcastFloat(CHANNEL_GROVE_YMAX)) {
					moveDirection = moveDirection.opposite();
				}

				// Move
				moveDirection = Movement.tryMove(moveDirection, 90, 3);

				// End Turn
				RobotPlayer.shakeNearbyTree();
				RobotPlayer.endTurn();
			} catch (Exception e) {
				System.out.println("Archon Exception");
				e.printStackTrace();
			}
		}
	}

	static void firstArchonSetup() throws GameActionException {

		/*----------------*
		 * MAP BOUNDARIES *
		 *----------------*/

		myLocation = rc.getLocation();

		MapLocation[] enemyArchons = rc.getInitialArchonLocations(RobotPlayer.enemyTeam);
		MapLocation[] myArchons = rc.getInitialArchonLocations(RobotPlayer.myTeam);
		int numArchons = myArchons.length;

		rc.broadcastFloat(Channels.ENEMY_ARCHON1_START + 1, enemyArchons[0].x);
		rc.broadcastFloat(Channels.ENEMY_ARCHON1_START + 2, enemyArchons[0].y);
		if (enemyArchons.length > 1) {
			rc.broadcastFloat(Channels.ENEMY_ARCHON2_START + 1, enemyArchons[1].x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON2_START + 2, enemyArchons[1].y);
		}
		if (enemyArchons.length > 2) {
			rc.broadcastFloat(Channels.ENEMY_ARCHON3_START + 1, enemyArchons[2].x);
			rc.broadcastFloat(Channels.ENEMY_ARCHON3_START + 2, enemyArchons[2].y);
		}

		// Initialize map variable with Leader location
		float xmin = myLocation.x;
		float xmax = myLocation.x;
		float ymin = myLocation.y;
		float ymax = myLocation.y;

		// Locate all Archons to get rough outline of map and map center
		for (int i = 0; i < numArchons; i++) {

			xmin = Math.min(myArchons[i].x, xmin);
			xmax = Math.max(myArchons[i].x, xmax);
			ymin = Math.min(myArchons[i].y, ymin);
			ymax = Math.max(myArchons[i].y, ymax);

			xmin = Math.min(enemyArchons[i].x, xmin);
			xmax = Math.max(enemyArchons[i].x, xmax);
			ymin = Math.min(enemyArchons[i].y, ymin);
			ymax = Math.max(enemyArchons[i].y, ymax);

		}

		// Save the map information
		Communication.setMapEdge(xmin, xmax, ymin, ymax);

		/*------------------------*
		 * MAP STARTING POSITIONS *
		 *------------------------*/

		float toxmin = Math.abs(myLocation.x - xmin);
		float toxmax = Math.abs(myLocation.x - xmax);
		float toymin = Math.abs(myLocation.y - ymin);
		float toymax = Math.abs(myLocation.y - ymax);

		// find closest edge
		float nearest = Math.min(Math.min(toxmin, toxmax), Math.min(toymin, toymax));

		if (nearest == toxmin) {
			// Left Side
			rc.broadcast(Channels.BUILD_DIRECTION, 1);
			buildDirection = Direction.NORTH;
		} else if (nearest == toxmax) {
			// Right Side
			rc.broadcast(Channels.BUILD_DIRECTION, 2);
			buildDirection = Direction.SOUTH;
		} else if (nearest == toymin) {
			// Bottom Side
			rc.broadcast(Channels.BUILD_DIRECTION, 3);
			buildDirection = Direction.NORTH;
		} else if (nearest == toymax) {
			// Top Side
			rc.broadcast(Channels.BUILD_DIRECTION, 4);
			buildDirection = Direction.SOUTH;
		} else {
			// error
		}

	}

	static void initialize() throws GameActionException {

		// Archons count themselves
		Communication.countMe(Channels.COUNT_ARCHON);
		archonNumber = rc.readBroadcast(Channels.COUNT_ARCHON);

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

		// Get grove channels
		groveChannels();

		// Setup my home grove
		groveSetup();

		// get move direction
		moveDirection = buildDirection.rotateRightDegrees(90);

	}

	static void groveSetup() throws GameActionException {

		for (int i = 0; i < Constants.MAX_COUNT_GROVE; i++) {
			rc.broadcastBoolean(CHANNEL_GROVE_LOCATIONS + i, false);
			rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED + i, false);
		}

		myLocation = rc.getLocation();

		System.out.println(" ");
		System.out.println(archonNumber);

		// check if in another Archon's grove
		if (archonNumber == 2) {
			// 2rd Archon checks if in 1nd's grove

			float xmin = rc.readBroadcastFloat(Channels.GROVE1_XMIN);
			float xmax = rc.readBroadcastFloat(Channels.GROVE1_XMAX);
			float ymin = rc.readBroadcastFloat(Channels.GROVE1_YMIN);
			float ymax = rc.readBroadcastFloat(Channels.GROVE1_YMAX);

			System.out.println("(" + xmin + "," + ymin + ")");
			System.out.println("(" + myLocation.x + "," + myLocation.y + ")");
			System.out.println("(" + xmax + "," + ymax + ")");

			boolean xcheck = (xmin <= myLocation.x && xmax >= myLocation.x);
			boolean ycheck = (ymin <= myLocation.y && ymax >= myLocation.y);

			System.out.println(xcheck + ", " + ycheck);

			if (xcheck && ycheck) {
				// in grove 1, don't make our own grove
				Communication.setGroveEdge(archonNumber, xmin, xmax, ymin, ymax);
				archonNumber = 1;
				groveChannels();
				return;
			}

		} else if (archonNumber == 3) {
			// 3rd Archon checks if in 2nd's grove

			float xmin = rc.readBroadcastFloat(Channels.GROVE2_XMIN);
			float xmax = rc.readBroadcastFloat(Channels.GROVE2_XMAX);
			float ymin = rc.readBroadcastFloat(Channels.GROVE2_YMIN);
			float ymax = rc.readBroadcastFloat(Channels.GROVE2_YMAX);

			System.out.print("(" + xmin + "," + ymin + ")");
			System.out.print("(" + myLocation.x + "," + myLocation.y + ")");
			System.out.print("(" + xmax + "," + ymax + ")");

			boolean xcheck = (xmin <= myLocation.x && xmax >= myLocation.x);
			boolean ycheck = (ymin <= myLocation.y && ymax >= myLocation.y);

			System.out.println(xcheck + ", " + ycheck);

			if (xcheck && ycheck) {
				// 3rd Archon checks if in 1nd's grove as well

				xmin = rc.readBroadcastFloat(Channels.GROVE1_XMIN);
				xmax = rc.readBroadcastFloat(Channels.GROVE1_XMAX);
				ymin = rc.readBroadcastFloat(Channels.GROVE1_YMIN);
				ymax = rc.readBroadcastFloat(Channels.GROVE1_YMAX);

				System.out.println("(" + xmin + "," + ymin + ")");
				System.out.println("(" + myLocation.x + "," + myLocation.y + ")");
				System.out.println("(" + xmax + "," + ymax + ")");

				xcheck = (xmin <= myLocation.x && xmax >= myLocation.x);
				ycheck = (ymin <= myLocation.y && ymax >= myLocation.y);

				System.out.println(xcheck + ", " + ycheck);

				// Check if inside grove 1
				if (xcheck && ycheck) {
					// in grove 1, don't make our own grove
					Communication.setGroveEdge(archonNumber, xmin, xmax, ymin, ymax);
					archonNumber = 1;
					groveChannels();
					return;
				} else {
					// in grove 2, don't make our own grove
					Communication.setGroveEdge(archonNumber, xmin, xmax, ymin, ymax);
					archonNumber = 2;
					groveChannels();
					return;
				}
			}
		}

		// Not in another grove, so lets make our own
		groveCenter = myLocation.add(buildDirection, RobotType.ARCHON.bodyRadius + RobotType.GARDENER.bodyRadius);

		// setup first grove location
		rc.broadcast(CHANNEL_GROVE_COUNT, rc.readBroadcast(CHANNEL_GROVE_COUNT) + 1);
		rc.broadcastBoolean(CHANNEL_GROVE_LOCATIONS, true);
		rc.broadcastBoolean(CHANNEL_GROVE_ASSIGNED, false);
		rc.broadcastFloat(CHANNEL_GROVE_X, groveCenter.x);
		rc.broadcastFloat(CHANNEL_GROVE_Y, groveCenter.y);

		// Save the grove information
		Communication.setGroveEdge(archonNumber, groveCenter.x - RobotType.ARCHON.sensorRadius,
				groveCenter.x + RobotType.ARCHON.sensorRadius, groveCenter.y - RobotType.ARCHON.sensorRadius,
				groveCenter.y + RobotType.ARCHON.sensorRadius);

	}

	static void checkGroves() throws GameActionException {

		System.out.println(archonNumber);

		float xmin = rc.readBroadcastFloat(CHANNEL_GROVE_XMIN);
		float xmax = rc.readBroadcastFloat(CHANNEL_GROVE_XMAX);
		float ymin = rc.readBroadcastFloat(CHANNEL_GROVE_YMIN);
		float ymax = rc.readBroadcastFloat(CHANNEL_GROVE_YMAX);

		for (int i = 0; i < Constants.MAX_COUNT_GROVE; i++) {
			if (rc.readBroadcastBoolean(CHANNEL_GROVE_LOCATIONS + i)) {

				float groveX = rc.readBroadcastFloat(CHANNEL_GROVE_X + i);
				float groveY = rc.readBroadcastFloat(CHANNEL_GROVE_Y + i);

				MapLocation tempGrove = new MapLocation(groveX, groveY);
				rc.setIndicatorDot(tempGrove, 0, 0, 0);

				xmin = Math.min(xmin, groveX);
				xmax = Math.max(xmax, groveX);
				ymin = Math.min(ymin, groveY);
				ymax = Math.max(ymax, groveY);

				Communication.setGroveEdge(archonNumber, xmin, xmax, ymin, ymax);

				MapLocation grovePt1 = new MapLocation(xmin, ymin);
				MapLocation grovePt2 = new MapLocation(xmin, ymax);
				MapLocation grovePt3 = new MapLocation(xmax, ymin);
				MapLocation grovePt4 = new MapLocation(xmax, ymax);

				switch (archonNumber) {
				case 1:
					rc.setIndicatorLine(grovePt1, grovePt2, 200, 100, 100);
					rc.setIndicatorLine(grovePt1, grovePt3, 200, 100, 100);
					rc.setIndicatorLine(grovePt4, grovePt2, 200, 100, 100);
					rc.setIndicatorLine(grovePt4, grovePt3, 200, 100, 100);
					break;
				case 2:
					rc.setIndicatorLine(grovePt1, grovePt2, 100, 200, 100);
					rc.setIndicatorLine(grovePt1, grovePt3, 100, 200, 100);
					rc.setIndicatorLine(grovePt4, grovePt2, 100, 200, 100);
					rc.setIndicatorLine(grovePt4, grovePt3, 100, 200, 100);
					break;
				case 3:
					rc.setIndicatorLine(grovePt1, grovePt2, 100, 100, 200);
					rc.setIndicatorLine(grovePt1, grovePt3, 100, 100, 200);
					rc.setIndicatorLine(grovePt4, grovePt2, 100, 100, 200);
					rc.setIndicatorLine(grovePt4, grovePt3, 100, 100, 200);
					break;

				}
			} else {
				break;
			}
		}

	}

	/**
	 * 
	 * @return true if leader, false otherwise
	 * @throws GameActionException
	 */
	static boolean electLeader() throws GameActionException {
		int currentRound = rc.getRoundNum();
		for (int channel = Channels.MIN_ARCHON; channel <= RobotPlayer.myChannel; channel++) {
			int lastCheckIn = rc.readBroadcast(channel);
			if (currentRound - lastCheckIn > 0 && lastCheckIn != 0) {
				rc.broadcast(channel, 0);
				Communication.zeroComms(channel);
				int numArchons = rc.readBroadcast(Channels.COUNT_ARCHON);
				rc.broadcast(Channels.COUNT_ARCHON, --numArchons);
			} else {
				return channel == RobotPlayer.myChannel;
			}
		}
		return false;
	}

	/**
	 * Cleans up dead robots.
	 * 
	 * @throws GameActionException
	 */
	static void bringOutYourDead() throws GameActionException {

		int currentRound = rc.getRoundNum();

		for (int channel = Channels.MIN_ROBOT; channel <= Channels.MAX_ROBOT; channel++) {

			int lastCheckIn = rc.readBroadcast(channel);

			if (currentRound - lastCheckIn > 2 && lastCheckIn != 0) {

				rc.broadcast(channel, 0);

				// currently not needed. Changed how comms work
				// Communication.zeroComms(channel);

				// Remove Robot from main count
				int countChannel = Communication.getCountChannel(channel);
				int numRobotsOfType = rc.readBroadcast(countChannel);
				rc.broadcast(countChannel, --numRobotsOfType);

				// Remove Gardener from Archon count
				if (countChannel == Channels.COUNT_GARDENER) {
					int numMyGardeners = rc.readBroadcast(CHANNEL_GARDENER_COUNT);
					rc.broadcast(CHANNEL_GARDENER_COUNT, --numMyGardeners);
				}

			}
		}
	}

	/**
	 * Build Gardeners, if we can
	 * 
	 * @throws GameActionException
	 */
	static void tryHireGardener() throws GameActionException {

		canBuild = ((rc.readBroadcast(Channels.COUNT_SOLDIER) > 0) || canBuild);

		int numArchons = rc.readBroadcast(Channels.COUNT_ARCHON);
		int numGardeners = rc.readBroadcast(CHANNEL_GARDENER_COUNT);
		int totGardeners = rc.readBroadcast(Channels.COUNT_GARDENER);
		int maxGardeners = Constants.MAX_COUNT_GARDENER;
		int numGroves = rc.readBroadcast(CHANNEL_GROVE_COUNT);

		// Curve maxGardeners into the mid game
		maxGardeners = (int) ((Constants.MAX_COUNT_GARDENER - numArchons) * (rc.getRoundNum() / 2500.0f) + numArchons);
		maxGardeners = Math.min(maxGardeners, Constants.MAX_COUNT_GARDENER);

		System.out.println("Gardeners: " + numGardeners + "/" + maxGardeners);
		


		if (totGardeners == 0 || (numGardeners < maxGardeners && numGardeners < numGroves && canBuild)) {

			int maxChecks = 360;
			float radianOffset = Constants.TWO_PI / maxChecks;

			for (float check = 0.0f; check < Constants.TWO_PI; check += radianOffset) {
				if (rc.canBuildRobot(RobotType.GARDENER, buildDirection.rotateRightRads(check))) {

					// If all other gardeners had died, reset the grove
					if (totGardeners == 0 && rc.getRoundNum() > 10) {
						groveSetup();
					}
					
					// Build the Gardener
					rc.buildRobot(RobotType.GARDENER, buildDirection.rotateRightRads(check));
					// Count the Gardener
					rc.broadcast(Channels.COUNT_GARDENER, ++totGardeners);
					rc.broadcast(CHANNEL_GARDENER_COUNT, ++numGardeners);

					// Locate the Gardener
					float gDist = RobotType.ARCHON.bodyRadius + RobotType.GARDENER.bodyRadius
							+ GameConstants.GENERAL_SPAWN_OFFSET;
					MapLocation gLoc = myLocation.add(buildDirection.rotateRightRads(check), gDist);
					int gID = rc.senseRobotAtLocation(gLoc).getID();

					// save Gardener information
					for (int i = 0; i <= Constants.MAX_COUNT_GARDENER; i++) {
						if (rc.readBroadcast(Channels.GARDENER_ID + i) == 0) {
							rc.broadcast(Channels.GARDENER_ID + i, gID);
							rc.broadcast(Channels.GARDENER_NUMBER + i, totGardeners);
							rc.broadcast(Channels.GARDENER_PARENT + i, archonNumber);
							break;
						}
					}

				}
			}
		}
	}

	/**
	 * Gets the correct channels for all of the gardener/grove management.
	 * Definitely not the best way to do this stuff, but fuck it. We are almost
	 * out of time.
	 */
	static void groveChannels() {

		switch (archonNumber) {
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
