package com.searchengine.java;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>" + "This is the Search Page: " + request.getParameter("q")+ "</h1>");

//        RequestDispatcher rd = request.getRequestDispatcher("");
//        rd.forward(request, response);
    }
}
