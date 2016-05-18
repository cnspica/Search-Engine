/**
 * Created by boomerang on 5/15/16.
 */
import java.util.*;
import com.searchengine.queryprocessors.*;
public class test {



           public static void main(String[] args){
               String phrase = "Role-Playing Games";
               PhraseSearch p = new PhraseSearch();
               ArrayList<PhraseDocument> t = p.searchForPhrase(phrase);

               for(PhraseDocument y : t){
                   System.out.println(y.url);
               }


           }

}
