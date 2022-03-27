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
								sendMessage(event,returnMessage.getText());
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
								sendMessage(event,returnMessage.getText().replace("#", ""+replacement));
								if (streamView.isWatched(commandname)){
									for (StreamUIElement e : streamView.getItemsByCommand(commandname)) {
										e.setDisplayValue(""+replacement);
									}
									streamView.checkForAnimation(commandname);
								}
							}
							break;
						case List:
							String command = event.getMessage().substring(0, event.getMessage().contains(" ")? event.getMessage().indexOf(" "):event.getMessage().length());
							int count = database.getListSize(command);
							int message = event.getMessage().contains(" ")? Integer.parseInt(event.getMessage().substring(event.getMessage().indexOf(" ")).trim()):-1;
							if (message>0 && !database.checkListOrdinal(command, message)) {
								sendMessage(event,command.substring(1)+" "+message+" not found");
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
							sendMessage(event,returnMessage.getText().replace("#", ""+lm[index].getOrdinal())+ " "+lm[index].getText());
							break;
						case Command:
							TwitchComplexFunctions tcf = new TwitchComplexFunctions();
							String newMsg = tcf.callMethod(commandname,database.getComplexFunction(commandname), event);

							if (!newMsg.equals(""))
								sendMessage(event,newMsg);
							break;
						default:
							break;
					}
				}
				else
				{
					String level="mod";
					if (returnMessage.getAccessLevel().getRank() ==Access.Creator.getRank())
							level = Access.Creator.getDescription();
					if (returnMessage.getAccessLevel().getRank() ==Access.Friend.getRank())
						level = Access.Friend.getDescription();
					if (returnMessage.getAccessLevel().getRank() ==Access.General.getRank())
						level = Access.General.getDescription();
					if (returnMessage.getAccessLevel().getRank() ==Access.Mod.getRank())
						level = Access.Mod.getDescription();
					if (returnMessage.getAccessLevel().getRank() ==Access.VIP.getRank())
							level = Access.VIP.getDescription();
					sendMessage(event,"Please ask a "+level +" to run this command");
				}
			}
		}
	}
	private void sendMessage(MessageEvent event, String message) {
		while (message.length() >0) {
			if (message.length()<=500) {
				event.getChannel().send().message(message);
				message ="";
			}
			else {
				String sendmsg = message.substring(0,500);
				message = message.substring(501);
				event.getChannel().send().message(message);
				try {
					this.wait(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
