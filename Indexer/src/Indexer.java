import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Author : mostafa mahmoud
 * Email: mostafa_mahmoud@protonmail.com
 * Created: 3/21/16
 * Licence: NONE
 */
public class Indexer implements Runnable{
    private static Indexer indexer;
    private HashSet<String> urls;
    private HashSet<String> excludedKeywords;
    private HashSet<String> excludedTags;
    private final Object urlsLock = new Object();

    /**
     * Search for and load the excluded keywords file once indexer is created
     */
    private Indexer() {
        File excludedKeywordsFile = new File("Indexer/excludedKeywords.txt");
        File excludedTagsFile = new File("Indexer/excludedTags.txt");
        excludedKeywords = new HashSet<>();
        excludedTags = new HashSet<>();

        Scanner excludedKeywordsScanner, excludedTagsScanner;
        try {
            excludedKeywordsScanner = new Scanner(excludedKeywordsFile);
            while (excludedKeywordsScanner.hasNext()){
                excludedKeywords.add(excludedKeywordsScanner.next());
            }
        } catch (FileNotFoundException e) {
            System.out.println("EXCLUDED KEYWORDS FILE WAS NOT FOUND: NO KEYWORDS WILL BE EXCLUDED");
        }

        try {
            excludedTagsScanner = new Scanner(excludedTagsFile);
            while (excludedTagsScanner.hasNext()){
                excludedTags.add(excludedTagsScanner.next());
            }
        } catch (FileNotFoundException e) {
            System.out.println("EXCLUDED TAGS FILE WAS NOT FOUND: NO TAGS WILL BE EXCLUDED");
        }
    }

    /** Facade pattern
     * Use this method to get the instance of the Indexer
     * it creates an instance when first called
     */
    public static Indexer getInstance(){
        if (indexer == null)
            indexer = new Indexer();
        return indexer;
    }

    public void setUrls(HashSet<String> urls) {
        synchronized (urlsLock){
            this.urls = urls;
        }
    }

    /**
     * use only this to check if there are remaining urls
     * Avoid race conditions on urlsLock
     */
    public boolean hasNextURL(){
        synchronized (urlsLock) {
            return urls.iterator().hasNext();
        }
    }

    /**
     * use only this to get next url
     * Avoid race conditions on urlsLock
     */
    public String nextURL(){
        synchronized (urlsLock){
            String url = urls.iterator().next();
            urls.remove(url);
            return url;
        }
    }

    @Override
    public void run() {
        String url;
        Document doc;
        Elements elements;

        while (hasNextURL()) {
            try {
                url = nextURL();
                doc = Jsoup.connect(url).get();
                elements = doc.body().select("*");
                PageIndexedData pageData = new PageIndexedData(url);
                int counter = 0;

                for (Element element:elements){

                    // Filtering Tags
                    if (excludedTags.contains(element.tagName()))
                        continue;

                    // Filtering Keywords
                    for(String keyword:element.text().split(" ")){
                        counter++;
                        if(!excludedKeywords.contains(keyword)) {
                            pageData.add(keyword,element.tagName(),counter);
                        }
                    }
                }

                // TESTING: PRINTING PAGE SUMMARY
                pageData.print();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
