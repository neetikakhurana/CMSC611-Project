package com.nitika.scoreboard;

import java.util.ArrayList;
import java.util.List;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.pipeline.Stages;

public class CalcScoreboard {
	public static int fetchControl=1;
	public static List<Integer> Allfetch=new ArrayList<Integer>();
	public static boolean branchInProgress=false;
	public static int lastEnd=0;

	public static void calculate(){
		boolean iterate=true;
		//move instruction to fetch stage
		while(iterate){
			
			//move instruction to issue stage
			for(int i=Stages.writeIncomplete;i<=Allfetch.size();i++){
				if(i==Simulator.totalInst){
					iterate=false;
					break;
				}
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT)){
					if(Simulator.fetch[i]==0 && !branchInProgress){
						if(i==0){
							Stages.fetchStage(i);
							if(!Allfetch.contains(i))
								Allfetch.add(i);
							continue;
						}
						else{
							if(Simulator.issue[i-1]!=0){
								Stages.fetchStage(i);
								if(!Allfetch.contains(i))
									Allfetch.add(i);
								continue; 
							}
						}
					}
			
						if(Simulator.issue[i]==0 && Simulator.fetch[i]<fetchControl && Simulator.fetch[i]!=0){
							if(i==0){
								Stages.issueStage(i);
								continue;
								
							}
							else if(Simulator.fetch[i-1]!=0){
								if(Stages.writeIncomplete==0){
									if(Simulator.write[i-1]!=0){
										if(Simulator.write[i-1]!=fetchControl)
											Stages.issueStage(i);
											continue;
									}
									else{
										Stages.issueStage(i);
										continue;
									}
								}
								else{
									//check if the previously written instruction that was causing a hazard was in the same cycle
									if(Simulator.write[Stages.writeComplete]!=0){
										if(Simulator.write[Stages.writeComplete]!=fetchControl)
											Stages.issueStage(i);
											continue;
									}
									else{
										Stages.issueStage(i);
										continue;
									}
								}
							}
						}
						
						if(Simulator.read[i]==0 && Simulator.issue[i]<fetchControl && Simulator.issue[i]!=0){
							if(i==0){
								Stages.readStage(i);
								continue;
							}
							else{
								if(Simulator.write[i-1]!=0){
									if(Simulator.write[i-1]!=fetchControl)
										Stages.readStage(i);
										continue;
								}
								else{
									Stages.readStage(i);
									continue;
								}
							}
						}
						
						if(Simulator.execute[i]==0 && Simulator.read[i]<fetchControl && Simulator.read[i]!=0){
							if(i==0){
								Stages.executeStage(i);
								continue;
							}
							else {
								Stages.executeStage(i);
								continue;
							}
						}
						
						if(Simulator.write[i]==0 && Simulator.execute[i]<fetchControl && Simulator.execute[i]!=0){
							if(i==0){
								Stages.writeStage(i);
								continue;
							}
							else{
								Stages.writeStage(i);
								//after the branching, new instructions have been inserted so this value would change
								if(i==(Simulator.totalInst-1)){
									iterate=false;
								}
								continue;
							}
						}
					//}
				}
				//if it is a halt instruction
				else{
					//just fetch the HLT instruction
					//we again to implement the check that the previous instruction has been issued
					// and on every clock cycle, it is fetching the hlt again and again (since no other stage has been defined for it)
					if(Simulator.issue[i-1]!=0 && Simulator.fetch[i]==0 && !branchInProgress){
						Stages.fetchStage(i);
						System.out.println("Fetched HLT instruction");
						if(!Allfetch.contains(i))
							Allfetch.add(i);
						continue;
					}
					else if(Simulator.issue[i-1]!=0 && Simulator.fetch[i]!=0 && !branchInProgress && Simulator.read[i-1]!=fetchControl){
						Stages.issueStage(i);
						System.out.println("Issued HLT instruction");
						Simulator.read[i]=-1;
						Simulator.execute[i]=-1;
						Simulator.write[i]=-1;
						continue;
					}
				}
			}
			
			fetchControl++;
		}
	}
	
	
	//write values to result.txt
	public static void writeResultToFile(int beg, int end){
		//access requests is the total no of instructions executed-> no of times fetch stage is called(say)
		//cache hits
		//requests for data cache
		//data cache hits
		lastEnd=end;
		 System.out.println("\nWriting on score board");
	     	for(int i=beg;i<=end;i++)
	     	{
	     		String string="";
	     		if(Simulator.memory[i][0].matches("")==false)
	     		{
	     			string = string + Simulator.memory[i][0];
	     			for(int j=0;j<7-Simulator.memory[i][0].length();j++)
	     				string=string+" ";
	     			string=string+":";
	     		}
	     		else
	     		string=string+"        ";
	     		
	     		string = string + Simulator.memory[i][1];
	             for(int j=0;j<7-Simulator.memory[i][1].length();j++)
	             	string=string+" ";
	             
	             string = string + Simulator.memory[i][2];
	             for(int j=0;j<5-Simulator.memory[i][2].length();j++)
	             	string=string+" ";
	             
	             if(Simulator.memory[i][3].matches("")==false)
	             {
	             	string = string + ","+Simulator.memory[i][3];
	             	for(int j=0;j<5-Simulator.memory[i][3].length();j++)
	             		string=string+" ";
	             }
	             else
	             string=string+"      ";

	             if(Simulator.memory[i][4].matches("")==false)
	             {
	             	string = string + ","+Simulator.memory[i][4];
	             	for(int j=0;j<5-Simulator.memory[i][4].length();j++)
	             		string=string+" ";
	             }
	             else
	             string=string+"      ";

	             string=string+Simulator.fetch[i];
	             for(int j=0;j<10-(String.valueOf(Simulator.fetch[i]).length());j++)
	             	string=string+" ";
	             
	             if(Simulator.issue[i]==-1 || Simulator.issue[i]==0)
		             	string=string+"          ";
	             else{
		             string=string+Simulator.issue[i];
		             for(int j=0;j<10-(String.valueOf(Simulator.issue[i]).length());j++)
		             	string=string+" ";	             
	             }
	             if(Simulator.read[i]==-1 || Simulator.read[i]==0)
		             	string=string+"          ";
	             else{
		             string=string+Simulator.read[i];
		             for(int j=0;j<10-(String.valueOf(Simulator.read[i]).length());j++)
		             	string=string+" ";
	             }
	             if(Simulator.execute[i]==-1 || Simulator.execute[i]==0)
	             	string=string+"          ";
	             else
	             {
	             	string=string+Simulator.execute[i];
	             	for(int j=0;j<10-(String.valueOf(Simulator.execute[i]).length());j++)
	             		string=string+" ";
	             }
	             
	             if(Simulator.write[i]==-1 || Simulator.write[i]==0)
	             	string=string+"    ";
	             else 
	             {
	             	string=string+Simulator.write[i];
	             	for(int j=0;j<5-(String.valueOf(Simulator.write[i]).length());j++)
	             		string=string+" ";
	             }
	             string=string+"\t\t"+Simulator.RAW[i]+"\t\t"+Simulator.WAW[i]+"\t\t"+Simulator.STRUCT[i];
	             System.out.println(string);
	             Simulator.scoreBoard.format("%s %n",string);
	     	}
	}
}
