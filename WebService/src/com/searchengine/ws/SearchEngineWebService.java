package com.searchengine.ws;
import javax.jws.WebService;
/**
 * Author : mostafa
 * Created: 5/14/16
 * Licence: NONE
 */

@WebService(endpointInterface = "com.searchengine.ws.SearchEngineInterface")
public class SearchEngineWebService implements SearchEngineInterface{

    @Override
    public String search(String query)
    {
        // Use the other Search Engine Parts to get the results of the Search then return
        return query;
    }
}