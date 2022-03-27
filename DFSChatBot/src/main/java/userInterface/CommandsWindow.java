package userInterface;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import entities.Category;

public class CommandsWindow extends JPanel{
	private static final long serialVersionUID = -8854608908956611894L;
	
	CommandsWindow()
	{
		JTabbedPane tabbed = new JTabbedPane();
		
		JScrollPane Basic = new JScrollPane(new ResponseCommandWindow(Category.Response));
		JScrollPane Counters = new JScrollPane(new ResponseCommandWindow(Category.Counter));
		JScrollPane Core = new JScrollPane (new ResponseCommandWindow(Category.Command));
		JScrollPane List = new JScrollPane( new ResponseCommandWindow(Category.List));
		JScrollPane ListItems = new JScrollPane(new ResponseCommandWindow(Category.ListItem));

		Basic.setPreferredSize(new Dimension (700,300));
		Counters.setPreferredSize(new Dimension (700,300));
		Core.setPreferredSize(new Dimension (700,300));
		List.setPreferredSize(new Dimension (700,300));
		ListItems.setPreferredSize(new Dimension (700,300));

		Basic.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		Counters.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		Core.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		List.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		ListItems.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		tabbed.addTab("Basic Responses",Basic);
		tabbed.addTab("Counters",Counters );
		tabbed.addTab("Core Features",Core);
		tabbed.addTab("List",List);
		tabbed.addTab("List Items",ListItems);

		
		add(tabbed);
	}
}
