package my.projects.jandan.utils.test;

import my.projects.jandan.utils.SimpleHttpClient;

public class SimpleHttpClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] urlList=new String[2];
//		urlList[0]="http://blog.satikey.com";
//		urlList[1]="http://blog.satikey.com/2012/02/auto-save-image-plus.html";
//		urlList[0]="http://jandan.net/ooxx/page-215#comments";
//		urlList[1]="http://jandan.net/ooxx/page-218#comments";
		urlList[0]="http://jandan.net/ooxx";
		urlList[1]="http://jandan.net/ooxx/page-218";
		SimpleHttpClient httpclient=new SimpleHttpClient();
		for (String url : urlList) {
			System.out.println(httpclient.get(url));
		}

	}

}
