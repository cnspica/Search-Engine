import java.net.InetAddress;
import java.net.URL;

/**
 * Created by hp on 27/03/2016.
 */
public class Lock {
    public int lock;
    public static void main(String[] args){
        try {
            String url = "http://stackoverflow.com/questions/9286861/get-ip-address-with-url-string-java/";
            String url1 = "http://stackoverflow.com/questions/37238779/lagged-fibonnaci-algorthm";
            InetAddress address = InetAddress.getByName(new URL(url).getHost());
            InetAddress address1 = InetAddress.getByName(new URL(url1).getHost());
            String ip = address.getHostAddress();
            System.out.println(ip + new URL(url).getFile().toLowerCase());
            System.out.println(address1.getHostAddress());
        } catch(Exception ex) {

        }
    }
}
