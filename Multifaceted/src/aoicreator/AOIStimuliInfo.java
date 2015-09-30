package aoicreator;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class AOIStimuliInfo {

	
	protected String imageName;
	
	protected ArrayList<AOIItem> aoiItemList;
	
	public AOIStimuliInfo(String imageName) {
		super();
		this.imageName = imageName;
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
	public void setAoiItemList(ArrayList<AOIItem> aoiItemList) {
		this.aoiItemList = aoiItemList;
	}
	
	public static Type getType()
	{
		return new TypeToken<AOIStimuliInfo>(){}.getType();
	}
}
