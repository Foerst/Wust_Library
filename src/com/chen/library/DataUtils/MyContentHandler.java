package com.chen.library.DataUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.chen.library.HttpPath.UrlPath;
import com.chen.library.Utils.ImageUtils;
//这里定义解析xml文件的ContentHandler类
public class MyContentHandler extends DefaultHandler {
	public String tagName;//记录当前标签名称

	//构造函数
	public MyContentHandler() {
		tagName = "";
	
		
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		System.out.println("------>start");
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		System.out.println("------>end");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		//设置当前标签名为正在解析的标签
		tagName = localName;
	
		if (localName.equals("link")) {
			for (int i = 0; i < attributes.getLength(); i++) {
				System.out.println(attributes.getLocalName(i) + "=" + attributes.getValue(i));
				//解析书籍图片的src属性，即图片的url链接
				if (attributes.getLocalName(i).equals("rel")&&attributes.getValue(i).equals("image")) {
					System.out.println("src---------->"+attributes.getValue(i-1));
					String tmpString=attributes.getValue(i-1);
					UrlPath.IMAGE_URL=ImageUtils.GetLargePictureUrl(tmpString);
					
					
				}

			}
			
		}


	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		if (tagName.equals("summary")) {
			//获取豆瓣书评summary
			String summary=new String(ch, 0, length);
			System.out.println("summary==========="+summary);
			UrlPath.SUMMARY_OF_BOOK=summary;
			
		}
	}

}
