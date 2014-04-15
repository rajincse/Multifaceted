package imdb.entity;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;

public class Movie extends CompactMovie{
	
	protected double rating;
	
	protected ArrayList<CompactPerson> actors =null;
	protected ArrayList<CompactPerson> directors =null;
	
	
	public Movie(long id, String title, int year, double rating,  ArrayList<CompactPerson> actors,  ArrayList<CompactPerson> directors )
	{
		super(id,title,year);
        this.rating = rating;
		this.actors = actors;
		this.directors = directors;
		
	}
	

	public double getRating() {
		return rating;
	}
	public void setRating(double rating)
	{
		this.rating = rating;
	}


	public ArrayList<CompactPerson> getActors() {
		return actors;
	}
	public void setActors(ArrayList<CompactPerson> actors) {
		this.actors = actors;
	}
	public ArrayList<CompactPerson> getDirectors() {
		return directors;
	}
	public void setDirectors(ArrayList<CompactPerson> directors) {
		this.directors = directors;
	}
	public static Type getType()
	{
		return new TypeToken<Movie>(){}.getType();
	}
	public static Type getListType()
	{
		return new TypeToken<ArrayList<Movie>>(){}.getType();
	}
	
	@Override
	public String toString() {
		return super.toString()+", "+String.format("%.2f", this.rating)
				+", Actors:"+this.actors.size()
				+(this.directors.size()>0?", Director:"+this.directors.get(0).toString():"");
			
	}


}
