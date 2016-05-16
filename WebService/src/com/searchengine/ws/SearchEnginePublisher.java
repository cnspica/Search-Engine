package com.searchengine.ws;
import javax.xml.ws.Endpoint;

/**
 * Author : mostafa
 * Created: 5/15/16
 * Licence: NONE
 */
public class SearchEnginePublisher {
    public static void main(String[] args) {
        Endpoint endpoint = Endpoint.publish("http://localhost:9999/search", new SearchEngineWebService());
        System.out.println("Search engine service is " + (endpoint.isPublished() ? "" : "not ") + "published");
    }
}
