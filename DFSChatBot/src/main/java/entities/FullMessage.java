package entities;

import org.apache.commons.lang3.StringUtils;

public class FullMessage implements Comparable<FullMessage>{
	
	private String commandName;
	private String commandMessage;
	private Access accessLevel;
	private Category category;
	private int value;
	private int ordinal;
	private String listMessage;
	private String function;

	public FullMessage(	String commandName,	String commandMessage, Access accessLevel, Category category,int value, int ordinal, String listMessage, String function) {
		this.commandName=commandName;
		this.commandMessage=commandMessage;
		this.accessLevel=accessLevel;
		this.category=category;
		this.value =value;
		this.ordinal=ordinal;
		this.listMessage=listMessage;
		this.function=function;
	}

	public FullMessage() {
	}
	public void convertMessage(Message m) {
		this.accessLevel=m.getAccessLevel();
		this.category=m.getCategory();
		this.commandMessage = m.getText();
		this.commandName = m.getName();
	}

	@Override
	public int compareTo(FullMessage o) {
		int returnValue =0;
		returnValue = this.getCommandName().compareTo(o.getCommandName());
		if(this.getCommandName().equals(o.getCommandName())) {
			returnValue = Integer.valueOf(this.getOrdinal()).compareTo(Integer.valueOf(o.getOrdinal()));				
		}
		return returnValue;
	}
	@Override
	public String toString()
	{
		//		return StringUtils.leftPad(name,20," ")+" "+StringUtils.rightPad(accessLevel.getDescription(),10," ")+StringUtils.rightPad(category.getDescription(), 10," ")+text;

		String returnValue ="";
		returnValue = returnValue.concat(StringUtils.rightPad(commandName,15, " "));
		returnValue = returnValue.concat(StringUtils.rightPad(commandMessage, 45, " "));
		returnValue = returnValue.concat(StringUtils.rightPad(accessLevel.getDescription(),10, " "));
		if (value != 0) {
			returnValue = returnValue.concat(StringUtils.leftPad(""+value,5, " "));
		}
		if (ordinal != 0) {
			returnValue = returnValue.concat(StringUtils.leftPad(""+ordinal,5, " "));
			returnValue = returnValue.concat(StringUtils.rightPad(listMessage,30, " "));
		}
		if (function!=null)
			returnValue = returnValue.concat(StringUtils.rightPad(function,15, " "));
		return returnValue;
	}
		

	public String getCommandName() {
		return commandName;
	}

	public String getCommandMessage() {
		return commandMessage;
	}

	public Access getAccessLevel() {
		return accessLevel;
	}

	public Category getCategory() {
		return category;
	}

	public int getValue() {
		return value;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String getListMessage() {
		return listMessage;
	}

	public String getFunction() {
		return function;
	}

	public void setAccessLevel(Access accessLevel) {
		this.accessLevel=accessLevel;
	}
	public void setCategory(Category category) {
		this.category=category;
	}
	
	public void setCommandName(String commandName) {
		this.commandName=commandName;
	}
	
	public void setCommandMessage(String commandMessage) {
		this.commandMessage=commandMessage;
	}
	
	public void setValue(int value) {
		this.value=value;
	}
	
	public void setOrdinal(int ordinal) {
		this.ordinal=ordinal;
	}
	
	public void setListMessage(String listMessage) {
		this.listMessage=listMessage;
	}
	
	public void setFunction(String function) {
		this.function=function;
	}
}
