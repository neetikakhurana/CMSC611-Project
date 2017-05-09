package com.nitika.cache;

import java.util.HashMap;
import java.util.Map;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.parsers.InstParser;

public class Dcache {

	public static int dataAccessRequests=0;
	public static int dataHits=0;
	public static int dataMiss=0;
	public static Map<String, Integer> registerCache=new HashMap<String, Integer>();
	public static int dCache[][]=new int[4][4]; //size of cache given
	/*
	 * Will only be called if the isntructions are LD, SD , SW, or LW
	 */
	public static int dataInDcache(int instNo){
		dataAccessRequests++;
		if(instNo==0){
			for(int i=0;i<4;i++){
				for(int j=0;j<4;j++){
					dCache[i][j]=-1;
				}
			}
		}
		
		//if the isntruction is of type load
		if(Simulator.memory[instNo][1].contains(ApplicationConstants.LD) || Simulator.memory[instNo][1].equalsIgnoreCase(ApplicationConstants.LW))
		{
			if((registerCache.containsKey(InstParser.getLDSource(instNo))) || (registerCache.containsKey(Simulator.memory[instNo][2]))){
				//data hit for atleast one register
				dataHits++;
				if((registerCache.containsKey(InstParser.getLDSource(instNo))) || (registerCache.containsKey(Simulator.memory[instNo][2]))){
					dataHits++;
					//if both source and destination exists in the cache
				}
				
			}
			
		}
		else if(Simulator.memory[instNo][1].contains(ApplicationConstants.SD) || Simulator.memory[instNo][1].contains(ApplicationConstants.SW))
		{
			
		}
		return 0;
	}
}
