package imdb.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;



public class Person extends CompactPerson{
	
	protected ArrayList<CompactMovie> actedMovieList;
	protected ArrayList<CompactMovie> directedMovieList;
	protected ArrayList<String> biographyList;

	public Person(long id, String name, String gender
			,ArrayList<CompactMovie> actedMovieList
			, ArrayList<CompactMovie> directedMovieList
			, ArrayList<String> biographyList)
	{
		super(id,name,gender);
		this.actedMovieList = actedMovieList;
		this.directedMovieList = directedMovieList;
		this.biographyList = biographyList;
	}
	
	public static Type getType()
	{
		return new TypeToken<Person>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<Person>>(){}.getType();
	}

	public ArrayList<CompactMovie> getActedMovieList() {
		return actedMovieList;
	}

	public void setActedMovieList(ArrayList<CompactMovie> actedMovieList) {
		this.actedMovieList = actedMovieList;
	}

	public ArrayList<CompactMovie> getDirectedMovieList() {
		return directedMovieList;
	}

	public void setDirectedMovieList(ArrayList<CompactMovie> directedMovieList) {
		this.directedMovieList = directedMovieList;
	}

	public ArrayList<String> getBiographyList() {
		return biographyList;
	}

	public void setBiographyList(ArrayList<String> biographyList) {
		this.biographyList = biographyList;
	} 
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString()+" Acted:"+actedMovieList.size()
				+(this.biographyList.size()>0?", bio:"+this.biographyList.get(0).toString():"");
	}
}
