package com.example.uiuccampusevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class HtmlDownloader extends AsyncTask<String, Void, String[]>{

	private static String init = "<div class=\"place-on-screen\"";				  		//Skips all HTML up to this tag
	private static String eventIdentifierA = "<li class=\"odd-item\">";			  		//One possible tag identifier for event
	private static String eventIdentifierB = "<li class=\"even-item\">";		  		//Other identifier tag for event
	private static String splitLine1 = "<span class=\"event-name\"><a href=\".+\">";	//Tag of first split in line
	private static String splitLine2 = "</a>";											//Tag of second split

	protected String[] doInBackground(String... url)
	{
		String[] result = new String[1];
	    HttpClient httpclient = new DefaultHttpClient();

	    // Prepare a request object
	    HttpGet httpget = new HttpGet(url[0]); 

	    // Execute the request
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        // Examine the response status
	        Log.i("HTTP",response.getStatusLine().toString());

	        // Get hold of the response entity
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        // to worry about connection release

	        if (entity != null) {

	            // A Simple JSON Response Read
	            InputStream instream = entity.getContent();
	            result= scraper(instream);
	            // now you have the string representation of the HTML request
	            instream.close();
	        }
	    }
	    catch (Exception e) {
	    	//this.exception = e;
	    	 e.printStackTrace();
	    	return null;
        }
	    
	    return result;
	}

	    private static String[] scraper(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
    	//Array of 200 events at max
	    int length = 50;
    	String[] events = new String[length];
    	String[] temp = new String[2];
    	boolean start = false;
    	int i = 0;	
	    
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	        	while(!start){
	    			line = reader.readLine();
	    			line.trim();
	    			if (line.contains(init)) start = true;
	    		}
	        	while (start && i < length && line != null){
	    			line.trim();
	    			if (line.contains(eventIdentifierA) || line.contains(eventIdentifierB)){
	    				line = reader.readLine();
	    				temp = line.split(splitLine1);
	    				if(temp.length == 2){  //Checks length to make sure that the line actually split at reg1
	    					temp = temp[1].split(splitLine2);
	    					events[i] = temp[0];
	    					i ++;
	    				}
	    				line = reader.readLine();
	    			}
	    			else{
	    				line = reader.readLine();
	    			}
	    		}
	        	if (i < events.length){
	    			String[] oldEvents = events;
	    			events = new String[i];
	    			for (int x = 0; x < events.length; x++){
	    				events[x] = oldEvents[x];
	    			}
	    		}
	        	/*for (int j=0; j < 20; j ++){
	        		events[j] = line;
	        	}*/
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return events;
	}

	    protected void onPostExecute(String[] s) {
	    	super.onPostExecute(s);
	    }


}
