package fr.ezzud.loslauncher.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import fr.theshark34.openlauncherlib.bootstrap.Bootstrap;
import fr.theshark34.openlauncherlib.bootstrap.LauncherClasspath;
import fr.theshark34.openlauncherlib.bootstrap.LauncherInfos;
import fr.theshark34.openlauncherlib.util.ErrorUtil;
import fr.theshark34.openlauncherlib.util.GameDir;
import fr.theshark34.openlauncherlib.util.SplashScreen;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.SUpdate;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;

public class LoslauncherBootstrap {

	
	private static SplashScreen splash;
	private static SColoredBar bar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private static Thread barThread;
	
	private static final LauncherInfos LOS_B_INFOS = new LauncherInfos("Legend of Sideria", "fr.ezzud.loslauncher.launcher.LauncherFrame");
	private static final File LOS_DIR = GameDir.createGameDir("Legend Of Sideria");
	private static final LauncherClasspath LOS_B_CP = new LauncherClasspath(new File(LOS_DIR, "launcher/launcher.jar"), new File(LOS_DIR, "launcher/libs"));
	
	private static ErrorUtil errorUtil = new ErrorUtil(new File(LOS_DIR, "launcher/crashes/"));
	
	public static void main(String[] args) {
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/ezzud/loslauncher/bootstrap/resources/");
		displaySplash();
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try
		{
		doUpdate();
		} catch (Exception e) {
			errorUtil.catchError(e, "Impossible de mettre à jour le launcher");
			barThread.interrupt();
		}
		try {
			launchLauncher();
		} catch (IOException e) {
			errorUtil.catchError(e, "Impossible de lancer le launcher");
		}
	}
	private static void displaySplash() {
		String[] strings = {"splash1.png", "splash2.png", "splash3.png", "splash4.png", "splash5.png", "splash6.png", "splash7.png", "splash8.png", "splash9.png", "splash10.png"}; 
		Random random = new Random();
		int index = random.nextInt(strings.length);
		splash = new SplashScreen("Legend of Sideria", Swinger.getResource(strings[index]));
		splash.setLayout(null);
		
		bar.setBounds(0, 456, 350, 0);
		splash.add(bar);
		
		splash.setVisible(true);
	}

	private static void doUpdate() throws Exception {
		SUpdate su = new SUpdate("https://legendofsideria.fr/supdate-backup/bootstrap/", new File(LOS_DIR, "launcher"));
	
		barThread = new Thread() {
			
			@Override
			public void run() {
				while(!this.isInterrupted()) {
					bar.setValue((int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000)); 
					bar.setMaximum((int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000)); 
				}
			}
			
		};
		barThread.start();
		su.start();
		barThread.interrupt();
	}
	
	private static void launchLauncher() throws IOException {
		Bootstrap bootstrap = new Bootstrap(LOS_B_CP, LOS_B_INFOS);
		Process p = bootstrap.launch();
		splash.setVisible(false);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			
		}
		System.exit(0);
	}
}
