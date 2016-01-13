package cn.tzl.ems;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddEmpActivity extends Activity {

	private EditText etName;
	private EditText etAge;
	private EditText etSalary;
	private RadioGroup radioGroup;
	private static final int HANDLER_ADD_SUCCESS = 0;
	private static final int HANDLER_ADD_FAIL = 1;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_ADD_SUCCESS:
				Toast.makeText(AddEmpActivity.this, "添加成功", 0).show();
				//startActivity(new Intent(AddEmpActivity.this, ListEmpActivity.class));
				finish();
				break;
			case HANDLER_ADD_FAIL:
				Toast.makeText(AddEmpActivity.this, "添加失败", 0).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setViews();
		
	}

	private void setViews() {
		etName = (EditText) findViewById(R.id.editText1);
		etAge = (EditText) findViewById(R.id.editText2);
		etSalary = (EditText) findViewById(R.id.editText3);	
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
	}

public void doClick(View v){
	switch (v.getId()) {
	case R.id.button1:
		new Thread(){
			public void run() {
				try {
					addEmp();
					Log.i("test", "dddddddd");
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
		
		
		break;

	default:
		break;
	}
}

private void addEmp() throws ClientProtocolException, IOException, JSONException {
	HttpClient client = new DefaultHttpClient();
	String url = "http://192.168.5.42:80/ems/addEmp";
	HttpPost post = new HttpPost(url);
	post.setHeader("Content-Type","application/x-www-form-urlencoded");
	List<NameValuePair> list = new ArrayList<NameValuePair>();
	list.add(new BasicNameValuePair("name", etName.getText().toString().trim()));
	list.add(new BasicNameValuePair("salary", etSalary.getText().toString().trim()));
	list.add(new BasicNameValuePair("age", etAge.getText().toString().trim()));
	list.add(new BasicNameValuePair("gender",radioGroup.getCheckedRadioButtonId() == R.id.radio1?"m":"f"));
	HttpEntity reqEntity = new UrlEncodedFormEntity(list,"utf-8");
	post.setEntity(reqEntity);
	HttpResponse resp = client.execute(post);
	int code = resp.getStatusLine().getStatusCode();
	if(code == 200){
		String json = EntityUtils.toString(resp.getEntity());
		JSONObject obj = new JSONObject(json);
		String res = obj.getString("result");
		if("ok".equals(res)){
			handler.sendEmptyMessage(HANDLER_ADD_SUCCESS);
		}else{
			Message msg = handler.obtainMessage();
			msg.what = HANDLER_ADD_FAIL;
			msg.obj = obj.getString("msg");
			handler.sendMessage(msg);
		}
	}else{
		Log.e("info", "请求失败："+ code);
	}
	
}

}
