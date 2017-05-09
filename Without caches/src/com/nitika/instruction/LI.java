package com.nitika.instruction;

import com.nitika.main.Simulator;

public class LI {

	public static void result(int instNo){
		//load the data into the register
		Simulator.registers.put(Simulator.memory[instNo][2],Integer.parseInt(Simulator.memory[instNo][3]));
	}
}
