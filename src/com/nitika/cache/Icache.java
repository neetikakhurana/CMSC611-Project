package com.nitika.cache;

import com.nitika.hazards.Branch;
import com.nitika.main.Simulator;

public class Icache {

	public static int iCacheHit=0;
	public static int iCacheMiss=0;
	public static int accessRequests=0;
	public static int HitRate=1;
	public static int MMaccessTime=3;
	public static boolean MMinUse=false;
	public static int delay=-1;
	
	public static int iCache[][]=new int[Simulator.nbIcache][Simulator.bsizeIcache];
	/**
	 * I cache to fetch instructions
	 * @param instNo
	 * @return 0 if its a cache hit, 2 if we need to wait, 3 if wait time for MM is over
	 */
	public static int instInIcache(int instNo){
	
		accessRequests++;
		if(instNo==0){
			for(int i=0;i<Simulator.nbIcache;i++)
			{
				for(int j=0;j<Simulator.bsizeIcache;j++)
				{
					iCache[i][j]=-1;
				}
			}
		}
		int blockNo,blockOffset;
		if(instNo>(Simulator.bsizeIcache*Simulator.nbIcache-1))
		{
			blockNo=(instNo-(Simulator.bsizeIcache*Simulator.nbIcache))/(Simulator.nbIcache);
			blockOffset=(instNo-(Simulator.bsizeIcache*Simulator.nbIcache))%Simulator.bsizeIcache;
		}
		else
		{
			blockNo=instNo/(Simulator.nbIcache);
			blockOffset=instNo%Simulator.bsizeIcache;
		}
		if(delay==-1 && instNo==0)
		{
			delay=Simulator.bsizeIcache*MMaccessTime;
		}
		else if(delay==-1 && instNo!=0)
		{
			delay=Simulator.bsizeIcache*MMaccessTime+1;
		}
		
		//if the instruction is available in cache and update cache hit counter iCacheHit++
		if( instNo<(Simulator.bsizeIcache * Simulator.nbIcache) && iCache[blockNo][blockOffset]==instNo)
		{
			iCacheHit++;
			return 0;
		}
		if(instNo>(Simulator.bsizeIcache * Simulator.nbIcache-1))
		{
			if(Branch.branchFetch.size()>0){
				if(Branch.branchFetch.containsKey(instNo)){
					blockNo=Branch.branchFetch.get(instNo)/(Simulator.nbIcache);
					blockOffset=(Branch.branchFetch.get(instNo))%Simulator.bsizeIcache;
					if(Branch.branchFetch.get(instNo)>(Simulator.bsizeIcache*Simulator.nbIcache-1)){
						blockNo=(Branch.branchFetch.get(instNo)-(Simulator.bsizeIcache*Simulator.nbIcache))/(Simulator.nbIcache);
						blockOffset=(Branch.branchFetch.get(instNo)-(Simulator.bsizeIcache*Simulator.nbIcache))%Simulator.bsizeIcache;
					}
					if(iCache[blockNo][blockOffset]==Branch.branchFetch.get(instNo)){
						iCacheHit++;
						return 0;
					}
				}
			}
			else if(iCache[blockNo][blockOffset]==instNo)
			{
				iCacheHit++;
				return 0;
			}
			else
			{
				//else, wait till it is brought from the main memory and keep updating clock cycles Simulator.cycle++ in loop till then
				//calculate cache miss penalty iCacheMiss++ (only once)
				//and return the cycle number
				
				delay--;
				if(delay==-1)
				{
					int i=0;
					//instructions after the current one
					for(i=blockOffset;i<Simulator.bsizeIcache;i++){
						if(instNo+i<Simulator.totalInst)
						{
							iCache[blockNo][blockOffset+i]=instNo+i;
						}
						else
						{
							iCache[blockNo][blockOffset+i]=-1;
						}
					}
					//instructions before the current one
					for(int j=1;j<=blockOffset;j++){
						if(instNo-j<Simulator.totalInst)
						{
							iCache[blockNo][blockOffset-j]=instNo-j;
						}
						else
						{
							iCache[blockNo][blockOffset-j]=-1;
						}
						
					}
					MMinUse=false;
					return 1;
				}
				else
				{
					iCacheMiss++;
					MMinUse=true;
					return 2;
				}
			}
		}
	
		else
		{
			//else, wait till it is brought from the main memory and keep updating clock cycles Simulator.cycle++ in loop till then
			//calculate cache miss penalty iCacheMiss++ (only once)
			//and return the cycle number
			
			delay--;
			if(delay==-1)
			{
				int i=0;
				//instructions after the current one
				for(i=blockOffset;i<Simulator.bsizeIcache;i++){
					if(instNo+i<Simulator.totalInst)
					{
						iCache[blockNo][blockOffset+i]=instNo+i;
					}
					else
					{
						iCache[blockNo][blockOffset+i]=-1;
					}
				}
				//instructions before the current one
				for(int j=1;j<=blockOffset;j++){
					if(instNo-j<Simulator.totalInst)
					{
						iCache[blockNo][blockOffset-j]=instNo-j;
					}
					else
					{
						iCache[blockNo][blockOffset-j]=-1;
					}
					
				}
				MMinUse=false;
				return 1;
			}
			else
			{
				iCacheMiss++;
				MMinUse=true;
				return 2;
			}
		}
		return 0;
	}
}
