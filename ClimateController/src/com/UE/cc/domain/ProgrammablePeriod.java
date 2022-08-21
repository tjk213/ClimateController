package com.UE.cc.domain;

import java.io.Serializable;

import com.UE.cc.util.Time;

public class ProgrammablePeriod implements Serializable,Comparable<ProgrammablePeriod>
{
	private static final long serialVersionUID = 4992368998204978626L;
	private Time startTime;
	private int setPoint;
	private int opMode;
	private int fanMode;
	
	public ProgrammablePeriod(Time st, int sp, int om, int fm)
	{
		this.startTime = st;
		this.setPoint = sp;
		this.opMode = om;
		this.fanMode = fm;
	}
	
	public ProgrammablePeriod copy() {
		return new ProgrammablePeriod(this.startTime, this.setPoint, this.opMode, this.fanMode);
	}
	
	@Override
	public String toString() {
		return "Start Time: " + startTime.toString() + " Set Point: " + setPoint + " Op Mode: " + opMode + " Fan Mode: " + fanMode;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other == null) return false;
		if(!(other instanceof ProgrammablePeriod)) return false;
		ProgrammablePeriod otherPP = (ProgrammablePeriod) other;
		return this.startTime.equals(otherPP.startTime) && this.setPoint == otherPP.getSetPoint() && this.opMode == otherPP.getOpMode() && this.fanMode == otherPP.getFanMode();
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public int getSetPoint() {
		return setPoint;
	}

	public void setSetPoint(int setPoint) {
		this.setPoint = setPoint;
	}

	public int getOpMode() {
		return opMode;
	}

	public void setOpMode(int opMode) {
		this.opMode = opMode;
	}

	public int getFanMode() {
		return fanMode;
	}

	public void setFanMode(int fanMode) {
		this.fanMode = fanMode;
	}

	@Override
	public int compareTo(ProgrammablePeriod other) 
	{
		if(other == null) return -1;
		if(this.startTime == null)
			return other.getStartTime() == null ? 0:1;
		if(other.getStartTime() == null) return -1;
		int stComp = this.startTime.compareTo(other.getStartTime());
		if(stComp != 0)
			return stComp;
		int spComp = ((Integer)this.setPoint).compareTo(other.getSetPoint());
		if(spComp != 0)
			return spComp;
		int omComp = ((Integer)this.opMode).compareTo(other.getOpMode());
		if(omComp != 0)
			return omComp;
		int fmComp = ((Integer)this.fanMode).compareTo(other.getFanMode());
		if(fmComp != 0)
			return fmComp;
		return 0;
	}
}
