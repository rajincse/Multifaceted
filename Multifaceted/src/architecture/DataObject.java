package architecture;

import imdb.entity.CompactMovie;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class DataObject implements Serializable{
	
	String id;
	String label;
	int type;
	
	boolean hidden;
	
	public DataObject(String id, int type)
	{
		this.id = id;
		this.label = id;
		this.type = type;
	}
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + type;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataObject other = (DataObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	public static Type getTokenType()
	{
		return new TypeToken<DataObject>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<DataObject>>(){}.getType();
	}
}
