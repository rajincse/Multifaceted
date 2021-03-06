package imdb.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class CompactMovie implements SearchItem{
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
		return this.title+" ("+this.year+")";
			
	}
	@Override
	public int getSearchItemType() {
		// TODO Auto-generated method stub
		return TYPE_MOVIE;
	}
	
	@Override
    public boolean equals(Object obj) {
    	// TODO Auto-generated method stub
    	if(obj instanceof CompactMovie)
    	{
    		if(((CompactMovie)obj).getId() == this.id)
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	return super.equals(obj);
    }
	@Override
	public String getDisplayText() {
		// TODO Auto-generated method stub
		return getTitle();
	}
}
