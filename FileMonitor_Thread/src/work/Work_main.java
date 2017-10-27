package work;

import java.util.ArrayList;
import java.util.List;

public class Work_main {
	public static void main(String[] args) {
		try {
			List<Command> commandList=new ArrayList<Command>(); 
			record_summary summary=new record_summary();
			record_detail detail=new record_detail();
			Input input=new Input(commandList);
			input.startInput();
			//此时Command命令已经加载到commandList中
			for(int i=0;i<commandList.size();i++){
				Command command=commandList.get(i);
				if(command.getFile().isFile()){
					FileThread fileThread=new FileThread(command, summary, detail);
					fileThread.start();
				}
				else if(command.getFile().isDirectory()){
					DirThread dirThread=new DirThread(command, summary, detail);
					dirThread.start();
				}
			}
			//主线程休眠，以便监控线程获取目录或者文件信息
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("请进行操作");
			//如果要写测试代码，请在此处书写
		} catch (Exception e) {
			e.getMessage();
		}
	}

}