package work;

import java.io.FileWriter;
import java.io.IOException;

class Test{
	public synchronized boolean rename(FileSafeClass file,FileSafeClass rnameFile){
		if(!file.exists()){
			System.out.println("文件不存在!");
			return false;
		}
		if(!file.renameTo(rnameFile)){
			System.out.println("重命名未完成!");
			return false;
		}
		System.out.println("重命名完成!");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	//在监控范围内的路径
	public synchronized boolean moveFile(FileSafeClass file,String nowPath){
		FileSafeClass rnameFile=new FileSafeClass(nowPath+"/"+file.getName());
		if(!file.exists()){
			System.out.println("文件不存在!");
			return false;
		}
		if(!file.renameTo(rnameFile)){
			System.out.println("移动未完成!");
			return false;
		}
		System.out.println("移动完成!");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	public synchronized boolean deleteFile(FileSafeClass file) {
		//判断文件是否存在 
	    if(file.exists()) {
	    	if(file.isFile()) {//判断是否是文件 
	    		file.delete();		//删除文件   
	    	} 
	    	else if(file.isDirectory()) {			//否则如果它是一个目录  
	    		FileSafeClass[] files = file.listFiles();			//声明目录下所有的文件 files[];  
	    		for (int i = 0;i < files.length;i ++) {			//遍历目录下所有的文件  
	    			deleteFile(files[i]);	//把每个文件用这个方法进行迭代  
	    		}  
	    		file.delete();				//删除空文件夹  
	     }  
	    	System.out.println("删除文件成功!");
	    	 try {
	 			Thread.sleep(1000);
	 		} catch (InterruptedException e) {
	 			e.printStackTrace();
	 		}
	    	return true;
	    } 
	    System.out.println("所删除的文件不存在!");  
	    return false;
	    }  
	public synchronized boolean createFile(FileSafeClass file) {
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("新建文件成功!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		else{
			System.out.println("新建文件已经存在!");
			return false;
		}
	}
	public synchronized boolean createDir(FileSafeClass file) {
		if(!file.exists()){
			file.mkdirs();
			System.out.println("新建文件夹成功!");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		else{
			System.out.println("新建文件夹已经存在!");
			return false;
		}
	}
	//向文件中重写内容以改变文件大小
	public synchronized boolean changeSize(FileSafeClass file,String content){
		if(!file.exists()){
			System.out.println("要写入内容的文件不存在");
			return false;
		}
		FileWriter fw;
		try {
			fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("写入文件成功!");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}
	public synchronized long getSize(FileSafeClass file){
		return file.length();
	}
	public synchronized long getlastModifiedTime(FileSafeClass file){
		return file.lastModified();
	}
	public synchronized String getName(FileSafeClass file){
		return file.getAbsolutePath();
	}
}  