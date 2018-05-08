/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nytimesviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.simple.*;

/**
 *
 * @author dale
 */
public class NYTNewsStory {
    public String webUrl;
    public String headline;
    public String snippet;
    public String leadParagraph;
    public String newsDesk;
    public String sectionName;
    public String source;
    
    private static String urlString = "";
    // sample url:
    //private String urlString = "http://api.nytimes.com/svc/search/v2/articlesearch.json?q=Microsoft&api-key=1bd23e3003632f4c95bc0ff5ea313c29:14:71568150";
   
    // NOTE!!  The api key below is Dale Musser's api key.  If you build an app that uses the New York Times API
    // get your own api key!!!!!  Get it from: http://developer.nytimes.com
    // I also cannot guarantee that the api key provided will be valid in the future.
    private static final String baseUrlString = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    private static final String apiKey = "1bd23e3003632f4c95bc0ff5ea313c29:14:71568150";
    private static String searchString = "University of Missouri";
    private static URL url;
    private static ArrayList<NYTNewsStory> newsStories = new ArrayList<>();
    
    public NYTNewsStory(String webUrl, String headline, String snippet, 
            String leadParagraph, String newsDesk, String sectionName, String source ) {
        this.webUrl = webUrl;
        this.headline = headline;
        this.snippet = snippet;
        this.leadParagraph = leadParagraph;
        this.newsDesk = newsDesk;
        this.sectionName = sectionName;
    }
    
    public static void load(String searchString) throws Exception {
        if (searchString == null || searchString.equals("")) {
            throw new Exception("The search string was empty.");
        }
        
        NYTNewsStory.searchString = searchString;
        
        // create the urlString
        String encodedSearchString;
        try {
            // searchString must be URL encoded to put in URL
            encodedSearchString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw ex;
        }
        
        urlString = baseUrlString + "?q=" + encodedSearchString + "&api-key=" + apiKey;
        
        try {
            url = new URL(urlString);
        } catch(MalformedURLException muex) {
           throw muex;
        }
        
        String jsonString = "";
        try {
            BufferedReader in = new BufferedReader(
            new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                jsonString += inputLine;
            in.close();
        } catch (IOException ioex) {
            throw ioex;
        }
        
        try {
            parseJsonNewsFeed(jsonString);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private static void parseJsonNewsFeed(String jsonString) throws Exception {
        
        // start with clean list
        newsStories.clear();
        
        if (jsonString == null || jsonString == "") return;
        
        JSONObject jsonObj;
        try {
            jsonObj = (JSONObject)JSONValue.parse(jsonString);
        } catch (Exception ex) {
            throw ex;
        }
        
        if (jsonObj == null) return;
        
        String status = "";
        try {
            status = (String)jsonObj.get("status");
        } catch (Exception ex) {
            throw ex;
        }
        
        if (status == null || !status.equals("OK")) {
            throw new Exception("Status returned from API was not OK.");
        }
        
        JSONObject response;
        try {
            response = (JSONObject)jsonObj.get("response");
        } catch (Exception ex) {
            throw ex;
        }
        
        JSONArray docs;
        try {
            docs = (JSONArray)response.get("docs");
        } catch (Exception ex) {
            throw ex;
        }
      
        for (Object doc : docs) {
            try {
                JSONObject story = (JSONObject)doc;
                String webUrl = (String)story.getOrDefault("web_url", "");
                String snippet = (String)story.getOrDefault("snippet", "");
                String leadParagraph = (String)story.getOrDefault("lead_paragraph", "");
                String source = (String)story.getOrDefault("source", "");
                String newsDesk = (String)story.getOrDefault("news_desk", "");
                String sectionName = (String)story.getOrDefault("section_name", "");
                JSONObject headlineObj = (JSONObject)story.getOrDefault("headline", null);
                String headline = "";
                if (headlineObj != null) {
                    headline = (String)headlineObj.getOrDefault("main", "");
                }
                
                System.out.println("headline: " + headline + "\n");
                System.out.println("webUrl: " + webUrl + "\n");
                System.out.println("snippet: " + snippet + "\n");
                System.out.println("leadParagraph: " + leadParagraph + "\n");
                System.out.println("newsDesk: " + newsDesk + "\n");
                System.out.println("sectionName: " + sectionName + "\n");
                System.out.println("source: " + source + "\n");
                System.out.println("------------------------------------------------------\n");
                
                NYTNewsStory newsStory = new NYTNewsStory(webUrl, headline, snippet, leadParagraph, newsDesk, sectionName, source );
                newsStories.add(newsStory);
                
            } catch (Exception ex) {
                throw ex;
            }
            
        }
        
    }
    
    public static ArrayList<NYTNewsStory> getNewsStories() {
        return newsStories;
    }
    
    public static int getNumNewsStories() {        
        return newsStories.size();
    }
    
    
}
