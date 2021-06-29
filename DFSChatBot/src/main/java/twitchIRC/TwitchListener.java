package twitchIRC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JPanel;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import database.Dao;
import entities.Access;
import entities.ListMessage;
import entities.Message;
import entities.StreamUIElement;
import userInterface.StreamUI;

public class TwitchListener extends ListenerAdapter{
	private JPanel CommandUsage;
	private StreamUI streamView;
	
	public TwitchListener(StreamUI view) {
		streamView=view;
	}

	public TwitchListener() {
		streamView=new StreamUI();
	}	
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		super.onMessage(event);
		String propsFile = "./bot.properties";
		InputStream input;
		try{
			input= new FileInputStream(propsFile);
		}catch(FileNotFoundException e) {
			new File(propsFile).createNewFile();
			input = new FileInputStream(propsFile);
		}
		Properties props = new Properties();

		props.load(input);

		if (!event.getUser().equals(props.getProperty(props.getProperty("userName"))))
		{
			String commandname = (event.getMessage()).substring(0, (event.getMessage().indexOf(' ') != -1 ? event.getMessage().indexOf(" "):event.getMessage().length())).trim();
			Dao database = new Dao();
			if (database.isCommand(commandname))
			{
				Message returnMessage = database.chatCommand(commandname);
				if (!Arrays.asList(database.getUsers()).contains(event.getUser().getNick().toLowerCase()))
					database.addUser(event.getUser().getNick().toLowerCase(), Access.General);
				if (database.getUser(event.getUser().getNick()).getAccessLevel().getRank() <= returnMessage.getAccessLevel().getRank())
				{
					switch(returnMessage.getCategory()){
						case Response:
							if(returnMessage != null && !returnMessage.getText().equals(""))
								event.getChannel().send().message(returnMessage.getText());
							break;
						case Counter:
							if(returnMessage != null && !returnMessage.getText().equals("")) {
								String parameters = event.getMessage().replace(commandname,"");
								int value =0;
								if (parameters.contains("+") ||parameters.contains("-")) {
									try {
										value = Integer.parseInt(parameters.replace("-", "").replace("+", "").replace(" ", ""));
										}catch(NumberFormatException exc){
											value = 1;
										}
								}
								if (parameters.contains("+"))
									database.increaseCounter(commandname,value);
								if (parameters.contains("-"))
									database.decreaseCounter(commandname,value);
								if (parameters.contains("reset")||parameters.contains("clear"))
									database.resetCounter(commandname);
								int replacement = database.getCounterValue(commandname);
								event.getChannel().send().message(returnMessage.getText().replace("#", ""+replacement));
								if (streamView.isWatched(commandname)){
									for (StreamUIElement e : streamView.getItemsByCommand(commandname)) {
										e.setDisplayValue(""+replacement);
									}
									streamView.refreshPage();
								}
							}
							break;
						case List:
							String command = event.getMessage().substring(0, event.getMessage().contains(" ")? event.getMessage().indexOf(" "):event.getMessage().length());
							int count = database.getListSize(command);
							int message = event.getMessage().contains(" ")? Integer.parseInt(event.getMessage().substring(event.getMessage().indexOf(" ")).trim()):-1;
							if (message>0 && !database.checkListOrdinal(command, message)) {
								event.getChannel().send().message(command.substring(1)+" "+message+" not found");
								break;
							}
							ListMessage[] lm = database.getListMessages(command);
							int index=-1;
							for (int i = 0; i <lm.length; i++)
							{
								if (lm[i].getOrdinal()==message)
									index = i;
							}
							if (message ==-1)
								index = (int)(Math.random()*count);
							//event.getChannel().send().message(command.substring(1)+" ["+(lm[index].getOrdinal())+"]: "+lm[index].getText());
							event.getChannel().send().message(returnMessage.getText().replace("#", ""+lm[index].getOrdinal())+ " "+lm[index].getText());
							break;
						case Command:
							TwitchComplexFunctions tcf = new TwitchComplexFunctions();
							String newMsg = tcf.callMethod(commandname,database.getComplexFunction(commandname), event);
							String msg = event.getMessage().substring(event.getMessage().indexOf(" ")+1).replace("--","-").replace("/*", "*").replace("*/", "*").trim();
							String originalCommand = msg.substring(0, msg.indexOf(" ")).trim();
							String newMessage = msg.substring(msg.indexOf(" ")).trim();

							if (streamView.isWatched(originalCommand)){
								for (StreamUIElement e : streamView.getItemsByCommand(originalCommand)) {
									e.setDisplayValue(""+newMessage);
								}
								streamView.refreshPage();
							}
							if (!newMsg.equals(""))
								event.getChannel().send().message(newMsg);
							break;
						default:
							break;
					}
				}
				else
				{
					event.getChannel().send().message("Insufficient privilege to run this command");
				}
			}
		}
	}


	public JPanel getCommandUsage() {
		return CommandUsage;
	}


	public void setCommandUsage(JPanel commandUsage) {
		CommandUsage = commandUsage;
	}


	public StreamUI getStreamView() {
		return streamView;
	}


	public void setStreamView(StreamUI streamView) {
		this.streamView = streamView;
	}	
}
