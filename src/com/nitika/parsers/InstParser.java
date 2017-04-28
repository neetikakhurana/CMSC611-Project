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
}
