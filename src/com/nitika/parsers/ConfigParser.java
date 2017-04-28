package com.nitika.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.nitika.main.Simulator;

public class ConfigParser {

	//parse the config.txt file
	public static void parseConfigurations(String configFile) throws FileNotFoundException{
	
		Scanner scanner=new Scanner(new FileInputStream(configFile));
		String text[];
		String line="",config="";
		while(scanner.hasNext()){
			line=scanner.nextLine();
			if(line.isEmpty()==false){
				config+=line+":";
			}
		}
		scanner.close();

		config=config.replace(",",":");
		text=config.split(":");
		
		for(int i=0;i<text.length;i++){
			
			//there may be many whitespaces so trim them
			switch(text[i].trim().toUpperCase())
			{
				case "FP ADDER":
					i++;
					Simulator.nfpAdder=Integer.parseInt(text[i].trim());
					i++;
					Simulator.fpAddEx=Integer.parseInt(text[i].trim());
					System.out.println("FP ADDER:"+Simulator.nfpAdder+","+Simulator.fpAddEx);
					break;
				
				case "FP MULTIPLIER":
					i++;
					Simulator.nfpMult=Integer.parseInt(text[i].trim());
					i++;
					Simulator.fpMulEx=Integer.parseInt(text[i].trim());
					System.out.println("FP MULTIPLIER:"+Simulator.nfpMult+","+Simulator.fpMulEx);
					break;
					
				case "FP DIVIDER":
					i++;
					Simulator.nfpDiv=Integer.parseInt(text[i].trim());
					i++;
					Simulator.fpDivEx=Integer.parseInt(text[i].trim());
					System.out.println("FP DIVIDER:"+Simulator.nfpDiv+","+Simulator.fpDivEx);
					break;
					
				case "I-CACHE":
					i++;
					Simulator.nbIcache=Integer.parseInt(text[i].trim());
					i++;
					Simulator.bsizeIcache=Integer.parseInt(text[i].trim());
					System.out.println("I-CACHE:"+Simulator.nbIcache+","+Simulator.bsizeIcache);
					break;
					
				default:
					break;
			}
		}
	}
}
