package entities;

import org.apache.commons.lang3.StringUtils;

public class ListMessage extends Message {
	//private final String getListFromCommand ="SELECT ID, Ordinal, Message FROM Commands JOIN Lists ON Lists.ID = Commands.List WHERE Commands.Name = ?;";

	private int ordinal;
	public ListMessage() {
		
	}
	public ListMessage(Message msg) {
		this.setAccessLevel(msg.getAccessLevel());
		this.setName(msg.getName());
		this.setCategory(msg.getCategory());
		this.setText(msg.getText());
		this.setOrdinal(0);
	}

	public int getOrdinal() {
		return ordinal;
	}
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
	@Override
	public String toString()
	{
		return StringUtils.leftPad(ordinal+"",4," ")+"   "+StringUtils.rightPad(getName(), 15)+StringUtils.rightPad(getText(), 25);
	}
}
