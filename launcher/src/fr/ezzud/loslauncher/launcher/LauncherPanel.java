package fr.ezzud.loslauncher.launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.litarvan.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener {

	private Image background = Swinger.getResource("background.png");
	private BufferedImage logo = Swinger.getResource("logo.png");
		
	private Saver saver = new Saver(new File(Launcher.LOS_DIR, "launcher.properties"));
	
	private JTextField usernameField = new JTextField(saver.get("username"));
	private JPasswordField passwordField = new JPasswordField();
	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("playButton.png"));
	private STexturedButton quitButton = new STexturedButton(Swinger.getResource("quitButton.png"));
	private STexturedButton hideButton = new STexturedButton(Swinger.getResource("hideButton.png"));
	
	private STexturedButton optionButton = new STexturedButton(Swinger.getResource("optionButton.png"));
	
	private RamSelector ramSelector = new RamSelector(new File(Launcher.LOS_DIR, "ram.txt"));
	
	private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private JLabel infoLabel = new JLabel(" ");
	
	
	
	public LauncherPanel() {
		this.setLayout(null);
		
		
		usernameField.setForeground(Color.BLACK);
		usernameField.setFont(usernameField.getFont().deriveFont(20F));
		usernameField.setCaretColor(Color.black);
		usernameField.setOpaque(false);
		usernameField.setBorder(null);
		usernameField.setBounds(591, 194, 285, 52);
		this.add(usernameField);
		//playbutton: 339, 467, 568, 586 874 246

		passwordField.setForeground(Color.BLACK);
		passwordField.setFont(usernameField.getFont());
		passwordField.setCaretColor(Color.black);
		passwordField.setOpaque(false);
		passwordField.setBorder(null);
		passwordField.setBounds(591, 332, 285, 52);
		this.add(passwordField);
		
		playButton.setBounds(591, 430);
		playButton.addEventListener(this);
		this.add(playButton);
		
		quitButton.setBounds(933, 6);
		quitButton.addEventListener(this);
		this.add(quitButton);
		
		hideButton.setBounds(896, 6);
		hideButton.addEventListener(this);
		this.add(hideButton);
		
		progressBar.setBounds(5, 600, 965, 18);
		this.add(progressBar);
		
		infoLabel.setBounds(7, 578, 965, 20);
		infoLabel.setFont(usernameField.getFont());
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(infoLabel);
		
		this.optionButton.addEventListener(this);
		this.optionButton.setBounds(833, 430);
		this.add(optionButton);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEvent(SwingerEvent e) {
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || passwordField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Erreur! L'un des champs de connexion est vide");
				setFieldsEnabled(true);
				return;
			}
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Launcher.auth(usernameField.getText(), passwordField.getText());
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur! Connexion impossible: " + e.getErrorModel().getErrorMessage());
						setInfoText(" ");
						setFieldsEnabled(true);
						return;
					}
					System.out.println("Connexion effectuée");
					((Saver) saver).set("username", usernameField.getText());
					ramSelector.save();
					try {
						Launcher.update();
					} catch (Exception e) {
						Launcher.interrupThread();
						System.out.println(e.toString());
						setFieldsEnabled(true);
						setInfoText(" ");
						return;
					}
					
					System.out.println("Mise à jour effectuée");
					
					try 
					{
						Launcher.launch();
					} 
					catch (LaunchException e) {
						Launcher.interrupThread();
						LauncherFrame.getErrorUtil().catchError(e, "Impossible de lancer le jeu");
						System.out.println(e.toString());
						setFieldsEnabled(true);
						setInfoText(" ");
					}
					System.out.println("Lancement du jeu");
				}
			};
			t.start();
			
		} else if(e.getSource() == quitButton) {
			System.exit(0);
		} else if(e.getSource() == hideButton) {
			LauncherFrame.getInstance().setState(JFrame.ICONIFIED);
		} else if(e.getSource() == this.optionButton) {
			ramSelector.display();
			
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		Swinger.drawFullsizedImage(g, this, background);
		g.drawImage(logo, 141, 154, this);
	}
	
	private void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		playButton.setEnabled(enabled);
		optionButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {
		return progressBar;
	}

	public void setInfoText(String text) {
		infoLabel.setText(text);
	}
	
	public RamSelector getRamSelector() {
		return ramSelector;
	}
}
