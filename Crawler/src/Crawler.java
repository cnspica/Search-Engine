import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
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
import com.trigonic.jrobotx.RobotExclusion;
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
	private Lock lock;
	
	public Crawler(String db, String username, String pass, Lock lock){
		this.maxSize = 4000;
		queue = new LinkedList<String>();
		this.lock = lock;
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

	public String isValidURL(String absHref){
		if (!absHref.startsWith("https://") && !absHref.startsWith("http://"))
			return "";
		if(absHref.endsWith(".pdf") || absHref.endsWith(".jpeg") || absHref.endsWith(".jpg")
				|| absHref.endsWith(".gif") || absHref.endsWith(".docx") || absHref.endsWith(".ppt")
				|| absHref.endsWith(".xls") || absHref.endsWith(".PNG") || absHref.endsWith(".GIF"))
			return "";

		int ind=absHref.indexOf('#');
		if(ind!=-1){
			String f=absHref.substring(0, ind);
			absHref=f;
		}
		return absHref.toLowerCase().replaceFirst("www.", "");
	}

	private void updateInCountBatch(String url, Elements links) {
		String query = "Update URLs SET inCount = inCount + 1 WHERE URL in (";
		for (Element link: links){
			String u = isValidURL(link.attr("abs:href"));
			if (u.equals(""))
				continue;
			try {
				u = new URL(u).toString();
				query += "'" + u.toString() + "', ";
			} catch (MalformedURLException e) {
				//e.printStackTrace();
			}

		}
		query = query.substring(0, query.length() - 2) + ")";
		executeNonQuery(query);
	}

	public void run(){
		RobotExclusion robotExclusion = new RobotExclusion();
		while(true){
			// check if queue is empty
			if (queue.size() == 0 && Crawler.totalFetchedURLs.get() < this.maxSize){
				waitForData(threadURLCount);
			}
			
			// fetch URL
			try {
				// fetch the document
				if (queue.size() == 0) {
					System.out.println(Thread.currentThread().getName() + " has just finished fetching URLs.");
					break;
				}
				String toFetch = queue.removeFirst();
				Document doc = Jsoup.connect(toFetch).get();

				// fetch the URLs
				Elements links = doc.select("a");

				// update outCount of url and title
				incrementURLOutCount(toFetch, links.size());
				updateTitle(toFetch, doc.title());
				updateInCountBatch(toFetch, links);

				// save document and continue
				saveDocument(doc, toFetch);
				if (Crawler.totalFetchedURLs.get() >= this.maxSize) {
					continue;
				}

				// debugging
				System.out.println(links.size() + " " + toFetch);

				// add urls to DB
				for (Element url: links){
					String absHref = url.attr("abs:href");
					absHref = isValidURL(absHref);
					if (absHref.equals(""))		// check if valid URL
						continue;
					URL checkedURL = new URL(absHref);
					if(!robotExclusion.allows(checkedURL,"*")){
						System.out.println("Not allowed URL "+ checkedURL);
						continue;
					}
					int result;
					synchronized(lock){
						// insert into DB
						result = insertURLIntoDB(checkedURL.toString());
						// increment totalFetchedURLs
						if (result == 1) {
							totalFetchedURLs.incrementAndGet();
							lock.notifyAll();
						}
						//else
							//incrementURLInCount(checkedURL.toString());
					}

					// debugging
					//System.out.println(totalFetchedURLs.get());

					//check for ending condition
					if (totalFetchedURLs.get() >= maxSize)
						break;
				}
				
			} catch (Exception ex){
				//ex.printStackTrace();
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
		return executeNonQuery("INSERT INTO URLs(URL, fetched, inCount) VALUES ('" + url + "', false, 1)");
	}

	private int incrementURLInCount(String url){
		return executeNonQuery("UPDATE URLs SET inCount = inCount + 1 WHERE URL = '" + url + "'");
	}

	private int incrementURLOutCount(String url, int count){
		return executeNonQuery("UPDATE URLs SET outCount = " + count + " WHERE URL = '" + url + "'");
	}

	private int updateTitle(String url, String title) {
		return executeNonQuery("UPDATE URLs SET title = '" + title.replaceAll("'", "\\'") + "' WHERE URL = '" + url + "'");
	}
	
	private boolean getURLsFromDB(int n){
		// fetch URLs
		//System.out.println("SELECT URL, id FROM URLs WHERE fetched = false LIMIT " + n);
	    String query = "SELECT URL, id FROM URLs WHERE fetched = false LIMIT " + n;
	    boolean found = false;
	    ResultSet resultSet = null;
	    int ID = 0;
		try { 
		    // query database                                        
			resultSet = executeQuery(query);
			while (resultSet.next()) {
				queue.add(resultSet.getObject("URL").toString());
			    ID = resultSet.getInt("id");
			    executeNonQuery("UPDATE URLs SET fetched = true WHERE id = " + ID);
			    found = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return found;
	}

	public void fetchPages() {
		int total = (Crawler.totalFetchedURLs.get() - Crawler.fetchedPagesCount.get())/Crawler.threadCount;
		getURLsFromDB(total);

		// Write rest of pages to files
		System.out.println(Thread.currentThread().getName() + " has just started fetching actual pages(" + queue.size() + ")");
		while (this.queue.size() > 0) {
			try {
				// fetch page
				String toFetch = queue.removeFirst();
				Document doc;
				doc = Jsoup.connect(toFetch).get();
				Elements links = doc.select("a");
				incrementURLOutCount(toFetch, links.size());    // update outCount
				updateTitle(toFetch, doc.title());    // update title
				updateInCountBatch(toFetch, links);		// update inCount

				// write page to file
				saveDocument(doc, toFetch);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				//e.printStackTrace();
			}
		}

		System.out.println(Thread.currentThread().getName() + " has finished");
	}
	
	public int waitForData(int n){
		// sleep fore 1 second then try fetching data if nothing is returened sleep again
		// try this for 10 times if it fails
		// return 2
		// when 2 is returned the thread break from the fetching loop and start fetching actual documents then stop

		boolean found = false;
		while (!found) {
			try {

				synchronized(lock) {
					found = getURLsFromDB(n);
					if (found) break;
					lock.wait();
				}
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
