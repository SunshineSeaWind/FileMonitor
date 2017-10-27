package work;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class DirThread extends Thread{
	private	record_summary summary;
	private record_detail detail;
	private HashMap<String,fileInfo> currentDirMap=new HashMap<String, fileInfo>();
	private FileSafeClass file;
	private int [][]array=new int [4][3];
	public DirThread(Command command,record_summary summary,record_detail detail){
		//此处的file是一个文件夹(目录)
		this.file=command.getFile();
		this.array=command.getArray();
		this.summary=summary;
		this.detail=detail;
		//构成最开始的目录的快照
		getDirHashMap(this.file, currentDirMap);
	}
	public void run(){
		try {
			while(true){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				renamed();
				modified();
				path_changed();
				size_changed();
			
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	//监控线程是文件夹，但是只能对文件rename
	public synchronized void renamed(){
		//先获取当前目录的快照
		HashMap<String,fileInfo> nowDirMap=new HashMap<String, fileInfo>();
		getDirHashMap(file, nowDirMap);
		//遍历两个快照，比较不同
		Iterator iter_current = currentDirMap.keySet().iterator();
		Iterator iter_now = nowDirMap.keySet().iterator();
		List<fileInfo> list_current=new ArrayList<fileInfo>();
		List<fileInfo> list_now=new ArrayList<fileInfo>();
		while (iter_current.hasNext()) {
			Object key = iter_current.next();
			if(nowDirMap.get(key)==null){
				list_current.add(currentDirMap.get(key));
			}
		}
		while(iter_now.hasNext()) {
			Object key = iter_now.next();
			if(currentDirMap.get(key)==null){
				list_now.add(nowDirMap.get(key));
			}
		}
		if(!list_current.isEmpty() && !list_now.isEmpty()){
			for(int i=0;i<list_current.size();i++){
				for(int j=0;j<list_now.size();j++){
					fileInfo info_current=list_current.get(i);
					fileInfo info_now=list_now.get(j);
					if(!info_current.getPath().equals(info_now.getPath()) && !info_current.getName().equals(info_now.getName())
							&& info_current.getLastChangeTime()==info_now.getLastChangeTime() && info_current.getSize()==info_now.getSize()){
						//rename的summary操作
						if(array[0][0]==1){
							String string="renamed发生次数:"+summary.addrenamedTimes()+System.getProperty("line.separator");
							summary.add(string);
						}
						//rename的detail操作
						if(array[0][1]==1){
							String string="renamed:\t"+info_current.getPath()+"\t->\t"+info_now.getPath()+System.getProperty("line.separator");
							detail.add(string);
						}
						//rename的recover操作
						if(array[0][2]==1){
							FileSafeClass preFile=new FileSafeClass(info_current.getPath());
							FileSafeClass nowFile=new FileSafeClass(info_now.getPath());
							nowFile.renameTo(preFile);
						}
						System.out.println("renamed:\t"+info_current.getPath()+"\t->\t"+info_now.getPath()+System.getProperty("line.separator"));
						list_now.remove(info_now);
						list_current.remove(info_current);
						i--;
						break;
					}
				}
			}
			//将快照更新到最新状态
//			currentDirMap.clear();
//			currentDirMap=nowDirMap;
//			nowDirMap=new HashMap<String,fileInfo>();
//			nowDirMap.clear();
		}
	}
	public synchronized void modified(){
				//先获取当前目录的快照
				HashMap<String,fileInfo> nowDirMap=new HashMap<String, fileInfo>();
				getDirHashMap(file, nowDirMap);
				//遍历两个快照，比较不同
				Iterator iter_current = currentDirMap.keySet().iterator();
				List<fileInfo> list_current=new ArrayList<fileInfo>();
				List<fileInfo> list_now=new ArrayList<fileInfo>();
				while (iter_current.hasNext()) {
					Object key = iter_current.next();
					if(nowDirMap.get(key)!=null &&  currentDirMap.get(key)!=null && 
							nowDirMap.get(key).getLastChangeTime()!=currentDirMap.get(key).getLastChangeTime()){
						list_current.add(currentDirMap.get(key));
						list_now.add(nowDirMap.get(key));
					}
				}
				if(!list_current.isEmpty() && !list_now.isEmpty()){
					for(int i=0;i<list_current.size();i++){
						fileInfo info_current=list_current.get(i);
						fileInfo info_now=list_now.get(i);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Calendar calender = Calendar.getInstance();
						calender.setTimeInMillis(info_current.getLastChangeTime());
						String preTime=sdf.format(calender.getTime());
						calender.setTimeInMillis(info_now.getLastChangeTime());
						String nowTime=sdf.format(calender.getTime());
						//modified的summary操作
						if(array[1][0]==1){
							String string="modified发生次数:"+summary.addmodifiedTimes()+System.getProperty("line.separator");
							summary.add(string);
						}
						//modified的detail操作
						if(array[1][1]==1){
							String string="modified:\t"+info_current.getPath()+"\t"+preTime+"\t->\t"+info_now.getPath()+"\t"+nowTime+System.getProperty("line.separator");
							detail.add(string);
						}
						System.out.println("modified:\t"+info_current.getPath()+"\t"+preTime+"\t->\t"+info_now.getPath()+"\t"+nowTime+System.getProperty("line.separator"));
					}
					//将快照更新到最新状态
//					currentDirMap.clear();
//					currentDirMap=nowDirMap;
//					nowDirMap=new HashMap<String,fileInfo>();
//					nowDirMap.clear();
				}
	}
	public synchronized void path_changed(){
				//先获取当前目录的快照
				HashMap<String,fileInfo> nowDirMap=new HashMap<String, fileInfo>();
				getDirHashMap(file, nowDirMap);
				//遍历两个快照，比较不同
				Iterator iter_current = currentDirMap.keySet().iterator();
				Iterator iter_now = nowDirMap.keySet().iterator();
				List<fileInfo> list_current=new ArrayList<fileInfo>();
				List<fileInfo> list_now=new ArrayList<fileInfo>();
				while (iter_current.hasNext()) {
					Object key = iter_current.next();
					if(nowDirMap.get(key)==null){
						list_current.add(currentDirMap.get(key));
					}
				}
				while(iter_now.hasNext()) {
					Object key = iter_now.next();
					if(currentDirMap.get(key)==null){
						list_now.add(nowDirMap.get(key));
					}
				}
				if(!list_current.isEmpty() && !list_now.isEmpty()){
					for(int i=0;i<list_current.size();i++){
						for(int j=0;j<list_now.size();j++){
							fileInfo info_current=list_current.get(i);
							fileInfo info_now=list_now.get(j);
							if(!info_current.getPath().equals(info_now.getPath()) && info_current.getName().equals(info_now.getName()) 
									&& info_current.getLastChangeTime()==info_now.getLastChangeTime() && info_current.getSize()==info_now.getSize()){
								//path_changed的summary操作
								if(array[2][0]==1){
									String string="path_changed发生次数:"+summary.addpath_changedTimes()+System.getProperty("line.separator");
									summary.add(string);
								}
								//path_changed的detail操作
								if(array[2][1]==1){
									String string="path_changed:\t"+info_current.getPath()+"\t->\t"+info_now.getPath()+System.getProperty("line.separator");
									detail.add(string);
								}
								//path_changed的recover操作
								if(array[2][2]==1){
									FileSafeClass preFile=new FileSafeClass(info_current.getPath());
									FileSafeClass nowFile=new FileSafeClass(info_now.getPath());
									nowFile.renameTo(preFile);
								}
								System.out.println("path_changed:\t"+info_current.getPath()+"\t->\t"+info_now.getPath()+System.getProperty("line.separator"));
								list_now.remove(info_now);
								list_current.remove(info_current);
								i--;
								break;
							}
						}
					}
					//将快照更新到最新状态
//					currentDirMap.clear();
//					currentDirMap=nowDirMap;
//					nowDirMap=new HashMap<String,fileInfo>();
//					nowDirMap.clear();
				}
	}
	public synchronized void size_changed(){
				//先获取当前目录的快照
				HashMap<String,fileInfo> nowDirMap=new HashMap<String, fileInfo>();
				getDirHashMap(file, nowDirMap);
				//遍历两个快照，比较不同
				Iterator iter_current = currentDirMap.keySet().iterator();
				List<fileInfo> list_current=new ArrayList<fileInfo>();
				List<fileInfo> list_now=new ArrayList<fileInfo>();
				while (iter_current.hasNext()) {
					Object key = iter_current.next();
					if(nowDirMap.get(key)!=null &&  currentDirMap.get(key)!=null && nowDirMap.get(key).getSize()!=currentDirMap.get(key).getSize()){
						list_current.add(currentDirMap.get(key));
						list_now.add(nowDirMap.get(key));
					}
				}
				if(!list_current.isEmpty() && !list_now.isEmpty()){
					for(int i=0;i<list_current.size();i++){
						fileInfo info_current=list_current.get(i);
						fileInfo info_now=list_now.get(i);
						//size_changed的summary操作
						if(array[3][0]==1){
							String string="size_changed发生次数:"+summary.addsize_changedTimes()+System.getProperty("line.separator");
							summary.add(string);
						}
						//size_changed的detail操作
						if(array[3][1]==1){
							String string="size_changed:\t"+info_current.getPath()+"\t"+info_current.getSize()+"\t->\t"+
						info_now.getPath()+"\t"+info_now.getSize()+System.getProperty("line.separator");
							detail.add(string);
						}
						System.out.println("size_changed:\t"+info_current.getPath()+"\t"+info_current.getSize()+"\t->\t"+info_now.getPath()+"\t"+info_now.getSize()+System.getProperty("line.separator"));
					}
				}
				//将快照更新到最新状态
				currentDirMap.clear();
				currentDirMap=nowDirMap;
				nowDirMap=new HashMap<String,fileInfo>();
				nowDirMap.clear();
	}
	private synchronized void getDirHashMap(FileSafeClass file,HashMap<String,fileInfo> map){
        if(file!=null){
            if(file.isDirectory()){
            	//为文件夹添加到HashMap
            	long lastChangeTime=file.lastModified();
            	long size=getDirSize(file);
            	String path=file.getAbsolutePath();
            	String name=file.getName();
            	fileInfo fileinfo=new fileInfo(size,lastChangeTime,path,name);
            	map.put(file.getAbsolutePath(), fileinfo);
                FileSafeClass[] fileArray=file.listFiles();
                if(fileArray!=null){
                    for (int i = 0; i < fileArray.length; i++) {
                        //递归调用
                    	getDirHashMap(fileArray[i],map);
                    }
                }
            }
            else{
            	long lastChangeTime=file.lastModified();
            	long size=file.length();
            	String path=file.getAbsolutePath();
            	String name=file.getName();
            	fileInfo fileinfo=new fileInfo(size,lastChangeTime,path,name);
                map.put(file.getAbsolutePath(), fileinfo);
            }
        }
    }
	private synchronized long getDirSize(FileSafeClass file){
		long size=0;
		 if(file.isDirectory()){
			 FileSafeClass[] files=file.listFiles();
         	for(int i=0;i<files.length;i++){
         		if(files[i].isFile()){
         			size+=files[i].length();
         		}
         	}
		 }
		 return size;
	}
}
