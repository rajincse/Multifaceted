package imdb.entity;

public interface SearchItem {
	public static final int TYPE_MOVIE =0;
	public static final int TYPE_PERSON =1;
	public int getSearchItemType();
	public long getId();
	public String getDisplayText(); 
}
