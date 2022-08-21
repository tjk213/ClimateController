package com.UE.cc.util;

import java.io.Serializable;

public class Time implements Serializable,Comparable<Time>
{
	private static final long serialVersionUID = 1246543390989995620L;
	private int hour;
	private int min;
	
	public Time(int h, int m)
	{
		this.hour = h;
		this.min = m;
	}

	public Time(Time time) 
	{
		this.hour = time.getHour();
		this.min = time.getMin();
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}
	
	@Override
	public String toString()
	{
		
		String s = "AM";
		if(hour > 11)
			s = "PM";
		int h = hour % 12;
		if(h == 0)
			h = 12;
		return Integer.toString(h) + ":" + String.format("%02d",min) + s;
	}

	public boolean before(Time time) 
	{
		if(hour < time.getHour()) return true;
		if(hour == time.getHour() && min < time.getMin()) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other == null) return false;
		if(!(other instanceof Time)) return false;
		Time otherTime = (Time) other;
		return (hour == otherTime.getHour()) && (min == otherTime.getMin());
	}

	@Override
	public int compareTo(Time other) 
	{
		int hComp = ((Integer)this.hour).compareTo(other.getHour());
		if(hComp != 0)
			return hComp;
		int mComp = ((Integer)this.min).compareTo(other.getMin());
		if(mComp != 0)
			return mComp;
		return 0;
	}
}
