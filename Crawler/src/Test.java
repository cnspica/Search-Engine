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
		
		CrawlerController crawler = new CrawlerController();
		crawler.Crawle("seed.txt", 4, 6);
		
	}
	
}
