package comms;

import battlecode.common.*;

import static comms.Constants.*;

public class Build {

	static RobotController rc = RobotPlayer.rc;

	/** 
	 * If possible, builds a Gardener
	 * 
	 * @throws GameActionException
	 */
	static void Gardener() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		 
        if ( rc.canHireGardener(dir) ) {
            // Hire Gardener
        	rc.hireGardener(dir);
        }
	}

	/** 
	 * If possible, builds a Lumberjack
	 * Records that an Lumberjack has been built
	 * Sets up comm channels for the Lumberjack
	 * 
	 * @throws GameActionException
	 */
	static void Lumberjack() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.LUMBERJACK, dir) ) {
        	// Build Lumberjack
        	rc.buildRobot(RobotType.LUMBERJACK, dir);
        }
	}

	/** 
	 * If possible, builds a Soldier
	 * Records that an Soldier has been built
	 * Sets up comm channels for the Soldier
	 * 
	 * @throws GameActionException
	 */
	static void Soldier() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
        	// Build Soldier
        	rc.buildRobot(RobotType.SOLDIER, dir);
        }
}	
	
	/** 
	 * If possible, builds a Tank
	 * Records that an Tank has been built
	 * Sets up comm channels for the Tank
	 * 
	 * @throws GameActionException
	 */
	static void Tank() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.TANK, dir) ) {
        	// Build Tank
        	rc.buildRobot(RobotType.TANK, dir);
        }
	}	
	
	/** 
	 * If possible, builds a Scout
	 * Records that an Scout has been built
	 * Sets up comm channels for the Scout
	 * 
	 * @throws GameActionException
	 */
	static void Scout() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.SCOUT, dir) ) {
        	// Build Scout
        	rc.buildRobot(RobotType.SCOUT, dir);
        }
	}	

}
