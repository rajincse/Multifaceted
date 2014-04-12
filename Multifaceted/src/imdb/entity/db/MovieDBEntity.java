package imdb.entity.db;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import db.mysql.DatabaseHelper;

import imdb.IMDBMySql;
import imdb.entity.Movie;

public class MovieDBEntity implements DBEntity<Movie>{

	private DatabaseHelper db;
	public MovieDBEntity(DatabaseHelper db)
	{
		this.db = db;
	}
	@Override
	public ArrayList<Movie> getEntityList(String searchKey) {
		ArrayList<Movie> movieList = new ArrayList<Movie>();
		String query = "SELECT 	T.id, "
				+"		T.title,  "
				+"		T.production_year AS `year` "
				+"FROM title AS T "
				+"WHERE   "
				+" T.title LIKE '%"+searchKey+"%' "
				+"AND T.kind_id = 1 "
				+"LIMIT 0,"+IMDBMySql.LIMIT+" ; ";
		DefaultTableModel table = db.getData(query);
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

	@Override
	public Movie getEntity(long id) {
		String query ="SELECT 	T.title AS title,  "
				+"		T.production_year AS `year`,   "
				+"		MI.info AS rating "
				+"FROM  "
				+"title AS T "
				+"INNER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 "
				+"WHERE  "
				+"T.id = "+id+";";
		DefaultTableModel table = db.getData(query);
		String title = table.getValueAt(0, 0).toString();
		int year = Integer.parseInt( table.getValueAt(0,1).toString());
		double rating = Double.parseDouble(table.getValueAt(0,2).toString());
		Movie movie = new Movie(id, title, year);
		movie.setRating(rating);
		movie.setLoaded(true);
		return movie;
	}

	@Override
	public boolean loadEntity(Movie entity) {
		if(!entity.isLoaded())
		{
			String query ="SELECT MI.info AS rating "
					+"FROM  "
					+"title AS T "
					+"INNER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 "
					+"WHERE  "
					+"T.id = "+entity.getId()+";";
			DefaultTableModel table = db.getData(query);
			if(table.getRowCount() > 0)
			{
				double rating = Double.parseDouble(table.getValueAt(0,0).toString());
				entity.setRating(rating);
				entity.setLoaded(true);
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}

	public static void main(String[] args)
	{
		MovieDBEntity db = new MovieDBEntity(new IMDBMySql());
		ArrayList<Movie> movieList = db.getEntityList("Dark Knight");
		
		for(Movie m : movieList)
		{
			db.loadEntity(m);
			System.out.println(m);
		}
	}

}
