package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import multifaceted.layout.LayoutViewerInterface;
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
	
	private static final int MAX_ACTOR=10;
	
	private IMDBDataSource data;
	
	private ArrayList<CompactPerson> personList = null;
	
	private PivotPathLayout layout =null;
	
	public IMDBViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
		this.layout = new PivotPathLayout(this);
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
				System.out.println(resultList[i]);
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
		System.out.println("Selected:"+compactPerson);
		Person person = this.data.getPerson(compactPerson);
		int movieCount =0;
		for(int i=0;i< person.getActedMovieList().size() && movieCount< 10;i++)
		{
			
			CompactMovie compactMovie = person.getActedMovieList().get(i);
			
			System.out.println(compactMovie);
			Movie movie = this.data.getMovie(compactMovie);
			if(movie.getActors().size()< 5)
			{	
				continue;
			}
			else
			{
				movieCount++;
			}
			layout.addMiddleElement(""+compactMovie.getId());
			layout.addLabel(compactMovie.toString()+" rating:"+movie.getRating(), true,false);
			int source = layout.getElements().indexOf(""+compactMovie.getId());
			ArrayList<CompactPerson> directorList = movie.getDirectors();
			for(CompactPerson director: directorList)
			{
				if(!layout.getElements().contains(""+director.getId()))
				{
					layout.addBottomElement(""+director.getId());
					layout.addLabel(director.getDisplayName(), false,false);
					
				}
				int destination = layout.getElements().indexOf(""+director.getId());
				layout.addEdge(source, destination);
			}
			
			ArrayList<CompactPerson> actorList = movie.getActors();
			int actorCount=0;
			for(CompactPerson actor: actorList)
			{
				if(!layout.getElements().contains(""+actor.getId()) && actor.getId() != person.getId())
				{
					layout.addTopElement(""+actor.getId());
					layout.addLabel(actor.getDisplayName(),false,true);
					
					actorCount++;
					
				}
				if(actor.getId() != person.getId())
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
	
	
	
	
	@Override
	public void render(Graphics2D g) {
		layout.render(g);
	}

	@Override
	public void simulate() {
		
		for (int i=0; i<10; i++)
		{
			System.out.println("Iteration:"+i);
			layout.iteration();
		}
			
		
		
		this.requestRender();
		
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
	public boolean mousepressed(int x, int y, int button) {
		layout.getObjectInteraction().mousePress(x, y);
		return false;
	}

	@Override
	public boolean mousereleased(int x, int y, int button) {
		layout.getObjectInteraction().mouseRelease(x, y);
		return false;
	}

	@Override
	public boolean mousemoved(int x, int y) {
		return layout.getObjectInteraction().mouseMove(x, y);
		
	}

	@Override
	public boolean mousedragged(int x, int y, int oldx, int oldy) {
		return layout.getObjectInteraction().mouseMove(x, y);
	}


	@Override
	public void callRequestRender() {
		// TODO Auto-generated method stub
		this.requestRender();
	}

}
