package com.searchengine.java;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

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
        response.setStatus(200);
        response.setContentType("text/html");
        RequestDispatcher rd = request.getRequestDispatcher("/res/html/homepage.html");
        rd.forward(request, response);
    }

}
