package com.chen.library;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.NetWorkState.NetworkUtils;
import com.chen.library.Utils.HttpUtils;
import com.chen.library.SearchBookActivity.MyAdapter;
import com.chen.library.SearchBookActivity.MyHandler;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SearchPaperActivity extends Activity {
	private MyAdapter adapter;
	private ImageView backImageView;
	private Button searchButton;
	private EditText paperEditText;
	public int currentPageNo = 1;// 记录当前页数
	public int totalPageNo;// 总页数，由解析得到
	public int loadNum = 1;
	public List<Map<String, String>> list = new ArrayList<Map<String, String>>();// ListView总数据
	private TextView nameTextView;
	private TextView authorTextView;
	public String path1 = "http://s.g.wanfangdata.com.cn/Paper.aspx?q=%E9%A2%98%E5%90%8D:";
	public String path2 = "%20DBID:WF_XW&f=c.Thesis&p=";
	private PullToRefreshListView mPullRefreshListView;
	private ArrayAdapter<String> mAdapter;
	public Dialog dialog;
	public List<String> linkList;// 链接集合
	public List<String> contentsLinkList;// 链接集合

	// 实例化MyHandler对象
	MyHandler handler = new MyHandler();

	Handler notificationThreadHandler = new Handler() {
		public void handleMessage(Message msg) {
			mPullRefreshListView.onRefreshComplete();
			Toast.makeText(getApplicationContext(), "已经到底了哦！",
					Toast.LENGTH_SHORT).show();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_paper);
		searchButton = (Button) this.findViewById(R.id.button1);
		paperEditText = (EditText) this.findViewById(R.id.editText1);
		dialog = CustomDialogUtils.getViewContainer(this).getDialog();
		dialog.setCancelable(false);
		adapter = new MyAdapter(list);
		linkList = new ArrayList<String>();
		contentsLinkList = new ArrayList<String>();

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list1);
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);// 向下拉刷新
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						// Do work to refresh the list here.
						// new GetDataTask().execute();
						if (currentPageNo <= totalPageNo) {

							Log.v("----<currentPage", currentPageNo
									+ "------>中共" + totalPageNo);

							String paperName = paperEditText.getText()
									.toString().trim();

							try {
								paperName = URLEncoder.encode(paperName,
										"utf-8");
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
							String urlString = path1 + paperName + path2
									+ currentPageNo;
							Log.v("-----url------>>>>>", urlString);
							new Thread(new MyThread(urlString)).start();
						} else {
							new Thread(new NotificationThread()).start();
						}

					}
				});

		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SearchPaperActivity.this.finish();
			}
		});
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(SearchPaperActivity.this,
						AcademicPaperDetail.class);

				// 传递该书的详情页链接，这只是其中一部分
				intent.putExtra("linkedUrl", linkList.get(position - 1));
				Log.v("点击的连接----》", linkList.get(position - 1));
				intent.putExtra("contentsUrl",
						contentsLinkList.get(position - 1));
				Log.v("点击的连接----》", linkList.get(position - 1) + "----->"
						+ contentsLinkList.get(position - 1));
				// 进入详情页http://d.g.wanfangdata.com.cn/Thesis_D365640.aspx
				// http://d.g.wanfangdata.com.cn/ThesisCatalog.aspx?ID=Thesis_D365640
				SearchPaperActivity.this.startActivity(intent);
			}

		});
		// 点击搜索按钮触发事件
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 首先检查是否联网，这里封装了getNetWorkState()方法，需要android.permission.ACCESS_NETWORK_STATE权限
				if (NetworkUtils.getNetWorkState(SearchPaperActivity.this)) {// 有网络连接
					if (paperEditText.getText().toString().equals("")) {
						// 关键字为空时，提醒输入关键字
						Toast toast = new Toast(SearchPaperActivity.this);
						View view = LayoutInflater.from(
								SearchPaperActivity.this).inflate(
								R.layout.custom_toast, null);
						TextView textView = (TextView) view
								.findViewById(R.id.textView1);
						textView.setText("请输入关键字！");
						toast.setView(view);
						toast.setGravity(Gravity.LEFT | Gravity.TOP, 250, 100);
						toast.setDuration(1000);
						toast.show();

					} else {// 关键字不为空时
						currentPageNo = 1;
						String paperName = paperEditText.getText().toString()
								.trim();

						try {
							paperName = URLEncoder.encode(paperName, "utf-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
						String urlString = path1 + paperName + path2
								+ currentPageNo;
						Log.v("-----url------>>>>>", urlString);
						new Thread(new MyThread(urlString)).start();
						dialog.show();
					}
				} else {// 无网络连接时
					Toast toast = new Toast(SearchPaperActivity.this);
					View view = LayoutInflater.from(SearchPaperActivity.this)
							.inflate(R.layout.custom_toast, null);
					toast.setView(view);
					toast.setGravity(Gravity.LEFT | Gravity.TOP, 250, 100);
					toast.setDuration(1000);
					toast.show();
				}

			}

		});
	}

	// listview自定义的适配器类
	public class MyAdapter extends BaseAdapter {
		private List<Map<String, String>> data;// 数据集

		public MyAdapter(List<Map<String, String>> data) {
			this.data = data;
		}

		// 获取数据集大小
		@Override
		public int getCount() {
			return data.size();
		}

		// 获取item对应的数据
		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		// item的位置，即数据集中的position
		@Override
		public long getItemId(int position) {
			return position;
		}

		// 最重要的获取Itemde的视图，涉及到对象的重用，避免内存泄露
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = null;
			if (convertView == null) {
				view = LayoutInflater.from(SearchPaperActivity.this).inflate(
						R.layout.paper_listview_item, null);

			} else {
				view = convertView;
			}
			nameTextView = (TextView) view.findViewById(R.id.txt_name);
			authorTextView = (TextView) view.findViewById(R.id.txt_author);

			nameTextView.setText(data.get(position).get("up"));
			authorTextView.setText(data.get(position).get("down"));

			return view;
		}
	}

	// 处理数据的handler类
	public class MyHandler extends Handler {

		List<Map<String, String>> listOnce = new ArrayList<Map<String, String>>();

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==110) {
				dialog.dismiss();
				try {
					new Thread().sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Toast.makeText(SearchPaperActivity.this, "未找到相关论文", Toast.LENGTH_SHORT).show();
				return;
				
			}

			listOnce = (List<Map<String, String>>) msg.obj;
			list.addAll(listOnce);// 加入整个集合
			Log.v("url 数组-----》", linkList.toString());

			if (currentPageNo == 1) {// 在获取到第一页数据的时候设置适配器，避免适配器重置后产生一些怪现象，如每次分页后自动显示到第一项
				ListView actualListView = mPullRefreshListView
						.getRefreshableView();
				actualListView.setAdapter(adapter);
				dialog.dismiss();
			}
			mPullRefreshListView.onRefreshComplete();
			adapter.notifyDataSetChanged();// 更新adapter数据变化,如果不写无法分页显示更新的数据
			currentPageNo++;

		}
	}

	// 获取listview数据的线程
	public class MyThread implements Runnable {

		List<Map<String, String>> listOnce;
		private String workPath;

		public MyThread(String workPath) {
			this.workPath = workPath;
			listOnce = new ArrayList<Map<String, String>>();
		}

		@Override
		public void run() {

			String data = HttpUtils.GetString(workPath, "utf-8");
			Log.v("----->>>>>>data------>>>>>", data);
			Document document = Jsoup.parse(data);

			
			Element pageNumElement=document.select("p.pager_space span.page_link").first();
			if (pageNumElement == null) {
				Log.v("----->>>>>>是否查找到------>>>>>", "否");

				Message message = Message.obtain();

				message.what = 110;
				handler.sendMessage(message);
				return;
			} else {
				Elements elements = document
						.select("div#content div#content_div div div ul.list_ul");

				
				if (loadNum == 1) {
					totalPageNo = Integer.parseInt(pageNumElement.text()
							.charAt(2) + "");
					Log.v("----->>>>>>页数------>>>>>", pageNumElement.text()
							.charAt(2) + "");
				}

				for (Element element : elements) {
					Map<String, String> pass_dataMap = new HashMap<String, String>();

					String urlString = element.select("li.title_li").first()
							.getElementsByTag("a").last().attr("href");

					linkList.add(urlString);// 加入链接表

					if (element.select("li.zi a").size() <= 2) {
						contentsLinkList.add("no");// 加入链接表

					} else {

						String contentsString = element.select("li.zi span")
								.last().getElementsByTag("a").first()
								.attr("href");

						contentsLinkList.add(contentsString);// 加入链接表

					}

					String titleString = element.select("li.title_li").first()
							.text();
					String authorString = element.select("li.greencolor")
							.first().text();
					Log.v("----->>>>>>title------>>>>>", titleString);
					Log.v("----->>>>>>author------>>>>>", authorString);
					// 加入集合中
					pass_dataMap.put("up", titleString);
					pass_dataMap.put("down", authorString);

					listOnce.add(pass_dataMap);
					titleString = "";
					authorString = "";
				}
			}

			Message message = Message.obtain();

			message.obj = listOnce;
			message.what = 111;
			handler.sendMessage(message);

		}
	}

	public class NotificationThread implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			notificationThreadHandler.sendEmptyMessage(0);
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
