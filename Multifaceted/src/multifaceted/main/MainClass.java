package multifaceted.main;

import imdb.IMDBDataFactory;
import imdb.IMDBViewerFactory;
import perspectives.base.Environment;

public class MainClass {
	public static void main(String[] args)
	{
		Environment e = new Environment(false);
		e.registerDataSourceFactory(new IMDBDataFactory());
		e.registerViewerFactory(new IMDBViewerFactory());
		
	}
}
