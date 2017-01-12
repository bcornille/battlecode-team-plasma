package teamplasma;

import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    static int age = 0;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
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
