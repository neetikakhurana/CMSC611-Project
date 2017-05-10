package com.nitika.cache;

import com.nitika.constants.ApplicationConstants;
import com.nitika.main.Simulator;
import com.nitika.parsers.InstParser;
import com.nitika.scoreboard.CalcScoreboard;

/**
 * returns 0 if its a hit, 1 if it is a miss and 2 if it needs to wait it out
 * @author neeti
 *
 */
public class Dcache {

	public static int dataAccessRequests=0;
	public static int dataHits=0;
	public static int dataMiss=0;
	public static int delay=-1;
	public static boolean setByMe=false;
	public static int[] orderOfAccess0=new int[100];
	public static int[] orderOfAccess1=new int[100];
	public static int index0=0;
	public static int index1=0;
	public static boolean reduceLatency=false;
	public static int dependency=0;
	/*
	 * within variable checks if the MMInUse is set by this cache itself
	 */
	public static boolean within=false;
	public static int dCache[][]=new int[4][4]; //size of cache given
	/*
	 * Will only be called if the isntructions are LD, SD , SW, or LW
	 */
	public static int dataInDcache(int instNo){
		if(dataAccessRequests==0){
			for(int i=0;i<4;i++){
				for(int j=0;j<4;j++){
					dCache[i][j]=-1;
				}
			}
			for(int i=0;i<100;i++){
				orderOfAccess0[i]=-1;
				orderOfAccess1[i]=-1;
			}
		}
		if(delay==-1 && instNo==0)
		{
			delay=12;
		}
		else if(delay==-1 && instNo!=0)
		{
			delay=12;
		}
		int blockAddress, setOffset, blockNo, blockOffset,loc=0,x,y=0;
		
		for(x=0;x<Icache.checkLast.length;x++)
		{
			if(Icache.checkLast[x]!=0)
			{
				y=x;
				if(Icache.checkLast[y]==CalcScoreboard.fetchControl-1){
					dependency++;
					break;
				}
		}
			}
		
		//if the instruction is of type load
		//value to be stored 
		 //location of the word within a block
		//there are two blocks in each set with 4 words each
		if(Simulator.memory[instNo][1].contains(ApplicationConstants.LD) || Simulator.memory[instNo][1].equalsIgnoreCase(ApplicationConstants.LW))
		{
			String source=InstParser.getLDSource(instNo);
			String indexedAdd[]=new String[2];
			indexedAdd=Simulator.memory[instNo][3].split("\\(");
			loc=Integer.parseInt(indexedAdd[0])+Simulator.registers.get(source);
		}
		/*
		 * STORE INSTRUCTION
		 */
		else if(Simulator.memory[instNo][1].contains(ApplicationConstants.SD) || Simulator.memory[instNo][1].contains(ApplicationConstants.SW))
		{
			String source=InstParser.getStoreDest(instNo);
			String[] indexedAdd=new String[2];
			indexedAdd=Simulator.memory[instNo][3].split("\\(");
			loc=Integer.parseInt(indexedAdd[0])+Simulator.registers.get(source);
			
		}
			//calculate the set
			blockAddress=loc/16;
			setOffset=blockAddress%2;
			//calculate to which offset in a block the word will go
			blockNo=loc/4;
			blockOffset=blockNo%4;
			
			/**
			 * Now, if the instruction is of type LD or SD, it will access two words instead 1 and both the address referred by these and the word next to it should be present in the cache
			 * Else, it will be a miss if either one is missing
			 */
			
			if(Simulator.memory[instNo][1].contains(ApplicationConstants.LW) || Simulator.memory[instNo][1].contains(ApplicationConstants.SW)){
				//the word should go according to the set offset
				//if set is 1, it would lie in block 2 and 3 else for 0, it will be either 0 or 1
				if(setOffset==1){
					
					boolean flag=false;
					for(int i=2;i<4;i++){
						//if both blocks are free, then it is a data cache miss
						//check if mm is free to get the data
						if(dCache[2][blockOffset]==-1 && dCache[3][blockOffset]==-1 && (!Icache.MMinUse || within)){
							Icache.MMinUse=true;
							setByMe=true;
							dataMiss++;
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							within=true;
							//if both blocks are free, put it in the first block by default
							if(delay==-1){
								int k=0;
							for(int j=blockOffset;j<4;j++){
								//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
								if(Simulator.memoryData.containsKey(loc+k))
								{
									dCache[i][j]=loc+k;
								}
								else
								{
									dCache[i][j]=-1;
								}
								k+=4;
							}
							k=4;
							//instructions before the current one
							for(int j=blockOffset-1;j>=0;j--){
								if(Simulator.memoryData.containsKey(loc-k))
								{
									dCache[i][j]=loc-k;
								}
								else
								{
									dCache[i][j]=-1;
								}
								k+=4;
							}
							orderOfAccess0[index0++]=i;
							within=false;
							dataAccessRequests++;
							return 0;
						}
							flag=true;
							break;
						}
						//if the data already exists
						else if(dCache[i][blockOffset]==loc){
								dataHits++;
								orderOfAccess0[index0++]=i;
								//it's a hit
								flag=true;
								within=false;
								dataAccessRequests++;
								return 0;
						}
						//check in the other block as well
							else
							{
								flag=false;
								continue;
							}
					}
					
					
					//if the flag is not set, that means it was not found, and we need to use LRU to get that block
					/*
					 * LRU TECHNIQUE
					 */
					if(!flag && (!Icache.MMinUse || within) ){
						dataMiss++;
						if(dependency!=0){
							delay--;
							dependency=0;
						}
						delay--;
						within=true;
						Icache.MMinUse=true;
						if(delay==-1){
	
						//1 block was least recently used
						if(orderOfAccess0[index0-1]==2){ //what if index0 is 0
							
							//if both blocks are free, put it in the first block by default
							int k=0;
							for(int j=blockOffset;j<4;j++){
								//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
								if(Simulator.memoryData.containsKey(loc+k))
								{
									dCache[3][j]=loc+k;
								}
								else
								{
									dCache[3][j]=-1;
								}
								k+=4;
							}
							k=4;
							//instructions before the current one
							for(int j=blockOffset-1;j>=0;j--){
								if(Simulator.memoryData.containsKey(loc-k))
								{
									dCache[3][j]=loc-k;
								}
								else
								{
									dCache[3][j]=-1;
								}
								k+=4;
							}
							orderOfAccess0[index0++]=3;
						}
						else{
							//block 0 was least recently used
							//if both blocks are free, put it in the first block by default
							int k=0;
							for(int j=blockOffset;j<4;j++){
								//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
								if(Simulator.memoryData.containsKey(loc+k))
								{
									dCache[2][j]=loc+k;
								}
								else
								{
									dCache[2][j]=-1;
								}
								k+=4;
							}
							k=4;
							//instructions before the current one
							for(int j=blockOffset-1;j>=0;j--){
								if(Simulator.memoryData.containsKey(loc-k))
								{
									dCache[2][j]=loc-k;
								}
								else
								{
									dCache[2][j]=-1;
								}
								k+=4;
							}
							orderOfAccess0[index0++]=2;
						}
					}
					}
					else{
						//main memory in use
						return 2;
					}
					if(delay==-1){
						within=false;
						dataAccessRequests++;
						return 0;
					}
					else{
						return 2;
					}
					
				}
				else if(setOffset==0)
				{
					boolean flag=false;
					for(int i=0;i<2;i++)
					{
						
						//if both blocks are free, then it is a data cache miss
						if(dCache[0][blockOffset]==-1 && dCache[1][blockOffset]==-1 && (!Icache.MMinUse || within))
						{
							dataMiss++;
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							within=true;
							Icache.MMinUse=true;
							if(delay==-1){
							//if both blocks are free, put it in the first block by default
							int k=0;
							for(int j=blockOffset;j<4;j++){
								//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
								if(Simulator.memoryData.containsKey(loc+k))
								{
									dCache[i][j]=loc+k;
								}
								else
								{
									dCache[i][j]=-1;
								}
								k+=4;
							}
							k=4;
							//instructions before the current one
							for(int j=blockOffset-1;j>=0;j--){
								if(Simulator.memoryData.containsKey(loc-k))
								{
									dCache[i][j]=loc-k;
								}
								else
								{
									dCache[i][j]=-1;
								}
								k+=4;
							}
							orderOfAccess1[index1++]=i;
							within=false;
							dataAccessRequests++;
							return 0;
							}
							flag=true;
							break;
						}
						else{
							//check if this exists in any block
							if(dCache[i][blockOffset]==loc){
								dataHits++;
								orderOfAccess1[index1++]=i;
								//it's a hit
								flag=true;
								within=false;
								dataAccessRequests++;
								return 0;
							}
							else
							{
								if(!flag){flag=false;}
								continue;
							}
						}
					}
					
					
					//if the flag is not set, that means it was not found, and we need to use LRU to get that block
					/*
					 * LRU TECHNIQUE
					 */
					if(!flag && (!Icache.MMinUse || within)){
						dataMiss++;
						if(dependency!=0){
							delay--;
							dependency=0;
						}
						delay--;
						within=true;
						Icache.MMinUse=true;
						//1 block was least recently used
						if(delay==-1){
						if(orderOfAccess1[index1-1]==1){
							
							//if both blocks are free, put it in the first block by default
							int k=0;
							for(int j=blockOffset;j<4;j++){
								//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
								if(Simulator.memoryData.containsKey(loc+k))
								{
									dCache[0][j]=loc+k;
								}
								else
								{
									dCache[0][j]=-1;
								}
								k+=4;
							}
							k=4;
							//instructions before the current one
							for(int j=blockOffset-1;j>=0;j--){
								if(Simulator.memoryData.containsKey(loc-k))
								{
									dCache[0][j]=loc-k;
								}
								else
								{
									dCache[0][j]=-1;
								}
								k+=4;
							}
							orderOfAccess1[index1++]=0;
						}
						else{
							//block 1 was least recently used
							//if both blocks are free, put it in the first block by default
							int k=0;
							for(int j=blockOffset;j<4;j++){
								//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
								if(Simulator.memoryData.containsKey(loc+k))
								{
									dCache[1][j]=loc+k;
								}
								else
								{
									dCache[1][j]=-1;
								}
								k+=4;
							}
							k=4;
							//instructions before the current one
							for(int j=blockOffset-1;j>=0;j--){
								if(Simulator.memoryData.containsKey(loc-k))
								{
									dCache[1][j]=loc-k;
								}
								else
								{
									dCache[1][j]=-1;
								}
								k+=4;
							}
							orderOfAccess1[index1++]=1;
						}
						}
					}
					else{
						//main memory used by i cache
						return 2;
					}
					if(delay==-1){
						within=false;
						dataAccessRequests++;
						return 0;
					}
					else{
						return 2;
					}
				
				}
				return 0;
			}
			else{
				/**
				 * ##################################the instruction is of type LD or SD, access two words############################################
				 */
				if(setOffset==1){
					/*********************************SET 1 OFFSET IS NOT THE LAST ONE FOR LD/SD*******************************************************
					 * if the offset is 3, that means the first word is present at the last location of a block and next word will be fetched from next block
					 * If not, that means that they lie in the same block and hence the same algo as above can be applied
					 * Also, we need to check if the set of the next word is same as the first one or not
					 */
					if(blockOffset!=3){
						
						boolean flag=false;
						for(int i=2;i<4;i++){
							//if both blocks are free, then it is a data cache miss
							//check if mm is free to get the data
							if(dCache[2][blockOffset]==-1 && dCache[3][blockOffset]==-1 && (!Icache.MMinUse || within)){
								Icache.MMinUse=true;
								dataMiss++;
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								within=true;
								//if both blocks are free, put it in the first block by default
								if(delay==-1){
									int k=0;
								for(int j=blockOffset;j<4;j++){
									//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
									if(Simulator.memoryData.containsKey(loc+k))
									{
										dCache[i][j]=loc+k;
									}
									else
									{
										dCache[i][j]=-1;
									}
									k+=4;
								}
								k=4;
								//instructions before the current one
								for(int j=blockOffset-1;j>=0;j--){
									if(Simulator.memoryData.containsKey(loc-k))
									{
										dCache[i][j]=loc-k;
									}
									else
									{
										dCache[i][j]=-1;
									}
									k+=4;
								}
								orderOfAccess0[index0++]=i;
								within=false;
								dataAccessRequests++;
								return 0;
							}
								flag=true;
								break;
							}
							//if the data already exists
							else if(dCache[i][blockOffset]==loc){
									dataHits++;
									orderOfAccess0[index0++]=i;
									//it's a hit
									flag=true;
									within=false;
									dataAccessRequests++;
									return 0;
							}
							//check in the other block as well
								else
								{
									if(!flag){
									flag=false;}
									continue;
								}
						}
						
						
						//if the flag is not set, that means it was not found, and we need to use LRU to get that block
						/*
						 * LRU TECHNIQUE
						 */
						if(!flag && (!Icache.MMinUse || within) ){
							dataMiss++;
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							within=true;
							Icache.MMinUse=true;
							setByMe=true;
							if(delay==-1){
		
							//1 block was least recently used
							if(orderOfAccess0[index0-1]==2){ //what if index0 is 0
								
								//if both blocks are free, put it in the first block by default
								int k=0;
								for(int j=blockOffset;j<4;j++){
									//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
									if(Simulator.memoryData.containsKey(loc+k))
									{
										dCache[3][j]=loc+k;
									}
									else
									{
										dCache[3][j]=-1;
									}
									k+=4;
								}
								k=4;
								//instructions before the current one
								for(int j=blockOffset-1;j>=0;j--){
									if(Simulator.memoryData.containsKey(loc-k))
									{
										dCache[3][j]=loc-k;
									}
									else
									{
										dCache[3][j]=-1;
									}
									k+=4;
								}
								orderOfAccess0[index0++]=3;
							}
							else{
								//block 0 was least recently used
								//if both blocks are free, put it in the first block by default
								int k=0;
								for(int j=blockOffset;j<4;j++){
									//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
									if(Simulator.memoryData.containsKey(loc+k))
									{
										dCache[2][j]=loc+k;
									}
									else
									{
										dCache[2][j]=-1;
									}
									k+=4;
								}
								k=4;
								//instructions before the current one
								for(int j=blockOffset-1;j>=0;j--){
									if(Simulator.memoryData.containsKey(loc-k))
									{
										dCache[2][j]=loc-k;
									}
									else
									{
										dCache[2][j]=-1;
									}
									k+=4;
								}
								orderOfAccess0[index0++]=2;
							}
						}
						}
						else{
							//main memory in use
							return 2;
						}
						if(delay==-1){
							within=false;
							dataAccessRequests++;
							return 0;
						}
						else{
							return 2;
						}
					}
				
					/*****************************************SET 1 OFFSET LAST FOR LD/SD**********************************************************
					 * the offset of the word is last, check the next block for data, i.e. offset=3
					 * Only if the offset of first word is 3, we need to check for the set no of the next word
					 */
					else{
						
						int blockAddress1=(loc+4)/16;
						int setOffset1=blockAddress1%2;
/**
 * ####################################################################################################################################################################	
 */
						//set nos of both the words is different!!!!!!!!!!!!
						if(setOffset1!=1){
							
							int j=0;
							//************************checking if the cache is empty when both belong different set************************************
							boolean empty2=true;
							for(int m=2;m<4;m++){
								for(int n=0;n<4;n++){
									if(dCache[m][n]!=-1){
										empty2=false;
										break;
									}
								}
							}
							boolean empty1=true;
							for(int m=0;m<2;m++){
								for(int n=0;n<4;n++){
									if(dCache[m][n]!=-1){
										empty1=false;
										break;
									}
								}
							}
							//******************************************************************************************
							boolean flag1=false;
							boolean flag2=false;
							if(empty1 && empty2 && (!Icache.MMinUse || within)){
								//the two blocks are empty, so we need to fetch both the words
								flag1=false;
								flag2=false;
							}
							
							for(int i=2;i<4;i++)
							{
								
								if(dCache[i][blockOffset]==loc){
										flag1=true;
										//the first word is present
										orderOfAccess0[index0++]=i;
										if(dCache[j][0]==loc+4){
											//second word is also present
											orderOfAccess1[index1++]=j;
											//it's a hit
											dataHits++;
											flag2=true;
											within=false;
											dataAccessRequests++;
											return 0;
										}
										else{
											//first word is present but second is not, so fetch it
											if(!flag2){flag2=false;}
											j++;
											continue;
										}
								}
								else{
									if(!flag1){
									flag1=false;}
									//first word is not present
									if(dCache[j][0]==loc+4)
									{
											//second word is present
											orderOfAccess1[index1++]=j;
											//it's a hit
											flag2=true;
											within=false;
											dataAccessRequests++;
											return 0;
									}
									else{
												//both are not present
												if(!flag1 && !flag2){flag1=false;
												flag2=false;}
												j++;
												continue;
									}
									
								}
							}
							
							
							//***********************apply LRU
							//both blocks are not empty but both of them are not present
							if(!flag1 && !flag2 && (!Icache.MMinUse || within)){
								Icache.MMinUse=true;
								within=true;
								if(delay==-1){
									delay=24;
								}
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								if(delay==-1){
									fetchWord(loc,empty1,blockOffset);
									fetchWord0(loc+4,empty2,0);
									Icache.MMinUse=false;
									within=false;
									dataAccessRequests++;
									return 0;
								}
								else{
									return 2;
								}
							}
							else if(!flag1 && (!Icache.MMinUse || within)){
								//just fetch the first word
								Icache.MMinUse=true;
								
								within=true;
								if(delay==-1){
									delay=12;
								}
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								if(delay==-1){
									dataHits++;

									reduceLatency=true;
									fetchWord(loc,empty1,blockOffset);
									Icache.MMinUse=false;
									within=false;
									dataAccessRequests++;
									return 0;
								}
								else{
									return 2;
								}
							}
							else if(!flag2 && (!Icache.MMinUse || within)){
								//fetch the second word
								Icache.MMinUse=true;
								within=true;
								if(delay==-1){
									delay=12;
								}
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								if(delay==-1){
									dataHits++;

									reduceLatency=true;
									fetchWord0(loc+4,empty2,0);
									Icache.MMinUse=false;
									within=false;
									dataAccessRequests++;
									return 0;
								}
								else{
									return 2;
								}
							}
							else{
								//MM in use by I cache
								return 2;
							}
							
							
							
						}
						
						
/**
 * #####################################################################################################################################################################
 */
						
						int j=0;
						//************************checking if the cache is empty when both belong to the same set************************************
						boolean empty=true;
						for(int m=2;m<4;m++){
							for(int n=0;n<4;n++){
								if(dCache[m][n]!=-1){
									empty=false;
									break;
								}
							}
						}
						//******************************************************************************************
						boolean flag1=false;
						boolean flag2=false;
						if(empty && (!Icache.MMinUse || within)){
							//the two blocks are empty, so we need to fetch both the words
							flag1=false;
							flag2=false;
						}
						for(int i=2;i<4;i++)
						{
							if(i==2){
								j=3;
							}
							else{
								j=2;
							}
							
							if(dCache[i][blockOffset]==loc){
									flag1=true;
									//the first word is present
									orderOfAccess0[index0++]=i;
									if(dCache[j][0]==loc+4){
										//second word is also present
										orderOfAccess0[index0++]=j;
										dataHits++;
										//it's a hit
										flag2=true;
										within=false;
										dataAccessRequests++;
										return 0;
									}
									else{
										//first word is present but second is not, so fetch it
										if(!flag2){flag2=false;}
										continue;
									}
							}
							else{
								if(!flag1){
								flag1=false;}
								//first word is not present
								if(dCache[j][0]==loc+4)
								{
										//second word is present
										orderOfAccess0[index0++]=j;
										//it's a hit
										flag2=true;
										within=false;
										dataAccessRequests++;
										return 0;
								}
								else{
											//both are not present
											if(!flag1 && !flag2){flag1=false;
											flag2=false;}
											continue;
								}
								
							}
						}
						
						
						//***********************apply LRU
						//both blocks are not empty but both of them are not present
						if(!flag1 && !flag2 && (!Icache.MMinUse || within)){
							Icache.MMinUse=true;
							within=true;
							if(delay==-1){
								delay=24;
							}
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							if(delay==-1){

								fetchWord(loc,empty,blockOffset);
								fetchWord(loc+4,empty,0);
								Icache.MMinUse=false;
								within=false;
								dataAccessRequests++;
								return 0;
							}
							else{
								return 2;
							}
						}
						else if(!flag1 && (!Icache.MMinUse || within)){
							//just fetch the first word
							Icache.MMinUse=true;
							within=true;
							if(delay==-1){
								delay=12;
							}
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							if(delay==-1){
								dataHits++;

								reduceLatency=true;
								fetchWord(loc,empty,blockOffset);
								Icache.MMinUse=false;
								within=false;
								dataAccessRequests++;
								return 0;
							}
							else{
								return 2;
							}
						}
						else if(!flag2 && (!Icache.MMinUse || within)){
							//fetch the second word
							Icache.MMinUse=true;
							within=true;
							if(delay==-1){
								delay=12;
							}
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							if(delay==-1){
								dataHits++;

								reduceLatency=true;
								fetchWord(loc+4,empty,0);
								Icache.MMinUse=false;
								within=false;
								dataAccessRequests++;
								return 0;
							}
							else{
								return 2;
							}
						}
						else{
							//MM in use by I cache
							return 2;
						}
					
					}
						
					//return 0;
				}
				else{
					/**********************************************SET 0 OFFSET NOT LAST FOR LD/SD******************************************************
					 * if the offset is 3, that means the first word is present at the last location of a block and next word will be fetched from next block
					 * If not, that means that they lie in the same block and hence the same algo as above can be applied
					 */
					if(blockOffset!=3){
						
						boolean flag=false;
						for(int i=0;i<2;i++){
							//if both blocks are free, then it is a data cache miss
							//check if mm is free to get the data
							if(dCache[0][blockOffset]==-1 && dCache[1][blockOffset]==-1 && (!Icache.MMinUse || within)){
								Icache.MMinUse=true;
								setByMe=true;
								dataMiss++;
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								within=true;
								//if both blocks are free, put it in the first block by default
								if(delay==-1){
									int k=0;
								for(int j=blockOffset;j<4;j++){
									//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
									if(Simulator.memoryData.containsKey(loc+k))
									{
										dCache[i][j]=loc+k;
									}
									else
									{
										dCache[i][j]=-1;
									}
									k+=4;
								}
								k=4;
								//instructions before the current one
								for(int j=blockOffset-1;j>=0;j--){
									if(Simulator.memoryData.containsKey(loc-k))
									{
										dCache[i][j]=loc-k;
									}
									else
									{
										dCache[i][j]=-1;
									}
									k+=4;
								}
								orderOfAccess1[index1++]=i;
								within=false;
								dataAccessRequests++;
								return 0;
							}
								flag=true;
								break;
							}
							//if the data already exists
							else if(dCache[i][blockOffset]==loc){
									dataHits++;
									orderOfAccess1[index1++]=i;
									//it's a hit
									flag=true;
									within=false;
									dataAccessRequests++;
									return 0;
							}
							//check in the other block as well
								else
								{
									if(!flag){
									flag=false;
									}
									continue;
								}
						}
						
						
						//if the flag is not set, that means it was not found, and we need to use LRU to get that block
						/*
						 * LRU TECHNIQUE
						 */
						if(!flag && (!Icache.MMinUse || within) ){
							dataMiss++;
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							within=true;
							Icache.MMinUse=true;
							setByMe=true;
							if(delay==-1){
								dataAccessRequests++;

							//1 block was least recently used
							if(orderOfAccess1[index1-1]==0){ //what if index0 is 0
								
								//if both blocks are free, put it in the first block by default
								int k=0;
								for(int j=blockOffset;j<4;j++){
									//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
									if(Simulator.memoryData.containsKey(loc+k))
									{
										dCache[1][j]=loc+k;
									}
									else
									{
										dCache[1][j]=-1;
									}
									k+=4;
								}
								k=4;
								//instructions before the current one
								for(int j=blockOffset-1;j>=0;j--){
									if(Simulator.memoryData.containsKey(loc-k))
									{
										dCache[1][j]=loc-k;
									}
									else
									{
										dCache[1][j]=-1;
									}
									k+=4;
								}
								orderOfAccess1[index1++]=1;
							}
							else{
								//block 0 was least recently used
								//if both blocks are free, put it in the first block by default
								int k=0;
								for(int j=blockOffset;j<4;j++){
									//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
									if(Simulator.memoryData.containsKey(loc+k))
									{
										dCache[0][j]=loc+k;
									}
									else
									{
										dCache[0][j]=-1;
									}
									k+=4;
								}
								k=4;
								//instructions before the current one
								for(int j=blockOffset-1;j>=0;j--){
									if(Simulator.memoryData.containsKey(loc-k))
									{
										dCache[0][j]=loc-k;
									}
									else
									{
										dCache[0][j]=-1;
									}
									k+=4;
								}
								orderOfAccess1[index1++]=0;
							}
						}
						}
						else{
							//main memory in use
							return 2;
						}
						if(delay==-1){
							within=false;
							dataAccessRequests++;
							return 0;
						}
						else{
							return 2;
						}
					}
				
					/*********************************************SET 0 FOR LAST OFFSET FOR LD/SD*************************************************
					 * the offset of the word is last, check the next block for data, i.e. offset=3
					 */
					else{
						
						int blockAddress1=(loc+4)/16;
						int setOffset1=blockAddress1%2;
/**
 * ####################################################################################################################################################################	
 */
						//set nos of both the words is different!!!!!!!!!!!!
						if(setOffset1!=0){
							
							int j=2;
							//************************checking if the cache is empty when both belong different set************************************
							boolean empty2=true;
							for(int m=2;m<4;m++){
								for(int n=0;n<4;n++){
									if(dCache[m][n]!=-1){
										empty2=false;
										break;
									}
								}
							}
							boolean empty1=true;
							for(int m=0;m<2;m++){
								for(int n=0;n<4;n++){
									if(dCache[m][n]!=-1){
										empty1=false;
										break;
									}
								}
							}
							//******************************************************************************************
							boolean flag1=false;
							boolean flag2=false;
							if(empty1 && empty2 && (!Icache.MMinUse || within)){
								//the two blocks are empty, so we need to fetch both the words
								flag1=false;
								flag2=false;
							}
							
							for(int i=0;i<2;i++)
							{
								
								if(dCache[i][blockOffset]==loc){
										flag1=true;
										//the first word is present
										orderOfAccess1[index1++]=i;
										if(dCache[j][0]==loc+4){
											//second word is also present
											orderOfAccess0[index0++]=j;
											dataHits++;
											//it's a hit
											flag2=true;
											within=false;
											dataAccessRequests++;
											return 0;
										}
										else{
											//first word is present but second is not, so fetch it
											if(!flag2){
											flag2=false;}
											j++;
											continue;
										}
								}
								else{
									if(!flag1){
									flag1=false;}
									//first word is not present
									if(dCache[j][0]==loc+4)
									{
											//second word is present
											orderOfAccess0[index0++]=j;
											//it's a hit
											flag2=true;
											within=false;
											dataAccessRequests++;
											return 0;
									}
									else{
												//both are not present
											if(!flag1 && !flag2){	
											flag1=false;
												flag2=false;
											}
												j++;
												continue;
									}
									
								}
							}
							
							
							//***********************apply LRU
							//both blocks are not empty but both of them are not present
							if(!flag1 && !flag2 && (!Icache.MMinUse || within)){
								Icache.MMinUse=true;
								within=true;
								if(delay==-1){
								delay=24;
								}
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								if(delay==-1){
									fetchWord0(loc,empty1,blockOffset);
									fetchWord(loc+4,empty2,0);
									Icache.MMinUse=false;
									within=false;
									dataAccessRequests++;
									return 0;
								}
								else{
									return 2;
								}
							}
							else if(!flag1 && (!Icache.MMinUse || within)){
								//just fetch the first word
								Icache.MMinUse=true;
								within=true;
								if(delay==-1){
								delay=12;
								}
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								if(delay==-1){
									dataHits++;

									reduceLatency=true;
									fetchWord0(loc,empty1,blockOffset);
									Icache.MMinUse=false;
									within=false;
									dataAccessRequests++;
									return 0;
								}
								else{
									return 2;
								}
							}
							else if(!flag2 && (!Icache.MMinUse || within)){
								//fetch the second word
								Icache.MMinUse=true;
								within=true;
								if(delay==-1){
								delay=12;
								}
								if(dependency!=0){
									delay--;
									dependency=0;
								}
								delay--;
								if(delay==-1){
									dataHits++;

									reduceLatency=true;
									fetchWord(loc+4,empty2,0);
									Icache.MMinUse=false;
									within=false;
									dataAccessRequests++;
									return 0;
								}
								else{
									return 2;
								}
							}
							else{
								//MM in use by I cache
								return 2;
							}
							
							
							
						}
						
						
/**
 * #####################################################################################################################################################################
 */
		
						
						
						
						int j=0;
						//************************checking if the cache is empty************************************
						boolean empty=true;
						for(int m=0;m<2;m++){
							for(int n=0;n<4;n++){
								if(dCache[m][n]!=-1){
									empty=false;
								}
							}
						}
						//******************************************************************************************
						boolean flag1=false;
						boolean flag2=false;
						if(empty && (!Icache.MMinUse || within)){
							//the two blocks are empty, so we need to fetch both the words
							flag1=false;
							flag2=false;
						}
						for(int i=0;i<2;i++)
						{
							if(i==0){
								j=1;
							}
							else{
								j=0;
							}
							
							if(dCache[i][blockOffset]==loc){
									flag1=true;
									//the first word is present
									orderOfAccess1[index1++]=i;
									if(dCache[j][0]==loc+4){
										//second word is also present
										orderOfAccess1[index1++]=j;
										dataHits++;
										//it's a hit
										flag2=true;
										within=false;
										dataAccessRequests++;
										return 0;
									}
									else{
										//first word is present but second is not, so fetch it
										if(!flag2){
										flag2=false;}
										continue;
									}
							}
							else{
								if(!flag1){
								flag1=false;}
								//first word is not present
								if(dCache[j][0]==loc+4)
								{
										//second word is present
										orderOfAccess1[index1++]=j;
										//it's a hit
										flag2=true;
										within=false;
										dataAccessRequests++;
										return 0;
								}
								else{
											//both are not present
											if(!flag1 && !flag2){
											flag1=false;
											flag2=false;}
											continue;
								}
								
							}
						}
						
						
						//***********************apply LRU
						//both blocks are not empty but both of them are not present
						if(!flag1 && !flag2 && (!Icache.MMinUse || within)){
							Icache.MMinUse=true;
							within=true;
							if(delay==-1){
							delay=24;
							}
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							if(delay==-1){

								fetchWord0(loc,empty,blockOffset);
								fetchWord0(loc,empty,0);
								Icache.MMinUse=false;
								within=false;
								dataAccessRequests++;
								return 0;
							}
						}
						else if(!flag1 && (!Icache.MMinUse || within)){
							//just fetch the first word
							Icache.MMinUse=true;
							within=true;
							if(delay==-1){
							delay=12;
							}
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							if(delay==-1){
								dataHits++;

								reduceLatency=true;
								fetchWord0(loc,empty,blockOffset);
								Icache.MMinUse=false;
								within=false;
								dataAccessRequests++;
								return 0;
							}
						}
						else if(!flag2 && (!Icache.MMinUse || within)){
							//fetch the second word
							Icache.MMinUse=true;
							within=true;
							if(delay==-1){
							delay=12;
							}
							if(dependency!=0){
								delay--;
								dependency=0;
							}
							delay--;
							if(delay==-1){
								dataHits++;

								reduceLatency=true;
								fetchWord0(loc,empty,0);
								Icache.MMinUse=false;
								within=false;
								dataAccessRequests++;
								return 0;
							}
						}
						else{
							//MM in use by I cache
							return 2;
						}
					
					}
				}
				return 0;
			}
	}
	
	
	
	public static void fetchWord(int loc, boolean empty, int blockOffset){
		if(empty){
			//store in any of them by default, choose the 3rd one
			int k=0;
			for(int j=blockOffset;j<4;j++){
				//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
				if(Simulator.memoryData.containsKey(loc+k))
				{
					dCache[2][j]=loc+k;
				}
				else
				{
					dCache[2][j]=-1;
				}
				k+=4;
			}
			k=4;
			//instructions before the current one
			for(int j=blockOffset-1;j>=0;j--){
				if(Simulator.memoryData.containsKey(loc-k))
				{
					dCache[2][j]=loc-k;
				}
				else
				{
					dCache[2][j]=-1;
				}
				k+=4;
			}
			orderOfAccess0[index0++]=2;
		}
		else{
		if(orderOfAccess0[index0-1]==2){
			//3rd word was recently used so store data in the 4th one
			int k=0;
			for(int j=blockOffset;j<4;j++){
				//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
				if(Simulator.memoryData.containsKey(loc+k))
				{
					dCache[3][j]=loc+k;
				}
				else
				{
					dCache[3][j]=-1;
				}
				k+=4;
			}
			k=4;
			//instructions before the current one
			for(int j=blockOffset-1;j>=0;j--){
				if(Simulator.memoryData.containsKey(loc-k))
				{
					dCache[3][j]=loc-k;
				}
				else
				{
					dCache[3][j]=-1;
				}
				k+=4;
			}
			orderOfAccess0[index0++]=3;
		}
		else{
			//last one was recently used, so store in 3rd one
			int k=0;
			for(int j=blockOffset;j<4;j++){
				//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
				if(Simulator.memoryData.containsKey(loc+k))
				{
					dCache[2][j]=loc+k;
				}
				else
				{
					dCache[2][j]=-1;
				}
				k+=4;
			}
			k=4;
			//instructions before the current one
			for(int j=blockOffset-1;j>=0;j--){
				if(Simulator.memoryData.containsKey(loc-k))
				{
					dCache[2][j]=loc-k;
				}
				else
				{
					dCache[2][j]=-1;
				}
				k+=4;
			}
			orderOfAccess0[index0++]=2;
		}
	}
	}
	
	
	
	//for set 0
	public static void fetchWord0(int loc, boolean empty, int blockOffset){
		if(empty){
			//store in any of them by default, choose the 3rd one
			int k=0;
			for(int j=blockOffset;j<4;j++){
				//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
				if(Simulator.memoryData.containsKey(loc+k))
				{
					dCache[0][j]=loc+k;
				}
				else
				{
					dCache[0][j]=-1;
				}
				k+=4;
			}
			k=4;
			//instructions before the current one
			for(int j=blockOffset-1;j>=0;j--){
				if(Simulator.memoryData.containsKey(loc-k))
				{
					dCache[0][j]=loc-k;
				}
				else
				{
					dCache[0][j]=-1;
				}
				k+=4;
			}
			orderOfAccess1[index1++]=0;
		}
		else{
		if(orderOfAccess1[index1-1]==0){
			//3rd word was recently used so store data in the 4th one
			int k=0;
			for(int j=blockOffset;j<4;j++){
				//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
				if(Simulator.memoryData.containsKey(loc+k))
				{
					dCache[1][j]=loc+k;
				}
				else
				{
					dCache[1][j]=-1;
				}
				k+=4;
			}
			k=4;
			//instructions before the current one
			for(int j=blockOffset-1;j>=0;j--){
				if(Simulator.memoryData.containsKey(loc-k))
				{
					dCache[1][j]=loc-k;
				}
				else
				{
					dCache[1][j]=-1;
				}
				k+=4;
			}
			orderOfAccess1[index1++]=1;
		}
		else{
			//last one was recently used, so store in 3rd one
			int k=0;
			for(int j=blockOffset;j<4;j++){
				//if the required location exists in the data.txt file. If it does, then start populating the data from this location onwards till a block's length
				if(Simulator.memoryData.containsKey(loc+k))
				{
					dCache[0][j]=loc+k;
				}
				else
				{
					dCache[0][j]=-1;
				}
				k+=4;
			}
			k=4;
			//instructions before the current one
			for(int j=blockOffset-1;j>=0;j--){
				if(Simulator.memoryData.containsKey(loc-k))
				{
					dCache[0][j]=loc-k;
				}
				else
				{
					dCache[0][j]=-1;
				}
				k+=4;
			}
			orderOfAccess1[index1++]=0;
		}
	}
	}
}
