package com.UE.cc.domain;

import java.io.Serializable;

import com.UE.cc.common.CCConstants;

/**
 * Object to represent current state of HVAC system.
 * 
 */
public class HvacSystem implements CCConstants,Serializable
{
	private static final long serialVersionUID = -4317229300710303192L;
	private String zoneTitle;
	private int currentTemp;
	private int setPoint;
	private int hvacStatus;
	private int fanStatus;
	private int opMode;
	private int fanMode;
	private ProgrammablePeriod currentPeriod;
	private boolean onHold;
	
	public HvacSystem(String zt, int ct, int sp,int hvacStatus,int fanStatus, int om, int fm)
	{
		this.zoneTitle = zt;
		this.currentTemp = ct;
		this.setPoint = sp;
		this.hvacStatus = hvacStatus;
		this.fanStatus = fanStatus;
		this.opMode = om;
		this.fanMode = fm;
	}
	
	public String getZoneTitle() {
		return zoneTitle;
	}

	//TODO: prevent this from being accessible by UI Layer
	public void setZoneTitle(String zoneTitle) {
		this.zoneTitle = zoneTitle;
	}

	public int getCurrentTemp() {
		return currentTemp;
	}

	public void setCurrentTemp(int currentTemp) {
		this.currentTemp = currentTemp;
	}

	public int getTargetTemp() {
		return setPoint;
	}

	public void setSetPoint(int setPoint) {
		this.setPoint = setPoint;
	}

	public int getHvacStatus() {
		return hvacStatus;
	}

	public void setHvacStatus(int hvacStatus) {
		this.hvacStatus = hvacStatus;
	}

	public int getFanStatus() {
		return fanStatus;
	}

	public void setFanStatus(int fanStatus) {
		this.fanStatus = fanStatus;
	}

	public int getOpMode() {
		return opMode;
	}

	public void setOpMode(int opMode) {
		this.opMode = opMode;
	}

	public void setFanMode(int fanMode) {
		this.fanMode = fanMode;
	}

	public int getFanMode() {
		return fanMode;
	}

	public void stepTargetTemp(int delta) {
		this.setPoint += delta;
	}

	public boolean updateProgrammablePeriod(ProgrammablePeriod newPeriod) 
	{
		if(!newPeriod.equals(currentPeriod) && !onHold)
		{
			currentPeriod = newPeriod;
			this.setPoint = newPeriod.getSetPoint();
			this.opMode = newPeriod.getOpMode();
			this.fanMode = newPeriod.getFanMode();
			return true;
		}
		return false;
	}

	public boolean isOnHold() {
		return onHold;
	}

	public void setOnHold(boolean onHold) {
		this.onHold = onHold;
	}
}
