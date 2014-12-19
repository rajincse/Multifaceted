package eyeinterestanalyzer;

public class DataObject{
	
	String id;
	public String label;
	public String type;
	
	boolean hidden;
	
	public DataObject(String id, String label, String type){
		this.id = id;
		this.label = label;
		this.type = type;
	}
}
