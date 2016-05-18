package com.searchengine.java;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
        String results = client.search(request.getParameter("q"));
        PrintWriter out = response.getWriter();
        out.println("<h1>" + "This is the Search Page: " + results+ "</h1>");

//        RequestDispatcher rd = request.getRequestDispatcher("");
//        rd.forward(request, response);
    }
}
