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
import userInterface.StreamUI;

public class ConnectionFactory  extends Thread{
	private PircBotX cf;
	private StreamUI streamUI;
	private TwitchListener tl;
	
	public ConnectionFactory(StreamUI ui) {
		streamUI=ui;
		tl=new TwitchListener(streamUI);
	}	


	public ConnectionFactory() {
		streamUI=new StreamUI();
		tl = new TwitchListener();
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
					.addListener(tl)
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

	public StreamUI getStreamUI() {
		return streamUI;
	}

	public void setStreamUI(StreamUI streamUI) {
		this.streamUI = streamUI;
	}


}
