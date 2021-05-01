// 
// Decompiled by Procyon v0.5.36
// 

package fr.ezzud.loslauncher.bootstrap;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.Image;
import java.util.Random;
import java.io.IOException;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import java.io.File;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.openlauncherlib.util.SplashScreen;

public class LoslauncherBootstrap
{
    private static SplashScreen splash;
    private static SColoredBar bar;
    private static Thread barThread;
    private static final LauncherInfos LOS_B_INFOS;
    private static final File LOS_DIR;
    private static final LauncherClasspath LOS_B_CP;
    private static ErrorUtil errorUtil;
    
    static {
        LoslauncherBootstrap.bar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        LOS_B_INFOS = new LauncherInfos("Legend of Sideria", "fr.ezzud.loslauncher.launcher.LauncherFrame");
        LOS_DIR = GameDir.createGameDir("Legend Of Sideria");
        LOS_B_CP = new LauncherClasspath(new File(LoslauncherBootstrap.LOS_DIR, "launcher/launcher.jar"), new File(LoslauncherBootstrap.LOS_DIR, "launcher/libs"));
        LoslauncherBootstrap.errorUtil = new ErrorUtil(new File(LoslauncherBootstrap.LOS_DIR, "launcher/crashes/"));
    }
    
    public static void main(final String[] args) {
        Swinger.setSystemLookNFeel();
        Swinger.setResourcePath("/fr/ezzud/loslauncher/bootstrap/resources/");
        displaySplash();
        try {
            Thread.sleep(2000L);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            doUpdate();
        }
        catch (Exception e2) {
            LoslauncherBootstrap.errorUtil.catchError(e2, "Impossible de mettre \u00e0 jour le launcher");
            LoslauncherBootstrap.barThread.interrupt();
        }
        try {
            launchLauncher();
        }
        catch (IOException e3) {
            LoslauncherBootstrap.errorUtil.catchError((Exception)e3, "Impossible de lancer le launcher");
        }
    }
    
    private static void displaySplash() {
        final String[] strings = { "splash1.png", "splash2.png", "splash3.png", "splash4.png", "splash5.png", "splash6.png", "splash7.png", "splash8.png", "splash9.png", "splash10.png" };
        final Random random = new Random();
        final int index = random.nextInt(strings.length);
        (LoslauncherBootstrap.splash = new SplashScreen("Legend of Sideria", (Image)Swinger.getResource(strings[index]))).setLayout((LayoutManager)null);
        LoslauncherBootstrap.bar.setBounds(0, 456, 350, 0);
        LoslauncherBootstrap.splash.add((Component)LoslauncherBootstrap.bar);
        LoslauncherBootstrap.splash.setVisible(true);
    }
    
    private static void doUpdate() throws Exception {
        final SUpdate su = new SUpdate("https://legendofsideria.fr/supdate-backup/bootstrap/", new File(LoslauncherBootstrap.LOS_DIR, "launcher"));
        (LoslauncherBootstrap.barThread = new Thread() {
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    LoslauncherBootstrap.bar.setValue((int)(BarAPI.getNumberOfTotalDownloadedBytes() / 1000L));
                    LoslauncherBootstrap.bar.setMaximum((int)(BarAPI.getNumberOfTotalBytesToDownload() / 1000L));
                }
            }
        }).start();
        su.start();
        LoslauncherBootstrap.barThread.interrupt();
    }
    
    private static void launchLauncher() throws IOException {
        final Bootstrap bootstrap = new Bootstrap(LoslauncherBootstrap.LOS_B_CP, LoslauncherBootstrap.LOS_B_INFOS);
        final Process p = bootstrap.launch();
        LoslauncherBootstrap.splash.setVisible(false);
        try {
            p.waitFor();
        }
        catch (InterruptedException ex) {}
        System.exit(0);
    }
}
