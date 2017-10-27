package work;

class fileInfo{
	private long size;
	private long lastChangeTime;
	private String path;
	private String name;
	public fileInfo(long size,long lastChangeTime,String path,String name){
		this.size=size;
		this.lastChangeTime=lastChangeTime;
		this.path=path;
		this.name=name;
	}
	public long getSize() {
		return size;
	}
	public long getLastChangeTime() {
		return lastChangeTime;
	}
	public String getPath(){
		return path;
	}
	public String getName(){
		return name;
	}
}
