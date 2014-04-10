package imdb;


import db.mysql.DatabaseHelper;

public class IMDBMySql extends DatabaseHelper{
	public static final String URL ="jdbc:mysql://localhost:3306/imdb";
	public static final String CLASSPATH ="com.mysql.jdbc.Driver";
	public static final String USER = "root";
	public static final String PASSWORD = "rajin";
	public IMDBMySql(String url, String classPath, String user, String password) {
		super(url, classPath, user, password);
		// TODO Auto-generated constructor stub
	}
	public IMDBMySql(String host, String port, String databaseName, String userName, String password)
	{
		super( "jdbc:mysql://"+host+":"+port+"/"+databaseName, CLASSPATH, userName, password);
	}
	
	

}
