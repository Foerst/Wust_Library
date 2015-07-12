package com.chen.library;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.DataClass.CoAuthor;
import com.chen.library.DataClass.HistoryProjects;
import com.chen.library.DataClass.Paper;
import com.chen.library.Json.JsonUtils;
import com.chen.library.Jsoup.JsoupUtils;
import com.chen.library.NetWorkState.NetworkUtils;
import com.chen.library.Utils.HttpUtils;

public class CDblpSearchActivity extends Activity {

	String urlString1 = "http://cdblp.cn/search_result.php?author_name=";
	String urlString2 = "&domain=computer";
	private EditText txtAuthor;
	private Button searchButton;
	private Dialog progressDialog;
	private TableLayout tableLayoutCoAuthor;
	private TableLayout tableLayoutHistory;
	private TableLayout tableLayoutPaper;
	private TableLayout tableLayoutSameAuthor;
	private ImageView backImageView;
	private TextView textViewCoAuthors;
	private TextView textViewHistory;
	private TextView textViewPaper;
	private TextView tvSameAuthor;
	private TextView tvPic;
	private ImageView imgPaperPic;
	private TableRow.LayoutParams talbeRowLayoutParams = new TableRow.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	Handler handler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(android.os.Message msg) {

			Map<String, ArrayList<?>> map = (Map<String, ArrayList<?>>) msg.obj;
			if (map.get("flag").get(0).equals("noFounded")) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "未搜索到数据",
						Toast.LENGTH_LONG).show();

				return;
			}
			ArrayList<CoAuthor> coAuthorsList = new ArrayList<CoAuthor>();
			ArrayList<HistoryProjects> historyProjectsList = new ArrayList<HistoryProjects>();
			ArrayList<Paper> paperList = new ArrayList<Paper>();
			ArrayList<String> sameAuthorList = new ArrayList<String>();
			// @SuppressWarnings("unused")
			// ArrayList<byte[]> imageByteArrayList = new ArrayList<byte[]>();

			coAuthorsList = (ArrayList<CoAuthor>) map.get("coAuthorsList");
			historyProjectsList = (ArrayList<HistoryProjects>) map
					.get("historyProjectsList");
			paperList = (ArrayList<Paper>) map.get("paperList");
			sameAuthorList = (ArrayList<String>) map.get("sameAuthorList");
			// imageByteArrayList = (ArrayList<byte[]>)
			// map.get("imageByteArrayList");

			for (int i = 0; i < coAuthorsList.size(); i++) {

				TableRow tableRow = new TableRow(CDblpSearchActivity.this);
				tableRow.setBackgroundColor(getResources().getColor(
						R.color.black));//
				// 背景设置色为黑色
				tableRow.setLayoutParams(talbeRowLayoutParams);
				tableRow.setPadding(1, 1, 1, 1);

				TextView textViewNo = new TextView(CDblpSearchActivity.this);
				// textViewNo.setPadding(10, 0, 10, 0);
				textViewNo.setLayoutParams(new LayoutParams(100,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				textViewNo.setGravity(Gravity.CENTER);
				TextView textViewName = new TextView(CDblpSearchActivity.this);
				textViewName.setLayoutParams(new LayoutParams(200,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				textViewName.setGravity(Gravity.CENTER);
				TextView textViewMore = new TextView(CDblpSearchActivity.this);
				textViewMore.setLayoutParams(new LayoutParams(100,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				// textViewMore.setPadding(0, 10, 0, 10);

				textViewNo.setText(coAuthorsList.get(i).getNo().toString());
				textViewNo.setBackgroundColor(getResources().getColor(
						R.color.gray));

				textViewName.setText(coAuthorsList.get(i).getName().toString());
				// textViewName.setTextColor(Color.RED);
				textViewName.setTextSize(14.0f);
				textViewName.setBackgroundColor(getResources().getColor(
						R.color.white));

				textViewMore.setText(coAuthorsList.get(i).getMore().toString());
				textViewMore.setBackgroundColor(getResources().getColor(
						R.color.white));

				tableRow.addView(textViewNo);
				tableRow.addView(textViewName);
				tableRow.addView(textViewMore);
				tableLayoutCoAuthor.addView(tableRow);

			}
			TextPaint textPaint = textViewCoAuthors.getPaint();
			textPaint.setFakeBoldText(true);
			textViewCoAuthors.setVisibility(View.VISIBLE);

			textViewCoAuthors.setTextSize(15.0f);
			if (historyProjectsList.get(0).getFlag() == 1) {
				for (int i = 1; i < historyProjectsList.size(); i++) {

					TableRow tableRow1 = new TableRow(CDblpSearchActivity.this);
					TableRow tableRow2 = new TableRow(CDblpSearchActivity.this);
					TableRow tableRow3 = new TableRow(CDblpSearchActivity.this);
					TableRow tableRow4 = new TableRow(CDblpSearchActivity.this);

					tableRow1.setBackgroundColor(getResources().getColor(
							R.color.black));
					tableRow1.setLayoutParams(talbeRowLayoutParams);
					tableRow1.setPadding(1, 1, 1, 1);

					tableRow2.setBackgroundColor(getResources().getColor(
							R.color.black));
					tableRow2.setLayoutParams(talbeRowLayoutParams);
					tableRow2.setPadding(1, 1, 1, 1);

					tableRow3.setBackgroundColor(getResources().getColor(
							R.color.black));
					tableRow3.setLayoutParams(talbeRowLayoutParams);
					tableRow3.setPadding(1, 1, 1, 1);

					tableRow4.setBackgroundColor(getResources().getColor(
							R.color.black));
					tableRow4.setLayoutParams(talbeRowLayoutParams);
					tableRow4.setPadding(1, 1, 1, 1);

					TextView textViewTime = new TextView(
							CDblpSearchActivity.this);
					textViewTime.setBackgroundColor(getResources().getColor(
							R.color.white));

					TextView textViewProjectName = new TextView(
							CDblpSearchActivity.this);
					textViewProjectName.setBackgroundColor(getResources()
							.getColor(R.color.white));

					TextView textViewProjectCategory = new TextView(
							CDblpSearchActivity.this);
					textViewProjectCategory.setBackgroundColor(getResources()
							.getColor(R.color.white));

					TextView textViewCost = new TextView(
							CDblpSearchActivity.this);
					textViewCost.setBackgroundColor(getResources().getColor(
							R.color.white));

					textViewTime.setText("项目起止年份:\n"
							+ historyProjectsList.get(i).getTime().toString());
					textViewTime.setTextSize(12.0f);
					tableRow1.addView(textViewTime);

					textViewProjectName.setText("项目名称:\n"
							+ historyProjectsList.get(i).getProjectName()
									.toString());
					// textViewProjectName.setTextColor(Color.CYAN);
					textViewProjectName.setSingleLine(false);
					tableRow2.addView(textViewProjectName);

					textViewProjectCategory.setText("项目类别:\n"
							+ historyProjectsList.get(i).getProjectCategory()
									.toString());
					textViewProjectCategory.setTextSize(12.0f);
					tableRow3.addView(textViewProjectCategory);

					textViewCost.setText("经费(万):\n"
							+ historyProjectsList.get(i).getProjectCost()
									.toString());
					textViewCost.setTextSize(12.0f);
					tableRow4.addView(textViewCost);

					tableLayoutHistory.addView(tableRow1);
					tableLayoutHistory.addView(tableRow2);
					tableLayoutHistory.addView(tableRow3);
					tableLayoutHistory.addView(tableRow4);

				}
				TextPaint textPaint1 = textViewHistory.getPaint();
				textPaint1.setFakeBoldText(true);
				textViewHistory.setVisibility(View.VISIBLE);
			}

			for (int i = 0; i < paperList.size(); i++) {
				TableRow tableRow1 = new TableRow(CDblpSearchActivity.this);
				tableRow1.setBackgroundColor(getResources().getColor(
						R.color.black));
				tableRow1.setLayoutParams(talbeRowLayoutParams);
				tableRow1.setPadding(1, 1, 1, 1);

				TableRow tableRow2 = new TableRow(CDblpSearchActivity.this);
				tableRow2.setBackgroundColor(getResources().getColor(
						R.color.black));
				tableRow2.setLayoutParams(talbeRowLayoutParams);
				tableRow2.setPadding(1, 1, 1, 1);

				TextView textViewNO = new TextView(CDblpSearchActivity.this);
				textViewNO.setBackgroundColor(getResources().getColor(
						R.color.white));

				TextView textViewInfo = new TextView(CDblpSearchActivity.this);
				textViewInfo.setBackgroundColor(getResources().getColor(
						R.color.white));

				if (paperList.get(i).getInfo().toString().equals("")) {
					textViewNO.setText(paperList.get(i).getNo() + "年");
					tableRow1.addView(textViewNO);
					tableLayoutPaper.addView(tableRow1);
				} else {
					textViewNO.setText("No："
							+ paperList.get(i).getNo().toString().trim());
					textViewInfo.setText("Paper Infomation:\n"
							+ paperList.get(i).getInfo().toString().trim());
					tableRow1.addView(textViewNO);
					tableRow2.addView(textViewInfo);
					tableLayoutPaper.addView(tableRow1);
					tableLayoutPaper.addView(tableRow2);
				}

			}
			TextPaint textPaint2 = textViewPaper.getPaint();
			textPaint2.setFakeBoldText(true);
			textViewPaper.setVisibility(View.VISIBLE);

			for (int i = 0; i < sameAuthorList.size(); i++) {
				TableRow tableRow1 = new TableRow(CDblpSearchActivity.this);
				tableRow1.setBackgroundColor(getResources().getColor(
						R.color.black));
				tableRow1.setLayoutParams(talbeRowLayoutParams);
				tableRow1.setPadding(1, 1, 1, 1);

				TextView tvAuthorName = new TextView(CDblpSearchActivity.this);
				tvAuthorName.setBackgroundColor(getResources().getColor(
						R.color.white));

				tvAuthorName.setText(sameAuthorList.get(i).toString().trim());

				tableRow1.addView(tvAuthorName);
				tableLayoutSameAuthor.addView(tableRow1);

			}
			TextPaint textPaint3 = tvSameAuthor.getPaint();
			textPaint3.setFakeBoldText(true);
			tvSameAuthor.setVisibility(View.VISIBLE);

			tvPic.setText(txtAuthor.getText().toString().trim()
					+ "论文数量曲线图(按年份)");
			tvPic.setVisibility(View.VISIBLE);

			progressDialog.dismiss();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 因为要自定义标题，所以去掉title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 加载布局文件
		setContentView(R.layout.activity_cdblp_search);
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				CDblpSearchActivity.this.finish();
			}
		});
		progressDialog = CustomDialogUtils.getViewContainer(this).getDialog();
		txtAuthor = (EditText) this.findViewById(R.id.editTextAuthor);

		searchButton = (Button) findViewById(R.id.search_btn);
		tableLayoutCoAuthor = (TableLayout) this
				.findViewById(R.id.tableLayoutCoAuthors);
		tableLayoutHistory = (TableLayout) this
				.findViewById(R.id.tableLayoutHistory);
		tableLayoutPaper = (TableLayout) this
				.findViewById(R.id.tableLayoutPaper);
		tableLayoutSameAuthor = (TableLayout) this
				.findViewById(R.id.tableLayoutSameAthor);

		textViewCoAuthors = (TextView) this
				.findViewById(R.id.textViewCoAuthors);
		textViewHistory = (TextView) this.findViewById(R.id.textViewHistory);
		textViewPaper = (TextView) this.findViewById(R.id.textViewPaper);
		tvSameAuthor = (TextView) this.findViewById(R.id.tvSameAthor);
		tvPic = (TextView) this.findViewById(R.id.tvPic);
		imgPaperPic = (ImageView) this.findViewById(R.id.imgPaperPic);

		// Intent intent = getIntent();
		// txtAuthor.setText(intent.getExtras().getString("authorName"));
		Intent intent = getIntent();
		if (intent.getExtras() != null) {
			txtAuthor.setText(intent.getExtras().getString("authorName"));
		}

		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (NetworkUtils.getNetWorkState(CDblpSearchActivity.this)) {
					String authorString = txtAuthor.getText().toString().trim();
					authorString = URLEncoder.encode(authorString);

					if (!authorString.equals("")) {
						tableLayoutCoAuthor.removeAllViewsInLayout();
						tableLayoutHistory.removeAllViewsInLayout();
						tableLayoutPaper.removeAllViewsInLayout();
						tableLayoutSameAuthor.removeAllViewsInLayout();

						new Thread(new MyThread(urlString1 + authorString
								+ urlString2)).start();
						progressDialog.show();
					} else {
						Toast.makeText(CDblpSearchActivity.this, "请输入关键字",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(CDblpSearchActivity.this, "请检查网络连接！",
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
			String dataToReturn = "";
			dataToReturn = HttpUtils.doGetString(url, "utf-8");
			Log.v("----->html", dataToReturn);
			Document document = Jsoup.parse(dataToReturn);
			int tabNum = document.select("table").size();
			Log.v("table个数----->>", tabNum + "");

			if (tabNum == 3 || tabNum == 4) {
				Map<String, ArrayList<?>> attachedMap = new HashMap();
				ArrayList<String> list = new ArrayList<String>();
				list.add(0, "noFounded");

				attachedMap.put("flag", list);
				Message msg = new Message();
				msg.obj = attachedMap;
				handler.sendMessage(msg);
				return;
			}

			String coAuthorTableJson = JsonUtils.getJsonStringFromHtml(
					dataToReturn, "table#projectHistory", "coAuthorTable");

			ArrayList<CoAuthor> coAuthorsList = new ArrayList<CoAuthor>();
			coAuthorsList = JsonUtils.parseJsonToCoAuthor(coAuthorTableJson);

			String historyProjectsTableJson = JsonUtils
					.getJsonStringFromHtml(dataToReturn,
							"table#projectHistory", "projectHistoryTable");
			ArrayList<HistoryProjects> historyProjectsList = new ArrayList<HistoryProjects>();
			if (historyProjectsTableJson.equals("noContent")) {
				HistoryProjects historyProjects = new HistoryProjects();
				historyProjects.setFlag(0);
				historyProjectsList.add(historyProjects);
			} else {
				historyProjectsList = JsonUtils
						.parseJsonToHistoryProjects(historyProjectsTableJson);
			}

			String paperTableJson = JsonUtils.getJsonStringFromHtml(
					dataToReturn, "table.paper", "paperTable");
			ArrayList<Paper> paperList = JsonUtils
					.parseJsonToPaper(paperTableJson);

			String sameAuthorJson = JsonUtils.getJsonStringFromHtml(
					dataToReturn, "table#projectHistory", "sameAuthorTable");
			ArrayList<String> sameAuthorList = JsonUtils
					.parseJsonToSameAuthor(sameAuthorJson);

			String selector = "div#col3_content img";
			String src = JsoupUtils.getImageSrcFormHtml(dataToReturn, selector);
			Log.v("--->src", src);
			byte[] imageData = null;
			// if (src != null) {
			// imageData = HttpUtils.doGetByteArray(src);
			// }

			ArrayList<byte[]> imageByteArrayList = new ArrayList<byte[]>();
			// imageByteArrayList.add(imageData);

			Map<String, ArrayList<?>> attachedMap = new HashMap();
			attachedMap.put("coAuthorsList", coAuthorsList);
			attachedMap.put("historyProjectsList", historyProjectsList);
			attachedMap.put("paperList", paperList);
			attachedMap.put("sameAuthorList", sameAuthorList);

			ArrayList<String> list = new ArrayList<String>();
			list.add(0, "Founded");
			attachedMap.put("flag", list);
			// attachedMap.put("imageByteArrayList", imageByteArrayList);

			Message msg = new Message();
			msg.obj = attachedMap;
			handler.sendMessage(msg);

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			this.finish();

		}

		return false;

	}

}
