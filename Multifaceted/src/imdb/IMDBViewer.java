package imdb;

import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.sun.org.glassfish.external.statistics.annotations.Reset;

import perspectives.base.ObjectInteraction;
import perspectives.base.Property;
import perspectives.base.Task;
import perspectives.base.Viewer;
import perspectives.base.ObjectInteraction.RectangleItem;
import perspectives.properties.POptions;
import perspectives.properties.PSignal;
import perspectives.properties.PString;
import perspectives.properties.PText;
import perspectives.two_d.JavaAwtRenderer;
import perspectives.util.Label;

public class IMDBViewer extends Viewer implements JavaAwtRenderer {

	private static final String PROPERTY_SEARCH_ACTOR="Search Actor";
	private static final String PROPERTY_SEARCH_RESULT = "Search Result";
	private static final String PROPERTY_SELECTED_ITEM="Selected Item";
	private static final String PROPERTY_SELECT="Select";
	
	
	
	private IMDBDataSource data;
	
	private ArrayList<CompactPerson> personList = null;
	
	
	public IMDBViewer(String name, IMDBDataSource data) {
		super(name);
		this.data = data;
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
		
		personList = this.data.searchPerson(searchKey);
		if(personList!= null && !personList.isEmpty())
		{
			String[] resultList = new String[personList.size()];
			for(int i=0;i<personList.size();i++)
			{
				resultList[i]= personList.get(i).toString();				
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
		
		for(int i=0;i< person.getActedMovieList().size() && i< 10;i++)
		{
			
			CompactMovie compactMovie = person.getActedMovieList().get(i);
			
			System.out.println(compactMovie);
			Movie movie = this.data.getMovie(compactMovie);
			addMiddleElement(""+compactMovie.getId());
			addLabel(compactMovie.toString()+" rating:"+movie.getRating(), true);
			int source = this.elem.indexOf(""+compactMovie.getId());
			ArrayList<CompactPerson> directorList = movie.getDirectors();
			for(CompactPerson director: directorList)
			{
				if(!this.elem.contains(""+director.getId()))
				{
					addBottomElement(""+director.getId());
					addLabel(director.getName(), false);
					int destination = this.elem.indexOf(""+director.getId());
					addEdge(source, destination);
				}
			}
			
			ArrayList<CompactPerson> actorList = movie.getActors();
			int actorCount=0;
			for(CompactPerson actor: actorList)
			{
				if(!this.elem.contains(""+actor.getId()) && actor.getId() != person.getId())
				{
					addTopElement(""+actor.getId());
					addLabel(actor.getName(),false);
					int destination = this.elem.indexOf(""+actor.getId());
					addEdge(source, destination);
					actorCount++;
					if(actorCount >=10)
					{
						break;
					}
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
	
	
	ArrayList<Label> labels = new ArrayList<Label>();
	
	ObjectInteraction o = new ObjectInteraction()
	{

		@Override
		protected void mouseIn(int object) {
			if (o.getItem(object).selected)
				labels.get(object).setColor(Color.red);
			else
				labels.get(object).setColor(Color.yellow);
			requestRender();
		}

		@Override
		protected void mouseOut(int object) {
			if (o.getItem(object).selected)
				labels.get(object).setColor(Color.pink);
			else
				labels.get(object).setColor(Color.LIGHT_GRAY);
			requestRender();
		}

		@Override
		protected void itemsSelected(int[] objects) {
			for (int i=0; i<labels.size(); i++)
				labels.get(i).setColor(Color.LIGHT_GRAY);
			for (int i=0; i<objects.length; i++)
				labels.get(objects[i]).setColor(Color.pink);
			requestRender();
		}
		
	};
	
	public void addLabel(String s, boolean tilt)
	{
		Label l = new Label(0 , 0, s);
		l.setColor(Color.LIGHT_GRAY);
		if (tilt)
		l.setRotationAngle(Math.PI / 3);
		labels.add(l);
		o.addItem(o.new RectangleItem(l));
	}

	ArrayList<String> elem = new ArrayList<String>();
	ArrayList<Point2D> elemPos = new ArrayList<Point2D>(); 
	
	ArrayList<Integer> layer = new ArrayList<Integer>();
	
	ArrayList<Integer> edges1 = new ArrayList<Integer>();
	ArrayList<Integer> edges2 = new ArrayList<Integer>();
	
	int cnt = 0;
	
	int middles = 0;
	
	public int addTopElement(String label)
	{
		elem.add(label);
		elemPos.add(new Point2D.Double(0,0));
		layer.add(0);
		return cnt++;
	}
	
	public int addBottomElement(String label)
	{
		elem.add(label);
		elemPos.add(new Point2D.Double(0,900));	
		layer.add(2);
		return cnt++;
	}
	
	public int addMiddleElement(String label)
	{
		elem.add(label);
		elemPos.add(new Point2D.Double(middles*50,450));
		middles++;
		
		layer.add(1);
		return cnt++;
	}
	
	public void addEdge(int e1, int e2)
	{
		edges1.add(e1);
		edges2.add(e2);
	}
	
	public void iteration()
	{
		//init forces for top and bottom
		double[] fx = new double[elem.size()];
		double[] fy = new double[elem.size()];

		
		//repulsive forces between point
		for (int i=0; i<elem.size()-1; i++)
			for (int j=i+1; j<elem.size(); j++)
			{
				if (layer.get(i) == 1 || layer.get(j) == 1)
					continue;
				
				if (!layer.get(i).equals(layer.get(j)))
					continue;
				
				double[] f = this.compRepulsion(elemPos.get(i), elemPos.get(j));
			
				fx[i] += f[0];
				fy[i] += f[1];
				
				fx[j] -= f[0];
				fy[j] -= f[1];
			}
		

		//edge forces
		for (int i=0; i<edges1.size(); i++)
		{
			double[] f = compAttraction(elemPos.get(edges1.get(i)), elemPos.get(edges2.get(i)), 100);
			fx[edges1.get(i)] += f[0];
			fy[edges1.get(i)] += f[1];
			
			fx[edges2.get(i)] -= f[0];
			fy[edges2.get(i)] -= f[1];
		}
		
	//boundary forces
		for (int i=0; i<elem.size(); i++)
		{
			if (layer.get(i) == 1) continue;
			
			double y = 300;
			if (layer.get(i) == 2) y = 600;
			
			double d = elemPos.get(i).getY() - y;
			
			double mag = 10000 * 1/(d*d);
			
			if (layer.get(i) == 0)
				fy[i] -= mag;
			else fy[i] += mag;
			
		}
		
		for (int i=0; i<fx.length; i++)
		{
			if (layer.get(i) == 1)
				continue;
				
			double fl = Math.sqrt(fx[i]*fx[i] + fy[i]*fy[i]);
			if (fl > 5)
			{
				fx[i] = 5 * (fx[i]/fl);
				fy[i] = 5 * (fy[i]/fl);
			}
			
			double x =elemPos.get(i).getX();
			double y =elemPos.get(i).getY();
			
			x = x+ fx[i];
			y = y +fy[i];
			
			elemPos.remove(i);
			elemPos.add(i, new Point2D.Double(x,y));
		}
		
	}
	
	private double[] compAttraction(Point2D p1, Point2D p2, double springLength)
	{
		double x1 = p1.getX();
		double y1 = p1.getY();	

		double x2 = p2.getX();
		double y2 = p2.getY();
		
		double d = p1.distance(p2);
		
		if (d == 0) return new double[]{0,0};
		
		double vx = x2-x1;
		double vy = y2-y1;
		double vl = Math.sqrt(vx*vx + vy*vy);
		vx = vx/vl;
		vy = vy/vl;
		
		double mag = d/springLength;
		
		return new double[]{vx*mag, vy*mag};			
	}
	
	private double[] compRepulsion(Point2D p1, Point2D p2)
	{
		double d = p1.distance(p2);
		
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		
		double vx = x2 - x1;
		double vy = y2 - y1;
		
		while (d < 0.1)
		{
			x2 = x2 + Math.random()/10;
			y2 = y2 + Math.random()/10;
			vx = x2-x1;
			vy = y2-y1;				
			d = vx*vx + vy*vy;
			d = Math.sqrt(d);
		}			
		vx /= d;
		vy /= d;
		
		double mag = -5000 * 1./(d*d);
		
		return new double[]{vx*mag, vy*mag};
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.black);
		for (int i=0; i<elem.size(); i++)
		{
			int x = (int)elemPos.get(i).getX();
			int y = (int)elemPos.get(i).getY();
			
			labels.get(i).x = x;
			labels.get(i).y = y;
			
			//g.drawString(elem.get(i), x, y);
			labels.get(i).render(g);
		}
		
		for (int i=0; i<edges1.size(); i++)
		{
			g.setColor(Color.lightGray);
			if (o.getItem(edges1.get(i)).hovered || (o.getItem(edges2.get(i)).hovered))
					g.setColor(Color.black);
			if (o.getItem(edges1.get(i)).selected || (o.getItem(edges2.get(i)).selected))
					g.setColor(Color.red);
			int x1 = (int)elemPos.get(edges1.get(i)).getX();
			int y1 = (int)elemPos.get(edges1.get(i)).getY();
			int x2 = (int)elemPos.get(edges2.get(i)).getX();
			int y2 = (int)elemPos.get(edges2.get(i)).getY();
			
			g.drawLine(x1, y1, x2, y2);
		}
	}

	@Override
	public void simulate() {
		for (int i=0; i<10; i++)
			this.iteration();
		
		
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
		o.mousePress(x, y);
		return false;
	}

	@Override
	public boolean mousereleased(int x, int y, int button) {
		o.mouseRelease(x, y);
		return false;
	}

	@Override
	public boolean mousemoved(int x, int y) {
		return o.mouseMove(x, y);
		
	}

	@Override
	public boolean mousedragged(int x, int y, int oldx, int oldy) {
		return o.mouseMove(x, y);
	}

}
