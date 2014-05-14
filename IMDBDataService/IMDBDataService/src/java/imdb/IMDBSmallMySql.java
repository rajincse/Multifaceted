/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imdb;

import util.Configuration;

/**
 *
 * @author rajin
 */
public class IMDBSmallMySql extends IMDBMySql{
    public IMDBSmallMySql(String host, String port, String databaseName, String userName, String password)
    {
            super( host,port,databaseName,userName,password);
    }
    @Override
    protected String getQuerySearchMovie(String searchKey) {
        String query ="SELECT 	T.id, "
                        +"		T.title,  "
                        +"		T.production_year "
                        +"FROM movie AS T "
                        +"WHERE   "
                        +" T.title LIKE '%"+searchKey+"%' "
                        +"LIMIT 0,"+Configuration.getQueryLimit()+" ; ";
        return query;
    }

    @Override
    protected String getQueryGetMovie(long movieId) {
         String query ="SELECT 	T.title AS title,  "
                        +"		T.production_year,   "
                        +"		T.rating, "
                        +"		C.role_id, "
                        +"		N.id,  "
                        +"		N.`name`, "
                        +"		N.gender "
                        +"		 "
                        +"FROM  "
                        +"movie AS T "
                        +"INNER JOIN cast_info AS C ON C.movie_id = T.id "
                        +" AND ( C.role_id=8 OR C.role_id = 1 OR C.role_id =2) "
                        +"INNER JOIN person AS N ON N.id = C.person_id "
                        +"WHERE  "
                        +"T.id =  "+movieId+" "
                        +"ORDER BY COALESCE(C.nr_order,1000);";
        return query;
    }

    @Override
    protected String getQuerySearchPerson(String searchKey) {
        
        String query = "SELECT   "
                        +"	N.id,  "
                        +"	`name`,  "
                        +"	N.gender "
                        +" FROM person AS N                        "
                        +"WHERE   "
                        +"`name` LIKE '%"+searchKey+"%' "
                        +"LIMIT 0,"+Configuration.getQueryLimit()+" ; ";
         
        return query;
    }

    @Override
    protected String getQueryGetPerson(long personId, int sortType) {
        String sortString ="T.rating DESC";
        if(sortType == SORT_BY_YEAR)
        {
            sortString = "T.production_year DESC";
        }
        String query ="SELECT 	 "
                        +"		N.`name`,"
                        +"		N.gender, "
                        +"		C.role_id, "
                        +"		T.id, "
                        +"		T.title AS title,  "
                        +"		T.production_year AS `year` "
                        +"		 ,COALESCE(B.biography,'') AS biography "
                        +"FROM  "
                        +"movie AS T "
                        +"INNER JOIN cast_info AS C ON C.movie_id = T.id  AND (C.role_id = 1 OR C.role_id=2 OR C.role_id=8) "
                        +"INNER JOIN person AS N ON N.id = C.person_id "
                        +"  LEFT OUTER JOIN biography AS B ON B.person_id = N.id "
                        +"WHERE  "
                        +"N.id = "+personId+" "
                        +"ORDER BY "+sortString+" ;";
        return query;
    }
    
}
