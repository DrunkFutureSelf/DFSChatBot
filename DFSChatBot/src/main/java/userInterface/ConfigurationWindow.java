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
		//super("Configuration Window");
		//new File(propsFile).createNewFile();

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
		
//		JMenuBar bar = new JMenuBar();
		
/*		JMenu file = new JMenu("File");
		JMenuItem reset = new JMenuItem("Reset Database");
		JMenuItem exit = new JMenuItem("Exit");

		JMenu help = new JMenu("Help");
		JMenuItem oauthHelp = new JMenuItem("OAuth");
		JMenuItem databaseHelp = new JMenuItem("Database");
	*/	
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
/*				byte[] salt = new String("SwagDuck").getBytes();
				int iterationcount = 40000;
				int keyLength = 128;*/
				try {
//					SecretKeySpec key = createSecretKey(inputOauth.getText().toCharArray(), salt, iterationcount,keyLength);
					InputStream input = new FileInputStream(propsFile);

					props.load(input);
					props.setProperty("userName",inputUsr.getText());
					props.setProperty("oauth", "oauth:" +new String(inputOauth.getPassword()).replace("oauth:", ""));
					props.setProperty("database",inputDatabase.getText());
					props.setProperty("channelToJoin", inputChat.getText());
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
		        	else 
		        		JOptionPane.showMessageDialog(null,"Database has been found and is ready to be used");


				} catch (FileNotFoundException e1) {
					//e1.printStackTrace();
					System.err.println("your workign directory must be where the jar file is :( can't figure this one out");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
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
        /*
        exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}});
        
        file.add(reset);
        file.add(exit);
        
        help.add(oauthHelp);
        help.add(databaseHelp);
        
        bar.add(file);
        bar.add(help);*/
        
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
//        setJMenuBar(bar);
//        setSize(360,195);
//        setResizable(true);
//        setVisible(true);
	}
/*
    private static SecretKeySpec createSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes("UTF-8"));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }
    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }


    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }*/

}
