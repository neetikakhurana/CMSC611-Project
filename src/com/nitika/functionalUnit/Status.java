package com.nitika.functionalUnit;

import com.nitika.main.Simulator;

public class Status {

	public static String busy[]=new String[5];
	public static String fi[]=new String[5];
	public static String fj[]=new String[5];
	public static String fk[]=new String[5];
	public static String qj[]=new String[5];
	public static String qk[]=new String[5];
	public static String op[]=new String[5];
	public static String rj[]=new String[5];
	public static String rk[]=new String[5];

	public static int fUnit[]=new int[Simulator.totalInst];
	
	//call it only after total instructions have been determined i.e. inst.txt has been parsed
	public static void initializeFUStatusPara(){
		for(int i=0;i<4;i++){
			busy[i]="N";
			op[i]="";
			fi[i]="";
			fj[i]="";
			fk[i]="";
			qj[i]="";
			qk[i]="";
			rj[i]="";
			rk[i]="";
		}
	}
	
	public static void assignFUPara(int instNo){
		if(fUnit[instNo]==4){
			//it does not use any fucntional unit so just pass
		}
		else
		{
			//this will change for every type of instruction
			//ignoring qj and qk for sometime
			busy[fUnit[instNo]]="Y";
			op[fUnit[instNo]]=Simulator.memory[instNo][1];
			fi[fUnit[instNo]]=Simulator.memory[instNo][2];//destination value
			fj[fUnit[instNo]]=Simulator.memory[instNo][3];//source1
			fk[fUnit[instNo]]=Simulator.memory[instNo][4];//source2
			if(fk[fUnit[instNo]]!=""){
				rk[fUnit[instNo]]="Y";
			}
			if(fj[fUnit[instNo]]!=""){
				rj[fUnit[instNo]]="Y";
			}
		}
	}
	
}
