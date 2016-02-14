package architecture;

import java.io.Serializable;
import java.util.ArrayList;

public class EyeData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7996864144283724661L;
	private ArrayList<EyeEvent> events;
	private ArrayList<DataObject> dataObjects;
	public EyeData() {
		
	}
	public ArrayList<EyeEvent> getEvents() {
		return events;
	}
	public ArrayList<DataObject> getDataObjects() {
		return dataObjects;
	}
	public void setEvents(ArrayList<EyeEvent> events) {
		this.events = events;
	}
	public void setDataObjects(ArrayList<DataObject> dataObjects) {
		this.dataObjects = dataObjects;
	}
	
	
}
