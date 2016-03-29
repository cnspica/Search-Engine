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
			String query = "INSERT INTO URLs(URL, fetched, inCount) VALUES";
			while(seed.hasNextLine()){
				String url = seed.nextLine();
				query += "('" + url + "', 0, 1), ";
			}
			System.out.println(statement.executeUpdate(query.substring(0, query.length()-2)));
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

	}
}
