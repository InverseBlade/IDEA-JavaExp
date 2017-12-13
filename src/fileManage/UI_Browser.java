package fileManage;

import java.awt.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.*;
import javax.swing.table.*;

public class UI_Browser extends JFrame implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //初始界面大小
    private final int frame_wid = 854;
    private final int frame_hgt = 640;
    //用户变量
    private Browser browser;
    /////////////
    CardLayout card = new CardLayout();//属于JFrame.contentPanel的卡片布局
    JPanel pad_0 = new JPanel();//欢迎界面
    JPanel pad_1 = new JPanel();//文档列表界面
    /////////////////////////菜单////////////////////////////
    JMenuBar mb = new JMenuBar();
    JMenu menu1 = new JMenu("文档");
    JMenu menu2 = new JMenu("用户");
    //user
    JMenuItem item1 = new JMenuItem("显示文档列表");
    JMenuItem item2 = new JMenuItem("下载文档");
    JMenuItem item3 = new JMenuItem("退出登录");
    JMenuItem item4 = new JMenuItem("修改密码");
    //administrator

    //operator

    ////////////////////////////////////////////////////////////
    JPanel pad1 = new JPanel();  //belongs to pad_1
    JPanel pad2 = new JPanel();  //belongs to pad_1
    JPanel pad3 = new JPanel();  //belongs to pad_1

    JButton jb1 = new JButton("下载");
    JLabel jl1 = new JLabel("文档列表");
    //JTable
    JTable jt1 = new JTable(){
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column){
            return false;
        }
    };
    String[] col_doc_name = {"文档ID","上传者","上传时间","描述","文件名"};

    //Socket通信变量

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if(arg0 == null || arg0.getSource()==jb1){//下载文档按钮事件以及事件被主动调用
            int item = jt1.getSelectedRow();
            if(item != -1){
                FileDialog fileName = new FileDialog(this,"下载文件",FileDialog.SAVE);
                fileName.setDirectory("C:\\Users\\Dell\\Desktop");
                fileName.setFile((String)jt1.getModel().getValueAt(item,4));
                fileName.setVisible(true);
                if(fileName.getFile()!=null){
                    if(browser.downloadFile((String)jt1.getValueAt(item, 0), fileName.getDirectory()+fileName.getFile())){
                        JOptionPane.showMessageDialog(null, "下载完毕!", "提示", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "下载失败!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }else if(arg0.getSource()==item3){//退出登录
            if(JOptionPane.showConfirmDialog(null, "确定退出登录?", "注销", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                this.setVisible(false);
                this.browser.exitSystem();
            };
        }else if(arg0.getSource()==item1){//显示文档列表
            this.refreshTableModel(jt1);
            card.show(this.getContentPane(), "文档列表");
        }else if(arg0.getSource()==item4){//修改密码对话框
            JPanel container = new JPanel();
            JPanel pad1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel pad2 = new JPanel();
            JPasswordField jpf = new JPasswordField();
            String passwd = null;
            jpf.setColumns(15);
            container.setLayout(new GridLayout(2,1));
            pad1.add(new JLabel("请输入修改后的密码:"));
            pad2.add(jpf);
            container.add(pad1);
            container.add(pad2);
            if(JOptionPane.showConfirmDialog(this, container, "修改密码", JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){
                passwd = new String(jpf.getPassword());
                if(!passwd.equals("")){
                    if(browser.changeSelfInfo(passwd)){
                        JOptionPane.showMessageDialog(null, "密码修改成功", "提示", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "修改失败!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "密码不能为空!", "警告", JOptionPane.WARNING_MESSAGE);
                }
            }
        }else if(arg0.getSource()==item2){//下载文档对话框
            String id;
            id = JOptionPane.showInputDialog("请输入文档ID:");
            if(id!=null){
                FileDialog fileName = new FileDialog(this,"下载文件",FileDialog.SAVE);
                fileName.setVisible(true);
                if(fileName.getFile()!=null){
                    if(browser.downloadFile(id, fileName.getDirectory()+fileName.getFile())){
                        JOptionPane.showMessageDialog(null, "下载完毕!", "提示", JOptionPane.PLAIN_MESSAGE);
                    }else{
                        JOptionPane.showMessageDialog(null, "下载失败!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }else{//还未实现的功能事件
            JOptionPane.showMessageDialog(null, "Warnning!", "此功能还未实现...", JOptionPane.WARNING_MESSAGE);
        }
    }

    public UI_Browser(Browser browser) {
        super("档案浏览人员界面");

        //设置用户
        this.browser = browser;
        //文档列表及JFrame的布局设置
        pad_1.setLayout(new BorderLayout());
        this.getContentPane().setLayout(card);
        this.getContentPane().add(pad_0,"欢迎界面");
        this.getContentPane().add(pad_1,"文档列表");

        //设置JTable
        jt1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jt1.setFont(new Font("宋体",Font.PLAIN,20));
        jt1.setRowMargin(0);
        jt1.setRowHeight(35);
        jt1.setOpaque(true);

        //绘制JFrame及添加菜单
        this.setBounds((1920-frame_wid)/2, (1080-frame_hgt)/2, frame_wid, frame_hgt);
        menu1.add(item1);
        menu1.add(item2);
        menu1.addSeparator();
        menu1.add(item3);
        menu2.add(item4);
        mb.add(menu1);mb.add(menu2);
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
        //operator

        //绘制欢迎页面
        JLabel welcome = new JLabel(browser.getName()+",欢迎来到档案浏览员界面!");welcome.setFont(new Font("楷体",Font.BOLD,30));
        Panel temp = new Panel();temp.add(welcome);
        pad_0.setLayout(new GridLayout(3,1));
        pad_0.add(new JPanel());
        pad_0.add(temp);
        pad_0.add(new JPanel());

        //JFrame最终设置
        //this.setResizable(false);
        this.addWindowListener(new DefaultWindowListener(this.browser));
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
        if(jt == jt1){
            jt.setModel(new DefaultTableModel(this.browser.showFileList(),col_doc_name));
            this.setJTableWidth(jt);
        }
    }
}
