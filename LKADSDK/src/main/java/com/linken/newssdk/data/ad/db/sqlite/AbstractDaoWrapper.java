package com.linken.newssdk.data.ad.db.sqlite;

import android.database.SQLException;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.annotation.apihint.Experimental;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.rx.RxDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by caichen on 2018/1/10.
 */

public class AbstractDaoWrapper {

    private static String TAG = AbstractDaoWrapper.class.getSimpleName();
    private static final int DEFAULT_ERROR_VALUE = 0;

    private AbstractDao mAbstractDao;

    public AbstractDaoWrapper(@NonNull AbstractDao abstractDao, String tag) {
        if (abstractDao != null) {
            mAbstractDao = abstractDao;
        }
        if (!TextUtils.isEmpty(tag)) {
            TAG = tag;
        }
    }

    public AbstractDaoSession getSession() {
        return mAbstractDao.getSession();
    }

    public String getTablename() {
        return mAbstractDao.getTablename();
    }

    public Property[] getProperties() {
        return mAbstractDao.getProperties();
    }

    public Property getPkProperty() {
        return mAbstractDao.getPkProperty();
    }

    public String[] getAllColumns() {
        return mAbstractDao.getAllColumns();
    }

    public String[] getPkColumns() {
        return mAbstractDao.getPkColumns();
    }

    public String[] getNonPkColumns() {
        return mAbstractDao.getNonPkColumns();
    }

    /**
     * Loads the mEntityClass for the given PK.
     *
     * @param key a PK value or null
     * @return The mEntityClass or null, if no mEntityClass matched the PK value
     */
    public Object load(Object key) {
        try {
            return mAbstractDao.load(key);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return null;
        }
    }

    public Object loadByRowId(long rowId) {
        try {
            return mAbstractDao.loadByRowId(rowId);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return null;
        }
    }

    /** Loads all available entities from the database. */
    public List<?> loadAll() {
        try {
            return mAbstractDao.loadAll();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return new ArrayList<>();
        }
    }

    /** Detaches an mEntityClass from the identity scope (session). Subsequent query results won't return this object. */
    public boolean detach(Object entity) {
        try {
            return mAbstractDao.detach(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return false;
        }
    }

    /**
     * Detaches all entities (of type T) from the identity scope (session). Subsequent query results won't return any
     * previously loaded objects.
     */
    public void detachAll() {
        try {
            mAbstractDao.detachAll();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Inserts the given entities in the database using a transaction.
     *
     * @param entities The entities to insert.
     */
    public void insertInTx(Iterable<?> entities) {
        try {
            mAbstractDao.insertInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Inserts the given entities in the database using a transaction.
     *
     * @param entities The entities to insert.
     */
    public void insertInTx(Object... entities) {
        try {
            mAbstractDao.insertInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Inserts the given entities in the database using a transaction. The given entities will become tracked if the PK
     * is set.
     *
     * @param entities      The entities to insert.
     * @param setPrimaryKey if true, the PKs of the given will be set after the insert; pass false to improve
     *                      performance.
     */
    public void insertInTx(Iterable<?> entities, boolean setPrimaryKey) {
        try {
            mAbstractDao.insertInTx(entities, setPrimaryKey);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Inserts or replaces the given entities in the database using a transaction. The given entities will become
     * tracked if the PK is set.
     *
     * @param entities      The entities to insert.
     * @param setPrimaryKey if true, the PKs of the given will be set after the insert; pass false to improve
     *                      performance.
     */
    public void insertOrReplaceInTx(Iterable<?> entities, boolean setPrimaryKey) {
        try {
            mAbstractDao.insertInTx(entities, setPrimaryKey);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Inserts or replaces the given entities in the database using a transaction.
     *
     * @param entities The entities to insert.
     */
    public void insertOrReplaceInTx(Iterable<?> entities) {
        try {
            mAbstractDao.insertOrReplaceInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Inserts or replaces the given entities in the database using a transaction.
     *
     * @param entities The entities to insert.
     */
    public void insertOrReplaceInTx(Object... entities) {
        try {
            mAbstractDao.insertOrReplaceInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Insert an mEntityClass into the table associated with a concrete DAO.
     *
     * @return row ID of newly inserted mEntityClass
     */
    public long insert(Object entity) {
        try {
            return mAbstractDao.insert(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return DEFAULT_ERROR_VALUE;
        }
    }

    /**
     * Insert an mEntityClass into the table associated with a concrete DAO <b>without</b> setting key property.
     * <p>
     * Warning: This may be faster, but the mEntityClass should not be used anymore. The mEntityClass also won't be attached to
     * identity scope.
     *
     * @return row ID of newly inserted mEntityClass
     */
    public long insertWithoutSettingPk(Object entity) {
        try {
            return mAbstractDao.insertWithoutSettingPk(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return DEFAULT_ERROR_VALUE;
        }
    }

    /**
     * Insert an mEntityClass into the table associated with a concrete DAO.
     *
     * @return row ID of newly inserted mEntityClass
     */
    public long insertOrReplace(Object entity) {
        try {
            return mAbstractDao.insertOrReplace(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return DEFAULT_ERROR_VALUE;
        }
    }

    /**
     * "Saves" an mEntityClass to the database: depending on the existence of the key property, it will be inserted
     * (key is null) or updated (key is not null).
     * <p>
     * This is similar to {@link #insertOrReplace(Object)}, but may be more efficient, because if a key is present,
     * it does not have to query if that key already exists.
     */
    public void save(Object entity) {
        try {
            mAbstractDao.save(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Saves (see {@link #save(Object)}) the given entities in the database using a transaction.
     *
     * @param entities The entities to save.
     */
    public void saveInTx(Object... entities) {
        try {
            mAbstractDao.saveInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Saves (see {@link #save(Object)}) the given entities in the database using a transaction.
     *
     * @param entities The entities to save.
     */
    public void saveInTx(Iterable<?> entities) {
        try {
            mAbstractDao.saveInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<?> queryRaw(String where, String... selectionArg) {
        try {
            return mAbstractDao.queryRaw(where, selectionArg);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return new ArrayList<>();
        }
    }

    /**
     * Creates a repeatable {@link Query} object based on the given raw SQL where you can pass any WHERE clause and
     * arguments.
     */
    public Query<?> queryRawCreate(String where, Object... selectionArg) {
        try {
            return mAbstractDao.queryRawCreate(where, selectionArg);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return null;
        }
    }

    /**
     * Creates a repeatable {@link Query} object based on the given raw SQL where you can pass any WHERE clause and
     * arguments.
     */
    public Query<?> queryRawCreateListArgs(String where, Collection<Object> selectionArg) {
        try {
            return mAbstractDao.queryRawCreateListArgs(where, selectionArg);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return null;
        }
    }

    public void deleteAll() {
        try {
            mAbstractDao.deleteAll();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /** Deletes the given mEntityClass from the database. Currently, only single value PK entities are supported. */
    public void delete(Object entity) {
        try {
            mAbstractDao.delete(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /** Deletes an mEntityClass with the given PK from the database. Currently, only single value PK entities are supported. */
    public void deleteByKey(Object key) {
        try {
            mAbstractDao.deleteByKey(key);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Deletes the given entities in the database using a transaction.
     *
     * @param entities The entities to delete.
     */
    public void deleteInTx(Iterable<?> entities) {
        try {
            mAbstractDao.deleteInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Deletes the given entities in the database using a transaction.
     *
     * @param entities The entities to delete.
     */
    public void deleteInTx(Object... entities) {
        try {
            mAbstractDao.deleteInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Deletes all entities with the given keys in the database using a transaction.
     *
     * @param keys Keys of the entities to delete.
     */
    public void deleteByKeyInTx(Iterable<?> keys) {
        try {
            mAbstractDao.deleteByKeyInTx(keys);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Deletes all entities with the given keys in the database using a transaction.
     *
     * @param keys Keys of the entities to delete.
     */
    public void deleteByKeyInTx(Object... keys) {
        try {
            mAbstractDao.deleteByKeyInTx(keys);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /** Resets all locally changed properties of the mEntityClass by reloading the values from the database. */
    public void refresh(Object entity) {
        try {
            mAbstractDao.refresh(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    public void update(Object entity) {
        try {
            mAbstractDao.update(entity);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    public QueryBuilderWrapper queryBuilder() {
        return new QueryBuilderWrapper(mAbstractDao.queryBuilder(), TAG);
    }

    /**
     * Updates the given entities in the database using a transaction.
     *
     * @param entities The entities to insert.
     */
    public void updateInTx(Iterable<?> entities) {
        try {
            mAbstractDao.updateInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    /**
     * Updates the given entities in the database using a transaction.
     *
     * @param entities The entities to update.
     */
    public void updateInTx(Object... entities) {
        try {
            mAbstractDao.updateInTx(entities);
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
        }
    }

    public long count() {
        return mAbstractDao.count();
    }

    /**
     * The returned RxDao is a special DAO that let's you interact with Rx Observables without any Scheduler set
     * for subscribeOn.
     * @see #rx()
     */
    @Experimental
    public RxDao<?, ?> rxPlain() {
        try {
            return mAbstractDao.rxPlain();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return null;
        }
    }

    /**
     * The returned RxDao is a special DAO that let's you interact with Rx Observables using RX's IO scheduler for
     * subscribeOn.
     *
     * @see #rxPlain()
     */
    @Experimental
    public RxDao<?, ?> rx() {
        try {
            return mAbstractDao.rx();
        } catch (SQLException e) {
            SQLiteExceptionHandler.handle(mAbstractDao, e, TAG);
            return null;
        }
    }

    /** Gets the SQLiteDatabase for custom database access. Not needed for greenDAO entities. */
    public Database getDatabase() {
        return mAbstractDao.getDatabase();
    }

}
