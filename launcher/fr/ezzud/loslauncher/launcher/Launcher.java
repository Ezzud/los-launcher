// 
// Decompiled by Procyon v0.5.36
// 

package fr.ezzud.loslauncher.launcher;

import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.util.ProcessLogManager;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import java.util.Collection;
import java.util.Arrays;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.application.Application;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.supdate.SUpdate;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.AuthPoints;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import java.io.File;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;

public class Launcher
{
    public static final GameVersion LOS_VERSION;
    public static final GameInfos LOS_INFOS;
    public static final File LOS_DIR;
    public static final GameFolder LOS_FOLDER;
    public static final File LOS_CRASHES_DIR;
    private static AuthInfos authInfos;
    private static Thread updateThread;
    
    static {
        LOS_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
        LOS_INFOS = new GameInfos("Legend Of Sideria", Launcher.LOS_VERSION, new GameTweak[] { GameTweak.FORGE });
        LOS_DIR = Launcher.LOS_INFOS.getGameDir();
        LOS_FOLDER = new GameFolder("game/assets", "game/libs", "launcher/natives", "launcher/legendofsideria.jar");
        LOS_CRASHES_DIR = new File(Launcher.LOS_DIR, "crashes");
    }
    
    public static void auth(final String username, final String password) throws AuthenticationException {
        final Authenticator authenticator = new Authenticator("https://authserver.mojang.com/", AuthPoints.NORMAL_AUTH_POINTS);
        final AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
        Launcher.authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
    }
    
    public static void update() throws Exception {
        final SUpdate su = new SUpdate("https://legendofsideria.fr/supdate-backup/", Launcher.LOS_DIR);
        su.addApplication((Application)new FileDeleter());
        (Launcher.updateThread = new Thread() {
            private int val = 0;
            private int max = 0;
            
            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    this.val = (int)(BarAPI.getNumberOfTotalDownloadedBytes() / 1000L);
                    this.max = (int)(BarAPI.getNumberOfTotalBytesToDownload() / 1000L);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(this.max);
                    LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(this.val);
                    if (BarAPI.getNumberOfFileToDownload() == 0) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("V\u00e9rification des fichiers du jeu...");
                    }
                    else if (this.val == this.max) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("V\u00e9rification des fichiers du jeu...");
                    }
                    else if (this.val > this.max) {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("V\u00e9rification des fichiers du jeu...");
                    }
                    else {
                        LauncherFrame.getInstance().getLauncherPanel().setInfoText("T\u00e9l\u00e9chargement des fichiers du jeu " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " + Swinger.percentage(this.val, this.max) + "%");
                    }
                }
            }
        }).start();
        su.start();
        Launcher.updateThread.interrupt();
    }
    
    public static void launch() throws LaunchException {
        final ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(Launcher.LOS_INFOS, GameFolder.BASIC, Launcher.authInfos);
        profile.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
        final ExternalLauncher launcher = new ExternalLauncher(profile);
        final Process pp = launcher.launch();
        final ProcessLogManager manager = new ProcessLogManager(pp.getInputStream(), new File(Launcher.LOS_DIR, "logs.txt"));
        manager.start();
        LauncherFrame.getInstance().setVisible(false);
        try {
            Thread.sleep(5000L);
            pp.waitFor();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    
    public static void interrupThread() {
        Launcher.updateThread.interrupt();
    }
}
