import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by HanaaMohamed on 3/26/16.
 */
public class Controller {
    /*this class is mainly specialized  with handling the transactions with database,using known stored procedures
     * according to the operation we are going to perform but not directly where DBManager is the class that deals with
      * the database directly
      * main functions of this class:
      * sets the database,userName,passowrd parameters for connection
      *
      * constructs up the queries,insert,delete&update required for indexing & updating Search index
      *
      * */
    private DBManager db;
    private static final String DBURL="JDBC:mysql://localhost:3306/search_engine";
    private static final String UserName="root";
    private static final String Pass=null;

    public Controller(){

        this.db=new DBManager(DBURL,UserName,Pass);
    }

    /*
    * temporary method
    * will be erased after integration
    * */
    public void insertURL(String URL){
        String Insertionstmt="call InsertURL('"+URL+"')";
        this.db.ExecuteNonQuery(Insertionstmt);


    }



    /*
    * for inserting keyword in Keywords
    * */
    public boolean insertKeyWord(String keyword)
    {
        String Query="call getKey('"+keyword+"')";
        try {
            if(db.ExecuteQuery(Query).next()){
                return true;
            }
            else{
                String InsertionStmt="call InsertKeyWord('"+ keyword +"')";
                return db.ExecuteNonQuery(InsertionStmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /*
    * for inserting a keyword and URL in SearchIndex
    * a new entry if it doesn't exist
    * if the entry already exists in the database
    * it updates the term frequency and list of locations
    * */
    public boolean insertIntoSearchIndex(String keyword,String Url,int TermFreq,String ListofLocations){

        String Query="call checkKeyURLExistence('"+keyword+"','"+Url+"');";
        ResultSet rset=this.db.ExecuteQuery(Query);

        try {
            if(rset.next()){
                String UpdateStmt="call UpdateSearchIndex('"+keyword+"','"+Url+"',"+TermFreq+",'"+ListofLocations+"');";
                return this.db.ExecuteNonQuery(UpdateStmt);

            }
            else{
                String InsertionStmt="call InsertIntoSearchIndex('"+keyword+"','"+Url+"',"+TermFreq+",'"+ListofLocations+"');";
                return this.db.ExecuteNonQuery(InsertionStmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }




    }



    /*
    * for calculating IDF fr each keyword
    * */
    public void calculateIDF(){

        String Query="call getKeyWords()";
        ResultSet rset=this.db.ExecuteQuery(Query);
        String UpdateIDF;
        try{
            while(rset.next()){
                //  System.out.println(rset.getString("keyword"));
                UpdateIDF="call updateIDF('"+rset.getString("keyword")+"')";
                this.db.ExecuteNonQuery(UpdateIDF);
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public ResultSet queryProcessor(String queryWords){
        String Query="call QueryProcessor('"+ queryWords +"')";

        return this.db.ExecuteQuery(Query);
    }

    public float getIDF(String Keyword){
        String Query="call getIDF('"+Keyword+"')";
        ResultSet rset=db.ExecuteQuery(Query);
        try{
            if(rset.next()){
                if(rset.getString("IDF") != null){
                    return (float)Double.parseDouble(rset.getString("IDF"));
                }

            }

        }
        catch(Exception exc){
            exc.printStackTrace();

        }
        return 0;
    }

    public ResultSet getSearchResult(String Keyword,String stemmedWord){

        String Query="call LoadSearchResults('"+Keyword+"','"+stemmedWord+"')";

        return this.db.ExecuteQuery(Query);

    }




}
