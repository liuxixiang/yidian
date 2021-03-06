package com.linken.ad.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "AD_DOWNLOAD_FILE".
*/
public class AdDownloadFileDao extends AbstractDao<AdDownloadFile, Long> {

    public static final String TABLENAME = "AD_DOWNLOAD_FILE";

    /**
     * Properties of entity AdDownloadFile.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PackageName = new Property(1, String.class, "packageName", false, "PACKAGE_NAME");
        public final static Property LocalFile = new Property(2, String.class, "localFile", false, "LOCAL_FILE");
    }


    public AdDownloadFileDao(DaoConfig config) {
        super(config);
    }
    
    public AdDownloadFileDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AD_DOWNLOAD_FILE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PACKAGE_NAME\" TEXT," + // 1: packageName
                "\"LOCAL_FILE\" TEXT);"); // 2: localFile
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AD_DOWNLOAD_FILE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AdDownloadFile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(2, packageName);
        }
 
        String localFile = entity.getLocalFile();
        if (localFile != null) {
            stmt.bindString(3, localFile);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AdDownloadFile entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String packageName = entity.getPackageName();
        if (packageName != null) {
            stmt.bindString(2, packageName);
        }
 
        String localFile = entity.getLocalFile();
        if (localFile != null) {
            stmt.bindString(3, localFile);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AdDownloadFile readEntity(Cursor cursor, int offset) {
        AdDownloadFile entity = new AdDownloadFile( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // packageName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // localFile
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AdDownloadFile entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPackageName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLocalFile(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AdDownloadFile entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AdDownloadFile entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AdDownloadFile entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
