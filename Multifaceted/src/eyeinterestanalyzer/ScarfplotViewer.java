package eyeinterestanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PFileInput;
import perspectives.properties.PList;
import perspectives.two_d.JavaAwtRenderer;

public class ScarfplotViewer extends Viewer implements JavaAwtRenderer{
	public static final String PROPERTY_LOAD_USER = "Load User";
	public static final String PROPERTY_USERS = "Users";
	public static int HEATMAP_X_OFFSET = 100;
	public static int HEATMAP_Y_OFFSET = 650;
	
	public static int CELL_WIDTH =50;
	
	protected ArrayList<User> users;
	protected User currentUser;
	protected User aggUser = null;
	
	
	public ScarfplotViewer(String name) {
		super(name);

		users = new ArrayList<User>();
		aggUser = new User("Aggregate", true);
		users.add(aggUser);
		
		Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD_USER, new PFileInput()){

			@Override
			protected boolean updating(PFileInput newvalue) {
				User u = new User(new File(newvalue.path));
				u.setCellWidth(CELL_WIDTH);
				users.add(u);
				
				aggUser.addToAggregate(u);		
				
				
				PList list = new PList();
				list.items = new String[users.size()];
				for (int i=0; i<users.size(); i++)
					list.items[i] = users.get(i).name;
				list.selectedIndeces = new int[]{list.items.length-1};
				getProperty(PROPERTY_USERS).setValue(list);
				return super.updating(newvalue);
			}
			
		};
		addProperty(pLoad);
		
		Property<PList> pUserList = new Property<PList>(PROPERTY_USERS, new PList()){
			@Override
			protected boolean updating(PList newvalue) {
				if (newvalue.selectedIndeces.length > 0)
					currentUser = users.get(newvalue.selectedIndeces[0]);
				
				requestRender();
				return super.updating(newvalue);
			}
		};
		addProperty(pUserList);
		
		
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
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(this.currentUser != null)
		{
			for (int i=0; i<currentUser.viewedObjects.size(); i++){

				if (currentUser.viewedObjects.get(i).type == 1)
					g.setColor(new Color(250,200,200));
				else if (currentUser.viewedObjects.get(i).type == 2)
					g.setColor(new Color(200,250,200));
				else if (currentUser.viewedObjects.get(i).type == 3)
					g.setColor(new Color(200,200,250));
				else if (currentUser.viewedObjects.get(i).type == 4)
					g.setColor(new Color(250,250,150));
				else if (currentUser.viewedObjects.get(i).type == 5)
					g.setColor(new Color(250,150,250));
				g.fillRect(HEATMAP_X_OFFSET+currentUser.cellWidth*i,0, 200+currentUser.cellHeight-1,currentUser.cellHeight-1);
				g.setColor(Color.black);
				String label = currentUser.viewedObjects.get(i).label.substring(0, Math.min(26, currentUser.viewedObjects.get(i).label.length()));
				g.setFont(g.getFont().deriveFont(10f));
				g.drawString(label, 0, HEATMAP_Y_OFFSET+currentUser.cellHeight*i+currentUser.cellHeight-2);
				

			}
		}
	}

	
}
