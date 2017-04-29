package com.nitika.hazards;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;

public class Hazards {
	
	//check for structural hazards
	public static boolean structural(int inst){
		String instType=Simulator.memory[inst][1];
		if(instType.matches(ApplicationConstants.ADDD) || instType.matches(ApplicationConstants.SUBD)){
			if(Simulator.nfpAdder>0){
				//no hazard
				return false;
			}
			else{
				Simulator.STRUCT[inst]="Y";
				return true;
			}
		}
		else if(instType.matches(ApplicationConstants.MULTD)){
			if(Simulator.nfpMult>0){
				//no hazard
				return false;
			}
			else{
				Simulator.STRUCT[inst]="Y";
				return true;
			}
		}
		else if(instType.matches(ApplicationConstants.DIVD)){
			if(Simulator.nfpDiv>0){
				//no hazard
				return false;
			}
			else{
				Simulator.STRUCT[inst]="Y";
				return true;
			}
		}
		else if((instType.matches(ApplicationConstants.LW)) || (instType.matches(ApplicationConstants.SW)) || (instType.matches(ApplicationConstants.LD)) || (instType.matches(ApplicationConstants.SD)) || (instType.matches(ApplicationConstants.HLT)) || (instType.matches(ApplicationConstants.BNE)) || (instType.matches(ApplicationConstants.BEQ)) || (instType.matches(ApplicationConstants.J))){
			//no hazard
			return false;
		}
		else{
			if(Simulator.nIntU>0){
				//no hazard
				return false;
			}
			else{
				Simulator.STRUCT[inst]="Y";
				return true;
			}
		}
	}
	
	//check for RAW hazards
	public static boolean RAW(int instruction){
		//if its the first instruction, then no RAW
		if(instruction==0){
			return false;
		}
		else
		{
			for(int i=0;i<instruction;i++){
			//some previous instruction is writing to this source register
				//this would again be instruction type wise
				//if both blank, then skip(so check it)
				if((Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][3])) || (Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][4]))){
					if(Simulator.execute[i]!=0){
						//previous one is active i.e. results have not been written yet
						//wait till it completes execution
						Simulator.RAW[instruction]="Y";
						return true;
					}
					else{
						//previous one has finished execution
						return false;
					}
				}
				else{
					//no previous instruction uses the same destination
					return false;
				}
			}
			return false;
		}
	}
	
	//check for WAW hazards(wait if previous active instruction uses the same dest as the current one)
	public static boolean WAW(int instruction){
		
		//if its the first instruction, then no WAW
		if(instruction==0){
			return false;
		}
		else
		{
			for(int i=0;i<instruction;i++){
			//some previous instruction is using this destination register
				//if((!Simulator.memory[i][1].matches(ApplicationConstants.SD)) || (!Simulator.memory[i][1].matches(ApplicationConstants.SW))){
					if(Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][2])){
						if(Simulator.execute[i]!=0){
							//previous one is active i.e. results have not been written yet
							//wait till it completes execution
							Simulator.WAW[instruction]="Y";
							return true;
						}
						else{
							//previous one has finished execution
							return false;
						}
					}
					else{
						//no previous instruction uses the same destination
						return false;
					}
				}
				//else
				//{
					//if(Simulator.memory[i][2])
				//}
			//}
			return false;
		}
	}
}
