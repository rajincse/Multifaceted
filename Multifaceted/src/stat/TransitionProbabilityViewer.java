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
import java.util.ArrayList;

import eyetrack.EyeTrackerItem;

import multifaceted.IOUtil;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class TransitionProbabilityViewer extends Viewer implements JavaAwtRenderer {

	public static final String PROPERTY_LOAD = "Load";
	
	public static final int OFFSET_NOT_CONNECTED =0;
	public static final int OFFSET_CONNECTED =1;
	public static final int OFFSET_CONNECTED_HOVER =2;
	public static final int TOTAL_OFFSETS =3;
	
	public static final String[] TYPE_NAME =new String[]{"INIT", "ACTOR", "MOVIE", "DIRECTOR", "GENRE", "STAR"};
	public static final String[] OFFSET_NAME =new String[]{"NOT CONNECTED", "CONNECTED", "HOVER"};
	
	public static final int COLUMNS_PER_ELEMENT =4;
	
	private int [][] countMatrix = new int[6][6*TOTAL_OFFSETS];
	private IMDBDataSource data = null;
	
	public TransitionProbabilityViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
		
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
	}
	private void printResult()
	{
		for(int i=0;i<countMatrix.length;i++)
		{
			for(int j=0;j<countMatrix[i].length;j++)
			{
				if(countMatrix[i][j] >0)
				{
					String msg= TYPE_NAME[i]+"\t"+TYPE_NAME[j/ TOTAL_OFFSETS]+"\t"+OFFSET_NAME[j%TOTAL_OFFSETS]+"\t"+countMatrix[i][j];
					System.out.println(msg);
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
						if(elementData[0].toUpperCase().equals("HOV") && elementData.length == COLUMNS_PER_ELEMENT)
						{
							String name = elementData[1];
							String id = elementData[2];
							int type = Integer.parseInt(elementData[3]);
							Element elem = new Element(true, name, id, type, 0);
							currentElementList.add(elem);
						}
						else if(elementData[0].toUpperCase().equals("HOV") && elementData.length > COLUMNS_PER_ELEMENT)
						{
							int extraLength = elementData.length - COLUMNS_PER_ELEMENT;
							String name="";
							for(int i=0;i< extraLength;i++)
							{
								 name+= elementData[1+i]+", ";
							}
							name =name.substring(0, name.indexOf(", ")+1);
							String id = elementData[2+extraLength];
							int type = Integer.parseInt(elementData[3+extraLength]);
							Element elem = new Element(true, name, id, type, 0);
							currentElementList.add(elem);
						}
						else if(elementData.length > COLUMNS_PER_ELEMENT)
						{
							int extraLength = elementData.length - COLUMNS_PER_ELEMENT;
							String name="";
							for(int i=0;i< extraLength;i++)
							{
								 name+= elementData[i]+", ";
							}
							name =name.substring(0, name.indexOf(", ")+1);
							String id = elementData[1+extraLength];
							int type = Integer.parseInt(elementData[2+extraLength]);
							double score = Double.parseDouble(elementData[3+extraLength]);
							Element elem = new Element(false, name, id, type, score);
							currentElementList.add(elem);
						}
						else
						{
							String name = elementData[0];
							String id = elementData[1];
							int type = Integer.parseInt(elementData[2]);
							double score = Double.parseDouble(elementData[3]);
							Element elem = new Element(false, name, id, type, score);
							currentElementList.add(elem);
						}
					}
					calculate(lastElementList, currentElementList);
					lastElementList = new ArrayList<TransitionProbabilityViewer.Element>(currentElementList);
					currentElementList.clear();
					lastLine = fileLine;
				}
				
				
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
			boolean isSourceSameType = true;
			int sourceType=lastElementList.get(0).type;
			for(Element elem: lastElementList)
			{
				isSourceSameType =isSourceSameType && ( elem.type == sourceType);
			}
			
			if(!isSourceSameType)
			{
				System.out.println("Not same source type:"+lastElementList);
			}
			
			boolean isSameDestinationType = true;
			int destinationType=currentElementList.get(0).type;
			for(Element elem: currentElementList)
			{
				isSameDestinationType =isSameDestinationType && ( elem.type == destinationType);
			}
			
			if(!isSameDestinationType)
			{
				System.out.println("Not same destination type:"+currentElementList);
			}
			if(isSourceSameType && isSameDestinationType)
			{
				int offset = OFFSET_NOT_CONNECTED;
				for(Element source: lastElementList)
				{
					int newOffset =  OFFSET_NOT_CONNECTED;
					for(Element dest: currentElementList)
					{	
						boolean isConnected =isConnected(source, dest); 
						if(isConnected && (source.isHovered || dest.isHovered) && newOffset< OFFSET_CONNECTED_HOVER)
						{
							newOffset = OFFSET_CONNECTED_HOVER;
							break;
						}
						else if(isConnected && newOffset< OFFSET_CONNECTED)
						{
							newOffset = OFFSET_CONNECTED;
						}
					}
					if( newOffset> offset)
					{
						offset = newOffset;
					}
					
					if(offset == OFFSET_CONNECTED_HOVER)
					{
						break;
					}
				}
				countMatrix[sourceType][destinationType * TOTAL_OFFSETS+ offset]++;
			}
			
		}
	}

	private boolean isConnected(Element source, Element dest)
	{
		if(source.type == dest.type)
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
