package comms;

import battlecode.common.*;

import comms.Archon;
import comms.Gardener;
import comms.Lumberjack;
import comms.Scout;
import comms.Soldier;
import comms.Tank;

public strictfp class RobotPlayer {
    static RobotController rc;
    
    // Robot specific variables
    static int age = 0;
    static int channel = 0;
    static int id = 0;
    
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        RobotPlayer.rc = rc; 

        // Control methods for each RobotType.
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
                
        }
	}
    
    
    /**
     * End turn immediately.
     * 
     * Increments age by 1 and calls Clock.yield().
     */
    static void endTurn() {
    	age++;
    	Clock.yield();
    }
    
    
    
}


