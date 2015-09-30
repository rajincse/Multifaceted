package aoicreator;


import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public class AOIItem {
	protected String name;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	public AOIItem(String name, int x, int y, int width, int height) {
		super();
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	public static Type getType()
	{
		return new TypeToken<AOIItem>(){}.getType();
	}

	@Override
	public String toString() {
		return "AOIItem [name=" + name + ", x=" + x + ", y=" + y + ", width="
				+ width + ", height=" + height + "]";
	}
	
	
}
