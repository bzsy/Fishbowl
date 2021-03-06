package com.github.bzsy.fishbowl.sqlite.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "IMAGE_ENTITY".
*/
public class ImageEntityDao extends AbstractDao<ImageEntity, Long> {

    public static final String TABLENAME = "IMAGE_ENTITY";

    /**
     * Properties of entity ImageEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, long.class, "time", false, "TIME");
        public final static Property Image_url = new Property(2, String.class, "image_url", false, "IMAGE_URL");
        public final static Property Thumbnail_url = new Property(3, String.class, "thumbnail_url", false, "THUMBNAIL_URL");
        public final static Property Device_id = new Property(4, Integer.class, "device_id", false, "DEVICE_ID");
    };


    public ImageEntityDao(DaoConfig config) {
        super(config);
    }
    
    public ImageEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"IMAGE_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"TIME\" INTEGER NOT NULL ," + // 1: time
                "\"IMAGE_URL\" TEXT," + // 2: image_url
                "\"THUMBNAIL_URL\" TEXT," + // 3: thumbnail_url
                "\"DEVICE_ID\" INTEGER);"); // 4: device_id
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"IMAGE_ENTITY\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ImageEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getTime());
 
        String image_url = entity.getImage_url();
        if (image_url != null) {
            stmt.bindString(3, image_url);
        }
 
        String thumbnail_url = entity.getThumbnail_url();
        if (thumbnail_url != null) {
            stmt.bindString(4, thumbnail_url);
        }
 
        Integer device_id = entity.getDevice_id();
        if (device_id != null) {
            stmt.bindLong(5, device_id);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ImageEntity readEntity(Cursor cursor, int offset) {
        ImageEntity entity = new ImageEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // time
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // image_url
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // thumbnail_url
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4) // device_id
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ImageEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.getLong(offset + 1));
        entity.setImage_url(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setThumbnail_url(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDevice_id(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ImageEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ImageEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
