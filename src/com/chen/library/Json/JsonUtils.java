package com.chen.library.Json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chen.library.DataClass.*;

import android.util.Log;

public class JsonUtils {

	/***
	 * 
	 * @param htmlString
	 * @param selectorf
	 * @param tableName
	 * @return
	 */

	public static String getJsonStringFromHtml(String htmlString,
			String selector, String tableName) {

		Document document = Jsoup.parse(htmlString);
		Element element = document.select(selector).first();
		String txt = null;
		if (tableName.equals("paperTable")) {
			txt = "{" + tableName + ":[";
			Elements row = element.getElementsByTag("tr");
			Elements col = row.first().getElementsByTag("th");
			if (col != null) {
				Log.v("______------>", "!null");
				Log.v("value------>", col.toString());
			}
			// Log.v("th----->", col.first().toString());
			for (int j = 1; j < row.size(); j++) {
				String r = "{";
				Elements tds = row.get(j).getElementsByTag("td");
				for (int i = 0; i < tds.size(); i++) {

					r += "\"" + col.get(i).text() + "\":\"" + tds.get(i).text()
							+ "\",";
				}
				r = r.substring(0, r.length() - 1);
				r += "},";
				txt += r;
			}
			txt = txt.substring(0, txt.length() - 1);
			txt += "]}";
			Log.v("json--->paperTable---->", txt);
		} else if (tableName.equals("projectHistoryTable")) {
			if (element==null){
				return "noContent";
				
			}
			txt = "{" + tableName + ":[";
			Elements row = element.getElementsByTag("tr");
			Elements col = row.get(2).getElementsByTag("td");
			if (col != null) {
				Log.v("______------>", "!null");
				Log.v("value------>", col.toString());
			}
			// Log.v("th----->", col.first().toString());
			for (int j = 2; j < row.size(); j++) {
				String r = "{";
				Elements tds = row.get(j).getElementsByTag("td");
				for (int i = 0; i < tds.size(); i++) {

					r += "\"" + col.get(i).text() + "\":\"" + tds.get(i).text()
							+ "\",";
				}
				r = r.substring(0, r.length() - 1);
				r += "},";
				txt += r;
			}
			txt = txt.substring(0, txt.length() - 1);
			txt += "]}";
			Log.v("json--->projectHistoryTable------>", txt);
		} else if (tableName.equals("coAuthorTable")) {
			int tabNum = document.select("table").size();
			if (tabNum == 8) {
				element = document.getElementsByTag("table").get(6);
			} else {
				element = document.getElementsByTag("table").get(7);
			}

			txt = "{" + tableName + ":[";
			Elements row = element.getElementsByTag("tr");

			for (int j = 1; j < row.size(); j++) {
				String r = "{";
				Elements tds = row.get(j).getElementsByTag("td");
				for (int i = 0; i < tds.size(); i++) {

					r += "\"" + i + "\":\""
							+ tds.get(i).text().toString().trim() + "\",";
				}
				r = r.substring(0, r.length() - 1);
				r += "},";
				txt += r;
			}
			txt = txt.substring(0, txt.length() - 1);
			txt += "]}";
			Log.v("json--->coAuthorTable------", txt);
		} else if (tableName.equals("sameAuthorTable")) {
			element = document.getElementsByTag("table").get(3);
			txt = "{" + tableName + ":[";
			Elements row = element.getElementsByTag("tr");

			for (int j = 0; j < row.size(); j++) {
				String r = "{";
				Elements tds = row.get(j).getElementsByTag("td");
				for (int i = 0; i < tds.size(); i++) {

					r += "\"" + i + "\":\""
							+ tds.get(i).text().toString().trim() + "\",";
				}
				r = r.substring(0, r.length() - 1);
				r += "},";
				txt += r;
			}
			txt = txt.substring(0, txt.length() - 1);
			txt += "]}";
			Log.v("json--->sameAuthorTable--->", txt);
		}

		return txt;
	}

	/****
	 * 
	 * @param jsonString
	 * @return
	 */
	public static ArrayList<Paper> parseJsonToPaper(String jsonString) {
		ArrayList<Paper> paperList = new ArrayList<Paper>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("paperTable");

			for (int i = 0; i < jsonArray.length(); i++) {

				if (((JSONObject) jsonArray.opt(i)).length() == 2) {
					Log.v("json" + i, ((JSONObject) jsonArray.opt(i))
							.getString("No.").toString());
					String no = ((JSONObject) jsonArray.opt(i))
							.getString("No.").toString().trim();

					Log.v("json" + i, ((JSONObject) jsonArray.opt(i))
							.getString("Paper Inforamtion").toString());
					String info = ((JSONObject) jsonArray.opt(i))
							.getString("Paper Inforamtion").toString().trim();
					Paper paper = new Paper(no, info);
					paperList.add(paper);
				} else {
					Log.v("json" + i, ((JSONObject) jsonArray.opt(i))
							.getString("No.").toString());
					String no = ((JSONObject) jsonArray.opt(i))
							.getString("No.").toString().trim();
					Paper paper = new Paper(no, "");
					paperList.add(paper);
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.v("paperList", paperList.toString());
		return paperList;
	}

	/**
	 * 
	 * @param jsonString
	 * @return
	 */
	public static ArrayList<CoAuthor> parseJsonToCoAuthor(String jsonString) {
		JSONObject jsonObject;
		ArrayList<CoAuthor> list = new ArrayList<CoAuthor>();
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("coAuthorTable");

			for (int i = 0; i < jsonArray.length(); i++) {
				String no = ((JSONObject) jsonArray.opt(i)).getString("0")
						.toString();
				String name = ((JSONObject) jsonArray.opt(i)).getString("1")
						.toString();
				String more = ((JSONObject) jsonArray.opt(i)).getString("2")
						.toString();
				CoAuthor coAuthor = new CoAuthor(no, name, more);
				list.add(coAuthor);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.v("AuthorList", list.toString());
		return list;

	}

	/**
	 * 
	 * @param jsonString
	 * @return
	 */
	public static ArrayList<HistoryProjects> parseJsonToHistoryProjects(
			String jsonString) {
		JSONObject jsonObject;
		ArrayList<HistoryProjects> list = new ArrayList<HistoryProjects>();
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject
					.getJSONArray("projectHistoryTable");

			for (int i = 0; i < jsonArray.length(); i++) {
				String time = ((JSONObject) jsonArray.opt(i)).getString(
						"项目起止年份").toString();
				String projectName = ((JSONObject) jsonArray.opt(i)).getString(
						"项目名称").toString();
				String projectCategory = ((JSONObject) jsonArray.opt(i))
						.getString("项目类别").toString();
				String projectCost = ((JSONObject) jsonArray.opt(i)).getString(
						"经费(万)").toString();
				HistoryProjects data = new HistoryProjects(time, projectName,
						projectCategory, projectCost);
				list.add(data);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.v("ProjectHistoryList", list.toString());
		return list;

	}

	public static ArrayList<String> parseJsonToSameAuthor(String jsonString) {
		JSONObject jsonObject;
		ArrayList<String> list = new ArrayList<String>();
		try {
			jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("sameAuthorTable");

			for (int i = 0; i < jsonArray.length(); i++) {
				for (int j = 0; j < ((JSONObject) jsonArray.opt(i)).length(); j++) {
					String data = ((JSONObject) jsonArray.opt(i)).getString(
							"" + j).toString();
					list.add(data);
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.v("sameAuthorList", list.toString());
		return list;

	}
}
