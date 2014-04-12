package imdb;


import imdb.entity.Actor;
import imdb.entity.Movie;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import db.mysql.DatabaseHelper;

public class IMDBMySql extends DatabaseHelper{
	public static final int LIMIT =10;
	
	public static final String CLASSPATH ="com.mysql.jdbc.Driver";
	public static final String HOST ="localhost";
	public static final String PORT ="3306";
	public static final String DB ="imdb";
	
	public static final String USER = "root";
	public static final String PASSWORD = "rajin";
	
	public IMDBMySql()
	{
		super( "jdbc:mysql://"+HOST+":"+PORT+"/"+DB, CLASSPATH, USER, PASSWORD);
	}
	public IMDBMySql(String host, String port, String databaseName, String userName, String password)
	{
		super( "jdbc:mysql://"+host+":"+port+"/"+databaseName, CLASSPATH, userName, password);
	}
	
	/*- Movie-*/
	
	public ArrayList<Movie> getMovieList(String searchKey )
	{
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		String query = "SELECT 	T.id, "
				+"		T.title,  "
				+"		T.production_year AS `year` "
				+"FROM title AS T "
				+"WHERE   "
				+" T.title LIKE '%"+searchKey+"%' "
				+"AND T.kind_id = 1 "
				+"LIMIT 0,"+LIMIT+" ; ";
		DefaultTableModel table = this.getData(query);
		int totalRows = table.getRowCount();
		for(int row=0;row<totalRows;row++)
		{
			long id =Long.parseLong( table.getValueAt(row, 0).toString());
			String title = table.getValueAt(row, 1).toString();
			int year = Integer.parseInt( table.getValueAt(row, 2).toString());
			Movie movie = new Movie(id, title, year);
			movieList.add(movie);
		}
		return movieList;
	}
	public Movie getMovie(long id)
	{
		String query ="SELECT 	T.title AS title,  "
				+"		T.production_year AS `year`,   "
				+"		MI.info AS rating "
				+"FROM  "
				+"title AS T "
				+"INNER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 "
				+"WHERE  "
				+"T.id = "+id+";";
		DefaultTableModel table = this.getData(query);
		String title = table.getValueAt(0, 0).toString();
		int year = Integer.parseInt( table.getValueAt(0,1).toString());
		double rating = Double.parseDouble(table.getValueAt(0,2).toString());
		Movie movie = new Movie(id, title, year);
		movie.setRating(rating);
		movie.setLoaded(true);
		return movie;
	}
	public void loadMovie(Movie movie)
	{
		if(!movie.isLoaded())
		{
			String query ="SELECT MI.info AS rating "
					+"FROM  "
					+"title AS T "
					+"INNER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 "
					+"WHERE  "
					+"T.id = "+movie.getId()+";";
			DefaultTableModel table = this.getData(query);
			if(table.getRowCount() > 0)
			{
				double rating = Double.parseDouble(table.getValueAt(0,0).toString());
				movie.setRating(rating);
				movie.setLoaded(true);
			}
		}
	}
	
	/*- Actor-*/
	public ArrayList<Actor> getActorList(String searchKey)
	{
		String query ="SELECT  "
				+"	N.id, "
				+"	N.`name`, "
				+"	N.gender "
				+" FROM `name` AS N "
				+"WHERE  "
				+"N.`name` LIKE '%"+searchKey+"%' "
				+"LIMIT 0,"+LIMIT+";";
		DefaultTableModel table = this.getData(query);
		int totalRows = table.getRowCount();
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		for(int row=0;row<totalRows;row++)
		{
			long id =Long.parseLong( table.getValueAt(row, 0).toString());
			String name = table.getValueAt(row, 1).toString();
			String gender = table.getValueAt(row, 2).toString();
			Actor actor = new Actor(id, name, gender);
			actorList.add(actor);
		}
		
		return actorList;
	}
	
	
}
