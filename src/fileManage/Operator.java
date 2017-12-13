package fileManage;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.*;
import java.io.*;

public class Operator extends User{
	public Operator(String name, String passwd, String role){
		setName(name);
		setPassword(passwd);
		setRole(role);
	}
	
	public boolean uploadFile(String fsName, String descr){
		final int buffer_space = 1024*1024;
		String fileName;
		File fs, ft;
		byte[] buffer = new byte[buffer_space], buf = new byte[1024];
		//double degree=0;int t=0;
		//long init;
		int temp;
		
		if(!(fs=new File(fsName)).exists()){
			System.out.println("文件不存在!");
			return false;
		}
		/*System.out.println("请输入拷贝至服务器的路径(以\\结束):");
		fileName = in.next();
		if(fileName.equals("\\")){
			fileName = "";
		}
		fileName += fsName.substring(fsName.lastIndexOf('\\')+1);*/
		fileName = fsName.substring(fsName.lastIndexOf('\\')+1);
		try{
		    int i=0;
			Enumeration<Doc> docs = DataProcessing.getAllDocs();
			while(docs.hasMoreElements()){
				if(docs.nextElement().getFilename().equals(fileName)){
					System.out.println("文件名与已上传文件冲突,上传终止!");
					return false;
				}
				i++;
			}

			if(!DataProcessing.insertDoc(this.getName(), descr, fileName)){
				System.out.println("文件名重复,上传终止!");
				return false;
			};
			ft = new File(serve_path + fileName);
			FileInputStream fis = new FileInputStream(fs);
			FileOutputStream fos = new FileOutputStream(ft);
			
			/*System.out.println("上传开始...");
			init = fis.available();
			while(fis.available() > buffer_space){
				fis.read(buffer);
				fos.write(buffer);
				degree = 1.0d*(init-fis.available())/init;
				if(degree >= 0.9 && t==3){
					System.out.println("已上传"+(int)(degree*1000)/10.d+"%...");
					t=4;fos.flush();
				}else if(degree >= 0.7 && t==2){
					System.out.println("已上传"+(int)(degree*1000)/10.d+"%...");
					t=3;fos.flush();
				}else if(degree >= 0.5 && t==1){
					System.out.println("已上传"+(int)(degree*1000)/10.d+"%...");
					t=2;fos.flush();
				}else if(degree >= 0.2 && t == 0){
					System.out.println("已上传"+(int)(degree*1000)/10.d+"%...");
					t=1;fos.flush();
				}
			}*/
			while(fis.available() > buffer_space){
				fis.read(buffer);
				fos.write(buffer);
			}
			while(fis.available() > 1024){
				fis.read(buf);
				fos.write(buf);
			}
			while((temp=fis.read())!=-1){
				fos.write(temp);
			}
			fis.close();
			fos.close();
		}catch(Exception err){
			System.out.println("上传失败,未知错误:"+err);
			return false;
		}
		return true;
	}
	
	public void showMenu(){
		new UI_Operator(this);
		//back.menu3.setVisible(false);
	}
}
