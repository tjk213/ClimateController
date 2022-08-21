package com.UE.cc.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import com.UE.cc.domain.DaySchedule;
import com.UE.cc.domain.ProgrammablePeriod;

public class DayScheduleComparator implements Comparator<DaySchedule>,Serializable
{
	private static final long serialVersionUID = 6454921868163968745L;
	private int name,numPeriods,p;
	
	@Override
	public int compare(DaySchedule d1, DaySchedule d2) 
	{
		name = d1.getName().compareTo(d2.getName());
		if(name != 0) return name;
		numPeriods = ((Integer)(d1.getNumPeriods())).compareTo(d2.getNumPeriods());
		if(numPeriods != 0) return numPeriods;
		ArrayList<ProgrammablePeriod> d1Periods = d1.getPeriods();
		ArrayList<ProgrammablePeriod> d2Periods = d2.getPeriods();
		for(int i=0; i<d1Periods.size(); i++)
		{
			p = d1Periods.get(i).compareTo(d2Periods.get(i));
			if(p != 0) return p;
		}
		return 0;
	}
}
