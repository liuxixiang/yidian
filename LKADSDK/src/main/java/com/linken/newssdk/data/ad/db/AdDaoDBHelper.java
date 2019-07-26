package com.linken.newssdk.data.ad.db;

import android.content.Context;


import com.linken.ad.data.AdDownloadFileDao;
import com.linken.ad.data.AdvertisementCardDao;
import com.linken.ad.data.DaoMaster;
import com.linken.ad.data.DaoSession;
import com.linken.newssdk.core.ad.AdvertisementModule;
import com.linken.newssdk.data.ad.db.sqlite.AbstractDaoWrapper;
import com.linken.newssdk.utils.LogUtils;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.io.File;

/**
 * Created by liuyue on 16/6/20.
 */
public class AdDaoDBHelper {
    private static final String TAG = AdDaoDBHelper.class.getSimpleName();
    private static final String DB_NAME = "ad.db";
    private static DaoMaster.DevOpenHelper dbHelper = null;
    private static DaoMaster daoMaster = null;
    private static DaoSession writableSession = null;

    private static boolean sHasGeneralConfig = false;

    public static void init() {
        try {
            dbHelper = new DaoMaster.DevOpenHelper(AdvertisementModule.getInstance().getApplicationContext(),
                    DB_NAME, null);

//            generalConfig();

            daoMaster = new DaoMaster(dbHelper.getWritableDatabase());
            writableSession = daoMaster.newSession(IdentityScopeType.Session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generalConfig() {
        if (dbHelper != null && !sHasGeneralConfig) {
            String databaseDir = AdvertisementModule.getInstance().getApplicationContext().getDir("databases", Context.MODE_PRIVATE).getAbsolutePath();
            String tempDatabaseDir = databaseDir + "/temp";
            File tempDatabaseFile = new File(tempDatabaseDir);

            boolean res;
            if (!tempDatabaseFile.exists() || !tempDatabaseFile.isDirectory()) {
                res = tempDatabaseFile.mkdir();
                LogUtils.d(TAG, tempDatabaseFile.getAbsolutePath() + " is not exist");
            } else {
                res = true;
                LogUtils.d(TAG, tempDatabaseFile.getAbsolutePath() + " is exist");
            }

            if (res) {
                // 修改临时文件的存储位置
                String SQL_CHANGE_TEMP_STORE_DIR = "PRAGMA temp_store_directory = '" + tempDatabaseDir + "';";
                dbHelper.getWritableDatabase().execSQL(SQL_CHANGE_TEMP_STORE_DIR);
                // 修改临时文件的存储策略为内存，而非磁盘
                dbHelper.getWritableDatabase().execSQL("PRAGMA temp_store = 2;");
                sHasGeneralConfig = true;
                LogUtils.d(TAG, "config success");
            } else {
                LogUtils.d(TAG, "config fail");
            }
        }
    }

    public static DaoSession getDaoSession() {
        if (writableSession == null) {
            init();
//            IconAdHelper.loadLocalIconAd();
        }
        return writableSession;
    }

    public static AbstractDaoWrapper getAdvertisementCardDao() {
        if (getDaoSession() != null) {
            AdvertisementCardDao dao = getDaoSession().getAdvertisementCardDao();
            if (dao != null) {
                AbstractDaoWrapper daoWrapper = new AbstractDaoWrapper(dao, "AdvertisementCardDao");
                return daoWrapper;
            }
        }
        return null;
    }

    public static AbstractDaoWrapper getAdDownloadFileDao() {
        if (getDaoSession() != null) {
            AdDownloadFileDao dao =  getDaoSession().getAdDownloadFileDao();
            if (dao != null) {
                AbstractDaoWrapper daoWrapper = new AbstractDaoWrapper(dao, "AdDownloadFileDao");
                return daoWrapper;
            }
        }
        return null;
    }

    public static void closeDb() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
