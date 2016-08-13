package com.github.bzsy.fishbowl.sqlite;

import android.content.Context;

import com.github.bzsy.fishbowl.sqlite.greendao.DaoSession;
import com.github.bzsy.fishbowl.sqlite.greendao.GreenDao;
import com.github.bzsy.fishbowl.sqlite.greendao.ImageEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.ImageEntityDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class ImageEntityDBHelper {
    public static final int DEVICE_BOWL = 1;

    private static ImageEntityDBHelper instance;
    private DaoSession daoSession;
    private ImageEntityDao imageEntityDao;
    private Context appContext;

    public ImageEntityDBHelper(Context context) {
        appContext = context.getApplicationContext();
    }

    public static ImageEntityDBHelper getInstance(Context context) {
        if (null == instance) {
            instance = new ImageEntityDBHelper(context);
            instance.daoSession = GreenDao.getDaoSession(context.getApplicationContext());
            instance.imageEntityDao = instance.daoSession.getImageEntityDao();
        }
        return instance;
    }

    public long save(ImageEntity entity) {
        return imageEntityDao.insertOrReplace(entity);
    }

    public ImageEntity loadLastData() {
        QueryBuilder<ImageEntity> builder = imageEntityDao.queryBuilder();
        builder.limit(1).orderDesc(ImageEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list().get(0);
        } else {
            return null;
        }
    }

    public List<ImageEntity> loadData(int limit) {
        QueryBuilder<ImageEntity> builder = imageEntityDao.queryBuilder();
        builder.limit(limit).orderDesc(ImageEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public long getLastDataTime() {
        QueryBuilder<ImageEntity> builder = imageEntityDao.queryBuilder();
        builder.limit(1).orderDesc(ImageEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list().get(0).getTime();
        } else {
            return 0;
        }
    }

    public void clear() {
        imageEntityDao.deleteAll();
    }
}
