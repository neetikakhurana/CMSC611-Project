package com.nitika.functionalUnit;

import java.util.HashMap;
import java.util.Map;

import com.nitika.constants.ApplicationConstants;
import com.nitika.enums.FunctionalUnit;
import com.nitika.hazards.Hazards;
import com.nitika.main.Simulator;

public class Available {
	
	private static int oFpMult=Simulator.nfpMult;
	private static int oFpAdd=Simulator.nfpAdder;
	private static int oFpDiv=Simulator.nfpDiv;
	private static int oIntU=Simulator.nIntU;
	public static Map<Integer,Integer> allUnits=new HashMap<Integer,Integer>();
	//public static int allUnits[]=new int[oFpAdd+oFpDiv+oFpMult+oIntU+1];
	public static NewUnit newUnit;
	public static NewUnit ArrayUnits[]=new NewUnit[Simulator.totalInst];
	public static int i=0;
	//occupy the functional unit
	public static void resourceAllocate(int instNo){
		
		if((Simulator.memory[instNo][1].equals(ApplicationConstants.SUBD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ADDD))){
			allUnits.put(instNo,oFpAdd-Simulator.nfpAdder);
			//i++;
			Simulator.nfpAdder--;
			newUnit=new NewUnit();
			newUnit.setLatency(Simulator.fpAddEx);
			newUnit.setLocation(allUnits.get(instNo));
			newUnit.setInstNo(instNo);
			Status.fUnit[instNo]=FunctionalUnit.FPADDER.getId();
			newUnit.setType(Status.fUnit[instNo]);
			ArrayUnits[instNo]=newUnit;
			i++;
		}
		else if((Simulator.memory[instNo][1].equals(ApplicationConstants.MULTD))){
			allUnits.put(instNo,oFpAdd+(oFpMult-Simulator.nfpMult));
			newUnit=new NewUnit();
			newUnit.setLatency(Simulator.fpMulEx);
			newUnit.setLocation(allUnits.get(instNo));
			newUnit.setInstNo(instNo);
			Status.fUnit[instNo]=FunctionalUnit.FPMULTIPLIER.getId();
			newUnit.setType(Status.fUnit[instNo]);
			ArrayUnits[instNo]=newUnit;
			Simulator.nfpMult--;
		}
		else if((Simulator.memory[instNo][1].equals(ApplicationConstants.DIVD))){
			allUnits.put(instNo,oFpAdd+oFpMult+(oFpDiv-Simulator.nfpDiv));
			newUnit=new NewUnit();
			newUnit.setLatency(Simulator.fpDivEx);
			newUnit.setLocation(allUnits.get(instNo));
			newUnit.setInstNo(instNo);
			Status.fUnit[instNo]=FunctionalUnit.FPDIVIDER.getId();
			newUnit.setType(Status.fUnit[instNo]);
			ArrayUnits[instNo]=newUnit;
			Simulator.nfpDiv--;
		}
		else if ((Simulator.memory[instNo][1].contains(ApplicationConstants.LI)) || (Simulator.memory[instNo][1].matches(ApplicationConstants.DADD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DADDI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DSUB)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.DSUBI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.LUI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ANDI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ORI)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.AND))) {
			allUnits.put(instNo,oFpAdd+oFpMult+oFpDiv+(oIntU-1));
			newUnit=new NewUnit();
			newUnit.setLatency(1);
			newUnit.setLocation(allUnits.get(instNo));
			newUnit.setInstNo(instNo);
			Status.fUnit[instNo]=FunctionalUnit.INTEGERUNIT.getId();
			newUnit.setType(Status.fUnit[instNo]);
			ArrayUnits[instNo]=newUnit;
			Simulator.nIntU--;
		}
		else if((Simulator.memory[instNo][1].contains(ApplicationConstants.LD)) || (Simulator.memory[instNo][1].contains(ApplicationConstants.SD)) || (Simulator.memory[instNo][1].contains(ApplicationConstants.LW)) || (Simulator.memory[instNo][1].contains(ApplicationConstants.SW))){
			allUnits.put(instNo,oFpAdd+oFpMult+oFpDiv+oIntU+(Hazards.loads-1));
			newUnit=new NewUnit();
			if(Simulator.memory[instNo][1].contains(ApplicationConstants.LD) || Simulator.memory[instNo][1].contains(ApplicationConstants.SD)){
				newUnit.setLatency(2);
			}
			else
			{
				newUnit.setLatency(1);
			}
			newUnit.setLocation(allUnits.get(instNo));
			newUnit.setInstNo(instNo);
			Status.fUnit[instNo]=5;
			newUnit.setType(Status.fUnit[instNo]);
			ArrayUnits[instNo]=newUnit;
			Hazards.loads--;
		}
		else{
			Status.fUnit[instNo]=4; //if instr doesnt use any of the FU
		}
	}
	
	//release it once the work is done
	public static void resourceReleased(int instNo){
		
		if((Simulator.memory[instNo][1].equals(ApplicationConstants.SUBD)) || (Simulator.memory[instNo][1].equals(ApplicationConstants.ADDD))){
			if(Simulator.nfpAdder!=oFpAdd)
			{
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
		else if(((Simulator.memory[instNo][1].contains(ApplicationConstants.SW)) || (Simulator.memory[instNo][1].contains(ApplicationConstants.LW)) || (Simulator.memory[instNo][1].contains(ApplicationConstants.LD)) || (Simulator.memory[instNo][1].contains(ApplicationConstants.SD)))){
			if(Hazards.loads!=1){
				Hazards.loads++;
			}
			else{
				//functional unit is released already
			}
		}
	}
}
