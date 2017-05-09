package com.nitika.instruction;

import com.nitika.main.Simulator;
import com.nitika.parsers.InstParser;

public class LW {

	public static void result(int instNo){
		//get data for source1 and source2 and add them
		//Integer.parseInt(InstParser.getSource1(instNo)+InstParser.getSource2(instNo)
		String dest=Simulator.memory[instNo][2]; //dest register
		//value to be stored 
		String source=InstParser.getLDSource(instNo);
		String indexedAdd[]=new String[2];
		indexedAdd=Simulator.memory[instNo][3].split("\\(");
		int loc=Integer.parseInt(indexedAdd[0])+Simulator.registers.get(source);
		int val=Simulator.memoryData.get(loc);
		Simulator.registers.put(dest, val);
	}
}
