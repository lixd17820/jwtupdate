package com.jwt.update.bean;

public class OneLineSelectBean {
	private String text1;
	private boolean isSelect;

	public OneLineSelectBean() {
		isSelect = false;
	}

	public OneLineSelectBean(String text1) {
		this.text1 = text1;
		this.isSelect = false;
	}

	public OneLineSelectBean(String text1, boolean isSelect) {
		this.text1 = text1;
		this.isSelect = isSelect;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

}
