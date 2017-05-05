package com.nitika.data;

import com.nitika.main.Simulator;

public class Registers {

	//get the value inside the register
	public static int getValue(String reg){
		return Simulator.registers.get(reg);
	}
	
	public static void setValue(String reg, int value){
		Simulator.registers.put(reg,value);
	}
}
