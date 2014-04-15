package imdb.entity;

public class Movie {
	private long id;
	
	private String title;
	private int year;
	
	private boolean isLoaded;
	
	
	private double rating;
	
	public Movie(long id, String title, int year)
	{
		this.id =id;
		this.title = title;
		this.year = year;
		
		this.isLoaded = false;
	}
	
	
	public boolean isLoaded() {
		return isLoaded;
	}
	public void setLoaded(boolean isLoaded)
	{
		this.isLoaded = isLoaded;
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


	public double getRating() {
		return rating;
	}
	public void setRating(double rating)
	{
		this.rating = rating;
	}


	@Override
	public String toString() {
		if(isLoaded)
		{
			return ""+this.id+", "+this.title+", "+","+this.year+", "+String.format("%.2f", this.rating);
		}
		else
		{
			return ""+this.id+", "+this.title+", "+","+this.year;
		}
			
	}


}
