package com.appspot.jwebnotifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Text;

public class Control 
{

	//Obtiene el código fuente de la web indicada en la url y elimina código indeseado
	public static Text getCode(String url)
    {
    	String code = "";
        try {
            //Realizamos la petición web mediante la clase URL
            String HTTPString = url;
            URL theURL = new URL(HTTPString);
            InputStream inStream = theURL.openStream( );
            BufferedReader input = new BufferedReader(new InputStreamReader(inStream));

            //Obtenemos el código HTML de la página web
            String response;
            response = input.readLine();
            //Read and display one line at a time
            while (response != null) {
                code = code + response;
                response = input.readLine();
            }
        }
        catch (Exception e) {}
        
        //Elimino el código script, html y los espacios de code1
        code = code.replaceAll("\\/\\*.*?\\*\\/","");
        code = code.replaceAll("<script.*?</script>","");
        code = code.replaceAll("<.*?>","");
        code = code.replaceAll("\\p{Blank}","");
        
        //Elimino cualquier referencia a la hora y a usuarios conectados
        code = code.replaceAll("[0-9].*?segundos","");
        code = code.replaceAll("[0-9].*?segundo","");
        code = code.replaceAll("[0-9].*?minutos","");
        code = code.replaceAll("[0-9].*?minuto","");
        code = code.replaceAll("[0-9].*?horas","");
        code = code.replaceAll("[0-9].*?hora","");
        code = code.replaceAll("Usuarios.*?invitados","");
        
        //Elimino tildes
        code = code.replaceAll("Á","A");
        code = code.replaceAll("É","E");
        code = code.replaceAll("Í","I");
        code = code.replaceAll("Ó","O");
        code = code.replaceAll("Ú","U");
        code = code.replaceAll("á","a");
        code = code.replaceAll("é","e");
        code = code.replaceAll("í","i");
        code = code.replaceAll("ó","o");
        code = code.replaceAll("u","u");
        
        return new Text(code);        
    }
	
	//Manda email a la dirección indicada informando de la modificación de la web indicada
	public static void sendMail(String email, String url)
	{
		try
		{
			Message msg = new MimeMessage(Session.getDefaultInstance(new Properties(), null));
	        msg.setFrom(new InternetAddress("admin@jwebnotifier.appspotmail.com", "JWebNotifier"));
	        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
	        msg.setSubject("Web modificada");
	        msg.setText("Le informamos de que la página web con url " + url + " ha sido modificada");
	        Transport.send(msg);
		}	 
		catch (Exception ex) {}		
	}
	
	//Crea un nuevo registro en la tabla de notificaciones con el email y url indicados
	public static void insertNotification(String email, String url, int frequency)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity employee = new Entity("Notification");
        employee.setProperty("email", email);
        employee.setUnindexedProperty("url", url);
        employee.setProperty("frequency", frequency);
        employee.setUnindexedProperty("code", getCode(url));        
        datastore.put(employee); 	
	}	
	
	//Comprueba la modificación o no de la web de cada registro y en caso positivo, lo notifica por email
	public static void doNotification(int frequency)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filter = new FilterPredicate("frequency", FilterOperator.EQUAL, frequency);
		Query q = new Query("Notification").setFilter(filter);
		PreparedQuery pq = datastore.prepare(q);
		for (Entity result : pq.asIterable()) 
		{
		  String email = (String)result.getProperty("email");
		  String url = (String)result.getProperty("url");
		  Text code = (Text)result.getProperty("code");
		  if (!getCode(url).equals(code))
		  {		  
			  sendMail(email, url);
			  datastore.delete(result.getKey());
		  }
		}
	}
	
}