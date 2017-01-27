package teamplasma;

import battlecode.common.*;

public interface Channels {
	
	/**
	 * COUNT_XXX 
	 * 
	 * Sets the channels for counting the number of each robot currently in play
	**/
	
	int COUNT_SPAWNED = 0;
	int COUNT_ARCHON = 1;
	int COUNT_GARDENER = 2;
	int COUNT_LUMBERJACK = 3;
	int COUNT_SOLDIER = 4;
	int COUNT_TANK = 5;
	int COUNT_SCOUT = 6;
	
	/**
	 * CHANNEL_MAP_XXX 
	 * 
	 * Sets the channels for keeping track of the map limits
	**/
	
	int MAP_XMIN = 10;
	int MAP_XMAX = 11;
	int MAP_YMIN = 12;
	int MAP_YMAX = 13;
	int MAP_XCEN = 14;
	int MAP_YCEN = 15;
	
	/**
	 * CHANNEL_GROVEi_XXX 
	 * 
	 * Sets the channels for keeping track of the grove information
	**/

	int GROVE1_PARENT = 20;
	int GROVE1_XMIN = 21;
	int GROVE1_XMAX = 22;
	int GROVE1_YMIN = 23;
	int GROVE1_YMAX = 24;
	int GROVE1_XCEN = 25;
	int GROVE1_YCEN = 26;
	
	int GROVE2_PARENT = 30;
	int GROVE2_XMIN = 31;
	int GROVE2_XMAX = 32;
	int GROVE2_YMIN = 33;
	int GROVE2_YMAX = 34;
	int GROVE2_XCEN = 35;
	int GROVE2_YCEN = 36;

	int GROVE3_PARENT = 40;
	int GROVE3_XMIN = 41;
	int GROVE3_XMAX = 42;
	int GROVE3_YMIN = 43;
	int GROVE3_YMAX = 44;
	int GROVE3_XCEN = 45;
	int GROVE3_YCEN = 46;
	
	int BUILD_DIRECTION = 50;
	
	/**
	 * CHANNEL_MIN/MAX_XXX 
	 * 
	 * Sets the channels for ID storage for robots currently in play.
	 * 
	**/
	
	int MIN_ROBOT = 1000;
	
	int MIN_ARCHON = MIN_ROBOT;
	int MAX_ARCHON = MIN_ARCHON + Constants.MAX_COUNT_ARCHON -1;
	
	int MIN_GARDENER = MAX_ARCHON + 1;
	int MAX_GARDENER = MIN_GARDENER + Constants.MAX_COUNT_GARDENER -1;
	
	int MIN_LUMBERJACK = MAX_GARDENER + 1;
	int MAX_LUMBERJACK = MIN_LUMBERJACK + Constants.MAX_COUNT_LUMBERJACK -1;
	
	int MIN_SCOUT = MAX_LUMBERJACK + 1;
	int MAX_SCOUT = MIN_SCOUT + Constants.MAX_COUNT_SCOUT -1;
	
	int MIN_SOLDIER = MAX_SCOUT + 1;
	int MAX_SOLDIER = MIN_SOLDIER + Constants.MAX_COUNT_SOLDIER -1;
	
	int MIN_TANK = MAX_SOLDIER + 1;
	int MAX_TANK = MIN_TANK + Constants.MAX_COUNT_TANK -1;
	
	int MAX_ROBOT = MAX_SCOUT;
	
	/**
	 * GROVEi_XXX 
	 * 
	 * Sets the channels for keeping track of the grove contents
	**/
	
	int GROVE1_START = 7000;
	
	int GROVE1_LOCATIONS = GROVE1_START;
	int GROVE1_ASSIGNED = GROVE1_START + Constants.MAX_COUNT_GROVE;
	int GROVE1_X = GROVE1_START + 2*Constants.MAX_COUNT_GROVE;
	int GROVE1_Y = GROVE1_START + 3*Constants.MAX_COUNT_GROVE;
	
	int GROVE2_START = 8000;
	
	int GROVE2_LOCATIONS = GROVE2_START;
	int GROVE2_ASSIGNED = GROVE2_START + Constants.MAX_COUNT_GROVE;
	int GROVE2_X = GROVE2_START + 2*Constants.MAX_COUNT_GROVE;
	int GROVE2_Y = GROVE2_START + 3*Constants.MAX_COUNT_GROVE;
	
	int GROVE3_START = 9000;
	
	int GROVE3_LOCATIONS = GROVE3_START;
	int GROVE3_ASSIGNED = GROVE3_START + Constants.MAX_COUNT_GROVE;
	int GROVE3_X = GROVE3_START + 2*Constants.MAX_COUNT_GROVE;
	int GROVE3_Y = GROVE3_START + 3*Constants.MAX_COUNT_GROVE;
	
	
}
