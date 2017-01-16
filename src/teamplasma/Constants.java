package teamplasma;

import battlecode.common.*;

public interface Constants {

	/**
	 * MAX_COUNT_XXX 
	 * 
	 * Sets the maximum number of each robot currently in play at any one time
	**/
	int MAX_COUNT_ARCHON = 3;
	int MAX_COUNT_GARDENER = 5;
	int MAX_COUNT_LUMBERJACK = 5;
	int MAX_COUNT_SOLDIER = 5;
	int MAX_COUNT_TANK = 5;
	int MAX_COUNT_SCOUT = 10;
	int MAX_COUNT_TREE = (int)(GameConstants.WATER_HEALTH_REGEN_RATE / GameConstants.BULLET_TREE_DECAY_RATE) * MAX_COUNT_GARDENER;
	
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
	 * CHANNEL_MIN/MAX_XXX 
	 * 
	 * Sets the channels for ID storage for robots currently in play.
	 * 
	**/
	int CHANNEL_MIN_ARCHON = 10;
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
}
