package com.searchengine.java;

import javax.naming.Context;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author : mostafa
 * Created: 5/15/16
 * Licence: NONE
 */
public class homepage extends HttpServlet{

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        // Set response content type
        response.setContentType("text/html");

//        ServletContext context = getServletContext();
//        String path = context.getRealPath("/res/html/homepage.html");

//        PrintWriter out = response.getWriter();
//        out.println("<h1>" + "Hello This is the Homepage" +  "</h1>");
//        out.println("<p>"+" the current dir is : "+path+"</p>");

        RequestDispatcher rd = request.getRequestDispatcher("/res/html/homepage.html");
        rd.forward(request, response);
    }

}
