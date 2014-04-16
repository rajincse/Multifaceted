package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

public class IMDBDataServiceClient {
	public static final String ENCODING ="UTF-8";
	
	public static final String SERVLET="DataServlet";
	
	public static final String METHOD_IS_VALID ="IsValid";
    public static final String METHOD_SEARCH_MOVIE ="SearchMovie";
    public static final String METHOD_GET_MOVIE ="GetMovie";
    public static final String METHOD_SEARCH_PERSON ="SearchPerson";
    public static final String METHOD_GET_PERSON ="GetPerson";
    
    private Gson gson;
    private String host;
	public IMDBDataServiceClient(String host)
	{
		this.gson = new Gson();
		this.host = host;
		if(!this.host.endsWith("/"))
		{
			this.host+="/";
		}
	}
	public boolean isValid()
	{
		boolean result = false;
		try
		{
			String url = this.host+SERVLET+"?method="+METHOD_IS_VALID;
			String data = this.getData(url);
			if(data != null && !data.isEmpty())
			{
				result = this.gson.fromJson(data, Boolean.class);
			}
			
			
		}
		catch(Exception e)
		{
			
		}
		
		return result;
		
	}
	public ArrayList<CompactMovie> searchMovie(String searchKey)
	{
		
		try {
			String url = this.host+SERVLET+"?method="+METHOD_SEARCH_MOVIE+"&searchKey="+URLEncoder.encode(searchKey, ENCODING);
			String data = this.getData(url);
			
			ArrayList<CompactMovie> movieList = this.gson.fromJson(data, CompactMovie.getListType());
			
			return movieList;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public ArrayList<CompactPerson> searchPerson(String searchKey)
	{
		try {
			String url = this.host+SERVLET+"?method="+METHOD_SEARCH_PERSON+"&searchKey="+URLEncoder.encode(searchKey, ENCODING);
			String data = this.getData(url);
			ArrayList<CompactPerson> personList = this.gson.fromJson(data, CompactPerson.getListType());
			
			return personList;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public Movie getMovie(long id)
	{
		try {
			String url = this.host+SERVLET+"?method="+METHOD_GET_MOVIE+"&id="+URLEncoder.encode(""+id, ENCODING);
			String data = this.getData(url);
			Movie movie = this.gson.fromJson(data, Movie.getType());
			
			return movie;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Person getPerson(long id)
	{
		try {
			String url = this.host+SERVLET+"?method="+METHOD_GET_PERSON+"&id="+URLEncoder.encode(""+id, ENCODING);
			String data = this.getData(url);
			Person person = this.gson.fromJson(data, Person.getType());
			
			return person;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private  String getData(String urlToRead) throws IOException, Exception {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      url = new URL(urlToRead);
	      conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("GET");
          rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          while ((line = rd.readLine()) != null) {
            result += line;
          }
          rd.close();
     
	      return result;
	   }
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
}
