package imdb.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class CompactMovie {
	protected long id;
	
	protected String title;
	protected int year;
	
	public CompactMovie(long id, String title, int year)
	{
		this.id =id;
		this.title = title;
		this.year = year;
		
	}
	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	

	public int getYear() {
		return year;
	}

	public static Type getType()
	{
		return new TypeToken<CompactMovie>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<CompactMovie>>(){}.getType();
	}
	@Override
	public String toString() {
		return ""+this.id+", "+this.title+", "+","+this.year;
			
	}
}
