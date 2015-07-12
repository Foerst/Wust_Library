package com.chen.library;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.HttpPath.UrlPath;
import com.chen.library.Login.LoginUtils;

public class CurrentBorrowActivity extends Activity {
	// private TableLayout currentBorrowTableLayout;
	private LinearLayout layout;
	private ScrollView titleScrollView;
	private Dialog dialog;
	private TableRow.LayoutParams talbeRowLayoutParams = new TableRow.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	private ImageView backImageView;
	Handler formHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			String infoString = (String) msg.obj;
			Log.v("flag", infoString);
			if (infoString.equals("no")) {
				Toast.makeText(getApplicationContext(), "暂无记录",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
				return;
			}

			try {

				JSONObject tmpJsonObject = new JSONObject(infoString);
				JSONArray rowArray = new JSONArray(
						tmpJsonObject.getString("currentBorrow"));
				Log.v("dothisle", "rowArray的长度=====" + rowArray.length());
				for (int i = 0; i < rowArray.length(); i++) {
					TableLayout tableLayout = new TableLayout(
							CurrentBorrowActivity.this);

					JSONObject infObject = rowArray.getJSONObject(i);

					List<String> list = new ArrayList<String>();
					list.add("条码号");
					list.add("题名/责任者");
					list.add("借阅日期");
					list.add("应还日期");
					list.add("续借量");
					list.add("馆藏地");
					list.add("附件");
					list.add("续借");

					for (int j = 0; j < infObject.length(); j++) {

						TableRow tableRow = new TableRow(
								CurrentBorrowActivity.this);
						tableRow.setBackgroundColor(getResources().getColor(
								R.color.green));// 背景设置色为黑色
						tableRow.setLayoutParams(talbeRowLayoutParams);
						tableRow.setPadding(2, 1, 2, 1);
						TextView textView1 = new TextView(
								CurrentBorrowActivity.this);
						textView1.setBackgroundColor(getResources().getColor(
								R.color.white));
						textView1
								.setLayoutParams(new TableRow.LayoutParams(
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
										android.view.ViewGroup.LayoutParams.MATCH_PARENT));
						textView1.setTextSize(16);
						textView1.setBackgroundColor(getResources().getColor(
								R.color.white));
						textView1.setTypeface(Typeface.DEFAULT_BOLD);

						TextView textView2 = new TextView(
								CurrentBorrowActivity.this);
						textView2.setBackgroundColor(getResources().getColor(
								R.color.white));
						textView2
								.setLayoutParams(new TableRow.LayoutParams(
										android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
										android.view.ViewGroup.LayoutParams.MATCH_PARENT));
						if (j < 7) {

							textView2.setText(infObject.getString(list.get(j)));
							textView1.setText(list.get(j));
							tableRow.addView(textView1);
							View separateView = new View(
									getApplicationContext());
							separateView
									.setLayoutParams(new TableRow.LayoutParams(
											2,
											android.view.ViewGroup.LayoutParams.MATCH_PARENT));

							separateView.setBackgroundColor(getResources()
									.getColor(R.color.green));// 背景设置色为黑色

							tableRow.addView(separateView);
							tableRow.addView(textView2);
						}

						else if (j == 7) {
							textView1.setText(list.get(j) + ":");
							Button reborrowButton = new Button(
									getApplicationContext());
							reborrowButton.setText("续借");
							reborrowButton.setBackgroundColor(getResources()
									.getColor(R.color.gray));

							reborrowButton.setTextColor(getResources()
									.getColor(R.color.red));
							reborrowButton
									.setOnClickListener(new View.OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// 开始续借线程
										}
									});
							tableRow.addView(textView1);
							View separateView = new View(
									getApplicationContext());
							separateView
									.setLayoutParams(new TableRow.LayoutParams(
											2,
											android.view.ViewGroup.LayoutParams.MATCH_PARENT));

							separateView.setBackgroundColor(getResources()
									.getColor(R.color.green));// 背景设置色为黑色

							tableRow.addView(separateView);
							tableRow.addView(reborrowButton);
						}

						tableLayout.addView(tableRow);
						tableLayout.setShrinkAllColumns(true);

					}

					titleScrollView.addView(tableLayout);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			dialog.dismiss();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_current_borrow);
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				CurrentBorrowActivity.this.finish();
			}
		});
		layout = (LinearLayout) this.findViewById(R.id.LinearLayout1);
		titleScrollView = (ScrollView) this.findViewById(R.id.title_scrollview);
		titleScrollView.setVerticalScrollBarEnabled(false);// 隐藏垂直滚动条

		dialog = CustomDialogUtils.getViewContainer(this).getDialog();
		new Thread(new GetCurrentBorrowThread()).start();
		dialog.show();

	}

	// 获取用户信息的线程类
	public class GetCurrentBorrowThread implements Runnable {
		@Override
		public void run() {

			String info = LoginUtils.getHtml(UrlPath.BOOK_LST_URL);
			Document document = Jsoup.parse(info);
			Element tbElement = document.select("div#mylib_content table")
					.first();
			String txt = "{" + "currentBorrow" + ":[";
			if (tbElement == null) {
				Message msg = Message.obtain();
				msg.obj = "no";
				formHandler.sendMessage(msg);
				return;
			}

			Elements row = tbElement.getElementsByTag("tr");
			Elements col = row.first().getElementsByTag("td");

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
