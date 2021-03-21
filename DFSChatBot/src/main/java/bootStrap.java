import java.io.IOException;

import database.Dao;
import entities.Access;
import entities.Category;
import userInterface.ConfigurationWindow;
import userInterface.GraphicalInterface;

public class bootStrap {
//java -jar C:\Users\baile\eclipse-workspace\twitch-bot\target\twitch-bot-0.0.1-SNAPSHOT-jar-with-dependencies.jar -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
	public static void main(String[] args) throws IOException {
/*
//			Thread botThread = new Thread() {
//				public void run() {
					try {
						System.out.println("doing things");
						cf.startBot();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IrcException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					cf.close();
			//	}
	//		};
System.out.println("here");*/
/*		User[] users = database.getUsers();
		for (User u:users)
		{
			System.out.println(u.toString());
		}
		
		User me = database.getUser("DrunkFutureSelf");
		System.out.println(me.toString());
		*/
//		Dao database = new Dao();
/*		String event = "!quote";
		String command = event.substring(0, event.contains(" ")? event.indexOf(" "):event.length());
		
		int count = database.getListSize(command);
		
		int message = event.contains(" ")? Integer.parseInt(event.substring(event.indexOf(" ")).trim())-1:count+1;


		if (count < message)
			message = (int)(Math.random()*count);
		System.out.println(command.substring(1)+" ["+(message+1)+"]: "+database.getListMessages(command)[message].getText());
		*/

		new GraphicalInterface();
		//cw.toFront();
		
	}
}
