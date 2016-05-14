import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.trigonic.jrobotx.RobotExclusion;
import org.jsoup.select.Elements;


public class Crawler implements Runnable {
	
	public int maxSize;
	private static AtomicInteger totalFetchedURLs;
	private static AtomicInteger fetchedPagesCount;
	private static AtomicInteger fetchedPagesCount1;
	private LinkedList<String> queue;
	private static int threadURLCount;
	private static int threadCount = 1;
	private int totalFetchedByMe = 0;
	private Lock lock;
	private DBManager dbManager;
	
	public Crawler(String db, String username, String pass, Lock lock){
		this.maxSize = 4000;
		queue = new LinkedList<String>();
		this.lock = lock;
		dbManager = new DBManager(db, username, pass);
	}
     
	public static void configure(int threadCount, int threadURLCount, int seedSize){
		Crawler.threadCount = threadCount;
		Crawler.threadURLCount = threadURLCount;
		Crawler.totalFetchedURLs = new AtomicInteger(seedSize);
		Crawler.fetchedPagesCount = new AtomicInteger();
		Crawler.fetchedPagesCount1 = new AtomicInteger();
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
		absHref = absHref.toLowerCase().replaceFirst("www.", "");
		return absHref;
	}

	private boolean deleteURL(String url) {
		return dbManager.ExecuteNonQuery("DELETE FROM URLs WHERE URL = '" + url + "'");
	}

	private void updateInCountBatch(Elements links) {
		StringBuilder query = new StringBuilder("UPDATE URLs SET inCount = inCount + 1 WHERE URL in (");
		for (Element link: links){
			String u = isValidURL(link.attr("abs:href"));
			if (u.equals(""))
				continue;
			try {
				u = new URL(u).toString();
				query.append("'" + u.toString() + "', ");
			} catch (MalformedURLException e) {
				//e.printStackTrace();
			}
		}
		dbManager.ExecuteNonQuery(query.substring(0, query.length() - 2) + ")");
	}

	public void run(){
		RobotExclusion robotExclusion = new RobotExclusion();
		while(true){
			// check if queue is empty
			if (queue.size() == 0 && Crawler.totalFetchedURLs.get() < this.maxSize){
				waitForData(threadURLCount);
			}
			
			// fetch URL
			String toFetch="";
			try {
				// fetch the document
				if (queue.size() == 0) {
					System.out.printf(Thread.currentThread().getName() + " has just finished fetching URLs(%d).\n", totalFetchedByMe);
					break;
				}
				toFetch = queue.removeFirst();
				Document doc = Jsoup.connect(toFetch).get();

				// fetch the URLs
				Elements links = doc.select("a");

				// update outCount of url and title
				incrementURLOutCount(toFetch, links.size());
				updateTitle(toFetch, doc.title());
				updateInCountBatch(links);

				// save document and continue
				saveDocument(doc, toFetch);
				updateFetchedField(toFetch);	// mark as fetched
				fetchedPagesCount1.incrementAndGet();
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
						//System.out.println("Not allowed URL "+ checkedURL);
						continue;
					}
					boolean result;
					synchronized(lock){
						// insert into DB
						result = insertURLIntoDB(checkedURL.toString());
						// increment totalFetchedURLs
						if (result) {
							totalFetchedURLs.incrementAndGet();
							lock.notifyAll();
							totalFetchedByMe++;
						}
					}

					//check for ending condition
					if (totalFetchedURLs.get() >= maxSize)
						break;
				}
				
			} catch (Exception ex){
				//ex.printStackTrace();
				//System.out.println(ex.getMessage());
				deleteURL(toFetch);
			}
			
			
		}
		//System.out.println(totalFetchedURLs.get());
		// fetch and save not fetched pages
		fetchPages();
		dbManager.TerminateConnection();
	}
	
	private boolean insertURLIntoDB(String url){
		return dbManager.ExecuteNonQuery("INSERT INTO URLs(URL, status, inCount) VALUES ('" + url + "', false, 1)");
	}

	private boolean incrementURLOutCount(String url, int count){
		return dbManager.ExecuteNonQuery("UPDATE URLs SET outCount = " + count + " WHERE URL = '" + url + "'");
	}

	private boolean updateTitle(String url, String title) {
		return dbManager.ExecuteNonQuery("UPDATE URLs SET title = '" + title.replaceAll("'", "\"") + "' WHERE URL = '" + url + "'");
	}

	private boolean updateFetchedField(String url){
		return dbManager.ExecuteNonQuery("UPDATE URLs SET status = 2 WHERE url = '" + url + "'");
	}

	private boolean getURLsFromDB(int n){
		// fetch URLs
	    String query = "SELECT URL, url_id FROM URLs WHERE status = 0 LIMIT " + n; // 0 is not visited URL, 1 fetched to Crawler memory but
		// not downloaded, 2 fetched and downloaded
	    boolean found = false;
	    ResultSet resultSet = null;
	    int ID = 0;
		try { 
		    // query database                                        
			resultSet = dbManager.ExecuteQuery(query);
			while (resultSet.next()) {
				queue.add(resultSet.getObject("URL").toString());
			    ID = resultSet.getInt("url_id");
				dbManager.ExecuteNonQuery("UPDATE URLs SET status = 1 WHERE url_id = " + ID);
			    found = true;
			}
		} catch (Exception e) {
            System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		return found;
	}

	public void fetchPages() {
		int total = (Crawler.totalFetchedURLs.get() - Crawler.fetchedPagesCount1.get())/Crawler.threadCount;
		synchronized (lock) {
			getURLsFromDB(total);
		}

		// Write rest of pages to files
		System.out.println(Thread.currentThread().getName() + " has just started fetching actual pages(" + queue.size() + ").");
		while (this.queue.size() > 0) {
			String toFetch="";
			try {
				// fetch page
				toFetch = queue.removeFirst();
				Document doc;
				doc = Jsoup.connect(toFetch).get();
				Elements links = doc.select("a");
				incrementURLOutCount(toFetch, links.size());    // update outCount
				updateTitle(toFetch, doc.title());    // update title
				updateInCountBatch(links);		// update inCount

				// write page to file
				saveDocument(doc, toFetch);
				updateFetchedField(toFetch);	// mark as fetched
			} catch (Exception e) {
				System.out.println(e.getMessage());
				deleteURL(toFetch);
			}
		}

		System.out.println(Thread.currentThread().getName() + " has finished");
	}
	
	public int waitForData(int n){
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
        String docN = "doc"+ fetchedPagesCount.incrementAndGet() + ".txt";
		String docName =  Paths.get("").toAbsolutePath().toString()+"/documents/" + docN;

		PrintStream out;
		try {
			out = new PrintStream(new File(docName));
			out.println(url);
			out.print(doc.html());
			out.close();
            insertDocumentNumber(url, docN);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

    private boolean insertDocumentNumber(String url, String docName) {
        return dbManager.ExecuteNonQuery("UPDATE URLs SET doc = '" + docName + "' WHERE url = '" + url + "'");
    }

    public static int getTotalFetchedPages() {
		return fetchedPagesCount.get();
	}
}
