import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Author : mostafa
 * Created: 3/21/16
 * Licence: NONE
 */
public class Main {

    public static void main(String[] args) {

        File crawlerOutputFile = new File("Crawler/Results.txt");
        HashSet<String> urls = new HashSet<>();
        Indexer indexer = Indexer.getInstance();
        int numberOfThreads;

        Scanner scanner = null;
        try {
            scanner = new Scanner(crawlerOutputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Feeding the indexer
        assert scanner != null;
        while (scanner.hasNext()){
            urls.add(scanner.next());
        }
        indexer.setUrls(urls);
        scanner.close();

        //Getting number of threads
        scanner = new Scanner(System.in);
        System.out.print("How many threads do you want ? ");
        numberOfThreads = scanner.nextInt();
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(Indexer.getInstance());
            threads[i].start();
        }
    }
}
