package jdumper.stat;
import jpcap.packet.*;
import java.util.*;

import jdumper.JDPacketAnalyzerLoader;
import jdumper.analyzer.JDPacketAnalyzer;

public class ApplicationProtocolStat extends JDStatisticsTaker
{
	List<JDPacketAnalyzer> analyzers;
	long[] numOfPs;
	long[] sizeOfPs;
	long totalPs,totalSize;
	String[] labels;
	static final String[] types={"# of packets","% of packets","total packet size","% of size"};
	
	public ApplicationProtocolStat(){
		analyzers=JDPacketAnalyzerLoader.getAnalyzersOf(JDPacketAnalyzer.APPLICATION_LAYER);
		numOfPs=new long[analyzers.size()+1];
		sizeOfPs=new long[analyzers.size()+1];

		labels=new String[analyzers.size()+1];
		for(int i=0;i<analyzers.size();i++)
			labels[i]=analyzers.get(i).getProtocolName();
		labels[analyzers.size()]="Other";
	}
	
	public String getName(){
		return "Application Layer Protocol Ratio";
	}
	
	public void analyze(List<Packet> packets){
		for(Packet p:packets){
			totalPs++;			
			boolean flag=false;
			for(int j=0;j<analyzers.size();j++)
				if(analyzers.get(j).isAnalyzable(p)){
					numOfPs[j]++;
					sizeOfPs[j]+=((IPPacket)p).length;
					totalSize+=((IPPacket)p).length;
					flag=true;
					break;
				}
			if(!flag){
				numOfPs[numOfPs.length-1]++;
				sizeOfPs[sizeOfPs.length-1]+=p.len-12;//减去12个字节
				totalSize+=p.len-12;
			}
		}
	}
	
	public void addPacket(Packet p){
		boolean flag=false;
		totalPs++;
		for(int j=0;j<analyzers.size();j++)
			if(analyzers.get(j).isAnalyzable(p)){
				numOfPs[j]++;
				sizeOfPs[j]+=((IPPacket)p).length;
				totalSize+=((IPPacket)p).length;
				flag=true;
				break;
			}
		if(!flag){//其他类型的包
			numOfPs[numOfPs.length-1]++;
			sizeOfPs[sizeOfPs.length-1]+=p.len-12;
			totalSize+=p.len-12;
		}
	}
	
	public String[] getLabels(){
		return labels;
	}
	
	public String[] getStatTypes(){
		return types;
	}
	
	public long[] getValues(int index){
		switch(index){
			case 0: //# of packets
				if(numOfPs==null) return new long[0];
				return numOfPs;
			case 1: //% of packets
				long[] percents=new long[numOfPs.length];
				if(totalPs==0) return percents;
				for(int i=0;i<numOfPs.length;i++)
					percents[i]=numOfPs[i]*100/totalPs;
				return percents;
			case 2: //total packet size
				if(sizeOfPs==null) return new long[0];
				return sizeOfPs;
			case 3: //% of size
				long[] percents2=new long[sizeOfPs.length];
				if(totalSize==0) return percents2;
				for(int i=0;i<sizeOfPs.length;i++)
					percents2[i]=sizeOfPs[i]*100/totalSize;
				return percents2;
			default:
				return null;
		}
	}

	public void clear(){
		numOfPs=new long[analyzers.size()+1];
		sizeOfPs=new long[analyzers.size()+1];
		totalPs=0;
		totalSize=0;
	}
}
