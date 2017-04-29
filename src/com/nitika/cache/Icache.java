package com.nitika.cache;

public class Icache {

	public static int iCacheHit=0;
	public static int iCacheMiss=0;
	public static int instInIcache(int instNo){
		//if the instruction is available in cache and update cache hit counter iCacheHit++
		return 0;
		//else, wait till it is brought from the main memory and keep updating clock cycles Simulator.cycle++ in loop till then
		//calculate cache miss penalty iCacheMiss++ (only once)
		//and return the cycle number
	}
}
