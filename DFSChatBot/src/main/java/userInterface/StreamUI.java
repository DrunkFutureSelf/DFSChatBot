package userInterface;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.Dao;
import entities.StreamUIElement;
import entities.StreamUIProfile;

public class StreamUI  extends JFrame implements ActionListener{

	private static final long serialVersionUID = 4411487660213040921L;
	
	private StreamUIElement[] elements;
	private Vector<StreamUIProfile> profiles;
	private StreamUIProfile activeProfile;
	private DrawCanvas canvas;


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

		c.add(canvas);
		pack();
		setSize(activeProfile.getWidth(), activeProfile.getHeight());
		setResizable(false);

	}
	public void showPage() {
		setup();
		setTitle("DFS ChatBot Stream overlay" );
		setVisible(true);
		invalidate();
		repaint();
	}
	public void refreshPage() {
		Dimension d = new Dimension(activeProfile.getWidth(),activeProfile.getHeight());

		pack();
		setTitle("DFS ChatBot Stream overlay" );
		setSize(d);
		setVisible(true);
		repaint();

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
	@Override
	public void dispose() {
		
	}
	private class DrawCanvas extends JPanel{
		private static final long serialVersionUID = 4851573556281581186L;
		private Dimension dimension;
		
		public void setDimension(Dimension d) {
			this.dimension=d;
		}
		@Override
		public void paintComponent(Graphics g) {
			Graphics offScreen = g;
			super.paintComponent(offScreen);
			//if (activeProfile.getBackGroundColor()) {
				offScreen.setColor(activeProfile.getBackGroundColor());
				offScreen.fillRect(0, 0, activeProfile.getWidth(), activeProfile.getHeight());
//			}
			for (StreamUIElement element: elements) {
				offScreen.setFont(element.getFont());
				FontMetrics fm = offScreen.getFontMetrics(element.getFont());

				int textoffset = fm.getAscent();
				int xpos = element.getXPosition();
				if (element.getIcon()!=null && !element.getIcon().equals("")) {
					try {
						BufferedImage img = ImageIO.read(new File(element.getIcon()));
						if (img != null) {
							offScreen.drawImage(img, element.getXPosition(), element.getYPosition(), img.getWidth(), img.getHeight(), null );
							xpos+=img.getWidth()+5;
							textoffset+=(img.getHeight()-(fm.getAscent()+fm.getDescent()))/2;
							if (textoffset<0)textoffset=0;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				offScreen.setColor(element.getTextColor());
				offScreen.drawString(element.getText().replace("#", element.getDisplayValue()), xpos, element.getYPosition()+textoffset);				
			}
		g=offScreen;	
		}
	}
}
