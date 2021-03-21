package userInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;

import database.Dao;
import entities.Access;

public class UserMaintenence extends JFrame{

	private static final long serialVersionUID = 8170735880355860921L;
	
	public UserMaintenence() {
		super("Add/Udate User");
		showScreen("",Access.General);
	}



	public UserMaintenence(String userName, Access accessLevel) {
		super("Add/Udate User");
		showScreen(userName,accessLevel);
	}
	
	private void showScreen(String userName,Access accessLevel) {
		setTitle("Add/Update User");

		JLabel lblUserName = new JLabel("User Name");		
		JTextField txtUserName = new JTextField(25);
	
		JComboBox<Access> dropUserAccess = new JComboBox<Access>(Access.class.getEnumConstants());
		dropUserAccess.setSelectedIndex(accessLevel.getRank()-1);
		JButton btnAdd = new JButton("Save");
		JButton btnClear = new JButton("Clear");
		
		JPanel panMain = new JPanel();
				
		JPopupMenu rightClick = new JPopupMenu();
		JMenuItem copy = new JMenuItem(new DefaultEditorKit.CopyAction());
		JMenuItem cut = new JMenuItem(new DefaultEditorKit.CutAction());
		JMenuItem paste = new JMenuItem(new DefaultEditorKit.PasteAction());
		rightClick.add(cut);
		rightClick.add(copy);
		rightClick.add(paste);
		
		txtUserName.setComponentPopupMenu(rightClick);
		txtUserName.setText(userName);

		
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dao database = new Dao();
				if (userName.equals("")){
					if(database.addUser(txtUserName.getText(), Access.valueOf(Access.class, dropUserAccess.getSelectedItem().toString())))
						JOptionPane.showMessageDialog(null,"User Saved");
				}else {
					if (database.editUser(Access.valueOf(Access.class, dropUserAccess.getSelectedItem().toString()),txtUserName.getText(),userName))
						JOptionPane.showMessageDialog(null,"User Saved");
				}

				dispose();
				
			}
		});
		panMain.add(lblUserName);
		panMain.add(txtUserName);
		panMain.add(dropUserAccess);
		panMain.add(btnAdd);
		
		add(panMain);
		setSize(500,150);
		setVisible(true);
	}

}
