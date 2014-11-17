package pivotpath;

import imdb.entity.CompactMovie;

public class MovieSortingItem implements Comparable<MovieSortingItem> {

	private CompactMovie movie;
	private int value;
	public MovieSortingItem(CompactMovie movie, int value)
	{
		this.movie = movie;
		this.value = value;
	}
	
	public CompactMovie getMovie() {
		return movie;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int compareTo(MovieSortingItem other) {
		// TODO Auto-generated method stub
		return other.getValue() - this.value;
	}
	 @Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof MovieSortingItem)
		{
			return this.movie.equals(((MovieSortingItem)obj).getMovie());
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.movie+", "+this.value;
	}
}
