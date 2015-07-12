package com.chen.library.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

//import com.wust.news.DecodeUtils.DecodeUnicode;

public class HttpUtils {
	// 以Get的方式根据url获取html内容
	public static String GetString(String urlString, String encodeSet) {
		String result = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), encodeSet);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}

		return result;
	}

	// 以Get的方式根据url获取图片(以字节数组的形式保存)
	public static byte[] GetByteArray(String urlString) {
		byte[] result = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toByteArray(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}

		return result;
	}

	// 以Post的方式根据url获取html内容
	public static String PostString(String urlString, String encode_set) {
		String result = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(urlString);
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), encode_set);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return result;
	}

	// 最基础的获取方式，没用到apache的http封装方法
	public static String Download(String path, String encode_set) {
		String result = "";
		try {
			URL url = new URL(path);
			URLConnection connection = url.openConnection();

			String line;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), encode_set));
			while ((line = in.readLine()) != null) {
				result += line;
			}

			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param path
	 *            url链接
	 * @param encodeSet
	 *            字符串编码方式
	 * @return 根据path用get方式获取的html字符串
	 */
	public static String doGetString(String path, String encodeSet) {
		String data = null;
		HttpGet get = new HttpGet(path);
		HttpClient client = new DefaultHttpClient();
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				data = EntityUtils.toString(response.getEntity(), encodeSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return data;
	}

	public static byte[] doGetByteArray(String path) {
		byte[] data = null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10 * 1000);
			InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			data = readInputStream(inStream);// 得到图片的二进制数据

		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * 从输入流中获取数据
	 * 
	 * @param inStream
	 *            输入流
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

}
