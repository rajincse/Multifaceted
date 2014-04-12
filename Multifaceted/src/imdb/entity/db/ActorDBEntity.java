package imdb.entity.db;

import imdb.IMDBMySql;
import imdb.entity.Actor;
import imdb.entity.Movie;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import db.mysql.DatabaseHelper;

public class ActorDBEntity implements DBEntity<Actor>{

	private DatabaseHelper db;
	public ActorDBEntity(DatabaseHelper db)
	{
		this.db = db;
	}
	@Override
	public ArrayList<Actor> getEntityList(String searchKey) {
		String query ="SELECT  "
				+"	N.id, "
				+"	N.`name`, "
				+"	N.gender "
				+" FROM `name` AS N "
				+"WHERE  "
				+"N.`name` LIKE '%"+searchKey+"%' "
				+"LIMIT 0,10;";
		DefaultTableModel table = db.getData(query);
		int totalRows = table.getRowCount();
		ArrayList<Actor> actorList = new ArrayList<Actor>();
		for(int row=0;row<totalRows;row++)
		{
			long id =Long.parseLong( table.getValueAt(row, 0).toString());
			String name = table.getValueAt(row, 1).toString();
			String gender = table.getValueAt(row, 2).toString();
			Actor actor = new Actor(id, name, gender);
			actorList.add(actor);
		}
		
		return actorList;
	}

	@Override
	public Actor getEntity(long id) {
		String query ="SELECT  "
				+"	N.`name`, "
				+"	N.gender "
				+" FROM `name` AS N "
				+"WHERE  "
				+"N.id ="+id+";";
		DefaultTableModel table = db.getData(query);
		String name = table.getValueAt(0, 0).toString();
		String gender = table.getValueAt(0, 1).toString();
		Actor actor = new Actor(id, name, gender);
		actor.setLoaded(true);
		return actor;
	}

	@Override
	public boolean loadEntity(Actor entity) {
		entity.setLoaded(true);
		return true;
	}

	public static void main(String[] args)
	{
		ActorDBEntity db = new ActorDBEntity(new IMDBMySql());
		ArrayList<Actor> actorList = db.getEntityList("Ananta");
		for(Actor entity: actorList)
		{
			db.loadEntity(entity);
			System.out.println(entity);
		}
	}

}
