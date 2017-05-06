package com.nitika.hazards;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.parsers.InstParser;
import com.nitika.pipeline.Stages;

public class Hazards {
	public static int loads=1;
	public static int[] rawSource=new int[Simulator.totalInst];
	public static int[] wawSource=new int[Simulator.totalInst];
	public static int[] structSource=new int[Simulator.totalInst];
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
			//until the first instruction hasn't completed its execution
			if(Stages.writeIncomplete==0){
				if((Simulator.memory[Stages.writeIncomplete][2].equalsIgnoreCase(Simulator.memory[instruction][3])) || (Simulator.memory[Stages.writeIncomplete][2].equalsIgnoreCase(Simulator.memory[instruction][4]))){
					rawSource[instruction]=Stages.writeIncomplete;
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
					//branch instruction
					if((!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BEQ)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BNE)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.LW)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.LD)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.SW)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.SD))){ 
						if((Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][3])) || (Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][4]))){
							rawSource[instruction]=i;
							Simulator.RAW[instruction]="Y";
							return true;
						}else{
							//no previous instruction uses the same destination
							continue;
						}
					}
					//current instruction is a load one
					else if((Simulator.memory[instruction][1].matches(ApplicationConstants.LD)) || (Simulator.memory[instruction][1].matches(ApplicationConstants.LW)) ){
						//if source reg in the source is a destination of previous instruction
						if(!InstParser.getLDSource(instruction).equals(Simulator.memory[i][2])){
							continue;
						}
						else{
							rawSource[instruction]=i;
							Simulator.RAW[instruction]="Y";
							return true;
						}
					}
					//store instruction
					else if((Simulator.memory[instruction][1].matches(ApplicationConstants.SW)) || (Simulator.memory[instruction][1].matches(ApplicationConstants.SD))){
						//if store instruction's source doesn't match with any of the previous instruction's dest and no previous instruction was another store instruction
						if(!Simulator.memory[instruction][2].equals(Simulator.memory[i][2]) && !Simulator.memory[i][1].matches(ApplicationConstants.SW) && !Simulator.memory[i][1].matches(ApplicationConstants.SD)){
							continue;
						}
						//if store instruction's source doesn't match with any of the previous instruction's dest and the previous instruction was another store instruction
						else if(!Simulator.memory[instruction][2].equals(Simulator.memory[i][2]) && (Simulator.memory[i][1].matches(ApplicationConstants.SW) || !Simulator.memory[i][1].matches(ApplicationConstants.SD))){
							//check if current instruction's source matched the prev store's destination
							if(!InstParser.getStoreDest(i).equals(Simulator.memory[instruction][2])){
								continue;
							}
							else{
								rawSource[instruction]=i;
								Simulator.RAW[instruction]="Y";
								return true;
							}
						}
						else{
							rawSource[instruction]=i;
							Simulator.RAW[instruction]="Y";
							return true;
						}
					}
					else{
						if((Simulator.registers.get(Simulator.memory[i][2])==(Simulator.registers.get(Simulator.memory[instruction][2]))) || (Simulator.registers.get(Simulator.memory[i][2])==(Simulator.registers.get(Simulator.memory[instruction][3])))){
							rawSource[instruction]=i;
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
					wawSource[instruction]=Stages.writeIncomplete;
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
					if((!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BEQ)) && (!Simulator.memory[instruction][1].equalsIgnoreCase(ApplicationConstants.BNE)) )
					{ 
						if(Simulator.memory[i][2].equalsIgnoreCase(Simulator.memory[instruction][2])){
							wawSource[instruction]=i;
							Simulator.WAW[instruction]="Y";
							return true;
						}
						else
						{
							//no previous instruction uses the same destination
							continue;
						}
					}
					//if current instruction is a store one
					else if(Simulator.memory[instruction][1].matches(ApplicationConstants.SW) || Simulator.memory[instruction][1].matches(ApplicationConstants.SD)){
						//if prev isntruction is also a store one and their destinations do not match
						if(!InstParser.getStoreDest(instruction).equals(InstParser.getStoreDest(i)) && (Simulator.memory[i][1].equals(ApplicationConstants.SW) || Simulator.memory[i][1].equals(ApplicationConstants.SD))){
							continue;
						}
						//if prev instr is not store and still their destinations do not clash
						else if(!InstParser.getStoreDest(instruction).equals(Simulator.memory[i][2]) && (Simulator.memory[i][1].equals(ApplicationConstants.SW) || Simulator.memory[i][1].equals(ApplicationConstants.SD))){
							continue;
						}
						else{
							wawSource[instruction]=i;
							Simulator.WAW[instruction]="Y";
							return true;
						}
					}
					else{
						continue;
					}
				}
			}
			return false;
			
		}
	}
}
