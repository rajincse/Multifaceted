package imdb;

import perspectives.base.DataSource;
import perspectives.base.Property;
import perspectives.properties.PInteger;
import perspectives.properties.PSignal;
import perspectives.properties.PString;

public class IMDBDataSource extends DataSource{

	private static final String PROPERTY_HOST = "MySQL Host";
	private static final String PROPERTY_PORT = "Port";
	private static final String PROPERTY_DATABASE="Database";
	private static final String PROPERTY_USER= "UserName";
	private static final String PROPERTY_PASSWORD="Password";
	private static final String PROPERTY_SUBMIT="Create";
	
			
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
			
			Property<PSignal> pSubmit = new Property<PSignal>(PROPERTY_SUBMIT, new PSignal());
			this.addProperty(pSubmit);
			
			
		}catch(Exception e)
		{
			
		}
	}

}
