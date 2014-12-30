package com.hua.gz.model;

import java.io.Serializable;

public class AppointmentInfo implements Serializable{

	private String username;
	private String sex;
	private String birthdayDate;
	public AppointmentInfo() {
		super();
	}
	public AppointmentInfo(String username, String sex, String birthdayDate) {
		super();
		this.username = username;
		this.sex = sex;
		this.birthdayDate = birthdayDate;
	}
	
	
	
}
