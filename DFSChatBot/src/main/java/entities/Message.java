package entities;

import org.apache.commons.lang3.StringUtils;

public class Message {
	
	private String name;
	private String text;
	private Category category;
	private Access accessLevel;
	
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Access getAccessLevel() {
		return accessLevel;
	}
	public void setAccessLevel(Access accessLevel) {
		this.accessLevel = accessLevel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return StringUtils.leftPad(name,20," ")+" "+StringUtils.rightPad(accessLevel.getDescription(),10," ")+StringUtils.rightPad(category.getDescription(), 10," ")+text;

	}

}
