package com.searchengine.ws;

import javax.jws.WebService;
import com.google.gson.Gson;
import com.searchengine.queryprocessors.*;

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
        Gson gson = new Gson();
        PhraseSearch p = new PhraseSearch();
        String results = gson.toJson(p.searchForPhrase(query));
        results = results.replace("\\\"","&quot;");
        return results;
    }
}