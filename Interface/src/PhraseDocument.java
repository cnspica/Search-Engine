import java.sql.ResultSet;

/**
 * Created by boomerang on 5/15/16.
 */


public class PhraseDocument{
    public String url;
    public int counter;
    public String title;

    public PhraseDocument(ResultSet rset){
        try{
            this.url = rset.getString("URL");
            this.counter = 1; //when this object is created at least one word was found
            this.title = rset.getString("title");
        }
        catch(Exception exc){
            exc.printStackTrace();
        }
    }
}
