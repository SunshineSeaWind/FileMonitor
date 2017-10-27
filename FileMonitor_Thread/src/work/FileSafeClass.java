package work;

import java.io.File;


class FileSafeClass extends File{
	public FileSafeClass(String pathname){
		super(pathname);
	} 
	public synchronized long length(){
		return super.length();
	}
	public synchronized long lastModified(){
		return super.lastModified();
	}
	public synchronized String getAbsolutePath(){
		return super.getAbsolutePath();
	}
	public synchronized String getParent(){
		return super.getParent();
	}
	public synchronized FileSafeClass[] listFiles(){
		File [] files=super.listFiles();
		FileSafeClass [] safefiles = new FileSafeClass[files.length];
		for(int i=0;i<files.length;i++){
			safefiles[i]=new FileSafeClass(files[i].getAbsolutePath());
		}
		return safefiles;
	}
	public synchronized boolean renameTo(FileSafeClass safefile){
		return super.renameTo(safefile);
	}
}
