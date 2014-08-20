package my.projects.jandan.utils.test;

import my.projects.jandan.utils.HtmlParser;
import my.projects.jandan.utils.SimpleHttpClient;

public class HtmlParserTest {

	public static void main(String[] args) {
		HtmlParser parser = new HtmlParser();
		SimpleHttpClient client = new SimpleHttpClient();
		String[] urlList = new String[5];
		urlList[0]="http://jandan.net/ooxx";
		urlList[1]="http://jandan.net/ooxx/page-217";
		urlList[2]="http://jandan.net/ooxx/page-207";
		urlList[3]="http://jandan.net/ooxx/page-117";
		urlList[4]="http://jandan.net/ooxx/page-7";
		String html = "";
		for (String url : urlList) {
			System.out.println("fecthing url===>" + url);
			html = client.get(url);
			parser.setHtml(html);
			System.out.println("Now get Next Page url:" + parser.getPageNavi());
			System.out.println("Now get Images URLs:");
			System.out.println(parser.handleImgs());
			System.out.println("========");
		}
	}
}
