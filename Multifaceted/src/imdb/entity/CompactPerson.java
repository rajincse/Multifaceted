package imdb.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class CompactPerson {
	protected long id ;
	

	protected String name;
	protected String gender;

	public CompactPerson(long id, String name, String gender)
	{
		this.id = id;
		this.name = name;
		this.gender = gender;
		
	}
	public long getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public String getGender() {
		return gender;
	}

	public static Type getType()
	{
		return new TypeToken<CompactPerson>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<CompactPerson>>(){}.getType();
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name+", "+(gender.equalsIgnoreCase("m")?"Male":"Female");
		
		
	}
}
