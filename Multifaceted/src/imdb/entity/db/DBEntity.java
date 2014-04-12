package imdb.entity.db;



import java.util.ArrayList;


public interface DBEntity<T> {
	public  ArrayList<T> getEntityList(String searchKey);
	public T getEntity(long id);
	public boolean loadEntity(T entity);
}
