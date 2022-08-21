package com.UE.cc.domain;

import java.io.Serializable;
import java.util.EnumMap;

import com.UE.cc.util.DayOfWeek;

public class Schedule implements Serializable 
{
	private static final long serialVersionUID = -3380734138449528621L;
	private EnumMap<DayOfWeek, DaySchedule> scheduleMap;
	
	public Schedule() {
		this.scheduleMap = new EnumMap<DayOfWeek, DaySchedule>(DayOfWeek.class);
	}
	
	public void setSchedule(DaySchedule sun, DaySchedule mon, DaySchedule tues, DaySchedule weds, DaySchedule thurs, DaySchedule fri, DaySchedule sat)
	{
		scheduleMap.put(DayOfWeek.SUNDAY,sun);
		scheduleMap.put(DayOfWeek.MONDAY,mon);
		scheduleMap.put(DayOfWeek.TUESDAY,tues);
		scheduleMap.put(DayOfWeek.WEDNESDAY,weds);
		scheduleMap.put(DayOfWeek.THURSDAY,thurs);
		scheduleMap.put(DayOfWeek.FRIDAY,fri);
		scheduleMap.put(DayOfWeek.SATURDAY,sat);
	}

	public DaySchedule getDaySchedule(DayOfWeek day) {
		return scheduleMap.get(day);
	}

	//TODO: prevent this from being accessible by UI Layer
	public void setSchedule(DayOfWeek day, DaySchedule newSchedule) {
		scheduleMap.put(day,newSchedule);
	}

	public static Schedule getDefaultSchedule() 
	{
		Schedule s = new Schedule();
		DaySchedule weekend = DaySchedule.getDefaultWeekend();
		DaySchedule weekday = DaySchedule.getDefaultWeekday();
		s.setSchedule(weekend,weekday,weekday,weekday,weekday,weekday,weekend);
		return s;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other == null) return false;
		if(!(other instanceof Schedule)) return false;
		Schedule otherSchedule = (Schedule) other;
		if(this.scheduleMap == null) return otherSchedule.scheduleMap == null;
		return this.scheduleMap.equals(otherSchedule.getScheduleMap());
	}

	public EnumMap<DayOfWeek, DaySchedule> getScheduleMap() {
		return scheduleMap;
	}
}
