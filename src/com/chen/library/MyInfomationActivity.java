package com.chen.library;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.HttpPath.UrlPath;
import com.chen.library.Login.LoginUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class MyInfomationActivity extends Activity {
	private Dialog dialog;
	private TableLayout infoTableLayout;
	private TableRow.LayoutParams talbeRowLayoutParams = new TableRow.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	private List<String> infoTitleList;
	private ImageView backImageView;
	Handler formHandler = new Handler() {
		@SuppressLint("NewApi")
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			String infoString = (String) msg.obj;
			Log.v("flag", infoString);
			try {
				JSONObject tmpJsonObject = new JSONObject(infoString);
				JSONArray rowArray = new JSONArray(
						tmpJsonObject.getString("userInfo"));
				
				Log.v("jsonArray---->", rowArray.toString());
				int keyFlag = 0;
				for (int i = 0; i < rowArray.length(); i++) {
					JSONObject infObject = rowArray.getJSONObject(i);

					for (int j = 0; j < infObject.length(); j++) {

						String titleString = infoTitleList.get(keyFlag);
						TextView titleTextView = new TextView(
								MyInfomationActivity.this);

						titleTextView.setText(titleString);
						titleTextView.setTextSize(16);
						titleTextView.setBackgroundColor(getResources()
								.getColor(R.color.white));
						titleTextView.setTypeface(Typeface.DEFAULT_BOLD);
						titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

						titleTextView
								.setLayoutParams(new TableRow.LayoutParams(
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
										50));

						TextView contentTextView = new TextView(
								MyInfomationActivity.this);
						contentTextView.setBackgroundColor(getResources()
								.getColor(R.color.white));
						contentTextView.setText(infObject
								.getString(titleString));
						Log.v("------>title", titleString + "------->value"
								+ infObject.getString(titleString));
						contentTextView.setTextSize(14);

						contentTextView
								.setLayoutParams(new TableRow.LayoutParams(
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
										android.view.ViewGroup.LayoutParams.MATCH_PARENT));

						TableRow tableRow = new TableRow(
								MyInfomationActivity.this);
						tableRow.setPadding(2, 1, 2, 1);
						tableRow.setBackgroundColor(getResources().getColor(
								R.color.green));// 背景设置色为green
						tableRow.setLayoutParams(talbeRowLayoutParams);

						

						tableRow.addView(titleTextView);
						
						View separateView = new View(getApplicationContext());
						separateView.setLayoutParams(new TableRow.LayoutParams(2,
								android.view.ViewGroup.LayoutParams.MATCH_PARENT));

						separateView.setBackgroundColor(getResources().getColor(
								R.color.green));// 背景设置色为green

						tableRow.addView(separateView);

						tableRow.addView(contentTextView);
						infoTableLayout.addView(tableRow);
						keyFlag++;

					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			dialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_information);

		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyInfomationActivity.this.finish();
			}
		});
		infoTitleList = getInfoKeySet();
		infoTableLayout = (TableLayout) this.findViewById(R.id.tableLayoutInfo);
		dialog = CustomDialogUtils.getViewContainer(this).getDialog();
		new Thread(new GetInfoThread()).start();
		dialog.show();

	}

	// 从xml文件中获取数据
	public List<String> getInfoKeySet() {
		List<String> data = new ArrayList<String>();
		String[] tempString = getResources().getStringArray(R.array.mylibinfo);
		for (int i = 0; i < tempString.length; i++) {
			data.add(tempString[i]);
		}
		return data;
	}

	// 获取用户信息的线程类
	public class GetInfoThread implements Runnable {
		@Override
		public void run() {

			String info = LoginUtils.getHtml(UrlPath.REDR_INFO_URL);
			Document document = Jsoup.parse(info);
			Element tbElement = document.select("div#mylib_content table")
					.first();
			
			String txt = "{" + "userInfo" + ":[";
			Elements row = tbElement.getElementsByTag("tr");
		
			for (int j = 0; j < row.size(); j++) {
				String r = "{";
				Elements tds = row.get(j).getElementsByTag("td");
				for (int i = 0; i < tds.size(); i++) {
					if (tds.get(i).select("span.bluetext") != null) {

						String key = tds.get(i).select("span").first().text()
								.toString();
						String value = tds.get(i).text().toString();
						value = value.replace(key, "");
						System.out.println("key====" + key + ":value======"
								+ value);

						r += "\"" + key + "\":\"" + value + "\",";
					} else {
						continue;
					}

				}
				r = r.substring(0, r.length() - 1);
				r += "},";
				txt += r;
			}
			txt = txt.substring(0, txt.length() - 1);
			txt += "]}";
			Log.v("json--->", txt);

		
		

			Message msg = Message.obtain();
			msg.obj = txt;
			formHandler.sendMessage(msg);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			this.finish();

		}

		return false;

	}

}
