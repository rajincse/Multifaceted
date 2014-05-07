package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import perspectives.base.DataSource;
import perspectives.base.Property;
import perspectives.properties.PString;
import sun.misc.Perf.GetPerfAction;

public class IMDBDataSource extends DataSource{

	private static final String PROPERTY_URL = "DataService Host";
	private static final String PROPERTY_STATUS = "Status";
	private static final String PROPERTY_URL_VALUE = "http://vizlab.cs.fiu.edu/IMDBDataService/";
	
	
	private static final String STATUS_CONNECTED="Connected";
	private static final String STATUS_NOT_CONNECTED="Not Connected";
	
	
	private IMDBDataServiceClient client;
			
	public IMDBDataSource(String name) {
		super(name);
		this.client = new IMDBDataServiceClient(PROPERTY_URL_VALUE);
		try
		{
			Property<PString> pHost = new Property<PString>(PROPERTY_URL, new PString(PROPERTY_URL_VALUE))
			{
				protected boolean updating(PString newvalue) {
					client = new IMDBDataServiceClient(newvalue.stringValue());
					if(client.isValid())
					{
						onConnectionSuccess();
					}
					else
					{
						updateStatus("Can't connect:"+newvalue.stringValue());
					}
 					return true;
				};
			};
			this.addProperty(pHost);
			
			
			
			Property<PString> pStatus = new Property<PString>(PROPERTY_STATUS, new PString(STATUS_NOT_CONNECTED));
			pStatus.setReadOnly(true);
			this.addProperty(pStatus);
			
		}catch(Exception e)
		{
			
		}
	}
	
	
	private void updateStatus(String status)
	{
		removeProperty(PROPERTY_STATUS);

		Property<PString> pStatus = new Property<PString>(PROPERTY_STATUS, new PString(status));
		pStatus.setReadOnly(true);
		this.addProperty(pStatus);
	}
	private void onConnectionSuccess()	
	{
		
		Property<PString> url= getProperty(PROPERTY_URL);
		url.setReadOnly(true);
		updateStatus(STATUS_CONNECTED);
		this.loaded = true;
		
	}
	
	public ArrayList<CompactMovie> searchMovie(String searchKey)
	{
		return this.client.searchMovie(searchKey);
	}
	
	public ArrayList<CompactPerson> searchPerson(String searchKey)
	{
		return this.client.searchPerson(searchKey);		
	}
	
	public Movie getMovie(CompactMovie movie)
	{
		return this.client.getMovie(movie.getId());
	}
	
	public Person getPerson(CompactPerson person)
	{
		return this.client.getPerson(person.getId());
	}
	
	public ArrayList<CompactPerson> getActors(CompactMovie movie)
	{
		return this.client.getMovie(movie.getId()).getActors();
	}
	public ArrayList<CompactPerson> getDirectors(CompactMovie movie)
	{
		return this.client.getMovie(movie.getId()).getDirectors();
	}
	
	public ArrayList<String> getBiography(CompactPerson person)
	{
		return this.client.getPerson(person.getId()).getBiographyList();
	}
	public  ArrayList<CompactMovie> getActedMovieList(CompactPerson person)
	{
		return this.client.getPerson(person.getId()).getActedMovieList();
	}
	public  ArrayList<CompactMovie> getDirectedMovieList(CompactPerson person)
	{
		return this.client.getPerson(person.getId()).getActedMovieList();
	}
}
