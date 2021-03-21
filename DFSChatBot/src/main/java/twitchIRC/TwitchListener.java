package twitchIRC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import database.Dao;
import entities.Access;
import entities.ListMessage;
import entities.Message;

public class TwitchListener extends ListenerAdapter{
	
/*	@Override
	public void onGenericMessage(GenericMessageEvent event) throws Exception {
		// TODO Auto-generated method stub
		super.onGenericMessage(event);
		System.out.println("genericMessage found: "+event.getMessage());
		Dao database = new Dao();
		//if (event.getMessage().charAt(0)=='!')
		if (database.isCommand(event.getMessage()))
		{
			String returnMessage = database.chatCommand(event.getMessage(), event.getUser().getNick());
			if (returnMessage.equals("") ) {
				event.getChannel().send().message(returnMessage);
			}
		}		

	}*/
	/*@Override
	public void onJoin(JoinEvent event) throws Exception{
		super.onJoin(event);
		event.getChannel().send().message("I'm here");
	}*/
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
								database.increaseCounter(event.getMessage());
								event.getChannel().send().message(returnMessage.getText().replace("#", ""+database.getCounterValue(event.getMessage())));
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
	//@Override
//		event.getChannel().send().message("well hello there");
//	}
}
