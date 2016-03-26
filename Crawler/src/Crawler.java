
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Crawler implements Runnable {
	
	public int maxSize;
	private static AtomicInteger totalFetchedURLs;
	private static AtomicInteger fetchedPagesCount;
	private LinkedList<String> queue;
	private static int threadURLCount;
	private static int threadCount = 1;
	private Connection connection = null;
	private Statement statement = null;
	private Statement statementNonQuery = null;
	
	
	public Crawler(String db, String username, String pass){
		this.maxSize = 4000;
		queue = new LinkedList<String>();
		
		// establish connection to database                              
	    try {
			connection = DriverManager.getConnection(db, username, pass);
	
			// create Statement for querying database
			statement = connection.createStatement();
			statementNonQuery = connection.createStatement();
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
     
	public static void configure(int threadCount, int threadURLCount){
		Crawler.threadCount = threadCount;
		Crawler.threadURLCount = threadURLCount;
		Crawler.totalFetchedURLs = new AtomicInteger();
		Crawler.fetchedPagesCount = new AtomicInteger();
	}
	
	public void setSize(int n){
		this.maxSize=n;	
	}
	public int getSize(){
		return this.maxSize;
	} 

	public void run(){
		
		while(true){
			// check if queue is empty
			if (queue.size() == 0 && Crawler.totalFetchedURLs.get() < this.maxSize){
				int z = waitForData();
				if (z == 2)	// all threads are waiting then break
					break;
			}
			
			// fetch URL
			try {
				// fetch the document
				String toFetch = queue.removeFirst();
				Document doc = Jsoup.connect(toFetch).get();
				
				// save document and continue
				saveDocument(doc, toFetch);
				if (Crawler.totalFetchedURLs.get() >= this.maxSize) {
					continue;
				}
				
				// fetch the URLs
				Elements links = doc.select("a");
				System.out.println(links.size() + " " + toFetch);
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
					URL u = new URL(absHref);
					int result;
					synchronized(totalFetchedURLs){
						// insert into DB
						result = insertURLIntoDB(u.toString());
					}
					
					// increment totalFetchedURLs
					if (result == 1)
						totalFetchedURLs.incrementAndGet();
					
					// debugging
					//System.out.println(totalFetchedURLs.get());
				}
				
				//check for ending condition
				if (queue.size() == 0 && totalFetchedURLs.get() >= maxSize)
					break;
				
			} catch (Exception ex){
				System.out.println(ex.getMessage());
			}
			
			
		}
		System.out.println(totalFetchedURLs.get());
		// fetch and save not fetched pages
		try {
			fetchPages();
		}
		catch (Exception e){
			
		} finally {
			// close connection
			try                                                        
	         {                                                                                               
	            statement.close();                                      
	            connection.close();                                     
	         } // end try                                               
	         catch ( Exception exception )                              
	         {                                                          
	            exception.printStackTrace();                            
	         } // end catch             
		}
	}
	
	private int insertURLIntoDB(String url){
		return executeNonQuery("INSERT INTO URLs(URL, beingFetched) VALUES ('" + url + "', false)");
	}
	
	private boolean getURLsFromDB(int n){
		// fetch URLs
		//System.out.println("SELECT URL, id FROM URLs WHERE beingFetched = false LIMIT " + n);
	    String query = "SELECT URL, id FROM URLs WHERE beingFetched = false LIMIT " + n;
	    boolean found = false;
	    ResultSet resultSet = null;
	    int ID = 0;
		try { 
		    // query database                                        
			resultSet = executeQuery(query);
			while (resultSet.next()) {
				queue.add(resultSet.getObject("URL").toString());
			    ID = resultSet.getInt("id");
			    executeNonQuery("UPDATE URLs SET beingFetched = true WHERE id = " + ID);
			    found = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return found;
	}

	public void fetchPages() {
		// get pages URLs from DB
		int total = (totalFetchedURLs.get() - fetchedPagesCount.get())/Crawler.threadCount;
		synchronized(totalFetchedURLs){
			Crawler.threadCount--;
			getURLsFromDB(total);
		}
		// Write rest of pages to files
		System.out.println(Thread.currentThread().getName() + " has just started fetching actual pages(" + queue.size() + ")");
		while (this.queue.size() > 0){
			try {
				// fetch page
				String toFetch = queue.removeFirst();
				Document doc;
				doc = Jsoup.connect(toFetch).get();
			
				// write page to file
				saveDocument(doc, toFetch);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println(Thread.currentThread().getName() + " has finished");
	}
	
	public int waitForData(){
		// sleep fore 1 second then try fetching data if nothing is returened sleep again
		// try this for 10 times if it fails
		// return 2
		// when 2 is returned the thread break from the fetching loop and start fetching actual documents then stop
		int c = 0;
		boolean found = false;
		while (!found) {
			try {
				Thread.sleep(1000);
				c++;
				synchronized(totalFetchedURLs){
					found = getURLsFromDB(threadURLCount);
				}
				if (c == 100)
					return 2;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		return 1;
	}
	
	private void saveDocument(Document doc, String url){
		String docName = "documents\\doc" + fetchedPagesCount.incrementAndGet() + ".txt";
		PrintStream out;
		try {
			out = new PrintStream(new File(docName));
			out.println(url);
			out.print(doc.html());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet executeQuery(String query){
		ResultSet resultSet = null;
	    try {
		     // query database                                        
		     resultSet = statement.executeQuery(query);
		     
		     
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	    
	    return resultSet;
	}
	
	public int executeNonQuery(String query){
		
	    int result = 100;
	    try {
		    // query database                                        
		    result = statementNonQuery.executeUpdate(query);
		    
		} catch (Exception ex) {
			//System.out.println(ex.getMessage());
		} 
	    return result;
	}
}
