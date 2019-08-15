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
 * DAO for table "REWARD_CARD".
*/
public class RewardCardDao extends AbstractDao<RewardCard, Long> {

    public static final String TABLENAME = "REWARD_CARD";

    /**
     * Properties of entity RewardCard.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CardId = new Property(1, String.class, "cardId", false, "CARD_ID");
        public final static Property Type = new Property(2, String.class, "type", false, "TYPE");
    }


    public RewardCardDao(DaoConfig config) {
        super(config);
    }
    
    public RewardCardDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"REWARD_CARD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CARD_ID\" TEXT," + // 1: cardId
                "\"TYPE\" TEXT);"); // 2: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"REWARD_CARD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RewardCard entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cardId = entity.getCardId();
        if (cardId != null) {
            stmt.bindString(2, cardId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RewardCard entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String cardId = entity.getCardId();
        if (cardId != null) {
            stmt.bindString(2, cardId);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(3, type);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RewardCard readEntity(Cursor cursor, int offset) {
        RewardCard entity = new RewardCard( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // cardId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // type
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RewardCard entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCardId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setType(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RewardCard entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RewardCard entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(RewardCard entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
