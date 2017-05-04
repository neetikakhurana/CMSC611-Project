package com.nitika.cache;

import com.nitika.main.Simulator;

public class Icache {

	public static int iCacheHit=0;
	public static int iCacheMiss=0;
	public static int HitRate=1;
	public static int MMaccessTime=3;
	public static int delay=0;
	public static int iCache[][]=new int[Simulator.nbIcache][Simulator.bsizeIcache];
	public static int instInIcache(int instNo){
		
		if(instNo==0){
			for(int i=0;i<Simulator.nbIcache;i++){
				for(int j=0;j<Simulator.bsizeIcache;j++){
					iCache[i][j]=-1;
				}
			}
		}
		int blockNo=instNo/(Simulator.nbIcache);
		int blockOffset=instNo%Simulator.bsizeIcache;
		if(delay==0){
			delay=(Simulator.bsizeIcache*MMaccessTime);
		}
		//if the instruction is available in cache and update cache hit counter iCacheHit++
		if(iCache[blockNo][blockOffset]==instNo){
			iCacheHit++;
			return 0;
		}
		else{
			//else, wait till it is brought from the main memory and keep updating clock cycles Simulator.cycle++ in loop till then
			//calculate cache miss penalty iCacheMiss++ (only once)
			//and return the cycle number
			iCacheMiss++;
			delay--;
			if(delay==0){
				iCache[blockNo][blockOffset]=instNo;
				iCache[blockNo][blockOffset+1]=instNo+1;
				iCache[blockNo][blockOffset+2]=instNo+2;
				iCache[blockNo][blockOffset+3]=instNo+3;
				return 1;
			}
			/*iCache[blockNo][blockOffset]=instNo;
			iCache[blockNo][blockOffset+1]=instNo+1;
			iCache[blockNo][blockOffset+2]=instNo+2;
			iCache[blockNo][blockOffset+3]=instNo+3;
			Simulator.cycle+=(Simulator.bsizeIcache*MMaccessTime);*/
			else{
				return 2;
			}
		}
	}
}
