package com.jwt.update.dao;

public class CdmaUpdateFileDao extends UpdateFileDao {

	@Override
	public String getUrl() {
		return "http://www.ntjxj.com/";
	}

	@Override
	public String getFileUrl() {
		return "http://www.ntjxj.com/ydjw/DownloadFile?pack=";
	}
}
