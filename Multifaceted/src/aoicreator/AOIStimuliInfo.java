package aoicreator;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class AOIStimuliInfo {

	
	protected String imageName;
	
	protected int width;
	
	protected int height;
	
	protected ArrayList<AOIItem> aoiItemList;
	
	public AOIStimuliInfo(String imageName,int width, int height ) {
		super();
		this.imageName = imageName;
		this.width = width;
		this.height = height;
		this.aoiItemList = new ArrayList<AOIItem>();
	}
	
	public void addItem(AOIItem item)
	{
		this.aoiItemList.add(item);
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public ArrayList<AOIItem> getAoiItemList() {
		return aoiItemList;
	}
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setAoiItemList(ArrayList<AOIItem> aoiItemList) {
		this.aoiItemList = aoiItemList;
	}
	
	public static Type getType()
	{
		return new TypeToken<AOIStimuliInfo>(){}.getType();
	}
}
