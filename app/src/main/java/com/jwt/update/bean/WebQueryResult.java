package com.jwt.update.bean;

import java.io.Serializable;


@SuppressWarnings("serial")
public class WebQueryResult<E> implements Serializable {
	/**
	 * 
	 */
	int status;
	E result;

	public WebQueryResult() {
	}

	public WebQueryResult(int status, E result) {
		this.status = status;
		this.result = result;
	}

	public WebQueryResult(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public E getResult() {
		return result;
	}

	public void setResult(E result) {
		this.result = result;
	}

}
