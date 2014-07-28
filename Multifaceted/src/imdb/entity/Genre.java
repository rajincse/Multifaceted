/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imdb.entity;

/**
 *
 * @author rajin
 */
public class Genre {

  
    private long id;
    private String genreName;
    
    public Genre(long id, String genreName)
    {
        this.id = id;
        this.genreName = genreName;
    }
    public long getId() {
        return id;
    }

    public String getGenreName() {
        return genreName;
    }

    @Override
    public boolean equals(Object obj) {
    	// TODO Auto-generated method stub
    	if(obj instanceof Genre)
    	{
    		if(((Genre)obj).getId() == this.id)
    		{
    			return true;
    		}
    		else
    		{
    			return false;
    		}
    	}
    	return super.equals(obj);
    }
    @Override
    public String toString() {
        return "{ id :"+id+", genreName:"+genreName+"}";
    }
    
    
}
