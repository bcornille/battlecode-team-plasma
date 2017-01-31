package teamplasma;

import java.awt.Robot;

import battlecode.common.*;

public class Shooting {
	
	static RobotController rc = RobotPlayer.rc;
	
	static void shoot(RobotInfo target) throws GameActionException {
		
		if (rc.canFirePentadShot() && willHitPentadShot(target) && canAffordPentadShot(target)) {
			rc.firePentadShot(rc.getLocation().directionTo(target.location));
		} else if (rc.canFireTriadShot() && willHitTriadShot(target) && canAffordTriadShot(target)) {
			rc.fireTriadShot(rc.getLocation().directionTo(target.location));
		} else if (rc.canFireSingleShot() && willHitSingleShot(target) && canAffordSingleShot(target)) {
			rc.fireSingleShot(rc.getLocation().directionTo(target.location));
		}
		
		if (target.type == RobotType.ARCHON && target.health < RobotType.ARCHON.maxHealth*(0.1f)) {
			System.out.println("Killed Enemy Archon");
			if (rc.readBroadcast(Channels.ENEMY_ARCHON1_START) == target.ID){
				rc.broadcast(Channels.ENEMY_ARCHON1_START, -1);
			} else if (rc.readBroadcast(Channels.ENEMY_ARCHON2_START) == target.ID){
				rc.broadcast(Channels.ENEMY_ARCHON2_START, -1);
			} else if (rc.readBroadcast(Channels.ENEMY_ARCHON3_START) == target.ID){
				rc.broadcast(Channels.ENEMY_ARCHON3_START, -1);
			}		
		}
		
	}

	static private boolean willHitPentadShot(RobotInfo target) throws GameActionException {
		Direction directionToTarget = rc.getLocation().directionTo(target.location);
		float distanceToTarget = rc.getLocation().distanceTo(target.location);
		boolean FF = checkFF(directionToTarget,distanceToTarget);
		boolean TF = checkTF(directionToTarget,distanceToTarget);
		
		float angle = 2.0f * GameConstants.PENTAD_SPREAD_DEGREES * (float) Math.PI / 180.0f;
		float hitDistance = (target.getRadius() + 2.0f) / (float) Math.sin(angle);
		
		System.out.println(distanceToTarget < hitDistance && FF && TF);

		return distanceToTarget < hitDistance && FF && TF;
	}

	static private boolean willHitTriadShot(RobotInfo target) throws GameActionException {
		Direction directionToTarget = rc.getLocation().directionTo(target.location);
		float distanceToTarget = rc.getLocation().distanceTo(target.location);
		boolean FF = checkFF(directionToTarget,distanceToTarget);
		boolean TF = checkTF(directionToTarget,distanceToTarget);
		
		float angle = GameConstants.TRIAD_SPREAD_DEGREES * (float) Math.PI / 180.0f;
		float hitDistance = (target.getRadius() + 2.0f) / (float) Math.sin(angle);
		
		System.out.println(distanceToTarget < hitDistance && FF && TF);
		
		return distanceToTarget < hitDistance && FF && TF;
	}
	
	static private boolean willHitSingleShot(RobotInfo target) throws GameActionException {
		Direction directionToTarget = rc.getLocation().directionTo(target.location);
		float distanceToTarget = rc.getLocation().distanceTo(target.location);
		boolean FF = checkFF(directionToTarget,distanceToTarget);
		boolean TF = checkTF(directionToTarget,distanceToTarget);
		
		System.out.println(FF && TF);
		
		return FF && TF;
	}
	
	static private boolean checkFF(Direction directionToTarget, float distanceToTarget) throws GameActionException {
		MapLocation point1 = rc.getLocation().add(directionToTarget, (3.0f/4.0f)*distanceToTarget);
		MapLocation point2 = rc.getLocation().add(directionToTarget, (1.0f/4.0f)*distanceToTarget);
		RobotInfo[] friends1 = rc.senseNearbyRobots(point1, (1.0f/4.0f)*distanceToTarget, rc.getTeam());
		RobotInfo[] friends2 = rc.senseNearbyRobots(point2, (1.0f/4.0f)*distanceToTarget, rc.getTeam());
		
		rc.setIndicatorDot(point1, 250, 0, 0);
		rc.setIndicatorDot(point2, 250, 0, 0);
		
		if(friends1.length==0 && friends2.length==0 ){
			System.out.println("No Friends In Way");
			return true;
		} else {
			System.out.println("Don't Shoot Our Friends!");
			return false;
		}
	}
	
	static private boolean checkTF(Direction directionToTarget, float distanceToTarget) throws GameActionException {
		MapLocation point1 = rc.getLocation().add(directionToTarget, (3.0f/4.0f)*distanceToTarget);
		MapLocation point2 = rc.getLocation().add(directionToTarget, (1.0f/4.0f)*distanceToTarget);
		TreeInfo[] trees1 = rc.senseNearbyTrees(point1, (1.0f/4.0f)*distanceToTarget, Team.NEUTRAL);
		TreeInfo[] trees2 = rc.senseNearbyTrees(point2, (1.0f/4.0f)*distanceToTarget, Team.NEUTRAL);
		
		rc.setIndicatorDot(point1, 250, 0, 0);
		rc.setIndicatorDot(point2, 250, 0, 0);
		
		if(trees1.length==0 && trees2.length==0 ){
			System.out.println("No Trees In Way");
			return true;
		} else {
			System.out.println("Don't Shoot The Trees!");
			return false;
		}
	}
	
	static private boolean canAffordSingleShot(RobotInfo target) {
		
		float singleShotLimit;
		
		switch(target.getType()){
		case ARCHON:
			singleShotLimit = Constants.ATTACK_BULLET_BANK/2;
			break;
		case GARDENER:
			singleShotLimit = GameConstants.SINGLE_SHOT_COST;
			break;
		case LUMBERJACK:
			singleShotLimit = Constants.ATTACK_BULLET_BANK;
			break;
		case SCOUT:
			singleShotLimit = GameConstants.SINGLE_SHOT_COST;
			break;
		case SOLDIER:
			singleShotLimit = GameConstants.SINGLE_SHOT_COST;
			break;
		case TANK:
			singleShotLimit = GameConstants.SINGLE_SHOT_COST;
			break;
		default:
			singleShotLimit = Constants.ATTACK_BULLET_BANK;
			break;
		}
		
		boolean canAfford = (rc.getTeamBullets() > singleShotLimit);
		System.out.println("Can Afford Single? " + canAfford);

		return canAfford;
		
	}
	
	static private boolean canAffordTriadShot(RobotInfo target) {
		
		float triadShotLimit;
		
		switch(target.getType()){
		case ARCHON:
			triadShotLimit  = Constants.ATTACK_BULLET_BANK;
			break;
		case GARDENER:
			triadShotLimit  = GameConstants.TRIAD_SHOT_COST;
			break;
		case LUMBERJACK:
			triadShotLimit  = Constants.ATTACK_BULLET_BANK;
			break;
		case SCOUT:
			triadShotLimit  = Constants.ATTACK_BULLET_BANK;
			break;
		case SOLDIER:
			triadShotLimit  = GameConstants.BULLET_TREE_COST + GameConstants.TRIAD_SHOT_COST;
			break;
		case TANK:
			triadShotLimit  = GameConstants.BULLET_TREE_COST + GameConstants.TRIAD_SHOT_COST;
			break;
		default:
			triadShotLimit  = Constants.ATTACK_BULLET_BANK;
			break;
		}

		boolean canAfford = (rc.getTeamBullets() > triadShotLimit);
		System.out.println("Can Afford Triad? " + canAfford);

		return canAfford;
		
	}
	
	static private boolean canAffordPentadShot(RobotInfo target) {
		
		float pentadShotLimit;
		
		switch(target.getType()){
		case ARCHON:
			pentadShotLimit = Constants.ATTACK_BULLET_BANK;
			break;
		case GARDENER:
			pentadShotLimit = GameConstants.PENTAD_SHOT_COST;
			break;
		case LUMBERJACK:
			pentadShotLimit = Constants.ATTACK_BULLET_BANK;
			break;
		case SCOUT:
			pentadShotLimit = Constants.ATTACK_BULLET_BANK;
			break;
		case SOLDIER:
			pentadShotLimit = GameConstants.BULLET_TREE_COST + GameConstants.PENTAD_SHOT_COST;
			break;
		case TANK:
			pentadShotLimit = GameConstants.BULLET_TREE_COST + GameConstants.PENTAD_SHOT_COST;
			break;
		default:
			pentadShotLimit = Constants.ATTACK_BULLET_BANK;
			break;
		}

		boolean canAfford = (rc.getTeamBullets() > pentadShotLimit);
		System.out.println("Can Afford Pentad? " + canAfford);

		return canAfford;
		
	}

}