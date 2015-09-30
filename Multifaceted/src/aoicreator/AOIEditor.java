package aoicreator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.gson.Gson;

import perspectives.base.Property;
import perspectives.base.Viewer;
import perspectives.properties.PBoolean;
import perspectives.properties.PFileInput;
import perspectives.properties.PList;
import perspectives.properties.POptions;
import perspectives.properties.PSignal;
import perspectives.properties.PString;
import perspectives.properties.PText;
import perspectives.two_d.JavaAwtRenderer;

public class AOIEditor extends Viewer implements JavaAwtRenderer{

	public static final String PROPERTY_LOAD="Load";
	public static final String PROPERTY_LIST="AOI List";
	public static final String PROPERTY_ADD="Add";
	public static final String PROPERTY_NAME="Name";
	public static final String PROPERTY_RECREATE="Re Create";
	public static final String PROPERTY_SAVE="Save";
	
	private Gson gson = new Gson();
	
	private BufferedImage currentImage = null;
	private String currentImagePath="";
	private AOIStimuliInfo currentStimuliInfo = null;
	public AOIEditor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		try
		{
			PFileInput fileInput = new PFileInput();
			fileInput.extensions = new String[]{"png","*"};
			Property<PFileInput> pLoad = new Property<PFileInput>(PROPERTY_LOAD, fileInput )
					{
						@Override
						protected boolean updating(PFileInput newvalue) {
							// TODO Auto-generated method stub
							readImage(newvalue.path);
							updateList();
							requestRender();
							return super.updating(newvalue);
						}
					};
		    addProperty(pLoad);
		    
		    
		    Property<PList> pList = new Property<PList>(PROPERTY_LIST, new PList())
		    		{
		    			protected boolean updating(PList newvalue) {
		    				
		    				
		    				if(newvalue.selectedIndeces.length > 0 && currentStimuliInfo != null)
		    				{
		    					int index = newvalue.selectedIndeces[0];
		    					AOIItem aoi = currentStimuliInfo.getAoiItemList().get(index);
		    					((Property<PString>) getProperty(PROPERTY_NAME)).setValue(new PString(aoi.getName()));
		    					((Property<PBoolean>) getProperty(PROPERTY_RECREATE)).setValue(new PBoolean(false));
		    					
		    				}
		    					
		    				return super.updating(newvalue);
		    			};
		    		};
		    addProperty(pList);
		    
		    Property<PSignal> pAdd = new Property<PSignal>(PROPERTY_ADD, new PSignal())
		    		{
		    			protected boolean updating(PSignal newvalue) {
		    				if(currentStimuliInfo != null)
		    				{
		    					AOIItem aoi = new AOIItem("aoi",0,0,0,0);
		    					currentStimuliInfo.addItem(aoi);
		    					updateList();		    					
		    				}
		    				return super.updating(newvalue);
		    			};
		    		};
		   addProperty(pAdd);
		   
		   Property<PString> pName = new Property<PString>(PROPERTY_NAME, new PString(""))
				   {
			   			@Override
			   			protected boolean updating(PString newvalue) {
			   				// TODO Auto-generated method stub
			   				int index = getCurrentAOIIndex();
			   				if(currentStimuliInfo != null && index >=0)
			   				{
			   					currentStimuliInfo.getAoiItemList().get(index).setName(newvalue.stringValue());
			   					
			   					updateList();
			   				}
			   				
			   				return super.updating(newvalue);
			   			}
				   };
		   addProperty(pName);
		   
		   Property<PBoolean> pRecreate = new Property<PBoolean>(PROPERTY_RECREATE, new PBoolean(false))
				   {
			   			@Override
			   			protected boolean updating(PBoolean newvalue) {
			   				// TODO Auto-generated method stub
			   				return super.updating(newvalue);
			   			}
				   };
		   addProperty(pRecreate);
				 
		   
		   Property<PSignal> pSave = new Property<PSignal>(PROPERTY_SAVE, new PSignal())
		    		{
		    			protected boolean updating(PSignal newvalue) {
		    				saveFile(currentImagePath, currentStimuliInfo);
		    				return super.updating(newvalue);
		    			};
		    		};
		   addProperty(pSave);
		}
		catch(Exception ex)
		{
			
		}
	}
	private int getCurrentAOIIndex()
	{
		PList options = ((Property<PList>)getProperty(PROPERTY_LIST)).getValue();
		if(options.selectedIndeces.length > 0)
		{
			return options.selectedIndeces[0];
		}
		else
		{
			return -1;
		}
		
	}
	
	private boolean isReCreateOn()
	{
		PBoolean pReCreate = ((Property<PBoolean>)getProperty(PROPERTY_RECREATE)).getValue();
		
		return pReCreate.boolValue();
	}
	private String getDataFilePath(String imageFilePath)
	{
		String extension = imageFilePath.substring(imageFilePath.lastIndexOf("."));
		String fullPathName = imageFilePath.substring(0, imageFilePath.lastIndexOf(extension));
		String dataFilePath= fullPathName+".JSON";
		
		return dataFilePath;
	}
	private boolean dataFileExists(String imageFilePath)
	{
		File file = new File(getDataFilePath(imageFilePath));
		return file.exists();
	}
	private void readImage(String imageFilePath)
	{
		try {
			File imageFile = new File(imageFilePath);
		
			this.currentImage = ImageIO.read(imageFile);
			this.currentImagePath = imageFilePath;
			
			if(dataFileExists(imageFilePath))
			{
				this.currentStimuliInfo = readDataFile(imageFilePath); 
			}
			else
			{
				this.currentStimuliInfo = new AOIStimuliInfo(imageFile.getName());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private AOIStimuliInfo readDataFile(String imageFilePath)
	{
		try
		{
			
			String dataFilePath = getDataFilePath(imageFilePath);
			File file = new File(dataFilePath);
			FileReader fStream = new FileReader(file);
				
			BufferedReader bufferedReader = new BufferedReader(fStream);
			
			String jsonData ="";
			String fileLine = bufferedReader.readLine();
			
			
			while(fileLine != null)
			{
				jsonData +="\r\n"+fileLine;
				
				fileLine = bufferedReader.readLine();
			}
			
			
			
			bufferedReader.close();
			fStream.close();
			
			AOIStimuliInfo stimuliInfo = gson.fromJson(jsonData, AOIStimuliInfo.getType());
			
			return stimuliInfo;
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void saveFile(String imageFilePath, AOIStimuliInfo stimuliInfo)
	{
		if(stimuliInfo != null && imageFilePath != null && !imageFilePath.isEmpty())
		{
			try {
				String filePath = getDataFilePath(imageFilePath);
				String json = gson.toJson(stimuliInfo, AOIStimuliInfo.getType());
				
				FileWriter fstream= new FileWriter(new File(filePath), false);
				
				BufferedWriter br = new BufferedWriter(fstream);
	
				br.write(json);
	
				br.close();
				fstream.close();
				
				System.out.println("File Saved:"+filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void updateList()
	{
		if(currentStimuliInfo != null)
		{
			ArrayList<AOIItem> aois = currentStimuliInfo.getAoiItemList();
			String [] list = new String[aois.size()];
			
			for(int i=0;i<aois.size();i++)
			{
				list[i] = aois.get(i).getName();
			}
			((Property<PList>) getProperty(PROPERTY_LIST)).setValue(new PList(list));
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
	public boolean mousedragged(int x, int y, int oldX, int oldY) {
		// TODO Auto-generated method stub
		if(isReCreateOn())
		{
			return true;
		}
		else
		{
			return false;
		}
		
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
		if(currentImage != null)
		{
			g.drawImage(currentImage, 0, 0, currentImage.getWidth(),  currentImage.getHeight(),
					0, 0, currentImage.getWidth(), currentImage.getHeight(),null);
			
			int currentIndex = getCurrentAOIIndex();
			if(this.currentStimuliInfo != null && getCurrentAOIIndex() >= 0)
			{
				AOIItem aoi = this.currentStimuliInfo.getAoiItemList().get(currentIndex);
				g.setColor(Color.red);
				g.drawRect(aoi.getX(), aoi.getY(), aoi.getWidth(), aoi.getHeight());
			}
		}
	}

}
