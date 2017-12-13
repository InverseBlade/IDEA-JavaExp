package fileManage;
import common.Request;
import common.Response;

import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

public abstract class User implements Serializable {
	private String name;
	private String password;
	private String role;
	protected final String serve_path = "UploadFile\\";
	protected Socket s;
	protected ObjectOutputStream oos;
	protected ObjectInputStream ois;

	public User(){
	    setS(null);
	    setOos(null);
	    setOis(null);
    }

    public void setS(Socket s) {
        this.s = s;
    }

    public void setOos(ObjectOutputStream oos) {
        this.oos = oos;
    }

    public void setOis(ObjectInputStream ois) {
        this.ois = ois;
    }

    public Socket getS() {
        return s;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public String getName(){
		return name;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getRole(){
		return role;
	}
	
	public boolean setName(String name){
		if(name!=null&&!name.equals("")){
			name.trim();
			this.name=name;
			return true;
		}else{
			return false;
		}
	}
	
	public boolean setPassword(String password){
		if(password!=null&&!password.equals("")){
			this.password=password;
			return true;
		}else{
			return false;
		}
	}
	
	public boolean setRole(String role){
		if(role!=null&&!role.equals("")){
			this.role=role;
			return true;
		}else{
			return false;
		}
	}
	public abstract void showMenu();
	
	public String[][] showFileList() {
        String[][] row=null;

		try{
		    int size;

		    oos.writeObject(new Request("getFileList",null));
            Response res = (Response)ois.readObject();

            row = (String[][])res.getObjRes();
		}catch(IllegalStateException err){
			System.out.println("获得文档列表失败: "+err.toString());
			return null;
		}catch(Exception err){
		    err.printStackTrace();
        }
		return row;
	}
	
	public boolean downloadFile(String ID, String ftName){
		Doc doc;
		
		final int buffer_space = 1024*1024;
		byte[] buffer = new byte[buffer_space], buf = new byte[1024];
		//double degree=0;int t=0;
		File fs, ft;
		//long init;
		byte temp;
		
		try{
			if((doc=DataProcessing.searchDoc(ID))!=null){
				/*
				if(doc.getFilename().indexOf('\\')!=-1){
					ftName += "\\" + doc.getFilename().substring(doc.getFilename().lastIndexOf('\\')+1);
				}else{
					ftName += "\\" + doc.getFilename();*/
				
				fs = new File(serve_path + doc.getFilename());
				ft = new File(ftName);
				FileInputStream fis = new FileInputStream(fs);
				FileOutputStream fos = new FileOutputStream(ft);
				
				/*init = fis.available();
				while(fis.available() > buffer_space){
					fis.read(buffer);
					fos.write(buffer);
					degree = 1.0d*(init-fis.available())/init;
					if(degree >= 0.9 && t==3){
						System.out.println("已下载"+(int)(degree*1000)/10.f+"%...");
						t=4;fos.flush();
					}else if(degree >= 0.7 && t == 2){
						System.out.println("已下载"+(int)(degree*1000)/10.f+"%...");
						t=3;fos.flush();
					}else if(degree >= 0.5 && t==1){
						System.out.println("已下载"+(int)(degree*1000)/10.f+"%...");
						t=2;fos.flush();
					}else if(degree >= 0.2 && t == 0){
						System.out.println("已下载"+(int)(degree*1000)/10.f+"%...");
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
				while((temp=(byte)fis.read())!=-1){
					fos.write(temp);
				}
				
				fis.close();
				fos.close();
			}else{
				return false;
			}
		}catch(Exception err){
			System.out.println("下载出错:"+err);
			return false;
		}
		return true;
	}
	
	public boolean changeSelfInfo(String password){
		try{
			if(this.setPassword(password.trim())){
				return DataProcessing.updateUser(this.name, password, this.role);
			}else{
				return false;
			}
		}catch(Exception err){
			return false;
		}
	}
	
	public void exitSystem(){
        try{
            //很重要!!!:退出前向服务器发送连接关闭消息!!!
            this.oos.writeObject(new Request("logout",null));
            if(!((Response)this.ois.readObject()).isIfRun()){
                return;
            }

            if(this.ois!=null)
                this.ois.close();
            if(this.oos!=null)
                this.oos.close();
            if(this.s!=null)
                this.s.close();
        }catch (Exception err){
            err.printStackTrace();
        }
		System.exit(0);
	}
}