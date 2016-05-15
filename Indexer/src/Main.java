

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;


/**
 * Author : mostafa
 * Created: 3/21/16
 * Licence: NONE
 */
public class Main {



    public static void main(String[] args) {


       //HashSet<String> urls = new HashSet<>();
      // Indexer indexer = Indexer.getInstance();
//       int numberOfThreads;
//
//
//
//
//
//        //Feeding the indexer
//
//        numberOfThreads = 8;
//        Thread[] threads = new Thread[numberOfThreads];
//        for (int i = 0; i < numberOfThreads; i++) {
//            threads[i] = new Thread(Indexer.getInstance());
//            threads[i].start();
//        }
//
//        Controller c = new Controller();
//        c.calculateIDF();

         QueryProcessor processor = new QueryProcessor();
         ArrayList<QueryResult> result = processor.find("contact tutorial ruby on rails");

        for(QueryResult q: result){
            System.out.println(q.getUrl());
            System.out.println(q.getSnippet());
            System.out.println("_______________________________________________________________________");
        }


        //testing retrieval from the index
//        SearchResult SR=new SearchResult("contact",new Controller());
//        SR.Print();

    }
}
