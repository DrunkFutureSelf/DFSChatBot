package userInterface;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import database.Dao;
import entities.StreamUIElement;
import entities.StreamUIProfile;
import userInterface.modify.ModifyUIElement;
import userInterface.modify.StreamUIProfileEditor;


public class StreamUIDesigner extends JFrame implements ActionListener,MouseMotionListener, MouseListener {
	
	private static final long serialVersionUID = 8031506388137234073L;
	private DrawCanvas canvas;
	private Vector<StreamUIElement> items;
	JMenuItem jmiNew;
	JMenuItem jmiNewItem;
	JMenuItem jmiSave;
	JMenuItem jmiLoad;
	JMenuItem jmiClose;
	JMenuItem jmiDelItem;
	JMenuItem jmiDelProf;
	private int indexToMove=-1;
	private char resize = 'N'; //N=no , H=Horizontal, V=Vertical, M=Move
	private final int defaultBoxSizeX = 85;
	private final int defaultBoxSizeY = 30;
	private int leftEdge;
	
	private StreamUIProfile activeProfile;
	Dao databaseDao = new Dao();
	StreamUIDesigner(){
		indexToMove=-1;
		addMouseMotionListener(this);
		addMouseListener(this);
		Container c =  getContentPane();
		items = new Vector<StreamUIElement>();
		JMenuBar jmb = new JMenuBar();
		
		JMenu jmFile = new JMenu("File");
		jmiNew = new JMenuItem("New Page");
		jmiDelProf = new JMenuItem("Delete Page");
		jmiSave = new JMenuItem("Save");
		jmiLoad = new JMenuItem("Load");
		jmiClose = new JMenuItem("Close");
 		 
		JMenu jmEdit = new JMenu ("Edit");
		jmiNewItem = new JMenuItem("New Item...");
		jmiDelItem = new JMenuItem("Delete Item...");
		 
		
		jmiNew.addActionListener(this);
		jmiLoad.addActionListener(this);
		jmiNewItem.addActionListener(this);
		jmiSave.addActionListener(this);
		jmiClose.addActionListener(this);
		jmiDelItem.addActionListener(this);
		jmiDelProf.addActionListener(this);
		
		canvas = new DrawCanvas();
		
		{
			Dao Database = new Dao();
			if (Database.getAllUIProfileNames().size()==0)
				jmiLoad.setEnabled(false);

		}
		
		jmFile.add(jmiNew);
		jmFile.add(jmiDelProf);
		jmFile.add(jmiSave);
		jmFile.add(jmiLoad);
		jmFile.add(jmiClose);

		jmEdit.add(jmiNewItem);
		jmEdit.add(jmiDelItem);
		
		jmb.add(jmFile);
		jmb.add(jmEdit);
		
		jmiSave.setEnabled(false);
		jmiNewItem.setEnabled(false);
		jmiDelItem.setEnabled(false);

		setJMenuBar(jmb);
		c.add(canvas);

		pack();

		setTitle("Stream overlay designer");
		setSize(350,150);
		setVisible(true);
 
	}

	private class DrawCanvas extends JPanel{
		private static final long serialVersionUID = 4851573556281581186L;
		private Dimension dimension;
		
		public void setDimension(int w,int h) {
			this.dimension.setSize(w, h);
		}
		public Dimension getDimenstion() {
			return dimension;
		}
		public DrawCanvas() {
			dimension =new Dimension();
			//dimension.setSize(500, 500);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			leftEdge = ((int)dimension.getWidth()-100);
			int boxIndent = leftEdge + 10;
			int boxPadding = 10;
			int nextSpace = 25;
			g.setColor(Color.black);
			g.setFont(new Font("Segoe UI",Font.BOLD,18));
			g.drawString("Unused", (int) dimension.getWidth()-85, 20);
			g.drawRect(0,0,leftEdge,((int)dimension.getHeight()-100));

			if (items.size() >0) {
				for(StreamUIElement element: items) {
					if (element!=null) {
						if (element.getHeight()==0) element.setHeight(defaultBoxSizeY);
						if (element.getWidth()==0) element.setWidth(defaultBoxSizeX);
						if (!element.isActive()) {
							g.drawRect(boxIndent, nextSpace, element.getWidth(), element.getHeight());
							element.setXPosition(boxIndent);
							element.setYPosition(nextSpace);
							nextSpace +=element.getHeight()+boxPadding;
						}
						else {
							g.drawRect(element.getXPosition(), element.getYPosition(), element.getWidth(), element.getHeight());
						}
						g.fillOval(element.getXPosition()+(element.getWidth()/2)-3, element.getYPosition()-3+element.getHeight(),6,6);
						g.fillOval(element.getXPosition()+(element.getWidth())-3, element.getYPosition()-3+(element.getHeight()/2),6,6);
						
					}
					g.drawString(element.getName(), element.getXPosition()+5, element.getYPosition()+5+g.getFont().getSize());
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==jmiSave)	{

			Dimension d = canvas.getSize();
			d.setSize(d.getWidth()+100, d.getHeight()+100);

			jmiLoad.setEnabled(true);

			pack();

			setTitle("Stream overlay designer "+activeProfile.getName());
			setSize(d);
			setVisible(true);
			repaint();

		}

		if (e.getSource()==jmiNew) {
			activeProfile = new StreamUIProfile();
			StreamUIProfileEditor dialog = new StreamUIProfileEditor(this, activeProfile );
			activeProfile =dialog.run();
			canvas.setDimension(activeProfile.getWidth()+100,activeProfile.getHeight()+100);
			canvas.setSize(canvas.getDimenstion());
			setTitle("Stream overlay designer: "+activeProfile.getName());

			jmiSave.setEnabled(true);
			jmiNewItem.setEnabled(true);
			jmiDelItem.setEnabled(true);
			Dao Database = new Dao();
			if (activeProfile.getBackGroundColor() == null)
				activeProfile.setBackGroundColor(new Color(0,0,0,100));
			Database.addUIProfile(activeProfile.getName(), activeProfile.getHeight(), activeProfile.getWidth(), activeProfile.getBackGroundColor().getRGB());

			pack();

			setTitle("Stream overlay designer "+activeProfile.getName());
			setSize(canvas.getDimenstion());
			setVisible(true);
			repaint();
		}
		if (e.getSource()==jmiDelProf) {
			Dao Database = new Dao();
			Vector<String> profiles = Database.getAllUIProfileNames();
			String input =((String)JOptionPane.showInputDialog(null,"Delete Profile","",JOptionPane.QUESTION_MESSAGE,null,profiles.toArray(new String[profiles.size()]),""));
			if (!input.equals("") &&input!=null) {
				if(Database.deleteUIProfile(input))
					Database.removeAllUIElementsByProfile(input);
			}

		}
		if (e.getSource() == jmiLoad) {
			Dao Database = new Dao();
			Vector<String> profiles = Database.getAllUIProfileNames();
			String input =((String)JOptionPane.showInputDialog(null,"Open Profile","",JOptionPane.QUESTION_MESSAGE,null,profiles.toArray(new String[profiles.size()]),""));
			for (StreamUIProfile suip : Database.getAllUIProfiles()) {
				if (suip.getName().equals(input)) {
					activeProfile = suip;
				}
			}
			items= new Vector<StreamUIElement>();
			for (StreamUIElement element : Database.getUIElementsByProfile(activeProfile.getName())) {
				items.add(element);
			}
			
			canvas.setDimension(activeProfile.getWidth()+100,activeProfile.getHeight()+100);
			jmiSave.setEnabled(true);
			jmiNewItem.setEnabled(true);
			jmiDelItem.setEnabled(true);

			pack();

			setTitle("Stream overlay designer "+ activeProfile.getName());
			setSize(canvas.getDimenstion());
			setVisible(true);
			repaint();

		}
		if (e.getSource() == jmiClose) {
			dispose();
		}
		if (e.getSource() == jmiNewItem){
			StreamUIElement item = new StreamUIElement();
			ModifyUIElement element = new ModifyUIElement(this,item);
			item=element.run();
			if (item != null) {
				
				Dao Database = new Dao();
				if (item.getBackgroundColor()==null)
					item.setBackgroundColor(new Color(0,0,0,100));
				if (item.getTextColor()== null)
					item.setTextColor(new Color(0,0,0,100));
				item.setProfile(activeProfile.getName());
				Database.addUIElement(item);
				items.add(Database.getUIElementByName(item.getName(), activeProfile.getName()));

				pack();
				
				setTitle("*Stream overlay designer "+ activeProfile.getName());
				setSize(canvas.getDimenstion());
				setVisible(true);
				repaint();
			}
		}
		if (e.getSource() == jmiDelItem) {
			Dao Database = new Dao();
			StreamUIElement[] elemt = Database.getUIElementsByProfile(activeProfile.getName());
			String[] elemtNames = new String[elemt.length];
			for (int i = 0 ;i< elemt.length;i++) {
				elemtNames[i]=elemt[i].getName();
			}
			String input =(String)JOptionPane.showInputDialog(null,"Open Profile","",JOptionPane.QUESTION_MESSAGE,null,elemtNames,"");
			Vector<StreamUIElement> removeItems = new Vector<StreamUIElement>();
			for (StreamUIElement el : items) {
				if (el.getName().equals(input)) {
					removeItems.add(el);
					Database.deleteUIElement(el.getId());
				}
			}
			items.removeAll(removeItems);
		//	d.setSize(d.getWidth(),d.getHeight());
			pack();

			setTitle("*Stream overlay designer "+ activeProfile.getName());
			setSize(canvas.getDimenstion());
			setVisible(true);
			repaint();

		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (indexToMove !=-1 ) {
			if (resize=='M') {
				items.get(indexToMove).setXPosition((int)canvas.getMousePosition().getX()-10);
				items.get(indexToMove).setYPosition((int)canvas.getMousePosition().getY()-10);
				items.get(indexToMove).setActive(true);
			}
			else if(resize=='H'){
				if (e.getY()-50-items.get(indexToMove).getYPosition() >0)
				items.get(indexToMove).setHeight(e.getY()-50-items.get(indexToMove).getYPosition());
			}
			else if(resize=='V') {
				if (e.getX()-items.get(indexToMove).getXPosition() >0)
				items.get(indexToMove).setWidth(e.getX()-items.get(indexToMove).getXPosition());
			}
		}
		else {
			for(int i = 0; i <items.size(); i++) {
				 StreamUIElement element=items.get(i);
				if (e.getX()>=(element.getXPosition()+(element.getWidth()/2)-6) &&
					e.getX()>=(element.getXPosition()+(element.getWidth()/2)+6) &&
					e.getY()-50>= element.getYPosition()+element.getHeight() -6 &&
					e.getY()-50<= element.getYPosition()+element.getHeight() +6 &&
					element.isActive()) {
					indexToMove=i;
					resize='H';
				}
				else if (e.getX()>=(element.getXPosition()+element.getWidth()-6) &&
					e.getX()>=(element.getXPosition()+element.getWidth()+6) &&
					e.getY()-50>=(element.getYPosition()+element.getHeight()/2) -6 &&
					e.getY()-50<=(element.getYPosition()+element.getHeight()/2) +6 &&
					element.isActive()){
					indexToMove=i;
					resize='V';
				}
	
				else if (e.getX()>=element.getXPosition() &&
					e.getX()<=element.getXPosition()+element.getWidth() &&
					e.getY()-50>=element.getYPosition() &&
					e.getY()-50<=element.getYPosition()+element.getHeight()) {
					indexToMove=i;
					resize='M';
			
				}
			}
		}
		if (indexToMove>=0) {
			pack();
			setTitle("* Stream overlay designer "+ activeProfile.getName() );
			setSize(canvas.getDimenstion());
			setVisible(true);
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Dao Database = new Dao();
		if (indexToMove >=0) {
			
			if(resize == 'M') 
				Database.updateUIElementLocation(items.get(indexToMove).getId(), items.get(indexToMove).getXPosition(), items.get(indexToMove).getYPosition());
			else if(resize =='H'||resize=='W') 
				Database.updateUIElementDimention(items.get(indexToMove).getId(), items.get(indexToMove).getWidth(), items.get(indexToMove).getHeight());
			
			items.get(indexToMove).setActive(e.getX() < activeProfile.getWidth() && (e.getY())<activeProfile.getHeight());
			Database.updateUIElementActive(items.get(indexToMove).getId(),items.get(indexToMove).isActive());
			indexToMove=-1;
			resize='N';
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
