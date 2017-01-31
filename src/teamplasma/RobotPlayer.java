package teamplasma;

import battlecode.common.*;

public strictfp class RobotPlayer {

	static RobotController rc;

	// Common variables for all RobotType
	static boolean canCommunicate = false;
	static Direction myDirection;
	static MapLocation mapCenter;
	static RobotType myType;
	static int mySpawnNumber = 0;
	static int myChannel = 0;
	static int myAge = 0;
	static int myID = 0;
	static Team myTeam;
	static Team enemyTeam;

	/**
	 * run() is the method that is called when a robot is instantiated in the
	 * Battlecode world. If this method returns, the robot dies!
	 **/
	@SuppressWarnings("unused")
	public static void run(RobotController rc) throws GameActionException {

		// This is the RobotController object. You use it to perform actions
		// from this robot,
		// and to get information on its current status.
		RobotPlayer.rc = rc;

		// Setup common to all RobotType
		boot();

		// Here, we've separated the controls into a different method for each
		// RobotType.
		// You can add the missing ones or rewrite this into your own control
		// structure.
		switch (rc.getType()) {
		case ARCHON:
			Archon.run(rc);
			break;
		case GARDENER:
			Gardener.run(rc);
			break;
		case SOLDIER:
			Soldier.run(rc);
			break;
		case TANK:
			Tank.run(rc);
			break;
		case LUMBERJACK:
			Lumberjack.run(rc);
			break;
		case SCOUT:
			Scout.run(rc);
			break;
		default:
			System.out.println("An unknown RobotType has appeared!");
		}
	}

	/**
	 * End turn immediately.
	 * 
	 * Increments age by 1 and calls Clock.yield().
	 */
	static void endTurn() {
		myAge++;
		Clock.yield();
	}

	static void shakeNearbyTree() throws GameActionException {
		TreeInfo[] closeTrees = rc.senseNearbyTrees(myType.bodyRadius + GameConstants.INTERACTION_DIST_FROM_EDGE,
				Team.NEUTRAL);
		for (TreeInfo tree : closeTrees) {
			if (tree.containedBullets > 0 && rc.canShake(tree.ID)) {
				rc.shake(tree.ID);
				return;
			}
		}
		return;
	}

	/**
	 * RobotPlayer.boot(): Boot up sequence for robots. Sets up all local robot
	 * constants.
	 * 
	 * @throws GameActionException
	 */
	static void boot() throws GameActionException {
		myID = rc.getID();
		myType = rc.getType();

		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();

		int numSpawned = rc.readBroadcast(Channels.COUNT_SPAWNED);
		mySpawnNumber = ++numSpawned;
		rc.broadcast(Channels.COUNT_SPAWNED, numSpawned);

		myChannel = Communication.getOpenChannel(myType);
		if (myChannel != -1)
			canCommunicate = true;

		mapCenter = Communication.readMapCenter();

		myDirection = new Direction(rc.getLocation(), mapCenter).rotateLeftDegrees(90);

	}

	/**
	 * RobotPlayer.checkIn(): A given robot checks in by broadcasting the round
	 * number to their channel. Also handles VictoryPoint purchasing.
	 * 
	 * @throws GameActionException
	 */
	static void checkIn() throws GameActionException {

		// Broadcast to channel
		if (canCommunicate)
			rc.broadcast(myChannel, rc.getRoundNum());

		// Check for Victory Point win
		float ourBullets = rc.getTeamBullets();
		float canBuyVPs = ourBullets / rc.getVictoryPointCost();
		float canWinVPs = GameConstants.VICTORY_POINTS_TO_WIN - rc.getTeamVictoryPoints();

		if (canBuyVPs >= canWinVPs) {
			// Have enough victory points to win
			donate(ourBullets);
		} else if (rc.getRoundLimit() - rc.getRoundNum() < 2) {
			// Game ending, by all the points
			donate(ourBullets);
		} else if (ourBullets >= Constants.MAX_BULLET_BANK) {
			// Surplus money, time to invest in victory
			donate(ourBullets - Constants.MAX_BULLET_BANK);
		}
	}

	/**
	 * RobotPlayer.donate(): Loads RobotController.donate() and modifies so only
	 * who Victory Points are able to be purchased.
	 * 
	 * @param float
	 *            bullets
	 * @throws GameActionException
	 */
	static void donate(float bullets) throws GameActionException {
		// Get exchange rate
		float pointXRate = rc.getVictoryPointCost();
		// Round donation amount to whole Victory Point
		float amount = (float) (Math.floor(bullets / pointXRate) * pointXRate);
		// Donate bullets
		rc.donate(amount);
	}
}
