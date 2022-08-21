package com.UE.cc.util;

import java.io.Serializable;
import java.lang.reflect.Array;

public class CCCommand<E> implements Serializable
{
	private static final long serialVersionUID = 614928040366197351L;
	private String command;
	private E[] param;
	
	public CCCommand(String c, E[] e)
	{
		this.command = c;
		this.param = e;
	}
	
	public CCCommand(String c) {
		this(c,(E[]) null);
	}
	
	public CCCommand(String c, E e)
	{
		this.command = c;
		@SuppressWarnings("unchecked")
		E[] array = ((E[]) Array.newInstance(e.getClass(),1));
		array[0] = e;
		this.param = array;
	}

	public String getCommand() {
		return command;
	}
	
	public E getParam(int index) {
		return param[index];
	}
	
	@Override
	public String toString() {
		return command;
	}
}
