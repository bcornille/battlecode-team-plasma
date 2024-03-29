package teamplasma;

import java.util.Arrays;

import battlecode.common.*;

public class Movement {

	static RobotController rc = RobotPlayer.rc;

	/**
	 * Returns a random Direction
	 * 
	 * @return a random Direction
	 */
	static Direction randomDirection() {
		return new Direction((float) Math.random() * 2 * (float) Math.PI);
	}

	/**
	 * Attempts to move in a given direction, while avoiding small obstacles
	 * direction in the path.
	 *
	 * @param dir
	 *            The intended direction of movement
	 * @param degreeOffset
	 *            Spacing between checked directions (degrees)
	 * @param checksPerSide
	 *            Number of extra directions checked on each side, if intended
	 *            direction was unavailable
	 * @return true if a move was performed
	 * @throws GameActionException
	 */
	static Direction tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

		// First, try intended direction
		boolean safe = rc.senseNearbyBullets(rc.getLocation().add(dir, RobotPlayer.myType.strideRadius),
				RobotPlayer.myType.bodyRadius).length == 0;
		if (rc.canMove(dir) && !rc.hasMoved() && safe) {
			rc.move(dir);
			return dir;
		}

		// Now try a bunch of similar angles
		int currentCheck = 1;

		while (currentCheck <= checksPerSide) {
			// Try the offset of the left side
			Direction testDir = dir.rotateLeftDegrees(degreeOffset * currentCheck);
			safe = rc.senseNearbyBullets(rc.getLocation().add(testDir, RobotPlayer.myType.strideRadius),
					RobotPlayer.myType.bodyRadius).length == 0;
			if (rc.canMove(testDir) && !rc.hasMoved() && safe) {
				rc.move(testDir);
				return testDir;
			}
			// Try the offset on the right side
			testDir = dir.rotateRightDegrees(degreeOffset * currentCheck);
			safe = rc.senseNearbyBullets(rc.getLocation().add(testDir, RobotPlayer.myType.strideRadius),
					RobotPlayer.myType.bodyRadius).length == 0;
			if (rc.canMove(testDir) && !rc.hasMoved() && safe) {
				rc.move(testDir);
				return testDir;
			}
			// No move performed, try slightly further
			currentCheck++;
		}

		// A move never happened, so return false.
		return dir;
	}

	/**
	 * Attempts to move in a given direction, while avoiding small obstacles
	 * directly in the path.
	 *
	 * @param dir
	 *            The intended direction of movement
	 * @return true if a move was performed
	 * @throws GameActionException
	 */
	static Direction tryMove(Direction dir) throws GameActionException {
		return tryMove(dir, 10, 10);
	}

	static Direction dodge(Direction myDirection) {
		// Get all bullets in sensor distance (might change this)
		BulletInfo[] bullets = rc.senseNearbyBullets(rc.getType().sensorRadius);
		if (bullets.length == 0) {
			// No bullets! Look at you mister safe.
			return myDirection;
		} else {
			MapLocation myLocation = rc.getLocation();
			MapLocation targetLocation = myLocation;
			// Scan nearby bullets to determine how to dodge them
			for (BulletInfo bullet : bullets) {
				// Get relevant bullet information
				MapLocation bulletLocation = bullet.location;
				MapLocation futureLocation = bulletLocation.add(bullet.dir, bullet.speed);
				float bulletDistance = bulletLocation.distanceTo(myLocation);
				float futureDistance = futureLocation.distanceTo(myLocation);
				// if distance increase, bullet is moving away, ignore bullet
				if (futureDistance > bulletDistance) {
					continue;
				}
				// Adjust our location to be out of bullet path
				// TODO: add weighting based on proximity and bullet damage
				Direction bulletDirection = myLocation.directionTo(bulletLocation);
				Direction futureDirection = myLocation.directionTo(futureLocation);
				float theta = bulletDirection.degreesBetween(futureDirection);

				if (theta < 0) {
					targetLocation = targetLocation.add(bulletLocation.directionTo(myLocation));
					targetLocation = targetLocation.add(futureLocation.directionTo(myLocation).rotateRightDegrees(90));
				} else {
					targetLocation = targetLocation.add(bulletLocation.directionTo(myLocation));
					targetLocation = targetLocation.add(futureLocation.directionTo(myLocation).rotateLeftDegrees(90));
				}
				rc.setIndicatorLine(bulletLocation, futureLocation, 250, 0, 0);
				rc.setIndicatorLine(myLocation, targetLocation, 0, 250, 0);
			} // end for
			if (myLocation == targetLocation) {
				return myDirection;
			} else {
				rc.setIndicatorLine(myLocation, targetLocation, 0, 150, 150);
				myDirection = myLocation.directionTo(targetLocation);
				return myDirection;
			} // end if
		} // end if
	}// end method

	/**
	 * checkFriendlySpacing(): Determines if there are nearby friendly units and
	 * attempts to move away from them if too close.
	 * 
	 * @param myDirection
	 * @return
	 */
	static Direction checkFriendlySpacing(Direction myDirection) {
		// Get all friendly robots in sensorRadius
		RobotInfo[] friends = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam());
		if (friends.length == 0) {
			// You have no friends! How sad. :(
			return myDirection;
		} else {
			// Scan all friendly robots, separation based on average distance
			// from other robots
			MapLocation myLocation = rc.getLocation();
			MapLocation targetLocation = myLocation;
			for (RobotInfo bot : friends) {
				MapLocation botLocation = bot.getLocation();
				float seperation = myLocation.distanceTo(botLocation);
				float spacing = getSpacing(rc.getType(), bot.type);
				if (seperation <= spacing) {
					targetLocation = targetLocation.add(botLocation.directionTo(myLocation));
					rc.setIndicatorLine(myLocation, botLocation, 0, 0, 150);
				}
			} // end for
			if (myLocation == targetLocation) {
				return myDirection;
			} else {
				rc.setIndicatorLine(myLocation, targetLocation, 0, 150, 150);
				myDirection = myLocation.directionTo(targetLocation);
				return myDirection;
			}
		} // end if
	}// end method

	/**
	 * A big heaping mess of switch statements to get the proper constant for
	 * unit spacing.
	 * 
	 * @param me
	 * @param you
	 * @return
	 */
	static float getSpacing(RobotType me, RobotType you) {

		switch (me) {
		case ARCHON:
			switch (you) {
			case ARCHON:
				return Constants.SPACING_ARCHON_ARCHON;
			case GARDENER:
				return Constants.SPACING_ARCHON_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_ARCHON_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_ARCHON_SCOUT;
			case SOLDIER:
				return Constants.SPACING_ARCHON_SOLDIER;
			case TANK:
				return Constants.SPACING_ARCHON_TANK;
			default:
				return 0.0f;
			}
		case GARDENER:
			switch (you) {
			case ARCHON:
				return Constants.SPACING_GARDENER_ARCHON;
			case GARDENER:
				return Constants.SPACING_GARDENER_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_GARDENER_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_GARDENER_SCOUT;
			case SOLDIER:
				return Constants.SPACING_GARDENER_SOLDIER;
			case TANK:
				return Constants.SPACING_GARDENER_TANK;
			default:
				return 0.0f;
			}
		case LUMBERJACK:
			switch (you) {
			case ARCHON:
				return Constants.SPACING_LUMBERJACK_ARCHON;
			case GARDENER:
				return Constants.SPACING_LUMBERJACK_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_LUMBERJACK_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_LUMBERJACK_SCOUT;
			case SOLDIER:
				return Constants.SPACING_LUMBERJACK_SOLDIER;
			case TANK:
				return Constants.SPACING_LUMBERJACK_TANK;
			default:
				return 0.0f;
			}
		case SCOUT:
			switch (you) {
			case ARCHON:
				return Constants.SPACING_SCOUT_ARCHON;
			case GARDENER:
				return Constants.SPACING_SCOUT_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_SCOUT_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_SCOUT_SCOUT;
			case SOLDIER:
				return Constants.SPACING_SCOUT_SOLDIER;
			case TANK:
				return Constants.SPACING_SCOUT_TANK;
			default:
				return 0.0f;
			}
		case SOLDIER:
			switch (you) {
			case ARCHON:
				return Constants.SPACING_SOLDIER_ARCHON;
			case GARDENER:
				return Constants.SPACING_SOLDIER_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_SOLDIER_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_SOLDIER_SCOUT;
			case SOLDIER:
				return Constants.SPACING_SOLDIER_SOLDIER;
			case TANK:
				return Constants.SPACING_SOLDIER_TANK;
			default:
				return 0.0f;
			}
		case TANK:
			switch (you) {
			case ARCHON:
				return Constants.SPACING_TANK_ARCHON;
			case GARDENER:
				return Constants.SPACING_TANK_GARDENER;
			case LUMBERJACK:
				return Constants.SPACING_TANK_LUMBERJACK;
			case SCOUT:
				return Constants.SPACING_TANK_SCOUT;
			case SOLDIER:
				return Constants.SPACING_TANK_SOLDIER;
			case TANK:
				return Constants.SPACING_TANK_TANK;
			default:
				return 0.0f;
			}
		default:
			return 0.0f;
		}
	}

	static Direction attackTarget(RobotInfo target) {
		switch (target.type) {
		case ARCHON:
			// Move toward Archons
			return rc.getLocation().directionTo(target.location);
		case GARDENER:
			// Move toward Gardeners
			return rc.getLocation().directionTo(target.location);
		case LUMBERJACK:
			return rc.getLocation().directionTo(target.location);
		case SCOUT:
			// Move toward Scouts
			return rc.getLocation().directionTo(target.location);
		case SOLDIER:
			return rc.getLocation().directionTo(target.location);
		case TANK:
			return rc.getLocation().directionTo(target.location);
		default:
			return RobotPlayer.myDirection;
		}
	}

	static Direction pathing(Direction myDirection, MapLocation goal) throws GameActionException {
		return new BaseNode(RobotPlayer.rc.getLocation(), goal).getNextStep(myDirection);
	}
}

abstract class Node implements Comparable<Node> {

	static MapLocation goal;

	MapLocation nodeLocation;

	int nodeDepth;

	Node(MapLocation nodeLocation, int nodeDepth) {
		this.nodeLocation = nodeLocation;
		this.nodeDepth = nodeDepth;
	}

	abstract boolean descend() throws GameActionException;

	void setGoal(MapLocation goal) {
		Node.goal = goal;
	}

	public int compareTo(Node other) {
		return Float.compare(this.nodeLocation.distanceTo(goal), other.nodeLocation.distanceTo(goal));
	}

	boolean isOpen() throws GameActionException {
		// RobotInfo[] robotsA = RobotPlayer.rc.senseNearbyRobots(nodeLocation,
		// RobotPlayer.myType.bodyRadius, Team.A);
		// RobotInfo[] robotsB = RobotPlayer.rc.senseNearbyRobots(nodeLocation,
		// RobotPlayer.myType.bodyRadius, Team.B);
		// TreeInfo[] treesA = RobotPlayer.rc.senseNearbyTrees(nodeLocation,
		// RobotPlayer.myType.bodyRadius, Team.A);
		// TreeInfo[] treesB = RobotPlayer.rc.senseNearbyTrees(nodeLocation,
		// RobotPlayer.myType.bodyRadius, Team.B);
		// TreeInfo[] treesN = RobotPlayer.rc.senseNearbyTrees(nodeLocation,
		// RobotPlayer.myType.bodyRadius, Team.NEUTRAL);
		// return robotsA.length == 0 && robotsB.length == 0 && treesA.length ==
		// 0
		// && treesB.length == 0 && treesN.length == 0;
		// return RobotPlayer.rc.isCircleOccupiedExceptByThisRobot(nodeLocation,
		// RobotPlayer.myType.bodyRadius);
		switch (RobotPlayer.myType) {
		case ARCHON:
			break;
		case GARDENER:
			return !RobotPlayer.rc.isCircleOccupiedExceptByThisRobot(nodeLocation, RobotPlayer.myType.bodyRadius);
		case LUMBERJACK:
			return RobotPlayer.rc.senseNearbyRobots(nodeLocation, RobotPlayer.myType.bodyRadius,
					RobotPlayer.myTeam).length == 0
					&& RobotPlayer.rc.senseNearbyTrees(nodeLocation, RobotPlayer.myType.bodyRadius,
							RobotPlayer.myTeam).length == 0;
		case SCOUT:
			break;
		case SOLDIER:
			return !RobotPlayer.rc.isCircleOccupiedExceptByThisRobot(nodeLocation, RobotPlayer.myType.bodyRadius);
		case TANK:
			return RobotPlayer.rc.senseNearbyTrees(nodeLocation, RobotPlayer.myType.bodyRadius,
					RobotPlayer.myTeam).length == 0;
		default:
			break;
		}
		return false;
	}

	RobotInfo[] concatenate(RobotInfo[] a, RobotInfo[] b) {
		int aLen = a.length;
		int bLen = b.length;

		RobotInfo[] c = new RobotInfo[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	TreeInfo[] concatenate(TreeInfo[] a, TreeInfo[] b) {
		int aLen = a.length;
		int bLen = b.length;

		TreeInfo[] c = new TreeInfo[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	enum FromSide {
		LEFT, RIGHT
	}
}

class BaseNode extends Node {
	BaseNode(MapLocation nodeLocation, MapLocation goal) {
		super(nodeLocation, 0);
		setGoal(goal);
	}

	boolean descend() {
		return true;
	}

	Direction getNextStep(Direction myDirection) throws GameActionException {
		Direction goalDirection = nodeLocation.directionTo(goal);
		int newDepth = nodeDepth + 1;
		Direction[] directions = { goalDirection, goalDirection.rotateRightDegrees(60),
				goalDirection.rotateLeftDegrees(60), goalDirection.rotateLeftDegrees(120),
				goalDirection.rotateRightDegrees(120), goalDirection.opposite() };
		Node[] branches = {
				new CornerNode(nodeLocation.add(directions[0], RobotPlayer.myType.strideRadius), newDepth,
						directions[0]),
				new CornerNode(nodeLocation.add(directions[1], RobotPlayer.myType.strideRadius), newDepth,
						directions[1]),
				new CornerNode(nodeLocation.add(directions[2], RobotPlayer.myType.strideRadius), newDepth,
						directions[2]),
				new CornerNode(nodeLocation.add(directions[3], RobotPlayer.myType.strideRadius), newDepth,
						directions[3]),
				new CornerNode(nodeLocation.add(directions[4], RobotPlayer.myType.strideRadius), newDepth,
						directions[4]),
				new CornerNode(nodeLocation.add(directions[5], RobotPlayer.myType.strideRadius), newDepth,
						directions[5]) };

		if (branches[0].descend()) {
			return directions[0];
		} else if (branches[1].descend()) {
			return directions[1];
		} else if (branches[2].descend()) {
			return directions[2];
		} else if (branches[3].descend()) {
			return directions[3];
		} else if (branches[4].descend()) {
			return directions[4];
		} else if (branches[5].descend()) {
			return directions[5];
		}

		return myDirection;
	}
}

class CornerNode extends Node {

	Direction fromDirection;

	CornerNode(MapLocation nodeLocation, int nodeDepth, Direction fromDirection) {
		super(nodeLocation, nodeDepth);
		this.fromDirection = fromDirection;
	}

	boolean descend() throws GameActionException {
		float stride = RobotPlayer.myType.strideRadius;
		float body = RobotPlayer.myType.bodyRadius;
		// int maxDepth = (int) (RobotPlayer.myType.sensorRadius / stride);
//		System.out.println("Depth: " + nodeDepth);
//		System.out.println("Bytecodes left: " + Clock.getBytecodesLeft());
//		System.out.println("Test value: " + Clock.getBytecodeNum() / Constants.PATH_PENALTY * nodeDepth);
		if (!RobotPlayer.rc.canSenseAllOfCircle(nodeLocation, RobotPlayer.myType.bodyRadius)
				|| Clock.getBytecodesLeft() < Clock.getBytecodeNum() / Constants.PATH_PENALTY * nodeDepth) {
			RobotPlayer.rc.setIndicatorLine(nodeLocation.add(fromDirection.opposite(), stride), nodeLocation, 0, 255,
					0);
			return true;
		} else if (!isOpen()// RobotPlayer.rc.isCircleOccupiedExceptByThisRobot(nodeLocation,
							// RobotPlayer.myType.bodyRadius)
				|| !RobotPlayer.rc.onTheMap(nodeLocation, body)) {
			RobotPlayer.rc.setIndicatorLine(nodeLocation.add(fromDirection.opposite(), stride), nodeLocation, 255, 0,
					0);
			return false;
		} else {
			int newDepth = nodeDepth + 1;
			Direction[] directions = { fromDirection, fromDirection.rotateLeftDegrees(60),
					fromDirection.rotateRightDegrees(60) };
			Node[] branches = { new CornerNode(nodeLocation.add(directions[0], stride), newDepth, directions[0]),
					new EdgeNode(nodeLocation.add(directions[1], stride), newDepth, directions[1], FromSide.LEFT),
					new EdgeNode(nodeLocation.add(directions[1], stride), newDepth, directions[1], FromSide.RIGHT) };

			Arrays.sort(branches);
			for (Node branch : branches) {
				if (branch.descend()) {
					RobotPlayer.rc.setIndicatorLine(nodeLocation.add(fromDirection.opposite(), stride), nodeLocation, 0,
							255, 0);
					return true;
				}
			}

			return false;
		}
	}
}

class EdgeNode extends Node {

	Direction fromDirection;

	FromSide fromSide;

	EdgeNode(MapLocation nodeLocation, int nodeDepth, Direction fromDirection, FromSide fromSide) {
		super(nodeLocation, nodeDepth);
		this.fromDirection = fromDirection;
		this.fromSide = fromSide;
	}

	boolean descend() throws GameActionException {
		float stride = RobotPlayer.myType.strideRadius;
		float body = RobotPlayer.myType.bodyRadius;
		// int maxDepth = (int) ((RobotPlayer.myType.sensorRadius -
		// RobotPlayer.myType.bodyRadius) / stride);
//		System.out.println("Depth: " + nodeDepth);
//		System.out.println("Bytecodes left: " + Clock.getBytecodesLeft());
//		System.out.println("Test value: " + Clock.getBytecodeNum() / Constants.PATH_PENALTY * nodeDepth);
		if (!RobotPlayer.rc.canSenseAllOfCircle(nodeLocation, RobotPlayer.myType.bodyRadius)
				|| Clock.getBytecodesLeft() < Clock.getBytecodeNum() / Constants.PATH_PENALTY * nodeDepth) {
			RobotPlayer.rc.setIndicatorLine(nodeLocation.add(fromDirection.opposite(), stride), nodeLocation, 0, 255,
					0);
			return true;
		} else if (!isOpen()// RobotPlayer.rc.isCircleOccupiedExceptByThisRobot(nodeLocation,
							// body)
				|| !RobotPlayer.rc.onTheMap(nodeLocation, body)) {
			RobotPlayer.rc.setIndicatorLine(nodeLocation.add(fromDirection.opposite(), stride), nodeLocation, 255, 0,
					0);
			return false;
		} else {
			int newDepth = nodeDepth + 1;
			Direction[] directions = new Direction[2];
			directions[0] = fromDirection;
			Node[] branches = new Node[2];
			if (fromSide == FromSide.RIGHT) {
				directions[1] = fromDirection.rotateLeftDegrees(60);
				branches[0] = new EdgeNode(nodeLocation.add(directions[0], stride), newDepth, directions[0],
						FromSide.RIGHT);
				branches[1] = new EdgeNode(nodeLocation.add(directions[1], stride), newDepth, directions[1],
						FromSide.LEFT);
			} else {
				directions[1] = fromDirection.rotateRightDegrees(60);
				branches[0] = new EdgeNode(nodeLocation.add(directions[0], stride), newDepth, directions[0],
						FromSide.LEFT);
				branches[1] = new EdgeNode(nodeLocation.add(directions[1], stride), newDepth, directions[1],
						FromSide.RIGHT);
			}

			Arrays.sort(branches);
			for (Node branch : branches) {
				if (branch.descend()) {
					RobotPlayer.rc.setIndicatorLine(nodeLocation.add(fromDirection.opposite(), stride), nodeLocation, 0,
							255, 0);
					return true;
				}
			}

			return false;
		}
	}
}
