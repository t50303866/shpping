package cn.tzl.ems.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.tzl.ems.R;
import cn.tzl.ems.dao.EmploeeData.Emploee;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyListAdapter extends BaseAdapter{
	private Activity mActivity;
	private List<Emploee> data;
	
	public MyListAdapter(Activity mActivity, List<Emploee> data) {
		super();
		this.mActivity = mActivity;
		setData(data);
	}

	public void setData(List<Emploee> data) {
		if(data == null){
			data = new ArrayList<Emploee>();
		}
		this.data = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(mActivity, R.layout.item_list_emp, null);
			holder = new ViewHolder();
			holder.tvId = (TextView) convertView.findViewById(R.id.tv_id);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tvSalary = (TextView) convertView.findViewById(R.id.tv_salary);
			holder.tvAge = (TextView) convertView.findViewById(R.id.tv_age);
			holder.tvGender = (TextView) convertView.findViewById(R.id.tv_gender);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		Emploee emp = data.get(position);
		holder.tvId.setText(""+emp.id);
		holder.tvName.setText(emp.name);
		holder.tvSalary.setText(""+emp.salary);
		holder.tvAge.setText(""+emp.age);
		holder.tvGender.setText(emp.gender.equals("m")?"ÄÐÊ¿":"Å®Ê¿");
		return convertView;
	}
	
private class ViewHolder{
	private TextView tvId;
	private TextView tvName;
	private TextView tvSalary;
	private TextView tvAge;
	private TextView tvGender;
}
}
