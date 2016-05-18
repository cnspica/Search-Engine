package com.searchengine.java;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.searchengine.queryprocessors.PhraseDocument;
import com.searchengine.queryprocessors.QueryResult;
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
        List<QueryResult> feedback = client.search(request.getParameter("q"));
        PrintWriter out = response.getWriter();
        for(QueryResult t : feedback){
        out.println("<a href=\"" +t.getUrl() + "\"/>" + t.getTitle() + "<br/>");
        }
//        RequestDispatcher rd = request.getRequestDispatcher("");
//        rd.forward(request, response);
    }
}
