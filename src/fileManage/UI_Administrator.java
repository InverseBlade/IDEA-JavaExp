package fileManage;

import java.awt.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;
import javax.swing.table.*;

public class UI_Administrator extends JFrame implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //初始界面大小
    private final int frame_wid = 854;
    private final int frame_hgt = 640;
    //用户变量
    private Administrator admin;
    /////////////
    CardLayout card = new CardLayout();//属于JFrame.contentPanel的卡片布局
    JPanel pad_0 = new JPanel();//欢迎界面
    JPanel pad_1 = new JPanel();//文档列表界面
    JPanel pad_2 = new JPanel();//用户列表界面
    /////////////////////////菜单////////////////////////////
    JMenuBar mb = new JMenuBar();
    JMenu menu1 = new JMenu("文档");
    JMenu menu2 = new JMenu("用户");
    JMenu menu3 = new JMenu("管理员");
    //user
    JMenuItem item1 = new JMenuItem("显示文档列表");
    JMenuItem item2 = new JMenuItem("下载文档");
    JMenuItem item3 = new JMenuItem("退出登录");
    JMenuItem item4 = new JMenuItem("修改密码");
    //administrator
    JMenuItem item5 = new JMenuItem("修改用户");
    JMenuItem item6 = new JMenuItem("删除用户");
    JMenuItem item7 = new JMenuItem("添加用户");
    JMenuItem item8 = new JMenuItem("用户列表");
    //operator

    ////////////////////////////////////////////////////////////
    JPanel pad1 = new JPanel();  //belongs to pad_1
    JPanel pad2 = new JPanel();  //belongs to pad_1
    JPanel pad3 = new JPanel();  //belongs to pad_1

    JPanel pad4 = new JPanel();  //belongs to pad_2
    JPanel pad5 = new JPanel();  //belongs to pad_2
    JPanel pad6 = new JPanel();  //belongs to pad_2

    JButton jb1 = new JButton("下载");
    JButton jb2 = new JButton("删除");
    JButton jb3 = new JButton("添加");
    JButton jb4 = new JButton("修改");
    JLabel jl1 = new JLabel("文档列表");
    JLabel jl2 = new JLabel("用户列表");
    //JTable
    JTable jt1 = new JTable(){
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    JTable jt2 = new JTable(){
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    String[] col_doc_name = {"文档ID","上传者","上传时间","描述","文件名"};
    String[] col_user_name = {"序号","用户名","密码","角色"};

    //Socket通信变量

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if(arg0 == null || arg0.getSource()==jb1){//下载文档按钮事件
            int item = jt1.getSelectedRow();
            if(item != -1){
                FileDialog fileName = new FileDialog(this,"下载文件",FileDialog.SAVE);
                fileName.setDirectory("D:\\");
                fileName.setFile((String)jt1.getModel().getValueAt(item,4));
                fileName.setVisible(true);
                if(fileName.getFile()!=null){
                    if(admin.downloadFile((String)jt1.getValueAt(item, 0), fileName.getDirectory()+fileName.getFile(), null)){
                        JOptionPane.showMessageDialog(null, "下载完毕!", "提示", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "下载失败!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }else if(arg0.getSource()==item3){//退出登录
            if(JOptionPane.showConfirmDialog(null, "确定退出登录?", "注销", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                this.setVisible(false);
                this.admin.exitSystem();
            };
        }else if(arg0.getSource()==item1){//显示文档列表
            this.refreshTableModel(jt1);
            card.show(this.getContentPane(), "文档列表");
        }else if(arg0.getSource()==item4){//修改密码对话框
            String passwd = null;
            if((passwd=this.inputPassword("请输入修改后的密码:","密码修改"))!=null){
                if(admin.changeSelfInfo(passwd)){
                    JOptionPane.showMessageDialog(null, "密码修改成功", "提示", JOptionPane.PLAIN_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "修改失败!", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }else if(arg0.getSource()==item2){//下载文档对话框
            String id;
            id = JOptionPane.showInputDialog("请输入文档ID:");
            if(id!=null){
                FileDialog fileName = new FileDialog(this,"下载文件",FileDialog.SAVE);
                fileName.setVisible(true);
                if(fileName.getFile()!=null){
                    if(admin.downloadFile(id, fileName.getDirectory()+fileName.getFile(), null)){
                        JOptionPane.showMessageDialog(null, "下载完毕!", "提示", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "下载失败!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }else if(arg0.getSource()==item8){                     //显示用户列表
            this.refreshTableModel(jt2);
            card.show(this.getContentPane(), "管理员");
        }else if(arg0.getSource()==item6 || arg0.getSource()==jb2){   //删除用户
            if(jt2.getSelectedRow()==-1){
                JOptionPane.showMessageDialog(null, "请先在用户列表界面选择一个用户!", "提示", JOptionPane.PLAIN_MESSAGE);
            }else{
                int row = jt2.getSelectedRow();
                if(JOptionPane.showConfirmDialog(this, "确认删除用户: "+(String)jt2.getModel().getValueAt(row, 1)+"吗?")==JOptionPane.OK_OPTION){
                    if(admin.delUser((String)jt2.getModel().getValueAt(row, 1))){
                        this.refreshTableModel(jt2);
                        JOptionPane.showMessageDialog(null, "删除成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "删除失败!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }else if(arg0.getSource()==item7 || arg0.getSource()==jb3){//添加用户
            String name, password=null, role;
            if((name=JOptionPane.showInputDialog("输入用户名:"))!=null && !name.equals("") &&
                    (password=this.inputPassword("请输入密码:","用户添加"))!=null &&
                    (role=inputRole())!=null){
                if(admin.addUser(name, password, role)){
                    this.refreshTableModel(jt2);
                    JOptionPane.showMessageDialog(null, "添加成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "添加失败!\n可能用户已存在", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                if(name!=null && name.equals("")){
                    JOptionPane.showMessageDialog(null, "用户名不能为空!", "警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        }else if(arg0.getSource()==item5 || arg0.getSource()==jb4){   //修改用户
            int row = jt2.getSelectedRow();

            if(row==-1){
                JOptionPane.showMessageDialog(null, "请先在用户列表界面选择一个用户!", "提示", JOptionPane.PLAIN_MESSAGE);
            }else{
                String name = (String)jt2.getModel().getValueAt(row, 1), password, role;
                if(JOptionPane.showConfirmDialog(this, "确认修改用户:"+name+"吗?")==JOptionPane.OK_OPTION){
                    if((password=this.inputPassword("请输入修改后的密码:","用户修改"))!=null &&
                            (role=this.inputRole())!=null){

                        if(admin.changeUserInfo(name, password, role)){
                            this.refreshTableModel(jt2);
                            JOptionPane.showMessageDialog(null, "修改成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "修改失败!", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }else{//还未实现的功能事件
            JOptionPane.showMessageDialog(null, "Warnning!", "此功能还未实现...", JOptionPane.WARNING_MESSAGE);
        }
    }

    public UI_Administrator(Administrator admin) {
        super("管理员界面");
        //设置用户
        this.admin = admin;
        //文档列表及JFrame的布局设置
        pad_1.setLayout(new BorderLayout());
        pad_2.setLayout(new BorderLayout());
        this.getContentPane().setLayout(card);
        this.getContentPane().add(pad_0,"欢迎界面");
        this.getContentPane().add(pad_1,"文档列表");
        this.getContentPane().add(pad_2,"管理员");

        //设置JTable
        jt1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jt1.setFont(new Font("宋体",Font.PLAIN,20));
        jt1.setRowMargin(0);
        jt1.setRowHeight(35);
        jt1.setOpaque(true);

        jt2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jt2.setFont(new Font("宋体",Font.PLAIN,20));
        jt2.setRowMargin(0);
        jt2.setRowHeight(35);
        jt2.setOpaque(true);
        //绘制JFrame及添加菜单
        this.setBounds((1920-frame_wid)/2, (1080-frame_hgt)/2, frame_wid, frame_hgt);
        menu1.add(item1);
        menu1.add(item2);
        menu1.addSeparator();
        menu1.add(item3);
        menu2.add(item4);
        menu3.add(item5);
        menu3.add(item6);
        menu3.add(item7);
        menu3.add(item8);
        mb.add(menu1);mb.add(menu2);mb.add(menu3);
        this.setJMenuBar(mb);
        //菜单权限控制

        //绘制文档列表界面pad_1
        pad1.add(jl1);
        JScrollPane js = new JScrollPane(jt1);
        js.getViewport().setBackground(Color.white);
        pad3.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
        pad3.add(jb1);

        pad_1.add("North",pad1);
        js.setBounds(10,this.getHeight()/12,this.getWidth()-25,this.getHeight()/3*2);
        pad_1.add("Center",js);
        pad_1.add("South",pad3);
        pad_1.add("West",new JPanel());
        pad_1.add("East",new JPanel());

        jl1.setFont(new Font("楷体",Font.BOLD,30));

        //绘制用户列表界面pad_2
        jl2.setFont(new Font("楷体",Font.BOLD,30));
        pad4.add(jl2);
        JScrollPane js1 = new JScrollPane(jt2);

        js1.getViewport().setBackground(Color.white);
        pad5.add(jb3);
        pad5.add(jb4);
        pad5.add(jb2);

        pad_2.add("North",pad4);
        pad_2.add("Center",js1);
        pad_2.add("South",pad5);
        pad_2.add("West",new JPanel());
        pad_2.add("East",new JPanel());

        pad5.setLayout(new FlowLayout(FlowLayout.CENTER,20,10));
        //添加事件
        jb1.addActionListener(this);
        item1.addActionListener(this);
        item2.addActionListener(this);
        item3.addActionListener(this);
        item4.addActionListener(this);
        jt1.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            actionPerformed(null);
                        }
                    }
                }
        );
        //administrator
        item5.addActionListener(this);
        item6.addActionListener(this);
        item7.addActionListener(this);
        item8.addActionListener(this);
        jb2.addActionListener(this);
        jb3.addActionListener(this);
        jb4.addActionListener(this);
        //operator

        //绘制欢迎页面
        JLabel welcome = new JLabel(admin.getName()+",欢迎来到管理员界面!");welcome.setFont(new Font("楷体",Font.BOLD,30));
        Panel temp = new Panel();temp.add(welcome);
        pad_0.setLayout(new GridLayout(3,1));
        pad_0.add(new JPanel());
        pad_0.add(temp);
        pad_0.add(new JPanel());

        //JFrame最终设置
        //this.setResizable(false);
        this.addWindowListener(new DefaultWindowListener(this.admin));
        this.setVisible(true);
    }

    private void setJTableWidth(JTable jt){
        jt.getColumnModel().getColumn(0).setPreferredWidth(30);
        jt.getColumnModel().getColumn(1).setPreferredWidth(50);
        jt.getColumnModel().getColumn(2).setPreferredWidth(190);
        jt.getColumnModel().getColumn(3).setPreferredWidth(180);
        jt.getColumnModel().getColumn(4).setPreferredWidth(195);
    }

    private void refreshTableModel(JTable jt){
        if(jt == jt2){
            jt2.setModel(new DefaultTableModel(admin.listUser(),col_user_name));
        }else if(jt == jt1){
            jt.setModel(new DefaultTableModel(this.admin.showFileList(),col_doc_name));
            this.setJTableWidth(jt);
        }
    }

    private String inputRole() {
        String role = null;
        JPanel jp = new JPanel();
        CheckboxGroup cg = new CheckboxGroup();
        JLabel j_tip = new JLabel("请选择角色:");
        Checkbox jb1 = new Checkbox("browser",true,cg);
        Checkbox jb2 = new Checkbox("operator",false,cg);
        Checkbox jb3 = new Checkbox("administrator",false,cg);

        jp.add(j_tip);
        jp.add(jb1);jp.add(jb2);jp.add(jb3);
        if(JOptionPane.showConfirmDialog(this,jp,"请输入",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            if (jb1.getState()) {
                role = jb1.getLabel();
            } else if (jb2.getState()) {
                role = jb2.getLabel();
            } else if (jb3.getState()) {
                role = jb3.getLabel();
            }
        }
        return role;
    }

    private String inputPassword(String tip, String title) {
        JPanel container = new JPanel();
        JPanel pad1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel pad2 = new JPanel();
        JPasswordField jpf = new JPasswordField();
        String passwd = null;
        jpf.setColumns(15);
        container.setLayout(new GridLayout(2,1));
        pad1.add(new JLabel(tip));
        pad2.add(jpf);
        container.add(pad1);
        container.add(pad2);
        if(JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){
            passwd = new String(jpf.getPassword());
            if(!passwd.equals("")){
                return passwd;
            }else{
                JOptionPane.showMessageDialog(null, "密码不能为空!", "警告", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }else{
            return null;
        }
    }
}
