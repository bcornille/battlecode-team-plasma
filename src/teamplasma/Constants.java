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
	int MAX_COUNT_TREES = (int)(GameConstants.WATER_HEALTH_REGEN_RATE / GameConstants.BULLET_TREE_DECAY_RATE) * MAX_COUNT_LUMBERJACK;
	
	/**
	 * CHANNEL_COUNT_XXX 
	 * 
	 * Sets the channels for counting the number of each robot currently in play
	**/
	int CHANNEL_COUNT_SPAWNED = 8;
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
         * ARCHON-GARDENER Movement Limitation Values
	 *
	 *
	 **/
        float GARDENER_RAD = 40.0f;
        float GARDENER_RAD_SQR = GARDENER_RAD*GARDENER_RAD;
        float ARCHON_BORDER_RAD = 9.5f;
        float ARCHON_BORDER_RAD_SQR = ARCHON_BORDER_RAD*ARCHON_BORDER_RAD;

        /**
         * Unit Build Probabilities
	 *
	 *
	 **/
        float SOLDIER_BUILD_PROB = 0.01f;
        float TANK_BUILD_PROB = 0.1f;
        float LUMBERJACK_BUILD_PROB = 0.01f;
        float SCOUT_BUILD_PROB = 0.01f;
    
	
}
