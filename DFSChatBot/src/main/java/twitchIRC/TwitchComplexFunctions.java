package twitchIRC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.pircbotx.hooks.events.MessageEvent;

import database.Dao;
import entities.Access;
import entities.Category;

public class TwitchComplexFunctions {
	
	private String prefix;
	
	TwitchComplexFunctions(){
		Properties props = new Properties();
		try {
			String propsFile = "./bot.properties";
			InputStream input = new FileInputStream(propsFile);

			props.load(input);
			prefix = props.getProperty("prefix");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}
	public String callMethod(String messageCommand, String command, MessageEvent event){
		if (command.equals("addCommand")) 
			return addCom(messageCommand, event);
		if (command.equals("editCommand"))
			return editCom(messageCommand,event);
		if (command.equals("addCounter"))
			return addCounter(messageCommand,event);
		if (command.equals("addList"))
			return addList(messageCommand,event);
		if (command.equals("addListItem"))
			return addListItem(event);
			
		return "";		
	}
	
	public String addCom(String messageCommand, MessageEvent event){
		String returnMessage= "";
		String origMsg = event.getMessage().substring(0,event.getMessage().indexOf(' '));

		Dao database = new Dao();
		
		String msg = event.getMessage().substring(event.getMessage().indexOf(" ")+1).replace("--","-").replace("/*", "*").replace("*/", "*").trim();
		String command = msg.substring(0, msg.indexOf(" ")).trim();
		String message = msg.substring(msg.indexOf(" ")).trim();
		if (msg.contains("-?") || msg.contains("-options") || msg.contains("-usage"))
		{
			returnMessage="-usage: "+prefix+"addcom command_name response text";
		}
		else if (database.isCommand(prefix+command.replaceAll("^"+prefix, ""))){
			returnMessage="Command already exists use edit";
		} 
		else if (database.getUser(event.getUser().getNick()).getAccessLevel().getRank() > database.chatCommand(prefix+messageCommand.replace(prefix,"")).getAccessLevel().getRank()){
			returnMessage = "Only "+database.chatCommand(prefix+messageCommand).getAccessLevel().getDescription() +" or higher can run "+messageCommand+"."; 
		}
		else {
			if (database.addCommand(prefix+command, message, Access.General, Category.Response))
				returnMessage = database.chatCommand(prefix+origMsg.replaceAll("^"+prefix, "")).getText();
		}
		return returnMessage;
	}
	
	public String addList(String messageCommand,MessageEvent event){
		//!addlist quote
		//	|
		//	->!addQuote
		//	|
		//	->!quote
		String returnMessage= "";
		Dao database = new Dao();
		String origMsg = event.getMessage().substring(0,event.getMessage().indexOf(' '));

		String msg = event.getMessage().substring(event.getMessage().indexOf(" ")+1).replace("--","-").replace("/*", "*").replace("*/", "*").trim();
		String command = msg.indexOf(" ")==-1?msg: msg.substring(0, msg.indexOf(" ")).trim();
		String message = msg.indexOf(" ")==-1? msg:msg.substring(msg.indexOf(" ")).trim();
		
		if (msg.contains("-?") || msg.contains("-options") || msg.contains("-usage")|| command.equals(""))
		{
			returnMessage="-usage: "+prefix+messageCommand+" list_name <response text>";
		}
		else if (database.isCommand(prefix+command.replaceAll("^"+prefix, ""))){
			returnMessage=editCom(messageCommand,event);
		} 
		else {
			if (database.addCommand(prefix+command.replaceAll("^"+prefix, ""), message,Access.General, Category.List))
			{
				if (database.addToComplexFunctions(prefix+"add"+ command.replaceAll("^"+prefix, "").toLowerCase(),"addListItem") &
						database.addCommand(prefix+"add"+command.replaceAll("^"+prefix, "").toLowerCase(), message, database.chatCommand(messageCommand).getAccessLevel(), Category.Command)){
					returnMessage = database.chatCommand(prefix+origMsg.replaceAll("^"+prefix, "")).getText();
				}
			}
		}
		return returnMessage;
	}

	public String editCom(String messageCommand,MessageEvent event) {
		String returnMessage= "";
		String origMsg = event.getMessage().substring(0,event.getMessage().indexOf(' '));

		Dao database = new Dao();

		String msg = event.getMessage().substring(event.getMessage().indexOf(" ")+1).replace("--","-").replace("/*", "*").replace("*/", "*").trim();
		String command = msg.substring(0, msg.indexOf(" ")).trim();
		String message = msg.substring(msg.indexOf(" ")).trim();
		
		if (msg.contains("-?") || msg.contains("-options") || msg.contains("-usage")|| command.equals(""))
		{
			returnMessage= ("-usage: "+prefix+"editcom command_name new response text");
		}
		else if (!database.isCommand(prefix+command.replaceAll("^"+prefix, ""))){
			returnMessage= addCom(messageCommand,event);
		}
		else {
			if (database.editCommand(prefix+command, message)) {
				returnMessage = database.chatCommand(prefix+origMsg.replaceAll("^"+prefix, "")).getText();
			}
		}
		return returnMessage;
	}
	public String addCounter(String messageCommand,MessageEvent event) {
		String returnMessage= "";
		Dao database = new Dao();
		String origMsg = event.getMessage().substring(0,event.getMessage().indexOf(' '));

//!addcounter giggle times you've made made me giggle: #

		String msg = event.getMessage().substring(event.getMessage().indexOf(" ")+1).replace("--","-").replace("/*", "*").replace("*/", "*").trim();
		String command = msg.substring(0, msg.indexOf(" ")).trim();
		String message = msg.substring(msg.indexOf(" ")).trim();
		if (msg.contains("-?") || msg.contains("-options") || msg.contains("-usage")){
			returnMessage= ("-usage: "+prefix+"addCounter counter_name message with new value:#");
		}
		else if (database.isCommand(prefix+command.replaceAll("^"+prefix, ""))){
			returnMessage= editCom(messageCommand,event);
		}
		else {
			if (database.addCounter(prefix+command, message))
				returnMessage = database.chatCommand(prefix+origMsg.replaceAll("^"+prefix, "")).getText();

		}
		return returnMessage;
	}
	public String addListItem(MessageEvent event) {
		String returnMessage = "";
		String origMsg = event.getMessage().substring(0,event.getMessage().indexOf(' '));

		Dao database = new Dao();
		String command = event.getMessage().substring(1,event.getMessage().indexOf(' '));
		String msg = event.getMessage().substring(event.getMessage().indexOf(' '));
		if (database.addListItem(msg, database.getListSize(prefix+command.replaceAll("^add",""))+1, prefix+command.replaceAll("^add","")))
		{
			returnMessage =command.replaceAll("^add","")+"["+database.getListSize(prefix+command.replaceAll("^add",""))+"]"+" added";
			returnMessage =database.chatCommand(prefix+origMsg.replaceAll("^"+prefix, "")).getText().replace("#",""+ database.getListSize(prefix+command.replaceAll("^add","")));

		}
		return returnMessage;
	}
}
