package userInterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.Dao;
import twitchIRC.ConnectionFactory;

public class GraphicalInterface extends JFrame{

	private static final long serialVersionUID = 5681214557742053017L;
	ConnectionFactory cf;
	JPanel mainDisplay;
	StreamUI streamWindow;
	public GraphicalInterface()
	{
		super("DFS Chatbot");
		setLayout(new GridLayout());
		
		JMenu file = new JMenu("File");
		JMenuItem menuConfig = new JMenuItem("Configuration");
		JMenuItem editUsers = new JMenuItem("User");
		JMenuItem menuCommands = new JMenuItem("Commands");
		JMenuItem startBot = new JMenuItem("Start Bot");		
		JMenuItem menuClose = new JMenuItem("Exit");
		JMenuItem menuStopBot = new JMenuItem("Stop Bot");
		
		JMenuItem oauthHelp = new JMenuItem("OAuth");
		JMenuItem databaseHelp = new JMenuItem("Database");

		JMenu jmView = new JMenu("View");
		JMenuItem jmiStreamView = new JMenuItem("Streamer View");
		JMenuItem jmiStreamViewDesign = new JMenuItem("Streamer View Design");
		JMenu help = new JMenu("Help");
		JMenuItem menuReadMe = new JMenuItem("Read Me");
		JMenuItem about = new JMenuItem("About");
		
		JMenuBar bar = new JMenuBar();
		
		mainDisplay = new JPanel();
		
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dao database = new Dao(); 
				JOptionPane.showMessageDialog(null,"DFS ChatBot Version "+database.getLatestVersion());
			}
		});
		
		menuConfig.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mainDisplay = new ConfigurationWindow();
					getContentPane().removeAll();
					add(mainDisplay);
					invalidate();
			        setSize(360,225);
			        setVisible(true);

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		startBot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (streamWindow==null) 
					streamWindow = new StreamUI();
				cf = new ConnectionFactory(streamWindow);
				cf.start();
			}});
		
		editUsers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainDisplay=new UsersInterface();				
				getContentPane().removeAll();
				add(mainDisplay);
				invalidate();
		        setSize(360,305);
		        setVisible(true);

			}});
		menuClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		menuStopBot.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cf.stopBot();
			}
		});
		
		menuCommands.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				mainDisplay = new CommandsWindow();
				getContentPane().removeAll();
				add(mainDisplay);
				invalidate();

		        setSize(750,400);
		        setVisible(true);

			}});
		jmiStreamView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (streamWindow == null) {
					streamWindow = new StreamUI();
				}
				streamWindow.showPage();
			}
		});
		
		jmiStreamViewDesign.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				jmiStreamViewDesign.setEnabled(true);
				new StreamUIDesigner();
			}
		});
		
		menuReadMe.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new ReadMe();
			}
		});
		
        oauthHelp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null,"Head over to https://twitchapps.com/tmi/ and log in with your bot user.  Copy and paste the OAuth token into the OAuth field");
		}});
        databaseHelp.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null,"This bot runs on SQLite select a location on your harddrive and this program will create everything you will need.");
		}});
        
		
		file.add(menuConfig);
		file.add(editUsers);
		file.add(menuCommands);
		file.addSeparator();
		file.add(startBot);
		file.add(menuStopBot);
		file.addSeparator();
		file.add(menuClose);
		
		jmView.add(jmiStreamView);		
		jmView.add(jmiStreamViewDesign);
		
		help.add(menuReadMe);
        help.add(oauthHelp);
        help.add(databaseHelp);
		help.add(about);
		
		bar.add(file);
		bar.add(jmView);
		bar.add(help);
		InputStream input;
		String propsFile = "./bot.properties";
		add(mainDisplay);
        setJMenuBar(bar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try{
			Dao database = new Dao();
			if (database.getAllUIProfileNames().size()==0) {
				jmiStreamView.setEnabled(false);
			}

			input= new FileInputStream(propsFile);
			
	        setSize(360,195);
	        setResizable(true);
	        setVisible(true);

		}catch(FileNotFoundException e) {
			try {
				mainDisplay = new ConfigurationWindow();
				getContentPane().removeAll();
				add(mainDisplay);
				invalidate();
		        setSize(360,225);
		        setVisible(true);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
