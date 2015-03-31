package stat;

import imdb.IMDBDataSource;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Genre;
import imdb.entity.Movie;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import eyetrack.EyeTrackerItem;

import multifaceted.IOUtil;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class TransitionProbabilityViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD = "Load";
	public static final String PROPERTY_LOAD_RELEVANCE_DATA = "Load Relevance";
	
	public static final String TAG_HOVER="HOV";
	public static final String TAG_VIEW ="VIEW";
	
	public static final int TOTAL_TYPES =6;
	public static final int TOTAL_CONNECTION_TYPE=6;
	
	public static final int CONNECTION_NOT_CONNECTED_NO_HOVER =0;
	public static final int CONNECTION_NOT_CONNECTED_DESTINATION_HOVER =1;
	public static final int CONNECTION_CONNECTED_NO_HOVER =2;
	public static final int CONNECTION_CONNECTED_DESTINATION_HOVER =3;
	
	
	
	public static final String[] TYPE_NAME =new String[]{"INIT", "ACTOR", "MOVIE", "DIRECTOR", "GENRE", "STAR"};
	public static final String[] CONNECTION_NAME =new String[]{"NOT CONNECTED NO HOVER",
														   "NOT CONNECTED DESTINATION HOVER",
														   "CONNECTED NO HOVER ",
														   "CONNECTED DESTINATION HOVER"};
	
	public static final int COLUMNS_PER_ELEMENT =4;
	
	private int [][][] countMatrix = new int[TOTAL_TYPES][TOTAL_TYPES][TOTAL_CONNECTION_TYPE];
	private int [][][] possibilityMatrix = new int[TOTAL_TYPES][TOTAL_TYPES][TOTAL_CONNECTION_TYPE];
	
	private ArrayList<StatElement> elementNames = new ArrayList<StatElement>();
	private IMDBDataSource data = null;
	
	private Element currentHover=null;
	private StatElement currentView =null;
	public TransitionProbabilityViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
		
		Property<PFileInput> pLoadRelevance = new Property<PFileInput>(PROPERTY_LOAD_RELEVANCE_DATA, new PFileInput())
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						// TODO Auto-generated method stub
						loadSavedData(newvalue.path);
						return super.updating(newvalue);
					}
			
				};
		addProperty(pLoadRelevance);
		
		PFileInput directoryOnly = new PFileInput();
		directoryOnly.onlyDirectories = true;
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD,directoryOnly)
				{
					@Override
					protected boolean updating(PFileInput newvalue) {
						// TODO Auto-generated method stub
						readTransProbDirectory(newvalue.path);
//						readTransProbFile(newvalue.path);
						printResult();
						return super.updating(newvalue);
					}
				};
		addProperty(pLoad);
	}
	private void loadSavedData(String filePath)
	{
		try {
			File file =new File(filePath);
			FileInputStream fOutput = new FileInputStream(file);
		
			ObjectInputStream out = new ObjectInputStream(fOutput);
			
			RelevanceData data = (RelevanceData) out.readObject();
			this.elementNames = data.getElementNames();
			out.close();
			fOutput.close();
			System.out.println("Loaded:"+filePath);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class Element
	{
		public boolean isHovered;
		public String name;
		public String id;
		public int type;
		public double score;
		public Element(boolean isHovered, String name, String id, int type,
				double score) {
			this.isHovered = isHovered;
			this.name = name;
			this.id = id;
			this.type = type;
			this.score = score;
		}
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			if(obj instanceof Element)
			{
				Element otherElement = (Element) obj;
				
				return this.id.equals(otherElement.id) && this.type== otherElement.type;
				
			}
			return super.equals(obj);
		}
		@Override
		public String toString() {
			return "Element [isHovered=" + isHovered + ", name=" + name
					+ ", id=" + id + ", type=" + type + ", score=" + score
					+ "]";
		}
	}
	private void printResult()
	{
		for(int i=1;i<countMatrix.length;i++)
		{
			for(int j=1;j<countMatrix[i].length;j++)
			{
				for(int k=0;k<countMatrix[i][j].length;k++)
				{
					if(i != EyeTrackerItem.TYPE_MOVIE_STAR_RATING && j!= EyeTrackerItem.TYPE_MOVIE_STAR_RATING
							&& !(countMatrix[i][j][k] == 0 && possibilityMatrix[i][j][k] == 0))
					{
						String msg= TYPE_NAME[i]+"\t"+TYPE_NAME[j]+"\t"+CONNECTION_NAME[k]+"\t"+countMatrix[i][j][k]+"\t"+this.possibilityMatrix[i][j][k];
						System.out.println(msg);
					}
					
				}
			}
			
			
		}
	}
	private void readTransProbDirectory(String filePath)
	{
		File dir = new File(filePath);
		File[] textFileList = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File childFile) {
				// TODO Auto-generated method stub
				return childFile.getName().toUpperCase().endsWith(".TXT");
			}
		});
		
		for(File childFile: textFileList)
		{
			readTransProbFile(childFile.getAbsolutePath());
		}
	}
	private void readTransProbFile(String filePath)
	{
		int dotIndex =filePath.lastIndexOf("."); 
		
		String taskString = filePath.substring(dotIndex-1, dotIndex);
		int currentTask =Integer.parseInt(taskString);
		 
		ArrayList<String> fileTextLines = IOUtil.readTextFile(filePath);
		ArrayList<Element> lastElementList = new ArrayList<TransitionProbabilityViewer.Element>();
		ArrayList<Element> currentElementList = new ArrayList<TransitionProbabilityViewer.Element>();
		String lastLine = "";
		int totalLines = fileTextLines.size();
		int index =0;
		int previousPercentile =-1;
		for(String fileLine: fileTextLines)
		{
			
			if(!fileLine.equals(lastLine))
			{
				
				String[] data = fileLine.split("\t");
				
				for(String element: data)
				{
					String[] elementData = element.split(",");
					if(elementData.length >=COLUMNS_PER_ELEMENT)
					{
						if(elementData[0].toUpperCase().equals(TAG_HOVER) && elementData.length >= COLUMNS_PER_ELEMENT)
						{
							int extraLength = elementData.length - COLUMNS_PER_ELEMENT;
							String name="";
							for(int i=0;i< extraLength+1;i++)
							{
								 name+= elementData[1+i]+", ";
							}
							name =name.substring(0, name.indexOf(", "));
							String id = elementData[2+extraLength];
							int type = Integer.parseInt(elementData[3+extraLength]);
							Element elem = new Element(true, name, id, type, 0);
							int elementIndex  = currentElementList.indexOf(elem);
							if(elementIndex >=0)
							{
								currentHover=currentElementList.get(elementIndex); 
								currentHover.isHovered = true;
							}
							else
							{
								currentHover = elem;
							}
						}
						else if(elementData.length >= COLUMNS_PER_ELEMENT)
						{
							int extraLength = elementData.length - COLUMNS_PER_ELEMENT;
							String name="";
							for(int i=0;i< extraLength+1;i++)
							{
								 name+= elementData[i]+", ";
							}
							name =name.substring(0, name.indexOf(", "));
							String id = elementData[1+extraLength];
							int type = Integer.parseInt(elementData[2+extraLength]);
							double score = Double.parseDouble(elementData[3+extraLength]);
							Element elem = new Element(false, name, id, type, score);
							currentElementList.add(elem);
						}
						
					}
					else if(elementData[0].toUpperCase().equals(TAG_VIEW) && elementData.length ==2)
					{
						String currentViewString = elementData[1];
						int elementIndex = this.elementNames.indexOf( new StatElement(currentViewString, currentTask));
						if(elementIndex>=0)
						{
							currentView = this.elementNames.get(elementIndex);
						}
					}
					
				}
				
				calculate(lastElementList, currentElementList);
				lastElementList = new ArrayList<TransitionProbabilityViewer.Element>(currentElementList);
				currentElementList.clear();
				lastLine = fileLine;
				
				//Track Progress
				index++;
				int percentile=(int)(100.0 *index/totalLines);
				if(percentile %10 == 0 && percentile != previousPercentile)
				{
					System.out.println("Done:"+percentile+"%");
				}
				previousPercentile = percentile;
				
			}
			
		}
		System.out.println("Loaded:"+filePath);
	}
	
	private void calculate(ArrayList<Element> lastElementList, ArrayList<Element> currentElementList )
	{
		if(!lastElementList.isEmpty() && !currentElementList.isEmpty())
		{
			
				Element sourceElement = lastElementList.get(0);
				for(Element item: lastElementList)
				{
					if(item.score > sourceElement.score)
					{
						sourceElement = item;
					}
				}
				
				Element destinationElement = currentElementList.get(0);
				
				for(Element item: currentElementList)
				{
					if(item.score > destinationElement.score)
					{
						destinationElement = item;
					}
				}
				if(sourceElement.type != EyeTrackerItem.TYPE_MOVIE_STAR_RATING && destinationElement.type != EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
				{
					boolean isConnected =isConnected(sourceElement, destinationElement); 
					boolean isSourceHovered =sourceElement.isHovered;
					boolean isDestinationHovered = destinationElement.isHovered;
					
					if(currentView!= null && currentHover != null)
					{
						isSourceHovered = isSourceHovered || isConnected(currentHover, sourceElement) ;
						isDestinationHovered = isDestinationHovered || isConnected(currentHover, destinationElement);
					}
					int connectionType = CONNECTION_NOT_CONNECTED_NO_HOVER;
					if(!isConnected && !isDestinationHovered)
					{
						connectionType = CONNECTION_NOT_CONNECTED_NO_HOVER;
					}
					else if(!isConnected && isDestinationHovered)
					{
						connectionType = CONNECTION_NOT_CONNECTED_DESTINATION_HOVER;
					}
					else if(isConnected &&! isDestinationHovered)
					{
						connectionType = CONNECTION_CONNECTED_NO_HOVER;
					}
					else if(isConnected && isDestinationHovered)
					{
						connectionType = CONNECTION_CONNECTED_DESTINATION_HOVER;
					}
					
					this.countMatrix[sourceElement.type][destinationElement.type][connectionType]++;
			
					populatePossibility(sourceElement);
				}
				
				
				
				
			
		}
	}

	private void populatePossibility(Element source)
	{
		
		ViewItem sourceItem = getViewItem(source, currentView);	

		
		if(currentView != null && currentView.getAdjacencyList().containsKey(sourceItem))
		{
			HashMap<ViewItem, ArrayList<ViewItem>> adjacencyList = currentView.getAdjacencyList();
			ViewItem hoverItem = getViewItem(currentHover, currentView);
			
			ArrayList<ViewItem> allItems = currentView.getItems();
			ArrayList<ViewItem> neighbors = adjacencyList.get(sourceItem);
			
			ArrayList<ViewItem> hoverItems = new ArrayList<ViewItem>();
			hoverItems.add(hoverItem);
			if(hoverItem != null && adjacencyList.containsKey(hoverItem))
			{
				ArrayList<ViewItem> hoveredNeighbors = adjacencyList.get(hoverItem);
				hoverItems.addAll(hoveredNeighbors);
			}
			
			
			for( int destinationType = EyeTrackerItem.TYPE_ACTOR;destinationType< TOTAL_TYPES;destinationType++)
			{
				
				for(ViewItem item: allItems)
				{
					if(item.getType() == destinationType)
					{
						if(!neighbors.contains(item) && !hoverItems.contains(item))
						{
							this.possibilityMatrix[source.type][destinationType][CONNECTION_NOT_CONNECTED_NO_HOVER]++;
						}
						else if(neighbors.contains(item) && !hoverItems.contains(item))
						{
							this.possibilityMatrix[source.type][destinationType][CONNECTION_CONNECTED_NO_HOVER]++;
						}
						else if(!neighbors.contains(item) && hoverItems.contains(item))
						{
							this.possibilityMatrix[source.type][destinationType][CONNECTION_NOT_CONNECTED_DESTINATION_HOVER]++;
						}
						else if(neighbors.contains(item) && hoverItems.contains(item))
						{
							this.possibilityMatrix[source.type][destinationType][CONNECTION_CONNECTED_DESTINATION_HOVER]++;
						}
					}
				}
				
				
			}
			
			
			
		}
		
	}
	private ViewItem getViewItem(Element elem, StatElement view)
	{
		if(elem.type == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return null;
		}
		ViewItem item = new ViewItem(Long.parseLong(elem.id), elem.type,elem.name);
		if(view != null)
		{
			int index = view.getItems().indexOf(item);
			if(index >=0)
			{
				item = view.getItems().get(index);
			}
		}
		return item;
	}
	private boolean isConnectedToMovie(CompactMovie compactMovie, Element dest)
	{
		Movie movie = this.data.getMovie(compactMovie);
		if(dest.type == EyeTrackerItem.TYPE_ACTOR)
		{
			CompactPerson actor = new CompactPerson(Long.parseLong(dest.id), dest.name, "m");
			if(movie.getActors().contains(actor))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(dest.type == EyeTrackerItem.TYPE_DIRECTOR)
		{
			CompactPerson director = new CompactPerson(Long.parseLong(dest.id), dest.name, "m");
			if(movie.getDirectors().contains(director))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(dest.type == EyeTrackerItem.TYPE_GENRE)
		{
			Genre genre = new Genre(Long.parseLong(dest.id), dest.name);
			if(movie.getGenreList().contains(genre))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(dest.type == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return false;
		}
		else
		{
			return false;
		}
	}
	private boolean isConnected(Element source, Element dest)
	{
		if(source.type == dest.type)
		{
			return false;
		}
		else if(source.type == EyeTrackerItem.TYPE_MOVIE_STAR_RATING || dest.type == EyeTrackerItem.TYPE_MOVIE_STAR_RATING)
		{
			return false;
		}
		else if(source.type == 0)
		{
			return false;
		}
		else if(source.type == EyeTrackerItem.TYPE_MOVIE)
		{
			CompactMovie compactMovie = new CompactMovie(Long.parseLong(source.id), source.name, 1900);
			if(currentView != null)
			{

				ViewItem sourceItem = getViewItem(source, currentView);
				ViewItem destItem =getViewItem(dest,currentView);
				
				if(this.currentView.getAdjacencyList().containsKey(sourceItem))
				{
					ArrayList<ViewItem> neighbors =  this.currentView.getAdjacencyList().get(sourceItem);
					boolean isConnected = neighbors.contains(destItem);
					
					return isConnected;
					
				}
				else
				{
					return isConnectedToMovie(compactMovie, dest);
				}
				
				
			}
			else
			{
				return isConnectedToMovie(compactMovie, dest);	
			}
			
		}
		else if(dest.type == EyeTrackerItem.TYPE_MOVIE)
		{
			return isConnected(dest, source);
		}
		else
		{
			return false;
		}
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
