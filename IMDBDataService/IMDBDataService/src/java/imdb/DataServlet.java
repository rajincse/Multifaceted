/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imdb;

import com.google.gson.Gson;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.Configuration;

/**
 *
 * @author rajin
 */
public class DataServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public static final String METHOD_IS_VALID ="IsValid";
    public static final String METHOD_SEARCH_MOVIE ="SearchMovie";
    public static final String METHOD_GET_MOVIE ="GetMovie";
    public static final String METHOD_SEARCH_PERSON ="SearchPerson";
    public static final String METHOD_GET_PERSON ="GetPerson";
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            IMDBMySql db = Configuration.getDBSmall();
            Gson gson = new Gson();
            String jsonData="";
            String method = request.getParameter("method");
            
            if(method.equalsIgnoreCase(METHOD_SEARCH_MOVIE))
            {
                String searchKey = request.getParameter("searchKey");
                ArrayList<CompactMovie> movieList = db.searchMovie(searchKey);
                jsonData = gson.toJson(movieList);
                
            }
            else if(method.equalsIgnoreCase(METHOD_GET_MOVIE))
            {
                long id = Long.parseLong( request.getParameter("id"));
                Movie movie = db.getMovie(id);
                jsonData = gson.toJson(movie);
            }
            else if(method.equalsIgnoreCase(METHOD_SEARCH_PERSON))
            {
                String searchKey = request.getParameter("searchKey");
                ArrayList<CompactPerson> personList = db.searchPerson(searchKey);
                jsonData = gson.toJson(personList);
            }
            else if(method.equalsIgnoreCase(METHOD_GET_PERSON))
            {
                long id = Long.parseLong( request.getParameter("id"));
                String sortString = request.getParameter("sort");
                
                int sortType = IMDBMySql.SORT_BY_RATING;
                if(sortString != null && !sortString.isEmpty())
                {
                    sortType = Integer.parseInt(sortString);
                }
                
                Person person = db.getPerson(id,sortType);
                jsonData = gson.toJson(person);
            }
            else if(method.equalsIgnoreCase(METHOD_IS_VALID))
            {
                boolean isValid = db.isValidConnection();
                jsonData = gson.toJson(isValid);
            }
            
            out.println(jsonData);
        
        }
        catch(Exception ex)
        {
            JSONObject exceptionObject = new JSONObject();
            
            exceptionObject.put("ExceptionMessage",ex.getMessage()+" ("+ex.getClass().toString()+")");
                    
            JSONArray stackArray = new JSONArray();
            StackTraceElement[] stackTrace = ex.getStackTrace();
            for(StackTraceElement elem: stackTrace)
            {
                stackArray.add(elem.toString());
            }
            exceptionObject.put("StackTrace", stackArray);
            
            JSONArray parameters = new JSONArray();
            Enumeration<String> names = request.getParameterNames();
            while(names.hasMoreElements())
            {
                 String n = names.nextElement();
                 JSONObject paramObj = new JSONObject();
                 paramObj.put(n, request.getParameter(n));
                 
                 parameters.add(paramObj);
            }
            exceptionObject.put("Parameters",parameters);
            
            out.println(exceptionObject);
            
        }
        finally {     
            out.close();
        }
    }
   
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
