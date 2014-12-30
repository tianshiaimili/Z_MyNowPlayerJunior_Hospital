
package com.hua.gz.model;

import java.util.List;

import android.widget.ImageView;
import android.widget.TextView;

public class CustomerDataModle extends BaseModle {

	private static final long serialVersionUID = 1L;
   
	private String id;
	private String username;
	private String  sex;
	private String date;
	
	public CustomerDataModle() {
		super();
	}
	public CustomerDataModle(String username, String sex, String date) {
		super();
		this.username = username;
		this.sex = sex;
		this.date = date;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
