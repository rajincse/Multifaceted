/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imdb;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import util.Configuration;

/**
 *
 * @author rajin
 */
public class DataExportQueryMySql extends IMDBMySql{
    public static final int TYPE_SMALL=0;
    public static final int TYPE_BIG=1;
    
    public static final int INVALID =-1;
    
    public static final int INFO_BIRTH_DATE =21;
    public static final int INFO_HEIGHT =22;
    public static final int INFO_DEATH_DATE =23;
    public  static DataExportQueryMySql getDBSmall()
    {
        String host = Configuration.getParamValue(Configuration.PARAM_HOST);
        String port = Configuration.getParamValue(Configuration.PARAM_PORT);
        String databaseName = Configuration.getParamValue(Configuration.PARAM_DATABASE_NAME_SMALL);
        String userName = Configuration.getParamValue(Configuration.PARAM_USER_NAME);
        String password = Configuration.getParamValue(Configuration.PARAM_PASSWORD);
        DataExportQueryMySql db = new DataExportQueryMySql(host, port, databaseName, userName, password);
        
        return db;
    }
    
    public DataExportQueryMySql(String host, String port, String databaseName, String userName, String password)
    {
            super( host,port,databaseName,userName,password);
    }
    private String getActorTable(int type)
    {
        if(type == TYPE_SMALL)
        {
            return "rajin_actor_name_id";
        }
        else
        {
            return "rajin_actor_name_id_big";
        }
    }
    
    private String getMovieTable(int type)
    {
        if(type == TYPE_SMALL)
        {
            return "rajin_movie_name";
        }
        else
        {
            return "rajin_movie_name_big";
        }
    }
    public String getQueryMovieList(int type)
    {
        String movieTableName = getMovieTable(type);
        String actorTableName = getActorTable(type);
        String query ="SELECT  "+
            "	M.id,  "+
            "	M.title, "+
            "	M.production_year, "+
            "	M.rating, "+
            "	COALESCE(RA.id,-1), "+
            "	COALESCE(RA.actorName,''), "+
            "	G.id, "+
            "	G.genre,  "+
            "	COALESCE(P.id,-1), "+
            "	COALESCE(P.`name`,'') AS directorName "+
            "FROM  "+
            movieTableName+" AS R  "+
            "INNER JOIN movie AS M ON M.id = R.id  "+
            "INNER JOIN cast_info AS CI ON CI.movie_id = M.id "+
            "INNER JOIN movie_genre AS MG ON MG.movie_id = M.id "+
            "INNER JOIN genre AS G ON G.id = MG.genre_id "+
            "LEFT OUTER JOIN "+actorTableName+" AS RA ON RA.id = CI.person_id AND (CI.role_id =1 OR CI.role_id =2) "+
            "LEFT OUTER JOIN person AS P ON P.id = CI.person_id AND CI.role_id =8 "+
            "WHERE  "+
            "NOT (RA.id IS  NULL AND P.id IS  NULL) "+
            "ORDER BY M.rating DESC,M.id, COALESCE(CI.nr_order,1000);";
        return query;
    }
    
    public String getQueryActorList(int type)
    {
        String tableName = getActorTable(type);
        String query ="SELECT  "+
                    "		P.id,  "+
                    "		TRIM(R.actorName) ,  "+
                    "		P.gender,   "+
                    "		COALESCE(PI.info_type_id,0),  "+
                    "		COALESCE(PI.info,'') "+
                    "FROM "+tableName+" AS R "+
                    "INNER JOIN person AS P ON P.id = R.id "+
                    "LEFT OUTER JOIN imdb.person_info AS PI  "+
                    "ON P.id = PI.person_id  "+
                    "AND PI.info_type_id >20 AND PI.info_type_id < 24 "+
                    " ORDER BY TRIM(R.actorName);";
        return query;
    }
    
    public String getFileContentActorList(int type)            
    {
        StringBuilder fileContent =new StringBuilder();
        String query = this.getQueryActorList(type);
        
        DefaultTableModel table = this.getData(query);
        int totalRows = table.getRowCount();
        
        long currentId =INVALID;
        String name="";
        String gender ="";
        String dateOfBirth="";
        String height="";
        String dateOfDeath="";
        
        for(int row=0;row<totalRows;row++)
        {
            long id = Long.parseLong( table.getValueAt(row, 0).toString());
            if(currentId != id && currentId != INVALID)
            {
                String fileLine =name+"\t"+gender+"\t"+dateOfBirth+"\t"+dateOfDeath+"\t"+height+"\r\n";
                fileContent.append(fileLine);
                dateOfBirth="";
                height="";
                dateOfDeath="";
                
                currentId =id;
                name = table.getValueAt(row, 1).toString();
                gender = table.getValueAt(row, 2).toString();
            }
            else if(currentId != id && currentId == INVALID)
            {   
                currentId =id;
                name = table.getValueAt(row, 1).toString();
                gender = table.getValueAt(row, 2).toString();
            }
            int infoType = Integer.parseInt( table.getValueAt(row, 3).toString());
            String info = table.getValueAt(row, 4).toString();
            if(infoType == INFO_BIRTH_DATE)
            {
                dateOfBirth=info;
            }
            else if (infoType == INFO_DEATH_DATE)
            {
                dateOfDeath=info;
            }
            else if (infoType == INFO_HEIGHT)
            {
                height=info;
            }
                   
        }
        String fileLine =name+"\t"+gender+"\t"+dateOfBirth+"\t"+dateOfDeath+"\t"+height+"\r\n";
        fileContent.append(fileLine);
        
        return fileContent.toString();
    }
    
    public String getFileContentMovieList(int type)            
    {
        StringBuilder fileContent =new StringBuilder();
        
        String query = getQueryMovieList(type);
        
        
        DefaultTableModel table = this.getData(query);
        int totalRows = table.getRowCount();
        
        long currentId =INVALID;
        String title ="";
        int productionYear=0;
        double rating = 0;
        
        ArrayList<String> genreList = new ArrayList<String>();
        ArrayList<String> actorList = new ArrayList<String>();
        ArrayList<String> directorList = new ArrayList<String>();
        
        ArrayList<Long> genreIdList = new ArrayList<Long>();
        ArrayList<Long> actorIdList = new ArrayList<Long>();
        ArrayList<Long> directorIdList = new ArrayList<Long>();
        for(int row=0;row<totalRows;row++)
        {
            long id = Long.parseLong( table.getValueAt(row, 0).toString());
            if(currentId != id && currentId != INVALID)
            {
                String fileLine = title+"\t"+productionYear+"\t"+String.format("%.2f",rating)+"\t";
                fileLine+= getCommaList(genreList)+"\t"+getCommaList(directorList)+"\t";
                fileLine+= getCommaList(actorList)+"\r\n";
                fileContent.append(fileLine);
                
                genreIdList.clear();
                genreList.clear();
                directorIdList.clear();
                directorList.clear();
                actorIdList.clear();
                actorList.clear();
                
                currentId =id;
                title = table.getValueAt(row, 1).toString();
                productionYear = Integer.parseInt(table.getValueAt(row, 2).toString());
                rating = Double.parseDouble(table.getValueAt(row, 3).toString());
            }
            else if(currentId != id && currentId == INVALID)
            {
                currentId =id;
                title = table.getValueAt(row, 1).toString();
                productionYear = Integer.parseInt(table.getValueAt(row, 2).toString());
                rating = Double.parseDouble(table.getValueAt(row, 3).toString());
            }
            long actorId = Long.parseLong(table.getValueAt(row, 4).toString());
            String actor  = table.getValueAt(row, 5).toString();
            long genreId = Long.parseLong(table.getValueAt(row, 6).toString());
            String genre = table.getValueAt(row, 7).toString();
            long directorId = Long.parseLong(table.getValueAt(row, 8).toString());
            String director = table.getValueAt(row, 9).toString();
            
            if(actorId != INVALID && !actorIdList.contains(actorId))
            {
                actorIdList.add(actorId);
                actorList.add(actor);
            }
            
            if(genreId != INVALID && !genreIdList.contains(genreId))
            {
                genreIdList.add(genreId);
                genreList.add(genre);
            }
            
            if(directorId != INVALID && !directorIdList.contains(directorId))
            {
                directorIdList.add(directorId);
                directorList.add(director);
            }
        }
        
        String fileLine = title+"\t"+productionYear+"\t"+String.format("%.2f",rating)+"\t";
        fileLine+= getCommaList(genreList)+"\t"+getCommaList(directorList)+"\t";
        fileLine+= getCommaList(actorList)+"\r\n";
        fileContent.append(fileLine);
        
        return fileContent.toString();
    }
    
    private String getCommaList(ArrayList<String> list)
    {
        String val ="";
        for(String elem : list)
        {
            val+=elem+", ";
        }
        if(!list.isEmpty())
        {
            val = val.substring(0,val.length()-2);
        }
        
        return val;
    }
    
}
