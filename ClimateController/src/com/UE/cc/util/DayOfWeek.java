package com.UE.cc.util;

import java.util.HashMap;
import java.util.Map;

public enum DayOfWeek 
{
	SUNDAY(1),MONDAY(2),TUESDAY(3),WEDNESDAY(4),THURSDAY(5),FRIDAY(6),SATURDAY(7);
	//Note: these values must match Java.util.Calendar for server implementation
	
	private int v;
	private static final Map<Integer,DayOfWeek> lookup = new HashMap<Integer,DayOfWeek>();
	static
	{
		for (DayOfWeek d: DayOfWeek.values())
			lookup.put(d.getV(), d);
	}
	
	private DayOfWeek(int i) {
		this.v = i;
	}
	
	public int getV() {
		return v;
	}
	
	public static DayOfWeek get(int i) {
		return lookup.get(i);
	}

	public static int getSize() {
		return lookup.size()+1;
	}
}
