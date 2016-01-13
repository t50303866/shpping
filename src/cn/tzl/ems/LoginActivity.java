package cn.tzl.ems;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.gson.JsonObject;
import com.lidroid.xutils.BitmapUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText etName;
	private EditText etPassword;
	private EditText etCode;
	private ImageView ivCode;
	private ImageView ivQiu;
	
	private Bitmap bitmap;
	private static final int HANDLER_CODE_SUCCESS = 0;
	private static final int HANDLER_LOGIN_SUCCESS = 1;
	private static final int HANDLER_LOGIN_FAIL = 2;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HANDLER_CODE_SUCCESS:
				//更新imageView
				if(bitmap != null){
					ivCode.setImageBitmap(bitmap);
				}
				break;
			case HANDLER_LOGIN_SUCCESS:
				Toast.makeText(LoginActivity.this, "登录成功", 0).show();
				startActivity(new Intent(LoginActivity.this, ListEmpActivity.class));
				finish();
				break;
			case HANDLER_LOGIN_FAIL:
				Toast.makeText(LoginActivity.this, "登录失败"+msg.obj, 1).show();
				new Thread(){
					public void run() {
						try {
							getCode();
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();
				break;
			default:
				break;
			}
		};
	};
	private String JSESSIONID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setViews();
		new Thread(){
			public void run() {
				try {
					getCode();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		
//		BitmapUtils  utils = new BitmapUtils(this);
//		utils.display(ivQiu, "assets/image/qiu.png");	
		startAnim();
	}
	protected void getCode() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		String url = "http://192.168.5.42:80/ems/getCode.do";
		HttpGet get = new HttpGet(url);
		HttpResponse resp = client.execute(get);
		HttpEntity entity = resp.getEntity();
		byte[] bytes = EntityUtils.toByteArray(entity);
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		//获取服务端返回给客户端的消息头
		Header header = resp.getFirstHeader("Set-Cookie");
		Log.i("info","header:"+header.getValue());
		JSESSIONID = header.getValue().split(";")[0];
		
		
		//发送消息，主线程更新Image
		handler.sendEmptyMessage(HANDLER_CODE_SUCCESS);
	}
	private void setViews() {
		etName = (EditText) findViewById(R.id.et_name);
		etPassword = (EditText) findViewById(R.id.et_password);
		etCode = (EditText) findViewById(R.id.editText1);
		ivCode = (ImageView) findViewById(R.id.imageView1);
		ivQiu = (ImageView) findViewById(R.id.iv_qiu);
		
	
		
	}
public void doClick(View v){
	switch (v.getId()) {
	case R.id.imageView1:
		new Thread(){
			public void run() {
				try {
					getCode();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
		break;
	case R.id.bt_login:
		new Thread(){
			public void run() {
				try {
					login();
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
	case R.id.tv_register:
		startActivity(new Intent(this, RegistActivity.class));
		break;
	default:
		break;
	}
}
protected void login() throws ClientProtocolException, IOException, JSONException {
	HttpClient client = new DefaultHttpClient();
	String url = "http://192.168.5.42:80/ems/login.do";
	HttpPost post = new HttpPost(url);
	post.setHeader("Content-Type","application/x-www-form-urlencoded");
	post.setHeader("Cookie",JSESSIONID);
	List<NameValuePair> list = new ArrayList<NameValuePair>();
	list.add(new BasicNameValuePair("loginname", etName.getText().toString().trim()));
	list.add(new BasicNameValuePair("password", etPassword.getText().toString().trim()));
	list.add(new BasicNameValuePair("code", etCode.getText().toString().trim()));
	HttpEntity reqEntity = new UrlEncodedFormEntity(list,"utf-8");
	post.setEntity(reqEntity);
	HttpResponse resp = client.execute(post);
	
	String json = EntityUtils.toString(resp.getEntity());
	JSONObject obj = new JSONObject(json);
	String res = obj.getString("result");
	if("ok".equals(res)){
		handler.sendEmptyMessage(HANDLER_LOGIN_SUCCESS);
	}else{
		Message msg = handler.obtainMessage();
		msg.what = HANDLER_LOGIN_FAIL;
		msg.obj = obj.get("msg");
		handler.sendMessage(msg);
	}
	
}
private void startAnim() {

	AnimationSet set = new AnimationSet(false);

	RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	rotate.setDuration(2000);

	rotate.setFillAfter(true);


	TranslateAnimation tran = new TranslateAnimation(0f, 360f, 0f,0f);
	tran.setDuration(2000);
	tran.setFillAfter(true);
	


	set.addAnimation(rotate);
	//set.addAnimation(tran);
	ivQiu.startAnimation(set);

}

}
