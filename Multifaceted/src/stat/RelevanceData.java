package stat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RelevanceData implements Serializable {
	private static final long serialVersionUID = 6336495023670801322L;
	
	private ArrayList<StatElement> elementNames = new ArrayList<StatElement>();
	private HashMap<Long, ArrayList<ViewItem>> relevantItemMap = new HashMap<Long, ArrayList<ViewItem>>(); 
	private ArrayList<String> ignoreList = new ArrayList<String>();
	private HashMap<String, Integer> taskList = new HashMap<String, Integer>();
	private HashMap<String, String> taskUserList = new HashMap<String, String>();
	
	public RelevanceData(	ArrayList<StatElement> elementNames
							,HashMap<Long, ArrayList<ViewItem>> relevantItemMap
							, ArrayList<String> ignoreList
							,HashMap<String, Integer> taskList
							, HashMap<String, String> taskUserList 
							
							)
	{
		this.elementNames = elementNames;
		this.relevantItemMap = relevantItemMap;
		this.ignoreList = ignoreList;
		this.taskList = taskList;
		this.taskUserList = taskUserList;
	}
	
	
	public ArrayList<StatElement> getElementNames() {
		return elementNames;
	}
	public void setElementNames(ArrayList<StatElement> elementNames) {
		this.elementNames = elementNames;
	}
	public HashMap<Long, ArrayList<ViewItem>> getRelevantItemMap() {
		return relevantItemMap;
	}
	public void setRelevantItemMap(
			HashMap<Long, ArrayList<ViewItem>> relevantItemMap) {
		this.relevantItemMap = relevantItemMap;
	}
	public ArrayList<String> getIgnoreList() {
		return ignoreList;
	}
	public void setIgnoreList(ArrayList<String> ignoreList) {
		this.ignoreList = ignoreList;
	}
	public HashMap<String, Integer> getTaskList() {
		return taskList;
	}
	public void setTaskList(HashMap<String, Integer> taskList) {
		this.taskList = taskList;
	}
	public HashMap<String, String> getTaskUserList() {
		return taskUserList;
	}
	public void setTaskUserList(HashMap<String, String> taskUserList) {
		this.taskUserList = taskUserList;
	}
}
