package com.github.bzsy.fishbowl.sqlite;

import android.content.Context;

import com.github.bzsy.fishbowl.sqlite.greendao.DaoSession;
import com.github.bzsy.fishbowl.sqlite.greendao.GreenDao;
import com.github.bzsy.fishbowl.sqlite.greendao.LightEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.LightEntityDao;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class LightEntityDBHelper {
    public static final int DEVICE_BOWL = 1;

    private static LightEntityDBHelper instance;
    private DaoSession daoSession;
    private LightEntityDao lightEntityDao;
    private Context appContext;

    public LightEntityDBHelper(Context context) {
        appContext = context.getApplicationContext();
    }

    public static LightEntityDBHelper getInstance(Context context) {
        if (null == instance) {
            instance = new LightEntityDBHelper(context);
            instance.daoSession = GreenDao.getDaoSession(context.getApplicationContext());
            instance.lightEntityDao = instance.daoSession.getLightEntityDao();
        }
        return instance;
    }

    public long save(LightEntity entity) {
        return lightEntityDao.insertOrReplace(entity);
    }

    public List<LightEntity> loadTodayData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        QueryBuilder<LightEntity> builder = lightEntityDao.queryBuilder();
        builder.where(LightEntityDao.Properties.Time.gt(now.getTimeInMillis()))
                .orderAsc(LightEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<LightEntity> loadThisWeekData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        long t = now.getTimeInMillis() - (dayOfWeek == 1 ? 6 : dayOfWeek - 2) * 24 * 3600 * 1000;
        QueryBuilder<LightEntity> builder = lightEntityDao.queryBuilder();
        builder.where(LightEntityDao.Properties.Time.gt(t), LightEntityDao.Properties.Id.like("%0"))
                .orderAsc(LightEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<LightEntity> loadThisMonthData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        QueryBuilder<LightEntity> builder = lightEntityDao.queryBuilder();
        builder.where(LightEntityDao.Properties.Time.gt(now.getTimeInMillis()), LightEntityDao.Properties.Id.like("%0"))
                .orderAsc(LightEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<LightEntity> loadData(int limit) {
        QueryBuilder<LightEntity> builder = lightEntityDao.queryBuilder();
        long count = builder.count();
        if (count > 50000) {
            builder.where(LightEntityDao.Properties.Id.like("%000"));
        } else if (count > 5000) {
            builder.where(LightEntityDao.Properties.Id.like("%00"));
        } else if (count > 500) {
            builder.where(LightEntityDao.Properties.Id.like("%0"));
        }
        builder.limit(limit).orderAsc(LightEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public long getLastDataTime() {
        QueryBuilder<LightEntity> builder = lightEntityDao.queryBuilder();
        builder.limit(1).orderDesc(LightEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list().get(0).getTime();
        } else {
            return 0;
        }
    }

    public void clear() {
        lightEntityDao.deleteAll();
    }
}
