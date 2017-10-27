package work;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//监视文件线程
class FileThread extends Thread{
	private List<FileSafeClass> arrayfiles=new ArrayList<>();
	private	record_summary summary;
	private record_detail detail;
	private FileSafeClass file;
	private int [][]array=new int [4][3];
	private String parentPath;
	private long lastChangeTime;
	private long size;
	private String absolutePath;
	private String name;
	public FileThread(Command command,record_summary summary,record_detail detail){
		this.file=command.getFile();
		this.array=command.getArray();
		this.summary=summary;
		this.detail=detail;
		this.parentPath=this.file.getParent();
		this.lastChangeTime=this.file.lastModified();
		this.size=this.file.length();
		this.absolutePath=this.file.getAbsolutePath();
		this.name=this.file.getName();
	}
	//文件重命名的触发器,重命名只改变了文件名字，所以有些变量不用修改
	public synchronized void renamed(){
		if(!file.exists()){
			FileSafeClass parentFile=new FileSafeClass(parentPath);
			FileSafeClass [] files=parentFile.listFiles();
			for(int i=0;i<files.length;i++){
				if(files[i].isFile()){
					//满足条件，监测到变化
					if(files[i].lastModified()==lastChangeTime && files[i].length()==size){
						System.out.println("renamed:\t"+absolutePath+"\t->\t"+files[i].getAbsolutePath()+System.getProperty("line.separator"));
						//rename的summary操作
						if(array[0][0]==1){
							String string="renamed发生次数:"+summary.addrenamedTimes()+System.getProperty("line.separator");
							summary.add(string);
						}
						//rename的detail操作
						if(array[0][1]==1){
							String string="renamed:\t"+absolutePath+"\t->\t"+files[i].getAbsolutePath()+System.getProperty("line.separator");
							detail.add(string);
						}
						//rename的recover操作
						if(array[0][2]==1){
							FileSafeClass preFile=new FileSafeClass(absolutePath);
							files[i].renameTo(preFile);
						}
						//更新到新文件的属性
						if(array[0][2]!=1){
							file=files[i];
							absolutePath=file.getAbsolutePath();
							name=file.getName();
							break;
						}
					}
				}
			}
		}
	}
	public synchronized void modified(){
		if(file.exists()){
			//若发生了修改
			if(file.lastModified()!=lastChangeTime){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar calender = Calendar.getInstance();
				calender.setTimeInMillis(lastChangeTime);
				String preTime=sdf.format(calender.getTime());
				calender.setTimeInMillis(file.lastModified());
				String nowTime=sdf.format(calender.getTime());
				//modified的summary操作
				if(array[1][0]==1){
					String string="modified发生次数:"+summary.addmodifiedTimes()+System.getProperty("line.separator");
					summary.add(string);
				}
				//modified的detail操作
				if(array[1][1]==1){
					String string="modified:\t"+preTime+"\t->\t"+nowTime+System.getProperty("line.separator");
					detail.add(string);
				}
				System.out.println("modified:\t"+preTime+"\t->\t"+nowTime+System.getProperty("line.separator"));
				//更新到最新状态
				lastChangeTime=file.lastModified();
			}
			}
		}
	public synchronized void path_changed(){
		if(!file.exists()){
			FileSafeClass parentFile=new FileSafeClass(parentPath);
			findAllFile(parentFile);
			//此时arrayfiles已经是拥有parentFile下所有文件的列表了
			for(int i=0;i<arrayfiles.size();i++){
				FileSafeClass tempFlie=arrayfiles.get(i);
				if(tempFlie.length()==size && tempFlie.getName().equals(name) && tempFlie.lastModified()==lastChangeTime){
					//path_changed的summary操作
					if(array[2][0]==1){
						String string="path_changed发生次数:"+summary.addpath_changedTimes()+System.getProperty("line.separator");
						summary.add(string);
					}
					//path_changed的detail操作
					if(array[2][1]==1){
						String string="path_changed:\t"+absolutePath+"\t->\t"+tempFlie.getAbsolutePath()+System.getProperty("line.separator");
						detail.add(string);
					}
					//path_changed的recover操作
					if(array[2][2]==1){
						FileSafeClass preFile=new FileSafeClass(absolutePath);
						tempFlie.renameTo(preFile);
					}
					System.out.println("path_changed:\t"+absolutePath+"\t->\t"+tempFlie.getAbsolutePath()+System.getProperty("line.separator"));
					//更新到最新状态
					if(array[2][2]!=1){
						parentPath=tempFlie.getParent();
						absolutePath=tempFlie.getAbsolutePath();
						file=tempFlie;
						break;
					}
				}
			}
			//清空arrayfiles
			arrayfiles.clear();
		}
	}
	public synchronized void size_changed(){
		if(file.exists()){
			//文件大小变化
			if(file.length()!=size){
				//size_changed的summary操作
				if(array[3][0]==1){
					String string="size_changed发生次数:"+summary.addsize_changedTimes()+System.getProperty("line.separator");
					summary.add(string);
				}
				//size_changed的detail操作
				if(array[3][1]==1){
					String string="size_changed:\t"+size+"\t->\t"+file.length()+System.getProperty("line.separator");
					detail.add(string);
				}
				System.out.println("size_changed:\t"+size+"\t->\t"+file.length()+System.getProperty("line.separator"));
				//更新到最新状态
				size=file.length();
			}
		}
	}
	private synchronized void findAllFile(FileSafeClass parentFile){
      if(parentFile!=null){
          if(parentFile.isDirectory()){
              FileSafeClass[] fileArray=parentFile.listFiles();
              if(fileArray!=null){
                  for (int i = 0; i < fileArray.length; i++) {
                      //递归调用
                  	findAllFile(fileArray[i]);
                  }
              }
          }
          else{
              arrayfiles.add(parentFile);
          }
      }
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
}