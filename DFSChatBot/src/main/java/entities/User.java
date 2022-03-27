package entities;

import org.apache.commons.lang3.StringUtils;

public class User {
	
	private String UserName;
	private Access AccessLevel; 
	private int ID;
	
	public User(String UserName, int ID, Access AccessLevel)
	{
		this.UserName=UserName;
		this.ID=ID;
		this.AccessLevel=AccessLevel;
	}

	public User() {
		this.UserName="";
		this.AccessLevel=AccessLevel.General;
		this.ID=-1;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public Access getAccessLevel() {
		return AccessLevel;
	}

	public void setAccessLevel(Access accessLevel) {
		AccessLevel = accessLevel;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	@Override
	public String toString() {
		return StringUtils.leftPad(""+ID,3," ")+"   "+StringUtils.rightPad(UserName,20," ")+AccessLevel;
		
	}

}
