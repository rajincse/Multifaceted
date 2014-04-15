package imdb.entity;



public class Actor {
	private long id ;
	

	private String name;
	private String gender;
	private boolean isLoaded;

	public Actor(long id, String name, String gender)
	{
		this.id = id;
		this.name = name;
		this.gender = gender;
		
		this.isLoaded = false;
	}
	public long getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public String getGender() {
		return gender;
	}


	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(isLoaded)
		{
			return ""+id+", "+name+", "+(gender.equalsIgnoreCase("m")?"Male":"Female");
		}
		else
		{
			return ""+id+", "+name+", "+(gender.equalsIgnoreCase("m")?"Male":"Female");
		}
		
	}
}
