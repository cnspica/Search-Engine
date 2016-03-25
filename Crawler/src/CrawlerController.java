import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Scanner;


public class CrawlerController {
	
	public void Crawl(String seedFile, int threadCount, int size) {
		
		HashSet<String> fetched = new HashSet<>();
		HashSet <String> tofetch = new HashSet<>();
		
		Scanner seed=null;
		try {
			seed = new Scanner(new File(seedFile));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// read the seed URLs
		assert seed != null;
		while(seed.hasNextLine()){
			String url = seed.nextLine();
			tofetch.add(url);
		}
		
		
		// create threadCount threads to fetch URLS
		Thread[] t = new Thread[threadCount];
		for (int i=0; i < threadCount; i++){
			Crawler test = new Crawler(fetched, tofetch);
			test.set_size(size);
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
		
		try {
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
		} 
	}
}
