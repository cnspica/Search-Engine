import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

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

    private HashSet<File> crawlerOutputFiles;
    private HashSet<String> excludedKeywords;
    private HashSet<String> excludedTags;
    private final Object filesLock = new Object();
    private String pathtoDocFolder;
    private int countofdocFiles;
    private static final String specialCharacters=" |\\.|:|!|\\?|\\)|\\(|,|\\{|\\}|\\]|\\[|/|\\+|\"|_|'|`|\'|'\'";
    private Controller dbController;

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


        pathtoDocFolder=    Paths.get("").toAbsolutePath().toString()+"/documents";
        File documentsFolder = new File(pathtoDocFolder);
        //countofdocFiles=(new File(pathtoDocFolder).listFiles().length);
        //System.out.println(countofdocFiles);

        crawlerOutputFiles = new HashSet<>();

        assert (documentsFolder.exists() && documentsFolder.isDirectory());

        Collections.addAll(crawlerOutputFiles, documentsFolder.listFiles());

        dbController = new Controller();
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




    /**
     * use this to check if there are remaining files in the documents folder
     * Avoids race conditions
     */
    public boolean hasMoreFiles(){
        synchronized (filesLock) {
            return crawlerOutputFiles.iterator().hasNext();
         //    return countofdocFiles>=0;
        }
    }

    /**
     * use this to get next file
     * Avoids race conditions
     */
    public File nextFile(){
        synchronized (filesLock){
           File file = crawlerOutputFiles.iterator().next();
           crawlerOutputFiles.remove(file);
          System.out.println(this.crawlerOutputFiles.size());
//            countofdocFiles--;
//            String FilePath=pathtoDocFolder+"/doc"+countofdocFiles+".txt";
//            File CrawlerOutputFile=new File(FilePath);
//           System.out.println(this.countofdocFiles);
//            return CrawlerOutputFile;
            return file;
        }
    }

    @Override
    public void run(){

        String url;
        Document doc;
        Elements elements;
        String Stemmedword;
        while(hasMoreFiles()){

            try {
                File file = nextFile();
                Scanner scanner = new Scanner(file);
                url = scanner.nextLine();
                doc = Jsoup.parse(file, "UTF-8", url);
                elements = doc.getAllElements();
                PageIndexedData pageData = new PageIndexedData(url);
                int counter = 0;
                for (Element element : elements) {

                    // Filtering Tags
                    if (excludedTags.contains(element.tagName()))
                        continue;

                    // Filtering Keywords
                    for (String keyword : element.text().split(specialCharacters)) {


                        keyword = formatKeyword(keyword);


                        if (keyword.equals(""))
                            continue;
                        counter++;

                        if (!excludedKeywords.contains(keyword)) {
                            //perform full stemming
                            Stemmedword=Stem(keyword);
                            pageData.add(keyword, element.tagName(), counter);

                            if(!Stemmedword.equals(keyword)){
                                pageData.add(Stemmedword, element.tagName(), counter);
                            }
                        }
                    }
                }

                //for inserting each page keywords with its related data
              pageData.storeIntoSearchIndex(dbController);


                // TESTING: PRINTING PAGE SUMMARY
                //  pageData.print();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //this.dbController.calculateIDF();

    }

    public String formatKeyword(String Keyword){

        return Keyword.replaceAll("[^a-zA-Z|\\- ]", "").toLowerCase().trim();
    }


    /**
     * This function is mainly used for stemming english words
     */
    public String Stem(String keyword){
        ///
      //  keyword=keyword.toLowerCase().replaceAll("\\s+","");

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
