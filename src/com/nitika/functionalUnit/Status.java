package com.nitika.functionalUnit;

import com.nitika.constants.ApplicationConstants;
import com.nitika.enums.FunctionalUnit;
import com.nitika.main.Simulator;

public class Status {

	public static int fUnit[]=new int[Simulator.totalInst];
	
	//call it only after total instructions have been determined i.e. inst.txt has been parsed
	public static void functional(){
		for(int i=0;i<Simulator.totalInst;i++){
			if((Simulator.memory[i][1].equals(ApplicationConstants.SUBD)) || (Simulator.memory[i][1].equals(ApplicationConstants.ADDD))){
				Status.fUnit[i]=FunctionalUnit.FPADDER.getId();
			}
			else if((Simulator.memory[i][1].equals(ApplicationConstants.MULTD))){
				Status.fUnit[i]=FunctionalUnit.FPMULTIPLIER.getId();
			}
			else if((Simulator.memory[i][1].equals(ApplicationConstants.DIVD))){
				Status.fUnit[i]=FunctionalUnit.FPDIVIDER.getId();
			}
			else if ((Simulator.memory[i][1].contains(ApplicationConstants.LI)) || (Simulator.memory[i][1].matches(ApplicationConstants.DADD)) || (Simulator.memory[i][1].equals(ApplicationConstants.DADDI)) || (Simulator.memory[i][1].equals(ApplicationConstants.DSUB)) || (Simulator.memory[i][1].equals(ApplicationConstants.DSUBI)) || (Simulator.memory[i][1].equals(ApplicationConstants.LUI)) || (Simulator.memory[i][1].equals(ApplicationConstants.ANDI)) || (Simulator.memory[i][1].equals(ApplicationConstants.ORI)) || (Simulator.memory[i][1].equals(ApplicationConstants.AND))) {
				Status.fUnit[i]=FunctionalUnit.INTEGERUNIT.getId();
			}
			else if((Simulator.memory[i][1].contains(ApplicationConstants.LD)) || (Simulator.memory[i][1].contains(ApplicationConstants.SD)) || (Simulator.memory[i][1].contains(ApplicationConstants.LW)) || (Simulator.memory[i][1].contains(ApplicationConstants.SW))){
				Status.fUnit[i]=5;
			}
			else{
				Status.fUnit[i]=4; //if instr doesnt use any of the FU
			}
		}
	}
}
