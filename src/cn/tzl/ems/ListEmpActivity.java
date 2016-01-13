package cn.tzl.ems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cn.tzl.ems.adapter.MyListAdapter;

import cn.tzl.ems.dao.EmploeeData;
import cn.tzl.ems.dao.EmploeeData.Emploee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

@SuppressLint("NewApi")
public class ListEmpActivity extends Activity {

	private List<Emploee> list;
	private static final int HANDLER_ADD_SUCCESS = 0;
	private static final int HANDLER_DEL_SUCCESS = 1;
	private ListView listEmp;

	private MyListAdapter adapter;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_ADD_SUCCESS:
				setAdapter();
				break;
			case HANDLER_DEL_SUCCESS:
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_emp);
		list = new ArrayList<Emploee>();
		setViews();	
		setListeners();
	}


	@Override
	protected void onResume() {
		super.onResume();
		new Thread(){
			public void run() {
				
				try {
					loadEmps();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			};
		}.start();
	
	}
	private void setAdapter() {
		adapter = new MyListAdapter(this, list);
		listEmp.setAdapter(adapter);
	}


	private void setViews() {
		listEmp = (ListView) findViewById(R.id.listView1);
		
	}
	
	public void doClick(View v){
		switch (v.getId()) {
		case R.id.tv_add_emp:
			startActivity(new Intent(this, AddEmpActivity.class));
			break;

		default:
			break;
		}
	}
	private void setListeners() {
		
		listEmp.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ListEmpActivity.this);
				builder.setTitle("删除记录");
				builder.setMessage("是否删除该条记录");
				builder.setPositiveButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(){
							public void run() {
								try {
									removeEmploee(position);
								} catch (ClientProtocolException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							
						}.start();	
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				});
				builder.create();
				builder.show();
				return false;
			}
		});
		
	}

	private void removeEmploee(int position) throws ClientProtocolException, IOException {
		Emploee removeEmp = list.remove(position);
		String id = removeEmp.id;
		String param = "?id="+id;
		HttpClient client = new DefaultHttpClient();
		String url = "http://192.168.5.42:80/ems/delEmp"+param;
		HttpGet get = new HttpGet(url);
		HttpResponse resp = client.execute(get);
		handler.sendEmptyMessage(HANDLER_DEL_SUCCESS);
	};


	protected void loadEmps() throws ClientProtocolException, IOException, JSONException {
		HttpClient client = new DefaultHttpClient();
		String url = "http://192.168.5.42:80/ems/listEmp";
		HttpGet get = new HttpGet(url);
		HttpResponse resp = client.execute(get);
		int code = resp.getStatusLine().getStatusCode();
		if(code == 200){
			String json = EntityUtils.toString(resp.getEntity());
			Gson gson = new Gson();
			EmploeeData emploeeData = gson.fromJson(json, EmploeeData.class);
			System.out.println(emploeeData.toString());
			list =  emploeeData.data;
			//list = emploeeData.data;
			handler.sendEmptyMessage(HANDLER_ADD_SUCCESS);
			
			}else{
				Log.e("info", "请求失败："+ code);
			}
		
		}
	

}
