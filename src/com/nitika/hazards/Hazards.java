package com.nitika.hazards;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.pipeline.Stages;

public class Hazards {
	public static int loads=1;
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
		else if((instType.matches(ApplicationConstants.HLT)) || (instType.matches(ApplicationConstants.BNE)) || (instType.matches(ApplicationConstants.BEQ)) || (instType.matches(ApplicationConstants.J))){
			//no hazard
			return false;
		}
		else if((instType.matches(ApplicationConstants.LW)) || (instType.matches(ApplicationConstants.SW)) || (instType.matches(ApplicationConstants.LD)) || (instType.matches(ApplicationConstants.SD))){
			if(loads>0){
				//no hazard
				return false;
			}
			else{
				Simulator.STRUCT[inst]="Y";
				return true;
			}
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
			if(Stages.writeIncomplete==0){
				if((Simulator.memory[Stages.writeIncomplete][2].equalsIgnoreCase(Simulator.memory[instruction][3])) || (Simulator.memory[Stages.writeIncomplete][2].equalsIgnoreCase(Simulator.memory[instruction][4]))){
					Simulator.RAW[instruction]="Y";
					return true;
				}else{
					//no previous instruction uses the same destination
					return false;
				}
			}
			for(int i=Stages.writeIncomplete;i<instruction;i++){
			//some previous instruction is writing to this source register
				//this would again be instruction type wise
				//if both blank, then skip(so check it)
				if(Simulator.write[i]==0){
					if((!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BEQ)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BNE)) ){ 
						if((Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][3])) || (Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][4]))){
							Simulator.RAW[instruction]="Y";
							return true;
						}else{
							//no previous instruction uses the same destination
							continue;
						}
					}
					else{
						if((Simulator.registers.get(Simulator.memory[i][2])==(Simulator.registers.get(Simulator.memory[instruction][2]))) || (Simulator.registers.get(Simulator.memory[i][2])==(Simulator.registers.get(Simulator.memory[instruction][3])))){
							Simulator.RAW[instruction]="Y";
							return true;
						}else{
							//no previous instruction uses the same destination
							continue;
						}
					}
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
			if(Stages.writeIncomplete==0){
				if((Simulator.memory[Stages.writeIncomplete][2].equalsIgnoreCase(Simulator.memory[instruction][2]))){
					Simulator.WAW[instruction]="Y";
					return true;
				}else{
					//no previous instruction uses the same destination
					return false;
				}
			}
			for(int i=Stages.writeIncomplete;i<instruction;i++){
			//some previous instruction is writing to this source register
				//this would again be instruction type wise
				//if both blank, then skip(so check it)
				if(Simulator.write[i]==0){
						//this should be the case for all instructions except branching ones
					if((!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BEQ)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BNE)) ){ 
						if(Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][2])){
							Simulator.WAW[instruction]="Y";
							return true;
						}else{
							//no previous instruction uses the same destination
							continue;
						}
					}
					else{
						return false;
					}
				}
			}
			return false;
			
		}
	}
}
