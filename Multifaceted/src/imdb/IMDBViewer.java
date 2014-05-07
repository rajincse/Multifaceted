package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import multifaceted.layout.LayoutViewerInterface;
import multifaceted.layout.PivotPathGroupLayout;
import multifaceted.layout.PivotPathLayout;

import perspectives.base.Property;
import perspectives.base.Task;
import perspectives.base.Viewer;
import perspectives.properties.POptions;
import perspectives.properties.PSignal;
import perspectives.properties.PString;
import perspectives.properties.PText;
import perspectives.two_d.JavaAwtRenderer;

public class IMDBViewer extends Viewer implements JavaAwtRenderer, LayoutViewerInterface {

	private static final String PROPERTY_SEARCH_ACTOR="Search Actor";
	private static final String PROPERTY_SEARCH_RESULT = "Search Result";
	private static final String PROPERTY_SELECTED_ITEM="Selected Item";
	private static final String PROPERTY_SELECT="Select";
	private static final String PROPERTY_STEP="Debug.Step";
	private static final String PROPERTY_PERFORMANCE="Debug.Check Performance";
	
	private static final int MAX_ACTOR=5;
	private static final int MIN_ACTOR=5;
	
	private static final int MAX_SIMULATION =50;
	private static final int SIMULATION_SPEED =10;
	
	private IMDBDataSource data;
	
	private ArrayList<CompactPerson> personList = null;
	
	private PivotPathLayout layout =null;
	
	public IMDBViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
		this.layout = new PivotPathGroupLayout(this);
//		this.layout = new PivotPathLayout(this);
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
									int selectedIndex = ((POptions)getProperty(PROPERTY_SEARCH_RESULT).getValue()).selectedIndex;
									if(personList != null && selectedIndex < personList.size())
									{
										CompactPerson person = personList.get(selectedIndex);
										selectPerson(person);
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
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
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
		updateSelectedItem(item);
	}
	private void selectPerson(CompactPerson compactPerson)
	{		
		long time = System.currentTimeMillis();
		this.stopSimulation();
		this.layout.init();
		
		System.out.println("Selected:"+compactPerson);
		
		Person person = this.data.getPerson(compactPerson);
		updateSelectedItem(person.getDisplayName()+"("+(person.getGender().equals("m")?"Male":"Female")+")"+"\r\n"
						+(person.getBiographyList().size()>0?person.getBiographyList().get(0):""));
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
				if(!layout.getElements().contains(""+director.getId()))
				{
					int destination =layout.addBottomElement(""+director.getId(),director.getDisplayName(),source );					
					layout.addEdge(source, destination);
				}
				else
				{
					int destination =layout.getElements().indexOf(""+director.getId());
					layout.addEdge(source, destination);
				}
				 
				
			}
			
			ArrayList<CompactPerson> actorList = movie.getActors();
			int actorCount=0;
			for(CompactPerson actor: actorList)
			{
				if(!layout.getElements().contains(""+actor.getId()) && actor.getId() != person.getId())
				{
					int destination =layout.addTopElement(""+actor.getId(),actor.getDisplayName(), source);					
					layout.addEdge(source, destination);
					actorCount++;
					
				}
				else if(actor.getId() != person.getId())
				{
					int destination = layout.getElements().indexOf(""+actor.getId());
					layout.addEdge(source, destination);
				}
				
				if(actorCount >=MAX_ACTOR)
				{
					break;
				}
			}
		}
		
		
		

		
		this.startSimulation(50);
		time = System.currentTimeMillis() - time;
		System.out.println("Total time:"+time);
	}
	private void selectMovie(CompactMovie movie)
	{
		
	}
			
	
	private void updateSelectedItem(String item)
	{
		removeProperty(PROPERTY_SELECTED_ITEM);
		Property<PText> pSelected = new Property<PText>(PROPERTY_SELECTED_ITEM,new PText(item));
		pSelected.setDisabled(true);
		addProperty(pSelected);
	}
	
	
	public void render(Graphics2D g) {
		layout.render(g);
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

	public boolean mousepressed(int x, int y, int button) {
		layout.getObjectInteraction().mousePress(x, y);
		return false;
	}


	public boolean mousereleased(int x, int y, int button) {
		layout.getObjectInteraction().mouseRelease(x, y);
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
	public void selectItem(String id) {
		// TODO Auto-generated method stub
		CompactPerson person = new CompactPerson(Long.parseLong(id), "", "");
		selectPerson(person);
	}

}
