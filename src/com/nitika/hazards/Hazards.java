package com.nitika.hazards;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;

public class Hazards {
	
	//check for structural hazards
	public static void structural(int inst){
		String instType=Simulator.memory[inst][1];
		if(instType.matches(ApplicationConstants.ADDD) || instType.matches(ApplicationConstants.SUBD)){
			if(Simulator.nfpAdder>0){
				//no hazard
			}
			else{
				Simulator.STRUCT[inst]="Y";
			}
		}
		else if(instType.matches(ApplicationConstants.MULTD)){
			if(Simulator.nfpMult>0){
				//no hazard
			}
			else{
				Simulator.STRUCT[inst]="Y";
			}
		}
		else if(instType.matches(ApplicationConstants.DIVD)){
			if(Simulator.nfpDiv>0){
				//no hazard
			}
			else{
				Simulator.STRUCT[inst]="Y";
			}
		}
		else if((instType.matches(ApplicationConstants.LW)) || (instType.matches(ApplicationConstants.SW)) || (instType.matches(ApplicationConstants.LD)) || (instType.matches(ApplicationConstants.SD)) || (instType.matches(ApplicationConstants.HLT)) || (instType.matches(ApplicationConstants.BNE)) || (instType.matches(ApplicationConstants.BEQ)) || (instType.matches(ApplicationConstants.J))){
			//no hazard
		}
		else{
			if(Simulator.nIntU>0){
				//no hazard
			}
			else{
				Simulator.STRUCT[inst]="Y";
			}
		}
	}
	
	//check for RAW hazards
	public static void RAW(){
		
	}
	
	//check for WAW hazards
	public static void WAW(){
		
	}

}
