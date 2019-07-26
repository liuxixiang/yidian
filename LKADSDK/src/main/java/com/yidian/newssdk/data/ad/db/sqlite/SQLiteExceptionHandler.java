package com.yidian.newssdk.data.ad.db.sqlite;

import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteFullException;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by caichen on 2018/1/11.
 */

public class SQLiteExceptionHandler {

    public static void handle(AbstractDao abstractDao, Exception e, String tag) {
        try {
            e.printStackTrace();
            if (e instanceof SQLiteFullException) {
                if (abstractDao != null) {
                    abstractDao.deleteAll();
                }
            } else if (e instanceof SQLiteCantOpenDatabaseException) {

            } else if (e instanceof SQLiteDatabaseLockedException) {

            } else if (e instanceof SQLiteConstraintException) {

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void handle(Exception e, String tag) {
        try {
            e.printStackTrace();
            if (e instanceof SQLiteFullException) {

            } else if (e instanceof SQLiteCantOpenDatabaseException) {

            } else if (e instanceof SQLiteDatabaseLockedException) {

            } else if (e instanceof SQLiteConstraintException) {

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
