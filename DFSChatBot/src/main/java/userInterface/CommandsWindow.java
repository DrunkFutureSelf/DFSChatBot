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
import javax.swing.JTabbedPane;

import database.Dao;
import entities.Category;
import entities.User;

public class CommandsWindow extends JPanel{
	private static final long serialVersionUID = -8854608908956611894L;
	
	CommandsWindow()
	{
		JTabbedPane tabbed = new JTabbedPane();
		tabbed.addTab("Basic Responses",new ResponseCommandWindow(Category.Response));
		tabbed.addTab("Counters",new ResponseCommandWindow(Category.Counter));
		tabbed.addTab("Core Features",new ResponseCommandWindow(Category.Command));
		tabbed.addTab("List",new ResponseCommandWindow(Category.List));
		tabbed.addTab("List Items",new ResponseCommandWindow(Category.ListItem));

		
		add(tabbed);
	}
}
