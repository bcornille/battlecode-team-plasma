package nothing;

import battlecode.common.*;

import nothing.Archon;

public strictfp class RobotPlayer {
    static RobotController rc;
    
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
                
        }
	}
    
    
    
}


