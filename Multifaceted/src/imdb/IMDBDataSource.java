package imdb;

import perspectives.base.DataSource;
import perspectives.base.Property;
import perspectives.base.Task;
import perspectives.properties.PInteger;
import perspectives.properties.PSignal;
import perspectives.properties.PString;
import perspectives.properties.PText;

public class IMDBDataSource extends DataSource{

	private static final String PROPERTY_HOST = "MySQL Host";
	private static final String PROPERTY_PORT = "Port";
	private static final String PROPERTY_DATABASE="Database";
	private static final String PROPERTY_USER= "UserName";
	private static final String PROPERTY_PASSWORD="Password";
	private static final String PROPERTY_STATUS="Status";
	private static final String PROPERTY_SUBMIT="Create";
	
	private static final String STATUS_CONNECTED="Connected";
	private static final String STATUS_NOT_CONNECTED="Not Connected";
	
	
	private IMDBMySql db;
			
	public IMDBDataSource(String name) {
		super(name);
		try
		{
			Property<PString> pHost = new Property<PString>(PROPERTY_HOST, new PString("localhost"));
			this.addProperty(pHost);
			
			Property<PString> pPort = new Property<PString>(PROPERTY_PORT, new PString("3306"));
			this.addProperty(pPort);
			
			Property<PString> pDatabase = new Property<PString>(PROPERTY_DATABASE, new PString("imdb"));
			this.addProperty(pDatabase);
			
			Property<PString> pUserName = new Property<PString>(PROPERTY_USER, new PString("root"));
			this.addProperty(pUserName);

			Property<PString> pPassword = new Property<PString>(PROPERTY_PASSWORD, new PString(""));
			this.addProperty(pPassword);
			
			
			
			
			Property<PSignal> pSubmit = new Property<PSignal>(PROPERTY_SUBMIT, new PSignal())
					{
						protected boolean updating(PSignal newvalue) {
							Task t = new Task("Connecting ...") {
								
								@Override
								public void task() {
									String host = getStringValue(PROPERTY_HOST);
									String port = getStringValue(PROPERTY_PORT);
									String dbName = getStringValue(PROPERTY_DATABASE);
									String user = getStringValue(PROPERTY_USER);
									String password = getStringValue(PROPERTY_PASSWORD);
									db = new IMDBMySql(host, port, dbName, user, password);
									if(db.isValidConnection())
									{
										onConnectionSuccess();
									}
									else
									{
										System.out.println("Invalid connection");
									}
									done();
								}
							};
							t.indeterminate = true;
							t.blocking = true;
							t.start();
							return true;
						};
					};
			this.addProperty(pSubmit);
			
			Property<PString> pStatus = new Property<PString>(PROPERTY_STATUS, new PString(STATUS_NOT_CONNECTED));
			pStatus.setReadOnly(true);
			this.addProperty(pStatus);
			
		}catch(Exception e)
		{
			
		}
	}
	
	private String getStringValue(String propertyName)
	{
		return ((PString) getProperty(propertyName).getValue()).stringValue();
	}
	private void onConnectionSuccess()	
	{
		removeProperty(PROPERTY_SUBMIT);
		Property[] propertyArray = this.getProperties();
		for(int i=0;i<propertyArray.length;i++)
		{
			propertyArray[i].setReadOnly(true);
		}
		
		removeProperty(PROPERTY_STATUS);

		Property<PString> pStatus = new Property<PString>(PROPERTY_STATUS, new PString(STATUS_CONNECTED));
		pStatus.setReadOnly(true);
		this.addProperty(pStatus);
		
	}

}
