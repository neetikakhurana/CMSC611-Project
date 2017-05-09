package com.nitika.instruction;

import com.nitika.data.Registers;
import com.nitika.main.Simulator;

public class DSUBI {

	public static void result(int instNo){
		//get data for source1 and source2 and add them
		int res=Registers.getValue(Simulator.memory[instNo][3])-(Integer.parseInt(Simulator.memory[instNo][4]));
		Registers.setValue(Simulator.memory[instNo][2], res);
	}
}
