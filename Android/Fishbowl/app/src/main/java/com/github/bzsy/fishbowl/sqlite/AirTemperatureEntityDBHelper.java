package com.github.bzsy.fishbowl.sqlite;

import android.content.Context;

import com.github.bzsy.fishbowl.sqlite.greendao.AirTemperatureEntity;
import com.github.bzsy.fishbowl.sqlite.greendao.AirTemperatureEntityDao;
import com.github.bzsy.fishbowl.sqlite.greendao.DaoSession;
import com.github.bzsy.fishbowl.sqlite.greendao.GreenDao;

import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

public class AirTemperatureEntityDBHelper {
    public static final int DEVICE_BOWL = 1;

    private static AirTemperatureEntityDBHelper instance;
    private DaoSession daoSession;
    private AirTemperatureEntityDao airTemperatureEntityDao;
    private Context appContext;

    public AirTemperatureEntityDBHelper(Context context) {
        appContext = context.getApplicationContext();
    }

    public static AirTemperatureEntityDBHelper getInstance(Context context) {
        if (null == instance) {
            instance = new AirTemperatureEntityDBHelper(context);
            instance.daoSession = GreenDao.getDaoSession(context.getApplicationContext());
            instance.airTemperatureEntityDao = instance.daoSession.getAirTemperatureEntityDao();
        }
        return instance;
    }

    public long save(AirTemperatureEntity entity) {
        return airTemperatureEntityDao.insertOrReplace(entity);
    }

    public List<AirTemperatureEntity> loadTodayData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        QueryBuilder<AirTemperatureEntity> builder = airTemperatureEntityDao.queryBuilder();
        builder.where(AirTemperatureEntityDao.Properties.Time.gt(now.getTimeInMillis()))
                .orderAsc(AirTemperatureEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<AirTemperatureEntity> loadThisWeekData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        long t = now.getTimeInMillis() - (dayOfWeek == 1 ? 6 : dayOfWeek - 2) * 24 * 3600 * 1000;
        QueryBuilder<AirTemperatureEntity> builder = airTemperatureEntityDao.queryBuilder();
        builder.where(AirTemperatureEntityDao.Properties.Time.gt(t), AirTemperatureEntityDao.Properties.Id.like("%0"))
                .orderAsc(AirTemperatureEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<AirTemperatureEntity> loadThisMonthData() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        QueryBuilder<AirTemperatureEntity> builder = airTemperatureEntityDao.queryBuilder();
        builder.where(AirTemperatureEntityDao.Properties.Time.gt(now.getTimeInMillis()), AirTemperatureEntityDao.Properties.Id.like("%0"))
                .orderAsc(AirTemperatureEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public List<AirTemperatureEntity> loadData(int limit) {
        QueryBuilder<AirTemperatureEntity> builder = airTemperatureEntityDao.queryBuilder();
        long count = builder.count();
        if (count > 50000) {
            builder.where(AirTemperatureEntityDao.Properties.Id.like("%000"));
        } else if (count > 5000) {
            builder.where(AirTemperatureEntityDao.Properties.Id.like("%00"));
        } else if (count > 500) {
            builder.where(AirTemperatureEntityDao.Properties.Id.like("%0"));
        }
        builder.limit(limit).orderAsc(AirTemperatureEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list();
        } else {
            return null;
        }
    }

    public long getLastDataTime() {
        QueryBuilder<AirTemperatureEntity> builder = airTemperatureEntityDao.queryBuilder();
        builder.limit(1).orderDesc(AirTemperatureEntityDao.Properties.Time);
        if (builder.list().size() > 0) {
            return builder.list().get(0).getTime();
        } else {
            return 0;
        }
    }

    public void clear() {
        airTemperatureEntityDao.deleteAll();
    }
}
