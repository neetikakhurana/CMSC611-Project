package com.nitika.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.nitika.main.Simulator;

public class DataParser {

	public static void parseData(String dataFile) throws FileNotFoundException{
		
		Scanner scanner=new Scanner(new FileInputStream(dataFile));
		String line="";
		int i=0;
		while(scanner.hasNext()){
			line=scanner.nextLine();
			if(line.isEmpty()==false){
				//for(int i=0;i<32;i++){
					System.out.println(Integer.parseInt(line,2));
					Simulator.data[i]=Integer.parseInt(line,2);
					i++;
				//}
			}
		}
		scanner.close();
	}
}
