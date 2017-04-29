package com.nitika.functionalUnit;

import com.nitika.constants.ApplicationConstants;
import com.nitika.enums.FunctionalUnit;
import com.nitika.main.Simulator;

public class Available {
	
	private static int oFpMult=Simulator.nfpMult;
	private static int oFpAdd=Simulator.nfpAdder;
	private static int oFpDiv=Simulator.nfpDiv;
	private static int oIntU=Simulator.nIntU;

	//occupy the functional unit
	public static void resourceAllocate(int instNo){
		
		if((Simulator.memory[instNo][1].equals(ApplicationConstants.SUBD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ADDD))){
			Simulator.nfpAdder--;
			Status.fUnit[instNo]=FunctionalUnit.FPADDER.getId();
		}
		else if((Simulator.memory[instNo][1].equals(ApplicationConstants.MULTD))){
			Simulator.nfpMult--;
			Status.fUnit[instNo]=FunctionalUnit.FPMULTIPLIER.getId();
		}
		else if((Simulator.memory[instNo][1].equals(ApplicationConstants.DIVD))){
			Simulator.nfpDiv--;
			Status.fUnit[instNo]=FunctionalUnit.FPDIVIDER.getId();
		}
		else if ((Simulator.memory[instNo][1].contains(ApplicationConstants.LI)) || (Simulator.memory[instNo][1].matches(ApplicationConstants.DADD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DADDI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DSUB)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DSUBI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.LUI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ANDI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ORI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.AND))) {
			Simulator.nIntU--;
			Status.fUnit[instNo]=FunctionalUnit.INTEGERUNIT.getId();
		}
		else{
			Status.fUnit[instNo]=4; //if instr doesnt use any of the FU
		}
	}
	
	//release it once the work is done
	public static void resourceReleased(int instNo){
		
		if((Simulator.memory[instNo][1].equals(ApplicationConstants.SUBD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ADDD))){
			if(Simulator.nfpAdder!=oFpAdd){
				Simulator.nfpAdder++;
			}
			else{
				//functional unit is released already
			}
		}
		else if((Simulator.memory[instNo][1].equals(ApplicationConstants.MULTD))){
			if(Simulator.nfpMult!=oFpMult){
				Simulator.nfpMult++;
			}
			else{
				//functional unit is released already
			}
		}
		else if((Simulator.memory[instNo][1].equals(ApplicationConstants.DIVD))){
			if(Simulator.nfpDiv!=oFpDiv){
				Simulator.nfpDiv++;
			}
			else{
				//functional unit is released already
			}
		}
		else if ((Simulator.memory[instNo][1].contains(ApplicationConstants.LI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DADD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DADDI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DSUB)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DSUBI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.LUI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ANDI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ORI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.AND))) {
			if(Simulator.nIntU!=oIntU){
				Simulator.nIntU++;
			}
			else{
				//functional unit is released already
			}
		}
	}
}
