package com.chen.library.Login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoginUtils {
	public static String cookie = "";
	public static boolean isLogin=false;

	/***
	 * 
	 * @param url
	 *            网页登陆链接
	 * @return 解析form表单之后，返回有标签(<input name...value..>)数据的HashMap<String,
	 *         String>(),键为name,值为value
	 * @throws ParseException
	 *             jsoup解析异常
	 * @throws IOException
	 *             IO异常
	 */
	public static HashMap<String, String> getLoginFormData(String url) {
		Document document = Jsoup.parse(getHtml(url));
		Elements element1 = document.getElementsByTag("form");// 找出所有form表单
		Element formElement = element1.select("[method=POST]").first();// 筛选出提交方法为post的第一个表单
		Elements elements = formElement.select("input[name]");// 把表单中带有name属性的所有input标签取出

		HashMap<String, String> parmas = new HashMap<String, String>();

		for (Element temp : elements) {
			if (temp.attr("name").equals("select")) {
				parmas.put(temp.attr("name"), "bar_no");
				continue;
			}
			parmas.put(temp.attr("name"), temp.attr("value"));// 把所有取出的input，取出其name，放入Map中
		}
		System.out.println(parmas.toString());
		return parmas;
	}

	/******
	 * 
	 * @param userName
	 *            用户名
	 * @param passWord
	 *            密码
	 * @param verifyCode
	 *            验证码
	 * @return List<NameValuePair> 填充输入信息
	 * @throws ParseException
	 * @throws IOException
	 */
	public static List<NameValuePair> initLoginParmas(HashMap<String, String> parmasMap, String userName,
			String passWord, String verifyCode)  {
		List<NameValuePair> parmasList = new ArrayList<NameValuePair>();
		// HashMap<String, String> parmasMap = data;
		Set<String> keySet = parmasMap.keySet();

		for (String temp : keySet) {
			if (temp.contains("number")) {
				parmasMap.put(temp, userName);
			} else if (temp.contains("passwd")) {
				parmasMap.put(temp, passWord);
			} else if (temp.contains("captcha")) {
				parmasMap.put(temp, verifyCode);
			}
		}

		Set<String> keySet2 = parmasMap.keySet();
		System.out.println("----------》表单内容：");
		for (String temp : keySet2) {
			System.out.println(temp + " = " + parmasMap.get(temp));
		}
		for (String temp : keySet2) {
			parmasList.add(new BasicNameValuePair(temp, parmasMap.get(temp)));
		}

		return parmasList;

	}

	/****
	 * 
	 * @param url
	 *            登陆页面url链接地址
	 * @return 登陆网页html字符串数据
	 * @throws ParseException
	 *             jsoup解析异常
	 * @throws IOException
	 *             io异常
	 */
	public static String getHtml(String url) {
		String result = "";
		HttpPost post = new HttpPost(url);
		if (cookie != "") {
			post.addHeader("Cookie", cookie);
		}
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "utf-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
//			Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return result;
	}

	/***
	 * 使用 用户名，密码，验证码 登陆
	 * 
	 * @param userName
	 * @param passWord
	 * @param verifyCode
	 * @return 登陆状态码 302成功，200等失败
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static int login(HashMap<String, String> parmasMap, String userName, String passWord, String verifyCode) {
		int statusCode=0;
		List<NameValuePair> parmasList = new ArrayList<NameValuePair>();
		try {
			parmasList = initLoginParmas(parmasMap, userName, passWord, verifyCode);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		HttpPost post = new HttpPost("http://opac.lib.wust.edu.cn:8080/reader/redr_verify.php");
		if (cookie != "") {
			post.addHeader("Cookie", cookie);
		}
		post.getParams().setParameter("http.protocol.handle-redirects", false);
		// 阻止自动重定向，目的是获取第一个ResponseHeader的Cookie和Location
		post.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");
		// 设置编码为GBK
		try {
			post.setEntity(new UrlEncodedFormEntity(parmasList, "GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		try {
			HttpResponse response = new DefaultHttpClient().execute(post);
			statusCode = response.getStatusLine().getStatusCode();
			System.out.println("状态码= " + statusCode);
			if (statusCode == 200) {// 没跳转报错
				System.out.println("登陆失败！！！！！！！！");

			} else if (statusCode == 302) {
				String location = response.getFirstHeader("location").getValue();
				// 重定向地址，目的是连接到主页
				String mainUrl = "http://opac.lib.wust.edu.cn:8080/reader/" + location;
				// 构建主页地址
				System.out.println("location=========" + mainUrl);
//				System.out.println("跳转之后的页面------->" + getHtml(mainUrl));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	

		return statusCode;
		
	}

}
