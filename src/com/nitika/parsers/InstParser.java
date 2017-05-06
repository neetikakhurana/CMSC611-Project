package com.nitika.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.nitika.main.Simulator;

public class InstParser {

	//parse the instructions from inst.txt
	public static void parseInstructions(String instFile) throws FileNotFoundException{
		
		Scanner scanner=new Scanner(new FileInputStream(instFile));
		String line="";
		int nInst=0;
		while(scanner.hasNext()){
			line=scanner.nextLine();
			if(line.isEmpty()==false){
				Simulator.memory[nInst]=getInstructionString(line);
				nInst++;
			}
		}
		scanner.close();
	}
	
	
	public static String[] getInstructionString(String instruction){
	
		String inst[]=new String[5];
		String text="";
		int j=0;
		for(int i=0;i<5;i++){
			inst[i]="";
		}
		boolean label=false,op=false;
		for(int i=0;i<instruction.length();i++)
		{
			if(instruction.charAt(i)!=':' && instruction.charAt(i)!=' ' && instruction.charAt(i)!=',')
			{
				text+=instruction.charAt(i);
				op=true;
			}
			else{
				if(instruction.charAt(i)==':'){
					label=true;
				}
					if(op==true){
						inst[j]=text.toUpperCase();
						j++;
						text="";
						op=false;
					}
				}
		}
		inst[j]=text.toUpperCase();
		if(label==false){
			inst[4]=inst[3];
     		inst[3]=inst[2];
     		inst[2]=inst[1];
     		inst[1]=inst[0];
     		inst[0]="";
		}
		return inst;
	}
	
	public static String getBranchSourceLabel(int instNo){
		return Simulator.memory[instNo][4];
	}
	
	public static String getSource1(int instNo){
		return Simulator.memory[instNo][3];
	}
	
	public static String getLDSource(int instNo){
		String indexedAdd[]=new String[2];
		indexedAdd=Simulator.memory[instNo][3].split("\\(");
		String temp[]=new String[2];
		temp=indexedAdd[1].split("\\)");
		indexedAdd[1]=temp[0];
		return indexedAdd[1];
	}
	
	public static String getSource2(int instNo){
		return Simulator.memory[instNo][4];
	}
	
	public static int getIncDecValue(int instNo){
		return Integer.parseInt(Simulator.memory[instNo][4].toString());
	}
	
	public static int getLIValue(int instNo){
		return Integer.parseInt(Simulator.memory[instNo][3].toString());
	}
	
	public static String getStoreDest(int instNo){
		String indexedAdd[]=new String[2];
		indexedAdd=Simulator.memory[instNo][3].split("\\(");
		String temp[]=new String[2];
		temp=indexedAdd[1].split("\\)");
		indexedAdd[1]=temp[0];
		return indexedAdd[1];
	}
	
	public static String getStoreSource(int instNo){
		return Simulator.memory[instNo][3];
	}
}
