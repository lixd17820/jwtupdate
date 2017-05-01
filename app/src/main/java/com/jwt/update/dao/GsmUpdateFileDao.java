package com.jwt.update.dao;

public class GsmUpdateFileDao extends UpdateFileDao {

	@Override
	public String getUrl() {
		return "http://www.ntjxj.com/";
	}

	@Override
	public String getFileUrl() {
		return "http://www.ntjxj.com/ydjw/DownloadFile?pack=";
	}
}
