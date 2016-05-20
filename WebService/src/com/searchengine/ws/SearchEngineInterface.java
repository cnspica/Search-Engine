package com.searchengine.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Author : mostafa
 * Created: 5/15/16
 * Licence: NONE
 */

@WebService
public interface SearchEngineInterface {
    @WebMethod
    String search(String query);
}
