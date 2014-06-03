package imdb.analysis;

import java.awt.Color;

public class AnalysisItem implements Comparable<AnalysisItem>{
	private String id;
	private String name;
	private Color color;
	private double value;
	
	public AnalysisItem(String id, String name)
	{
		this.id = id;
		this.name = name;
		this.value =0;
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
	
	public double getValue() {
		return value;
	}

	public void addValue(double amount) {
		this.value += amount;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{ id:"+id+", name:"+name+", color:"+color+", value:"+String.format("%.2f",value)+"}";
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

	@Override
	public int compareTo(AnalysisItem o) {
		return Double.compare(o.getValue(),this.getValue()); 
	}
	
}
