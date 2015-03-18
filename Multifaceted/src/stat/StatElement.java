package stat;

import imdb.IMDBDataSource;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Genre;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

import pivotpath.MovieSortingItem;
import pivotpath.PivotPathViewer;
import eyetrack.EyeTrackerItem;

public class StatElement implements Serializable {
	public static final int INVALID =-1;
	public static final int INFINITY_RELEVANCE =1000;
	
	private long id= INVALID;
	private String name;
	private int type=INVALID;
	private int elementCount =0;
	private int task = INVALID;
	private ArrayList<ViewItem> items = new ArrayList<ViewItem>();
	private HashMap<ViewItem, ArrayList<ViewItem>> adjacencyList = new HashMap<ViewItem, ArrayList<ViewItem>>();
	
	public StatElement(String name, int task)
	{
		if(name.contains("_"))
		{
			this.name = name.replace("_", " ").trim();
		}
		else
		{
			this.name = name;
		}
		this.task = task;
	}

	public int getElementCount() {
		return elementCount;
	}

	public void setElementCount(int elementCount) {
		this.elementCount = elementCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	 public ArrayList<ViewItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<ViewItem> items) {
		this.items = items;
	}

	public int getTask() {
		return task;
	}

	public void setTask(int task) {
		this.task = task;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StatElement)
		{
			StatElement otherElement = (StatElement) obj;
			return  otherElement.getName().equals(this.getName()) 
					&&	this.getTask() == otherElement.getTask();
//					&& this.getType() == otherElement.getType());
			
		}
		else
		{
			return super.equals(obj);
		}
		
	}
	public void printItems()
	{
		System.out.println("------------------------------------");
		ArrayList<ViewItem> sortedList = new ArrayList<ViewItem>(this.items);
		Collections.sort(sortedList);
		for(ViewItem item: sortedList)
		{
			System.out.println("Item\t"+item.getName()+"\t"+item.getId()+"\t"+item.getType()+"\t"+item.getRelevance());
		}
		System.out.println("------------------------------------");
	}
	private ViewItem addItem(long id, int type, String name)
	{
		ViewItem item = new ViewItem(id, type, name);
		if(this.items.contains(item))
		{
			int index = this.items.indexOf(item);
			item = this.items.get(index);
		}
		else
		{
			item.setRelevance(INFINITY_RELEVANCE);
			this.items.add(item);
		}
		
		return item;
	}
	private void addEdge(ViewItem source, ViewItem dest)
	{
		if(this.adjacencyList.containsKey(source) && this.adjacencyList.containsKey(dest))
		{
			ArrayList<ViewItem> connectionListSource = this.adjacencyList.get(source);
			connectionListSource.add(dest);
			
			ArrayList<ViewItem> connectionListDest = this.adjacencyList.get(dest);
			connectionListDest.add(source);
		}
		else
		{
			if(!this.adjacencyList.containsKey(source))
			{
				ArrayList<ViewItem> connectionList = new ArrayList<ViewItem>();
				connectionList.add(dest);
				this.adjacencyList.put(source, connectionList);
			}
			else
			{
				ArrayList<ViewItem> connectionListSource = this.adjacencyList.get(source);
				connectionListSource.add(dest);
			}
			
			if(!this.adjacencyList.containsKey(dest))
			{
				ArrayList<ViewItem> connectionList = new ArrayList<ViewItem>();
				connectionList.add(source);
				this.adjacencyList.put(dest, connectionList);
			}
			else
			{
				ArrayList<ViewItem> connectionListDest = this.adjacencyList.get(dest);
				connectionListDest.add(source);
			}
		}
		
	}
	 public int calculateCount(IMDBDataSource data)
	 {
		if(this.getType() == EyeTrackerItem.TYPE_MOVIE)
		{
			this.elementCount = getMovieViewElementCount(this.getId(), data);
		}
		else if(this.getType() == EyeTrackerItem.TYPE_ACTOR || this.getType()== EyeTrackerItem.TYPE_DIRECTOR)
		{
			this.elementCount = getPersonViewElementCount(this.getId(), this.getType(), data);
		}
		
		return this.elementCount;
	 }
	 private void propagateRelevance(ViewItem source)
	 {
		 ArrayList<ViewItem> connectionList = this.adjacencyList.get(source);
		 if(connectionList != null)
		 {
			 for(ViewItem destination: connectionList)
			 {
				 if(destination.getRelevance() > source.getRelevance() +1)
				 {
					 destination.setRelevance(source.getRelevance() +1);
					 propagateRelevance(destination);
				 }
			 } 
		 }
		 
	 }
	 private void printGraph()
	 {
		 String msg ="";
		 for(ViewItem source: this.adjacencyList.keySet())
		 {
			 msg+=source.getName()+"=>[";
			 for(ViewItem dest: this.adjacencyList.get(source))
			 {
				 msg+=dest.getName()+", ";
			 }
			 msg+="]\r\n";
		 }
		 System.out.println(msg);
	 }
	 public void processItems(IMDBDataSource data, ArrayList<ViewItem> relevantList )
	 {
		 calculateCount(data);
//		 printGraph();
		 if(relevantList != null)
		 {
			 for(ViewItem item: relevantList)
			 {
				 int index = this.items.indexOf(item);
				 if(index >= 0)
				 {
					 ViewItem relevantItem = this.items.get(index);
					 relevantItem.setRelevance(0);
					 propagateRelevance(relevantItem);
				 }
				 
			 }
		 }
		 
		 
		 
	 }
	

	 private int getMovieViewElementCount(long id, IMDBDataSource data)
		{
			
			ArrayList<CompactMovie> movieList = this.createMovieList(new CompactMovie(id, "title",1900), data);
			return getElementCount(movieList, data);
		}
	 private int getPersonViewElementCount(long id, int type, IMDBDataSource data)
		{
			CompactPerson compactPerson = new CompactPerson(id, "Name","sex");
			Person person = data.getPerson(compactPerson);
			ArrayList<CompactMovie> movieList=null;
			if(type == EyeTrackerItem.TYPE_ACTOR)
			{
				movieList = person.getActedMovieList();
			}
			else
			{
				movieList = person.getDirectedMovieList();
			}
			return getElementCount(movieList, data);
		}
		
		private int getElementCount(ArrayList<CompactMovie> movieList, IMDBDataSource data)
		{
			this.items.clear();
			int movieCount = Math.min( PivotPathViewer.MAX_MOVIE, movieList.size());
			ArrayList<CompactPerson> actorList = new ArrayList<CompactPerson>();
			ArrayList<CompactPerson> directorList = new ArrayList<CompactPerson>();
			ArrayList<Genre> genreList = new ArrayList<Genre>();
			
			
			for(int i=0;i<movieCount;i++)
			{
				CompactMovie compactMovie = movieList.get(i);
				Movie movie = data.getMovie(compactMovie);
				ViewItem movieItem = addItem(movie.getId(), EyeTrackerItem.TYPE_MOVIE, movie.getTitle());
				ViewItem starItem = addItem(movie.getId(), EyeTrackerItem.TYPE_MOVIE_STAR_RATING, movie.getTitle());
				addEdge(movieItem, starItem);
				for(CompactPerson director: movie.getDirectors())
				{
					if(!directorList.contains(director))
					{
						directorList.add(director);
						
					}
					ViewItem directorItem = addItem(director.getId(), EyeTrackerItem.TYPE_DIRECTOR, director.getName());
					addEdge(movieItem, directorItem);
				}
				
				for(Genre genre: movie.getGenreList())
				{
					if(!genreList.contains(genre))
					{
						genreList.add(genre);
					}
					ViewItem genreItem = addItem(genre.getId(), EyeTrackerItem.TYPE_GENRE, genre.getGenreName());
					addEdge(movieItem, genreItem);
				}
				int actorCount=0;
				for(CompactPerson actor: movie.getActors())
				{
					if(!actorList.contains(actor))
					{
						actorList.add(actor);
						actorCount++;
					}
					ViewItem actorItem = addItem(actor.getId(), EyeTrackerItem.TYPE_ACTOR, actor.getName());
					addEdge(movieItem, actorItem);
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
		private ArrayList<CompactMovie> createMovieList(CompactMovie compactMovie, IMDBDataSource data)
		{
			ArrayList<CompactMovie> movieList = new ArrayList<CompactMovie>();
			movieList.add(compactMovie);
			
			PriorityQueue<MovieSortingItem> priorityQueue = new PriorityQueue<MovieSortingItem>();
			MovieSortingItem item =new MovieSortingItem(compactMovie,PivotPathViewer.MAX_MOVIE+1);
			priorityQueue.add(item);
			HashMap<Long, MovieSortingItem> movieSortingMap = new HashMap<Long, MovieSortingItem>();
			movieSortingMap.put(item.getMovie().getId(), item);
			
			
			Movie movie =data.getMovie(compactMovie);
			ArrayList<CompactPerson> actorList = movie.getActors();
			int actorCount =0;
			for(CompactPerson actor: actorList)
			{
				ArrayList<CompactMovie> actedMovieList = data.getActedMovieList(actor);
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
				ArrayList<CompactMovie> directedMovieList = data.getDirectedMovieList(director);
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
//				System.out.println(movieitem);
				if(!movieList.contains(movieitem.getMovie()))
				{
					
					movieList.add(movieitem.getMovie());
					count++;
				}
				
			}
			return movieList;
		}
	 
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{name:"+name+", id:"+(this.id==INVALID?"Invalid":this.id)+", type:"+(type==INVALID?"Invalid":this.type)+" count:"+this.elementCount+"}";
	}
	
}
