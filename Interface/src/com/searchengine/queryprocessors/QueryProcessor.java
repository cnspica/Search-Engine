package com.searchengine.queryprocessors;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.ArrayList;



import org.tartarus.snowball.SnowballStemmer;

/**
 * Created by hp on 15/05/2016.
 */
public class QueryProcessor {
    private String pathtoDocFolder;
    private Controller dbController;


    public QueryProcessor(){
                dbController = new Controller();
                pathtoDocFolder = Paths.get("").toAbsolutePath().toString()+"/documents";
    }


    public String preprocessWord(String word){
        word = word.replaceAll("[^a-zA-Z|\\- ]", "").toLowerCase().trim();
        // stemming part
        try{

            Class stemClass=Class.forName("org.tartarus.snowball.ext.englishStemmer");
            SnowballStemmer stemmer=(SnowballStemmer)stemClass.newInstance();
            stemmer.setCurrent(word);
            stemmer.stem();
            word = stemmer.getCurrent();

        }
        catch(Exception exc){
            exc.printStackTrace();
        }
              return word;
        }

    public ArrayList<QueryResult> find(String query){

        ArrayList<QueryResult> results = new ArrayList<QueryResult>();
        String[] keywords = query.split("\\s+");
        String queryKeywords = "";


        for (String Key: keywords){
            queryKeywords += "\\'" + Key + "\\',";
            queryKeywords += "\\'" + preprocessWord(Key) + "\\',";
        }

        queryKeywords = queryKeywords.substring(0,queryKeywords.length()-1);

        ResultSet rset = dbController.queryProcessor(queryKeywords);
        try{

            assert(rset!=null);
            try{
                while(rset.next()){
                        results.add(new QueryResult(rset, pathtoDocFolder));
                }
            }
            catch(AssertionError exc){
                System.out.println("No results Found");}
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return results;
    }
}
