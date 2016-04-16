import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class CrawlerController {

	private void seed(String seedFile) {
		// insert the seed into DB
		DBManager dbManager = new DBManager("jdbc:mysql://localhost:3306/APT", "root", "");
		Scanner seed=null;
		try {
			seed = new Scanner(new File(seedFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		// set fetched, inCount, outCount to 0 in URLs Table
		dbManager.ExecuteNonQuery("UPDATE URLs SET fetched = false, inCount = 0, outCount = 0, title = NULL");

		// read the seed URLs
		StringBuilder query = new StringBuilder("INSERT INTO URLs(URL, fetched, inCount) VALUES");
		while(seed.hasNextLine()){
			String url = seed.nextLine();
			query.append("('" + url + "', 0, 1), ");
		}
		dbManager.ExecuteNonQuery(query.substring(0, query.length()-2));
		dbManager.TerminateConnection();
	}
	
	public void crawel(String seedFile, int threadCount, int size, int threadURLCount, int seedSize) {
		long startTime = System.currentTimeMillis();	// get start time to calculate total runtime
		seed(seedFile);		
		Crawler.configure(threadCount, threadURLCount, seedSize);
		
		// create threadCount threads to fetch URLS
		Thread[] t = new Thread[threadCount];
		Lock lock = new Lock();
		for (int i=0; i < threadCount; i++){
			Crawler test = new Crawler("jdbc:mysql://localhost:3306/APT", "root", "", lock);
			test.setSize(size);
			t[i]=new Thread(test);
			t[i].start();
		}
		
		for (int i=0;i<threadCount;i++){
			try {
				t[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 	
		}
		System.out.println("Fetched " + Crawler.getTotalFetchedPages() + " pages in "
				+ (System.currentTimeMillis() - startTime)/(60.0 * 1000) + " minutes.");
	}
}
