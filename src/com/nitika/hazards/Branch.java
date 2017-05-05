package com.nitika.hazards;

import java.security.AllPermission;

import com.nitika.constants.ApplicationConstants;
import com.nitika.data.Registers;
import com.nitika.main.Simulator;
import com.nitika.pipeline.Stages;
import com.nitika.scoreboard.CalcScoreboard;

public class Branch {
	
	public static int found=0;

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
			//next instruction has already been fetched
			if(Simulator.fetch[instNo+1]!=0){
				//flush the fetched instruction
				//Simulator.fetch[instNo+1]=0;
				//go to the label specified
				return 0;
			}
			else{
				//simply go to the label instruction
				return 1;
			}
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
	public static boolean onBEQBNE(int instNo){
		
		if(Simulator.memory[instNo][1].equals(ApplicationConstants.BEQ)){
			//branch condition is true and we need to jump to the label
			if(Registers.getValue(Simulator.memory[instNo][2])==Registers.getValue(Simulator.memory[instNo][3])){
				//the last fetched instruction after the branch is ignored here ****REMEMBER TO INCLUDE THAT AS WELL****
				CalcScoreboard.writeResultToFile(found,instNo+1);
				for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
					if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
						//find the instruction associated with the label
						found=i;
						break;
					}
				}
				if(Simulator.fetch[instNo+1]!=0){
					//next instruction has already been fetched
					System.out.println("next instruction has already been fetched");
				}
				
				//**************************correct till here**********************************************
				int k=instNo+2;
				for(int j=found;j<Simulator.totalInst;j++){
					//load all instructions into the memory array
					Simulator.memory[k]=Simulator.memory[j];
					k++;
				}
				
				
				//new instr added to memory********************
				
			
			}
			else
			{
				//values are equal, continue with the next instruction that has already been fetched
				return true;
			}
		}
		else	
		{
			if(Registers.getValue(Simulator.memory[instNo][2])!=Registers.getValue(Simulator.memory[instNo][3])){
				//the last fetched instruction after the branch is ignored here ****REMEMBER TO INCLUDE THAT AS WELL****
				CalcScoreboard.writeResultToFile(found,instNo+1);
				for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
					if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
						//find the instruction associated with the label
						found=i;
						break;
					}
				}
				
				if(Simulator.fetch[instNo+1]!=0){
					//next instruction has already been fetched
					System.out.println("next instruction has already been fetched");
				}
				
				//**************************correct till here**********************************************
				//int k=0;
				for(int j=found;j<Simulator.totalInst;j++){
					//load all instructions into the memory array
//					Simulator.memory[k]=Simulator.memory[j];
//					k++;
					Simulator.fetch[j]=0;
					Simulator.issue[j]=0;
					Simulator.read[j]=0;
					Simulator.execute[j]=0;
					Simulator.write[j]=0;
				}
				
				Stages.writeIncomplete=found;
				//new instr added to memory********************
				
				
				//now, the writeincomplete should also be updated so that it now starts from the next instruction fed into the memory?????
				
				
				
				//values are unequal so we need to go to the label (use Allfetch)
				
			}
			else
			{
				//values are equal, continue with the next instruction that has already been fetched
				return true;
			}
		}
		return false;
	}
	
}
