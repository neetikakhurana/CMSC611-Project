package com.nitika.hazards;

import java.security.AllPermission;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.scoreboard.CalcScoreboard;

public class Branch {

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
	
	public static boolean onBEQBNE(int instNo){
		if(Simulator.memory[instNo][1].equals(ApplicationConstants.BEQ)){
			if(Simulator.memory[instNo][2].equals(Simulator.memory[instNo][3])){
				int found=0;
				for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
					if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
						//find the instruction associated with the label
						Simulator.memory[instNo+1]=Simulator.memory[i];
						//Simulator.totalInst++;
						found=i;
						break;
					}
				}
				int k=instNo+2;
				for(int j=found+1;j<CalcScoreboard.Allfetch.size();j++){
					//add all instructions after the label into the memory array for in order to be executed
					Simulator.memory[k]=Simulator.memory[j];
					k++;
				}
			}
		}
		else	
		{
			if(!Simulator.memory[instNo][2].equals(Simulator.memory[instNo][3])){
				int found=0;
				for(int i=0;i<CalcScoreboard.Allfetch.size();i++){
					if(Simulator.memory[i][0].matches(Simulator.memory[instNo][4])){
						//find the instruction associated with the label
						Simulator.memory[instNo+1]=Simulator.memory[i];
						//Simulator.totalInst++;
						found=i;
						break;
					}
				}
				int k=instNo+2;
				for(int j=found+1;j<CalcScoreboard.Allfetch.size();j++){
					//add all instructions after the label into the memory array for in order to be executed
					Simulator.memory[k]=Simulator.memory[j];
					k++;
				}
				//values are unequal so we need to go to the label (use Allfetch)
				/*if(Simulator.fetch[instNo+1]!=0 && Simulator.fetch[instNo+2]!=0){
					//flush these two instructions
					return true;
				}*/
			}
			else
			{
				//values are equal, continue with the next instruction
				return false;
			}
		}
		return false;
	}
	
}
