package fileManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DownloadDialog extends JDialog implements ActionListener, WindowListener {
    public JLabel jl2 = new JLabel("");

    public void actionPerformed(ActionEvent e){
        this.dispose();
    }

    public DownloadDialog(JFrame parent) {
        super(parent,"文件下载",true);

        //JButton jb1 = new JButton("close");
        //jb1.addActionListener(this);
        //this.add(jb1);
        JLabel jl1 = new JLabel("正在下载中，请稍等。。。");
        this.add(jl1);
        this.add(jl2);

        this.setSize(200,200);
        this.setBounds(parent.getX()+(parent.getWidth()-getWidth())/2,parent.getY()+(parent.getHeight()-getHeight())/2,getWidth(),getHeight());
        this.setResizable(false);
        this.addWindowListener(this);
        this.setUndecorated(false);
        //this.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        //this.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
