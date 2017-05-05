package com.nitika.instruction;

import com.nitika.data.Registers;
import com.nitika.main.Simulator;

public class DADD {

	public static void result(int instNo){
		//get data for source1 and source2 and add them
		//Integer.parseInt(InstParser.getSource1(instNo)+InstParser.getSource2(instNo)
		int res=Registers.getValue(Simulator.memory[instNo][3])+(Registers.getValue(Simulator.memory[instNo][4]));
		Registers.setValue(Simulator.memory[instNo][2], res);
	}
}
