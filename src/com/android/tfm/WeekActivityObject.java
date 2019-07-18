package com.android.tfm;

public class WeekActivityObject {
	
	int total_moderate;
	int total_intensive;
	
	int aim_day_moderate;
	int aim_day_intensive;
	
	int aim_week_moderate;
	int aim_week_intensive;
	
	int mission_accomplished_week;
	String first_day_week;
	public WeekActivityObject(int total_moderate, int total_intensive,
			int aim_day_moderate, int aim_day_intensive, int aim_week_moderate,
			int aim_week_intensive, int mission_accomplished_week,
			String first_day_week) {
		super();
		this.total_moderate = total_moderate;
		this.total_intensive = total_intensive;
		this.aim_day_moderate = aim_day_moderate;
		this.aim_day_intensive = aim_day_intensive;
		this.aim_week_moderate = aim_week_moderate;
		this.aim_week_intensive = aim_week_intensive;
		this.mission_accomplished_week = mission_accomplished_week;
		this.first_day_week = first_day_week;
	}
	public int getTotal_moderate() {
		return total_moderate;
	}
	public void setTotal_moderate(int total_moderate) {
		this.total_moderate = total_moderate;
	}
	public int getTotal_intensive() {
		return total_intensive;
	}
	public void setTotal_intensive(int total_intensive) {
		this.total_intensive = total_intensive;
	}
	public int getAim_day_moderate() {
		return aim_day_moderate;
	}
	public void setAim_day_moderate(int aim_day_moderate) {
		this.aim_day_moderate = aim_day_moderate;
	}
	public int getAim_day_intensive() {
		return aim_day_intensive;
	}
	public void setAim_day_intensive(int aim_day_intensive) {
		this.aim_day_intensive = aim_day_intensive;
	}
	public int getAim_week_moderate() {
		return aim_week_moderate;
	}
	public void setAim_week_moderate(int aim_week_moderate) {
		this.aim_week_moderate = aim_week_moderate;
	}
	public int getAim_week_intensive() {
		return aim_week_intensive;
	}
	public void setAim_week_intensive(int aim_week_intensive) {
		this.aim_week_intensive = aim_week_intensive;
	}
	public int getMission_accomplished_week() {
		return mission_accomplished_week;
	}
	public void setMission_accomplished_week(int mission_accomplished_week) {
		this.mission_accomplished_week = mission_accomplished_week;
	}
	public String getFirst_day_week() {
		return first_day_week;
	}
	public void setFirst_day_week(String first_day_week) {
		this.first_day_week = first_day_week;
	} 
}