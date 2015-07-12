package com.chen.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.Utils.HttpUtils;

@SuppressLint("ResourceAsColor")
public class AcademicPaperDetail extends Activity {
	private ImageView backImageView;
	private Dialog dialog;
	private TextView abstractstTextView;
	private Button viewContentsbButton;
	private String contentsUrl;
	private TableLayout paperInfoLayout;
	private ArrayList<HashMap<String, String>> list;
	private LinearLayout linearLayout1;
	private LinearLayout linearLayout2;
	private LinearLayout linearLayout3;
	private LinearLayout linearLayout4;

	Handler detailHandler = new Handler() {
		@SuppressLint("ResourceAsColor")
		public void handleMessage(Message msg) {
			HashMap<String, String> map = (HashMap<String, String>) msg.obj;
			if (map.get("findAbsFlag").equals("no")) {
				Toast.makeText(AcademicPaperDetail.this, "未找到记录", Toast.LENGTH_SHORT).show();
				return;
			}
			abstractstTextView.setText(map.get("desp"));
			if (map.get("findTabFlag").equals("no")) {
				return;
			}
		
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = map.get(key);
				if (key.equals("desp")) {
					continue;

				}

				TableRow tableRow = new TableRow(getApplicationContext());
				tableRow.setBackgroundColor(getResources().getColor(
						R.color.green));// 背景设置色为黑色
				tableRow.setPadding(2, 1, 2, 1);

				TextView th = new TextView(AcademicPaperDetail.this);
				TextView td = new TextView(AcademicPaperDetail.this);

				th.setText(key.substring(0, key.length() - 1));
				th.setTextSize(16);
				th.setBackgroundColor(getResources().getColor(R.color.white));
				th.setTypeface(Typeface.DEFAULT_BOLD);
				th.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT));

				td.setText(value);
				td.setBackgroundColor(getResources().getColor(R.color.white));
				td.setLayoutParams(new TableRow.LayoutParams(
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT));

				View separateView = new View(getApplicationContext());
				separateView.setLayoutParams(new TableRow.LayoutParams(2,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT));

				separateView.setBackgroundColor(getResources().getColor(
						R.color.green));// 背景设置色为黑色

				tableRow.addView(th);
				tableRow.addView(separateView);
				tableRow.addView(td);

				paperInfoLayout.addView(tableRow);
			}
			linearLayout1.setVisibility(View.VISIBLE);
			linearLayout2.setVisibility(View.VISIBLE);
			linearLayout3.setVisibility(View.VISIBLE);
			linearLayout4.setVisibility(View.VISIBLE);
			dialog.dismiss();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_academic_detail);

		list = new ArrayList<HashMap<String, String>>();
		abstractstTextView = (TextView) findViewById(R.id.paper_detail_txt);
		viewContentsbButton = (Button) findViewById(R.id.view_paper_contents_btn);
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		paperInfoLayout = (TableLayout) findViewById(R.id.paper_info_table);
		linearLayout1 = (LinearLayout) findViewById(R.id.about_layout1);
		linearLayout2 = (LinearLayout) findViewById(R.id.about_layout2);
		linearLayout3 = (LinearLayout) findViewById(R.id.about_layout3);
		linearLayout4 = (LinearLayout) findViewById(R.id.about_layout4);
		dialog = CustomDialogUtils.getViewContainer(this).getDialog();
		dialog.setCancelable(false);

		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AcademicPaperDetail.this.finish();
			}
		});
		viewContentsbButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (contentsUrl == null) {
					Toast.makeText(getApplicationContext(), "连接失效",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					Intent intent = new Intent(AcademicPaperDetail.this,
							ViewAcademicPaperContentsActivity.class);
					intent.putExtra("contentsUrl", contentsUrl);

					AcademicPaperDetail.this.startActivity(intent);

				}

			}
		});

		Intent intent = getIntent();
		String urlPathString = intent.getExtras().getString("linkedUrl");
		contentsUrl = intent.getExtras().getString("contentsUrl");
		if (contentsUrl.equals("no")) {
			viewContentsbButton.setVisibility(View.INVISIBLE);
		}
		new Thread(new GetPaperAbstractsThread(urlPathString)).start();
		dialog.show();

	}

	public class GetPaperAbstractsThread implements Runnable {
		private String url;

		public GetPaperAbstractsThread(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			String html = HttpUtils.GetString(url, "utf-8");
			if (html == null) {
				Toast.makeText(getApplicationContext(), "加载失败，请重试！",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// System.out.println(html);
			// 这里用到了Jsoup，解析html字符串得到文档对象
			Document document = Jsoup.parse(html);
			// 得到所有<dl class="booklist">的标签
			Elements elements = document
					.select("div.content_div div.detail_div");
			HashMap<String, String> map = null;
			map = new HashMap<String, String>();
			if (elements == null) {
				Log.v("----->>>>>>是否查找到------>>>>>", "否");
				Message message = Message.obtain();
				map.put("findAbsFlag", "no");

				message.obj = map;
				detailHandler.sendMessage(message);
				return ;
			} else {
				map.put("findAbsFlag", "yes");
				if (elements.select("div#completeAbstract")
						.first()==null) {
					map.put("desp",
							"无");
				}else {
					String detailString = elements.select("div#completeAbstract")
							.first().text();
					map.put("desp",
							detailString.substring(0, detailString.length() - 3));
				}
				if (elements.select("table#perildical2_dl ").first()==null) {
					map.put("findTabFlag", "no");
				}else {
					map.put("findTabFlag", "yes");
					Element tab = elements.select("table#perildical2_dl ").first();
					Elements tr = tab.select("tr");

					
					for (int i = 0; i < tr.size(); i++) {

						String thString = tr.get(i).select("th").first().text();
						String thValueString = tr.get(i).select("td").first()
								.text();
						map.put(thString, thValueString);

					}
				}
				
				
				
				

				Message message = Message.obtain();

				message.obj = map;
				detailHandler.sendMessage(message);
			}

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
