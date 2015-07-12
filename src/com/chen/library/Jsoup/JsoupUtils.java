package com.chen.library.Jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class JsoupUtils {

	public static String getImageSrcFormHtml(String htmlString,String selector) {
		String srcString = null;
		Document document = Jsoup.parse(htmlString);
		Element element = document.select(selector).first();
		srcString=element.attr("src");
		return srcString;
	}
}
