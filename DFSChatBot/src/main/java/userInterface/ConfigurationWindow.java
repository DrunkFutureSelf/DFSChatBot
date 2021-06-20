package userInterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Properties;

import javax.swing.text.AbstractDocument.Content;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.TextAction;

import database.Dao;
import entities.User;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

public class ConfigurationWindow extends JPanel{

	private static final long serialVersionUID = -41481386375428566L;

	String propsFile = "./bot.properties";

	public ConfigurationWindow() throws IOException  {
		InputStream input;
		try{
			input= new FileInputStream(propsFile);
		}catch(FileNotFoundException e) {
			new File(propsFile).createNewFile();
			input = new FileInputStream(propsFile);
		}
		Properties props = new Properties();

		props.load(input);
		
		String propDatabase = props.getProperty("database");
		String propChannelToJoin= props.getProperty("channelToJoin");
		String propOauth = props.getProperty("oauth");
		String propUserName=props.getProperty("userName");
		String propPrefix = props.getProperty("prefix");
	
		JPanel panel = new JPanel();
		JLabel usr = new JLabel("Bot user name");
		JLabel oauth = new JLabel("OAuth");
		JLabel database = new JLabel("Database Location");
		JLabel chat = new JLabel("Chat to join");
		JLabel lblPrefix = new JLabel("Command Prefix");
		
		JTextField inputUsr = new JTextField(15);
		JPasswordField inputOauth = new JPasswordField(15);
		JTextField inputDatabase = new JTextField(15);
		JTextField inputChat = new JTextField(15);
		JTextField inputPrefix = new JTextField(1);
		
		JButton save = new JButton("Save");
		JButton clear = new JButton("Clear");
		
		JPopupMenu rightClick = new JPopupMenu();
		JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
		JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
		JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
		
		inputUsr.setText(propUserName);
		inputChat.setText(propChannelToJoin);
		inputOauth.setText(propOauth);
		inputDatabase.setText(propDatabase);
		inputPrefix.setText(propPrefix);
		
		rightClick.add(cut);
		rightClick.add(copy);
		rightClick.add(paste);
		
		inputUsr.setComponentPopupMenu(rightClick);
        inputOauth.setComponentPopupMenu(rightClick);
        inputDatabase.setComponentPopupMenu(rightClick);
        inputChat.setComponentPopupMenu(rightClick);
        
        
        save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream input = new FileInputStream(propsFile);

					props.load(input);
					props.setProperty("userName",inputUsr.getText().toLowerCase());
					props.setProperty("oauth", "oauth:" +new String(inputOauth.getPassword()).replace("oauth:", ""));
					props.setProperty("database",inputDatabase.getText());
					props.setProperty("channelToJoin", inputChat.getText().toLowerCase());
					props.setProperty("prefix", inputPrefix.getText());
					
					input.close();
					OutputStream output = new FileOutputStream("./bot.properties");
		            props.store(output, null);
		        	Dao database = new Dao();
		        	if (!database.checkStatusOfDatabase())
		        	{
		        		database.setupDatabase();
		        		JOptionPane.showMessageDialog(null,"Database has been setup");
		        	}
		        	else {
		    			if (!database.getVersionNumber().equals(database.getLatestVersion())) 
		    				database.performConvert();
		        		JOptionPane.showMessageDialog(null,"Database has been found and is ready to be used");
		        	}

				} catch (FileNotFoundException e1) {
					System.err.println("your workign directory must be where the jar file is :( can't figure this one out");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}});
        
        clear.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		inputUsr.setText("");
        		inputOauth.setText("");
        		inputDatabase.setText("");
        		inputChat.setText("");
			}});
        
        panel.setLayout(new GridLayout(6,2));
        
        panel.add(usr);
        panel.add(inputUsr);
        panel.add(oauth);
        panel.add(inputOauth);
        panel.add(database);
        panel.add(inputDatabase);
        panel.add(chat);
        panel.add(inputChat);
        panel.add(lblPrefix);
        panel.add(inputPrefix);
        
        panel.add(save);
        panel.add(clear);
        
        
        add(panel);
	}
}
