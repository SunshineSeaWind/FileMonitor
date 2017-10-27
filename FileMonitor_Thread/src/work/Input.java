package work;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


class Input{
	List<Command> commandList=new ArrayList<Command>(); 
	private List<String> InputPath=new ArrayList<String>();
	private List<String> InputTrigger=new ArrayList<String>();
	private List<String> InputTask=new ArrayList<String>();
	public Input(List<Command> commandList){
		this.commandList=commandList;
	}
	public void startInput(){
		Scanner scanner=new Scanner(System.in);
		while(scanner.hasNextLine()){
			String string=scanner.nextLine();
			//run结束程序
			if(string.equals("run")){
				break;
			}
			String regEx = "IF .+ ((modified)|(renamed)|(path-changed)|(size-changed)) THEN ((record-summary)|(record-detail)|(recover))";
			// 编译正则表达式
			Pattern pattern = Pattern.compile(regEx);
			if(pattern.matcher(string).matches()){
				String [] str_1=string.split(" ");
				if(str_1.length!=5){
					System.out.println("输入有误!");
					continue;
				}
				String path=str_1[1];
				String trigger=str_1[2];
				String task=str_1[4];
				FileSafeClass file=new FileSafeClass(path);
				if(!file.exists()){
					System.out.println("输入有误，文件不存在!");
					continue;
				}
				//recover只能与renamed和path-changed使用
				if(task.equals("recover") && (trigger.equals("modified") || trigger.equals("size-changed"))){
					System.out.println("输入错误,触发器与任务不匹配");
					continue;
				}
				//确保没有重复的指令
				if(InputPath.isEmpty()){
					InputPath.add(path);
					InputTrigger.add(trigger);
					InputTask.add(task);
				}
				else{
					for(int i=0;i<InputPath.size();i++){
					if(InputPath.get(i).equals(path) && InputTrigger.get(i).equals(trigger) && InputTask.get(i).equals(task)){
						System.out.println("输入重复!");
						break;
					}
					if(i==InputPath.size()-1){
						InputPath.add(path);
						InputTrigger.add(trigger);
						InputTask.add(task);
						break;
					}
				}
				}
			}
			else{
				System.out.println("输入有误!");
			}
		}
		//获取Command命令列表
		for(int i=0;i<InputPath.size();i++){
			FileSafeClass file=new FileSafeClass(InputPath.get(i));
			int [][] array=new int[4][3];
			int first_index=InputTrigger.get(i).equals("renamed") ? 0 : InputTrigger.get(i).equals("modified") ? 1 :  InputTrigger.get(i).equals("path-changed") ? 2 :  3;
			int second_index=InputTask.get(i).equals("record-summary") ? 0 :  InputTask.get(i).equals("record-detail") ? 1 : 2;
			array[first_index][second_index]=1;
			for(int j=i+1;j<InputPath.size();j++){
				//若有目录或者文件是同一个名字，则将命令合并
				if(InputPath.get(j).equals(InputPath.get(i))){
					first_index=InputTrigger.get(j).equals("renamed") ? 0 : InputTrigger.get(j).equals("modified") ? 1 :  InputTrigger.get(j).equals("path-changed") ? 2 :  3;
					second_index=InputTask.get(j).equals("record-summary") ? 0 :  InputTask.get(j).equals("record-detail") ? 1 : 2;
					array[first_index][second_index]=1;
					InputPath.remove(j);
					InputTrigger.remove(j);
					InputTask.remove(j);
					j--;
				}
			}
			Command command=new Command(file, array);
			commandList.add(command);
		}
	}
}
