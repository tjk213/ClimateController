package com.UE.cc.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

import com.UE.cc.common.CCConstants;
import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.HvacSystem;
import com.UE.cc.domain.Schedule;
import com.UE.cc.util.DayOfWeek;

public class CCServerManager extends Observable implements CCConstants 
{
	private HvacSystem hvac;
	private Schedule schedule;
	private ArrayList<DaySchedule> daySchedules;
	
	public CCServerManager()
	{
		this.hvac = new HvacSystem("Zone 1", 68, 70, HEAT, ON, HEAT, AUTO);
		this.daySchedules = new ArrayList<DaySchedule>();
		this.schedule = initSchedule();
	}
	
	private Schedule initSchedule() 
	{
		Schedule s = Schedule.getDefaultSchedule();
		saveDaysInSchedule(s);
		return s;
	}
	
	public void saveDaysInSchedule(Schedule s)
	{
		EnumMap<DayOfWeek, DaySchedule> e = s.getScheduleMap();
		Collection<DaySchedule> c = e.values();
		Set<DaySchedule> set = new HashSet<DaySchedule>(c);
		Iterator<DaySchedule> i = set.iterator();
		while(i.hasNext())
			saveDay(i.next());
	}

	/**
	 * If dailySchedules contains DaySchedule with same name, replace
	 * that DaySchedule with this
	 * Otherwise, add this
	 * Note:myArrayList<E>.contains() simply calls E.equals
	 * 	 from DaySchedule.equals() it can be seen that only names are compared
	 */
	private void updateDailySchedules(DaySchedule day) 
	{
		if(daySchedules.contains(day))
			daySchedules.set(daySchedules.indexOf(day), day); //Only names are compared, older DaySchedule is being replaced by this
		else
			daySchedules.add(day);
		Collections.sort(daySchedules);		
	}
	
	public boolean saveDay(DaySchedule day) 
	{
		if(!day.scheduleIsValid()) return false;
		updateDailySchedules(day);
		return true;
	}
	
	private void removeFromSchedule(DaySchedule toBeRemoved) 
	{
		for(DayOfWeek d: DayOfWeek.values())
			if(schedule.getDaySchedule(d).equals(toBeRemoved))
				schedule.setSchedule(d, daySchedules.get(0));
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public ArrayList<DaySchedule> getDaySchedules() {
		return daySchedules;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	public void remove(DaySchedule toBeRemoved)
	{
		daySchedules.remove(toBeRemoved);
		removeFromSchedule(toBeRemoved);
	}

	public HvacSystem getHvac() {
		return hvac;
	}
	
	public static void printToStandardOut(String out) {
		System.out.println(Calendar.getInstance().getTime().toString() + " - " + Thread.currentThread().getName() + " - " + out);
	}
	
	public static void printToStandardErr(String err) {
		System.err.println(Calendar.getInstance().getTime().toString() + " - " + Thread.currentThread().getName() + " - " + err);
	}
}
