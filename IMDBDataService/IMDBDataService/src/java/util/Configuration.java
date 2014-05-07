package util;

import imdb.IMDBMySql;
import imdb.IMDBSmallMySql;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author rajin
 */
public class Configuration {
    public static final String PARAM_HOST ="db.mysql.host";
    public static final String PARAM_PORT ="db.mysql.port";
    public static final String PARAM_DATABASE_NAME ="db.mysql.databasename";
    public static final String PARAM_DATABASE_NAME_SMALL ="db.mysql.databasename.small";
    public static final String PARAM_USER_NAME ="db.mysql.username";
    public static final String PARAM_PASSWORD ="db.mysql.password";
    
    public static final String PARAM_QUERY_LIMIT ="db.mysql.query.limit";
    
    public static String getParamValue(String paramName)
    {
        try {
            Context context = (Context)new InitialContext().lookup("java:comp/env");
            
            String value = (String)context.lookup(paramName);
            
            return value;
            
        } catch (NamingException ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public  static IMDBMySql getDB()
    {
        String host = getParamValue(PARAM_HOST);
        String port = getParamValue(PARAM_PORT);
        String databaseName = getParamValue(PARAM_DATABASE_NAME);
        String userName = getParamValue(PARAM_USER_NAME);
        String password = getParamValue(PARAM_PASSWORD);
        IMDBMySql db = new IMDBMySql(host, port, databaseName, userName, password);
        
        return db;
    }
     public  static IMDBMySql getDBSmall()
    {
        String host = getParamValue(PARAM_HOST);
        String port = getParamValue(PARAM_PORT);
        String databaseName = getParamValue(PARAM_DATABASE_NAME_SMALL);
        String userName = getParamValue(PARAM_USER_NAME);
        String password = getParamValue(PARAM_PASSWORD);
        IMDBMySql db = new IMDBSmallMySql(host, port, databaseName, userName, password);
        
        return db;
    }
    public static String getQueryLimit()
    {
        return Configuration.getParamValue(PARAM_QUERY_LIMIT);
    }
}
