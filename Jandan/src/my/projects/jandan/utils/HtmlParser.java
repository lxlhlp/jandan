package my.projects.jandan.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

	private String html = "";

	public HtmlParser() {
	}

	public HtmlParser(String html) {
		this.html = html;
	}

	public HtmlParser(File file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "utf-8"));

			StringBuilder sb = new StringBuilder();
			String tmp;
			while ((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			this.html = sb.toString();
		} catch (Exception e) {
			this.html = "";
			e.printStackTrace();
		}
	}

	public String getPageNavi() {
		// ArrayList<String> imgList = null;
		if (this.html == null || "".equals(this.html)) {
			return null;
		}
		Document doc = Jsoup.parse(this.html);
		Element comments = doc.select("div.cp-pagenavi").first();
		// 第一步获得这里上一页的地址：
		Element pageNavi = comments.select("a.previous-comment-page").first();
		if (pageNavi == null) {
			return null;
		}
		String tmp=pageNavi.attr("href");
		String nextPage =tmp .substring(0, tmp.lastIndexOf('#'));
		return nextPage;
	}

	public void handleImgs(BlockingQueue<String> imageQueue) {
		Document doc = Jsoup.parse(this.html);
		Element commentlist = doc.select("ol.commentlist").first();
		Elements comments = commentlist.select("p>img");
		if (comments.size() < 1) {
			return;
		}
		String src = "";
		for (Element img : comments) {
			src = img.attr("src");
			try {
				imageQueue.put(src);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public LinkedList<String> handleImgs() {
		Document doc = Jsoup.parse(this.html);
		Element commentlist = doc.select("ol.commentlist").first();
		Elements comments = commentlist.select("p>img");
		if (comments.size() < 1) {
			return null;
		}
		LinkedList<String> imgs = new LinkedList<String>();
		String src = "";
		for (Element img : comments) {
			src = img.attr("src");
			imgs.add(src);
		}
		return imgs;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

}
