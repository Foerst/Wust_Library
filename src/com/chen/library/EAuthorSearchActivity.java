package com.chen.library;

import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;

import com.chen.library.NetWorkState.NetworkUtils;
import com.chen.library.Utils.HttpUtils;

public class EAuthorSearchActivity extends Activity {
	private ProgressDialog progressDialog;
	// private ImageButton searchImagebButton;
	private Button searchButton;
	private ImageView backImageView;
	private EditText editTextAuthor;
	private TextView txtTitle;
	private TableLayout tableLayoutPaper;
	private TableLayout tableLayoutPaper1;
	private TableLayout tableLayoutPaper2;
	private TableLayout tableLayoutPaper3;
	private TableLayout tableLayoutPaper4;
	private String coAuthors = "";
	private String searchAuthor;
	private Button btnShowPic;
	private LinearLayout layout;
	private TableRow.LayoutParams talbeRowLayoutParams = new TableRow.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	final String urlString = "http://1.dblptest.sinaapp.com/1/dblptest/servlet/SaeServlet?authorName=";

	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			String papaerInfo = (String) msg.obj;
			if (papaerInfo == null) {
				Toast.makeText(getApplicationContext(), "抱歉，未连接到数据库",
						Toast.LENGTH_SHORT).show();
				progressDialog.dismiss();
				return;
			}

			if (papaerInfo.equals("!no!")) {
				searchAuthor = editTextAuthor.getText().toString();
				progressDialog.dismiss();
				searchButton.setEnabled(true);
				Toast.makeText(EAuthorSearchActivity.this, "数据库无此数据,将跳转至网页引擎",
						Toast.LENGTH_SHORT).show();
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
				Intent intent = new Intent(EAuthorSearchActivity.this,
						CDblpSearchActivity.class);
				intent.putExtra("authorName", searchAuthor);
				EAuthorSearchActivity.this.startActivity(intent);

			} else {

				try {
					JSONObject tmpJsonObject = new JSONObject(papaerInfo);
					JSONArray infoArray = new JSONArray(
							tmpJsonObject.getString("paperInfo"));

					for (int i = 0; i < infoArray.length(); i++) {
						JSONObject infObject = infoArray.getJSONObject(i);
						coAuthors += infObject.getString("authors") + ",";
						searchAuthor = infObject.getString("presentAuthor");

						for (int j = 0; j < 7; j++) {

							TableRow tableRow = new TableRow(
									EAuthorSearchActivity.this);
							tableRow.setBackgroundColor(getResources()
									.getColor(R.color.black));// 背景设置色为黑色
							tableRow.setLayoutParams(talbeRowLayoutParams);
							tableRow.setPadding(1, 1, 1, 1);

							TableRow tableRow1 = new TableRow(
									EAuthorSearchActivity.this);
							tableRow1.setBackgroundColor(getResources()
									.getColor(R.color.black));// 背景设置色为黑色
							tableRow1.setLayoutParams(talbeRowLayoutParams);
							tableRow1.setPadding(1, 1, 1, 1);

							TextView tv = new TextView(
									EAuthorSearchActivity.this);
							tv.setBackgroundColor(getResources().getColor(
									R.color.white));
							TextView title = new TextView(
									EAuthorSearchActivity.this);
							title.setBackgroundColor(getResources().getColor(
									R.color.white));
							switch (j) {
							case 0:
								title.setText("论文编号:");
								tv.setText(infObject.getString("id"));
								break;
							case 1:
								title.setText("论文标题:");
								tv.setText(infObject.getString("paperTitle"));
								break;
							case 2:
								title.setText("摘要:");
								tv.setText(infObject.getString("abstracts"));
								break;
							case 3:
								title.setText("协同作者:");
								tv.setText(infObject.getString("authors"));
								break;
							case 4:
								title.setText("发布时间:");
								tv.setText(infObject.getString("year"));
								break;
							case 5:
								title.setText("参考论文编号:");
								tv.setText(infObject
										.getString("referencedIndexes"));
								break;
							case 6:
								title.setText("发布地点:");
								tv.setText(infObject.getString("venue"));
								break;

							default:
								break;
							}

							tableRow.addView(tv);
							tableRow1.addView(title);
							switch (i) {
							case 0:
								tableLayoutPaper.addView(tableRow1);
								tableLayoutPaper.addView(tableRow);
								break;
							case 1:
								tableLayoutPaper1.addView(tableRow1);
								tableLayoutPaper1.addView(tableRow);
								break;
							case 2:
								tableLayoutPaper2.addView(tableRow1);
								tableLayoutPaper2.addView(tableRow);
								break;
							case 3:
								tableLayoutPaper3.addView(tableRow1);
								tableLayoutPaper3.addView(tableRow);
								break;
							case 4:
								tableLayoutPaper4.addView(tableRow1);
								tableLayoutPaper4.addView(tableRow);
								break;
							default:
								break;
							}

						}

					}

					TextPaint textPaint = txtTitle.getPaint();
					textPaint.setFakeBoldText(true);
					txtTitle.setVisibility(View.VISIBLE);
					txtTitle.setTextSize(15.0f);
					btnShowPic.setVisibility(View.VISIBLE);
					progressDialog.dismiss();
					searchButton.setEnabled(true);
					System.out.println("coauthors---------->" + coAuthors);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_eauthor_search);
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				EAuthorSearchActivity.this.finish();
			}
		});
		searchButton = (Button) this.findViewById(R.id.imageButtonSearch);
		editTextAuthor = (EditText) this.findViewById(R.id.editTextAuthor);
		txtTitle = (TextView) this.findViewById(R.id.textViewTitle);
		tableLayoutPaper = (TableLayout) this
				.findViewById(R.id.tableLayoutPaper);
		tableLayoutPaper1 = (TableLayout) this
				.findViewById(R.id.tableLayoutPaper1);
		tableLayoutPaper2 = (TableLayout) this
				.findViewById(R.id.tableLayoutPaper2);
		tableLayoutPaper3 = (TableLayout) this
				.findViewById(R.id.tableLayoutPaper3);
		tableLayoutPaper4 = (TableLayout) this
				.findViewById(R.id.tableLayoutPaper4);
		btnShowPic = (Button) this.findViewById(R.id.btnShowPic);
		layout = (LinearLayout) this.findViewById(R.id.layout);
		progressDialog = new ProgressDialog(this);

		btnShowPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (searchAuthor != null && coAuthors != null) {
					String[] tmpStrings = coAuthors.substring(0,
							coAuthors.length() - 1).split(",");
					// int location = -1;
					// for (int i = 0; i < tmpStrings.length; i++) {
					// if (tmpStrings[i].equals(searchAuthor)) {
					// location = i;
					// }
					// }

					ArrayList<String> intentDataList = new ArrayList<String>();
					intentDataList.add(searchAuthor);
					for (int i = 0; i < tmpStrings.length; i++) {
						if (!tmpStrings[i].equals(searchAuthor)) {
							intentDataList.add(tmpStrings[i]);
						}
					}

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("coAuthors", intentDataList);
					System.out.println(intentDataList.toString());
					intent.putExtra("bundle", bundle);
					intent.setClass(EAuthorSearchActivity.this,
							ShowCoAuthorsPicActivity.class);
					EAuthorSearchActivity.this.startActivity(intent);

				}
			}
		});

		searchButton.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				String authorName = editTextAuthor.getText().toString();
				System.out.println(authorName);
				if (NetworkUtils.getNetWorkState(EAuthorSearchActivity.this)) {
					if (!authorName.equals("")) {
						tableLayoutPaper.removeAllViewsInLayout();
						coAuthors = "";
						authorName = URLEncoder.encode(authorName);
						new Thread(new MyThread(urlString + authorName))
								.start();
						searchButton.setEnabled(false);
						progressDialog.show();
						progressDialog.setCancelable(false);
					} else {
						Toast.makeText(EAuthorSearchActivity.this, "请输入关键字",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(EAuthorSearchActivity.this, "请连接网络",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	public class MyThread implements Runnable {

		private String url;

		public MyThread(String url) {
			this.url = url;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			String returnData = HttpUtils.GetString(url, "utf-8");

			Message msg = new Message();
			msg.obj = returnData;
			handler.sendMessage(msg);

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
