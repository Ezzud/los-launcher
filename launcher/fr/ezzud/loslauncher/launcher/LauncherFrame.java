// 
// Decompiled by Procyon v0.5.36
// 

package fr.ezzud.loslauncher.launcher;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import fr.theshark34.swinger.util.WindowMover;
import java.awt.Container;
import java.awt.Image;
import fr.theshark34.swinger.Swinger;
import java.awt.Component;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import javax.swing.JFrame;

public class LauncherFrame extends JFrame
{
    private static LauncherFrame instance;
    private LauncherPanel launcherPanel;
    public static CrashReporter errorUtil;
    
    public LauncherFrame() {
        this.setTitle("Legend Of Sideria");
        this.setSize(975, 625);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setUndecorated(true);
        this.setIconImage(Swinger.getResource("icon.png"));
        this.setContentPane(this.launcherPanel = new LauncherPanel());
        final WindowMover mover = new WindowMover((JFrame)this);
        this.addMouseListener((MouseListener)mover);
        this.addMouseMotionListener((MouseMotionListener)mover);
        this.setVisible(true);
    }
    
    public static void main(final String[] args) {
        Swinger.setSystemLookNFeel();
        Swinger.setResourcePath("/fr/ezzud/loslauncher/launcher/resources");
        Launcher.LOS_CRASHES_DIR.mkdirs();
        LauncherFrame.errorUtil = new CrashReporter("Legend Of Sideria", Launcher.LOS_CRASHES_DIR);
        LauncherFrame.instance = new LauncherFrame();
    }
    
    public static LauncherFrame getInstance() {
        return LauncherFrame.instance;
    }
    
    public LauncherPanel getLauncherPanel() {
        return this.launcherPanel;
    }
    
    public static CrashReporter getErrorUtil() {
        return LauncherFrame.errorUtil;
    }
}
