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

public class HtmlDownloader extends AsyncTask<String, Void, String[][]> {

	// Skips all HTML up to this tag
	private static String init = "<div class=\"place-on-screen\"";
	// One possible tag identifier for event
	private static String eventIdentifierA = "<li class=\"odd-item\">";
	private static String eventIdentifierB = "<li class=\"even-item\">";
	private static String titleTag = "<span class=\"event-name\"><a href=\".+skinId=1\">";
	private static String endTitle = "</a>";
	private static String timeTag = "<span class=\"event-time\">";
	private static String endTime = "</span>";
	private static String urlTag = "<a href=\"http://illinois.edu";
	private static String endUrl = "\">";

	protected String[][] doInBackground(String... url) {
		String[][] result = new String[1][4];
		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object
		HttpGet httpget = new HttpGet(url[0]);

		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			// Examine the response status
			Log.i("HTTP", response.getStatusLine().toString());

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				result = mainScraper(instream);
				// now you have the string representation of the HTML request
				instream.close();
			}
		} catch (Exception e) {
			// this.exception = e;
			e.printStackTrace();
			return null;
		}

		return result;
	}

	
	private static String[][]  mainScraper(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		// Array of (length) events to be loaded from HTML
		int length = 50;
		String[][] events = new String[length][4];
		String[] temp = new String[2];
		boolean start = false;
		int i = 0;

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				while (!start) {
					line = reader.readLine();
					line.trim();
					if (line.contains(init))
						start = true;
				}
				while (start && i < length && line != null) {
					line.trim();
					if (line.contains(eventIdentifierA) || line.contains(eventIdentifierB)) {
						line = reader.readLine();
						String event = "";
						while (!line.equals("</li>")){
							event += line;
							line = reader.readLine();
						}

						String time = extract(event, timeTag, endTime);
						String url = extract(event, urlTag, endUrl);
						url = "http://illinois.edu" + url;
						String title = extract(event, titleTag, endTitle);
						events[i][0] = title;
						events[i][1] = time;
						events[i][3] = url;
						i ++;	
					} 
					else {
						line = reader.readLine();
					}
				}
				if (i < events.length) {
					String[][] oldEvents = events;
					events = new String[i][4];
					for (int x = 0; x < events.length; x++) {
						events[x] = oldEvents[x];
					}
				}
				/*
				 * for (int j=0; j < 20; j ++){ events[j] = line; }
				 */
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
	
	
	private static String[][]  detailScraper(InputStream is) {
		/*
		 * TODO: scrape details from page in HTML
		 */
		return new String[0][0];
	}

	private static String extract(String line, String start, String end){
		String[] temp = line.split(start);
		if (temp.length > 1) temp = temp[1].split(end);
		return temp[0];	
	}
	
	
	
	protected void onPostExecute(String[][] s) {
		super.onPostExecute(s);
	}

}
