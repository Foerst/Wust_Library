package com.chen.library;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.HttpPath.UrlPath;
import com.chen.library.NetWorkState.NetworkUtils;
import com.chen.library.Utils.HttpUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

//搜索页面
public class SearchBookActivity extends Activity {
	private ListView listView;
	private MyAdapter adapter;
	private TextView textView;
	private EditText editText;
	private ImageView imageView;
	private TextView textView1;
	public List<String> linkList = new ArrayList<String>();// 链接集合
	public List<Map<String, String>> list = new ArrayList<Map<String, String>>();// ListView总数据
	private boolean isDevide = false;// 是否分页的标记
	private Spinner spinner;
	private Button button;
	public int currentPageNo = 1;// 记录当前页数
	public int totalPageNo;// 总页数，由解析得到
	private ImageView backImageView;
	private Dialog dialog;
	private int titleFlag;
	// 实例化MyHandler对象
	MyHandler handler = new MyHandler();

	Handler getImageUrlHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			String urlString = (String) msg.obj;

			new Thread(new ImageThread(urlString)).start();

		};
	};
	// 更新listview图片
	Handler imageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			byte[] data = (byte[]) msg.obj;
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			imageView.setImageBitmap(bitmap);
		};
	};

	// 添加检索结果数
	Handler handlerResultNo = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Map<String, String> map = (Map<String, String>) msg.obj;
			String noString = map.get("listItemNumber");
			totalPageNo = Integer.parseInt(map.get("pageNo"));
			textView1.setTextSize(12);
			textView1.setText("共检索到" + noString + "条结果,共" + totalPageNo + "页");

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加载布局文件
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_book);
		dialog = CustomDialogUtils.getViewContainer(this).getDialog();
		backImageView = (ImageView) findViewById(R.id.about_us_back);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SearchBookActivity.this.finish();
			}
		});

		listView = (ListView) findViewById(R.id.listView1);
		button = (Button) this.findViewById(R.id.button1);
		editText = (EditText) this.findViewById(R.id.editText1);
		textView1 = (TextView) this.findViewById(R.id.textView1);
		titleFlag = 1;
		adapter = new MyAdapter(list);
		// spinner的适配器
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getSpinnerData());

		// 设置下拉样式，亦可自定义
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 点击搜索按钮触发事件
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 首先检查是否联网，这里封装了getNetWorkState()方法，需要android.permission.ACCESS_NETWORK_STATE权限
				if (NetworkUtils.getNetWorkState(SearchBookActivity.this)) {// 有网络连接
					if (editText.getText().toString().equals("")) {
						// 关键字为空时，提醒输入关键字
						Toast toast = new Toast(SearchBookActivity.this);
						View view = LayoutInflater
								.from(SearchBookActivity.this).inflate(
										R.layout.custom_toast, null);
						TextView textView = (TextView) view
								.findViewById(R.id.textView1);
						textView.setText("请输入关键字！");
						toast.setView(view);
						toast.setGravity(Gravity.LEFT | Gravity.TOP, 250, 100);
						toast.setDuration(1000);
						toast.show();

					} else {// 关键字不为空时

						SearchBookActivity.this.list.clear();
						
						adapter.notifyDataSetChanged();// 更新adapter数据变化,如果不写无法分页显示更新的数据
						currentPageNo = 1;
						dialog.show();
						dialog.setCancelable(false);
						titleFlag=1;
						String bookName = editText.getText().toString().trim();
						try {
							bookName = URLEncoder.encode(bookName, "utf-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
//						// 开启获取页数与总条数的线程
//						new Thread(new GetTotalItemThread(UrlPath.PAGE_URL1
//								+ bookName + UrlPath.PAGE_URL2 + currentPageNo))
//								.start();
						// 开启解析线程
						new Thread(new MyThread(UrlPath.PAGE_URL1 + bookName
								+ UrlPath.PAGE_URL2 + currentPageNo)).start();

					}
				} else {// 无网络连接时
					Toast toast = new Toast(SearchBookActivity.this);
					View view = LayoutInflater.from(SearchBookActivity.this)
							.inflate(R.layout.custom_toast, null);
					toast.setView(view);
					toast.setGravity(Gravity.LEFT | Gravity.TOP, 250, 100);
					toast.setDuration(1000);
					toast.show();
				}

			}

		});
		// 点击表选项事件
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("-->点击了" + position);
				Intent intent = new Intent(SearchBookActivity.this,
						DescribePageActivity.class);
				for (int i = 0; i < linkList.size(); i++) {
					System.out.println(linkList.get(i) + "-->" + i);
				}
				// 传递该书的详情页链接，这只是其中一部分
				intent.putExtra("linkedUrl", linkList.get(position));
				// 进入详情页
				SearchBookActivity.this.startActivity(intent);
			}
		});
		// 添加滑动事件，判断是否进行分页
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 该处的判断十分重要也是关键
				if (isDevide
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					currentPageNo++;// 当前页加1

					if (currentPageNo <= totalPageNo) {// 做一些提醒工作
						Toast.makeText(getApplicationContext(), "努力加载中......",
								Toast.LENGTH_LONG).show();

						// 开启获取下页内容的线程，url拼凑得到
						new Thread(new MyThread(UrlPath.PAGE_URL1
								+ editText.getText().toString().trim()
								+ UrlPath.PAGE_URL2 + currentPageNo)).start();
					} else {// 做一些提醒工作
						Toast.makeText(getApplicationContext(), "已经到底了哦！",
								Toast.LENGTH_SHORT).show();
					}

				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 改变分页标记
				isDevide = (firstVisibleItem + visibleItemCount == totalItemCount);

			}
		});

	}

	// 从xml文件中获取spinner的数据
	public List<String> getSpinnerData() {
		List<String> data = new ArrayList<String>();
		String[] tempString = getResources().getStringArray(R.array.according);
		for (int i = 0; i < tempString.length; i++) {
			data.add(tempString[i]);
		}
		return data;
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
				view = LayoutInflater.from(SearchBookActivity.this).inflate(
						R.layout.item, null);

			} else {
				view = convertView;
			}
			textView = (TextView) view.findViewById(R.id.textView1);
			imageView = (ImageView) view.findViewById(R.id.imageView1);

			// 这里textView要显示两种字体颜色，一中为HTML形式，一种为文本形式
			String s1 = "<font color=\"red\" face=\"宋体\" size=\"3\">"
					+ data.get(position).get("up").substring(4) + "</font>";
			textView.setText(Html.fromHtml(s1));
			textView.append(data.get(position).get("down"));

			return view;
		}
	}

	// 处理数据的handler类
	public class MyHandler extends Handler {

		List<Map<String, String>> list_get_every_time = new ArrayList<Map<String, String>>();

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 110) {
				Toast.makeText(SearchBookActivity.this, "未搜索到记录",
						Toast.LENGTH_SHORT).show();
				textView1.setTextSize(12);
				textView1.setText("共检索到" + 0 + "条结果,共" + 0 + "页");
				dialog.dismiss();
				return;

			}
			list_get_every_time = (List<Map<String, String>>) msg.obj;
			list.addAll(list_get_every_time);// 加入整个集合

			if (currentPageNo == 1) {// 在获取到第一页数据的时候设置适配器，避免适配器重置后产生一些怪现象，如每次分页后自动显示到第一项
				listView.setAdapter(adapter);
			}
			adapter.notifyDataSetChanged();// 更新adapter数据变化,如果不写无法分页显示更新的数据
			dialog.dismiss();
		}
	}

	// 获取网络图片的线程
	public class ImageThread implements Runnable {
		public String image_path;

		public ImageThread(String image_path) {
			this.image_path = image_path;
		}

		@Override
		public void run() {

			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(image_path);
			try {
				HttpResponse httpResponse = httpClient.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					byte[] data = EntityUtils.toByteArray(httpResponse
							.getEntity());

					Message message = Message.obtain();
					message.obj = data;
					imageHandler.sendMessage(message);

				}
			} catch (ClientProtocolException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}

	}

	public class GetImageUrlThread implements Runnable {
		private String urlString;

		public GetImageUrlThread(String urlString) {
			this.urlString = urlString;

		}

		@Override
		public void run() {

			String htmlString = HttpUtils.GetString(urlString, "utf-8");
			Document document = Jsoup.parse(htmlString);

			String imageUrl = document.select("img#book_img").first()
					.attr("src");
			System.out.println(imageUrl);
			Message message = Message.obtain();
			message.obj = imageUrl;
			getImageUrlHandler.sendMessage(message);

		}

	}

	public class GetTotalItemThread implements Runnable {
		private String workPath;

		public GetTotalItemThread(String workPath) {
			this.workPath = workPath;
		}

		@Override
		public void run() {

			String listItemNumber;
			String pageNo;

			Document document = Jsoup.parse(HttpUtils.doGetString(workPath,
					"utf-8"));
			Elements elements = document.select("strong.red");
			if (elements == null) {
				return;
			}
			Map<String, String> map = new HashMap<String, String>();
			for (Element element : elements) {
				listItemNumber = element.text();
				System.out.println(listItemNumber);
				// 放入总条数
				map.put("listItemNumber", listItemNumber);

			}
			Elements elements2 = document.select("span.pagination");
			for (Element element : elements2) {
				pageNo = element.getElementsByTag("font").last().text();
				map.put("pageNo", pageNo);
				// 放入页面号
				System.out.println(pageNo);

			}
			Message message = Message.obtain();
			message.obj = map;
			handlerResultNo.sendMessage(message);
			map = null;

		}

	}

	// 获取listview数据的线程
	public class MyThread implements Runnable {
		String updata = "";
		String downdata = "";
		List<Map<String, String>> list_once = new ArrayList<Map<String, String>>();
		private String workPath;

		public MyThread(String workPath) {
			this.workPath = workPath;
		}

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(workPath);
			try {

				HttpResponse httpResponse = httpClient.execute(httpGet);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					String data = EntityUtils.toString(
							httpResponse.getEntity(), "utf-8");
					Document document = Jsoup.parse(data);

					Elements elements = document.select("li.book_list_info");
					Element element1=elements.first();
					if (element1 == null) {
						Message message = Message.obtain();

						message.what = 110;
						handler.sendMessage(message);
						return;
					}
					if (titleFlag == 1) {
						String listItemNumber;
						String pageNo;

						Elements itemsElements = document.select("strong.red");
						if (itemsElements == null) {
							return;
						}
						Map<String, String> map = new HashMap<String, String>();
						for (Element element : itemsElements) {
							listItemNumber = element.text();
							System.out.println(listItemNumber);
							// 放入总条数
							map.put("listItemNumber", listItemNumber);

						}
						Elements elements2 = document.select("span.pagination");
						for (Element element : elements2) {
							pageNo = element.getElementsByTag("font").last()
									.text();
							map.put("pageNo", pageNo);
							// 放入页面号
							System.out.println(pageNo);

						}
						Message message = Message.obtain();
						message.obj = map;
						handlerResultNo.sendMessage(message);
						map = null;
						titleFlag++;

					}

					for (Element element_item : elements) {
						Map<String, String> pass_dataMap = new HashMap<String, String>();
						// 获取下一页的链接urlString
						String urlString = element_item.getElementsByTag("a")
								.first().attr("href");

						linkList.add(urlString);// 加入链接表
						Elements span_elements = element_item
								.getElementsByTag("span");
						// downdata为下方显示内容
						for (Element element2 : span_elements) {
							downdata += "\n" + element2.text();
						}
						Elements h3_elements = element_item
								.getElementsByTag("h3");
						// updata为上方显示内容
						for (Element element3 : h3_elements) {
							updata += element3.text();
						}
						// 加入集合中
						pass_dataMap.put("up", updata);
						pass_dataMap.put("down", downdata);

						list_once.add(pass_dataMap);
						pass_dataMap = null;
						downdata = "";
						updata = "";
						urlString = "";
					}

					Message message = Message.obtain();

					message.obj = list_once;
					message.what = 111;
					handler.sendMessage(message);

				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
			}
		}
	}

	// 弹出配置菜单
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
