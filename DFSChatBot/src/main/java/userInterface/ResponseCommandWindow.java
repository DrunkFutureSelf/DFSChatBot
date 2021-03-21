package userInterface;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import database.Dao;
import entities.Category;
import entities.FullMessage;
import entities.ListMessage;
import entities.Message;

public class ResponseCommandWindow extends JPanel {
	private static final long serialVersionUID = 4837937837124252137L;

	ResponseCommandWindow(Category cat){
		
		Dao database = new Dao();
		FullMessage commands[] = database.getCommandsByCategory(cat);
		
		JComboBox<String> dropListSelection = new JComboBox<String>(database.getListNames());
		ListMessage listCommands[] = new ListMessage[0];


		JScrollPane panMainLayout = new JScrollPane(); 
		JList<FullMessage> jlstCommands = new JList<FullMessage>(commands);
		JList<ListMessage> jlstListCommands = new JList<ListMessage>(listCommands);
		if (cat == Category.ListItem)
		{
			dropListSelection.setModel(new DefaultComboBoxModel<String>(database.getListNames()));
			String[] CommandNames =database.getListNames();
			for (int i = 0; i < CommandNames.length;i++)
			{
				if (database.getListSize(CommandNames[i])!=0) {
					dropListSelection.setSelectedIndex(i);
					jlstListCommands.setListData(database.getListMessages(dropListSelection.getSelectedItem().toString()));
					i = CommandNames.length+1;
				}
				
			}
			panMainLayout.setViewportView(jlstListCommands);
		}
		else
			panMainLayout.setViewportView(jlstCommands);
		
		jlstCommands.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlstListCommands.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton btnAddCommand= new JButton("Add");
		JButton btnEditCommand = new JButton("Edit");
		JButton btnRemCommand = new JButton("Delete");
		JButton btnRefresh = new JButton("Refresh");
		
		JPanel panButtons = new JPanel();

		
		btnAddCommand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (cat == Category.ListItem)
					new ModifyListItem(cat,dropListSelection.getSelectedItem().toString());
				else
					new ModifyResponseCommand(cat);
			}});
		btnEditCommand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jlstListCommands.getSelectedIndex() != -1) 
					new ModifyListItem(jlstListCommands.getSelectedValue());
				else if (jlstCommands.getSelectedIndex() != -1) 
					new ModifyResponseCommand(jlstCommands.getSelectedValue());
			}});
		btnRemCommand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dao database = new Dao();
				if (cat==Category.ListItem) {
					if (database.deleteListItem(jlstListCommands.getSelectedValue().getOrdinal(), jlstListCommands.getSelectedValue().getName()))
						JOptionPane.showMessageDialog(null,"List Item Command Deleted");
				}
				else {
					if(database.deleteMsg(jlstCommands.getSelectedValue().getCommandName())) {
						if(cat==Category.List &
								database.deleteMsg("!add"+(jlstCommands.getSelectedValue().getCommandName().replace("!", ""))) &
								database.deleteList("!add"+(jlstCommands.getSelectedValue().getCommandName().replace("!", ""))) 
								)
							JOptionPane.showMessageDialog(null,"Command Deleted");
						else JOptionPane.showMessageDialog(null,"Command Deleted");
					}
				}
			}
		});
		
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				dropListSelection.setModel(new DefaultComboBoxModel<String>(database.getListNames()));
				/*		
				 * 			dropListSelection.setModel(new DefaultComboBoxModel<String>(database.getListNames()));
				 * 			String[] CommandNames =database.getListNames();
				 * 			panMainLayout.setViewportView(jlstListCommands);
		}
*/
				if (cat==Category.ListItem)
				{
					Message Lists[] = null;

					if (dropListSelection.getItemCount()==0) {
						dropListSelection.setModel(new DefaultComboBoxModel<String>(database.getListNames()));
						if (dropListSelection.getItemCount()==0) {
							Lists = database.getListMessages(dropListSelection.getSelectedItem().toString());
						}
					}
					else
						Lists = database.getListMessages(dropListSelection.getSelectedItem().toString());

					if (Lists== null)
						panMainLayout.setViewport(null);
					else {
						jlstListCommands.setListData(database.getListMessages(dropListSelection.getSelectedItem().toString()));
						panMainLayout.setViewportView(jlstListCommands);
					}
				}
				else
					jlstCommands.setListData(database.getCommandsByCategory(cat));

				invalidate();
			}
		});
		
		dropListSelection.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Message Lists[] = database.getListMessages(dropListSelection.getSelectedItem().toString());
				if (Lists== null)
				{
					panMainLayout.setViewport(null);
				}
				else
				{
					jlstListCommands.setListData(database.getListMessages(dropListSelection.getSelectedItem().toString()));

					panMainLayout.setViewportView(jlstListCommands);
				}
				
			}
			
		});
		if (cat ==Category.ListItem)
			panButtons.add(dropListSelection);
		jlstCommands.setFont( new Font("monospaced", Font.PLAIN, 12) );
		
		panButtons.setLayout(new BoxLayout(panButtons, BoxLayout.Y_AXIS));
		panButtons.setAlignmentY(CENTER_ALIGNMENT);
		
		panButtons.add(btnAddCommand);
		panButtons.add(btnEditCommand);
		panButtons.add(btnRemCommand);
		panButtons.add(btnRefresh);
		setLayout(new FlowLayout());
        add(panMainLayout);
        add(panButtons);
	}
}
