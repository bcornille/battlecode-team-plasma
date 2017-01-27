package teamplasma;

import battlecode.common.*;

public interface Constants {

	/**
	 * MAX_COUNT_XXX 
	 * 
	 * Sets the maximum number of each robot currently in play at any one time
	**/
	int MAX_COUNT_ARCHON = 3;
	int MAX_COUNT_GARDENER = 20;
	int MAX_COUNT_LUMBERJACK = 10;
	int MAX_COUNT_SOLDIER = 50;
	int MAX_COUNT_TANK = 10;
	int MAX_COUNT_SCOUT = 10;
	int MAX_COUNT_TREE = 4; //(int)(GameConstants.WATER_HEALTH_REGEN_RATE / GameConstants.BULLET_TREE_DECAY_RATE) * MAX_COUNT_GARDENER;

	
	/**
	 * CHANNEL_COUNT_XXX 
	 * 
	 * Sets the channels for counting the number of each robot currently in play
	**/
	int CHANNEL_COUNT_SPAWNED = 0;
	int CHANNEL_COUNT_ARCHON = 1;
	int CHANNEL_COUNT_GARDENER = 2;
	int CHANNEL_COUNT_LUMBERJACK = 3;
	int CHANNEL_COUNT_SOLDIER = 4;
	int CHANNEL_COUNT_TANK = 5;
	int CHANNEL_COUNT_SCOUT = 6;
	
	/**
	 * CHANNEL_MAP_XXX 
	 * 
	 * Sets the channels for keeping track of the map limits
	**/
	int CHANNEL_MAP_XMIN = 7;
	int CHANNEL_MAP_XMAX = 8;
	int CHANNEL_MAP_YMIN = 9;
	int CHANNEL_MAP_YMAX = 10;
	int CHANNEL_MAP_XCEN = 11;
	int CHANNEL_MAP_YCEN = 12;
	
	/**
	 * CHANNEL_GROVE_XXX 
	 * 
	 * Sets the channels for keeping track of the grove limits
	**/
	int CHANNEL_GROVE_XMIN = 13;
	int CHANNEL_GROVE_XMAX = 14;
	int CHANNEL_GROVE_YMIN = 15;
	int CHANNEL_GROVE_YMAX = 16;
	int CHANNEL_GROVE_XCEN = 17;
	int CHANNEL_GROVE_YCEN = 18;

	/**
	 * CHANNEL_MIN/MAX_XXX 
	 * 
	 * Sets the channels for ID storage for robots currently in play.
	 * 
	**/
	int CHANNEL_MIN_ARCHON = 1000;
	int CHANNEL_MAX_ARCHON = CHANNEL_MIN_ARCHON + MAX_COUNT_ARCHON -1;
	
	int CHANNEL_MIN_GARDENER = CHANNEL_MAX_ARCHON + 1;
	int CHANNEL_MAX_GARDENER = CHANNEL_MIN_GARDENER + MAX_COUNT_GARDENER -1;
	
	int CHANNEL_MIN_LUMBERJACK = CHANNEL_MAX_GARDENER + 1;
	int CHANNEL_MAX_LUMBERJACK = CHANNEL_MIN_LUMBERJACK + MAX_COUNT_LUMBERJACK -1;
	
	int CHANNEL_MIN_SOLDIER = CHANNEL_MAX_LUMBERJACK + 1;
	int CHANNEL_MAX_SOLDIER = CHANNEL_MIN_SOLDIER + MAX_COUNT_SOLDIER -1;
	
	int CHANNEL_MIN_TANK = CHANNEL_MAX_SOLDIER + 1;
	int CHANNEL_MAX_TANK = CHANNEL_MIN_TANK + MAX_COUNT_TANK -1;
	
	int CHANNEL_MIN_SCOUT = CHANNEL_MAX_TANK + 1;
	int CHANNEL_MAX_SCOUT = CHANNEL_MIN_SCOUT + MAX_COUNT_SCOUT -1;
	
	int CHANNEL_MIN = CHANNEL_MIN_ARCHON;
	int CHANNEL_MAX = CHANNEL_MAX_SCOUT;
	
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
	
	float SPACING_ARCHON_ARCHON = 5.0f; 
	float SPACING_GARDENER_GARDENER = 8.0f;
	float SPACING_LUMBERJACK_LUMBERJACK = 2.0f;
	float SPACING_SCOUT_SCOUT = 12.0f;
	float SPACING_SOLDIER_SOLDIER = 3.0f;
	float SPACING_TANK_TANK = 5.0f;

	float SPACING_ARCHON_GARDENER = 5.0f;
	float SPACING_ARCHON_LUMBERJACK = 5.0f;
	float SPACING_ARCHON_SCOUT = 12.0f;
	float SPACING_ARCHON_SOLDIER = 3.0f;
	float SPACING_ARCHON_TANK = 3.0f;
	float SPACING_GARDENER_LUMBERJACK = 3.0f;
	float SPACING_GARDENER_SCOUT = 12.0f;
	float SPACING_GARDENER_SOLDIER = 3.0f;
	float SPACING_GARDENER_TANK = 10.0f;
	float SPACING_LUMBERJACK_SCOUT = 12.0f;
	float SPACING_LUMBERJACK_SOLDIER = 3.0f;
	float SPACING_LUMBERJACK_TANK = 3.0f;
	float SPACING_SCOUT_SOLDIER = 5.0f;
	float SPACING_SCOUT_TANK = 5.0f;
	float SPACING_SOLDIER_TANK = 3.0f;
	
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
	float MAX_BULLET_BANK = 500.0f;
	
	/**
	 * Miscellaneous definitions.
	 */
	float TWO_PI = 2.0f * (float)Math.PI;
	int EARLY_GAME_END = 200;
	
	/**
	 * Gardening Constants
	 */
	float GROVE_SPACING = 6.0f*GameConstants.BULLET_TREE_RADIUS + 2.1f*RobotType.ARCHON.bodyRadius; 
	
	int CHANNEL_BUILD_DIRECTION = 20;
	
	int CHANNEL_GROVE_START = 9000;
	int NUM_GROVE_MAX = 100;
	
	int CHANNEL_GROVE_LOCATIONS = CHANNEL_GROVE_START;
	int CHANNEL_GROVE_ASSIGNED = CHANNEL_GROVE_START + NUM_GROVE_MAX;
	int CHANNEL_GROVE_X = CHANNEL_GROVE_START + 2*NUM_GROVE_MAX;
	int CHANNEL_GROVE_Y = CHANNEL_GROVE_START + 3*NUM_GROVE_MAX;
	
	
}
