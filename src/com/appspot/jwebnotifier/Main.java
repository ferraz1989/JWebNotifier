package com.appspot.jwebnotifier;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class Main extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {       
    	//Obtenemos los parámetros
        String email = request.getParameter("email");
        String url = request.getParameter("web");
        int frequency = Integer.valueOf(request.getParameter("frequency"));
        
        //Llamamos al método de control
        if (!email.isEmpty() && !url.isEmpty())
        	Control.insertNotification(email, url, frequency);                   
             
        //Redireccionamos a la web inicial
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<meta HTTP-EQUIV='REFRESH' content='0; url=../index.html'>");        
        out.close();
    }    
    
}