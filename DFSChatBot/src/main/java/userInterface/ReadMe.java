package userInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ReadMe extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6303066858217428524L;

	public ReadMe() {
		JTextArea txtMain = new JTextArea();
		InputStream is = ReadMe.class.getClassLoader().getResourceAsStream("ReadMe.txt");
		InputStreamReader irs = new InputStreamReader(is);
		BufferedReader input = new BufferedReader(irs);
		try {
			
			txtMain.read(input, input);
		} catch (IOException e) {

			e.printStackTrace();
		}
		JScrollPane scroll = new JScrollPane(txtMain);
//        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		add(scroll);
		txtMain.setEditable(false);
		setSize(800,500);
		setVisible(true);
	}
}
