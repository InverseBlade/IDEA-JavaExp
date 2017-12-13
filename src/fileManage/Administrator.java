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
			if(DataProcessing.updateUser(name, passwd, role)){
				return true;
			}else{
				return false;
			}
		}catch(SQLException err){
			return false;
		}catch(IllegalStateException err){
			return false;
		}
	}
	
	public boolean delUser(String name){
		try{
			if(DataProcessing.deleteUser(name)){
				return true;
			}else{
				return false;
			}
		}catch(SQLException err){
			System.out.println("删除失败: "+err.toString());
			return false;
		}catch(IllegalStateException err){
			System.out.println("删除失败: "+err.toString());
			return false;
		}
	}
	
	public boolean addUser(String name, String passwd, String role){
		try{
			if(DataProcessing.insertUser(name,passwd,role)){
				return true;
			}else{
				return false;
			}
		}catch(SQLException err){
			System.out.println("添加失败: "+err.toString());
			return false;
		}catch(IllegalStateException err){
			System.out.println("添加失败: "+err.toString());
			return false;
		}
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
		//back.item9.setVisible(false);
		//back.menu3.setVisible(true);
	}
}
