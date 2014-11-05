package pivotpath;

import imdb.IMDBDataSource;
import imdb.analysis.HeatMapAnalysisViewer;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Genre;
import imdb.entity.Movie;
import imdb.entity.Person;
import imdb.entity.SearchItem;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import multifaceted.Util;
import perspectives.base.Property;
import perspectives.base.Task;
import perspectives.base.Viewer;
import perspectives.properties.PBoolean;
import perspectives.properties.PFileInput;
import perspectives.properties.POptions;
import perspectives.properties.PSignal;
import perspectives.properties.PString;
import perspectives.properties.PText;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.two_d.ViewerContainer2D;
import eyetrack.EyeTrackerItem;
import eyetrack.EyeTrackerLabelDetector;
import eyetrack.EyeTrackerViewer;
import eyetrack.probability.EyeTrackerProbabilityViewer;

public class PivotPathViewer extends Viewer implements JavaAwtRenderer, PivotPathViewerInterface , EyeTrackerProbabilityViewer{

	private static final String PROPERTY_SEARCH="Search";
	private static final String PROPERTY_SEARCH_PERSON="Search Person";
	private static final String PROPERTY_SEARCH_RESULT = "Search Result";
	private static final String PROPERTY_SELECTED_ITEM="Selected Item";
	private static final String PROPERTY_SELECT="Select";
	private static final String PROPERTY_RECENTLY_VIEWED="Recently Viewed";
	private static final String PROPERTY_BACK="Back";
	private static final String PROPERTY_STEP="Debug.Step";
	private static final String PROPERTY_PERFORMANCE="Debug.Check Performance";
	private static final String PROPERTY_SHOW_GAZE="Debug.Show Gaze";
	private static final String PROPERTY_MOUSE_GAZE="Debug.Mouse Gaze";
	private static final String PROPERTY_SAVE_VIEW="Debug.Save View";
	private static final String PROPERTY_RENDER_DEBUG="Debug.Render Debug";
	private static final String PROPERTY_PROBABILITY_ON="Probability";
	private static final String PROPERTY_REFRESH="Refresh";
	private static final String PROPERTY_END_STUDY = "End of Study";
	private static final String PROPERTY_SHOW_LIST_TYPE = "Show List";
	private static final String PROPERTY_LOAD="Load";
	
	private static final String STR_ACTOR ="actor";
	private static final String STR_DIRECTOR ="director";
	private static final String STR_GENRE = "genre";
	
	private static final int TYPE_PERSON =0;
	private static final int TYPE_MOVIE =1;
	
	private static final int SELECT_FROM_SEARCH =0;
	private static final int SELECT_FROM_RECENTLY_VIEWED =1;
	
	private static final int SELECT_FROM_ACTED=0;
	private static final int SELECT_FROM_DIRECTED=1;
	private static final int MAX_MOVIE =15;
	private static final int MAX_ACTOR=5;
	private static final int MIN_ACTOR=5;
	
	private static final int MAX_SIMULATION =10;
	private static final int SIMULATION_SPEED =20;
	private static final int TIME_LAPSE =10;
	

	public static final int IMAGE_SAVE_OFFSET_X =100;
	public static final int IMAGE_SAVE_OFFSET_Y =300;
	public static final String IMAGE_RESULT_DIR="C:\\work\\";
	
	public static final long TIMER_PERIOD_GAZE=500;
	public static final long TIMER_PERIOD_MOUSE_POSITION=50;
	
	public static final String RESULT_ANCHOR_EYE_ELEMENT ="Eye";
	public static final String RESULT_ANCHOR_MOUSE_POSITION ="Mouse";
	public static final String RESULT_ANCHOR_MOUSE_CLICK ="Click";
	public static final String RESULT_ANCHOR_ZOOM ="ZOOM";
	public static final String RESULT_GAZE_POSITION ="Gaze";
	public static final String RESULT_Hover ="Hover";
	
	private static final double ZOOM_THRESHOLD =0.6;
	
	private IMDBDataSource data;
	
	private ArrayList<SearchItem> recentlyViewed = null;
	private ArrayList<Long> recentlyViewedId = null;
	
	private ArrayList<SearchItem> searchResult = null;
	
	private EyeTrackerLabelDetector et = null;
	
	private boolean isLocked = false;
	
	private int selectFrom = SELECT_FROM_SEARCH;
	
	private int movieListSelectFrom = SELECT_FROM_ACTED;
	
	private StringBuffer resultText;
	
	private Timer timer =null;
	
	private Point mousePosition =null;
	
	private SearchItem currentItem;
	
	private GeneralPivotPaths pivotPaths;
	public PivotPathViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;		
		this.recentlyViewed = new ArrayList<SearchItem>();
		this.recentlyViewedId  = new ArrayList<Long>();
		et = new EyeTrackerLabelDetector(this);
		this.resultText = new StringBuffer();
		this.timer = new Timer("EyeTrack Data Collection Timer");
		this.mousePosition = new Point(0,0);
		
		this.pivotPaths = new MoviePivotPaths("Movies", new Rectangle2D.Double(0,0,1000,300))
		{
			@Override
			public void transition(String facet, String value, String id)
			{
				if(!facet.trim().equals("") && !facet.equals(STR_GENRE))
				{
					selectItem(id, value);
				}
			}
		};
		pivotPaths.addSlot(new Point2D[]{new Point2D.Double(0, -300),  new Point2D.Double(0,50), new Point2D.Double(1000,50), new Point2D.Double(1000,-300), new Point2D.Double(0,-300)}, new Color(200,200,0,100));
		pivotPaths.addSlot(new Point2D[]{new Point2D.Double(0, 400),  new Point2D.Double(0,700), new Point2D.Double(1000,700), new Point2D.Double(1000,400), new Point2D.Double(0,400)}, new Color(200,200,0,100));
		
		pivotPaths.viewer = this;
		pivotPaths.pivotPathViewer = this;
		
		try
		{	
			Property<PString> pSearch = new Property<PString>(PROPERTY_SEARCH, new PString(""))
					{
							@Override
							protected boolean updating(PString newvalue) {
								// TODO Auto-generated method stub
								final PString fNewValue=newvalue;
								Task t = new Task("Searching ... ") {
									
									@Override
									public void task() {
										// TODO Auto-generated method stub
										String searchKey = fNewValue.stringValue();
										searchTask(searchKey);
										done();
									}
								};
								t.blocking =true;
								t.indeterminate = true;
								t.start();
								
								return super.updating(newvalue);
							}
					};
			addProperty(pSearch);
			
			Property<PBoolean> pSearchActor = new Property<PBoolean>(PROPERTY_SEARCH_PERSON, new PBoolean(true));
			addProperty(pSearchActor);
			
			Property<PSignal> pSelect = new Property<PSignal>(PROPERTY_SELECT, new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							Task t = new Task("Loading ...") {
								
								@Override
								public void task() {
									// TODO Auto-generated method stub
									if(selectFrom == SELECT_FROM_SEARCH)
									{
										int selectedIndex = ((POptions)getProperty(PROPERTY_SEARCH_RESULT).getValue()).selectedIndex;
										if(searchResult != null && selectedIndex < searchResult.size())
										{
											SearchItem item = searchResult.get(selectedIndex);
											select(item);
										}
									}
									else if(selectFrom == SELECT_FROM_RECENTLY_VIEWED)
									{
										int selectedIndex = ((POptions)getProperty(PROPERTY_RECENTLY_VIEWED).getValue()).selectedIndex;
										if(recentlyViewed != null && selectedIndex < recentlyViewed.size())
										{
											int index = recentlyViewed.size()-selectedIndex-1;
											SearchItem item = recentlyViewed.get(index);
											select(item);
										}
									}
									
									done();
								}
							};
							t.blocking = false;
							t.indeterminate = true;
							t.start();
							return super.updating(newvalue);
						}
					};
			addProperty(pSelect);
			
			Property<PSignal> pBack = new Property<PSignal>(PROPERTY_BACK, new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							Task t = new Task("Loading ...") {
								
								@Override
								public void task() {
									// TODO Auto-generated method stub
									Property pRecentlyViewed =getProperty(PROPERTY_RECENTLY_VIEWED);
									if(pRecentlyViewed != null && recentlyViewed != null && recentlyViewed.size() > 0)
									{
										int selectedIndex = ((POptions)getProperty(PROPERTY_RECENTLY_VIEWED).getValue()).selectedIndex+1;
										int index = recentlyViewed.size()-selectedIndex-1;
										if(index >=0 && index < recentlyViewed.size())
										{	
											SearchItem item = recentlyViewed.get(index);
											select(item);
										}
									
									}
									
									
									
									done();
								}
							};
							t.blocking = false;
							t.indeterminate = true;
							t.start();
							return super.updating(newvalue);
						}
					};
			addProperty(pBack);
			
			Property<PSignal> pStep = new Property<PSignal>(PROPERTY_STEP, new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							simulate();
							return super.updating(newvalue);
						}
					};
			addProperty(pStep);
			Property<PString> pPerformance = new Property<PString>(PROPERTY_PERFORMANCE, new PString(""))
					{
						@Override
						protected boolean updating(PString newvalue) {
							// TODO Auto-generated method stub
							long[] ids = new long[]{437747,700661,369071,1304615,95992};
							
							String msg="\r\n";
							for(int i =0; i< ids.length;i++)
							{
								CompactPerson person = new CompactPerson(ids[i],"Rajin","m");
								long time = System.currentTimeMillis();
								selectPerson(person);
								time = System.currentTimeMillis() - time;
								msg+=ids[i]+": "+time+"\r\n";								
							}
							System.out.println("Performance:"+msg);
							return super.updating(newvalue);
						}
					};
			addProperty(pPerformance);
			
			Property<PBoolean> pShowGaze = new Property<PBoolean>(PROPERTY_SHOW_GAZE,new PBoolean(false));
			addProperty(pShowGaze);
			
			Property<PBoolean> pMouseGaze = new Property<PBoolean>(PROPERTY_MOUSE_GAZE,new PBoolean(false));
			addProperty(pMouseGaze);
			Property<PSignal> pSaveView = new Property<PSignal>(PROPERTY_SAVE_VIEW, new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							saveView();
							return super.updating(newvalue);
						}
					};
			addProperty(pSaveView);
			
			Property<PBoolean> pRenderDebug = new Property<PBoolean>(PROPERTY_RENDER_DEBUG,new PBoolean(false));
			addProperty(pRenderDebug);
			
			Property<PBoolean> pProbabilityOn = new Property<PBoolean>(PROPERTY_PROBABILITY_ON,new PBoolean(true));
			addProperty(pProbabilityOn);
			
			Property<PSignal> pEndOfStudy = new Property<PSignal>(PROPERTY_END_STUDY, new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							saveResult();
							JOptionPane.showMessageDialog(null, "End of Study");
							return super.updating(newvalue);
						}
					};
			addProperty(pEndOfStudy);
			
			POptions options = getMovieSelectionShowList(movieListSelectFrom);
			Property<POptions> pShowList = new Property<POptions>(PROPERTY_SHOW_LIST_TYPE, options)
					{
						@Override
						protected boolean updating(POptions newvalue) {
							// TODO Auto-generated method stub
							int selectionIndex = newvalue.selectedIndex;
							if(selectionIndex != movieListSelectFrom)
							{
								movieListSelectFrom= 1- movieListSelectFrom;
								select(currentItem);
							}
							return super.updating(newvalue);
						}
					};
			addProperty(pShowList);
			
			Property<PSignal> pRefresh = new Property<PSignal>(PROPERTY_REFRESH, new PSignal())
					{
						@Override
						protected boolean updating(PSignal newvalue) {
							// TODO Auto-generated method stub
							if(currentItem != null)
							{
								select(currentItem);
							}
							return super.updating(newvalue);
						}
					};
			addProperty(pRefresh);
			
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, new PFileInput())
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							loadFile(newvalue.path);
							return super.updating(newvalue);
						}
					};
			addProperty(pLoad);
			startTimer();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private POptions getMovieSelectionShowList(int selectionIndex)
	{
		POptions options= new POptions(new String[]{"Acted Movie", "Directed Movie"});
		options.selectedIndex  = selectionIndex;
		
		return options;
	}
	public Point getEyeTrackOffset()
	{
		Point p = new Point(0,0);
		if (this.getContainer() != null && this.getContainer().getViewerWindow() != null)
			p = this.getContainer().getViewerWindow().getDrawArea().getLocationOnScreen();
		
		
		return p;
	}
	private ArrayList<SearchItem> getSearchResult(String searchKey)
	{
		ArrayList<SearchItem> search=null;
		if(isSearchActorOn())
		{
			ArrayList<CompactPerson> personList = this.data.searchPerson(searchKey);
			search = new ArrayList<SearchItem>(personList);
		}
		else
		{
			ArrayList<CompactMovie> movieList = this.data.searchMovie(searchKey);
			search = new ArrayList<SearchItem>(movieList);
		}
		return  search;
	}
	private void searchTask(String searchKey)
	{
		System.out.println("Search");
		searchResult = getSearchResult(searchKey);
		if(searchResult != null && !searchResult.isEmpty())
		{
			String[] resultList = new String[searchResult.size()];
			for(int i=0;i<searchResult.size();i++)
			{
				resultList[i]= searchResult.get(i).toString();	
				System.out.println(resultList[i]+" "+searchResult.get(i).getId());
			}
			removeProperty(PROPERTY_SEARCH_RESULT);
			Property<POptions> pResult = new Property<POptions>(PROPERTY_SEARCH_RESULT, new POptions(resultList))
					{
						@Override
						protected boolean updating(POptions newvalue) {
							// TODO Auto-generated method stub
							resultListUpdated(newvalue);
							return super.updating(newvalue);
						}
					};
			addProperty(pResult);
		}
		
		
	}
	private void resultListUpdated(POptions newvalue)
	{
		if(searchResult != null && !searchResult.isEmpty())
		{
			String item = searchResult.get(newvalue.selectedIndex).toString();
			updateSelectedItem(item, SELECT_FROM_SEARCH);
		}
		
	}
	
	private ArrayList<CompactMovie> getMovieList(Person person)
	{
		ArrayList<CompactMovie> movieList = null;
		if(movieListSelectFrom == SELECT_FROM_ACTED && !person.getActedMovieList().isEmpty())
		{
			movieList = person.getActedMovieList();
		}
		else if(movieListSelectFrom == SELECT_FROM_DIRECTED && !person.getDirectedMovieList().isEmpty())
		{
			movieList = person.getDirectedMovieList();
		}
		else
		{
			if(person.getActedMovieList().size() > person.getDirectedMovieList().size())
			{
				movieList = person.getActedMovieList();
				movieListSelectFrom = SELECT_FROM_ACTED;				
			}
			else
			{
				movieList = person.getDirectedMovieList();
				movieListSelectFrom = SELECT_FROM_DIRECTED;
				
			}
			POptions options = getMovieSelectionShowList(movieListSelectFrom);
			getProperty(PROPERTY_SHOW_LIST_TYPE).setValue(options);
			
		}
		
		return movieList;
	}
	private void select(SearchItem item )
	{
		et.block(true);
		if(item instanceof CompactPerson)
		{
			selectPerson((CompactPerson)item);
		}
		else if(item instanceof CompactMovie)
		{
			selectMovie((CompactMovie) item);
		}
		et.block(false);
	}
	
	private void selectPerson(CompactPerson compactPerson)
	{	synchronized(this)
		{
			isLocked = true;
		}
		
		long time = System.currentTimeMillis();

		preSelectionTask();
		
		System.out.println("Selected:"+compactPerson);
		
		Person person = this.data.getPerson(compactPerson);
		
		updateSelectedItem(person.getName()+"("+(person.getGender().equals("m")?"Male":"Female")+")"+"\r\n"
						+(person.getBiographyList().size()>0?person.getBiographyList().get(0):""), SELECT_FROM_SEARCH);
		int movieCount =0;
		ArrayList<CompactMovie> movieList = getMovieList(person);

		PivotPathData pivotPathData = new PivotPathData();
		for(int i=0;i< movieList.size() && movieCount< MAX_MOVIE;i++)
		{
			
			CompactMovie compactMovie = movieList.get(i);
			
			System.out.println(compactMovie+" "+compactMovie.getId());
			Movie movie = this.data.getMovie(compactMovie);
			if(movie.getActors().size()< MIN_ACTOR)
			{	
				continue;
			}
			else
			{
				movieCount++;		
			}
			int dataIndex = pivotPathData.addData(movie.getTitle()+"\t"+movie.getRating()+"\t"+movie.getId());
			
		
			ArrayList<CompactPerson> directorList = movie.getDirectors();
			ArrayList<CompactPerson> actorList = movie.getActors();
			ArrayList<Genre> genreList = movie.getGenreList();
			
			
			
			for(CompactPerson director: directorList)
			{
				pivotPathData.addAttribute(dataIndex, new String[]{STR_DIRECTOR, director.getName(),""+director.getId()});				
			}
			int count =0;
			for(CompactPerson actor: actorList)
			{
				pivotPathData.addAttribute(dataIndex, new String[]{STR_ACTOR, actor.getName(),""+actor.getId()});
				
				count++;
				if(count >=MAX_ACTOR)
				{
					break;
				}
			}
			for(Genre genre : genreList)
			{
				pivotPathData.addAttribute(dataIndex,  new String[]{STR_GENRE, genre.getGenreName(),""+genre.getId()});				
			}
		}
		pivotPaths.setFacetToSlotMapping(new String[]{STR_ACTOR,STR_DIRECTOR,STR_GENRE}, new int[]{0,1,1});
		pivotPaths.setData(pivotPathData.getData(), pivotPathData.getAttribute());
		addRecentlyViewed(person);
		
		currentItem = person;
		
		postSelectionTask();
		
		time = System.currentTimeMillis() - time;
		System.out.println("Total time:"+time);
		
		synchronized(this)
		{
			isLocked = false;
		}
		
	}
	
	private void registerEyetrackPoints()
	{
		ArrayList<EyeTrackerItem> elements = new ArrayList<EyeTrackerItem>();
		for(InfoBitGroup group: this.pivotPaths.groups)
		{
			elements.addAll(group.getItems());
		}
		
		for(InfoBitGroup group: this.pivotPaths.dataGroups)
		{
			elements.addAll(group.getItems());
		}
		et.registerElements(elements);
	}
	private void selectMovie(CompactMovie compactMovie)
	{
		synchronized(this)
		{
			isLocked = true;
		}
		
		long time = System.currentTimeMillis();

		preSelectionTask();
		
		System.out.println("Selected:"+compactMovie);
		
		PivotPathData pivotPathData = new PivotPathData();
		Movie movie = this.data.getMovie(compactMovie);
		
		int dataIndex = pivotPathData.addData(movie.getTitle()+"\t"+(int)movie.getRating()+"\t"+movie.getId());
		
		ArrayList<CompactPerson> directorList = movie.getDirectors();
		ArrayList<CompactPerson> actorList = movie.getActors();
		ArrayList<Genre> genreList = movie.getGenreList();
		
		
	
		for(CompactPerson director: directorList)
		{
			pivotPathData.addAttribute(dataIndex, new String[]{STR_DIRECTOR, director.getName(),""+director.getId()});				
		}
		int count =0;
		for(CompactPerson actor: actorList)
		{
			pivotPathData.addAttribute(dataIndex, new String[]{STR_ACTOR, actor.getName(),""+actor.getId()});
			
			count++;
			if(count >=MAX_ACTOR)
			{
				break;
			}
		}
		for(Genre genre : genreList)
		{
			pivotPathData.addAttribute(dataIndex,  new String[]{STR_GENRE, genre.getGenreName(),""+genre.getId()});				
		}

		pivotPaths.setFacetToSlotMapping(new String[]{STR_ACTOR,STR_DIRECTOR,STR_GENRE}, new int[]{0,1,1});
		pivotPaths.setData(pivotPathData.getData(), pivotPathData.getAttribute());
		addRecentlyViewed(compactMovie);
		
		currentItem = compactMovie;
		
		postSelectionTask();
		
		time = System.currentTimeMillis() - time;
		System.out.println("Total time:"+time);
		
		synchronized(this)
		{
			isLocked = false;
		}
		
	}
	private void loadFile(String filePath)
	{
		try {
			File file = new File(filePath);
			FileReader fStream;
			fStream = new FileReader(file);		
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String line = bufferedReader.readLine();
			while(line != null)
			{
				String[] split = line.split("\t");
				if(split.length==3)
				{
					long id = Long.parseLong(split[0]);
					int type = Integer.parseInt(split[1]);
					String name = split[2];
					
					if(type == TYPE_MOVIE)
					{
						CompactMovie movie = new CompactMovie(id, name, 0);
						addRecentlyViewed(movie);
					}
					else if(type == TYPE_PERSON)
					{
						CompactPerson person = new CompactPerson(id, name ,"");
						addRecentlyViewed(person);
					}
				}
				 
				line = bufferedReader.readLine();
			}
			
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
	private void addRecentlyViewed(SearchItem item)
	{
		if(recentlyViewedId.contains(item.getId()))
		{
			int index = recentlyViewedId.indexOf(item.getId());
			recentlyViewedId.remove(item.getId());
			recentlyViewed.remove(index);
		}
		this.recentlyViewed.add(item);
		this.recentlyViewedId.add(item.getId());
		updateRecentlyViewed();
	}
			
	private void updateRecentlyViewed()
	{
		if(this.recentlyViewed.size() > 0)
		{
			removeProperty(PROPERTY_RECENTLY_VIEWED);
			int total = this.recentlyViewed.size();
			String[] searchList = new String[total];
			
			for(int i=0;i<total;i++)
			{
				searchList[i] = this.recentlyViewed.get(total-i-1).getDisplayText().trim();
			}
			POptions options = new POptions(searchList);
			Property<POptions> pRecentlyViewed = new Property<POptions>(PROPERTY_RECENTLY_VIEWED, options)
					{	
						@Override
						protected boolean updating(POptions newvalue) {
							String item = recentlyViewed.get(recentlyViewed.size()- newvalue.selectedIndex-1).getDisplayText();
							updateSelectedItem(item, SELECT_FROM_RECENTLY_VIEWED);
							return super.updating(newvalue);
						}
					};
			addProperty(pRecentlyViewed);
		}
		
	}
	private void updateSelectedItem(String item, int mode)
	{
		removeProperty(PROPERTY_SELECTED_ITEM);
		Property<PText> pSelected = new Property<PText>(PROPERTY_SELECTED_ITEM,new PText(item));
		pSelected.setDisabled(true);
		addProperty(pSelected);
		
		selectFrom = mode;
	}
	
	private int gazeX;
	private int gazeY;
	@Override
	public void gazeDetected(int x, int y) {
		this.gazeX = x;
		this.gazeY = y;
		if(isShowGazeOn())
		{
			this.gazeX = x;
			this.gazeY = y;
			
			this.requestRender();
		}
//		this.saveScoreInfo();
	}
	@Override
	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		return ((ViewerContainer2D)this.getContainer()).transform;
	}
	@Override
	public double getZoom() {
		// TODO Auto-generated method stub
		if(this.getContainer() != null)
		{
			return ((ViewerContainer2D)this.getContainer()).getZoom();
		}
		else
		{
			return 1.0;
		}
		
	}
	private boolean isShowGazeOn()
	{
		PBoolean showGaze = (PBoolean)this.getProperty(PROPERTY_SHOW_GAZE).getValue();
		return showGaze.boolValue();
	}
	private boolean isMouseGazeOn()
	{
		PBoolean mouseGaze = (PBoolean)this.getProperty(PROPERTY_MOUSE_GAZE).getValue();
		return mouseGaze.boolValue();
	}
	private boolean isRenderDebugOn()
	{
		PBoolean renderDebug = (PBoolean)this.getProperty(PROPERTY_RENDER_DEBUG).getValue();
		return renderDebug.boolValue();
	}
	private boolean isSearchActorOn()
	{
		PBoolean searchActor = (PBoolean)this.getProperty(PROPERTY_SEARCH_PERSON).getValue();
		return searchActor.boolValue();
	}
	@Override
	public boolean isProbabilityOn() {
		// TODO Auto-generated method stub
		PBoolean probabilityOn = (PBoolean)this.getProperty(PROPERTY_PROBABILITY_ON).getValue();
		return probabilityOn.boolValue();
	}
	public void render(Graphics2D g) {
		synchronized(this)
		{
			if (isLocked) return;
		}
		et.block(true);
		
		if(!pivotPaths.data.isEmpty())
		{
			pivotPaths.render(g);
		}
		
		if(isShowGazeOn())
		{
			drawEyeGaze(g);
		}
		if(isRenderDebugOn())
		{
			pivotPaths.renderDebug(g);
		}
		
		et.block(false);
	}
	
	private void drawEyeGaze(Graphics2D g)
	{
		double zoom = this.getZoom();
		int et =(int)( EyeTrackerLabelDetector.EDGETHRESHOLD/ zoom);
		g.setColor(new Color(255,0,0,120));
		g.fillOval(this.gazeX-5, this.gazeY-5, 10, 10);
		g.drawOval(this.gazeX-et, this.gazeY-et, et*2, et*2);
	}

	private int simulationCount=0;
	@Override
	public void simulate() {

		System.out.println("Simulate");
		
		
		for (int i=0; i<1; i++)
		pivotPaths.computeIteration();
		
		this.requestRender();
		saveView();
		super.simulate();
//		for (int i=0; i<SIMULATION_SPEED; i++)
//		{
//			layout.iteration();
//		}
//			
//		simulationCount++;
//		if(simulationCount > MAX_SIMULATION)
//		{
//			stopSimulation();
//			simulationCount=0;
//			saveView();
//		}
//		
//		
//		this.requestRender();
//		
	}
	public Color getBackgroundColor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void keyPressed(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	private void startTimer()
	{
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				saveScoreInfo();
			}
		}
		, 0, TIMER_PERIOD_GAZE);
		
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				addResultData(mousePosition);
			}
		}
		, 0, TIMER_PERIOD_MOUSE_POSITION);
	}
	
	private void stopTimer()
	{
		this.timer.cancel();
	}

	private void setPreviousColors()
	{
		for(InfoBitGroup group: this.pivotPaths.groups)
		{
			for(InfoBit bit: group.getItems())
			{
				if(bit instanceof LabelInfoBit)
				{
					((LabelInfoBit)bit).color = Util.getColor(bit.getType());
				}
			}
		}
		
		for(InfoBitGroup group: this.pivotPaths.dataGroups)
		{
			for(InfoBit bit: group.getItems())
			{
				((LabelInfoBit)bit).color = Util.getColor(bit.getType());
			}
		}
	}
	private void saveScoreInfo()
	{		
		synchronized(this)
		{
			if (isLocked) return;
		}
		et.block(true);
		if(this.et.getTopElements() != null )
		{
			if(isShowGazeOn())
			{
				this.setPreviousColors();
			}
			
			ArrayList<EyeTrackerItem> topelements = et.getTopElements();
			int elementsSize = topelements.size();
			for(int i=0;i<elementsSize;i++)
			{
				EyeTrackerItem element = this.et.getTopElements().get(i);
				if(element instanceof LabelInfoBit)
				{
					LabelInfoBit labelInfoBit = (LabelInfoBit) element;
					
					if(isShowGazeOn())
					{
						if(element.getScore() <= 1)
						{
							Color c = new Color(255, 255 - (int)(element.getScore()*255), 100);
							labelInfoBit.color = c;
						}
						
					}
					
					
					addResultData(labelInfoBit, labelInfoBit.getScore());
				}
				
			}
		}
		et.block(false);
		
	}
	private void addResultData(LabelInfoBit element, double score)
	{
		
		long time = System.currentTimeMillis();
		String id = element.getId();
		String name = element.label;		
		String data = RESULT_ANCHOR_EYE_ELEMENT+"\t"+time+"\t"+id+"\t"+name+"\t"+element.getType()+"\t"+String.format("%.2f",score)
				+"\t"+(int)(element.group.getItemX(element)+IMAGE_SAVE_OFFSET_X)+"\t"+(int)(element.group.getItemY(element)+IMAGE_SAVE_OFFSET_Y)
				+"\t"+currentImageFileName;
		synchronized (this) {
			this.resultText.append(data+"\r\n");
		}
		
//		System.out.println("##"+data);
	}
	private void addResultData(Point mousePosition)
	{
		long time = System.currentTimeMillis();
		int rad =(int)( EyeTrackerLabelDetector.EDGETHRESHOLD/ this.getZoom());
		String data = RESULT_ANCHOR_MOUSE_POSITION+"\t"+time+"\t"+(mousePosition.x+IMAGE_SAVE_OFFSET_X)+"\t"+(mousePosition.y+IMAGE_SAVE_OFFSET_Y)
				+"\t"+currentImageFileName+"\r\n"
				+RESULT_GAZE_POSITION+"\t"+time+"\t"+(gazeX+IMAGE_SAVE_OFFSET_X)+"\t"+(gazeY+IMAGE_SAVE_OFFSET_Y)+"\t"+rad
				+"\t"+currentImageFileName+"\r\n"
				+RESULT_ANCHOR_ZOOM+"\t"+time+"\t"+this.getZoom()
				+"\t"+currentImageFileName;
		
		//Hovering info
		if(this.hoverType>= 0 && this.hoverGroupIndex >= 0 && this.hoverElementIndex >= 0)
		{
			InfoBit element= null;
			if(this.hoverType == PivotPathViewerInterface.GROUP_DATA)
			{
				element = this.pivotPaths.dataGroups.get(hoverGroupIndex).getItems().get(hoverElementIndex);
			}
			else if(this.hoverType == PivotPathViewerInterface.GROUP_ATTRIBUTE)
			{
				element = this.pivotPaths.groups.get(hoverGroupIndex).getItems().get(hoverElementIndex);
			}
			if(element != null && element instanceof LabelInfoBit)
			{
				data+=	"\r\n"
						+RESULT_Hover+"\t"+time+"\t"+element.getId()+"\t"+((LabelInfoBit)element).label+"\t"+element.getType()+"\t"+String.format("%.2f",element.getScore())
						+"\t"+(int)(element.group.getItemX(element)+IMAGE_SAVE_OFFSET_X)+"\t"+(int)(element.group.getItemY(element)+IMAGE_SAVE_OFFSET_Y)
						+"\t"+currentImageFileName;
			}
			
		}
		synchronized (this) {
			this.resultText.append(data+"\r\n");
		}
//		System.out.println("##"+data);
	}
	private void addResultDataMouseClick(int x, int y, int button)
	{
		long time = System.currentTimeMillis();
		String data = RESULT_ANCHOR_MOUSE_CLICK+"\t"+(x+IMAGE_SAVE_OFFSET_X)+"\t"+(y+IMAGE_SAVE_OFFSET_Y)+"\t"+button
				+"\t"+currentImageFileName;
		synchronized (this) {
			this.resultText.append(data+"\r\n");
		}
	}
	public boolean mousepressed(int x, int y, int button) {
		this.addResultDataMouseClick(x, y, button);
		mousePosition.x = x;
		mousePosition.y = y;
		if (button == 1)
		{
			((ViewerContainer2D)this.getContainer()).rightButtonDown = false;
			if(isMouseGazeOn())
			{
				et.processScreenPoint(new Point(x,y));	
			}
			

			return true;
		}
		else if(button ==3)
		{
			this.pivotPaths.mouseClicked(x, y);
		}
		return false;
	}


	public boolean mousereleased(int x, int y, int button) {
		mousePosition.x = x;
		mousePosition.y = y;
		
		return false;
	}


	public boolean mousemoved(int x, int y) {
		mousePosition.x = x;
		mousePosition.y = y;
		if(this.pivotPaths.groups != null)
		{
			this.pivotPaths.mouseMoved(x,y);
		}
		
		return false;
		
	}


	public boolean mousedragged(int x, int y, int oldx, int oldy) {
		mousePosition.x = x;
		mousePosition.y = y;
		ViewerContainer2D container = (ViewerContainer2D)this.getContainer();
		double zoom = container.getZoom();
		if(container.rightButtonDown && zoom < ZOOM_THRESHOLD)
		{
			container.setZoom(ZOOM_THRESHOLD);
			return true;
		}
		return false;
	}



	public void callRequestRender() {
		this.requestRender();		
	}


	@Override
	public void selectItem(String id, String name) {
		// TODO Auto-generated method stub
		et.block(true);
		System.out.println(id+" "+name);
		CompactPerson person = new CompactPerson(Long.parseLong(id), name, "");
		int val = JOptionPane.showConfirmDialog(null, "Are you sure to select "+name+"?", "Confirmation?", JOptionPane.YES_NO_OPTION);
		if(val == JOptionPane.YES_OPTION)
		{
			selectPerson(person);
		}
		et.block(false);
		
	}

	@Override
	public void callSetToolTipText(String text) {
		// TODO Auto-generated method stub
		this.setToolTipText(text);
	}
	@Override
	public void callSaveView() {
		// TODO Auto-generated method stub
		saveView();
	}

	
	private String currentImageFileName="";
	private String currentResultFileName ="";
	private void saveView()
	{	
		this.timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				BufferedImage bim = new BufferedImage(1200,1200, BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D g = bim.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.translate(IMAGE_SAVE_OFFSET_X,IMAGE_SAVE_OFFSET_Y);
				render(g);
				
				
				String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+currentItem.getDisplayText().replace(" ", "_")+ ".PNG";
				
				try {
					ImageIO.write(bim, "PNG", new File(IMAGE_RESULT_DIR + filename ));
					currentImageFileName = filename;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0);
	
	}
	private void preSelectionTask()
	{
//		this.stopSimulation();

		saveResult();
		currentImageFileName ="";
		
	}
	
	private void postSelectionTask()
	{
		currentImageFileName ="";
		registerEyetrackPoints();
		saveView();
		this.requestRender();
//		this.startSimulation(TIME_LAPSE);
	}
	private void saveResult()
	{
		if(!resultText.toString().isEmpty())
		{
			this.timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if(currentResultFileName.isEmpty())
						{
							currentResultFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+"_RESULT.txt";
						}

						FileWriter fstream = new FileWriter(new File(IMAGE_RESULT_DIR+currentResultFileName), true);
						BufferedWriter br = new BufferedWriter(fstream);

						br.write(resultText.toString());

						br.close();
						synchronized (this) {
							resultText.setLength(0);
						}
						
						System.out.println("File saved:"+currentResultFileName);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, 0);
		}
		
	}

	private int hoverType =-1;
	private int hoverGroupIndex=-1;
	private int hoverElementIndex =-1;
	@Override
	public void hoverDetected(int type,int groupIndex, int elementIndex) {
		// TODO Auto-generated method stub
		this.hoverType = type;
		this.hoverGroupIndex = groupIndex;
		this.hoverElementIndex = elementIndex;
	}

}
