package entities;

public enum Category {
	
	List("List"),
	ListItem("ListItem"),
	Response("Response"),
	Command("Command"),
	Counter("Counter");
	
	private String description;
	
	Category(String description){
		this.description=description;
	}
	
	public String getDescription()
	{
		return description;
	}
}
