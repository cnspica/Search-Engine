
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Crawler implements Runnable {
	public int Max_size;
	private HashSet <String> fetched;
	private LinkedList<String> tovisit;
	public Crawler(HashSet <String> fetched, LinkedList<String> tovisit){
		this.fetched = fetched;
		this.tovisit = tovisit;
		this.Max_size=4000;
		
	}
     
	public void set_size(int n){
		this.Max_size=n;	
	}
	public int get_size(){
		return this.Max_size;
	} 

	public void run(){
		while(this.fetched.size()<this.Max_size){
			Document doc;
			String to_fetch="";
			System.out.println(fetched.size());
			if (this.tovisit.isEmpty())
				break;
			
			synchronized (tovisit) {// synchronize on tovisit to avoid race conditions
				if (this.tovisit.size()>0)
				to_fetch = this.tovisit.remove(0);
			}
			try {
				doc = Jsoup.connect(to_fetch).get();
				Elements links = doc.select("a");
				for (Element url: links){
					String absHref = url.attr("abs:href");
					if (!absHref.startsWith("https://") && !absHref.startsWith("http://"))
						continue;
					if(absHref.endsWith(".pdf") || absHref.endsWith(".jpeg") || absHref.endsWith(".jpg")
							|| absHref.endsWith(".gif") || absHref.endsWith(".docx") || absHref.endsWith(".ppt")
							|| absHref.endsWith(".xls") || absHref.endsWith(".PNG") || absHref.endsWith(".GIF"))
						continue;
					int ind=absHref.indexOf('#');
					if(ind!=-1){
						String f=absHref.substring(0, ind);
						absHref=f;
					}
					absHref = absHref.toLowerCase().replaceFirst("www.", "");
					
					synchronized(fetched) {	// synchronize on fetched to avoid race conditions
						
						//check if the link for file or site
						try {	// catch wrong URL
							URL u = new URL(absHref);
							boolean added = this.fetched.add(u.toString());
							if(added){
								this.tovisit.add(u.toString());
								//System.out.println(absHref);
							}
						}
						catch (MalformedURLException ex){
							System.out.println(ex.getMessage());
						}
					}
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Bad URL: " + to_fetch);
				//e.printStackTrace();
			} catch (SocketTimeoutException e){
				System.out.println("Read Time Out");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				//e.printStackTrace();
			}

		}
		System.out.println(this.fetched.size());
	}

}
