import java.io.File;
import java.io.FileNotFoundException;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.tartarus.snowball.SnowballStemmer;


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
    private Controller dbController;
    private String pathtoDocFolder;
    private AtomicInteger countofdocFiles;
    //regex for excluding delimiters that may be found in html page
    private final String undesiredDelimiters=" |\\.|:|!|\\?|\\)|\\(|,|\\{|\\}|\\]|\\[|/|\\+|\"|_|'|`|\'|'\'";

    /**
     * Search for and load the excluded keywords file once indexer is created
     */
    private Indexer() {
        //configuring the connection with the database
        dbController=new Controller();
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

        pathtoDocFolder=    Paths.get("").toAbsolutePath().toString()+"/documents";
        countofdocFiles=new AtomicInteger(new File(pathtoDocFolder).listFiles().length);




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
    public void run(){

        String url=" ";
        Document doc;
        Elements elements;
      synchronized (this.countofdocFiles){
        while(this.countofdocFiles.decrementAndGet()>=0){

            String FilePath=pathtoDocFolder+"/doc"+countofdocFiles+".txt";
            File CrawlerOutputFile=new File(FilePath);

            Scanner scan=null;
            try  {
                scan = new Scanner(CrawlerOutputFile);
                if(scan.hasNext())
                {  url=scan.next();

                 //  dbController.insertURL(url);
                }

                doc=Jsoup.parse(CrawlerOutputFile,"UTF-8",url);
                elements=doc.getAllElements();
                PageIndexedData pageData = new PageIndexedData(url);
                int counter = 0;   //for each web page for determining the position of the word

                for (Element element:elements){


                    // Filtering Tags
                    if (excludedTags.contains(element.tagName()))
                        continue;

                    // Filtering Keywords
                    for(String keyword:element.text().split(this.undesiredDelimiters)){
                        keyword=this.Stem(keyword);

                        if(keyword.equals(""))
                            continue;
                        counter++;

                        if(!excludedKeywords.contains(keyword)) {
                            pageData.add(keyword,element.tagName(),counter);



                        }
                    }
                }

                //for inserting each page keywords with its related data
                pageData.storeIntoSearchIndex(dbController);

               // TESTING: PRINTING PAGE SUMMARY

              //  System.out.println(this.countofdocFiles);
              //  pageData.print();
            }
            catch(Exception exc){

                exc.printStackTrace();
            }

        }
      }
    }




    /*
    * this function is mainly used for stemming english words using snowball.jar
    **& it converts keywords to LowerCase
     */
    public String Stem(String keyword){

      //  keyword=keyword.toLowerCase().replaceAll("\\s+","");
          keyword=keyword.replaceAll("[^a-zA-Z|\\- ]", "").toLowerCase().trim();
        try{

        Class stemClass=Class.forName("org.tartarus.snowball.ext.englishStemmer");
        SnowballStemmer stemmer=(SnowballStemmer)stemClass.newInstance();
        stemmer.setCurrent(keyword);
        stemmer.stem();
        keyword=stemmer.getCurrent();

        }
        catch(Exception exc){

            exc.printStackTrace();
        }
        return keyword;
    }
}
