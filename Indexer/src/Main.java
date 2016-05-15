

import java.util.HashSet;


/**
 * Author : mostafa
 * Created: 3/21/16
 * Licence: NONE
 */
public class Main {



    public static void main(String[] args) {


       //HashSet<String> urls = new HashSet<>();
      // Indexer indexer = Indexer.getInstance();
       int numberOfThreads;





        //Feeding the indexer

//        numberOfThreads = 8;
//        Thread[] threads = new Thread[numberOfThreads];
//        for (int i = 0; i < numberOfThreads; i++) {
//            threads[i] = new Thread(Indexer.getInstance());
//            threads[i].start();
//        }

           Controller c =new Controller();
           c.calculateIDF();
        //testing retrieval from the index
//        SearchResult SR=new SearchResult("contact",new Controller());
//        SR.Print();

    }
}
