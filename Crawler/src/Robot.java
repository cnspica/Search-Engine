/**
 * Created by hp on 27/03/2016.
 */
import com.trigonic.jrobotx.RobotExclusion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Robot {
    public static void main(String[] args){
        RobotExclusion robotExclusion = new RobotExclusion();
        URL url= null;
        try {
            url = new URL("https://stackexchange.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        boolean m=robotExclusion.allows(url, "*");
        if (m) {
            // do something with url
            System.out.println("you're allowed to fetch");
            try {
                Document doc = Jsoup.connect(url.toString()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
