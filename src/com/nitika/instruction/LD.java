package com.nitika.instruction;

import com.nitika.cache.Dcache;
import com.nitika.cache.Icache;
import com.nitika.main.Simulator;
import com.nitika.parsers.InstParser;

public class LD {

	public static void result(int instNo){
		/**
		 * first check if the data is available in the cache or not
		 */
		
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
