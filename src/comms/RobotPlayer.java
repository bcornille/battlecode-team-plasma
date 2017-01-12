package comms;
import battlecode.common.*;

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
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
            	break;
            case TANK:
            	break;
            case SCOUT:
            	runScout();
            	break;
            case LUMBERJACK:
                break;
                
        }
	}

    static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");

        while (true) {

            try {
            	
            	robotCheckIn();

                Direction dir = randomDirection();

                // Randomly attempt to build a gardener in this direction
                if (rc.canHireGardener(dir) && Math.random() < .01) {
                    rc.hireGardener(dir);
                }

                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

	static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        Team myTeam = rc.getTeam();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                
                // Sense trees for watering
                TreeInfo[] trees = rc.senseNearbyTrees(-1, myTeam);
                for(TreeInfo tree : trees) {
                	if (rc.canWater(tree.ID))
                		rc.water(tree.ID);
                }

                // Generate a random direction
                Direction dir = randomDirection();

                // Randomly attempt to build a soldier or lumberjack in this direction
                if (rc.canBuildRobot(RobotType.SCOUT, dir) && Math.random() < .1) {
                    rc.buildRobot(RobotType.SCOUT, dir);
                }

                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static void runScout() throws GameActionException {
        System.out.println("I'm an scout!");
        Team enemy = rc.getTeam().opponent();

        while (true) {

            try {
            	
                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                if (robots.length == 0) {
                	
                	tryMove(randomDirection());
                	
                } else if (robots.length == 1) {
                	
                	RobotInfo robot = robots[0];
                
                	float distance = checkDistance(robot);
                	
                	if ( distance > 7 ) {
                		
                		tryMove(dirLeftRobot(robot));
                		
                	} else {
                		
                		tryMove(dirFromRobot(robot));
                		
                	}
                	                	
                	
                } else {
                	
                	int index = (robots.length - 1);
                	
                	RobotInfo robot = robots[index];
                	
                	float distance = checkDistance(robot);
                	
                	if ( distance > 7 ) {
                		
                		tryMove(dirLeftRobot(robot));
                		
                	} else {
                		
                		tryMove(dirFromRobot(robot));
                		
                	}
                	
                }
           

                
                
                // End turn
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
  
    /**
     * Get the distance between robot and self
     * @return float distance - distance between robot and self
     */    
    static float checkDistance(RobotInfo robot) {
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	float distance = myLocation.distanceTo(robotLocation);
    	
    	return distance;
    }
    
    
    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
    
    static Direction dirLeftRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionToRobot = myLocation.directionTo(robotLocation);
        
    	Direction directitonLeftRobot = directionToRobot.rotateLeftRads( (float)Math.PI / 2 );	
    			
    	return directitonLeftRobot;
    	
    }
    
    static Direction dirRightRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionToRobot = myLocation.directionTo(robotLocation);
        
    	Direction directitonRightRobot = directionToRobot.rotateLeftRads( - (float)Math.PI / 2 );	
    			
    	return directitonRightRobot;
    	
    }

    static Direction dirToRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionToRobot = myLocation.directionTo(robotLocation);
    			
    	return directionToRobot;
    	
    }   
    
    static Direction dirFromRobot(RobotInfo robot) {
    	
    	MapLocation myLocation = rc.getLocation();
    	MapLocation robotLocation = robot.location;
    	
    	Direction directionFromRobot = robotLocation.directionTo(myLocation);
            			
    	return directionFromRobot;
    	
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    
    /**
     * Setups a check-in function for all robots at the start of each turn. Checking in 
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static int robotCheckIn() throws GameActionException {
    	
    	int myID = rc.getID();
    	RobotType myType = rc.getType();
    	
    	// indexCount - channel for counting number of robots of each type
    	// indexIdMin - first ID channel for robots of a certain type 
    	// indexIdMax - last ID channel for robots of a certain type
    	int indexCount, indexIdMin, indexIdMax;
    	switch(myType){
    		case ARCHON: {
    			indexCount = 1;
    			indexIdMin = 10;
    			indexIdMax = 19;
    			break;
    		}	
    		case GARDENER: {
    			indexCount = 2;
    			indexIdMin = 20;
    			indexIdMax = 39;
    			break;
    		}	
    		case LUMBERJACK: {
    			indexCount = 3;
    			indexIdMin = 40;
    			indexIdMax = 59;
    			break;
    		}	
    		case SOLDIER: {
    			indexCount = 4;
    			indexIdMin = 60;
    			indexIdMax = 79;
    			break;
    		}	
    		case TANK: {
    			indexCount = 5;
    			indexIdMin = 80;
    			indexIdMax = 89;
    			break;
    		}	
    		case SCOUT: {
    			indexCount = 6;
    			indexIdMin = 90;
    			indexIdMax = 99;
    			break;  
    		}	
    		default:
    			indexCount = 0;
    			indexIdMin = 10;
    			indexIdMax = 99;
    			break;
    	}
    	
    	int myIndex = -1;    	
    	    	
    	for (int i=indexIdMin; i<=indexIdMax; i++){
    		
    		int currentID = rc.readBroadcast(i);
    		    		
    		if(currentID==0){
    			
    			int cnt = rc.readBroadcast(indexCount);
    			rc.broadcast(indexCount, cnt+1);
    			rc.broadcast(i, myID);
    			
    			MapLocation myLocation = rc.getLocation();
    			rc.broadcast(i*10+0,(int)myLocation.x);
    			rc.broadcast(i*10+1,(int)myLocation.x);
    			
    			myIndex = i;
    			
    			break;
    			
    		} else if(currentID==myID) {
    			
    			MapLocation myLocation = rc.getLocation();
    			rc.broadcast(i*10+0,(int)myLocation.x);
    			rc.broadcast(i*10+1,(int)myLocation.x);
    			
    			myIndex = i;
    			
    			break;
    			
    		} else if (i==indexIdMax) {
    			
    			System.out.println("Out of storage");
    			
    			break;
    			
    		}	else {

    		}
    		
   		
    	}

    	
    	return myIndex;
	
    	
    	
    }
    
    

}



