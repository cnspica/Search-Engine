import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

public class Test {

	public static void main(String[] args){
		
		HashSet<String> fetched = new HashSet<String>();
		LinkedList <String> tovisit = new LinkedList<String>();
		
		Scanner seed=null;
		try {
			seed = new Scanner(new File("seed.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// read the seed URLs
		while(seed.hasNextLine()){
			String url = seed.nextLine();
			fetched.add(url);
			tovisit.add(url);
		}
		
	
		// create 4 threads to fetch URLS
		Thread t[]=new Thread[4];
		for (int i=0;i<4;i++){
			Crawler test = new Crawler(fetched,tovisit);
			test.set_size(1000);
			t[i]=new Thread(test);
			t[i].start();
		}
		
		for (int i=0;i<4;i++){
			try {
				t[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 	
		}
		
		// Write results to file
		try {
			PrintStream output = new PrintStream(new File("Results.txt"));
			for (String url: fetched){
				output.println(url);
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
