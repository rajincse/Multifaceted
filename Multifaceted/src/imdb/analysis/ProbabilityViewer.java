package imdb.analysis;

import imdb.IMDBViewer;

import java.awt.Color;
import java.awt.Graphics2D;

import multifaceted.layout.PivotElement;
import multifaceted.layout.PivotPathLayout;

import perspectives.base.Property;
import perspectives.properties.PFileInput;
import perspectives.two_d.JavaAwtRenderer;

public class ProbabilityViewer extends AnalysisViewer implements JavaAwtRenderer {
	public static final int INVALID =-1;
	public static final int TOTAL_CATEGORIES =4;
	public static final String PROPERTY_OPEN_FILE = "Open";
	public static final String[] CATEGORIES ={"Actor","Movie", "Director","Genre"};
	public static final double THRESHOLD = 0.6;
	
	private int [][] transitionMatrix ;
	private int[] categoryCount ;
	private int previousCategory;
	private long previousTimestamp;
	private String geneSequence;
	
	public ProbabilityViewer(String name) {
		super(name);
		init();
		try
		{
			Property<PFileInput> pOpenFile = new Property<PFileInput>(PROPERTY_OPEN_FILE, new PFileInput())
			{
				 @Override
				protected boolean updating(PFileInput newvalue) {
					// TODO Auto-generated method stub
					 init();
					 processFile(newvalue.path);
					 createVisualItems();
					 printInfo();
					 requestRender();
					return super.updating(newvalue);
				}
			};
			addProperty(pOpenFile);
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void init()
	{
		transitionMatrix = new int[TOTAL_CATEGORIES][TOTAL_CATEGORIES];
		categoryCount = new int[TOTAL_CATEGORIES];
		previousCategory=INVALID;
		previousTimestamp = INVALID;
		geneSequence ="";
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

	@Override
	protected void processFileLine(String line) {
		// TODO Auto-generated method stub
		String[] split = line.split("\t");
		if(split.length > 0 && split[0].equalsIgnoreCase("Mouse"))
		{
			// Do nothing for mouse lines.
			return;
		}
		
		if(split.length >= 7)
		{
			System.out.println(line);
			long time =Long.parseLong(split[0]);
			String id = split[1];
			String name = split[2];
			int layer = Integer.parseInt(split[3]);
			double score = Double.parseDouble(split[4]);
			int x = Integer.parseInt(split[5]);
			int y = Integer.parseInt(split[6]);
			String image = "";
			
			if(split.length == 8)
			{
				image = split[7];
			}
			if(score >= THRESHOLD && time != previousTimestamp)
			{
				int category = getCategory(layer, x);
				if(category != INVALID)
				{
					categoryCount[category]++;
					geneSequence +=CATEGORIES[category].charAt(0);
				}
				if(previousCategory != INVALID && category!= INVALID  )
				{
					transitionMatrix[previousCategory][category]++;
				}
				
				previousCategory = category;
			}
			
			previousTimestamp = time;
			
		}
	}
	private int getCategory(int layer, int x)
	{
		// Return invalid layer for main item.
		if(layer == PivotPathLayout.LAYER_MIDDLE && x == IMDBViewer.IMAGE_SAVE_OFFSET_X - 4 * PivotPathLayout.STEP_MIDDLE_ITEM)
		{
			return INVALID;
		}
		else
		{
			return layer;
		}
		
	}

	@Override
	protected void printInfo() {
		// TODO Auto-generated method stub
		System.out.println("Transition Matrix");
		for(int i=0;i<transitionMatrix.length;i++)
		{
			for(int j=0;j<transitionMatrix[i].length;j++)
			{
				//System.out.println(CATEGORIES[i]+"->"+CATEGORIES[j]+"\t"+transitionMatrix[i][j]);
				System.out.println(transitionMatrix[i][j]);
			}
		}
		
		System.out.println("Count");
		for(int i=0;i<categoryCount.length;i++)
		{
			System.out.println(categoryCount[i]);
		}
		System.out.println("Gene Sequence \r\n"+geneSequence);
	}

	@Override
	protected void createVisualItems() {
		// TODO Auto-generated method stub
		
	}

}
