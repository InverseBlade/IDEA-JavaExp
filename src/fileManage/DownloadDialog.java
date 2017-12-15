package fileManage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DownloadDialog extends JDialog implements ActionListener, WindowListener {
    public JLabel jl2 = new JLabel("");
    public JProgressBar jp = new JProgressBar();

    public void actionPerformed(ActionEvent e){
        this.dispose();
    }

    public DownloadDialog(JFrame parent, String title) {
        super(parent,title,true);

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JLabel jl1 = new JLabel("正在"+title+"中，请稍等。。。");

        this.getContentPane().setLayout(new GridLayout(3,1));
        p1.add(jl1);
        p2.add(jl2);
        p3.add(jp);
        jp.setBorderPainted(true);
        jp.setMinimum(0);
        jp.setMaximum(100);
        jp.setValue(0);

        this.add(p1);
        this.add(p2);
        this.add(p3);

        this.setSize(200,200);
        this.setBounds(parent.getX()+(parent.getWidth()-getWidth())/2,parent.getY()+(parent.getHeight()-getHeight())/2,getWidth(),getHeight());
        this.setResizable(false);
        //this.addWindowListener(this);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setUndecorated(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        JOptionPane.showMessageDialog(null,"还没下载完!!");
        this.setVisible(true);
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
