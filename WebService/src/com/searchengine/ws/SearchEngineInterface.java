package com.searchengine.ws;

import com.searchengine.queryprocessors.PhraseDocument;
import com.searchengine.queryprocessors.QueryResult;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

/**
 * Author : mostafa
 * Created: 5/15/16
 * Licence: NONE
 */

@WebService
public interface SearchEngineInterface {
    @WebMethod
    List<QueryResult> search(String query);
}
