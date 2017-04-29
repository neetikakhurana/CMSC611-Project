package com.nitika.pipeline;

import com.nitika.cache.Icache;
import com.nitika.constants.ApplicationConstants;
import com.nitika.functionalUnit.Available;
import com.nitika.hazards.Hazards;
import com.nitika.instruction.ADDD;
import com.nitika.main.Simulator;

public class Stages {

	//operations in fetch stage
	public static void fetchStage(int instNo){
		
		//if the instr is not available in cache, cycleNo returned will be zero
		int cycleNo = Icache.instInIcache(instNo);
			
	//	}
		//increment the cycle number if the instruction is available in cache
			Simulator.cycle=cycleNo+1;
			Simulator.fetch[instNo]=Simulator.cycle;
			//Available.resourceOccupied(instNo);
		
	}
	
	//operations in issue stage
	public static void issueStage(int instNo){
		
		//Allocate functional unit based on instrType
		//increment the cycle number if there are no hazards
		if(!Hazards.structural(instNo) && !Hazards.WAW(instNo)){
			Simulator.cycle++;
			Simulator.issue[instNo]=Simulator.cycle;
			Available.resourceAllocate(instNo);
		}
		/*else
		{
			Simulator.cycle++;
			issueStage(instNo);
		}*/
	}
	
	//operations in read stage
	public static void readStage(int instNo){
				
		//uses a register file
		//check for RAW
		if(!Hazards.RAW(instNo)){
		//increment the cycle number if there are no hazards
			Simulator.cycle++;
			Simulator.read[instNo]=Simulator.cycle;
		}
	}
	
	//operations in execute stage
	public static void executeStage(int instNo){
		
		//uses Dcache
		//increment the cycle number if there are no hazards
		Simulator.cycle++;
		Simulator.execute[instNo]=Simulator.cycle;
	}
	
	//operations in write stage
	public static void writeStage(int instNo){
		
		//uses a register file
		//increment the cycle number if there are no hazards
		Simulator.cycle++;
		Simulator.write[instNo]=Simulator.cycle;
		
		
		//release after writing the result
		Available.resourceReleased(instNo);
	}
	
	public static void performExecution(int instNo){
		//perform the task
		//take into account the delay of fucntional unit
		if((Simulator.memory[instNo][1].matches(ApplicationConstants.ADDD))){
			ADDD.result(instNo);
			//move this result into destination register
			int i=Simulator.fpAddEx;
			while(i==0){
				Simulator.cycle++;
				i--;
			}
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.SUBD))){
			//ADDD.result(instNo);
			//move this result into destination register
			int i=Simulator.fpAddEx;
			while(i==0){
				Simulator.cycle++;
				i--;
			}
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.MULTD))){
			//ADDD.result(instNo);
			//move this result into destination register
			int i=Simulator.fpMulEx;
			while(i==0){
				Simulator.cycle++;
				i--;
			}
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DIVD))){
			//ADDD.result(instNo);
			//move this result into destination register
			int i=Simulator.fpDivEx;
			while(i==0){
				Simulator.cycle++;
				i--;
			}
		}
		//1 delay for all instructions using integer unit
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DADDI))){
			//ADDD.result(instNo);
			//move this result into destination register
				Simulator.cycle++;
				
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.LI))){
			//ADDD.result(instNo);
			//move this result into destination register
				Simulator.cycle++;
				
		}
	}
}
