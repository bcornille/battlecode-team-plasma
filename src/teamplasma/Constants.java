package teamplasma;

import battlecode.common.*;

public interface Constants {

	/**
	 * MAX_COUNT_XXX
	 * 
	 * Sets the maximum number of each robot currently in play at any one time
	 **/

	int MAX_COUNT_ARCHON = 3;
	int MAX_COUNT_GARDENER = 10;
	int MAX_COUNT_LUMBERJACK = 20;
	int MAX_COUNT_SOLDIER = 40;
	int MAX_COUNT_TANK = 20;
	int MAX_COUNT_SCOUT = 2;
 	
	/**
	 * Gardening Constants
	 */

	int MAX_COUNT_GROVE = 100;
	int MAX_COUNT_TREE = 4;

	float GROVE_SPACING_V = 6.0f * GameConstants.BULLET_TREE_RADIUS + 1.9f * RobotType.ARCHON.bodyRadius;
	float GROVE_SPACING_H = 6.0f * GameConstants.BULLET_TREE_RADIUS + 2.3f * RobotType.ARCHON.bodyRadius;

	/**
	 * Constants related to message related channels.
	 */
	int NUM_MESSAGE_CHANNELS = 10;

	/**
	 * STRIDE_RADIUS_XXX
	 * 
	 * Stride radius for each robot. (Not available in GameConstants)
	 * 
	 */
	float STRIDE_RADIUS_ARCHON = 1.0f;
	float STRIDE_RADIUS_GARDENER = 1.0f;
	float STRIDE_RADIUS_LUMBERJACK = 1.5f;
	float STRIDE_RADIUS_SOLDIER = 2.0f;
	float STRIDE_RADIUS_TANK = 1.0f;
	float STRIDE_RADIUS_SCOUT = 2.5f;

	/**
	 * XXX_SPACING_FRIEND_YYY
	 * 
	 * Desired spacing of friendly units (eventually add all units)
	 * 
	 */
	
	float SPACING_ARCHON_ARCHON = 0.0f; 
	float SPACING_GARDENER_GARDENER = 0.0f;
	float SPACING_LUMBERJACK_LUMBERJACK = 2.0f;
	float SPACING_SCOUT_SCOUT = 3.0f;
	float SPACING_SOLDIER_SOLDIER = 0.0f;
	float SPACING_TANK_TANK = 0.0f;

	float SPACING_ARCHON_GARDENER = 0.0f;
	float SPACING_ARCHON_LUMBERJACK = 5.0f;
	float SPACING_ARCHON_SCOUT = 0.0f;
	float SPACING_ARCHON_SOLDIER = 0.0f;
	float SPACING_ARCHON_TANK = 0.0f;
	float SPACING_GARDENER_LUMBERJACK = 3.0f;
	float SPACING_GARDENER_SCOUT = 0.0f;
	float SPACING_GARDENER_SOLDIER = 0.0f;
	float SPACING_GARDENER_TANK = 0.0f;
	float SPACING_LUMBERJACK_SCOUT = 3.0f;
	float SPACING_LUMBERJACK_SOLDIER = 3.0f;
	float SPACING_LUMBERJACK_TANK = 5.0f;
	float SPACING_SCOUT_SOLDIER = 0.0f;
	float SPACING_SCOUT_TANK = 0.0f;
	float SPACING_SOLDIER_TANK = 0.0f;
	
	float SPACING_GARDENER_ARCHON = SPACING_ARCHON_GARDENER; 
	float SPACING_LUMBERJACK_ARCHON = SPACING_ARCHON_LUMBERJACK; 
	float SPACING_LUMBERJACK_GARDENER = SPACING_GARDENER_LUMBERJACK;
	float SPACING_SCOUT_ARCHON = SPACING_ARCHON_SCOUT;
	float SPACING_SCOUT_GARDENER = SPACING_GARDENER_SCOUT;
	float SPACING_SCOUT_LUMBERJACK = SPACING_LUMBERJACK_SCOUT;
	float SPACING_SOLDIER_ARCHON = SPACING_ARCHON_SOLDIER;
	float SPACING_SOLDIER_GARDENER = SPACING_GARDENER_SOLDIER;
	float SPACING_SOLDIER_LUMBERJACK = SPACING_LUMBERJACK_SOLDIER;
	float SPACING_SOLDIER_SCOUT = SPACING_SCOUT_SOLDIER;
	float SPACING_TANK_ARCHON = SPACING_ARCHON_TANK;
	float SPACING_TANK_GARDENER = SPACING_GARDENER_TANK;
	float SPACING_TANK_LUMBERJACK = SPACING_LUMBERJACK_TANK;
	float SPACING_TANK_SCOUT = SPACING_SCOUT_TANK;
	float SPACING_TANK_SOLDIER = SPACING_SOLDIER_TANK;

	/**
	 * Economy related constants.
	 */
	float MAX_BULLET_BANK = 2000.0f;
	float ATTACK_BULLET_BANK = 300.0f;

	

	/**
	 * Miscellaneous definitions.
	 */
	float TWO_PI = 2.0f * (float) Math.PI;
	int EARLY_GAME_END = 200;

	/**
	 * Related to pathing
	 */
	int MAX_PATH_DEPTH = 100;


}
