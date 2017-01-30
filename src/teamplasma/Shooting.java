package teamplasma;

import battlecode.common.*;

public class Shooting {
	
	static void shoot(RobotInfo target) throws GameActionException {
		if (RobotPlayer.rc.canFirePentadShot() && willHitPentadShot(target)) {
			RobotPlayer.rc.firePentadShot(RobotPlayer.rc.getLocation().directionTo(target.location));
		} else if (RobotPlayer.rc.canFireTriadShot() && willHitTriadShot(target)) {
			RobotPlayer.rc.fireTriadShot(RobotPlayer.rc.getLocation().directionTo(target.location));
		} else if (RobotPlayer.rc.canFireSingleShot()) {
			RobotPlayer.rc.fireSingleShot(RobotPlayer.rc.getLocation().directionTo(target.location));
		}
	}
	
	static private boolean willHitPentadShot(RobotInfo target) throws GameActionException {
		float distanceToTarget = RobotPlayer.rc.getLocation().distanceTo(target.location);
		float angle = 2.0f * GameConstants.PENTAD_SPREAD_DEGREES * (float)Math.PI / 180.0f;
		float hitDistance = (target.getRadius()) / (float)Math.sin(angle);
		return distanceToTarget < hitDistance;
	}
	
	static private boolean willHitTriadShot(RobotInfo target) throws GameActionException {
		float distanceToTarget = RobotPlayer.rc.getLocation().distanceTo(target.location);
		float angle = GameConstants.TRIAD_SPREAD_DEGREES * (float)Math.PI / 180.0f;
		float hitDistance = (target.getRadius()) / (float)Math.sin(angle);
		return distanceToTarget < hitDistance;
	}
}