package com.UE.cc.common;

public interface CCConstants 
{
	public static final int OFF=0,HEAT=1, COOL=2;
	public static final String[] OPERATION_MODES = {"Off","Heat","Cool"};
	public static final String[] HVAC_STATUSES = { "Idle","Heating", "Cooling"};
	public static final String OP_MODE_SELECT_TITLE = "Select Operation Mode";
	
	public static final int AUTO=0, ON=1;
	public static final String[] FAN_MODES = {"Auto","On"};
	public static final String[] FAN_STATUSES = {"Off","On"};
	public static final String FAN_MODE_SELECT_TITLE = "Select Fan Mode";
	
	public static final int TARGET_TEMP_MIN = 50;
	public static final int TARGET_TEMP_MAX = 90;
	
	public static final int NUM_PERIODS_MIN = 1;
	public static final int NUM_PERIODS_MAX = 24;
	
	public static final String DAYSCHEDULE = "DaySchedule";
	public static final String HAS_DAYSCHEDULE = "Has_DaySchedule";
	public static final String WEEKEND = "Weekend";
	public static final String WEEKDAY = "Weekday";
	
	public static final int MAX_TRIES = 3;
	public static final int ZONE_TITLE_MAX_LENGTH = 30;           
	                            
	public static final long UP_DOWN_INT_DELAY = 750;
	
	public static final int TCP_PORT = 1212;
	public static final int CMD_MAX_LENGTH = 31;
	public static final int PARAM_MAX_LENGTH = 30;
	public static final int ARDUINO_MAX_RESPONSE = 30;
	public static final int HEADER_SIZE = 7;
	public static final String ANDROID_HEADER = "Android";
	public static final String ARDUINO_HEADER = "Arduino";
	public static final String ARDUINO_PARAM_DELIMETER = "\n";
	
	public static final String TCP_CMD_GET_ZONE_TITLE = "getZoneTitle";
	public static final String TCP_CMD_GET_CURRENT_TEMP = "getCurrentTemp";
	public static final String TCP_CMD_GET_SET_POINT = "getSetPoint";
	public static final String TCP_CMD_GET_HVAC_STATUS = "getHvacStatus";
	public static final String TCP_CMD_GET_FAN_STATUS = "getFanStatus";
	public static final String TCP_CMD_GET_OP_MODE = "getOpMode";
	public static final String TCP_CMD_GET_FAN_MODE = "getFanMode";
	public static final String TCP_CMD_SET_SET_POINT = "setSetPoint";
	public static final String TCP_CMD_SET_OP_MODE = "setOpMode";
	public static final String TCP_CMD_SET_FAN_MODE = "setFanMode";
	public static final String TCP_CMD_SET_ZONE_TITLE = "setZoneTitle";
	public static final String TCP_CMD_SET_HOLD = "setHold";
	public static final String TCP_CMD_GET_WEEK_SCHEDULE = "getWeekSchedule";
	public static final String TCP_CMD_UPDATE_WEEKLY_SCHEDULE = "updateWeeklySchedule";
	public static final String TCP_CMD_GET_DAY_SCHEDULES = "getDaySchedules";
	public static final String TCP_CMD_GET_HVAC = "getHvac";
	public static final String TCP_CMD_SAVE_DAY = "saveDay";
	public static final String TCP_CMD_SYNC_ARDUINO = "syncArduino";
	public static final String TCP_CMD_SET_SCHEDULE = "setSchedule";
	public static final String TCP_CMD_DELETE_DAY = "deleteDay";
	public static final String TCP_CMD_SET_CURRENT_TEMP = "setCurrentTemp";
	public static final String TCP_CMD_SET_HVAC_STATUS = "setHvacStatus";
	public static final String TCP_CMD_SET_FAN_STATUS = "setFanStatus";
	public static final String TCP_CMD_REFRESH_SCHEDULE_ACTIVITY = "refrSchedAct";
}
