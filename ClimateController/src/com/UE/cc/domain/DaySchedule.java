package com.UE.cc.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.UE.cc.common.CCConstants;
import com.UE.cc.util.Time;

/**
 * This class implements the ADT of a 24-hr Thermostat schedule
 * 
 * The 24-hours may be divided into an integral number of programmable
 * periods (stored as 'numPeriods'), each with their own start time,
 * set point, operation mode, and fan mode.
 * 
 * Note: Equality between instances of this class is implemented via
 * the Comparable<> interface. The overridden methods equals() and
 * compareTo() only test this.name for equality
 * @author TK
 */
public class DaySchedule implements Comparable<DaySchedule>,CCConstants,Serializable
{
	private static final long serialVersionUID = -3760913060494066204L;
	private String name;
	private List<ProgrammablePeriod> periods;
	
	public DaySchedule(String n, int p)
	{
		this.name = n;
		this.periods = new ArrayList<ProgrammablePeriod>();
		for(int i=0; i<p; i++)
			periods.add(new ProgrammablePeriod(new Time(24/p*i,0),70,COOL,AUTO));
	}
	
	public DaySchedule(String n, ArrayList<ProgrammablePeriod> p) throws Exception
	{
		this.name = n;
		this.periods = p;
		if(!this.scheduleIsValid())
			throw new Exception("Schedule '" + name + "': Invalid Schedule");
	}
	
	@Override
	public boolean equals(Object other) 
	{
		if(other == null) return false;
		if(!(other instanceof DaySchedule)) return false;
		if(this.name == null) return ((DaySchedule)other).name == null;
		return (this.name).equals(((DaySchedule)other).name);
	}

	@Override
	public String toString() {
		return name;
	}

	public boolean scheduleIsValid() 
	{
		for(int i=1; i<periods.size(); i++)
			if((periods.get(i).getStartTime().before(periods.get(i-1).getStartTime())) || periods.get(i).getStartTime().equals(periods.get(i-1).getStartTime())) return false;
		return true;
	}

	public int getNumPeriods() {
		return periods.size();
	}

	@Override
	public int compareTo(DaySchedule other) 
	{
		if(other == null) return -1;
		if(this.name == null)
			return other.name == null ? 0:1;
		if(other.name == null) return -1; 
		return (this.name).compareTo(other.name);
	}

	public void stepNumPeriods(int delta) 
	{
		if((delta == 1 && periods.size() < NUM_PERIODS_MAX) || (delta == -1 && periods.size()> NUM_PERIODS_MIN))
		{
			if(delta == 1)
			{
				//Initialize new period to match settings of previous period
				ProgrammablePeriod prevPeriod = periods.get(periods.size()-1);
				ProgrammablePeriod newPeriod = prevPeriod.copy();
				int previousHour = prevPeriod.getStartTime().getHour();
				newPeriod.setStartTime(new Time(previousHour+(24-previousHour)/2,0));
				periods.add(newPeriod);
			}
			else
			{
				periods.remove(periods.size()-1);
			}
		}
	}
	
	/**
	 * set this DaySchedule's number of programmable periods
	 * @param newNumPeriods - new value; must be between 1 & 24
	 */
	public void updateNumPeriods(int newNumPeriods)
	{
		int previousHour;
		if(newNumPeriods >= NUM_PERIODS_MIN && newNumPeriods <= NUM_PERIODS_MAX && newNumPeriods != periods.size())
		{
			if(newNumPeriods > periods.size())
			{
				for(int i=periods.size(); i<newNumPeriods; i++)
				{
					ProgrammablePeriod prevPeriod = periods.get(periods.size()-1);
					ProgrammablePeriod newPeriod = prevPeriod.copy();
					previousHour = prevPeriod.getStartTime().getHour();
					newPeriod.setStartTime(new Time(previousHour+(24-previousHour)/2,0));
					periods.add(newPeriod);
				}
			}
			else
			{
				for(int i=periods.size()-1; i>=newNumPeriods; i--)
					periods.remove(i);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ProgrammablePeriod getPeriod(Time t)
	{//TODO: Change linear to binary search
		for(int i=1; i<periods.size(); i++)
		{
			Time ithPeriodStartTime = periods.get(i).getStartTime();
			if(t.before(ithPeriodStartTime))
				return periods.get(i-1);
		}
		return periods.get(periods.size()-1);
	}
	
	public static DaySchedule getDefaultWeekend()
	{
		ArrayList<ProgrammablePeriod> weekendPP = new ArrayList<ProgrammablePeriod>();
		weekendPP.add(new ProgrammablePeriod(new Time(0,0), 62, HEAT, AUTO));
		weekendPP.add(new ProgrammablePeriod(new Time(8,0), 70, HEAT, AUTO));
		weekendPP.add(new ProgrammablePeriod(new Time(10,0), 62, HEAT, AUTO));
		weekendPP.add(new ProgrammablePeriod(new Time(18,0), 70, HEAT, AUTO));
		weekendPP.add(new ProgrammablePeriod(new Time(22,0), 62, HEAT, AUTO));
		DaySchedule defaultWeekend = null;
		try {
			defaultWeekend = new DaySchedule(WEEKEND,weekendPP);
		} catch (Exception e) {
			System.out.println("DaySchedule.getDefaultWeekend()" + e.getMessage());
		}
		return defaultWeekend;
	}
	
	public static DaySchedule getDefaultWeekday()
	{
		ArrayList<ProgrammablePeriod> weekdayPP = new ArrayList<ProgrammablePeriod>();
		weekdayPP.add(new ProgrammablePeriod(new Time(0,0), 62, HEAT, AUTO));
		weekdayPP.add(new ProgrammablePeriod(new Time(6,0), 70, HEAT, AUTO));
		weekdayPP.add(new ProgrammablePeriod(new Time(8,0), 62, HEAT, AUTO));
		weekdayPP.add(new ProgrammablePeriod(new Time(18,0), 70, HEAT, AUTO));
		weekdayPP.add(new ProgrammablePeriod(new Time(22,0), 62, HEAT, AUTO));
		DaySchedule defaultWeekday = null;
		try {
			defaultWeekday = new DaySchedule(WEEKDAY,weekdayPP);
		} catch (Exception e) {
			System.out.println("DaySchedule.getDefaultWeekday()" + e.getMessage());
		}
		return defaultWeekday;
	}
	
	public ArrayList<ProgrammablePeriod> getPeriods() {
		return (ArrayList<ProgrammablePeriod>) periods;
	}

	public String getName() {
		return this.name;
	}
}
