package com.searchengine.ws;
import com.searchengine.queryprocessors.*;
import com.searchengine.queryprocessors.PhraseDocument;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Author : mostafa
 * Created: 5/15/16
 * Licence: NONE
 */
public class Client {
    private static URL url;
    private static QName qName;
    private static Service service;
    private static SearchEngineInterface searchEngineInterface;

    public Client() throws MalformedURLException {
        url = new URL("http://localhost:9999/search?wsdl");
        qName = new QName("http://ws.searchengine.com/", "SearchEngineWebServiceService");
        service = Service.create(url, qName);
        searchEngineInterface = service.getPort(SearchEngineInterface.class);
    }

    public List<QueryResult> search(String searchString){
        return searchEngineInterface.search(searchString);
    }

    public static void main(String[] args) throws MalformedURLException {
        url = new URL("http://localhost:9999/search?wsdl");
        qName = new QName("http://ws.searchengine.com/", "SearchEngineWebServiceService");
        service = Service.create(url, qName);
        searchEngineInterface = service.getPort(SearchEngineInterface.class);
        System.out.println(((List<QueryResult>)(searchEngineInterface.search("Ruby on Rails"))).get(0).getUrl());
    }
}
