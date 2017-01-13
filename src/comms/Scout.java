package comms;

import battlecode.common.*;

public class Scout {
    static void run(RobotController rc) throws GameActionException {
        System.out.println("I'm an scout!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

        	try {
        		
        		// Check if alive
            	
           	
                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                if (robots.length == 0) {
                	
                	Movement.tryMove(Movement.randomDirection());
                	
                } else if (robots.length == 1) {
                	
                	RobotInfo robot = robots[0];
                
                	float distance = Movement.checkDistance(robot);
                	
                	if ( distance > 7 ) {
                		
                		Movement.tryMove(Movement.dirLeftRobot(robot));
                		
                	} else {
                		
                		Movement.tryMove(Movement.dirFromRobot(robot));
                		
                	}
                	                	
                	
                } else {
                	
                	int index = (robots.length - 1);
                	
                	RobotInfo robot = robots[index];
                	
                	float distance = Movement.checkDistance(robot);
                	
                	if ( distance > 7 ) {
                		
                		Movement.tryMove(Movement.dirLeftRobot(robot));
                		
                	} else {
                		
                		Movement.tryMove(Movement.dirFromRobot(robot));
                		
                	}
                	
                }
           

                System.out.println(RobotPlayer.channel);
                
                // End turn
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
}