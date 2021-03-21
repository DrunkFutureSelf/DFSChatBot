package userInterface;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import database.Dao;
import entities.User;

public class UsersInterface extends JPanel{
	
	private static final long serialVersionUID = -6764737203097985332L;
	
	public UsersInterface() {
		JButton btnAddUser = new JButton("Add");
		JButton btnEditUser = new JButton("Edit");
		JButton btnRemUser = new JButton("Remove");
		JButton btnRefresh = new JButton("Refresh");
		
		Dao dao= new Dao();
		JList<User> userModel = new JList<User>(dao.getUsers());
		
		JPanel panUserControl = new JPanel();
		JScrollPane panMainLayout = new JScrollPane(); 
		panMainLayout.setViewportView(userModel);

		btnAddUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new UserMaintenence();
			}});
		btnEditUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new UserMaintenence(userModel.getSelectedValue().getUserName(),userModel.getSelectedValue().getAccessLevel());
			}});
		btnRemUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dao database = new Dao();
				if (database.getUsers().length>1) {
					if(database.deleteUser(userModel.getSelectedValue().getUserName()))
						JOptionPane.showMessageDialog(null,"User Deleted");
				}
				else
					JOptionPane.showMessageDialog(null,"At least one user is required");					
			}
		});
		
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userModel.setListData(dao.getUsers());
			}
		});

		userModel.setFont( new Font("monospaced", Font.PLAIN, 12) );
		
		panUserControl.setLayout(new BoxLayout(panUserControl, BoxLayout.Y_AXIS));
		panUserControl.setAlignmentY(CENTER_ALIGNMENT);
		
		panUserControl.add(btnAddUser);
		panUserControl.add(btnEditUser);
		panUserControl.add(btnRemUser);
		panUserControl.add(btnRefresh);
		setLayout(new FlowLayout());
        add(panMainLayout);
        add(panUserControl);

	}
}
