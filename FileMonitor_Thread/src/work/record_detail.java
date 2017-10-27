package work;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//记录细节
class record_detail{
	String path="record_detail.txt";
	FileSafeClass file=new FileSafeClass(path);
	private BufferedWriter bw;
	public record_detail(){
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
}