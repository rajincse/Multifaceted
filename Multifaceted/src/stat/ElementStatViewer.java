package stat;

import imdb.IMDBDataSource;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Genre;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import eyetrack.EyeTrackerItem;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;
import pivotpath.MovieSortingItem;
import pivotpath.PivotPathViewer;
import pivotpath.analysis.PivotPathFrame;

public class ElementStatViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD="Load";
	
	
	private ArrayList<StatElement> elementNames = new ArrayList<StatElement>();
	private IMDBDataSource data;
	public ElementStatViewer(String name,IMDBDataSource data) {
		super(name);
		this.data = data;
		this.elementNames = new ArrayList<StatElement>();
		try
		{
			PFileInput inputFile = new PFileInput();
			inputFile.onlyDirectories = true;
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, inputFile)
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readUserDirectory(newvalue.path);
//							readUserFile(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
		}
		catch(Exception ex)
		{
			
		}
	}

	private void readUserDirectory(String path)
	{
		File rootDirectory = new File(path);
		File[] userDirectories = rootDirectory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File childFile) {
				// TODO Auto-generated method stub
				return childFile.isDirectory();
				
			}
		});
		
		for(File userDirectory: userDirectories)
		{
			System.out.println("User: "+userDirectory.getName()+"\r\n------------------------------");
			
			File[] dataFiles = userDirectory.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return pathname.getName().toUpperCase().endsWith(".TXT");
				}
			});
			
			for(File dataFile: dataFiles)
			{
				System.out.println("Reading: "+dataFile.getPath()+"\r\n----------------------------");
				readUserFile(dataFile.getPath());
				fillupPresetElementNames();
				populateCount();
				for(StatElement elem: this.elementNames)
				{
					System.out.println(elem.getName()+"\t"+elem.getId()+"\t"+elem.getType()+"\t"+elem.getElementCount());
				}
				
				this.elementNames.clear();
			}
			
			
		}
	}
	private void fillupPresetElementNames()	
	{
		String[] elementNames = new String[]{
								"Ang Lee", 
								"Tim Burton",
								"James Cameron",
								"David Fincher",
								"Once in a Lifetime  The Extraordinary Story of the New York Cosmos",
								"Austin Powers  International Man of Mystery"
								,"Wayne s World"
								,"St. Elmo s Fire"
								,"Joe Strummer  The Future Is Unwritten"
								,"The Transformers  The Movie"
								,"Futbaal  The Price of Dreams"
								,"The Boondock Saints II  All Saints Day"
								};
		long[] ids = new long[]{
									984258, //"Ang Lee",
									237997,//	"Tim Burton",
									253636,//"James Cameron",
									541437,//"David Fincher"
									2446725//Once in a Lifetime: The Extraordinary Story of the New York Cosmos
									,1928710//Austin Powers: International Man of Mystery
									,2796141//"Wayne's World "
									,2595417//"St. Elmo's Fire"
									,2258108//"Joe Strummer: The Future Is Unwritten"
									,2719894//"The Transformers  The Movie"
									,2154141//"Futbaal  The Price of Dreams"
									,2642731//"The Boondock Saints II  All Saints Day"
								};
		int types[] = new int[]{
				3, //"Ang Lee",
				3,//	"Tim Burton",
				3,//"James Cameron",
				3//"David Fincher"
				,2//Once in a Lifetime: The Extraordinary Story of the New York Cosmos
				,2//Austin Powers: International Man of Mystery
				,2//"Wayne's World "
				,2//"St. Elmo's Fire"
				,2//"Joe Strummer: The Future Is Unwritten"
				,2//"The Transformers  The Movie"
				,2//"Futbaal  The Price of Dreams"
				,2//"The Boondock Saints II  All Saints Day"
								};
		
		for(int i=0;i<elementNames.length;i++)
		{
			String elemName = elementNames[i];
			int index = this.elementNames.indexOf(new StatElement(elemName));
			if(index >=0)
			{
				StatElement elem = this.elementNames.get(index);
				if(elem.getId() == StatElement.INVALID)
				{
					elem.setId(ids[i]);
				}
				if(elem.getType() == StatElement.INVALID)
				{
					elem.setType(types[i]);
				}
				
			}
		}
	}
	private void readUserFile(String filePath)
	{
		try {

			
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String fileline = bufferedReader.readLine();
			ArrayList<String> fileLines = new ArrayList<String>();
			while(fileline != null)
			{	
				fileLines.add(fileline);
				fileline = bufferedReader.readLine();
			}
			
			for(String line:fileLines)
			{
				String[] splits =line.split("\t");
				if(splits[0].equals("Image"))
				{
					String imageFileName = splits[2];
				
					String elementName = imageFileName.substring(16, imageFileName.length()-9);
					elementName = elementName.replaceAll("[0-9]", "");
//					System.out.println("line=>"+line+", image=>"+imageFileName+", elem=>"+elementName);
					StatElement element = new StatElement(elementName);
					
					
					if(!this.elementNames.contains(element))
					{
						this.elementNames.add(element);
					}
				}
			}
				
			
			for(String line:fileLines)
			{
				String[] splits =line.split("\t");
				if(splits[0].equals("Eye"))
				{
					String elementName = splits[3].trim();	
					int index = this.elementNames.indexOf(new StatElement(elementName));
					if(index >= 0)
					{
						StatElement elem = this.elementNames.get(index);
						
						Long id = Long.parseLong(splits[2]);
						int type = Integer.parseInt(splits[4]);
						
						elem.setId(id);
						elem.setType(type);
					}
				}
			}
//			for(StatElement elem: this.elementNames)
//			{
//				System.out.println("Elem:"+elem);
//			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private void populateCount()
	{
		for(StatElement elem: this.elementNames)
		{
			if(elem.getType() == EyeTrackerItem.TYPE_MOVIE)
			{
				int count = getMovieViewElementCount(elem.getId());
				elem.setElementCount(count);
			}
			else if(elem.getType() == EyeTrackerItem.TYPE_ACTOR || elem.getType()== EyeTrackerItem.TYPE_DIRECTOR)
			{
				int count = getPersonViewElementCount(elem.getId(), elem.getType());
				elem.setElementCount(count);
			}
		}
	}
	
	private int getMovieViewElementCount(long id)
	{
		if(id == 2546014)
		{
			System.out.println("Got savings");
		}
		ArrayList<CompactMovie> movieList = this.createMovieList(new CompactMovie(id, "title",1900));
		return getElementCount(movieList);
	}
	
	private int getPersonViewElementCount(long id, int type)
	{
		CompactPerson compactPerson = new CompactPerson(id, "Name","sex");
		Person person = this.data.getPerson(compactPerson);
		ArrayList<CompactMovie> movieList=null;
		if(type == EyeTrackerItem.TYPE_ACTOR)
		{
			movieList = person.getActedMovieList();
		}
		else
		{
			movieList = person.getDirectedMovieList();
		}
		return getElementCount(movieList);
	}
	
	private int getElementCount(ArrayList<CompactMovie> movieList)
	{
		int movieCount = Math.min( PivotPathViewer.MAX_MOVIE, movieList.size());
		ArrayList<CompactPerson> actorList = new ArrayList<CompactPerson>();
		ArrayList<CompactPerson> directorList = new ArrayList<CompactPerson>();
		ArrayList<Genre> genreList = new ArrayList<Genre>();
		
		
		for(int i=0;i<movieCount;i++)
		{
			CompactMovie compactMovie = movieList.get(i);
			Movie movie = this.data.getMovie(compactMovie);
			for(CompactPerson director: movie.getDirectors())
			{
				if(!directorList.contains(director))
				{
					directorList.add(director);
				}
			}
			
			for(Genre genre: movie.getGenreList())
			{
				if(!genreList.contains(genre))
				{
					genreList.add(genre);
				}
			}
			int actorCount=0;
			for(CompactPerson actor: movie.getActors())
			{
				if(!actorList.contains(actor))
				{
					actorList.add(actor);
					actorCount++;
				}
				if(actorCount >= PivotPathViewer.MAX_ACTOR)
				{
					break;
				}
			}
		}
		
		int total = movieCount+actorList.size()+directorList.size()+genreList.size();
		total += movieCount; // Star Rating
		return total;
	}
	private ArrayList<CompactMovie> createMovieList(CompactMovie compactMovie)
	{
		ArrayList<CompactMovie> movieList = new ArrayList<CompactMovie>();
		movieList.add(compactMovie);
		
		PriorityQueue<MovieSortingItem> priorityQueue = new PriorityQueue<MovieSortingItem>();
		MovieSortingItem item =new MovieSortingItem(compactMovie,PivotPathViewer.MAX_MOVIE+1);
		priorityQueue.add(item);
		HashMap<Long, MovieSortingItem> movieSortingMap = new HashMap<Long, MovieSortingItem>();
		movieSortingMap.put(item.getMovie().getId(), item);
		
		
		Movie movie = this.data.getMovie(compactMovie);
		ArrayList<CompactPerson> actorList = movie.getActors();
		int actorCount =0;
		for(CompactPerson actor: actorList)
		{
			ArrayList<CompactMovie> actedMovieList = this.data.getActedMovieList(actor);
			int index=0;
			for(CompactMovie actedMovie: actedMovieList)
			{
				
				if(movieSortingMap.containsKey(actedMovie.getId()))
				{
					MovieSortingItem movieitem = movieSortingMap.get(actedMovie.getId());
					priorityQueue.remove(movieitem);
					movieitem.setValue(movieitem.getValue()+PivotPathViewer.MAX_MOVIE-index+1);
					priorityQueue.add(movieitem);
				}
				else
				{
					MovieSortingItem movieitem =new MovieSortingItem(actedMovie, PivotPathViewer.MAX_MOVIE-index+1);
					priorityQueue.add(movieitem);
					movieSortingMap.put(movieitem.getMovie().getId(), movieitem);
				}
				index++;
			}
			
			actorCount++;
			if(actorCount>= PivotPathViewer.MAX_ACTOR)
			{
				break;
			}
		}
		ArrayList<CompactPerson> directorList = movie.getDirectors();
		for(CompactPerson director: directorList)
		{
			ArrayList<CompactMovie> directedMovieList = this.data.getDirectedMovieList(director);
			int index=0;
			for(CompactMovie directedMovie: directedMovieList)
			{
				
				if(movieSortingMap.containsKey(directedMovie.getId()))
				{
					MovieSortingItem movieitem = movieSortingMap.get(directedMovie.getId());					
					priorityQueue.remove(movieitem);					
					movieitem.setValue(movieitem.getValue()+PivotPathViewer.MAX_MOVIE-index+1);
					priorityQueue.add(movieitem);
				}
				else
				{	
					MovieSortingItem movieitem =new MovieSortingItem(directedMovie, PivotPathViewer.MAX_MOVIE-index+1);
					priorityQueue.add(movieitem);
					movieSortingMap.put(movieitem.getMovie().getId(), movieitem);
				}		
				index++;
			}
		}
		int count =1;
		while(!priorityQueue.isEmpty() && count <= PivotPathViewer.MAX_MOVIE)
		{
			MovieSortingItem movieitem = priorityQueue.poll();
//			System.out.println(movieitem);
			if(!movieList.contains(movieitem.getMovie()))
			{
				
				movieList.add(movieitem.getMovie());
				count++;
			}
			
		}
		return movieList;
	}
	@Override
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void keyPressed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mousedragged(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousemoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousepressed(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousereleased(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(Graphics2D arg0) {
		// TODO Auto-generated method stub
		
	}

}
