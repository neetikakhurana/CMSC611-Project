package com.nitika.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;

import com.nitika.parsers.ConfigParser;
import com.nitika.parsers.DataParser;
import com.nitika.parsers.InstParser;
import com.nitika.scoreboard.CalcScoreboard;

/**
 * 
 * @author neeti
 * This class takes the input files for parsing
 *
 */
public class Simulator {
	
	public static String instTxt="";
	public static String dataTxt="";
	public static String configTxt="";
	public static String resultTxt="";
	public static Formatter scoreBoard;
	public static int totalInst=0;
	public static int fetch[], read[], issue[], execute[], write[], data[];
	public static String memory[][], RAW[], WAW[], STRUCT[], WAR[];
	public static int nfpAdder=0, nfpMult=0, nfpDiv=0, fpAddEx=0, fpMulEx=0, fpDivEx=0, nbIcache=0, bsizeIcache=0, nIntU=1;
	
	public static void main(String[] args) throws IOException,FileNotFoundException {
		
		getInput();
		intializeResultTxt();
		initiateDefaultValues();
		InstParser.parseInstructions(instTxt);
		ConfigParser.parseConfigurations(configTxt);
		DataParser.parseData(dataTxt);
		
		CalcScoreboard.calculate();
		
	}
	
	//get input from command line
	public static void getInput(){
		
		boolean data=true;
		while(data){
			System.out.println("Enter the input in the form: simulator inst.txt data.txt config.txt result.txt");
			Scanner command=new Scanner(System.in);
			String comm=command.nextLine();
			String commWords[]=new String[5];
			commWords=comm.split(" ");
			
			//check of the command line input is correct
			if(commWords[0].matches("simulator")){
				data=false;
				int count=0;
				//check if the file formats are correct
				for(int i=1;i<5;i++){
					if(commWords[i].contains(".txt")==true){
						count++;
					}
					else
					{
						System.out.println("Wrong input file format. Run the program again");
						System.exit(0);
					}
					//perform parsing if all file formats are correct
					if(count==4){
						instTxt=commWords[1];
						dataTxt=commWords[2];
						configTxt=commWords[3];
						resultTxt=commWords[4];
					}
				}
			}
			else
			{
				//if the input is incorrect
				System.out.println("Incorrect input. Run the program again.");
				System.exit(0);
			}
			command.close();
		}
	}
	
	//intialize the result.txt file
	public static void intializeResultTxt() throws FileNotFoundException{
		
		//define the header for result.txt
		scoreBoard=new Formatter(resultTxt);
		scoreBoard.format("\t\t%s\t\t\t\t%s    %s\t   %s    %s     %s     %s     %s   %s%n",
   			 "Instruction","Fetch","Issue","Read","Exec","Write","RAW","WAW","Struct");
	}
	
	//initiate all stages and hazards to default values for all instructions
	public static void initiateDefaultValues() throws IOException{
		
		//count the number of instructions
		File instFile=new File(instTxt);
		Scanner scan=new Scanner(new FileInputStream(instFile));
		int noInst=0;
		String inst;
		while(scan.hasNext()){
			inst=scan.nextLine();
			noInst++;
			if(inst.isEmpty()==true){
				break;
			}
		}
		fetch=new int[noInst];
		read=new int[noInst];
		issue=new int[noInst];
		execute=new int[noInst];
		write=new int[noInst];
		totalInst=noInst;
		memory=new String[noInst][5];
		data=new int[noInst];
		RAW=new String[noInst];
		WAW=new String[noInst];
		WAR=new String[noInst];
		STRUCT=new String[noInst];
		
		for(int i=0;i<noInst;i++){
			fetch[i]=0;
			read[i]=0;
			issue[i]=0;
			execute[i]=0;
			write[i]=0;
			RAW[i]="N";
			WAR[i]="N";
			WAW[i]="N";
			STRUCT[i]="N";
		}
		
		scan.close();
	}
}
