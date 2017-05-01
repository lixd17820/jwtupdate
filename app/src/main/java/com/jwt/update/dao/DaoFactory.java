package com.jwt.update.dao;

import com.jwt.update.ConfigUpdateActivity;

public class DaoFactory {

    public static UpdateFileDao getDao() {
        if (ConfigUpdateActivity.connCata == ConnectionCatalog.ZGYT)
            return new GsmUpdateFileDao();
        else if (ConfigUpdateActivity.connCata == ConnectionCatalog.ZGDX)
            return new CdmaUpdateFileDao();
        else if (ConfigUpdateActivity.connCata == ConnectionCatalog.GASS)
            return new ThreeTeamDao();
        return null;
    }

}
