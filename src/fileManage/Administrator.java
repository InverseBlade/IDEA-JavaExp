package fileManage;
import common.Request;
import common.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

public class Administrator extends User {
	public Administrator(String name, String passwd, String role){
		setName(name);
		setPassword(passwd);
		setRole(role);
	}
	
	public boolean changeUserInfo(String name, String passwd, String role){
        try{
            oos.writeObject(new Request("updateUser",name+"|"+passwd+"|"+role));
            if(((Response)ois.readObject()).isIfRun()){
                return true;
            }
        }catch (Exception err){
            err.printStackTrace();
        }
        return false;
	}
	
	public boolean delUser(String name){
		try{
		    oos.writeObject(new Request("deleteUser",name));
		    if(((Response)ois.readObject()).isIfRun()){
		        return true;
            }
		}catch(Exception err){
			err.printStackTrace();
		}
		return false;
	}
	
	public boolean addUser(String name, String passwd, String role){
		try{
			oos.writeObject(new Request("addUser",name+"|"+passwd+"|"+role));
			if(((Response)ois.readObject()).isIfRun()){
			    return true;
            }
		}catch(Exception err){
		    err.printStackTrace();
		}
		return false;
	}
	
	public String[][] listUser(){
		try{
            String[][] data;

            this.oos.writeObject(new Request("getUserList",null));
            Response res = (Response)this.ois.readObject();

			data = (String[][])res.getObjRes();
            return data;
		}catch(Exception err){
		    err.printStackTrace();
			return null;
		}
	}
	
	public void showMenu(){
		new UI_Administrator(this);
	}
}
