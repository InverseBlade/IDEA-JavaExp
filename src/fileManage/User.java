package fileManage;
import Server.Doc;
import common.Request;
import common.Response;

import java.net.Socket;
import java.io.*;

public abstract class User implements Serializable {
	private String name;
	private String password;
	private String role;
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
		byte[] buffer = new byte[buffer_space];
		int rLength;
		File ft;
        FileOutputStream fos=null;
        DataInputStream dis;
		
		try{
		    this.oos.writeObject(new Request("downloadFile",ID));
            if(((Response)this.ois.readObject()).isIfRun()){
                ft = new File(ftName);
                fos = new FileOutputStream(ft);
                dis = new DataInputStream(this.s.getInputStream());
                Long fLength = dis.readLong();
                System.out.println(fLength);
                Long count=0L;

                while((rLength=dis.read(buffer))!=-1){
                    fos.write(buffer,0,rLength);
                    fos.flush();
                    count += rLength;
                    if(count >= fLength)
                        break;
                }
                System.out.println("Done!!");

                this.oos.writeObject(new Request(null,"download is ok"));
                fos.close();
                return true;
            }
		}catch(Exception err){
			System.out.println("下载出错:"+err);
		}
		return false;
	}
	
	public boolean changeSelfInfo(String password){
		try{
			this.oos.writeObject(new Request("updateUser",getName()+"|"+password+"|"+getRole()));
			if(((Response)this.ois.readObject()).isIfRun()){
			    return true;
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