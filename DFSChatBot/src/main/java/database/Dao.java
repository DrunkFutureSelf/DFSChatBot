package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.Vector;

import entities.Access;
import entities.Category;
import entities.FullMessage;
import entities.ListMessage;
import entities.Message;
import entities.User;

public class Dao {

	private final String sqlUsers = "SELECT * FROM Users;";
	private final String sqlSpecificUser = "SELECT * FROM USERS JOIN Access ON Access.Description = USERS.AccessLevel WHERE Name = ?;";
	private final String addMSG = "INSERT INTO Commands(Name, Message,AccessLevel,Category) VALUES(?,?,?,?);";
	private final String delMsg = "DELETE FROM Commands WHERE name = ?;";
	private final String delCounter = "DELETE FROM Counters WHERE Name = ?";
	private final String renameCounter = "UPDATE Counters SET Name = ? WHERE Name = ?;";
	private final String delList = "DELETE FROM ComplexFunctions WHERE Command = ?;"; 
	private final String checkCommand = "SELECT COUNT(*) as count FROM Commands WHERE Name = ?;";
	private final String updateCommand = "UPDATE Commands SET Message = ? where Name = ?;";
	private final String updateCommandAdvanced = "UPDATE Commands SET Name = ?, Message = ?, AccessLevel =? where Name = ?;";
	private final String getMessageFromCommand = "SELECT Name, Message, AccessLevel, Category FROM Commands WHERE Name = ? ";
	private final String newCounter = "INSERT INTO Counters (Name,Value) Values(?,0);";
	private final String getCounterValue = "SELECT Value from Counters WHERE Name = ?;";
	private final String updateCounter = "UPDATE Counters SET Value = ? WHERE Name = ?;";
	private final String getComplexFunction = "SELECT Function FROM ComplexFunctions WHERE Command = ?;";
	private final String getListFromCommand ="SELECT Lists.Name, Lists.Ordinal, Lists.Message, Commands.AccessLevel FROM Lists JOIN Commands ON Lists.Name = Commands.Name WHERE Lists.Name = ? ORDER BY Lists.Ordinal";
	private final String getListNames= "SELECT DISTINCT Commands.Name FROM Commands where Commands.Category = \"List\";";
	private final String addListItem ="INSERT INTO Lists(Message, Name, Ordinal) VALUES(?,?,?);";
	private final String getListCount ="SELECT COUNT(*) FROM Lists WHERE Lists.Name = ?;";
	private final String updateListItem = "UPDATE Lists SET Message = ?, Name = ?, Ordinal = ? WHERE Lists.Name = ? AND Lists.Ordinal =?;";
	private final String checkOrdinal = "Select * FROM Lists WHERE Lists.Name = ? AND Ordinal = ?;";
	private final String addListCommand = "INSERT INTO ComplexFunctions(Command, Function) VALUES(?,?);";
	
	private final String setupAccess="CREATE TABLE Access (Description CHAR PRIMARY KEY UNIQUE NOT NULL);";
	private final String setupCategories= "CREATE TABLE Categories (Description CHAR PRIMARY KEY UNIQUE NOT NULL);";
	private final String setupCommands ="CREATE TABLE Commands (Name PRIMARY KEY UNIQUE NOT NULL, Message STRING, AccessLevel REFERENCES Access (Description),Category REFERENCES Categories (Description) );";
	private final String setupComplexFunctions="CREATE TABLE ComplexFunctions (Command  STRING PRIMARY KEY UNIQUE, Function NOT NULL);";
	private final String setupCounters = "CREATE TABLE Counters (Name  STRING  NOT NULL REFERENCES Commands (Name) UNIQUE, Value INTEGER);";
	private final String setupLists = "CREATE TABLE Lists (Name    STRING REFERENCES Commands (Name) NOT NULL, Ordinal INT    NOT NULL, Message STRING NOT NULL);";
	private final String setupUsers = "CREATE TABLE Users (ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, AccessLevel REFERENCES Access (Description), Name CHAR NOT NULL);";
	
	private final String setupAccessDefaults = "INSERT INTO Access (Description) VALUES (\"Creator\"),(\"Mod\"),(\"VIP\"),(\"General\")";
	private final String setupCategoryDefaults = "INSERT INTO Categories (Description) VALUES(\"List\"),(\"Response\"),(\"Command\"),(\"Counter\")";
	
	private final String checkStatusOfDatabase = "SELECT Description FROM Access where Description = \"Creator\";";
	
	private final String addUser="INSERT INTO Users (AccessLevel,Name) VALUES (?,?);";
	private final String deleteUser = "DELETE From Users where Name = ?";
	private final String deleteListItem = "DELETE FROM Lists where Ordinal = ? and Name = ?";
	private final String updateUser = "UPDATE Users SET AccessLevel = ?,  Name = ? WHERE Name = ?;";
	
	private final String getCommandsByCategory = "SELECT Commands.Name as CommandName, Commands.Message as CommandMessage, AccessLevel, Category, c.Value as value, cf.Function as Function FROM Commands LEFT JOIN Counters as c on c.Name = Commands.Name LEFT JOIN ComplexFunctions as cf on cf.Command = Commands.Name Where Commands.Category = ? ORDER BY Commands.Name";
	
	private String URL;
	private String propsFile = "./bot.properties";
	
	private String chatPrefix;

	public Dao()  {
		//URL =  = "jdbc:sqlite:"D:/Bots/python/ChatBot/Twitch/bot-database.db";

		//new File(propsFile).createNewFile();
		Properties props = new Properties();
		try {
			InputStream input = new FileInputStream(propsFile);

			props.load(input);
			URL = "jdbc:sqlite:"+props.getProperty("database");
			chatPrefix = props.getProperty("prefix");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}
	
	public boolean deleteListItem(int ordinal, String message ) {
		boolean returnvalue = false;
		try {
			//	private final String deleteListItem = "DELETE FROM Lists where Ordinal = ? and Name = ?";

			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(deleteListItem);
			pstmt.setInt(1,ordinal);
			pstmt.setString(2,message);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			returnvalue = true;
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return returnvalue;
	}
	
	public FullMessage[] getCommandsByCategory(Category category) {
		Vector<FullMessage> commands = new Vector<FullMessage>();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getCommandsByCategory);
			pstmt.setString(1, category.getDescription());
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String commandName = rs.getString("CommandName");
				String message = rs.getString("CommandMessage");
				Access level = Access.valueOf(Access.class, rs.getString("AccessLevel"));
				int value = rs.getInt("value");
				//int ordinal = rs.getInt("Ordinal");
				//String listMessage = rs.getString("ListMessage");
				String function = rs.getString("Function");
	
				FullMessage fm = new FullMessage(commandName,message,level,category,value,0,null,function);
				commands.add(fm);
			}
			rs.close();
			pstmt.close();
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		Collections.sort(commands);
		return commands.toArray(new FullMessage[commands.size()]);
	}

	public User[] getUsers() {
		try {
			Connection conn = DriverManager.getConnection(URL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlUsers);
			Vector<User> users = new Vector<User>(1);

			while (rs.next()) {
				int id = rs.getInt("ID");
				Access accessLevel = Access.valueOf(Access.class, rs.getString("AccessLevel"));
				String name = rs.getString("Name");

				User u = new User(name, id, accessLevel);
				users.add(u);
			}
			rs.close();
			stmt.close();
			conn.close();
			return users.toArray(new User[users.size()]);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteMsg (String message) {
		boolean returnValue = false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(delMsg);
			pstmt.setString(1, message);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			returnValue = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;	
	}

	public boolean updateListItem(String commandName, int newOrdinal, String message, int oldOrdinal, String oldCommandName) {
		boolean returnValue = false;
		try{
			//private final String updateListItem = "UPDATE Lists SET Message = ?, Name = ?, Ordinal = ? WHERE Lists.Name = ? AND Lists.Ordinal =?;";
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(updateListItem);
			pstmt.setString(1, message);
			pstmt.setInt(3, newOrdinal);
			pstmt.setString(2, commandName);
			pstmt.setInt(5, oldOrdinal);
			pstmt.setString(4, oldCommandName);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			returnValue = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	
	public boolean checkListOrdinal(String commandName, int ordinal) {
		
//		private final String checkOrdinal = "Select * FROM Lists WHERE List.Name = ? AND Ordinal = ?;";
		boolean returnVal= false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(checkOrdinal);
			pstmt.setString(1, commandName);
			pstmt.setInt(2, ordinal);
			ResultSet rs = pstmt.executeQuery();
			returnVal = rs.getFetchSize() > 0;
			rs.close();
			pstmt.close();
			conn.close();
		}catch (SQLException e)
		{
			e.printStackTrace();
		}
		return returnVal;
		
	}
	public String[] getListNames()
	{
		String[] returnStrings = null;
		try {
			Connection conn = DriverManager.getConnection(URL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getListNames);
			Vector<String> listNames= new Vector<String>(1);
			
			while(rs.next())
			{
				listNames.add(rs.getString("Name"));
			}
			rs.close();
			stmt.close();
			conn.close();
			System.out.println("returning "+listNames.size());
			returnStrings = listNames.toArray(new String[listNames.size()]);
			
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return returnStrings;
	}
	public boolean deleteCounter(String message) {
		boolean returnValue = false;
		try {
			if (deleteMsg(message))
			{	
				Connection conn = DriverManager.getConnection(URL);
				PreparedStatement pstmt = conn.prepareStatement(delCounter);
				pstmt.setString(1, message);
				pstmt.executeUpdate();
				pstmt.close();
				conn.close();
				returnValue = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;	
	}

	public boolean deleteList(String message) {
		boolean returnValue = false;
		try {
			if (deleteMsg(message))
			{	
				Connection conn = DriverManager.getConnection(URL);
				PreparedStatement pstmt = conn.prepareStatement(delList);
				pstmt.setString(1, message);
				pstmt.executeUpdate();
				pstmt.close();
				conn.close();
				returnValue = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;	
	}

	public boolean deleteUser (String username)
	{
		boolean returnValue = false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(deleteUser);
			pstmt.setString(1, username);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			returnValue = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;

	}
	
	public boolean editUser (Access level, String newName, String oldName) {
		boolean returnValue = false;
		PreparedStatement pstmt;
		try {
			Connection conn = DriverManager.getConnection(URL);
			pstmt = conn.prepareStatement(updateUser);
			pstmt.setString(1, level.getDescription());
			pstmt.setString(2, newName);
			pstmt.setString(3, oldName);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			returnValue = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;

	}
	
	public User getUser(String searchName) {
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(sqlSpecificUser);
			pstmt.setString(1, searchName);
			ResultSet rs = pstmt.executeQuery();

			int id = rs.getInt("ID");
			Access accessLevel = Access.valueOf(Access.class, rs.getString("AccessLevel"));
			String name = rs.getString("Name");
			rs.close();
			pstmt.close();
			conn.close();
			return new User(name, id, accessLevel);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ListMessage[] getListMessages(String listName) {
		Vector<ListMessage> messages = new Vector<ListMessage>();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getListFromCommand);
			pstmt.setString(1, listName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				
				ListMessage m = new ListMessage();
				m.setName(listName);
				m.setAccessLevel(Access.valueOf(Access.class, rs.getString("AccessLevel")));
				m.setOrdinal(rs.getInt("Ordinal"));
				m.setText(rs.getString("Message"));
				m.setCategory(Category.ListItem);
				
				messages.add(m);
			}
			rs.close();
			pstmt.close();
			conn.close();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		if (messages.size() ==0)
			return null;
		return messages.toArray(new ListMessage[messages.size()]);
	}
	
	public int getListSize(String listName) {
		int returnValue = -1;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getListCount);
			pstmt.setString(1,listName);
			ResultSet rs = pstmt.executeQuery();

			returnValue = rs.getInt("COUNT(*)");
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public boolean addListItem(String message, int Ordinal, String name) {
		
		boolean returnValue =false;
		
		try {
//			private final String addListItem ="INSERT INTO Lists(Message, Name, Oridinal) VALUES(?,?,?);";

			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(addListItem);
			pstmt.setString(1, message);
			pstmt.setString(2, name);
			pstmt.setInt(3, Ordinal);			
			
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			returnValue = true;
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
		
	public boolean addToComplexFunctions(String commandName, String function) {
	//	private final String addListCommand = "INSERT INTO ComplexFunctions(Command, Function) VALUES(?,?);";
		boolean returnValue =false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(addListCommand);
			pstmt.setString(1, chatPrefix+commandName.replaceAll("^"+chatPrefix, ""));
			pstmt.setString(2, function);
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			returnValue = true;
		} catch (SQLException e) {
			e.printStackTrace();
			returnValue=false;
		}
		return returnValue;
	}
	
	public boolean isCommand(String message) {
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(checkCommand);

			pstmt.setString(1, message);

			ResultSet rs = pstmt.executeQuery();
			int returnInt = rs.getInt("count");
			rs.close();
			pstmt.close();
			conn.close();
			return (returnInt > 0);
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean addCounter(String command, String response) {
		boolean returnvalue = false;
		if (addCommand(command, response, Access.General, Category.Counter)) {
			try {
				Connection conn = DriverManager.getConnection(URL);
				PreparedStatement pstmt = conn.prepareStatement(newCounter);
				pstmt.setString(1,chatPrefix+command.replace(chatPrefix, ""));
				pstmt.executeUpdate();
				pstmt.close();
				conn.close();
				returnvalue = true;
			} catch (SQLException e) {
				System.out.println("Add Counter ERROR:" + e.getMessage());
			}
		}
		return returnvalue;
	}

	public boolean addCommand(String command, String response, Access accessLevel,  Category category) {
		boolean returnValue = false;
		try {
//	private final String addMSG = "INSERT INTO Commands(Name, Message,AccessLevel,Category) VALUES(?,?,?,?);";

			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(addMSG);
			pstmt.setString(1, chatPrefix+command.replace(chatPrefix,""));
			pstmt.setString(2, response);
			pstmt.setString(3, accessLevel.getDescription());
			pstmt.setString(4, category.getDescription());
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			returnValue = true;
		} catch (SQLException e) {
			System.out.println("Add Command ERROR:" + e.getMessage());
		}
		return returnValue;
	}
	
	public boolean editCommand(String newcommand, String newResponse, Access level, String oldCommand) {
		//private final String updateCommandAdvanced = "UPDATE Commands SET Name = ?, Message = ?, AccessLevel, Category where Name = ?;";
		boolean returnValue = false;
		try {
				Connection conn = DriverManager.getConnection(URL);
				PreparedStatement pstmt = conn.prepareStatement(updateCommandAdvanced);
				pstmt.setString(1, chatPrefix+newcommand.replace(chatPrefix,""));
				pstmt.setString(2,newResponse);
				pstmt.setString(3, level.getDescription());
				pstmt.setString(4, chatPrefix+oldCommand.replace(chatPrefix,""));
					
				pstmt.executeUpdate();
				pstmt.close();
				conn.close();
				returnValue = true;
		}catch(SQLException e) {
			e.printStackTrace();

		}
		return returnValue;
	}
	
	public boolean editCommand(String command, String newResponse) {
		boolean returnValue = false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(updateCommand);
			pstmt.setString(1, newResponse);
			pstmt.setString(2,chatPrefix+command.replace(chatPrefix, ""));
				
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
			returnValue = true;

		} catch (SQLException e) {
			System.out.println("Edit Command ERROR:" + e.getMessage());
		}
		return returnValue;
	}
	
	public void increaseCounter(String command) {
		int count = getCounterValue(chatPrefix+command.replace(chatPrefix, ""));
		
		if (count != -1) {
			setCounter(command,count+1);
		}
	}
	
	public void setCounter(String command, int newvalue) {
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(updateCounter);
			pstmt.setInt(1, newvalue);
			pstmt.setString(2, chatPrefix+command.replace(chatPrefix, ""));
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			}catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
		}
	}
	
	public boolean renameCounter(String newName, String oldName) {
		boolean returnValue = false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(renameCounter);

			ps.setString(1, newName);
			ps.setString(2, oldName);
			ps.executeUpdate();
			
			ps.close();
			conn.close();
			returnValue = true;


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public int getCounterValue (String Command) {
		int returnValue = -1;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getCounterValue);
			pstmt.setString(1, Command);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.getFetchSize() > -1) {
				 returnValue = rs.getInt("Value");
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		}
		return returnValue;
	}
	public String getComplexFunction(String Command) {
		String functionName = "";
		try {

			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getComplexFunction);
			pstmt.setString(1, Command);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.getFetchSize() > -1) {
				functionName = rs.getString("Function");
			}
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
		return functionName;
	}

	public Message chatCommand(String command) {
		Message message = new Message();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getMessageFromCommand);
			pstmt.setString(1, command);
			ResultSet rs = pstmt.executeQuery();

			if (rs.getFetchSize() > -1) {
				message.setName(rs.getString("Name"));
				message.setText(rs.getString("Message"));
				message.setCategory(Category.valueOf(Category.class, rs.getString("Category")));
				message.setAccessLevel(rs.getString("AccessLevel") == null ? Access.General
						: Access.valueOf(Access.class, rs.getString("AccessLevel")));
			}
			rs.close();
			pstmt.close();
			conn.close();


		} catch (SQLException e) {
			e.printStackTrace(System.out);
		}
		return message;
	}
	
	public boolean checkStatusOfDatabase() {
//		private final String checkStatusOfDatabase = "SELECT Description FROM Access where Description = \"Creator\";";
		boolean returnValue = false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt;
			pstmt = conn.prepareStatement(checkStatusOfDatabase);
			ResultSet rs = pstmt.executeQuery();
			returnValue = rs.getFetchSize() == 0;
			
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
		}
		return returnValue;
	}
	public boolean addUser(String name, Access access) {
		boolean returnValue = false;
		if (getUser(name)==null)
		{
			try {
				Connection conn = DriverManager.getConnection(URL);
				PreparedStatement ps = conn.prepareStatement(addUser);
				ps.setString(2, name);
				ps.setString(1, access.getDescription());
				ps.executeUpdate();
				
				ps.close();
				conn.close();
				returnValue= true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return returnValue;
	}
	
	public void setupDatabase() {
		try {
			String setupBasicCommands = "INSERT INTO Commands (Name, Message, AccessLevel, Category) VALUES(\""+chatPrefix+"addcom\",\"Command Added\", \"Mod\",\"Command\"),(\""+chatPrefix+"addlist\",\"List Added\",\"Mod\",\"Command\"),(\""+chatPrefix+"editcom\",\"Command Edited\",\"Mod\",\"Command\"),(\""+chatPrefix+"addcounter\",\"Counter Added\",\"Mod\",\"Command\");";
			String setupComplexDefaults = "INSERT INTO ComplexFunctions(Command, Function) VALUES(\""+chatPrefix+"addcom\",\"addCommand\"),(\""+chatPrefix+"editcom\",\"editCommand\"),(\""+chatPrefix+"addcounter\",\"addCounter\"),(\""+chatPrefix+"addlist\",\"addList\")";

			Connection conn = DriverManager.getConnection(URL);
			conn.prepareStatement(setupAccess).executeUpdate();
			conn.prepareStatement(setupCategories).executeUpdate();
			conn.prepareStatement(setupCommands).executeUpdate();
			conn.prepareStatement(setupComplexFunctions).executeUpdate();
			conn.prepareStatement(setupCounters).executeUpdate();
			conn.prepareStatement(setupLists).executeUpdate();
			conn.prepareStatement(setupUsers).executeUpdate();
			
			conn.prepareStatement(setupAccessDefaults).executeUpdate();
			conn.prepareStatement(setupCategoryDefaults).executeUpdate();
			conn.prepareStatement(setupComplexDefaults).executeUpdate();
			conn.close();
			
			conn = DriverManager.getConnection(URL);
			conn.prepareStatement(setupBasicCommands).executeUpdate();
			Properties props = new Properties();
			InputStream input = new FileInputStream(propsFile);
			props.load(input);
			conn.close();
			addUser(props.getProperty("channelToJoin"), Access.Creator);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
