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
	
	public boolean uploadFile(String fsName, String descr, DownloadDialog progress){
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
		        Long count=0L;
		        double complete=0.;
		        String temp=null;
		        while((rLength=fis.read(buffer))!=-1){
		            dos.write(buffer,0,rLength);
		            dos.flush();
		            count += rLength;
                    if(progress!=null){
                        complete = (1.0*count/fs.length()*100);
                        temp = String.valueOf(complete);
                        progress.jl2.setText("已上传"+temp.substring(0,temp.indexOf(".")+2)+"%");
                        progress.jp.setValue((int)complete);
                    }
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
