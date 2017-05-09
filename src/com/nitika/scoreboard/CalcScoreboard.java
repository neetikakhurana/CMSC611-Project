package com.nitika.scoreboard;

import java.util.ArrayList;
import java.util.List;

import com.nitika.constants.ApplicationConstants;
import com.nitika.functionalUnit.Status;
import com.nitika.hazards.Branch;
import com.nitika.hazards.Hazards;
import com.nitika.main.Simulator;
import com.nitika.pipeline.Stages;

public class CalcScoreboard {
	public static int fetchControl=1;
	public static List<Integer> Allfetch=new ArrayList<Integer>();
	public static boolean branchInProgress=false;
	public static int lastEnd=0;
	
	public static void calculate()
	{
		boolean iterate=true;
		/**
		 * increment clock cycle periodically
		 */
		while(iterate)
		{
			
			for(int i=Stages.writeIncomplete;i<Simulator.totalInst;i++)
			{
				//check if all instructions have been written already
				if(Stages.allCompletedWrite())
				{
						iterate=false;
						break;
				}
			
				//check if its not a HALT instruction
				if(!Simulator.memory[i][1].equals(ApplicationConstants.HLT))
				{
					/**
					 * FETCH STAGE
					 */
					if(Simulator.fetch[i]==0 && !branchInProgress)
					{
						if(i==0)
						{
							Stages.fetchStage(i);
							continue;
						}
						else{
							//check if prev one has been issued
							if(Simulator.fetch[i-1]!=0)
							{
								Stages.fetchStage(i);
								continue; 
							}
							
						}
					}
					
						/**
						 * ISSUE STAGE
						 */
						if(Simulator.issue[i]==0 && Simulator.fetch[i]<fetchControl && Simulator.fetch[i]!=0)
						{
							if(i==0)
							{
								Stages.issueStage(i);
								continue;
								
							}
							else if(Simulator.fetch[i-1]!=0)
							{
								if(Stages.writeIncomplete==0)
								{
									if(Simulator.write[i-1]!=0)
									{
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
									//check if the previously written instruction that was causing a structural hazard was in the same cycle
									if(Simulator.write[Stages.writeComplete]!=0 && Status.fUnit[i]==Status.fUnit[Stages.writeComplete])
									{
										if(Simulator.write[Stages.writeComplete]!=fetchControl)
											Stages.issueStage(i);
											continue;
									}
									//if the current one had a raw dependency over the last completed instruction
									else if(Simulator.write[Stages.writeComplete]!=0 && Hazards.wawSource[i]==Stages.writeComplete)
									{
										if(Simulator.write[Stages.writeComplete]!=fetchControl)
										{
											Stages.issueStage(i);
											continue;
										}
									}
									
									else{
										Stages.issueStage(i);
										continue;
									}
								}
							}
						}
						
						/**
						 * READ STAGE
						 */
						if(Simulator.read[i]==0 && Simulator.issue[i]<fetchControl && Simulator.issue[i]!=0)
						{
							if(i==0)
							{
								Stages.readStage(i);
								continue;
							}
							else if(Simulator.issue[i-1]!=0)
							{
								//none has been written so far
								if(Stages.writeIncomplete==0)
								{
									if(Simulator.write[i-1]!=0)
									{
										if(Simulator.write[i-1]!=fetchControl)
											Stages.readStage(i);
											continue;
									}
									else{
										Stages.readStage(i);
										continue;
									}
								}
								else{
									//check if the previously written instruction that was causing a hazard was in the same cycle(first one)
									if(Simulator.write[Stages.writeComplete]!=0 && Hazards.rawSource[i]==Stages.writeComplete)
									{
										//check if it has raw hazard on both the operands
										if(Hazards.rawSource2[i]!=0){
											if(Simulator.write[Stages.writeComplete]!=fetchControl && Simulator.write[Hazards.rawSource2[i]]!=0 && Simulator.write[Hazards.rawSource2[i]]!=fetchControl)
											{
												Stages.readStage(i);
												continue;
											}
										}
										//hazard is only on a single operand
										else{
											if(Simulator.write[Stages.writeComplete]!=fetchControl)
											{
												Stages.readStage(i);
												continue;
											}
										}
									}
									//if the second raw hazard operand is free first
									else if(Simulator.write[Stages.writeComplete]!=0 && Hazards.rawSource2[i]==Stages.writeComplete)
									{
										//check if it has raw hazard on both the operands
										if(Hazards.rawSource[i]!=0){
											if(Simulator.write[Stages.writeComplete]!=fetchControl && Simulator.write[Hazards.rawSource[i]]!=0 && Simulator.write[Hazards.rawSource[i]]!=fetchControl)
											{
												Stages.readStage(i);
												continue;
											}
										}
										//hazard is only on a single operand
										else{
											if(Simulator.write[Stages.writeComplete]!=fetchControl)
											{
												Stages.readStage(i);
												continue;
											}
										}
									}
									else{
										Stages.readStage(i);
										continue;
									}
								}
							}
						}
						
						/**
						 * EXECUTE STAGE				
						 */
						if(Simulator.execute[i]==0 && Simulator.read[i]<fetchControl && Simulator.read[i]!=0)
						{
							if(i==0)
							{
								Stages.executeStage(i);
								continue;
							}
							else {
								Stages.executeStage(i);
								continue;
							}
						}
						
						/**
						 * WRITE STAGE
						 */
						if(Simulator.write[i]==0 && Simulator.execute[i]<fetchControl && Simulator.execute[i]!=0)
						{
							if(i==0)
							{
								Stages.writeStage(i);
								continue;
							}
							else{
								Stages.writeStage(i);
								//after the branching, new instructions have been inserted so this value would change
								if(i==(Simulator.totalInst-1))
								{
									iterate=false;
								}
								continue;
							}
						}
						
				}
				//if it is a halt instruction
				else{
					//just fetch the HLT instruction
					//we again to implement the check that the previous instruction has been issued
					// and on every clock cycle, it is fetching the hlt again and again (since no other stage has been defined for it)
					if(Simulator.fetch[i]==0 && Simulator.fetch[i-1]!=0)
					{
						if(!Branch.branchSet){
						
							Stages.fetchStage(i);
							continue;
						}
						else{
							//second instruction after branch will never be fetched and needs to be flushed
							if(Simulator.issue[i-1]!=0)
							{
								Simulator.fetch[i]=-1;
								Simulator.issue[i]=-1;
								Simulator.read[i]=-1;
								Simulator.execute[i]=-1;
								Simulator.write[i]=-1;
								Branch.branchSet=false;
							}
						}
					}
					else if(Simulator.issue[i-1]!=0 && Simulator.fetch[i]!=0 && !branchInProgress && Simulator.read[i-1]!=fetchControl && Simulator.issue[i]==0 && !Stages.goingToBranch){
						//if the branch is true and we have to go to the label
						if(Branch.branchSet){
							Simulator.issue[i]=-1;
							Simulator.read[i]=-1;
							Simulator.execute[i]=-1;
							Simulator.write[i]=-1;
							writeResultToFile(i, i);
							continue;
						}
						//if the previous instruction is also a HLT, then stop processing IF AND ONLY IF the branch is false
						if(Simulator.memory[i-1][1].equalsIgnoreCase(ApplicationConstants.HLT) && !Branch.branchSet){
							Stages.goingToBranch=true;
							Simulator.issue[i]=-1;
							Simulator.read[i]=-1;
							Simulator.execute[i]=-1;
							Simulator.write[i]=-1;
							writeResultToFile(i, i);
							continue;
						}
						Stages.issueStage(i);
						System.out.println("Issued HLT instruction");
						Simulator.read[i]=-1;
						Simulator.execute[i]=-1;
						Simulator.write[i]=-1;
						writeResultToFile(i, i);
						//freeze fetching of instructions any further
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
		// System.out.println("\nWriting on score board");
	     	for(int i=beg;i<=end;i++)
	     	{
	     		String string="";
	     		if(Simulator.fetch[i]==-1){
	            	 continue;
	             }
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
