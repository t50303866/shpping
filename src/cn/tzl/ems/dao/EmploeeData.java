package cn.tzl.ems.dao;

import java.util.ArrayList;



import android.os.Parcel;
import android.os.Parcelable;

public class EmploeeData {
	public String result;
	public ArrayList<Emploee> data;
	
	public static class Emploee implements Parcelable{
		public String id;
		public String name;
		public double salary;
		public int age;
		public String gender;
		public Emploee(){
			
		}
		public Emploee(Parcel src){
			readFromParcel(src);
		}
		private void readFromParcel(Parcel src) {
			this.id = src.readString();
			this.name = src.readString();
			this.salary = src.readDouble();
			this.age = src.readInt();
			this.gender = src.readString();
			
		}
		@Override
		public String toString() {
			return "Emploee [id=" + id + ", name=" + name + ", salary="
					+ salary + ", age=" + age + ", gender=" + gender + "]";
		}
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(id);
			dest.writeString(name);
			dest.writeDouble(salary);
			dest.writeInt(age);
			dest.writeString(gender);
			
			
		}
		public static final Parcelable.Creator<Emploee> CREATOR = new Parcelable.Creator<Emploee>() {

			@Override
			public Emploee createFromParcel(Parcel source) {
				// TODO Auto-generated method stub
				return new Emploee(source);
			}

			@Override
			public Emploee[] newArray(int size) {
				
				return new Emploee[size];
			}
			
		};
		
	}

	@Override
	public String toString() {
		return "EmploeeData [result=" + result + ", data=" + data + "]";
	}
	
}
