import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;


public class CrawlerController {

	private void seed(String seedFile) {
		// insert the seed into DB
		Connection connection = null; // manages connection
	    Statement statement = null; // query statement
		Scanner seed=null;
		try {
			seed = new Scanner(new File(seedFile));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/APT", "root", "");
			// create Statement for querying database
			statement = connection.createStatement();

			// set fetched, inCount, outCount to 0 in URLs Table
			statement.executeUpdate("UPDATE URLs SET fetched = false, inCount = 0, outCount = 0, title = NULL");


			// read the seed URLs
			while(seed.hasNextLine()){
				String url = seed.nextLine();
				statement.executeUpdate("INSERT INTO URLs(URL, fetched, inCount) VALUES ('" + url + "', false, 1)");
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
		
		seed(seedFile);		
		Crawler.configure(threadCount, threadURLCount);
		
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
