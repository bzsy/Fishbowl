package com.github.bzsy.fishbowl.sqlite;

import android.content.Context;

import com.github.bzsy.fishbowl.sqlite.greendao.DaoSession;
import com.github.bzsy.fishbowl.sqlite.greendao.GreenDao;
import com.github.bzsy.fishbowl.sqlite.greendao.HumidityEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.HumidityEntityDao;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class HumidityEntityDBHelper {
    public static final int DEVICE_BOWL = 1;

    private static HumidityEntityDBHelper instance;
    private DaoSession daoSession;
    private HumidityEntityDao humidityEntityDao;
    private Context appContext;

    public HumidityEntityDBHelper(Context context) {
        appContext = context.getApplicationContext();
    }

    public static HumidityEntityDBHelper getInstance(Context context) {
        if (null == instance) {
            instance = new HumidityEntityDBHelper(context);
            instance.daoSession = GreenDao.getDaoSession(context.getApplicationContext());
            instance.humidityEntityDao = instance.daoSession.getHumidityEntityDao();
        }
        return instance;
    }

    public long save(HumidityEntity entity) {
        return humidityEntityDao.insertOrReplace(entity);
    }

    public List<HumidityEntity> loadTodayData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        QueryBuilder<HumidityEntity> builder = humidityEntityDao.queryBuilder();
        builder.where(HumidityEntityDao.Properties.Time.gt(now.getTimeInMillis()))
                .orderAsc(HumidityEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<HumidityEntity> loadThisWeekData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        long t = now.getTimeInMillis() - (dayOfWeek == 1 ? 6 : dayOfWeek - 2) * 24 * 3600 * 1000;
        QueryBuilder<HumidityEntity> builder = humidityEntityDao.queryBuilder();
        builder.where(HumidityEntityDao.Properties.Time.gt(t), HumidityEntityDao.Properties.Id.like("%0"))
                .orderAsc(HumidityEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<HumidityEntity> loadThisMonthData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        QueryBuilder<HumidityEntity> builder = humidityEntityDao.queryBuilder();
        builder.where(HumidityEntityDao.Properties.Time.gt(now.getTimeInMillis()), HumidityEntityDao.Properties.Id.like("%0"))
                .orderAsc(HumidityEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<HumidityEntity> loadData(int limit) {
        QueryBuilder<HumidityEntity> builder = humidityEntityDao.queryBuilder();
        long count = builder.count();
        if (count > 50000) {
            builder.where(HumidityEntityDao.Properties.Id.like("%000"));
        } else if (count > 5000) {
            builder.where(HumidityEntityDao.Properties.Id.like("%00"));
        } else if (count > 500) {
            builder.where(HumidityEntityDao.Properties.Id.like("%0"));
        }
        builder.limit(limit).orderAsc(HumidityEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public long getLastDataTime() {
        QueryBuilder<HumidityEntity> builder = humidityEntityDao.queryBuilder();
        builder.limit(1).orderDesc(HumidityEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list().get(0).getTime();
        } else {
            return 0;
        }
    }

    public void clear() {
        humidityEntityDao.deleteAll();
    }
}
