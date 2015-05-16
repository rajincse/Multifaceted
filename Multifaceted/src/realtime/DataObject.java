package realtime;

public class DataObject{
	
	private String id;
	private String label;
	private int type;
	
	
	public DataObject(String id, String label, int type){
		this.id = id;
		this.label = label;
		this.type = type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{id:"+id+", label:"+label+", type:"+type+"}";
	}
	
	public String getStringValue()
	{
		return ""+this.type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
