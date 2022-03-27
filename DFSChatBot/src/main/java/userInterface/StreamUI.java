package userInterface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import database.Dao;
import entities.StreamUIElement;
import entities.StreamUIProfile;

public class StreamUI  extends JFrame  {

	private static final long serialVersionUID = 4411487660213040921L;
	
	private StreamUIElement[] elements;
	private Vector<StreamUIProfile> profiles;
	private StreamUIProfile activeProfile;
	private DrawCanvas canvas;
	private int secondscount=0;
	private Timer timer;
	private boolean queueActive;
	private Dimension dimension;
	private Vector<StreamUIElement> queue;
	private boolean gif=false;
	private final int interval = 100;




	public boolean isWatched(String value) {
		boolean returnValue = false;
		for (StreamUIElement i : elements) {
			if (i.getCommand().getName().equals(value))
				returnValue=true;
		}
		return returnValue;
	}
	
	public Vector<StreamUIElement> getItemsByCommand(String command){
		Vector<StreamUIElement> returnValue = new Vector<StreamUIElement>();
		for (StreamUIElement sui : elements) {
			if (sui.getCommand().getName().equals(command)) {
				returnValue.add(sui);
			}
		}
		return returnValue;
	}
	private void setup() {
		String propsFile = "./bot.properties";
		dimension= new Dimension();
		queue=new Vector<StreamUIElement>();

		InputStream input;
		try{
			input= new FileInputStream(propsFile);
			Properties props = new Properties();
			props.load(input);
			
			Dao database = new Dao();
			String StreamCurrent="";
			Vector<String> availableProfiles=database.getAllUIProfileNames();
			if (availableProfiles.size()==1) {
				StreamCurrent=availableProfiles.lastElement();
			}
			if (availableProfiles.size()>1) {
				
				StreamCurrent=((String)JOptionPane.showInputDialog(null,"Open Profile","",JOptionPane.QUESTION_MESSAGE,null,availableProfiles.toArray(new String[availableProfiles.size()]),""));
			}
        	elements=database.getUIElementsByProfile(StreamCurrent);
        	profiles=database.getAllUIProfiles();
        	for (StreamUIProfile e : profiles) {
        		if (e.getName().equals(StreamCurrent)) {
        			activeProfile=e;
        		}
        	}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Container c =  getContentPane();
		canvas = new DrawCanvas();
		canvas.setSize(activeProfile.getWidth(), activeProfile.getHeight());
		dimension.setSize(activeProfile.getWidth(), activeProfile.getHeight());

		c.add(canvas);
		pack();
		setSize(activeProfile.getWidth(), activeProfile.getHeight());
		setResizable(false);

	}
	public void showPage() {
		setup();
		for (StreamUIElement element:elements) {
			if (element.getDuration()==0)
				element.setVisible(true);
		}

		setTitle("DFS ChatBot Stream overlay" );
		setVisible(true);
		invalidate();
		repaint();
	}
	public void refreshPage() {
		Dimension d = new Dimension(activeProfile.getWidth(),activeProfile.getHeight());
		for (StreamUIElement element:elements) {
			if (element.getDuration()==0)
				element.setVisible(true);
		}

		pack();
		setTitle("DFS ChatBot Stream overlay" );
		setSize(d);
		setVisible(true);
		repaint();

	}
	public void checkForAnimation(String commandName) {
		for (StreamUIElement element:elements) {
			if (element.getDuration()==0)
				element.setVisible(true);

			if (element.getDuration()>0 &&
				element.getCommand().getName().equals(commandName))
			{
				queue.add(element);
			}
		}
		if (!queueActive&&!queue.isEmpty())
			timedRefresh();
		else
			refreshPage(); 
	}
	private void timedRefresh() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(queue.get(0).getIcon()));

			if (br.readLine().substring(0, 6).equals("GIF89a")) 
				gif=true;	

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		queue.get(0).setVisible(true);
		timer=new Timer(interval,new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				queueActive=true;
				secondscount+=interval;
				if (gif)
					repaint();
				if (secondscount==queue.get(0).getDuration()*1000)
				{	
					queue.get(0).setVisible(false);
					queue.remove(queue.get(0));
					if (queue.isEmpty()) {
						timer.stop();
						queueActive=false;
					}
					else {
						queue.get(0).setVisible(true);
						try {
							BufferedReader br = new BufferedReader(new FileReader(queue.get(0).getIcon()));
						if (br.readLine().substring(0, 6).equals("GIF89a"))
							gif=true;

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					secondscount=0;
					refreshPage();
				}
			}
		});
		timer.start();
	}

	private class DrawCanvas extends JPanel{
		private static final long serialVersionUID = 4851573556281581186L;
		
		@Override
		public void paintComponent(Graphics g) {

			BufferedImage bi = new BufferedImage((int)dimension.getWidth(),(int)dimension.getHeight(),BufferedImage.TYPE_INT_RGB);
			Graphics2D offScreen = bi.createGraphics();

			//if (activeProfile.getBackGroundColor()) {
				offScreen.setColor(activeProfile.getBackGroundColor());
				offScreen.fillRect(0, 0, activeProfile.getWidth(), activeProfile.getHeight());
//			}
			for (StreamUIElement element: elements) {
				if (element.isActive()&& element.isVisible()) {
					offScreen.setFont(element.getFont());
					FontMetrics fm = offScreen.getFontMetrics(element.getFont());
	
					int textoffset = fm.getAscent();
					int xpos = element.getXPosition();
					if (element.getIcon()!=null && !element.getIcon().equals("")) {
						try {
							Image img = new ImageIcon(element.getIcon()).getImage();
							if (img != null) {
								if (element.getWidth()==0 || element.getHeight()==0 ) {
									offScreen.drawImage(img, element.getXPosition(), element.getYPosition(), null );
								}
								else {
									offScreen.drawImage(img, element.getXPosition(), element.getYPosition(), element.getWidth(), element.getWidth(),null);
								}
								BufferedImage bimg = ImageIO.read(new File(element.getIcon()));

								xpos+=bimg.getWidth()+5;
								textoffset+=(bimg.getHeight()-(fm.getAscent()+fm.getDescent()))/2;
								if (textoffset<0)textoffset=0;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					offScreen.setColor(element.getTextColor());
					if (element.getDisplayValue()==null) element.setDisplayValue("");
					offScreen.drawString(element.getText().replace("#", element.getDisplayValue()), xpos, element.getYPosition()+textoffset);				
				}
			}
			g.drawImage(bi,0,0,this);
		}
	}
}
