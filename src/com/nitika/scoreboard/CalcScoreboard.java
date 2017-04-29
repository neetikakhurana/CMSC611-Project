package com.nitika.scoreboard;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.pipeline.Stages;

public class CalcScoreboard {
	
	public static void calculate(){
		boolean iterate=true;
		//move instruction to fetch stage
		while(iterate){
			for(int i=0;i<Simulator.totalInst;i++){
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT)){
					if(Simulator.fetch[i]==0){
						//if its the first instruction, simply move it to the fetch stage
						if(i==0){
							Stages.fetchStage(i);
							break;
						}
						//if there are instructions before it, check if the previous one has reached the issue stage
						else if(Simulator.issue[i-1]!=0){
							Stages.fetchStage(i);
							break;
						}
					}
				}
			}
			
			//move instruction to issue stage
			for(int i=0;i<Simulator.totalInst;i++){
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT)){
					if(Simulator.fetch[i]!=0 && Simulator.issue[i]==0){
						//check if the previous one has reached the read stage
						if(i==0){
							Stages.issueStage(i);
							break;
						}
						else if(Simulator.issue[i-1]!=0){
							Stages.issueStage(i);
							break;
						}
					}
				}
			}
			
			//move instruction to read stage
			for(int i=0;i<Simulator.totalInst;i++){
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT)){
					if(Simulator.issue[i]!=0 && Simulator.read[i]==0){
						//check if the previous one has reached the read stage
						if(i==0){
							Stages.readStage(i);
							break;
						}
						else if(Simulator.read[i-1]!=0){
							Stages.readStage(i);
							break;
						}
					}
				}
			}
			
			//move instruction to execute stage
			for(int i=0;i<Simulator.totalInst;i++){
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT)){
					if(Simulator.read[i]!=0 && Simulator.execute[i]==0){
						//check if the previous one has reached the read stage
						if(i==0){
							Stages.executeStage(i);
							break;
						}
						else if(Simulator.execute[i-1]!=0){
							Stages.executeStage(i);
							break;
						}
					}
				}
			}
			
			//move instruction to  write stage
			for(int i=0;i<Simulator.totalInst;i++){
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT)){
					if(Simulator.execute[i]!=0 && Simulator.write[i]==0){
						//check if the previous one has reached the read stage
						if(i==0){
							Stages.writeStage(i);
							break;
						}
						else if(Simulator.write[i-1]!=0){
							Stages.writeStage(i);
							if(i==(Simulator.totalInst-1)){
								iterate=false;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	
	//write values to result.txt
	public static void writeResultToFile(){
		//access requests is the total no of instructions executed-> no of times fetch stage is called(say)
		//cache hits
		//requests for data cache
		//data cache hits
	}
}
