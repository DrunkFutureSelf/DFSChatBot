package database;

import java.awt.Color;
import java.awt.Font;
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
import java.util.Properties;
import java.util.Vector;

import entities.Access;
import entities.Category;
import entities.FullMessage;
import entities.ListMessage;
import entities.Message;
import entities.StreamUIElement;
import entities.StreamUIProfile;
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
	private final String getListCount = "SELECT COUNT(*) FROM Lists WHERE Lists.Name = ?;";
	private final String getCommands = "SELECT DISTINCT Commands.Name, Commands.Category FROM Commands ORDER BY Commands.Name;";
	private final String updateListItem = "UPDATE Lists SET Message = ?, Name = ?, Ordinal = ? WHERE Lists.Name = ? AND Lists.Ordinal =?;";
	private final String checkOrdinal = "Select Count(*) FROM Lists WHERE Lists.Name = ? AND Ordinal = ?;";
	private final String addListCommand = "INSERT INTO ComplexFunctions(Command, Function) VALUES(?,?);";
	
	private final String setupAccess="CREATE TABLE Access (Description CHAR PRIMARY KEY UNIQUE NOT NULL);";
	private final String setupCategories= "CREATE TABLE Categories (Description CHAR PRIMARY KEY UNIQUE NOT NULL);";
	private final String setupCommands ="CREATE TABLE Commands (Name PRIMARY KEY UNIQUE NOT NULL, Message STRING, AccessLevel REFERENCES Access (Description),Category REFERENCES Categories (Description) );";
	private final String setupComplexFunctions="CREATE TABLE ComplexFunctions (Command  STRING PRIMARY KEY UNIQUE, Function NOT NULL);";
	private final String setupCounters = "CREATE TABLE Counters (Name  STRING  NOT NULL REFERENCES Commands (Name) UNIQUE, Value INTEGER);";
	private final String setupLists = "CREATE TABLE Lists (Name STRING REFERENCES Commands (Name) NOT NULL, Ordinal INT    NOT NULL, Message STRING NOT NULL);";
	private final String setupUsers = "CREATE TABLE Users (ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, AccessLevel REFERENCES Access (Description), Name CHAR NOT NULL UNIQUE);";
	private final String setupUIElements= "CREATE TABLE UIElements ( ID INTEGER PRIMARY KEY AUTOINCREMENT, Name STRING, Text STRING, Profile STRING REFERENCES UIProfile (Name), Command CHAR REFERENCES Commands (Name), Icon STRING, FontFamily STRING, FontStyle INTEGER, FontSize INTEGER, Active INTEGER, XPosition INTEGER, YPosition INTEGER, ZPosition INTEGER, Height INTEGER, Width INTEGER, Visible BOOLEAN, SecondsActive INTEGER, BackgroundColor INTEGER, TextColor INTEGER);";
	private final String setupUIProfile = "CREATE TABLE UIProfile (Name STRING PRIMARY KEY NOT NULL UNIQUE, Height INTEGER NOT NULL, Width INTEGER NOT NULL, Background INTEGER);";
	private final String setupGeneralTable = "CREATE TABLE General (Keys  STRING, Value STRING);";
	
	private final String setupAccessDefaults = "INSERT INTO Access (Description) VALUES (\"Creator\"),(\"Mod\"),(\"VIP\"),(\"General\")";
	private final String setupCategoryDefaults = "INSERT INTO Categories (Description) VALUES(\"List\"),(\"Response\"),(\"Command\"),(\"Counter\")";
	
	private final String checkStatusOfDatabase = "SELECT Description FROM Access where Description = \"Creator\";";
	
	private final String addUser="INSERT INTO Users (AccessLevel,Name) VALUES (?,?);";
	private final String deleteUser = "DELETE From Users where Name = ?";
	private final String deleteListItem = "DELETE FROM Lists where Ordinal = ? and Name = ?";
	private final String updateUser = "UPDATE Users SET AccessLevel = ?,  Name = ? WHERE Name = ?;";
	
	private final String selectUIElement ="SELECT UIElements.*, Commands.Name as MessageName, Commands.* FROM UIElements JOIN Commands on UIElements.Command=Commands.Name WHERE ID = ?;";
	private final String selectAllUIElementsByProfile = "SELECT UIElements.*,Commands.Name as MessageName,message,accessLevel,Category,Value,Height,Width FROM UIElements JOIN Commands on Commands.Name = UIElements.Command JOIN Counters ON UIElements.Command = Counters.Name WHERE Profile = ?;";
	private final String addUIElement = "INSERT INTO UIElements (Name, Text , Profile, Command, Icon, FontFamily, FontStyle, FontSize, Active, XPosition, YPosition, ZPosition, TextColor, BackgroundColor, Height, Width,SecondsActive) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private final String selectUIElementByName ="SELECT * from UIElements where Name = ? AND Profile = ?";
	private final String updateUIElementHeight="UPDATE UIElements SET Height = ? where ID =?";
	private final String updateUIElementWidth ="UPDATE UIElements SET Width = ? where ID =?";
	private final String updateUIElementActive = "UPDATE UIElements SET Active = ? where ID =?";
	private final String updateUIElementPlacement="UPDATE UIElements SET XPosition = ?, YPosition =? where ID =?";
	private final String deleteUIElementByName = "DELETE FROM UIElements where ID = ?";

	private final String removeUIElementByProfile ="Delete from UIElements where Profile = ?";
	private final String selectAllProfileNames = "SELECT Name from UIProfile;";
	private final String selectAllProfiles = "SELECT * from UIProfile;";
	private final String addUIProfile = "INSERT INTO UIProfile (Name, Height, Width, Background) VALUES (?,?,?,?);";
	private final String deleteProfileName= "DELETE from UIProfile where Name = ?;";
		
	private final String getCommandsByCategory = "SELECT Commands.Name as CommandName, Commands.Message as CommandMessage, AccessLevel, Category, c.Value as value, cf.Function as Function FROM Commands LEFT JOIN Counters as c on c.Name = Commands.Name LEFT JOIN ComplexFunctions as cf on cf.Command = Commands.Name Where Commands.Category = ? ORDER BY Commands.Name";
	private final String updateVersionNumber = "UPDATE General SET value = ? where keys = \"Version\";";
	private final String addVersionNumber = "INSERT INTO General (keys) VALUES(\"Version\");";
	private final String getVersionNumber = "Select value FROM General where keys = \"Version\";";
	
	private String URL;
	private String propsFile = "./bot.properties";
	
	private String chatPrefix;

	public Dao()  {
		Properties props = new Properties();
		try {
			InputStream input = new FileInputStream(propsFile);

			props.load(input);
			URL = "jdbc:sqlite:"+props.getProperty("database");
			chatPrefix = props.getProperty("prefix");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
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
		User[] returnUesrs = new User[0]; 
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
			returnUesrs = users.toArray(new User[users.size()]);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnUesrs;
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
			int rowCount = rs.getInt("Count(*)");
	
			returnVal = rowCount > 0;
			rs.close();
			pstmt.close();
			conn.close();
		}catch (SQLException e)
		{
			e.printStackTrace();
		}
		return returnVal;
		
	}
	public String [] getCommands() {
		//	private final String getCommands = "SELECT DISTINCT Commands.Name FROM Commands;";
		String[] returnStrings = null;
		try {
			Connection conn = DriverManager.getConnection(URL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getCommands);
			Vector<String> listNames= new Vector<String>(1);
			
			while(rs.next())
			{
				listNames.add(rs.getString("Name"));
			}
			rs.close();
			stmt.close();
			conn.close();
			returnStrings = listNames.toArray(new String[listNames.size()]);
			
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return returnStrings;

	}
	public String [] getCommands(Category cat) {
		//	private final String getCommands = "SELECT DISTINCT Commands.Name FROM Commands;";
		String[] returnStrings = null;
		try {
			Connection conn = DriverManager.getConnection(URL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(getCommands);
			Vector<String> listNames= new Vector<String>(1);
			
			while(rs.next())
			{
				if (rs.getString("Category").equals(cat.getDescription())) {
					listNames.add(rs.getString("Name"));
				}
			}
			rs.close();
			stmt.close();
			conn.close();
			returnStrings = listNames.toArray(new String[listNames.size()]);
			
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return returnStrings;

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
			e.printStackTrace();
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

	public User getUser(String searchName) {
		User returnValue = null;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(sqlSpecificUser);
			pstmt.setString(1, searchName);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {

				int id = rs.getInt("ID");
				Access accessLevel = Access.valueOf(Access.class, rs.getString("AccessLevel"));
				String name = rs.getString("Name");

				returnValue= new User(name, id, accessLevel);
			}
			rs.close();
			pstmt.close();
			conn.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}

		return returnValue;
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
				e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public void increaseCounter(String command, int value) {
		int count = getCounterValue(chatPrefix+command.replace(chatPrefix, ""));
		
		if (count != -1) {
			setCounter(command,count+value);
		}
	}
	public void resetCounter(String command) {
		int count = getCounterValue(chatPrefix+command.replace(chatPrefix, ""));
		
		if (count != -1) {
			setCounter(command,0);
		}
	}
	public void decreaseCounter(String command, int value) {
		int count = getCounterValue(chatPrefix+command.replace(chatPrefix, ""));
		
		if (count != -1) {
			setCounter(command,count-value);
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
			returnValue = rs.getInt("Value");
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
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
			functionName = rs.getString("Function");
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
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
			message.setName(rs.getString("Name"));
			message.setText(rs.getString("Message"));
			message.setCategory(Category.valueOf(Category.class, rs.getString("Category")));
			message.setAccessLevel(rs.getString("AccessLevel") == null ? Access.General
					: Access.valueOf(Access.class, rs.getString("AccessLevel")));

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
			int rowCount = 0;
			while(rs.next())
				rowCount++;
			
			returnValue = rowCount == 1;
			
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
		}
		return returnValue;
	}
	public Vector<StreamUIProfile> getAllUIProfiles(){
		Vector<StreamUIProfile> returnList = new Vector<StreamUIProfile>();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(selectAllProfiles);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				StreamUIProfile prof = new StreamUIProfile();
				prof.setBackGroundColor(new Color(rs.getInt("Background")));
				prof.setHeight(rs.getInt("Height"));
				prof.setName(rs.getString("Name"));
				prof.setWidth(rs.getInt("Width"));
				returnList.add(prof);
			}
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnList;
	}
	public Vector<String> getAllUIProfileNames()	{
		//	private final String selectAllProfiles = "SELECT Name from UIProfile;";
		Vector<String> returnList = new Vector<String>();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(selectAllProfileNames);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				returnList.add(rs.getString("Name"));
			}
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnList;
	}
	public boolean addUIProfile(String name, int height, int width, int background) {
		//private final String addUIProfile = "INSERT INTO UIProfile (Name, Height, Width, Background) VALUES (?,?,?,?);";
		boolean returnValue= false;
		
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(addUIProfile);
			ps.setString(1, name);
			ps.setInt(2,height);
			ps.setInt(3,width);
			ps.setInt(4, background);
			ps.executeUpdate();

			ps.close();
			conn.close();
			returnValue = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	

	public StreamUIElement getUIElementByID(int ID) {
		StreamUIElement item = new StreamUIElement();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(selectUIElement);
			ps.setInt(1, ID);
			ResultSet rs = ps.executeQuery();

			item.setId(rs.getInt("ID"));
			item.setName(rs.getString("Name"));
			item.setText(rs.getString("Text"));
			item.setProfile(rs.getString("Profile"));
			item.setIcon(rs.getString("Icon"));
			item.setFont(new Font(rs.getString("FontFamily"),rs.getInt("FontStyle"),rs.getInt("FontSize")));
			item.setActive((rs.getInt("Active")==1));
			item.setXPosition(rs.getInt("XPosition"));
			item.setYPosition(rs.getInt("YPosition"));
			item.setZPosition(rs.getInt("ZPosition"));
			item.setHeight(rs.getInt("Height"));
			item.setWidth(rs.getInt("Width"));
			item.setTextColor(new Color(rs.getInt("TextColor")));
			item.setBackgroundColor(new Color(rs.getInt("BackgroundColor")));
			item.setVisible(rs.getBoolean("Visible"));
			item.setDuration(rs.getInt("SecondsActive"));
				
			Message message = new Message();
				
			message.setName(rs.getString("MessageName"));
			message.setText(rs.getString("Message"));
			message.setCategory(Category.valueOf(Category.class, rs.getString("Category")));
			message.setAccessLevel(rs.getString("AccessLevel") == null ? Access.General
					: Access.valueOf(Access.class, rs.getString("AccessLevel")));
			item.setCommand(message);
 
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return item;
	}
	public boolean deleteUIProfile(String profileName) {
		boolean returnValue = false;
		
		Connection conn;
		try {
			conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(deleteProfileName);
			ps.setString(1, profileName);
			ps.executeUpdate();
			ps.close();
			conn.close();
			returnValue=true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public StreamUIElement[] getUIElementsByProfile(String profileName) {
		//private final String selectAllUIElementsByProfile = "SELECT * FROM UIElements JOIN Commands on Commands.Name = UIElements.Command WHERE Profile = ?;";

		Vector<StreamUIElement> list = new Vector<StreamUIElement>();
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(selectAllUIElementsByProfile);
			ps.setString(1, profileName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				//Text = ?, Profile = ?, Command = ?, Icon = ?, FontFamily = ?, FontStyle = ?, FontSize = ?, Active = ?, XPosition = ?, YPosition = ?, ZPosition = ? where ID = ?;";
				StreamUIElement item = new StreamUIElement();
				
				item.setId(rs.getInt("ID"));
				item.setName(rs.getString("Name"));
				item.setText(rs.getString("Text"));
				item.setProfile(rs.getString("Profile"));
				item.setIcon(rs.getString("Icon"));
				item.setFont(new Font(rs.getString("FontFamily"),rs.getInt("FontStyle"),rs.getInt("FontSize")));
				item.setActive((rs.getInt("Active")==1));
				item.setXPosition(rs.getInt("XPosition"));
				item.setYPosition(rs.getInt("YPosition"));
				item.setZPosition(rs.getInt("ZPosition"));
				item.setHeight(rs.getInt("Height"));
				item.setWidth(rs.getInt("Width"));
				item.setTextColor(new Color(rs.getInt("TextColor")));
				item.setBackgroundColor(new Color(rs.getInt("BackgroundColor")));
				item.setDuration(rs.getInt("SecondsActive"));
				
				Message message = new Message();
				
				message.setName(rs.getString("MessageName"));
				message.setText(rs.getString("Message"));
				message.setCategory(Category.valueOf(Category.class, rs.getString("Category")));
				message.setAccessLevel(rs.getString("AccessLevel") == null ? Access.General
						: Access.valueOf(Access.class, rs.getString("AccessLevel")));
				item.setCommand(message);
				item.setDisplayValue(""+rs.getInt("Value"));
				list.add(item);
			}
			rs.close();
			ps.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list.toArray(new StreamUIElement[list.size()]);
	}

	

	public boolean removeAllUIElementsByProfile(String prof){
		//	private final String removeUIElementByProfile ="Delete from UIElements where Profile = ?";
		boolean returnValue=false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(removeUIElementByProfile);
			ps.setString(1, prof);
			ps.executeUpdate();
			ps.close();
			conn.close();
			returnValue=true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	public boolean addUIElement(StreamUIElement item) {
/*		private final String addUIElement = "INSERT INTO UIElements (
 * 1-Name, 
 * 2-Text , 
 * 3-Profile, 
 * 4-Command, 
 * 5-Icon, 
 * 6-FontFamily, 
 * 7-FontStyle, 
 * 8-FontSize, 
 * 9-Active, 
 * 10-XPosition,
 * 11-YPosition, 
 * 12-ZPosition, 
 * 13-TextColor, 
 * 14-BackgroundColor, 
 * 15-Height, 
 * 16-Width,
 * 17-SecondsActive) VALUES (?????????????????);";		boolean returnValue = false;
*/	
		boolean returnValue=false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(addUIElement);
			ps.setString(1, item.getName());
			ps.setString(2, item.getText());
			ps.setString(3, item.getProfile());
			ps.setString(4, item.getCommand().getName());
			ps.setString(5, item.getIcon());
			ps.setString(6, item.getFont().getFamily());
			ps.setInt(7, item.getFont().getStyle());
			ps.setInt(8, item.getFont().getSize());
			ps.setInt(9,(item.isActive()==true)?1:0);
			ps.setInt(10, item.getXPosition());
			ps.setInt(11, item.getYPosition());
			ps.setInt(12, item.getZPosition());
			ps.setInt(13, item.getTextColor().getRGB());
			ps.setInt(14, item.getBackgroundColor().getRGB());
			ps.setInt(15, item.getHeight());
			ps.setInt(16, item.getWidth());
			ps.setInt(17, item.getDuration());
			ps.executeUpdate();
			ps.close();
			conn.close();
			returnValue= true;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	public void convertFromBeta002() {
		try {
			Connection conn = DriverManager.getConnection(URL);
			conn.prepareStatement(setupUIElements).executeUpdate();
			conn.prepareStatement(setupUIProfile).executeUpdate();
			conn.prepareStatement(setupGeneralTable).executeUpdate();
			conn.prepareStatement(addVersionNumber).executeUpdate();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("updated to 1.0.0");

	}
	
	public boolean updateVersionNumber(String VersionNumber) {
		//	private final String updateVersionNumber = "UPDATE General SET Version = ?;";
		boolean returnValue = false;
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement ps = conn.prepareStatement(updateVersionNumber);
		
			ps.setString(1, VersionNumber);
			ps.executeUpdate();
			ps.close();
			conn.close();
			returnValue= true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
	}
	
	public String getVersionNumber() {
		//	private final String getVersionNumber = "Select Version from General;";
		String returnValue="none";
		try {
			Connection conn = DriverManager.getConnection(URL);
			PreparedStatement pstmt = conn.prepareStatement(getVersionNumber);
			ResultSet rs = pstmt.executeQuery();
			returnValue = rs.getString("Value");
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	public boolean deleteUIElement(int ID) {
		boolean returnValue=false;
		try {
			Connection con = DriverManager.getConnection(URL);
			PreparedStatement ps = con.prepareStatement(deleteUIElementByName);
			ps.setInt(1, ID);
			ps.executeUpdate();
			con.close();
			returnValue=true;
		}catch(SQLException exc) {
			exc.printStackTrace();
			returnValue=false;
		}
		return returnValue;
	}
	public boolean updateUIElementLocation(int ID, int x,int y) {
		boolean returnValue = false;
		try {
			Connection con = DriverManager.getConnection(URL);
			PreparedStatement ps = con.prepareStatement(updateUIElementPlacement);
			
			ps.setInt(1, x);
			ps.setInt(2, y);
			ps.setInt(3, ID);
			ps.executeUpdate();
			con.close();
			returnValue =true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnValue =false;
		}
		return returnValue;
	}
	public boolean updateUIElementActive(int ID, boolean active) {
		boolean returnValue = false;
		try {
			Connection con = DriverManager.getConnection(URL);
			PreparedStatement ps = con.prepareStatement(updateUIElementActive);
			
			ps.setBoolean(1, active);
			ps.setInt(2, ID);
			ps.executeUpdate();
			con.close();
			returnValue =true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnValue =false;
		}
		return returnValue;
	}
	public StreamUIElement getUIElementByName(String name, String profile) {
		StreamUIElement returnValue= new StreamUIElement();
		
		try {
			Connection con = DriverManager.getConnection(URL);
			PreparedStatement ps = con.prepareStatement(selectUIElementByName);
			ps.setString(1,name);
			ps.setString(2, profile);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				returnValue.setId(rs.getInt("ID"));
				returnValue.setName(rs.getString("Name"));
				returnValue.setText(rs.getString("Text"));
				returnValue.setProfile(rs.getString("Profile"));
				returnValue.setIcon(rs.getString("Icon"));
				returnValue.setFont(new Font(rs.getString("FontFamily"),rs.getInt("FontStyle"),rs.getInt("FontSize")));
				returnValue.setActive((rs.getInt("Active")==1));
				returnValue.setXPosition(rs.getInt("XPosition"));
				returnValue.setYPosition(rs.getInt("YPosition"));
				returnValue.setZPosition(rs.getInt("ZPosition"));
				returnValue.setTextColor(new Color(rs.getInt("TextColor")));
				returnValue.setBackgroundColor(new Color(rs.getInt("BackgroundColor")));
			}

			rs.close();
			ps.close();
			con.close();
		}
		catch(SQLException exc) {
			exc.printStackTrace();
		}
		return returnValue;
	}
	
	public boolean updateUIElementDimention(int ID, int width, int height) {
		StreamUIElement item = getUIElementByID(ID);
		/*	private final String updateUIElementHeight="UPDATE UIElements SET Height = ? where ID =?";
	private final String updateUIElementWidth ="UPDATE UIElements SET Width = ? where ID =?"; 
*/
		boolean returnValue = true;
		if (width!= item.getWidth()) {
			try {
				Connection con = DriverManager.getConnection(URL);
				PreparedStatement ps = con.prepareStatement(updateUIElementHeight);
				ps.setInt(1, width);
				ps.setInt(2, item.getId());
				ps.executeUpdate();
				con.close();
				returnValue =true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				returnValue =false;
			}
		}
		if (height!= item.getHeight()) {
			try {
				Connection con = DriverManager.getConnection(URL);
				PreparedStatement ps = con.prepareStatement(updateUIElementWidth);
				ps.setInt(1, height);
				ps.setInt(2, item.getId());
				ps.executeUpdate();
				con.close();
				returnValue =true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				returnValue =false;
			}
		}
		
		return returnValue;
	}
	public String getLatestVersion() {
		return ""+getClass().getPackage().getImplementationVersion();
	}
	public void performConvert() {
		System.out.println("Current version of database: "+getVersionNumber()+"\tMost current version is: "+getLatestVersion());
		switch(getVersionNumber()) {
			case "none":
			case "0.0.2":
				convertFromBeta002();
			case "1.0.0":
			case "1.0.1":
			case "1.0.2":
				UIUpdatePatch();
				
				updateVersionNumber(getLatestVersion());
				break;
		}
	}
	public void UIUpdatePatch()	{
		
		String[] updateUI= new String[] {
			"PRAGMA foreign_keys = 0;CREATE TABLE sqlitestudio_temp_table AS SELECT * FROM UIElements;DROP TABLE UIElements;CREATE TABLE UIElements ( ID INTEGER PRIMARY KEY AUTOINCREMENT, Name STRING, Text STRING, Profile STRING REFERENCES UIProfile (Name), Command CHAR REFERENCES Commands (Name), Icon STRING, FontFamily STRING, FontStyle INTEGER, FontSize INTEGER, Active INTEGER, XPosition INTEGER, YPosition INTEGER, ZPosition INTEGER, Height INTEGER, Width INTEGER, Visible BOOLEAN, SecondsActive INTEGER, BackgroundColor INTEGER, TextColor INTEGER);INSERT INTO UIElements ( Name, Text, Profile, Command, Icon, FontFamily, FontStyle, FontSize, Active, XPosition, YPosition, ZPosition, BackgroundColor, TextColor ) SELECT Name, Text, Profile, Command, Icon, FontFamily, FontStyle, FontSize, Active, XPosition, YPosition, ZPosition, BackgroundColor, TextColor FROM sqlitestudio_temp_table;DROP TABLE sqlitestudio_temp_table;PRAGMA foreign_keys = 1;",
			"PRAGMA foreign_keys = 0;CREATE TABLE sqlitestudio_temp_table AS SELECT * FROM Users;DROP TABLE Users;CREATE TABLE Users (ID INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, AccessLevel REFERENCES Access (Description), Name CHAR NOT NULL UNIQUE);INSERT INTO Users ( ID, AccessLevel, Name ) SELECT ID, AccessLevel, Name FROM sqlitestudio_temp_table;DROP TABLE sqlitestudio_temp_table;PRAGMA foreign_keys = 1;"	
		};
	
		try {
			for(String querie: updateUI)
			{
				Connection con = DriverManager.getConnection(URL);
				for(String command: querie.split(";"))
				{
					con.prepareStatement(command).executeUpdate();
				}
				
				con.close();
			}
			
			System.out.println("updated to 1.0.3");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("error updating database");
		}
		
		
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
			conn.prepareStatement(setupUIElements).executeUpdate();
			conn.prepareStatement(setupUIProfile).executeUpdate();
			conn.prepareStatement(setupGeneralTable).executeUpdate();

			conn.prepareStatement(setupAccessDefaults).executeUpdate();
			conn.prepareStatement(setupCategoryDefaults).executeUpdate();
			conn.prepareStatement(setupComplexDefaults).executeUpdate();
			conn.prepareStatement(addVersionNumber).executeUpdate();

			conn.close();
			
			conn = DriverManager.getConnection(URL);
			conn.prepareStatement(setupBasicCommands).executeUpdate();
			updateVersionNumber(getLatestVersion());

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
