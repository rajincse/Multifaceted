package eyetracker.instrument;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

import eyetrack.EyeTrackServer;
import eyetrack.EyeTrackerDataReceiver;

public class EyeTrackerFixationServer implements Runnable{
	public static final int PORT = 9876;
	EyeFixationDataReceiver gazeReceiver;
	
    private BufferedReader mIn;
    private String message;
    private String decoded = null;
	
	Socket socket;
	private ServerSocket serverSocket;
	public EyeTrackerFixationServer(EyeFixationDataReceiver gazeReceiver)
	{
		this.gazeReceiver = gazeReceiver;		
		
		this.serverSocket = null;
		    try {
		       serverSocket = new ServerSocket(EyeTrackServer.PORT);
		       }
		    catch (IOException se) 
		    {
		       System.err.println("Can not start listening on port " + EyeTrackServer.PORT);
		       se.printStackTrace();
		       System.exit(-1);
		    }
		    
		    Thread t = new Thread(this);
		    t.start();
	}
	
	@Override
	public void run() {

		if(this.serverSocket != null)
		{
	       try  
	       {
	    	   System.out.println("gazeserver: waiting for connection");
	           socket = serverSocket.accept();
	           mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	           
	           System.out.println("gazeserver: connected");
	       }
	       catch (IOException ioe) 
	       {
	           ioe.printStackTrace();
	       }
		}
        
        while (true) {
            try {
                message = mIn.readLine();
              
                if (message == null)
                {
                	continue;
                }
                    
                try {
                    decoded = URLDecoder.decode(message, "UTF-8");
                    
//                    System.out.println("from server:" + decoded);
                    
                    String[] split = decoded.split(",");
                    
                    if (split.length > 7)
                    {
                                              		
	        			int x = Integer.parseInt(split[0]);
	        			int y = Integer.parseInt(split[1]);        			
	        			
	        			double pupilDiameter = Double.parseDouble(split[2]);
	        			
	        			String method = split[6];
	        			
	        			
	        			if(method.equalsIgnoreCase(EyeFixationDataReceiver.METHOD_GAZE))
	        			{
	        				this.gazeReceiver.processGaze(new Point(x,y), pupilDiameter);
	        			}
	        			else if(method.equalsIgnoreCase(EyeFixationDataReceiver.METHOD_FIXATION))
	        			{
	        				String type = split[7];
	        				
	        				this.gazeReceiver.processFixation(new Point(x,y), pupilDiameter, type);
	        			}
	        				
	        			
        			 
        			
                    }
                    else if (split.length == 6)
                    {
                                              		
	        			int x = Integer.parseInt(split[0]);
	        			int y = Integer.parseInt(split[1]);        			
	        			
	        			double pupilDiameter = Double.parseDouble(split[2]);
	        			this.gazeReceiver.processGaze(new Point(x,y), pupilDiameter);
        			 
        			
                    }
                    else if (split.length == 4)
                    {
                    	int x = Integer.parseInt(split[1]);
	        			int y = Integer.parseInt(split[2]);        			
	        			
	        			double pupilDiameter = -1;
	        			this.gazeReceiver.processGaze(new Point(x,y), pupilDiameter);  
                    }
        			
        			
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                catch(Exception ex)
                {
                	ex.printStackTrace();
                }
            } 
            catch (IOException e) {
                break;
            }

        }

      
    }	
}
