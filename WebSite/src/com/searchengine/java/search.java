package com.searchengine.java;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.searchengine.ws.Client;

/**
 * Author : mostafa
 * Created: 5/16/16
 * Licence: NONE
 */
public class search extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        Client client = new Client();
        response.setStatus(200);
        response.setContentType("text/html");
        String query = request.getParameter("q");
        if (query == null){
            RequestDispatcher rd = request.getRequestDispatcher("home");
            rd.forward(request, response);
        }else {
            String results = client.search(query);
            RequestDispatcher rd = request.getRequestDispatcher("/res/html/search.jsp");
            request.setAttribute("searchResults",results);
            request.setAttribute("searchText",query);
            rd.forward(request, response);
        }
    }
}
