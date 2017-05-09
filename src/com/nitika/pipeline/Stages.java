package com.nitika.pipeline;

import com.nitika.cache.Icache;
import com.nitika.constants.ApplicationConstants;
import com.nitika.functionalUnit.Available;
import com.nitika.hazards.Branch;
import com.nitika.hazards.Hazards;
import com.nitika.instruction.ADDD;
import com.nitika.instruction.AND;
import com.nitika.instruction.ANDI;
import com.nitika.instruction.DADD;
import com.nitika.instruction.DADDI;
import com.nitika.instruction.DIVD;
import com.nitika.instruction.DSUB;
import com.nitika.instruction.DSUBI;
import com.nitika.instruction.LD;
import com.nitika.instruction.LI;
import com.nitika.instruction.LUI;
import com.nitika.instruction.LW;
import com.nitika.instruction.MULD;
import com.nitika.instruction.OR;
import com.nitika.instruction.ORI;
import com.nitika.instruction.SD;
import com.nitika.instruction.SUBD;
import com.nitika.instruction.SW;
import com.nitika.main.Simulator;
import com.nitika.scoreboard.CalcScoreboard;

public class Stages {

	public static int addDelay=Simulator.fpAddEx;
	public static int subDelay=Simulator.fpAddEx;
	public static int mulDelay=Simulator.fpMulEx;
	public static int divDelay=Simulator.fpDivEx;
	public static int loadDelay=2;
	public static int storeDelay=2;
	/**
	 * Branching is going to happen, then goingToBranch will be set
	 */
	public static boolean goingToBranch=false;
	public static int writeIncomplete=0;	//last incomplete instruction
	public static int writeComplete=0;		//last complete instruction
	
	public static int branchRead=0;			//set when branch read is done but it has to wait since icache fetch is in progress for the next one
	//operations in fetch stage
	public static void fetchStage(int instNo){
		
		int cycleNo = Icache.instInIcache(instNo);
		if(cycleNo==0)
		{
			/**
			 * cache hit
			 */
			if(instNo==0){
				Simulator.fetch[instNo]=CalcScoreboard.fetchControl;
				if(!CalcScoreboard.Allfetch.contains(instNo))
					CalcScoreboard.Allfetch.add(instNo);
			}
			else{
				if(Simulator.issue[instNo-1]!=0){
					Simulator.fetch[instNo]=CalcScoreboard.fetchControl;
					if(!CalcScoreboard.Allfetch.contains(instNo))
						CalcScoreboard.Allfetch.add(instNo);

				}
			}
		}
		else if(cycleNo==1){
		//if it was available in the cache, then use the previous stage's 
			/**
			 * if it is a cache hit, then only the fetch will be dependent upon the previous instr's issue stage
			 * else it will keep on executing independently
			 * 
			 * Cache miss solved
			**/
			if(instNo!=0 && Simulator.issue[instNo-1]!=0 && Simulator.issue[instNo-1]<CalcScoreboard.fetchControl){
				Simulator.fetch[instNo]=CalcScoreboard.fetchControl;
				if(!CalcScoreboard.Allfetch.contains(instNo))
					CalcScoreboard.Allfetch.add(instNo);
			}
			else if(instNo==0){
				Simulator.fetch[instNo]=CalcScoreboard.fetchControl;
				if(!CalcScoreboard.Allfetch.contains(instNo))
					CalcScoreboard.Allfetch.add(instNo);
			}
		}
		else{
			//do nothing coz we need to wait it out
			/**
			 * Cache miss
			 * fetching from MM in progress
			 */
		}
	}
	
	//operations in issue stage
	public static void issueStage(int instNo){
		
		//Allocate functional unit based on instrType
		//increment the cycle number if there are no hazards
		if(!Hazards.structural(instNo) && !Hazards.WAW(instNo) && !Branch.branch(instNo)){
			Simulator.issue[instNo]=CalcScoreboard.fetchControl;
			Available.resourceAllocate(instNo);
		}
		else if (Branch.branch(instNo)){
			int result=Branch.onBranch(instNo);
			if(result==0){
				//J instruction, next instruction has been fetched so now update it since we have flushed the instruction
				Simulator.issue[instNo]=CalcScoreboard.fetchControl;
			}
			else if(result==1)
			{
				//J with next instruction not fetched; wait for it to be fetched
				//Simulator.issue[instNo]=CalcScoreboard.fetchControl;
			}
			else if(result==2 || result==3)
			{
				//BEQ or BNE or normal instruction
				Simulator.issue[instNo]=CalcScoreboard.fetchControl;
				goingToBranch=true;
			}
			
		}
		
	}
	
	//operations in read stage
	public static void readStage(int instNo){
				
		//uses a register file
		//check for RAW
		if(Branch.onBranch(instNo)==2){
			//branch instruction has been so stall the later instructions
			CalcScoreboard.branchInProgress=true;
		}
		
		if(!Hazards.RAW(instNo)){
		//increment the cycle number if there are no hazards
			if(Branch.onBranch(instNo)==3){
				//no branching
				Simulator.read[instNo]=CalcScoreboard.fetchControl;
			}
			else if(Branch.onBranch(instNo)==2){
				//BEQ or BNE (and they should not go beyond this point)
				if(branchRead==0){
					/**
					 * coz of the previous instruction if not fetched coz of the delay, it will come to this stage again and again for branch instr and hence we need to control the read counter for it
					 */
					branchRead=CalcScoreboard.fetchControl;
				}
				Simulator.execute[instNo]=-1;
				Simulator.write[instNo]=-1;
				CalcScoreboard.branchInProgress=false;
				int result=Branch.onBEQBNE(instNo);
				if(result==2){
					
					//if it return true, then that means let the next instruction continue
					//we need not to do flushing (or rather anything at all)
					goingToBranch=false;
					Simulator.read[instNo]=branchRead;
					branchRead=0;
				}
				else if(result==3)
				{
					//branch turned out to be true. Going to label
					System.out.println("The condition turned out to be true..Going to new label");
					branchRead=0;
					goingToBranch=false;
				}
				else{
					/**
					 * next instruction has not been fetched and wait for it to be fetched
					 */
				}
			}
		}
	}
	
	//operations in execute stage
	public static void executeStage(int instNo){
		
		//uses Dcache
		//increment the cycle number if there are no hazards
		int result=performExecution(instNo);
		if(result==0){
			//do nothing just wait it out
		}
		else{
			Simulator.execute[instNo]=CalcScoreboard.fetchControl;
			CalcScoreboard.writeResultToFile(instNo, instNo);
		}
 	}
	
	//operations in write stage
	public static void writeStage(int instNo){
		
		//uses a register file
		//increment the cycle number if there are no hazards
		Simulator.cycle++;
		
		Simulator.write[instNo]=CalcScoreboard.fetchControl;
		int i,r=0;
		for(i=1;i<instNo;i++){
			if(Simulator.write[i]==0){
				r=1;
				break;
			}
		}
		
		//************************check if thsi works on adding new instr on branch********************coz branch and halt will never complete******************8
		
		//if there is an incomplete instruction in between
		if(instNo!=0 && r!=0)
			writeIncomplete=i;
		//if there is no intermediate incomplete instruction
		else if(instNo!=0 && r==0){
			writeIncomplete=i+1;
		}
		writeComplete=instNo;
		
		//release after writing the result
		Available.resourceReleased(instNo);
	}
	
	public static int performExecution(int instNo){
		//perform the task
		//take into account the delay of functional unit
		if((Simulator.memory[instNo][1].matches(ApplicationConstants.ADDD))){
			if(Available.ArrayUnits[instNo].getLatency()!=0){
				Available.ArrayUnits[instNo].setLatency(Available.ArrayUnits[instNo].getLatency()-1);
				if(Available.ArrayUnits[instNo].getLatency()==0){
					ADDD.result(instNo);
					return 1;
				}
				return 0;
			}
			else{
				ADDD.result(instNo);
				return 1;
			}
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.SUBD))){
			//move this result into destination register
			if(subDelay!=0){
				subDelay--;
				if(subDelay==0){
					SUBD.result(instNo);
					subDelay=Simulator.fpAddEx;
					return 1;
				}
				return 0;
			}
			else{
				SUBD.result(instNo);
				return 1;
			}
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.MULTD))){
			//move this result into destination register
			if(Available.ArrayUnits[instNo].getLatency()!=0){
				Available.ArrayUnits[instNo].setLatency(Available.ArrayUnits[instNo].getLatency()-1);
				if(Available.ArrayUnits[instNo].getLatency()==0){
					MULD.result(instNo);
					return 1;
				}
				return 0;
			}
			else{
				MULD.result(instNo);
				return 1;
			}
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DIVD))){
			//move this result into destination register
			if(Available.ArrayUnits[instNo].getLatency()!=0){
				Available.ArrayUnits[instNo].setLatency(Available.ArrayUnits[instNo].getLatency()-1);
				if(Available.ArrayUnits[instNo].getLatency()==0){
					DIVD.result(instNo);
					return 1;
				}
				return 0;
			}
			else{
				DIVD.result(instNo);
				return 1;
			}
		}
		//1 delay for all instructions using integer unit
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DADDI))){
			//move this result into destination register
			DADDI.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.LD))){
			//move this result into destination register
			if(loadDelay!=0){
				loadDelay--;
				if(loadDelay==0){
					LD.result(instNo);
					loadDelay=2;
					return 1;
				}
				return 0;
			}
			else{
				LD.result(instNo);
				loadDelay=2;
				return 1;
			}
				
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.SD))){
			//move this result into destination register
			if(storeDelay!=0){
				storeDelay--;
				if(storeDelay==0){
					SD.result(instNo);
					storeDelay=2;
					return 1;
				}
				return 0;
			}
			else{
				SD.result(instNo);
				storeDelay=2;
				return 1;
			}
				
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DADD))){
			//move this result into destination register
			DADD.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DSUBI))){
			//move this result into destination register
			DSUBI.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.DSUB))){
			//move this result into destination register
			DSUB.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.LW))){
			//move this result into destination register
			LW.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.SW))){
			//move this result into destination register
			SW.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.AND))){
			//move this result into destination register
			AND.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.ANDI))){
			//move this result into destination register
			ANDI.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.ORI))){
			//move this result into destination register
			ORI.result(instNo);
			return 1;
		}
		else if((Simulator.memory[instNo][1].matches(ApplicationConstants.OR))){
			//move this result into destination register
			OR.result(instNo);
			return 1;
		}
		else if ((Simulator.memory[instNo][1].contains(ApplicationConstants.LI))){
			LI.result(instNo);
			return 1;
		}
		else{
			LUI.result(instNo);
			return 1;
		}
	}
	
	//check if all instructions have completed the write stage
	public static boolean allCompletedWrite(){
		for(int i=writeIncomplete;i<Simulator.totalInst;i++){
			if(Simulator.write[i]==0){
				return false;
			}
			else{
				continue;
			}
		}
		return true;
	}
}
