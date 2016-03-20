
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class Crawler implements Runnable {
	
	public int Max_size;
	private HashSet <String> fetched;
	private HashSet <String> tofetch;
	private static int count = 0;
	
	public Crawler(HashSet <String> fetched, HashSet <String> tofetch){
		this.fetched = fetched;
		this.tofetch = tofetch;
		this.Max_size=4000;
	}
     
	public void set_size(int n){
		this.Max_size=n;	
	}
	public int get_size(){
		return this.Max_size;
	} 

	public void run(){
		
		while(this.fetched.size() + this.tofetch.size() < this.Max_size){
			Document doc;
			String to_fetch="";
			//System.out.println(this.fetched.size() + this.tofetch.size());
			
			synchronized (tofetch) {// synchronize on tofetch to avoid race conditions
				if (this.tofetch.isEmpty())
					break;
				to_fetch = this.tofetch.iterator().next();
				tofetch.remove(to_fetch);
			}
			
			try {
				// fetch the document
				doc = Jsoup.connect(to_fetch).get();
				
				// add fetchd page to fetched hashset
				synchronized (fetched) {
					fetched.add(to_fetch); 
				}
				
				// get all URLs inside that page
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
					
					synchronized(tofetch) {	// synchronize on fetched to avoid race conditions
						
						//check if the link for file or site
						try {	
							// catch wrong URL
							URL u = new URL(absHref);
							
							// if fetched before, do not add it to tofetch
							if (fetched.contains(u.toString()))
								continue;
							
							this.tofetch.add(u.toString());
						}
						catch (MalformedURLException ex){
							System.out.println(ex.getMessage());
						}
					}
				}
				
				// write page to file
				String docName="";
				synchronized(this){
					docName = "documents\\doc" + count + ".txt";
					count++;
				}
				
				PrintStream out = new PrintStream(new File(docName));
				out.println(to_fetch);
				out.print(doc.html());
				out.close();
				
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
		
		// fetch and save not fetched pages
		save();
	}
	
	public void save() {
		// Write rest of pages to files
		for (int i=0; i < tofetch.size(); i++) {
			try {
				
				String docName="", to_fetch="";
				synchronized (tofetch) {// synchronize on tofetch to avoid race conditions
					to_fetch = this.tofetch.iterator().next();
					tofetch.remove(to_fetch);
				}
				
				synchronized(this){
					docName = "documents\\doc" + count + ".txt";
					count++;
				}
				
				// write page to file
				PrintStream output = new PrintStream(new File(docName));
				output.println(to_fetch);
				output.print(Jsoup.connect(to_fetch).get().html());
				output.close();
				
				// add to fetched
				synchronized (fetched) {
					fetched.add(to_fetch); 
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			System.out.println(tofetch.size());	// debugining
		}
		
	}

}
