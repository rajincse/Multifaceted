/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imdb;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import imdb.entity.CompactMovie;
import imdb.entity.CompactPerson;
import imdb.entity.Movie;
import imdb.entity.Person;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            IMDBMySql db = getDB();
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
                Person person = db.getPerson(id);
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
            out.println(ex.getMessage());
            ex.printStackTrace();
            Enumeration<String> names = request.getParameterNames();
            while(names.hasMoreElements())
            {
                 String n = names.nextElement();
                 out.println(n+":"+request.getParameter(n));

            }
            
        }
        finally {     
            out.close();
        }
    }
   
    private IMDBMySql getDB()
    {
        String host = getServletContext().getInitParameter(QueryServlet.PARAM_HOST);
        String port = getServletContext().getInitParameter(QueryServlet.PARAM_PORT);
        String databaseName = getServletContext().getInitParameter(QueryServlet.PARAM_DATABASE_NAME);
        String userName = getServletContext().getInitParameter(QueryServlet.PARAM_USER_NAME);
        String password = getServletContext().getInitParameter(QueryServlet.PARAM_PASSWORD);
        IMDBMySql db = new IMDBMySql(host, port, databaseName, userName, password);
        
        return db;
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
