package userInterface.modify;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import entities.StreamUIProfile;

public class StreamUIProfileEditor extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3905420754754226425L;
	private StreamUIProfile data;
	private JButton jbtnOkay;
	private JButton jbtnCancel;
	
	private JTextField jtfName;
	private JTextField jtfWidth;
	private JTextField jtfHeight;
	private Color backgroundColor;

	public StreamUIProfileEditor(JFrame frame, StreamUIProfile prof) {
		super(frame,"Profile Editor",true);
		data=prof;
		

		JPanel mainPan = new JPanel();
		JLabel lblName = new JLabel("Profile Name");
		JLabel lblWidth= new JLabel("Screen Width");
		JLabel lblHeight= new JLabel("Screen Height");
		JLabel lblBackgroundColor = new JLabel("Background Color");
		
		jtfName = new JTextField();
		jtfWidth = new JTextField();
		jtfHeight = new JTextField();
		JButton btnBackgroundColor = new JButton("Select Color");
	
		JLabel lblBlank1= new JLabel();
		JLabel lblBlank2= new JLabel();
		JLabel lblBlank3= new JLabel();
		JLabel lblBackgroundColorDisplay= new JLabel();
		
		jbtnOkay= new JButton("Okay");
		jbtnCancel = new JButton("Cancel");

		jtfName.setText(data==null?"":data.getName());
		jtfWidth.setText(data==null?"":data.getWidth()+"");
		jtfHeight.setText(data==null?"":data.getHeight()+"");
		lblBackgroundColorDisplay.setBackground(Color.BLACK);
		
		jbtnOkay.addActionListener(this);
		jbtnCancel.addActionListener(this);
		
		btnBackgroundColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backgroundColor = JColorChooser.showDialog(null, "Choose a Color", lblBackgroundColorDisplay.getBackground());
				if (backgroundColor !=null)
					lblBackgroundColorDisplay.setBackground(backgroundColor);
			}
		});
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainPan.setLayout(new GridLayout(5,3));
		
		mainPan.add(lblName);
		mainPan.add(jtfName);
		mainPan.add(lblBlank1);
	
		mainPan.add(lblWidth);
		mainPan.add(jtfWidth);
		mainPan.add(lblBlank2);	
		
		mainPan.add(lblHeight);
		mainPan.add(jtfHeight);
		mainPan.add(lblBlank3);
	
		mainPan.add(lblBackgroundColor);
		mainPan.add(btnBackgroundColor);
		mainPan.add(lblBackgroundColorDisplay);
		
		mainPan.add(jbtnOkay);
		mainPan.add(jbtnCancel);
		
	
		add(mainPan);
		setSize(500,200);
	}
	public void actionPerformed(ActionEvent event) {
		if(event.getSource()==jbtnOkay) {
			if (jtfName.getText().equals("")||
					jtfHeight.getText().equals("")||
					jtfWidth.getText().equals("") ||
					Integer.parseInt(jtfHeight.getText()) == 0 ||
					Integer.parseInt(jtfWidth.getText()) == 0)
			{
				data.setUsable(false);
			}
			else {
				data.setUsable(true);
				data.setName(jtfName.getText());
				data.setHeight(Integer.parseInt(jtfHeight.getText()));
				data.setWidth(Integer.parseInt(jtfWidth.getText()));
				data.setBackGroundColor(backgroundColor);
			}

			dispose();
		}
		else if(event.getSource() == jbtnCancel){
			data=null;
			dispose();
		}
	}
	public void setData(StreamUIProfile profile) {
		data=profile;
	}
	public StreamUIProfile run() {
		this.setVisible(true);
		return data;
	}
}
