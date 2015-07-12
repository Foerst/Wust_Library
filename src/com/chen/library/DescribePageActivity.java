package com.chen.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chen.library.CustomDialog.CustomDialogUtils;
import com.chen.library.DataUtils.XMLParser;
import com.chen.library.HttpPath.UrlPath;
import com.chen.library.Utils.HttpUtils;
import com.chen.library.Utils.HttpsUtils;
import com.chen.library.Utils.ImageUtils;

//书籍详情页
public class DescribePageActivity extends Activity {

	private TextView titleTextView;// 显示dt值
	private TextView valueTextView;// 显示dd值
	private ListView listView;// 用于显示解析数据的ListView对象
	private ImageView imageView;// 用于显示书籍图片的ImageView对象
	private Dialog progressDialog;// 获取信息时展示的进度条，在解析完成后消失
	private List<String> isbnName;// isbn字符串链表
	private String imagePath;// 图片地址
	private String summaryString;// 书籍描述
	private ImageView backImageView;
	// 定义更新书籍图片的handler
	final Handler imageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			byte[] result = (byte[]) msg.obj;
			if (result == null) {
				return;

			}
			// 根据字节数组获得位图
			Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0,
					result.length);
			// 为图片控件设置位图，即显示图片

			imageView.setImageBitmap(bitmap);
		};
	};

	// 定义更新书籍信息的handler
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			List<Map<String, String>> data = (List<Map<String, String>>) msg.obj;
			CustomAdapter adapter = new CustomAdapter(data);
			listView.setAdapter(adapter);

			progressDialog.dismiss();// 进度条消失
//			if (isbnName.get(0) != null) {
//				// 不空则开始获取图片url及书评的线程
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						String isbnPathString = UrlPath.DOUBAN_BOOK_INFO_WITH_ISBN
//								+ isbnName.get(0).toString();
//						Log.v("isbn------->isbnPathString", isbnPathString);
//						String jsonString = "";
//						if (isbnPathString != null) {
//							jsonString = HttpsUtils.GetString(isbnPathString,
//									"utf-8");
//						}
//						Log.v("jsonString------->jsonString", jsonString);
//						try {
//							JSONObject tmpJsonObject = new JSONObject(
//									jsonString);
//
////							imagePath = tmpJsonObject
////									.getJSONObject("rating").getString(
////											"image");
//							imagePath=tmpJsonObject.getString("image");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//						System.out.println("imagePath=========" + imagePath);
//						// summaryString = UrlPath.SUMMARY_OF_BOOK;
//						// System.out.println("summary=========" +
//						// summaryString);
//						// textView3.setText("this is summaryString");
//						// 开始获取图片的线程，在imageHandler中更新图片
//						if (imagePath == null) {
//							return;
//
//						}
//						new Thread(new ImageThread(ImageUtils.GetMediumPictureUrl(isbnPathString))).start();
//
//					}
//				}).start();
//			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 因为要自定义标题，所以去掉title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 加载布局文件
		this.setContentView(R.layout.activity_describe_page);
		backImageView = (ImageView) findViewById(R.id.back_img);
		backImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DescribePageActivity.this.finish();
			}
		});

		isbnName = new ArrayList<String>();// 初始化isbnName
		summaryString = "";// 初始化summaryString
		imagePath = "";// 初始化imagePath
		// 以下为根据id获取控件句柄
		imageView = (ImageView) this.findViewById(R.id.imageView1);
		listView = (ListView) this.findViewById(R.id.listView1);
		progressDialog = CustomDialogUtils.getViewContainer(this).getDialog();
		progressDialog.show();// 显示进度条
		progressDialog.setCancelable(false);// 不可触屏取消
		Intent intent = getIntent();
		// 获取该书对应的详情页链接
		final String loadUrl = UrlPath.WUST_LIB_HOME_URL
				+ intent.getStringExtra("linkedUrl");

		// 由书籍链接获取该书对应详情页的信息，并解析得到所需信息
		new Thread(new GetBookInofThread(loadUrl)).start();

	}

	public class GetBookInofThread implements Runnable {
		private String urlString;

		public GetBookInofThread(String urlString) {
			this.urlString = urlString;
		}

		@Override
		public void run() {

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			String html = HttpUtils.GetString(urlString, "utf-8");
			if (html == null) {

				return;
			}
			// 这里用到了Jsoup，解析html字符串得到文档对象
			Document document = Jsoup.parse(html);
			// 得到所有<dl class="booklist">的标签
			Elements elements = document.select("dl.booklist");
			for (Element element : elements) {
				Map<String, String> map = new HashMap<String, String>();

				String dt = element.getElementsByTag("dt").first().text();
				System.out.println(dt);

				String dd = element.getElementsByTag("dd").first().text();
				System.out.println(dd);
				if (dt.equals("ISBN及定价:")) {
					String isbnName1 = dd.substring(0, 17);
					System.out.println("isbnName===========" + isbnName1);
					isbnName.add(isbnName1);

				} else if (dt.equals("ISBN:")) {
					String isbnName2 = dd.substring(0, 13);
					System.out.println("isbnName===========" + isbnName2);
					isbnName.add(isbnName2);
				}
				map.put("dt", dt);
				map.put("dd", dd);
				list.add(map);

			}

			Message message = Message.obtain();
			message.obj = list;
			handler.sendMessage(message);

		}
	}

	// imageThread的定义
	class ImageThread implements Runnable {
		private String urlString;

		public ImageThread(String urlString) {
			this.urlString = urlString;
		}

		@Override
		public void run() {
			byte[] data = HttpUtils.GetByteArray(urlString);
			Message message = Message.obtain();
			message.obj = data;
			// 利用handlel发送消息，也可用异步任务做
			imageHandler.sendMessage(message);
		}
	}

	// 自定义listView的适配器类，该类继承了BaseAdapter并重写了其中的方法
	class CustomAdapter extends BaseAdapter {
		private List<Map<String, String>> map;// 数据集

		public CustomAdapter(List<Map<String, String>> map) {
			this.map = map;
		}

		// 获取数据集大小
		@Override
		public int getCount() {
			return map.size();
		}

		// 获取item对应的数据
		@Override
		public Object getItem(int position) {
			return map.get(position);
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
			if (convertView == null) {// 如果重用的view为空，则根据自定义的布局文件填充得到视图
				view = LayoutInflater.from(getApplicationContext()).inflate(
						R.layout.item_detail, null);

			} else {// 不为空时则引用重用视图
				view = convertView;
			}
			// 以下内容为设置listView对应item的显示信息
			titleTextView = (TextView) view.findViewById(R.id.textView1);
			valueTextView = (TextView) view.findViewById(R.id.textView2);
			valueTextView.setTextSize(18);
			titleTextView.setText(map.get(position).get("dt"));
			valueTextView.setText(map.get(position).get("dd"));

			return view;
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
