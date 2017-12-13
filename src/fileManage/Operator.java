package fileManage;

import common.Request;
import common.Response;

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
		File fs;
		byte[] buffer = new byte[buffer_space];
		FileInputStream fis;
		DataOutputStream dos;
		
		if(!(fs=new File(fsName)).exists()){
			System.out.println("文件不存在!");
			return false;
		}

		fileName = fsName.substring(fsName.lastIndexOf('\\')+1);
		try{
		    oos.writeObject(new Request("uploadFile",getName()+"|"+descr+"|"+fileName));
		    if(((Response)ois.readObject()).isIfRun()){
		        fis = new FileInputStream(fs);
		        dos = new DataOutputStream(s.getOutputStream());

		        dos.writeLong(fs.length());
		        int rLength;
		        while((rLength=fis.read(buffer))!=-1){
		            dos.write(buffer,0,rLength);
		            dos.flush();
                }
                if(((Response) ois.readObject()).isIfRun()){
		            fis.close();
		            return true;
                }
            }
		}catch(Exception err){
			System.out.println("上传失败,未知错误:"+err);
		}
		return false;
	}
	
	public void showMenu(){
		new UI_Operator(this);
	}
}
