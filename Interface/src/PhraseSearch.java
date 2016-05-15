
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.*;
/**
 * Created by hanaa mohamed on 5/14/16.
 */
public class PhraseSearch {

    private HashSet<String> excludedKeywords;
    private Controller dbController;
    private String pathtoDocFolder;


    public PhraseSearch(){


        File excludedKeywordsFile = new File("Interface/excludedKeywords.txt");
        excludedKeywords = new HashSet<>();

        Scanner excludedKeywordsScanner;
        try {
            excludedKeywordsScanner = new Scanner(excludedKeywordsFile);
            while (excludedKeywordsScanner.hasNext()){
                excludedKeywords.add(excludedKeywordsScanner.next());
            }
        } catch (FileNotFoundException e) {
            System.out.println("EXCLUDED KEYWORDS FILE WAS NOT FOUND: NO KEYWORDS WILL BE EXCLUDED");
        }
        pathtoDocFolder=    Paths.get("").toAbsolutePath().toString()+"/documents/";

        dbController = new Controller();
    }

    public ArrayList<PhraseDocument> searchForMultiplePhrases(String phrase){
        ArrayList<PhraseDocument> urls = new ArrayList<>();
        ArrayList<PhraseDocument> returnedUrls = new ArrayList<>();
        String[] phrases = phrase.split("\"");
        for(String phraseTobeSearched: phrases){
            returnedUrls = searchForPhrase(phraseTobeSearched);

            for(PhraseDocument p: returnedUrls){
                    urls.add(p);
            }
        }
        return urls;
    }

    //temporarily, we will return a string array containing URLs
    public ArrayList<PhraseDocument> searchForPhrase(String phrase){

        System.out.println(phrase);
        ArrayList<PhraseDocument> urls = new ArrayList<>();

        phrase = phrase.toLowerCase();
        String[] keywords = phrase.split("\\s+");
        int keywordsCounter = keywords.length;

        HashMap<String, PhraseDocument> documentsToBeSearched = new HashMap<>();
        boolean excluded = false;


        for(String keyword:keywords){
            excluded = excludedKeywords.contains(keyword);

            if (excluded){
                keywordsCounter--;
            }
            else {
                //this word is not a stop word, so most probably we can find it in the DB
                //trying to reuse the same procedure we created for the normal queries
                ResultSet rset = dbController.getSearchResult(keyword, keyword);

                try{

                    assert(rset!=null);

                    try{
                        while(rset.next()){
                                PhraseDocument doc = documentsToBeSearched.get(rset.getString("doc"));
                                if( doc == null ){
                                    documentsToBeSearched.put(rset.getString("doc"), new PhraseDocument(rset));
                                }
                                else{
                                    doc.counter++;
                                }
                        }
                    }
                    catch(AssertionError exc){

                        return null;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }


            }




        }

       // now we are out with the set of PhraseDocuments we collected
       for (Map.Entry entry: documentsToBeSearched.entrySet()) {
                String doc = (String)entry.getKey();
                if(documentsToBeSearched.get(doc).counter != keywordsCounter){
                        continue;
                }
                String path = pathtoDocFolder + doc;

                //byte[] fileBytes = new byte[(int)file.length()];
                try{
                    byte[] fileBytes = Files.readAllBytes(Paths.get(path));

                    String fileContent = new  String(fileBytes, Charset.defaultCharset()).toLowerCase();

                    int index = fileContent.indexOf(phrase);

                    if(index > -1){
                        urls.add(documentsToBeSearched.get(doc));
                    }

                }catch(Exception exc){
                    exc.printStackTrace();
                }
        }
        return urls;

    }
}


