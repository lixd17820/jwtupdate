package com.jwt.update.dao;

public enum ConnectionCatalog {

    ZGYT("中国移动", 0), ZGDX("中国电信", 1),
    GASS("公安三所", 2);
    //, OFFLINE("离线登录", 3);
    private String name;
    private int index;

    private ConnectionCatalog(String _name, int _index) {
        this.name = _name;
        this.index = _index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return name;
    }


}
