package fileManage;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;

public class DefaultWindowListener implements WindowListener {

    private User user;

    public DefaultWindowListener(User user) {
        this.user = user;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("给个好评呗!~~");
        user.exitSystem();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("给个好评呗!~~");
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
