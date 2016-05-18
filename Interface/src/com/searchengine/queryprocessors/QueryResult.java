package com.searchengine.queryprocessors;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by hp on 15/05/2016.
 */


public class QueryResult {
    private String url;
    private String snippet;

    private String title;

    public QueryResult(ResultSet rset, String path){
        try {
            this.url = rset.getString("URL");
            this.title = rset.getString("title");
            String doc = rset.getString("doc");
            String keyword = rset.getString("keyword");
            path += "/" + doc;
            File f = new File(path);
            byte[] fileBytes = Files.readAllBytes(Paths.get(path));
            String bodyContent = Jsoup.parse(f, "UTF-8" , this.url).body().text();

            int index = bodyContent.indexOf(keyword);
            int start = Math.max(0, index - 100);
            int end = Math.min(bodyContent.length()-1, index+100);
            this.snippet = bodyContent.substring(start, end);
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
