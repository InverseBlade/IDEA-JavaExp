package fileManage;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

import java.io.*;

import common.*;

public class Login extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	PrintStream out = System.out;
	User user;
	
	JLabel title = new JLabel("档案管理系统");
	JLabel lb1 = new JLabel("用户名: ");
	JLabel lb2 = new JLabel("  密码: ");
	JTextField text1 = new JTextField();
	JPasswordField text2 = new JPasswordField();
	JButton bt1 = new JButton("登录");
	JPanel pad_form = new JPanel();
	JPanel pad1 = new JPanel();
	JPanel pad2 = new JPanel();
	JPanel pad3 = new JPanel();
	JPanel pad_title = new JPanel();

	//Socket通信变量
	Socket s;
    ObjectInputStream ois;
    ObjectOutputStream oos;

	//用户登录逻辑处理
	private void userLogin(String name, String password) {
		try{
			if(text1.getText().equals("")){
				text1.requestFocus();
				JOptionPane.showMessageDialog(null, "用户名不能为空!", "登录失败", JOptionPane.WARNING_MESSAGE);
			}else if(new String(text2.getPassword()).equals("")){
				text2.requestFocus();
				JOptionPane.showMessageDialog(null, "密码不能为空!", "登录失败", JOptionPane.WARNING_MESSAGE);
			}else{
                oos.writeObject(new Request("login",name+"|"+password));

                Response res=null;
                User user;
				if((res=(Response)ois.readObject()).isIfRun()){
					text1.requestFocus();
					this.dispose();

					user = (User)res.getObjRes();
                    user.setS(s);
                    user.setOis(ois);
                    user.setOos(oos);
                    user.showMenu();
				}else{
					JOptionPane.showMessageDialog(null, "用户名或密码错误!", "登录失败", JOptionPane.WARNING_MESSAGE);
				}
			}
		}catch(IllegalStateException err){
			JOptionPane.showMessageDialog(null, "登录错误: "+err.toString()+" 请稍后重试...", "登录失败", JOptionPane.ERROR_MESSAGE);
		}catch(Exception err){
		    err.printStackTrace();
			JOptionPane.showMessageDialog(null, "程序异常退出登录!", "提示", JOptionPane.OK_CANCEL_OPTION);
			this.setVisible(true);
		}
	}

	//程序入口
	public static void main(String[] args) {
		/*try{
			Class.forName("fileManage.DataProcessing");
		}catch(Exception e){
			e.printStackTrace();
		}*/

		new Login();
	}
	
	//登录按钮事件响应
	public void actionPerformed(ActionEvent arg0){
		if(arg0.getSource()==bt1 || arg0.getSource()==text2){
			userLogin(text1.getText(),new String(text2.getPassword()));
			text2.setText("");
		}
	}
	//UI设计代码
	public Login() {
		super("登录界面");
		Color bg_color = new Color(235,242,249);
		
		this.setLayout(null);
		this.setBounds((1890-538)/2, (900-416)/2, 538, 416);
		this.getContentPane().setBackground(bg_color);
		pad_title.setBackground(new Color(0x337ab7));
		pad1.setBackground(bg_color);
		pad2.setBackground(bg_color);
		pad_form.setBackground(bg_color);
		pad3.setBackground(bg_color);
		pad3.setLayout(new FlowLayout(FlowLayout.CENTER,0,15));
		pad_title.setBounds(0, 0, this.getWidth(), this.getHeight()*52/100);
		pad_title.setLayout(new FlowLayout(FlowLayout.CENTER,0,70));
		pad_title.add(title);
		pad1.add(lb1);
		pad1.add(text1);
		pad2.add(lb2);
		pad2.add(text2);
		pad_form.setLayout(new GridLayout(2,1,0,0));
		pad_form.setBounds(0, pad_title.getHeight()+10, this.getWidth(), (int)(this.getHeight()*23f/100));
		pad_form.add(pad1);pad_form.add(pad2);
		pad3.setBounds(0, pad_title.getHeight()+pad_form.getHeight(), this.getWidth(), this.getHeight()-pad_title.getHeight()-pad_form.getHeight());
		pad3.add(bt1);
		
		this.add(pad_title);
		this.add(pad_form);
		this.add(pad3);
		
		title.setFont(new Font("宋体",Font.BOLD,60));
		title.setForeground(Color.white);
		Font font1 = new Font("黑体",Font.PLAIN,23);
		lb1.setFont(new Font("黑体",Font.PLAIN,21));
		lb2.setFont(new Font("黑体",Font.PLAIN,21));
		text1.setFont(font1);
		text2.setFont(font1);
		bt1.setFont(font1);
		text1.setColumns(15);
		text2.setColumns(15);
		text2.setEchoChar('●');
		
		bt1.addActionListener(this);
		text2.addActionListener(this);

        //与服务器交互
        try{
            String serv_ip =null;
            serv_ip = JOptionPane.showInputDialog("输入服务器IP(不填则使用默认IP):");
            if(serv_ip == null)
                System.exit(0);
            if(serv_ip.equals("")){
                serv_ip = "120.79.58.235";
            }
            s=new Socket(serv_ip,2017);

            ois = new ObjectInputStream(s.getInputStream());
            oos = new ObjectOutputStream(s.getOutputStream());

            Response res = (Response)ois.readObject();
            if(!res.isIfRun()){
                JOptionPane.showInternalConfirmDialog(null,"连不上服务器!");
                System.exit(0);
            }
        }catch (Exception err){
            err.printStackTrace();
            JOptionPane.showMessageDialog(null,"无法连接至服务器!","错误",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        this.setResizable(false);
        this.setVisible(true);

        //未登录前退出处理
        User user = new User() {
            @Override
            public void showMenu() {
                ;
            }
        };
        user.setS(s);
        user.setOis(ois);
        user.setOos(oos);
        this.addWindowListener(new DefaultWindowListener(user, this));
	}

}
