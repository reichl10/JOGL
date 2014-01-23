package de.joglearth.robot;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.lang.reflect.Field;
import java.util.Random;

import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.joglearth.JoglEarth;
import de.joglearth.location.LocationManager;
import de.joglearth.settings.SettingsContract;
import de.joglearth.ui.MainWindow;
import de.joglearth.ui.Messages;


public class JoglEarthRobot {

    public static void main(String[] args) {
        JoglEarthRobot jRobot = new JoglEarthRobot();
        System.out.println("Started!");
        jRobot.run();
        System.out.println("Run finished!");
    }


    Robot robot;
    Point onScreenUIPoint;
    Point viewmodeCBoxPoint;
    Point viewModeSolarPoint;
    Point viewModeGlobePoint;
    Point viewModePlainPoint;
    Point heightMapCheckBox;
    Point tabViewPoint;
    Point tabPlacesPoint;
    Point tabSettingPoint;
    Random random;


    public JoglEarthRobot() {
        random = new Random();
        try {
            robot = new Robot();
            robot.setAutoDelay(300);
        } catch (AWTException e) {
            e.printStackTrace();
            System.exit(1);
        }
        MainWindow ui = startJoglEarth();
        Point onScreenUIPoint = ui.getLocationOnScreen();

        // viewmode coords
        (viewmodeCBoxPoint = new Point(65, 103))
                .translate(onScreenUIPoint.x, onScreenUIPoint.y);
        (viewModeSolarPoint = new Point(96, 140)).translate(onScreenUIPoint.x,
                onScreenUIPoint.y);
        (viewModeGlobePoint = new Point(63, 176)).translate(onScreenUIPoint.x,
                onScreenUIPoint.y);
        (viewModePlainPoint = new Point(84, 204)).translate(onScreenUIPoint.x,
                onScreenUIPoint.y);
        (heightMapCheckBox = new Point(67, 205))
                .translate(onScreenUIPoint.x, onScreenUIPoint.y);

        // tabcoords
        (tabViewPoint = new Point(46, 48)).translate(onScreenUIPoint.x, onScreenUIPoint.y);
        (tabPlacesPoint = new Point(100, 43)).translate(onScreenUIPoint.x, onScreenUIPoint.y);
        (tabSettingPoint = new Point(175, 46)).translate(onScreenUIPoint.x, onScreenUIPoint.y);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        for (int i = 0; i < 5; i++) {
            clickRandomViewMode();
        }
        for (int i = 0; i < 5; i++) {
            clickOnRandomTab();
        }
        clickGlobeMode();
        ZoomInOrOut();
        for (int i = 0; i < 5; i++) {
            moveMapRandomly();
        }
        for (int i = 0; i < 30; i++) {
            zoomOrMoveOrChangeMode();
        }
    }

    private void zoomOrMoveOrChangeMode() {
        int r = random.nextInt(1);
        if (r == 0) {
            zoomOrMove();
        } else {
            changeToGlobeOrPlain();
        }
    }
    
    private void zoomOrMove() {
        int r = random.nextInt(1);
        if (r == 0) {
            ZoomInOrOut();
        } else {
            moveMapRandomly();
        }
    }
    
    private void changeToGlobeOrPlain() {
        changeToViewTab();
        int r = random.nextInt(1);
        if (r == 0) {
            clickGlobeMode();
        } else {
            clickPlainMode();
        }
    }

    private void moveMapRandomly() {
        Point topL = new Point(281, 48);
        Point botRight = new Point(937, 526);
        Point startM = new Point(topL.x + random.nextInt(botRight.x - topL.x), topL.y
                + random.nextInt(botRight.y - topL.y));
        Point endM = new Point(topL.x + random.nextInt(botRight.x - topL.x), topL.y
                + random.nextInt(botRight.y - topL.y));
        moveTo(startM);
        pressLeft();
        moveTo(endM);
        releaseLeft();
    }

    private void ZoomInOrOut() {
        Point topL = new Point(281, 48);
        Point botRight = new Point(937, 526);
        Point startM = new Point(topL.x + random.nextInt(botRight.x - topL.x), topL.y
                + random.nextInt(botRight.y - topL.y));
        moveTo(startM);
        doLeftClick();
        int inOO = random.nextInt(1);
        if (inOO == 0)
            robot.mouseWheel(-random.nextInt(80));
        else {
            robot.mouseWheel(random.nextInt(80));
        }
    }

    private void moveTo(Point p) {
        robot.mouseMove(p.x, p.y);
    }

    private void clickGlobeMode() {
        changeToViewTab();
        robot.mouseMove(viewmodeCBoxPoint.x, viewmodeCBoxPoint.y);
        doLeftClick();
        robot.mouseMove(viewModeGlobePoint.x, viewModeGlobePoint.y);
        doLeftClick();
    }
    private void clickPlainMode() {
        changeToViewTab();
        robot.mouseMove(viewmodeCBoxPoint.x, viewmodeCBoxPoint.y);
        doLeftClick();
        robot.mouseMove(viewModePlainPoint.x, viewModePlainPoint.y);
        doLeftClick();
    }
    
    
    private int clickRandomViewMode() {
        robot.mouseMove(viewmodeCBoxPoint.x, viewmodeCBoxPoint.y);
        doLeftClick();
        int rd = random.nextInt(3);
        Point moveToPoint;
        switch (rd) {
            case 0:
                moveToPoint = viewModeGlobePoint;
                break;
            case 1:
                moveToPoint = viewModePlainPoint;
                break;
            case 2:
                moveToPoint = viewModeSolarPoint;
                break;
            default:
                moveToPoint = viewModeGlobePoint;
                break;
        }
        robot.mouseMove(moveToPoint.x, moveToPoint.y);
        doLeftClick();
        return rd;
    }

    private void changeToSettingTab() {
        robot.mouseMove(tabSettingPoint.x, tabSettingPoint.y);
        doLeftClick();
    }

    private void changeToPlacesTab() {
        robot.mouseMove(tabPlacesPoint.x, tabPlacesPoint.y);
        doLeftClick();
    }

    private void changeToViewTab() {
        robot.mouseMove(tabViewPoint.x, tabViewPoint.y);
        doLeftClick();
    }

    private int clickOnRandomTab() {
        int rd = random.nextInt(3);
        switch (rd) {
            case 0:
                changeToViewTab();
                break;
            case 1:
                changeToPlacesTab();
                break;
            case 2:
                changeToSettingTab();
                break;
            default:
                break;
        }
        return rd;
    }

    private void doLeftClick() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void pressLeft() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void releaseLeft() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private MainWindow startJoglEarth() {
        SettingsContract.setDefaultSettings();
        SettingsContract.loadSettings();
        MainWindow ui;
        UIListener listener = new UIListener();
        SwingUtilities.invokeLater(new UIStarter(listener));
        synchronized (JoglEarthRobot.class) {
            try {
                JoglEarthRobot.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return listener.window;
    }


    private class UIListener {

        MainWindow window;
    }

    private class UIStarter implements Runnable {

        private UIListener listener;


        public UIStarter(UIListener l) {
            listener = l;
        }

        @Override
        public void run() {
            // Set the system-specific look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (
                ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {}

            // Set the window manager application name
            try {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Field awtAppName = tk.getClass().getDeclaredField("awtAppClassName");
                awtAppName.setAccessible(true);
                awtAppName.set(tk, JoglEarth.PRODUCT_NAME);
            } catch (NoSuchFieldException | IllegalAccessException e) {}

            // Check whether OpenGL2 ES1 is available
            GLProfile prof = null;
            try {
                prof = GLProfile.get(GLProfile.GL2ES1);
            } catch (GLException e) {
                JOptionPane.showMessageDialog(null, Messages.getString("JoglEarth.noGL"),
                        JoglEarth.PRODUCT_NAME, JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocationManager locationManager = new LocationManager();
            listener.window = new MainWindow(prof, locationManager);
            listener.window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            listener.window.setVisible(true);
            synchronized (JoglEarthRobot.class) {
                JoglEarthRobot.class.notify();
            }
        }
    }
}
