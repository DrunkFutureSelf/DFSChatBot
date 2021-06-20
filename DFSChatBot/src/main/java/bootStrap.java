import java.io.File;
import java.io.IOException;

import database.Dao;
import userInterface.GraphicalInterface;

public class bootStrap {
	public static void main(String[] args) throws IOException {
		
		if (new File("./bot.properties").isFile())
		{
			Dao database = new Dao();
			if (!database.getVersionNumber().equals(database.getLatestVersion())) 
				database.performConvert();
			
		}
		new GraphicalInterface();

	}
}
