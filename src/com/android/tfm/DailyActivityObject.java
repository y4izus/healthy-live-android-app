package com.android.tfm;

public class DailyActivityObject {
	
	int num_min_rest;
	int num_min_moderate;
	int num_min_intensive;
	
	
	
	String date_activity;
	
	boolean mission_accomplished_day;
	
	int fk_week_activity;

	/**
	 * Constructor.
	 * 
	 * @param num_min_rest
	 * @param num_min_moderate
	 * @param num_min_intensive
	 * @param date_activity
	 * @param mission_accomplished_day
	 * @param fk_week_activity
	 */
	public DailyActivityObject(int num_min_rest, int num_min_moderate,
			int num_min_intensive, String date_activity,
			boolean mission_accomplished_day, int fk_week_activity) {
		super();
		this.num_min_rest = num_min_rest;
		this.num_min_moderate = num_min_moderate;
		this.num_min_intensive = num_min_intensive;
		this.date_activity = date_activity;
		this.mission_accomplished_day = mission_accomplished_day;
		this.fk_week_activity = fk_week_activity;
	}

	public int getNum_min_rest() {
		return num_min_rest;
	}

	public void setNum_min_rest(int num_min_rest) {
		this.num_min_rest = num_min_rest;
	}

	public int getNum_min_moderate() {
		return num_min_moderate;
	}

	public void setNum_min_moderate(int num_min_moderate) {
		this.num_min_moderate = num_min_moderate;
	}

	public int getNum_min_intensive() {
		return num_min_intensive;
	}

	public void setNum_min_intensive(int num_min_intensive) {
		this.num_min_intensive = num_min_intensive;
	}

	public String getDate_activity() {
		return date_activity;
	}

	public void setDate_activity(String date_activity) {
		this.date_activity = date_activity;
	}

	public boolean isMission_accomplished_day() {
		return mission_accomplished_day;
	}

	public void setMission_accomplished_day(boolean mission_accomplished_day) {
		this.mission_accomplished_day = mission_accomplished_day;
	}

	public int getFk_week_activity() {
		return fk_week_activity;
	}

	public void setFk_week_activity(int fk_week_activity) {
		this.fk_week_activity = fk_week_activity;
	}	
}
