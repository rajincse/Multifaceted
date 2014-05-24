package imdb.analysis;

import java.awt.Color;
import java.util.Random;

public class AnalysisItem {
	private String id;
	private String name;
	private Color color;
	
	public AnalysisItem(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{ id:"+id+", name:"+name+", color:"+color+"}";
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof AnalysisItem)
		{
			AnalysisItem item =(AnalysisItem) obj;
			return item.getId().equals(id);
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	
}
