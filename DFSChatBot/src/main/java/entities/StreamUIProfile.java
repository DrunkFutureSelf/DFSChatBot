package entities;

import java.awt.Color;

public class StreamUIProfile {
	
	private String Name;
	private int Width;
	private int Height;
	private Color BackGroundColor;
	private boolean usable;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getWidth() {
		return Width;
	}
	public void setWidth(int width) {
		Width = width;
	}
	public int getHeight() {
		return Height;
	}
	public void setHeight(int height) {
		Height = height;
	}
	public Color getBackGroundColor() {
		return BackGroundColor;
	}
	public void setBackGroundColor(Color backGroundColor) {
		BackGroundColor = backGroundColor;
	}
	public boolean isUsable() {
		return usable;
	}
	public void setUsable(boolean usable) {
		this.usable = usable;
	}
	

}
