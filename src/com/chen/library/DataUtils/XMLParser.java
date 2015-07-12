package com.chen.library.DataUtils;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLParser {
//为获取的返回xml字符串设置解析器，用到了factory模式
	public static void   setXMLParser(String xMLString) {
		//新建一个ContentHandler对象
		MyContentHandler myContentHandler = new MyContentHandler();
		//获取工厂对象
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			//由工厂对象获取XMLReader对象
			XMLReader reader = factory.newSAXParser().getXMLReader();
			//为reader设置内容处理器
			reader.setContentHandler(myContentHandler);
			try {
				//开始解析
				reader.parse(new InputSource(new StringReader(xMLString)));

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	
		
	}

}
