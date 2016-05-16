package com.searchengine.ws;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

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

    public String search(String query){
        return searchEngineInterface.search(query);
    }

    public static void main(String[] args) throws MalformedURLException {
        url = new URL("http://localhost:9999/search?wsdl");
        qName = new QName("http://ws.searchengine.com/", "SearchEngineWebServiceService");
        service = Service.create(url, qName);
        searchEngineInterface = service.getPort(SearchEngineInterface.class);
        System.out.println(searchEngineInterface.search("Query"));
    }
}
