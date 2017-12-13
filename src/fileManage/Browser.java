package fileManage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Browser extends User {
	public Browser(String name, String passwd, String role){
		setName(name);
		setPassword(passwd);
		setRole(role);
	}
	
	public void showMenu(){
		new UI_Browser(this);
		//back.menu3.setVisible(false);
		//back.item9.setVisible(false);
	}
}







