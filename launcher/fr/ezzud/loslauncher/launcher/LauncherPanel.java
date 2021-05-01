// 
// Decompiled by Procyon v0.5.36
// 

package fr.ezzud.loslauncher.launcher;

import java.awt.image.ImageObserver;
import javax.swing.JComponent;
import java.awt.Graphics;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.litarvan.openauth.AuthenticationException;
import javax.swing.JOptionPane;
import fr.theshark34.swinger.event.SwingerEvent;
import java.awt.Component;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.LayoutManager;
import java.io.File;
import fr.theshark34.swinger.Swinger;
import javax.swing.JLabel;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.textured.STexturedButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import fr.theshark34.openlauncherlib.util.Saver;
import java.awt.image.BufferedImage;
import java.awt.Image;
import fr.theshark34.swinger.event.SwingerEventListener;
import javax.swing.JPanel;

public class LauncherPanel extends JPanel implements SwingerEventListener
{
    private Image background;
    private BufferedImage logo;
    private Saver saver;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private STexturedButton playButton;
    private STexturedButton quitButton;
    private STexturedButton hideButton;
    private STexturedButton optionButton;
    private RamSelector ramSelector;
    private SColoredBar progressBar;
    private JLabel infoLabel;
    
    public LauncherPanel() {
        this.background = Swinger.getResource("background.png");
        this.logo = Swinger.getResource("logo.png");
        this.saver = new Saver(new File(Launcher.LOS_DIR, "launcher.properties"));
        this.usernameField = new JTextField(this.saver.get("username"));
        this.passwordField = new JPasswordField();
        this.playButton = new STexturedButton(Swinger.getResource("playButton.png"));
        this.quitButton = new STexturedButton(Swinger.getResource("quitButton.png"));
        this.hideButton = new STexturedButton(Swinger.getResource("hideButton.png"));
        this.optionButton = new STexturedButton(Swinger.getResource("optionButton.png"));
        this.ramSelector = new RamSelector(new File(Launcher.LOS_DIR, "ram.txt"));
        this.progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
        this.infoLabel = new JLabel(" ");
        this.setLayout(null);
        this.usernameField.setForeground(Color.BLACK);
        this.usernameField.setFont(this.usernameField.getFont().deriveFont(20.0f));
        this.usernameField.setCaretColor(Color.black);
        this.usernameField.setOpaque(false);
        this.usernameField.setBorder(null);
        this.usernameField.setBounds(591, 194, 285, 52);
        this.add(this.usernameField);
        this.passwordField.setForeground(Color.BLACK);
        this.passwordField.setFont(this.usernameField.getFont());
        this.passwordField.setCaretColor(Color.black);
        this.passwordField.setOpaque(false);
        this.passwordField.setBorder(null);
        this.passwordField.setBounds(591, 332, 285, 52);
        this.add(this.passwordField);
        this.playButton.setBounds(591, 430);
        this.playButton.addEventListener((SwingerEventListener)this);
        this.add((Component)this.playButton);
        this.quitButton.setBounds(933, 6);
        this.quitButton.addEventListener((SwingerEventListener)this);
        this.add((Component)this.quitButton);
        this.hideButton.setBounds(896, 6);
        this.hideButton.addEventListener((SwingerEventListener)this);
        this.add((Component)this.hideButton);
        this.progressBar.setBounds(5, 600, 965, 18);
        this.add((Component)this.progressBar);
        this.infoLabel.setBounds(7, 578, 965, 20);
        this.infoLabel.setFont(this.usernameField.getFont());
        this.infoLabel.setForeground(Color.WHITE);
        this.infoLabel.setHorizontalAlignment(0);
        this.add(this.infoLabel);
        this.optionButton.addEventListener((SwingerEventListener)this);
        this.optionButton.setBounds(833, 430);
        this.add((Component)this.optionButton);
    }
    
    public void onEvent(final SwingerEvent e) {
        if (e.getSource() == this.playButton) {
            this.setFieldsEnabled(false);
            if (this.usernameField.getText().replaceAll(" ", "").length() == 0 || this.passwordField.getText().length() == 0) {
                JOptionPane.showMessageDialog(this, "Erreur! L'un des champs de connexion est vide");
                this.setFieldsEnabled(true);
                return;
            }
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Launcher.auth(LauncherPanel.this.usernameField.getText(), LauncherPanel.this.passwordField.getText());
                    }
                    catch (AuthenticationException e) {
                        JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur! Connexion impossible: " + e.getErrorModel().getErrorMessage());
                        LauncherPanel.this.setInfoText(" ");
                        LauncherPanel.this.setFieldsEnabled(true);
                        return;
                    }
                    System.out.println("Connexion effectu\u00e9e");
                    LauncherPanel.this.saver.set("username", LauncherPanel.this.usernameField.getText());
                    LauncherPanel.this.ramSelector.save();
                    try {
                        Launcher.update();
                    }
                    catch (Exception e2) {
                        Launcher.interrupThread();
                        System.out.println(e2.toString());
                        LauncherPanel.this.setFieldsEnabled(true);
                        LauncherPanel.this.setInfoText(" ");
                        return;
                    }
                    System.out.println("Mise \u00e0 jour effectu\u00e9e");
                    try {
                        Launcher.launch();
                    }
                    catch (LaunchException e3) {
                        Launcher.interrupThread();
                        LauncherFrame.getErrorUtil().catchError((Exception)e3, "Impossible de lancer le jeu");
                        System.out.println(e3.toString());
                        LauncherPanel.this.setFieldsEnabled(true);
                        LauncherPanel.this.setInfoText(" ");
                    }
                    System.out.println("Lancement du jeu");
                }
            };
            t.start();
        }
        else if (e.getSource() == this.quitButton) {
            System.exit(0);
        }
        else if (e.getSource() == this.hideButton) {
            LauncherFrame.getInstance().setState(1);
        }
        else if (e.getSource() == this.optionButton) {
            this.ramSelector.display();
        }
    }
    
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Swinger.drawFullsizedImage(g, (JComponent)this, this.background);
        g.drawImage(this.logo, 141, 154, this);
    }
    
    private void setFieldsEnabled(final boolean enabled) {
        this.usernameField.setEnabled(enabled);
        this.passwordField.setEnabled(enabled);
        this.playButton.setEnabled(enabled);
        this.optionButton.setEnabled(enabled);
    }
    
    public SColoredBar getProgressBar() {
        return this.progressBar;
    }
    
    public void setInfoText(final String text) {
        this.infoLabel.setText(text);
    }
    
    public RamSelector getRamSelector() {
        return this.ramSelector;
    }
}
