package cn.tzl.ems;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistActivity extends Activity {

	private static final int HANDLER_REGIST_SUCCESS = 0;
	private static final int HANDLER_REGIST_FAIL = 1;
	private static final int HANDLER_REGIST_PAW = 2;
	private EditText etName;
	private EditText etPassword;
	private EditText etRepPassword;
	private EditText etRealName;
	private EditText etMaile;
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_REGIST_SUCCESS:
				//·µ»ØµÇÂ¼½çÃæ
				Toast.makeText(RegistActivity.this, "×¢²á³É¹¦", 0).show();
				finish();
				break;
			case HANDLER_REGIST_FAIL:
				Toast.makeText(RegistActivity.this, "×¢²áÊ§°Ü"+msg.obj, 0).show();
				break;
			case HANDLER_REGIST_PAW:
				Toast.makeText(RegistActivity.this, "ÃÜÂë²»Ò»ÖÂ", 0).show();
				break;
			default:
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		setViews();
	}

	private void setViews() {
		etName = (EditText) findViewById(R.id.et_name);
		etPassword = (EditText) findViewById(R.id.et_password);
		etRepPassword = (EditText) findViewById(R.id.et_re_password);
		etRealName = (EditText) findViewById(R.id.et_real_name);
		etMaile = (EditText) findViewById(R.id.et_maile);
	}
	public void doClick(View v){
		new Thread(){
			public void run() {
				try {
					regist();
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

	private void regist() throws ClientProtocolException, IOException, JSONException{
		String paw = etPassword.getText().toString().trim();
		String reqPaw = etRepPassword.getText().toString().trim();
		if(!paw.equals(reqPaw)){
			handler.sendEmptyMessage(HANDLER_REGIST_PAW);
			return;
		}
		
		
		
		
		HttpClient client = new DefaultHttpClient();
		String url = "http://192.168.5.42/ems/regist.do";
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type","application/x-www-form-urlencoded");
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("loginname", etName.getText().toString().trim()));
		list.add(new BasicNameValuePair("password", etPassword.getText().toString().trim()));
		list.add(new BasicNameValuePair("realname", etRealName.getText().toString().trim()));
		list.add(new BasicNameValuePair("email", etMaile.getText().toString().trim()));
		HttpEntity reqEntity = new UrlEncodedFormEntity(list,"utf-8");
		post.setEntity(reqEntity);
		HttpResponse resp = client.execute(post);
		int code = resp.getStatusLine().getStatusCode();
		if(code == 200){
			String json = EntityUtils.toString(resp.getEntity());
			JSONObject obj = new JSONObject(json);
			String res = obj.getString("result");
			if("ok".equals(res)){
				handler.sendEmptyMessage(HANDLER_REGIST_SUCCESS);
			}else{
				Message msg = handler.obtainMessage();
				msg.what = HANDLER_REGIST_FAIL;
				msg.obj = obj.getString("msg");
				handler.sendMessage(msg);
			}
		}else{
			Log.e("info", "ÇëÇóÊ§°Ü£º"+ code);
		}
	}
}
