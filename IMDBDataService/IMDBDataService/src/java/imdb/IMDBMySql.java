package imdb;


import db.mysql.DatabaseHelper;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import util.Configuration;

public class IMDBMySql extends DatabaseHelper{
	
	public static final String CLASSPATH ="com.mysql.jdbc.Driver";
	public static final String HOST ="localhost";
	public static final String PORT ="3306";
	public static final String DB ="imdb";
	
	public static final String USER = "root";
	public static final String PASSWORD = "rajin";
	
        private static final int ACTOR_ID=1;
        private static final int ACTRESS_ID=2;
        private static final int DIRECTOR_ID=8;
        
        public static final int SORT_BY_RATING=0;
        public static final int SORT_BY_YEAR=1;
	public IMDBMySql()
	{
		super( "jdbc:mysql://"+HOST+":"+PORT+"/"+DB, CLASSPATH, USER, PASSWORD);
	}
	public IMDBMySql(String host, String port, String databaseName, String userName, String password)
	{
		super( "jdbc:mysql://"+host+":"+port+"/"+databaseName, CLASSPATH, userName, password);
	}
	
	/*- Movie-*/
	protected String getQuerySearchMovie(String searchKey )
        {
            String query = "SELECT 	T.id, "
				+"		T.title,  "
				+"		COALESCE(T.production_year,0) AS `year` "
				+"FROM title AS T "
				+"WHERE   "
				+" T.title LIKE '%"+searchKey+"%' "
				+"AND T.kind_id = 1 "
				+"LIMIT 0,"+Configuration.getQueryLimit()+" ; ";
            return query;
        }
        protected String getQueryGetMovie(long movieId)
        {
            String query ="SELECT 	T.title AS title,  "
                                +"		COALESCE(T.production_year,0) AS `year`,   "
                                +"		COALESCE(MI.info,0) AS rating, "
                                +"		C.role_id, "
                                +"		N.id,  "
                                +"		N.`name`, "
                                +"		COALESCE(N.gender,'m')  "
                                +"		 "
                                +"FROM  "
                                +"title AS T "
                                +"LEFT OUTER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 "
                                +"INNER JOIN cast_info AS C ON C.movie_id = T.id "
                                +" AND ( C.role_id=8 OR C.role_id = 1 OR C.role_id =2) "
                                +"INNER JOIN `name` AS N ON N.id = C.person_id "
                                +"WHERE  "
                                +"T.id = "+movieId+" "
                                +"ORDER BY COALESCE(C.nr_order,1000);";
            return query;
        }
        
        protected String getQuerySearchPerson(String searchKey)
        {
            String query ="SELECT   "
                            +"	N.id,  "
                            +"	N.`name`,  "
                            +"	N.gender "
                            +" FROM `name` AS N  "                            
                            +"WHERE   "
                            +"N.gender IS NOT NULL AND   "
                            +"N.`name` LIKE '%"+searchKey+"%' "
                            +"LIMIT 0,"+Configuration.getQueryLimit()+" ;";
            return query;
        }
        protected String getQueryGetPerson(long personId, int sortType)
        {
            String sortString ="COALESCE(MI.info,0) DESC";
            if(sortType == SORT_BY_YEAR)
            {
                sortString = "COALESCE(T.production_year,0) DESC";
            }
            String query="SELECT 	 "
                        +"		N.`name`, "
                        +"		N.gender, "
                        +"		C.role_id, "
                        +"		T.id, "
                        +"		T.title AS title,  "
                        +"		COALESCE(T.production_year,0) AS `year` , "
                        +"		COALESCE(PI.info,'') AS biography "
                        +"FROM  "
                        +"title AS T "
                        +"LEFT OUTER JOIN movie_info_idx AS MI ON MI.movie_id = T.id AND MI.info_type_id=101 "
                        +"INNER JOIN cast_info AS C ON C.movie_id = T.id AND (C.role_id = 1 OR C.role_id=2 OR C.role_id=8) AND T.kind_id=1 "
                        +"INNER JOIN `name` AS N ON N.id = C.person_id "
                        +" LEFT OUTER JOIN person_info AS PI ON N.id = PI.person_id AND PI.info_type_id=19 "
                        +"WHERE  "
                        +"N.id = "+personId+" "
                        +"ORDER BY "+sortString+" ;";
            return query;
        }
	public ArrayList<CompactMovie> searchMovie(String searchKey )
	{
		ArrayList<CompactMovie> movieList = new ArrayList<CompactMovie>();
		String query = this.getQuerySearchMovie(searchKey);
		DefaultTableModel table = this.getData(query);
		int totalRows = table.getRowCount();
		for(int row=0;row<totalRows;row++)
		{
			long id =Long.parseLong( table.getValueAt(row, 0).toString());
			String title = table.getValueAt(row, 1).toString();
			int year = Integer.parseInt( table.getValueAt(row, 2).toString());
			CompactMovie movie = new CompactMovie(id, title, year);
			movieList.add(movie);
		}
		return movieList;
	}
	public Movie getMovie(long movieId)
	{
		String query =this.getQueryGetMovie(movieId);
		DefaultTableModel table = this.getData(query);
		String title = table.getValueAt(0, 0).toString();
		int year = Integer.parseInt( table.getValueAt(0,1).toString());
		double rating = Double.parseDouble(table.getValueAt(0,2).toString());
		
                ArrayList<CompactPerson> actorList = new ArrayList<CompactPerson>();
                ArrayList<Long> actorIdList = new ArrayList<Long>();
                ArrayList<CompactPerson> directorList = new ArrayList<CompactPerson>();
                ArrayList<Long> directorIdList = new ArrayList<Long>();
                for(int row=0;row<table.getRowCount();row++)
                {
                    int roleID = Integer.parseInt( table.getValueAt(row,3).toString());
                    long id =Long.parseLong( table.getValueAt(row, 4).toString());
                    String name = table.getValueAt(row, 5).toString();
                    String gender = table.getValueAt(row, 6).toString();
                    CompactPerson person = new CompactPerson(id, name, gender);
                    
                    if(
                          (roleID == ACTOR_ID || roleID == ACTRESS_ID )
                            && ! actorIdList.contains(id)
                            )
                    {
                        actorList.add(person);
                        actorIdList.add(id);
                    }
                    else if(roleID == DIRECTOR_ID && !directorIdList.contains(id))
                    {
                        directorList.add(person);
                        directorIdList.add(id);
                    }
                }
                Movie movie = new Movie(movieId, title, year, rating, actorList, directorList);
		return movie;
	}
	
	
	/*- Actor-*/
	public ArrayList<CompactPerson> searchPerson(String searchKey)
	{
		String query =this.getQuerySearchPerson(searchKey);
		DefaultTableModel table = this.getData(query);
		int totalRows = table.getRowCount();
		ArrayList<CompactPerson> actorList = new ArrayList<CompactPerson>();
		for(int row=0;row<totalRows;row++)
		{
			long id =Long.parseLong( table.getValueAt(row, 0).toString());
			String name = table.getValueAt(row, 1).toString();
			String gender = table.getValueAt(row, 2).toString();
			CompactPerson actor = new CompactPerson(id, name, gender);
			actorList.add(actor);
		}
		
		return actorList;
	}
        
        public Person getPerson(long personId, int sortType)
        {  
            String query=this.getQueryGetPerson(personId, sortType);
            DefaultTableModel table = this.getData(query);            
            String name = table.getValueAt(0, 0).toString();
            String gender = table.getValueAt(0, 1).toString();
            
            ArrayList<CompactMovie> actedMovieList = new ArrayList<CompactMovie>();
            ArrayList<Long> actedMovieListId = new ArrayList<Long>();
            ArrayList<CompactMovie> directedMovieList = new ArrayList<CompactMovie>();
            ArrayList<Long> directedMovieListId = new ArrayList<Long>();
            ArrayList<String> biographyList = new ArrayList<String>();
           
            int totalRows = table.getRowCount();
            for(int row=0;row<totalRows;row++)
            {
                    long roleId =Long.parseLong( table.getValueAt(row, 2).toString());
                    
                    long movieId = Long.parseLong( table.getValueAt(row, 3).toString());
                    String title = table.getValueAt(row, 4).toString();
                    
                    int year = Integer.parseInt( table.getValueAt(row, 5).toString());
                    System.out.println(row);

                    CompactMovie movie = new CompactMovie(movieId, title, year);
                    
                    
                    if((roleId == ACTOR_ID || roleId == ACTRESS_ID) 
                            && !actedMovieListId.contains(movieId))
                    {
                        actedMovieList.add(movie);
                        actedMovieListId.add(movieId);
                    }
                    else if(roleId == DIRECTOR_ID && !directedMovieListId.contains(movieId))
                    {
                        directedMovieList.add(movie);
                        directedMovieListId.add(movieId);
                    }
                    
                    String bio = table.getValueAt(row, 6).toString();
                    if(bio != null && !bio.isEmpty() && !biographyList.contains(bio))
                    {
                        biographyList.add(bio);
                    }
            }
            Person person = new Person(personId, name, gender, actedMovieList, directedMovieList, biographyList);
            return person;
        }
	
	
}
