package com.example.uiuccampusevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class HtmlDownloader extends AsyncTask<String, Void, String[][]> {
	
	//Identifiers for mainScraper
	private static String init = "<div class=\"place-on-screen\"";
	private static String eventIdentifierA = "<li class=\"odd-item\">";
	private static String eventIdentifierB = "<li class=\"even-item\">";
	private static String titleTag = "<span class=\"event-name\"><a href=\".+skinId=1\">";
	private static String endTitle = "</a>";
	private static String timeTag = "<span class=\"event-time\">";
	private static String endTime = "</span>";
	private static String urlTag = "<a href=\"http://illinois.edu";
	private static String endUrl = "\">";
	private static String dateTag = "<h3 class=\"dayHeader corners-top h-label\">";
	
	//Identifiers for detailScraper
	private static String detailInit = "<div id=\"event-wrapper\">";  //""<h1 class=\"search-cal-name mobile-hide\" id=\"main_content\">";
	private static String detailTitleTag = "<h2 class=\"detail-title\">";
	private static String detailEndTitle = "</h2>";
	private static String detailDateTag = "<span class=\"column-label\">Date </span><span class=\"column-info\">";
	private static String detailTimeTag = "<span class=\"column-label\">Time </span><span class=\"column-info\">";
	private static String detailEndTime = " &nbsp; </span>";
	private static String detailSponsorTag = "<span class=\"column-label\">Sponsor </span><span class=\"column-info\">";
	private static String detailEndDetails = "</span>";
	private static String detailDescriptionTag = "<div class=\"description-row\">";
	private static String detailEndDescription = "</div>";
	private static String detailLocationTag = "<span class=\"column-label\">Location </span><span class=\"column-info\">";
	private static String detailCostTag = "<span class=\"column-label\">Cost </span><span class=\"column-info\">";
	
	private static String[] months = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
	private static String[] monthAbbr = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	
	protected String[][] doInBackground(String... url) {
		String[][] result = new String[1][5];
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
				if (url[0].contains("/calendar/list/")){
					result = mainScraper(instream);
				}
				else if (url[0].contains("/calendar/detail/")){
					result = detailScraper(instream);
				}
				Log.i("URL", url[0]);
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


	
	private static String[][] mainScraper(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * be returned as String[][].
		 */
		// Array of (length) events to be loaded from HTML
		int length = 200;
		String[][] events = new String[length][5];
		boolean start = false;
		int i = 0;

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				String day = "01";
				String month = "Jan";
				
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
						time = time.replaceAll("&nbsp;", " ");
						if (time.length() >= 20) time = time.substring(0,20).trim();
						String url = extract(event, urlTag, endUrl);
						url = "http://illinois.edu" + url;
						String title = extract(event, titleTag, endTitle);
						title = title.replaceAll("&amp;", "&");
						
						events[i][0] = title;
						events[i][1] = time;
						events[i][2] = month;
						events[i][3] = day;
						events[i][4] = url;
						i ++;	
					} 
					
					else if (line.contains(dateTag)){
						String temp = extract(line, ", ", ",");
						String[] tempArr = temp.split("&nbsp;");
						month = tempArr[0].toLowerCase();
						for (int j=0; j<months.length; j++){
							if (month.equals(months[j])) month = monthAbbr[j];
						}
						day = tempArr[1];
						if (day.length() < 2) day = "0" + day;
						line = reader.readLine();
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
			}
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		} 
		
		finally {
			try {
				is.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return events;
	}
	

	
	private static String[][] detailScraper(InputStream is) {
		/*
		 * Array details = [title, date, time, location, sponsor, contact, email, phone, cost, description]
		 */
		String[][] details = new String[1][10];
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		boolean start = false;
		int i = 0;
		
		String line = null;
		String event = "";
		
		try {
			while ((line = reader.readLine()) != null) {	
				while (!start) {
					line = reader.readLine();
					line.trim();
					if (line.contains(detailInit))
						start = true;
				}
				while (start && i < 1 && line != null) {
					line = reader.readLine();
	
					while (!line.contains("<script type")){
						event += line;
						line = reader.readLine();
						line.trim();
					}
					
					i ++;
				}
			}
			String title = extract(event, detailTitleTag, detailEndTitle);
			if (title.contains("<a href=")){
				title = extractFromUrl(title);
			}
			String date = extract(event, detailDateTag, detailEndDetails);
			String time = extract(event, detailTimeTag, detailEndTime);
			String sponsor = extract(event, detailSponsorTag, detailEndDetails);
			if (sponsor.contains("<a href=")){
				sponsor = extractFromUrl(sponsor);
			}
			String description = "";
			if (event.contains(detailDescriptionTag)){
				description = extract(event, detailDescriptionTag, detailEndDescription);
			}
			String location = "";
			if (event.contains(detailLocationTag)){
				location = extract(event, detailLocationTag, detailEndDetails);
			}
			String cost = "";
			if (event.contains(detailCostTag)){
				cost = extract(event, detailCostTag, detailEndDetails);
			}
			
			details[0][0] = title;
			details[0][1] = date;
			details[0][2] = time;
			details[0][3] = sponsor;
			details[0][4] = location;
			details[0][8] = cost;
			details[0][9] = description;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return details;
	}

	private static String extract(String line, String start, String end){
		String[] temp = line.split(start);
		if (temp.length > 1) temp = temp[1].split(end);
		return temp[0];	
	}
	
	private static String extractFromUrl(String line){
		return extract(line, "<a href=.+\">", "</a>");
	}
	
	protected void onPostExecute(String[][] s) {
		super.onPostExecute(s);
	}

}
