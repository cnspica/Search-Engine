/**
 * Created by hanaa mohamed on 3/29/16.
 */

import org.tartarus.snowball.SnowballStemmer;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * this data structure is mainly used for retrieving data from a database
 * where it has mainly one keyword and its morphological root after stemming,its IDF
 * and an arraylist of postings list where a postings list usually contains the URL
 * TF for the keyword, TF for the stemmed word,positions of occurence for the original
 * and the stemmed one
 *
 * */

public class SearchResult {

    private String Keyword;
    private String fullyStemmedWord;
    private ArrayList<PostingsList> StemmedPList;
    private ArrayList<PostingsList> keywordPList;
    private float StemmedIDF;
    private float KeywordIDF;

    public  SearchResult(String key,Controller C){

        LoadObjectFromDB(key,C);
    }

    public void LoadObjectFromDB(String keyword,Controller C){

        this.Keyword=ProcessKeyword(keyword);
        this.fullyStemmedWord=stemKeyWord(keyword);
        StemmedPList=new ArrayList<PostingsList>();
        keywordPList=new ArrayList<PostingsList>();
        StemmedIDF=C.getIDF(fullyStemmedWord);
        KeywordIDF=C.getIDF(Keyword);


        ResultSet rset=C.getSearchResult(Keyword,fullyStemmedWord);



        try{

            assert(rset!=null);
            try{


                while(rset.next()){
                    PostingsList P=new PostingsList(rset);

                    if(rset.getString("keyword").equals(this.Keyword)){
                    //call a function to add a new PostingsList to keywordPList
                     keywordPList.add(P);
                    }
                    else if(rset.getString("keyword").equals(this.fullyStemmedWord)){
                    ////call a function to add a new PostingsList to StemmedPList
                    StemmedPList.add(P);


                    }

                }
            }
            catch(AssertionError exc){

                System.out.println("No results Found");}
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void Print(){
        System.out.println(this.Keyword+" : "+ KeywordIDF);
        System.out.println(this.fullyStemmedWord+" : "+StemmedIDF);

        for(PostingsList e:keywordPList){
            e.print();
        }

        System.out.println("StemmedPList");
        for(PostingsList e:StemmedPList){
            e.print();
        }
    }



    public String ProcessKeyword(String Kword){
        return Kword.replaceAll("[^a-zA-Z|\\- ]", "").toLowerCase().trim();
    }

    public String stemKeyWord(String kword){
        try{

            Class stemClass=Class.forName("org.tartarus.snowball.ext.englishStemmer");
            SnowballStemmer stemmer=(SnowballStemmer)stemClass.newInstance();
            stemmer.setCurrent(kword);
            stemmer.stem();
            kword=stemmer.getCurrent();

        }
        catch(Exception exc){

            exc.printStackTrace();
        }
        return kword;
    }
}


class PostingsList{

    private int TF;
    private String URL;
    private ArrayList<String>ListofLocations;

    public PostingsList(ResultSet rset){
        setPostingsList(rset);
    }
    public void setPostingsList(ResultSet rset)  {
        ListofLocations=new ArrayList<String>();

        try{
            this.TF=Integer.parseInt(rset.getString("TF"));
            this.URL=rset.getString("URL");
            String Locations=rset.getString("ListofLocations");

            Locations=Locations.replaceAll("-","");

            for(String Position:Locations.split(",")){
                ListofLocations.add(Position);
            }


        }
        catch(Exception exc){
            exc.printStackTrace();
        }

    }
    public int getTF(){
        return this.TF;
    }

    public String getURL(){
        return this.URL;
    }

    public ArrayList<String> listoflocs(){
        return ListofLocations;
    }

    public void print(){
        System.out.println("TF : "+this.getTF());
        System.out.println("URL :"+this.getURL());
        System.out.print("Locations : ");
        System.out.println(listoflocs().toString());
        System.out.println();
    }
}