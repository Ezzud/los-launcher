package fr.ezzud.loslauncher.launcher;

import java.io.File;
import java.util.Arrays;

import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.openlauncherlib.util.ProcessLogManager;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import fr.theshark34.swinger.Swinger;

public class Launcher {

	public static final GameVersion LOS_VERSION = new GameVersion("1.7.10", GameType.V1_7_10);
	public static final GameInfos LOS_INFOS = new GameInfos("Legend Of Sideria", LOS_VERSION, new GameTweak[] {GameTweak.FORGE});
	public static final File LOS_DIR = LOS_INFOS.getGameDir();
	public static final GameFolder LOS_FOLDER = new GameFolder("game/assets", "game/libs", "launcher/natives", "launcher/legendofsideria.jar");
	public static final File LOS_CRASHES_DIR = new File(LOS_DIR, "crashes");
	
	private static AuthInfos authInfos;
	private static Thread updateThread;
	
	public static void auth(String username, String password) throws AuthenticationException {
		Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
		AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, "");
		authInfos = new AuthInfos(response.getSelectedProfile().getName(), response.getAccessToken(), response.getSelectedProfile().getId());
	}
	
	public static void update() throws Exception {
		SUpdate su = new SUpdate("https://legendofsideria.fr/supdate-backup/", LOS_DIR);
		su.addApplication(new FileDeleter());
		updateThread = new Thread() {
			private int val = 0;
			private int max = 0;
			
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
					max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);
					
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setMaximum(max);
					LauncherFrame.getInstance().getLauncherPanel().getProgressBar().setValue(val);
					if(BarAPI.getNumberOfFileToDownload() == 0) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Vérification des fichiers du jeu...");
					} else if(val == max) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Vérification des fichiers du jeu...");
					} else if(val > max) {
						LauncherFrame.getInstance().getLauncherPanel().setInfoText("Vérification des fichiers du jeu...");
					} else {
					LauncherFrame.getInstance().getLauncherPanel().setInfoText("Téléchargement des fichiers du jeu " +
						BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload() + " " +
							Swinger.percentage(val, max) + "%");
					}
				}
			}
		};
		updateThread.start();
		
		su.start();
		updateThread.interrupt();
	}
	
	public static void launch() throws LaunchException {
		ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(LOS_INFOS, GameFolder.BASIC, authInfos);
		profile.getVmArgs().addAll(Arrays.asList(LauncherFrame.getInstance().getLauncherPanel().getRamSelector().getRamArguments()));
		ExternalLauncher launcher = new ExternalLauncher(profile);
		
		
		Process pp = launcher.launch();
		
		ProcessLogManager manager = new ProcessLogManager(pp.getInputStream(), new File(LOS_DIR, "logs.txt"));
		manager.start();
		
		LauncherFrame.getInstance().setVisible(false);
		
		try {
			Thread.sleep(5000L);
			pp.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		System.exit(0);
	}
	
	public static void interrupThread() {
		updateThread.interrupt();
	}
	
}
