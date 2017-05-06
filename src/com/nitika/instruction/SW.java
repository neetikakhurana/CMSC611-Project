package com.nitika.instruction;

import com.nitika.main.Simulator;
import com.nitika.parsers.InstParser;

public class SW {

	public static void result(int instNo){
		//get data for source1 and source2 and add them
		//Integer.parseInt(InstParser.getSource1(instNo)+InstParser.getSource2(instNo)
		String source=InstParser.getStoreDest(instNo); //dest register
		//value to be stored 
		String dest=InstParser.getStoreSource(instNo);
		String indexedAdd[]=new String[2];
		indexedAdd=dest.split("\\(");
		String destReg[]=indexedAdd[1].split("\\)");
		int loc=Integer.parseInt(indexedAdd[0])+Simulator.registers.get(destReg[0]);
		int val=Simulator.registers.get(source);
		Simulator.memoryData.put(loc,val);
		//Simulator.registers.put(dest, val);
	}
}
