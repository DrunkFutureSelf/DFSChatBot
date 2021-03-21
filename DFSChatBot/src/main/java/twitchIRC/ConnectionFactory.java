package twitchIRC;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.pircbotx.Configuration;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.exception.IrcException;
import database.Dao;
import entities.Access;
import entities.Category;
import twitchIRC.TwitchListener;
import userInterface.ConfigurationWindow;

public class ConnectionFactory  extends Thread{
	private PircBotX cf;


	public ConnectionFactory() {
		
		// TODO Auto-generated constructor stub
	}
	
	public void run()  {
		try {
			InputStream input = new FileInputStream("./bot.properties");
			Properties props = new Properties();

			props.load(input);
			
			Configuration config= new Builder()
					.addServer("irc.twitch.tv")
					.setNickservDelayJoin(false)
					.setOnJoinWhoEnabled(false)
					.setCapEnabled(true)
					.addCapHandler(new EnableCapHandler("twitch.tv/membership"))
					.setName(props.getProperty("userName"))
					.setServerPassword(props.getProperty("oauth"))
					.addListener(new TwitchListener())
					.addAutoJoinChannel("#"+props.getProperty("channelToJoin").replace("#", ""))
					.buildConfiguration();
			
					
				cf = new PircBotX(config);
				cf.startBot();

		} catch (IOException | IrcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cf.close();
		}

	}
	public void stopBot() {
		cf.stopBotReconnect();
		cf.close();
	}


}
