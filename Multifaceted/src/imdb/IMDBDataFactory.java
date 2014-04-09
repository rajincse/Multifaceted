package imdb;

import perspectives.base.DataSource;
import perspectives.base.DataSourceFactory;

public class IMDBDataFactory extends DataSourceFactory{

	@Override
	public DataSource create(String name) {
		// TODO Auto-generated method stub
		return new IMDBDataSource(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "IMDBDataSource";
	}

}
