package com.nitika.hazards;

import java.util.HashMap;
import java.util.Map;

import com.nitika.constants.ApplicationConstants;
import com.nitika.data.Registers;
import com.nitika.functionalUnit.Status;
import com.nitika.main.Simulator;
import com.nitika.pipeline.Stages;
import com.nitika.scoreboard.CalcScoreboard;

public class Branch {
	
	public static int inProgress[]=new int[Simulator.totalInst];
	public static Map<Integer,String[]> leftOver=new HashMap<Integer, String[]>();
	public static int found=0;
	public static Map<Integer, Integer> branchFetch=new HashMap<Integer, Integer>();
	/**
	 * Branchset is true when the branch condition comes out to be true
	 */
	public static boolean branchSet=false;
	public static int Jfound=0;
	//just check if its a branch instruction or not
	public static boolean branch(int instNo){
		if((Simulator.memory[instNo][1].equals(ApplicationConstants.BEQ)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.BNE))|| (Simulator.memory[instNo][1].equals(ApplicationConstants.J))){
			return true;
		}
		else{
			return false;
		}
	}
	
	//what to do if its a branching instruction
	public static int onBranch(int instNo){

		if(Simulator.memory[instNo][1].equals(ApplicationConstants.J)){
			//write to file till this point
			if(Simulator.fetch[instNo+1]!=0){
				//next instruction has already been fetched
				System.out.println("next instruction has already been fetched");
				//return 1;
			}
			else{
				return 1;
			}
			Simulator.issue[instNo]=Stages.branchIssue;
			Simulator.read[instNo]=-1;
			Simulator.execute[instNo]=-1;
			Simulator.write[instNo]=-1;
			for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
				if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
					//find the instruction associated with the label
					found=i;
					break;
				}
			}
			
			/**
			 * found here
			 */
			int k=0;
			for(int i=found;i<Simulator.totalInst;i++)
			{
				Simulator.memory[Simulator.totalInst+k]=Simulator.memory[found+k];
				branchFetch.put(Simulator.totalInst+k, found+k);
				k++;
			}
			Simulator.totalInst=Simulator.totalInst+k;
			branchSet=true;
			Status.functional();
			return 0;
		}
		else{
			//branch instructions such as BNE, BEQ
			if(Simulator.memory[instNo][1].matches(ApplicationConstants.BEQ) || Simulator.memory[instNo][1].matches(ApplicationConstants.BNE))
			{
				return 2;
			}
			else{
				return 3;
			}
		}
	}
	
	
	//************make sure to move the HLT instruction later if you are adding new instructions********************
	public static int onBEQBNE(int instNo){
		
		if(Simulator.memory[instNo][1].equals(ApplicationConstants.BEQ)){
			//branch condition is true and we need to jump to the label
			if(Registers.getValue(Simulator.memory[instNo][2])==Registers.getValue(Simulator.memory[instNo][3])){
				//the last fetched instruction after the branch is ignored here ****REMEMBER TO INCLUDE THAT AS WELL****
				if(Simulator.fetch[instNo+1]==0){
					//next instruction has not been fetched due to icache delay so we need to wait until it is fetched
					System.out.println("next instruction has not been fetched");
					return 1;
				}
				Simulator.read[instNo]=Stages.branchRead;
				for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
					if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
						//find the instruction associated with the label
						found=i;
						break;
					}
				}
				
				/**
				 * found here
				 */
				int k=0;
				for(int i=found;i<Simulator.totalInst;i++)
				{
					Simulator.memory[Simulator.totalInst+k]=Simulator.memory[found+k];
					branchFetch.put(Simulator.totalInst+k, found+k);
					k++;
				}
				Simulator.totalInst=Simulator.totalInst+k;
				branchSet=true;
				Status.functional();		
			
			}
			else
			{
				//values are equal, continue with the next instruction that has already been fetched
				return 2;
			}
		}
		else	
		{
			if(Registers.getValue(Simulator.memory[instNo][2])!=Registers.getValue(Simulator.memory[instNo][3])){
				//the last fetched instruction after the branch is ignored here ****REMEMBER TO INCLUDE THAT AS WELL****
				if(Simulator.fetch[instNo+1]==0){
					//next instruction has not been fetched due to icache delay so we need to wait until it is fetched
					System.out.println("next instruction has not been fetched");
					return 1;
				}
				Simulator.read[instNo]=Stages.branchRead;
				for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
					if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
						//find the instruction associated with the label
						found=i;
						break;
					}
				}
				
				/**
				 * found here
				 */
				int k=0;
				for(int i=found;i<Simulator.totalInst;i++)
				{
					Simulator.memory[Simulator.totalInst+k]=Simulator.memory[found+k];
					branchFetch.put(Simulator.totalInst+k, found+k);
					k++;
				}
				Simulator.totalInst=Simulator.totalInst+k;
				branchSet=true;
				Status.functional();
				
			}
			else
			{
				//values are equal, continue with the next instruction that has already been fetched
				return 2;
			}
		}
		return 3;
	}
	
}
