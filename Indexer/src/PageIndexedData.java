
import java.util.*;

/**
 * Author : mostafa
 * Created: 3/22/16
 * Licence: NONE
 */

/**
 * This Class serves as a data structure holding the data extracted from a page
 */
public class PageIndexedData {
    private HashMap<String,KeywordData> mapper;
    private String url;

    public PageIndexedData(String url) {
        mapper = new HashMap<>();
        this.url = url;
    }

    public void add(String keyword, String tag, int position){
        KeywordData data = mapper.get(keyword);
        if(data == null){
            mapper.put(keyword,new KeywordData().addPosition(position,tag));
        }
        else{
            data.addPosition(position,tag);
        }
    }

    public void storeIntoSearchIndex(Controller c){

     for(String Keyword:mapper.keySet()){
            /*insert the keyword first if it was not inserted*/
           c.insertKeyWord(Keyword);

           /*insert the keyword occurency in the Search index
           * keywordID,URLID,TermFreq,ListofLocations
           * */
           c.insertIntoSearchIndex(Keyword,url,mapper.get(Keyword).getPositions().size(),mapper.get(Keyword).formatForStoringinDB());
       //  System.out.println(mapper.get(Keyword).formatForStoringinDB());
      }
    }
    /**
     * For testing purposes
     */
    public void print(){
        System.out.println("PAGE URL: " + url);
        for (Map.Entry entry: mapper.entrySet()) {
            KeywordData value = (KeywordData)entry.getValue();
            System.out.println(entry.getKey() + " " + value.getPositions().size()+
                               value.getPositions() + " " + value.getTags());
        }
    }
}

class KeywordData{
    private ArrayList<Integer> positions;
    private HashSet<String> tags;

    public HashSet<String> getTags() {
        return tags;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public KeywordData(){
        positions = new ArrayList<>();
        tags = new HashSet<>();
    }

    public KeywordData addPosition(int pos, String tag){

        positions.add(pos);
        tags.add(tag);
        return this;
    }

    public String formatForStoringinDB(){

        return this.getTags().toString();

    }

}