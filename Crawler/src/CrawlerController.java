import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;


public class CrawlerController {
	
<<<<<<< HEAD
	private void seed(String seedFile) {
		// insert the seed into DB
		Connection connection = null; // manages connection
	    Statement statement = null; // query statement
	    
=======
	public void Crawl(String seedFile, int threadCount, int size) {
		
		HashSet<String> fetched = new HashSet<>();
		HashSet <String> tofetch = new HashSet<>();
		
>>>>>>> abd5658109e0280f01c693344ff66af23d6aed64
		Scanner seed=null;
		try {
			seed = new Scanner(new File(seedFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/APT", "root", "");
			// create Statement for querying database
			statement = connection.createStatement();
			// read the seed URLs
			while(seed.hasNextLine()){
				String url = seed.nextLine();
				statement.executeUpdate("INSERT INTO URLs(URL, beingFetched) VALUES ('" + url + "', false)");
			}
		} catch (Exception ex){
			
		}  finally // ensure resultSet, statement and connection are closed
	      {                                                             
	         try                                                        
	         {                                                                                              
	            statement.close();                                      
	            connection.close();                                     
	         } // end try                                               
	         catch ( Exception exception )                              
	         {                                                          
	            exception.printStackTrace();                            
	         } // end catch                                             
	      } // end finally       
	}
	
	public void crawle(String seedFile, int threadCount, int size, int threadURLCount) {
		
<<<<<<< HEAD
		seed(seedFile);
=======
		// read the seed URLs
		assert seed != null;
		while(seed.hasNextLine()){
			String url = seed.nextLine();
			tofetch.add(url);
		}
>>>>>>> abd5658109e0280f01c693344ff66af23d6aed64
		
		Crawler.configure(threadCount, threadURLCount);
		
		// create threadCount threads to fetch URLS
		Thread[] t = new Thread[threadCount];
		for (int i=0; i < threadCount; i++){
			Crawler test = new Crawler("jdbc:mysql://localhost:3306/APT", "root", "");
			test.setSize(size);
			t[i]=new Thread(test);
			t[i].start();
		}
		
		for (int i=0;i<threadCount;i++){
			try {
				t[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
		}
		
	/*	try {
			// write the list of fetched URLs in a file
			PrintStream out = new PrintStream(new File("Crawler/Results.txt"));
		
			System.out.println("Total Fetched URLs: " + fetched.size());
			for (int i=0; i < fetched.size(); i++){
				String url = fetched.iterator().next();
				fetched.remove(url);
				out.println(url);
			}
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}
}
