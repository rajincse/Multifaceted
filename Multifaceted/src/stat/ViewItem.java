package stat;

import java.io.Serializable;


public class ViewItem implements Comparable<ViewItem>, Serializable{
	private static final long serialVersionUID = 9148949409946794644L;
	
	private long id;
	private int type;
	private String name;
	private int relevance;
	
	public ViewItem(long id, int type, String name)
	{
		this.id = id;
		this.type = type;
		this.name = name;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getRelevance() {
		return relevance;
	}
	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{ id:"+this.id+", type:"+this.type+", name:"+ this.name+", relevance:"+this.relevance+"}";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + type;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof ViewItem)
		{
			ViewItem item = (ViewItem) obj;
			
			return ( this.getId() == item.getId() && this.getType() == item.getType());  
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	@Override
	public int compareTo(ViewItem o) {
		// TODO Auto-generated method stub
		return this.getRelevance() - o.getRelevance();
	}
}
