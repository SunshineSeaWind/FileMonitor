package work;

//记录命令的数据结构
class Command{
	private FileSafeClass file;
	private int [][]array=new int [4][3];
	public Command(FileSafeClass file,int [][]array){
		this.file=file;
		this.array=array;
	}
	public FileSafeClass getFile() {
		return file;
	}
	public int[][] getArray() {
		return array;
	}
	
}