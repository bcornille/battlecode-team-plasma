package teamplasma;

import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    
    // Common variables for all RobotType
    static int age = 0;
    static int myChannel = 0;
    static boolean canCommunicate = false;
    static int myID = 0;
    static RobotType myType;
    static int mySpawnNumber = 0;
    static Direction myDirection;
    static Team myTeam;
    static Team enemyTeam;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        
        // Setup common to all RobotType
        boot();

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
            default:
            	System.out.println("An unkown RobotType has appeared!");
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
    
    /**
     * Robot boot-up sequence...
     */
   static void boot() throws GameActionException {
	   myID = rc.getID();
	   System.out.println(myID);
	   myType = rc.getType();
	   System.out.println(myType.toString());
	   
	   int numSpawned = rc.readBroadcast(Constants.CHANNEL_COUNT_SPAWNED);
	   mySpawnNumber = ++numSpawned;
	   System.out.println(mySpawnNumber);
	   rc.broadcast(Constants.CHANNEL_COUNT_SPAWNED, numSpawned);
	   
	   myChannel = Communication.getOpenChannel(myType);
	   System.out.println("My channel" + myChannel);
	   if (myChannel != -1)
		   canCommunicate = true;
	   
	   myDirection = Movement.randomDirection();
	   System.out.println("My direction" + myDirection.toString());
   }
   
   static void checkIn() throws GameActionException {
	   rc.broadcast(myChannel, rc.getRoundNum());
   }
}
