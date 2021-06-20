package userInterface;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.Dao;
import entities.StreamUIElement;
import entities.StreamUIProfile;

public class ModifyUIElement extends JDialog implements ActionListener {
	private static final long serialVersionUID = -8996371640004260802L;
	
	private JButton jbtnOkay;
	private JButton jbtnCancel;
	private JTextField jtfText;
	private JButton btnIcon;
	private JButton btnFont;
	private JComboBox<String> jcbDataSource;
	private JButton btnFontColor;
	private JButton btnBackgroundColor;
	private Color foreGround;
	private Color backGround;
	private JTextField jtfName;
	private JLabel lblBackgroundColorDisplay;
	private JLabel lblIconDisplay;
	private JLabel lblFontColorDisplay;
	private JLabel lblFontDisplay;
	private StreamUIElement data;
	
	private JFrame callingframe;

	public ModifyUIElement(JFrame frame, StreamUIElement prof) {
		super(frame, "Create/Modify item",true);
		callingframe=frame;
		
		data = prof;
		Dao database = new Dao();
		
		JPanel mainPan = new JPanel();
		JLabel lblName = new JLabel("Name");
		JLabel lblText = new JLabel("Display Text");
		JLabel lblIcon = new JLabel("Display Icon");
		JLabel lblDataSource = new JLabel("Source Command");
		JLabel lblFontColor = new JLabel("Font Color");
		JLabel lblBackgroundColor = new JLabel("Background Color");
		JLabel lblFont = new JLabel("Font");
		
		JLabel lblBlank3 = new JLabel("");
		lblIconDisplay = new JLabel(""); 
		lblFontDisplay = new JLabel("");
		lblFontColorDisplay = new JLabel("");
		lblBackgroundColorDisplay = new JLabel("");
		JLabel lblBlank = new JLabel("");
		JLabel lblBlank2 = new JLabel("");
		
		jtfName = new JTextField();
		jtfText = new JTextField();
		btnIcon = new JButton("Select Icon");
		btnFont = new JButton("Select Font");
		jcbDataSource = new JComboBox<String>(database.getCommands());
		btnFontColor = new JButton("Select Color");
		btnBackgroundColor = new JButton("Select Color");
		
		jbtnOkay = new JButton("Okay");
		jbtnCancel= new JButton("Cancel");
		
		mainPan.setLayout(new GridLayout(7,3));
		
		lblFontColorDisplay.setBackground(Color.BLACK);
		lblBackgroundColorDisplay.setBackground(Color.BLACK);
		lblFontColorDisplay.setOpaque(true);
		lblBackgroundColorDisplay.setOpaque(true);
		
		
		jbtnOkay.addActionListener(this);
		jbtnCancel.addActionListener(this);
		btnFont.addActionListener(this);
		btnBackgroundColor.addActionListener(this);
		btnFontColor.addActionListener(this);
		btnIcon.addActionListener(this);
		
		mainPan.add(lblName);
		mainPan.add(jtfName);
		mainPan.add(lblBlank3);
		
		mainPan.add(lblText);
		mainPan.add(jtfText);
		mainPan.add(lblBlank);
		
		mainPan.add(lblIcon);
		mainPan.add(btnIcon);
		mainPan.add(lblIconDisplay);
		
		mainPan.add(lblDataSource);
		mainPan.add(jcbDataSource);
		mainPan.add(lblBlank2);
		
		mainPan.add(lblFontColor);
		mainPan.add(btnFontColor);
		mainPan.add(lblFontColorDisplay);
		
		/*
		 * 		mainPan.add(lblBackgroundColor);
		 * 		mainPan.add(btnBackgroundColor);
		 * 		mainPan.add(lblBackgroundColorDisplay);
		*/
		mainPan.add(lblFont);
		mainPan.add(btnFont);
		mainPan.add(lblFontDisplay);
		
		mainPan.add(jbtnOkay);
		mainPan.add(jbtnCancel);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		add(mainPan);
		setSize(500,200);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==jbtnOkay) {
			Dao database = new Dao();
			if (jtfName.getText().isBlank()) {
				data = null;
			}
			else {
				data.setName(jtfName.getText());
				data.setText(jtfText.getText());
				//	data.setIcon();
				
				data.setBackgroundColor(backGround);
				data.setTextColor(foreGround);
				data.setCommand(database.chatCommand(jcbDataSource.getSelectedItem().toString()));
			}
			dispose();
		}
		else if (e.getSource() == btnFont) {
		    Font font = FontChooser.showDialog(callingframe, "Font");
		    data.setFont(font);
		    lblFontDisplay.setFont(font);
		    lblFontDisplay.setText(font.getFamily());
		    repaint();
		}
		else if (e.getSource()==btnBackgroundColor) {
			backGround= JColorChooser.showDialog(callingframe, "Choose a Color", backGround);
			lblBackgroundColorDisplay.setBackground(backGround);
			repaint();
		}
		else if (e.getSource()==btnFontColor) {
			foreGround= JColorChooser.showDialog(callingframe, "Choose a Color", backGround);
			lblFontColorDisplay.setBackground(foreGround);
			repaint();
		}
		else if (e.getSource()==btnIcon) {
			JFileChooser fc = new JFileChooser();
	        int returnVal = fc.showOpenDialog(callingframe);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            data.setIcon(fc.getSelectedFile().getAbsolutePath());
	            lblIconDisplay.setIcon(new ImageIcon(fc.getSelectedFile().getAbsolutePath()));
	            repaint();
	        }
		}
		else if (e.getSource() == jbtnCancel ) {
			data = null;
			dispose();
		}
		
	}
	public void setData(StreamUIElement element) {
		data=element;
	}
	public StreamUIElement run() {
		this.setVisible(true);
		return data;
	}


}
