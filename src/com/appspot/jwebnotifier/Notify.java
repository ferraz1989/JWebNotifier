package com.appspot.jwebnotifier;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Notify extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {       
    	//Ejecutamos la tarea programada
        Control.doNotification(Integer.valueOf(request.getParameter("frequency")));
    }    
    
}