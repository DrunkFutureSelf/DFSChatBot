package entities;

import java.awt.Color;
import java.awt.Font;

public class StreamUIElement {
	private int Id;
	private String Name;
	private String Profile;
	private String Text;
	private Message Command;
	private String Icon;
	private Font font;
	private boolean Active;
	private int XPosition;
	private int YPosition;
	private int ZPosition;
	private int Width;
	private int Height;
	private Color TextColor;
	private Color BackgroundColor;
	private String DisplayValue;
	private boolean Visible;
	private int Duration;
	
	public String getProfile() {
		return Profile;
	}
	public void setProfile(String profile) {
		Profile = profile;
	}
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}
	public Message getCommand() {
		return Command;
	}
	public void setCommand(Message command) {
		Command = command;
	}
	public String getIcon() {
		return Icon;
	}
	public void setIcon(String icon) {
		Icon = icon;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public boolean isActive() {
		return Active;
	}
	public void setActive(boolean active) {
		Active = active;
	}
	public int getXPosition() {
		return XPosition;
	}
	public void setXPosition(int xPosition) {
		XPosition = xPosition;
	}
	public int getYPosition() {
		return YPosition;
	}
	public void setYPosition(int yPosition) {
		YPosition = yPosition;
	}
	public int getZPosition() {
		return ZPosition;
	}
	public void setZPosition(int zPosition) {
		ZPosition = zPosition;
	}
	public Color getTextColor() {
		return TextColor;
	}
	public void setTextColor(Color textColor) {
		TextColor = textColor;
	}
	public Color getBackgroundColor() {
		return BackgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		BackgroundColor = backgroundColor;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getDisplayValue() {
		return DisplayValue;
	}
	public void setDisplayValue(String displayValue) {
		DisplayValue = displayValue;
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
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public boolean isVisible() {
		return Visible;
	}
	public void setVisible(boolean visible) {
		Visible = visible;
	}
	public int getDuration() {
		return Duration;
	}
	public void setDuration(int duration) {
		Duration = duration;
	}
}
