package com.jwt.update.dao;

public class ThreeTeamDao extends UpdateFileDao{

	@Override
	public String getUrl() {
		return "http://127.0.0.1:8099/";
	}

	@Override
	public String getFileUrl() {
		return getUrl() + "ydjw/DownloadFile?pack=";
	}

}
