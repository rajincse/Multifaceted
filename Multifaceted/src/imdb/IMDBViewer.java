package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import eyetrack.EyeTrackerPivotElementDetector;
import eyetrack.EyeTrackerViewer;

import multifaceted.layout.LayoutViewerInterface;
import multifaceted.layout.PivotElement;
import multifaceted.layout.PivotPathGroupLayout;
import multifaceted.layout.PivotPathLayout;

import perspectives.base.Property;
import perspectives.base.Task;
import perspectives.base.Viewer;
import perspectives.properties.PBoolean;
import perspectives.properties.POptions;
import perspectives.properties.PSignal;
import perspectives.properties.PString;
import perspectives.properties.PText;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.two_d.ViewerContainer2D;

public class IMDBViewer extends Viewer implements JavaAwtRenderer, LayoutViewerInterface , EyeTrackerViewer{

	private static final String PROPERTY_SEARCH_ACTOR="Search Actor";
	private static final String PROPERTY_SEARCH_RESULT = "Search Result";
	private static final String PROPERTY_SELECTED_ITEM="Selected Item";
	private static final String PROPERTY_SELECT="Select";
	private static final String PROPERTY_RECENTLY_VIEWED="Recently Viewed";
	private static final String PROPERTY_BACK="Back";
	private static final String PROPERTY_STEP="Debug.Step";
	private static final String PROPERTY_PERFORMANCE="Debug.Check Performance";
	private static final String PROPERTY_SHOW_GAZE="Debug.Show Gaze";
	
	private static final int SELECT_FROM_SEARCH =0;
	private static final int SELECT_FROM_RECENTLY_VIEWED =1;
	
	private static final int MAX_ACTOR=5;
	private static final int MIN_ACTOR=5;
	
	private static final int MAX_SIMULATION =50;
	private static final int SIMULATION_SPEED =10;
	
	private IMDBDataSource data;
	
	private ArrayList<CompactPerson> recentlyViewed = null;
	private ArrayList<Long> recentlyViewedId = null;
	private ArrayList<CompactPerson> personList = null;
	
	private PivotPathLayout layout =null;
	
	private EyeTrackerPivotElementDetector et = null;
	
	private boolean isLocked = false;
	
	private int selectFrom = SELECT_FROM_SEARCH;
	
	public IMDBViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
		this.layout = new PivotPathGroupLayout(this);
		this.recentlyViewed = new ArrayList<CompactPerson>();
		this.recentlyViewedId  = new ArrayList<Long>();
		et = new EyeTrackerPivotElementDetector(this);
		try
		{
			
			Property<PString> pSearch = new Property<PString>(PROPERTY_SEARCH_ACTOR, new PString(""))
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
										if(personList != null && selectedIndex < personList.size())
										{
											CompactPerson person = personList.get(selectedIndex);
											selectPerson(person);
										}
									}
									else if(selectFrom == SELECT_FROM_RECENTLY_VIEWED)
									{
										int selectedIndex = ((POptions)getProperty(PROPERTY_RECENTLY_VIEWED).getValue()).selectedIndex;
										if(recentlyViewed != null && selectedIndex < recentlyViewed.size())
										{
											int index = recentlyViewed.size()-selectedIndex-1;
											CompactPerson person = recentlyViewed.get(index);
											selectPerson(person);
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
											CompactPerson person = recentlyViewed.get(index);
											selectPerson(person);
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
			
			Property<PBoolean> pShowGaze = new Property<PBoolean>(PROPERTY_SHOW_GAZE,new PBoolean(true));
			addProperty(pShowGaze);
					
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public Point getEyeTrackOffset()
	{
		Point p = new Point(0,0);
		if (this.getContainer() != null && this.getContainer().getViewerWindow() != null)
			p = this.getContainer().getViewerWindow().getDrawArea().getLocationOnScreen();
		
		
		return p;
	}
	private void searchTask(String searchKey)
	{
		System.out.println("Search");
		
		personList = this.data.searchPerson(searchKey);
		if(personList!= null && !personList.isEmpty())
		{
			String[] resultList = new String[personList.size()];
			for(int i=0;i<personList.size();i++)
			{
				resultList[i]= personList.get(i).toString();	
				System.out.println(resultList[i]+" "+personList.get(i).getId());
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
		String item = personList.get(newvalue.selectedIndex).toString();
		updateSelectedItem(item, SELECT_FROM_SEARCH);
	}
	private void selectPerson(CompactPerson compactPerson)
	{	synchronized(this)
		{
			isLocked = true;
		}
		
		long time = System.currentTimeMillis();
		this.stopSimulation();
		this.layout.init();
		
		System.out.println("Selected:"+compactPerson);
		
		Person person = this.data.getPerson(compactPerson);
		
		updateSelectedItem(person.getDisplayName()+"("+(person.getGender().equals("m")?"Male":"Female")+")"+"\r\n"
						+(person.getBiographyList().size()>0?person.getBiographyList().get(0):""), SELECT_FROM_SEARCH);
		int movieCount =0;
		ArrayList<CompactMovie> movieList = person.getActedMovieList();
		if(person.getActedMovieList().size() < person.getDirectedMovieList().size())
		{
			movieList = person.getDirectedMovieList();
		}
		for(int i=0;i< movieList.size() && movieCount< PivotPathLayout.MAX_MIDDLE_ITEM;i++)
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
			MovieElement movieElement = new MovieElement(""+compactMovie.getId(), 
															compactMovie.getTitle(),
															movie.getRating()/2,
															movie.getYear());
			int source = layout.addMiddleElement(movieElement);
			
		
			ArrayList<CompactPerson> directorList = movie.getDirectors();
			for(CompactPerson director: directorList)
			{
				if(!layout.getElementIds().contains(""+director.getId()))
				{
					int destination =layout.addBottomElement(""+director.getId(),director.getDisplayName(),source );					
					layout.addEdge(source, destination);
				}
				else
				{
					int destination =layout.getElementIds().indexOf(""+director.getId());
					layout.addEdge(source, destination);
				}
				 
				
			}
			
			ArrayList<CompactPerson> actorList = movie.getActors();
			int actorCount=0;
			for(CompactPerson actor: actorList)
			{
				if(!layout.getElementIds().contains(""+actor.getId()) && actor.getId() != person.getId())
				{
					int destination =layout.addTopElement(""+actor.getId(),actor.getDisplayName(), source);					
					layout.addEdge(source, destination);
					actorCount++;
					
				}
				else if(actor.getId() != person.getId())
				{
					int destination = layout.getElementIds().indexOf(""+actor.getId());
					layout.addEdge(source, destination);
				}
				
				if(actorCount >=MAX_ACTOR)
				{
					break;
				}
			}
		}
		
		
		

		addRecentlyViewed(person);
		
		this.startSimulation(50);
		time = System.currentTimeMillis() - time;
		System.out.println("Total time:"+time);
		
		synchronized(this)
		{
			isLocked = false;
		}
		
		registerEyetrackPoints();
	}
	
	private void registerEyetrackPoints()
	{
		et.registerElements(layout.getElements());
	}
	private void selectMovie(CompactMovie movie)
	{
		
	}
	private void addRecentlyViewed(CompactPerson person)
	{
		if(recentlyViewedId.contains(person.getId()))
		{
			int index = recentlyViewedId.indexOf(person.getId());
			recentlyViewedId.remove(person.getId());
			recentlyViewed.remove(index);
		}
		this.recentlyViewed.add(person);
		this.recentlyViewedId.add(person.getId());
		updateRecentlyViewed();
	}
			
	private void updateRecentlyViewed()
	{
		if(this.recentlyViewed.size() > 0)
		{
			removeProperty(PROPERTY_RECENTLY_VIEWED);
			int total = this.recentlyViewed.size();
			String[] personList = new String[total];
			
			for(int i=0;i<total;i++)
			{
				personList[i] = this.recentlyViewed.get(total-i-1).getName();
			}
			POptions options = new POptions(personList);
			Property<POptions> pRecentlyViewed = new Property<POptions>(PROPERTY_RECENTLY_VIEWED, options)
					{	
						@Override
						protected boolean updating(POptions newvalue) {
							String item = recentlyViewed.get(recentlyViewed.size()- newvalue.selectedIndex-1).getName();
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
		if(isShowGazeOn())
		{
			this.gazeX = x;
			this.gazeY = y;
			
			this.requestRender();
			layoutScoreInfo();
		}
		
	}
	@Override
	public AffineTransform getTransform() {
		// TODO Auto-generated method stub
		return ((ViewerContainer2D)this.getContainer()).transform;
	}
	@Override
	public double getZoom() {
		// TODO Auto-generated method stub
		return ((ViewerContainer2D)this.getContainer()).getZoom();
	}
	private boolean isShowGazeOn()
	{
		PBoolean showGaze = (PBoolean)this.getProperty(PROPERTY_SHOW_GAZE).getValue();
		return showGaze.boolValue();
	}
	
	public void render(Graphics2D g) {
		synchronized(this)
		{
			if (isLocked) return;
		}
		et.block(true);
		layout.render(g);
		if(isShowGazeOn())
		{
			drawEyeGaze(g);
		}
		
		et.block(false);
	}
	
	private void drawEyeGaze(Graphics2D g)
	{
		double zoom = this.getZoom();
		int et =(int)( EyeTrackerPivotElementDetector.EDGETHRESHOLD/ zoom);
		g.setColor(new Color(255,0,0,120));
		g.fillOval(this.gazeX-5, this.gazeY-5, 10, 10);
		g.drawOval(this.gazeX-et, this.gazeY-et, et*2, et*2);
	}

	private int simulationCount=0;
	@Override
	public void simulate() {
		
		for (int i=0; i<SIMULATION_SPEED; i++)
		{
			layout.iteration();
		}
			
		simulationCount++;
		if(simulationCount > MAX_SIMULATION)
		{
			stopSimulation();
			simulationCount=0;
		}
		
		
		this.requestRender();
		
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

	private void layoutScoreInfo()
	{		
		double[] nodeScore = et.getNodeScore();
		double[] nodeScore2 = et.getNodeScore2();
		if(nodeScore != null && nodeScore2 != null)
		{
			for(int i=0;i<layout.getElements().size();i++)
			{
				PivotElement element = layout.getElements().get(i);
				if(nodeScore[i]> EyeTrackerPivotElementDetector.SELECTION_THRESHOLD)
				{
					//Selected
					Color c= new Color(221, 247,210, 200);
					element.getLabel().setColor(c);
					
				}
				else
				{
					Color c= new Color(237, 185,188, 150);
					element.getLabel().setColor(c);
				}
		
			}
		}
		
	}
	public boolean mousepressed(int x, int y, int button) {
		if (button == 1)
		{
			((ViewerContainer2D)this.getContainer()).rightButtonDown = false;
			
			et.processScreenPoint(new Point(x,y));

			return true;
		}
		else if(button ==3)
		{
			layout.getObjectInteraction().mousePress(x, y);
		}
		return false;
	}


	public boolean mousereleased(int x, int y, int button) {
		if(button ==3)
		{
			layout.getObjectInteraction().mouseRelease(x, y);
		}
		
		return false;
	}


	public boolean mousemoved(int x, int y) {
		return layout.getObjectInteraction().mouseMove(x, y);
		
	}


	public boolean mousedragged(int x, int y, int oldx, int oldy) {
		return layout.getObjectInteraction().mouseMove(x, y);
	}



	public void callRequestRender() {
		// TODO Auto-generated method stub
		
		this.requestRender();
		
		
	}


	@Override
	public void selectItem(String id, String name) {
		// TODO Auto-generated method stub
		CompactPerson person = new CompactPerson(Long.parseLong(id), name, "");
		int val = JOptionPane.showConfirmDialog(null, "Are you sure to select "+name+"?", "Confirmation?", JOptionPane.YES_NO_OPTION);
		if(val == JOptionPane.YES_OPTION)
		{
			selectPerson(person);
		}
		
	}

	@Override
	public void callSetToolTipText(String text) {
		// TODO Auto-generated method stub
		this.setToolTipText(text);
	}

}
