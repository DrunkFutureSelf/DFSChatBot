package entities;

public enum Access {
	
	Creator("Creator",1),
	Mod("Mod",2),
	VIP("VIP",3),
	Friend("Friend",4),
	General("General",5);
	
	private String description;
	private int rank;
	
	Access(String description, int rank)
	{
		this.description = description;
		this.rank = rank;
	}
	
	public int getRank()
	{
		return rank;
	}
	
	
	public String getDescription()
	{
		return description;
	}
}
