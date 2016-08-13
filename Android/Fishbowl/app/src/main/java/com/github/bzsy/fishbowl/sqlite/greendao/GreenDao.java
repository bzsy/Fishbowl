package com.github.bzsy.fishbowl.sqlite.greendao;

import android.content.Context;

public class GreenDao {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static final String DB_NAME = "PiData.db";

    public static DaoSession getDaoSession(Context context) {
        if (null == daoSession) {
            if (null == daoMaster) {
                DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
                daoMaster = new DaoMaster(helper.getWritableDatabase());
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
