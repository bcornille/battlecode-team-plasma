package comms;

import battlecode.common.*;

import static comms.Constants.*;

public class Build {
	

	static RobotController rc = RobotPlayer.rc;
	
	static void Gardener() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		 
        if ( rc.canHireGardener(dir) ) {
            
        	rc.hireGardener(dir);
            
        	Communications.cntRobot(CHANNEL_COUNT_GARDENER);
        	        	
        	Communications.setupComms(dir, RobotType.GARDENER, CHANNEL_MIN_GARDENER, CHANNEL_MAX_GARDENER);
        	
        }
		
	}
	
	static void Lumberjack() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.LUMBERJACK, dir) ) {
        	
        	rc.buildRobot(RobotType.LUMBERJACK, dir);
        
        	Communications.cntRobot(CHANNEL_COUNT_LUMBERJACK);

        	Communications.setupComms(dir, RobotType.LUMBERJACK, CHANNEL_MIN_LUMBERJACK, CHANNEL_MAX_LUMBERJACK);

        	
        }
		
	}
		
	static void Soldier() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.SOLDIER, dir) ) {
        	
        	rc.buildRobot(RobotType.SOLDIER, dir);
        
        	Communications.cntRobot(CHANNEL_COUNT_SOLDIER);
        	
        	Communications.setupComms(dir, RobotType.SOLDIER, CHANNEL_MIN_SOLDIER, CHANNEL_MAX_SOLDIER);

		
        }
        
	}	
	
	static void Tank() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.TANK, dir) ) {
        	
        	rc.buildRobot(RobotType.TANK, dir);
        
        	Communications.cntRobot(CHANNEL_COUNT_TANK);
        	
        	Communications.setupComms(dir, RobotType.TANK, CHANNEL_MIN_TANK, CHANNEL_MAX_TANK);
		
        }
        
	}	
	
	static void Scout() throws GameActionException {
		
		Direction dir = Movement.randomDirection();
		
        if ( rc.canBuildRobot(RobotType.SCOUT, dir) ) {
        	
        	rc.buildRobot(RobotType.SCOUT, dir);
        
        	Communications.cntRobot(CHANNEL_COUNT_SCOUT);
		
        	Communications.setupComms(dir, RobotType.SCOUT, CHANNEL_MIN_SCOUT, CHANNEL_MAX_SCOUT);
        
        }
        
	}	
	


	

	
	
	
	
}
