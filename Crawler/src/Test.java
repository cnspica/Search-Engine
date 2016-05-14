
public class Test {

	public static void main(String[] args){
		
		CrawlerController crawler = new CrawlerController();

        // 2nd parameter => number of threads, 3rd parameter => number of links
		crawler.crawel("seed.txt", 8, 2000, 2, 200);
	}
	
}
