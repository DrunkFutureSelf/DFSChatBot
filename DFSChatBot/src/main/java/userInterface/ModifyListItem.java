package userInterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import entities.Category;
import entities.FullMessage;
import entities.ListMessage;

public class ModifyListItem extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ModifyListItem(ListMessage msg){
		FullMessage newmsg = new FullMessage();
		newmsg.setAccessLevel(msg.getAccessLevel());
		newmsg.setCommandName(msg.getName());
		newmsg.setCategory(msg.getCategory());
		newmsg.setListMessage(msg.getText());
		newmsg.setAccessLevel(msg.getAccessLevel());
		newmsg.setOrdinal(msg.getOrdinal());
		showScreen(newmsg);
	}
	ModifyListItem(Category cat,String listName){
		FullMessage newmsg = new FullMessage();
		newmsg.setCommandName(listName);
		newmsg.setCategory(cat);
		showScreen(newmsg);

	}
	private void showScreen(FullMessage originalMessage) {
		JLabel lblOrdinal = new JLabel("Ordinal (0 for last)");
		JTextField txtOrdinal = new JTextField(25);
		JLabel lblResponse = new JLabel("Bot Response");
		JTextField txtResponse = new JTextField(25);

		JLabel lblListMsgSelection = new JLabel("List");		
		JComboBox<String> dropListSelection = new JComboBox<String>(new Dao().getListNames());

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

		txtOrdinal.setComponentPopupMenu(rightClick);
		txtResponse.setComponentPopupMenu(rightClick);
		txtResponse.setText(originalMessage.getListMessage());
		txtOrdinal.setText(""+originalMessage.getOrdinal());
		for(int i = 0; i <=dropListSelection.getItemCount(); i++)
			if (originalMessage.getCommandName().equals(dropListSelection.getItemAt(i)))
				dropListSelection.setSelectedIndex(i);
		
		
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dao database = new Dao();
				if (originalMessage.getOrdinal()==0) {
					int ordinal = Integer.parseInt(txtOrdinal.getText());
					if (ordinal == 0) {
						ListMessage[] messages = database.getListMessages(dropListSelection.getSelectedItem().toString());
						if (messages==null)
							ordinal = 1;
						else
							ordinal = messages[messages.length-1].getOrdinal()+1;
					}
					if (Integer.parseInt(txtOrdinal.getText())!=0 &&  database.checkListOrdinal(dropListSelection.getSelectedItem().toString(), ordinal))
						JOptionPane.showMessageDialog(null,"Ordinal in use for list "+dropListSelection.getSelectedItem().toString());
					else if (!txtResponse.getText().equals("") && !database.checkListOrdinal(dropListSelection.getSelectedItem().toString(), ordinal)){
						if(database.addListItem(txtResponse.getText(), ordinal, dropListSelection.getSelectedItem().toString())) {
							JOptionPane.showMessageDialog(null,"Message Added to list "+dropListSelection.getSelectedItem().toString());
							dispose();
							}
						}
					}
					else {
						int ordinal = originalMessage.getOrdinal();
						if (Integer.parseInt(txtOrdinal.getText())== 0) {
							ListMessage[] messages = database.getListMessages(dropListSelection.getSelectedItem().toString());
							ordinal = messages[messages.length-1].getOrdinal()+1;
						}
						if (Integer.parseInt(txtOrdinal.getText())!=ordinal &&  database.checkListOrdinal(dropListSelection.getSelectedItem().toString(), ordinal))
							JOptionPane.showMessageDialog(null,"Ordinal in use for list "+dropListSelection.getSelectedItem().toString());

						else if (database.updateListItem(dropListSelection.getSelectedItem().toString(), ordinal, txtResponse.getText(),originalMessage.getOrdinal(),originalMessage.getCommandName())) {
							JOptionPane.showMessageDialog(null,"Message updated");
							dispose();
						}
					}
			}
		});
		panMain.setLayout(new GridLayout(4, 2));
		panMain.add(lblListMsgSelection);		
		panMain.add(dropListSelection);
		panMain.add(lblResponse);
		panMain.add(txtResponse);

		panMain.add(lblOrdinal);
		panMain.add(txtOrdinal);	
		panMain.add(btnSave);
		panMain.add(btnClear);

		add(panMain);
		setSize(300, 150);
		setResizable(true);
		setVisible(true);

	}
}
