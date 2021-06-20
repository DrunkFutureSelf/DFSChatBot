package userInterface;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import entities.Category;

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
