package stat;

public class StatElement {
	public static final int INVALID =-1;
	
	private long id= INVALID;
	private String name;
	private int type=INVALID;
	private int elementCount =0;
	public StatElement(String name)
	{
		if(name.contains("_"))
		{
			this.name = name.replace("_", " ").trim();
		}
		else
		{
			this.name = name;
		}
		
	}

	public int getElementCount() {
		return elementCount;
	}

	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	 @Override
	public boolean equals(Object obj) {
		if(obj instanceof StatElement)
		{
			StatElement otherElement = (StatElement) obj;
			return otherElement.getName().equals(this.getName());
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	 
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{name:"+name+", id:"+(this.id==INVALID?"Invalid":this.id)+", type:"+(type==INVALID?"Invalid":this.type)+" count:"+this.elementCount+"}";
	}
	
}
