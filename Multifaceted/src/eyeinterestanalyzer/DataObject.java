package eyeinterestanalyzer;

public class DataObject{
	
	String id;
	String label;
	int type;
	
	boolean hidden;
	
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
}
