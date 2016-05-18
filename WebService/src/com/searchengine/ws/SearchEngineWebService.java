package com.searchengine.ws;
import javax.jws.WebService;
import com.searchengine.queryprocessors.*;

import java.util.List;

/**
 * Author : mostafa
 * Created: 5/14/16
 * Licence: NONE
 */

@WebService(endpointInterface = "com.searchengine.ws.SearchEngineInterface")
public class SearchEngineWebService implements SearchEngineInterface{

    @Override
    public List<QueryResult> search(String query)
    {
       QueryProcessor p = new QueryProcessor();

        // Use the other Search Engine Parts to get the results of the Search then return
        return p.find(query);
    }
}