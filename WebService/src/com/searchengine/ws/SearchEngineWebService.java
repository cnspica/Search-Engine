package com.searchengine.ws;

import javax.jws.WebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        //TODO: Hanaa & Nems Should fill this spot with whatever code they like and return a json object
        PhraseSearch p = new PhraseSearch();

        //TODO: Assuming p.find() returns the result
        String results = gson.toJson(p.searchForPhrase(query));
        results = results.replace("\\\"","&quot;");
        return results;
    }
}