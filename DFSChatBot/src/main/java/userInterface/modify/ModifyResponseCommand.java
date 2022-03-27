package userInterface.modify;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import database.Dao;
import entities.Access;
import entities.Category;
import entities.FullMessage;
import entities.ListMessage;
import entities.Message;
import entities.User;

public class ModifyResponseCommand extends JFrame {

	private static final long serialVersionUID = 3182870079914400494L;
	

	public ModifyResponseCommand(Category cat) {
		FullMessage newmsg = new FullMessage();
		newmsg.setCategory(cat);
		showScreen(newmsg);
	}

	public ModifyResponseCommand(FullMessage orig) {
		showScreen(orig);
	}

	private void showScreen(FullMessage originalMessage) {
		JLabel lblCommandName = new JLabel("Command Name");
		JTextField txtCommandName = new JTextField(25);
		JLabel lblResponse = new JLabel("Bot Response");
		JTextField txtResponse = new JTextField(25);
		JLabel lblCounter = new JLabel("Counter value");
		JTextField txtCounter = new JTextField(25);
		
		
		JLabel lblAccessLevel = new JLabel("Access Level");
		JComboBox<Access> dropUserAccess = new JComboBox<Access>(Access.class.getEnumConstants());
		
		String[] commandTypes = {"addCommand","editCommand","addCounter","addList"};

		JLabel lblCommandType = new JLabel("Command Type");
		JComboBox<String> dropCommandType= new JComboBox<String>(commandTypes);
		
		
		//JLabel 

		dropUserAccess.setSelectedIndex(Access.General.getRank() - 1);
		txtCounter.setText(""+originalMessage.getValue());

		JButton btnSave = new JButton("Save");
		JButton btnClear = new JButton("Clear");

		JPanel panMain = new JPanel();

		JPopupMenu rightClick = new JPopupMenu();
		JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
		JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
		JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
		rightClick.add(cut);
		rightClick.add(copy);
		rightClick.add(paste);

		txtCommandName.setComponentPopupMenu(rightClick);
		txtResponse.setComponentPopupMenu(rightClick);
		// txtCommandName.setText(userName);

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dao database = new Dao();
				if (!txtCommandName.getText().equals("")) {
					if (originalMessage.getAccessLevel()==null) {
						if (originalMessage.getCategory() == Category.Response)
							if(database.addCommand(txtCommandName.getText(), txtResponse.getText(),Access.valueOf(Access.class, dropUserAccess.getSelectedItem().toString()),Category.Response)) 
							JOptionPane.showMessageDialog(null, "Command Saved");
						if (originalMessage.getCategory() == Category.Counter) 
							if(database.addCounter(txtCommandName.getText(), txtResponse.getText()))
								JOptionPane.showMessageDialog(null, "Command Saved");
						if (originalMessage.getCategory() == Category.List)
						{
							if (database.addCommand(txtCommandName.getText(), txtResponse.getText(),Access.General, Category.List))
							{
								String propsFile = "./bot.properties";

								Properties props = new Properties();
								String chatPrefix=""; 
								try {
									InputStream input = new FileInputStream(propsFile);

									props.load(input);
									chatPrefix = props.getProperty("prefix");
								} catch (FileNotFoundException ese) {
									// TODO Auto-generated catch block
									ese.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

								if (database.addToComplexFunctions("add"+ txtCommandName.getText().replaceAll("^"+chatPrefix, "").toLowerCase(),"addListItem") &
										database.addCommand("add"+txtCommandName.getText().replaceAll("^"+chatPrefix, "").toLowerCase(), txtResponse.getText(), Access.valueOf(Access.class, dropUserAccess.getSelectedItem().toString()), Category.Command)){
									JOptionPane.showMessageDialog(null, "List Command Added");
								}
							}
						}
							
					} else {
						if (database.editCommand(txtCommandName.getText(), txtResponse.getText(),
								Access.valueOf(Access.class, dropUserAccess.getSelectedItem().toString()),
								originalMessage.getCommandName())) {
							if(originalMessage.getCategory().equals(Category.Counter)) {
								if(!originalMessage.getCommandName().equals(txtCommandName.getText()))
									database.renameCounter(txtCommandName.getText(),originalMessage.getCommandName());
								if(originalMessage.getValue()!=Integer.parseInt(txtCounter.getText())) 
									database.setCounter(txtCommandName.getText(), Integer.parseInt(txtCounter.getText()));
							}

							JOptionPane.showMessageDialog(null, "Command Saved");
						}
					}
				}
				dispose();
			}
		});
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtCommandName.setText("");
				txtResponse.setText("");
				dropUserAccess.setSelectedIndex(0);
			}
		});
		if (originalMessage.getCommandMessage()!=null) {
			txtCommandName.setText(originalMessage.getCommandName());
			txtResponse.setText(originalMessage.getCommandMessage());
			dropUserAccess.setSelectedIndex(originalMessage.getAccessLevel().getRank() - 1);
		}

		switch(originalMessage.getCategory()) {
			case Command:
			case Counter:
				panMain.setLayout(new GridLayout(5,2));
				break;
			default:
				panMain.setLayout(new GridLayout(4, 2));
		}
		panMain.add(lblAccessLevel);
		panMain.add(dropUserAccess);

		panMain.add(lblCommandName);
		panMain.add(txtCommandName);

		panMain.add(lblResponse);
		panMain.add(txtResponse);

		if (originalMessage.getCategory() == Category.Counter) {
			panMain.add(lblCounter);
			panMain.add(txtCounter);
		}
		if(originalMessage.getCategory() == Category.Command) {
			panMain.add(lblCommandType);
			panMain.add(dropCommandType);
		}
		panMain.add(btnSave);
		panMain.add(btnClear);

		add(panMain);
		setSize(300, 150);
		setResizable(true);
		setVisible(true);

	}

}
