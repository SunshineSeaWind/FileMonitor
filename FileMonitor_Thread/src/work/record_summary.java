package work;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//记录总体
class record_summary {
	private String path="record_summary.txt";
	private FileSafeClass file=new FileSafeClass(path);
	private int renamedTimes=0;
	private int modifiedTimes=0;
	private int path_changedTimes=0;
	private int size_changedTimes=0;
	private BufferedWriter bw;
	public record_summary(){
		try {
			bw=new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized void add(String string){
		try {
			bw.write(string);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public synchronized int addrenamedTimes(){
		return ++renamedTimes;
	}
	public synchronized int addmodifiedTimes(){
		return ++modifiedTimes;
	}
	public synchronized int addpath_changedTimes(){
		return ++path_changedTimes;
	}
	public synchronized int addsize_changedTimes(){
		return ++size_changedTimes;
	}
}
