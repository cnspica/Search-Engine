import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * For testing purposes
     */
    public void print(){
        System.out.println("PAGE URL: " + url);
        for (Map.Entry entry: mapper.entrySet()) {
            KeywordData value = (KeywordData)entry.getValue();
            System.out.println(entry.getKey() + " " + value.getPositions().size()
                                + " "+ value.getPositions()+" " + value.getTags());
        }
    }
}

class KeywordData{
    private ArrayList<Integer> positions;
    private ArrayList<String> tags;

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public KeywordData(){
        positions = new ArrayList<>();
        tags = new ArrayList<>();
    }

    public KeywordData addPosition(int pos, String tag){
        positions.add(pos);
        tags.add(tag);
        return this;
    }
}