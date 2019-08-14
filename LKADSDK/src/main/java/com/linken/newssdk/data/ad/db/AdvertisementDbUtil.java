package com.linken.newssdk.data.ad.db;

import android.text.TextUtils;
import android.util.Log;


import com.linken.ad.data.AdDownloadFile;
import com.linken.ad.data.AdDownloadFileDao;
import com.linken.ad.data.AdvertisementCard;
import com.linken.ad.data.AdvertisementCardDao;
import com.linken.ad.data.RewardCard;
import com.linken.ad.data.RewardCardDao;
import com.linken.newssdk.core.ad.AdvertisementUtil;
import com.linken.newssdk.data.ad.ADConstants;
import com.linken.newssdk.data.ad.db.sqlite.AbstractDaoWrapper;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyue on 16/6/20.
 */
public class AdvertisementDbUtil {
    public static boolean logEnabled = Log.isLoggable(ADConstants.ADVERTISEMENT_LOG, Log.VERBOSE);

    public static boolean createAdRecord(AdvertisementCard card, String eventType, long downloadId, boolean reportEvent) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null) {
            return false;
        }
        card.setDownloadId(downloadId);
        card.setReportEvent(reportEvent ? 1 : 0);
        card.setEvent(eventType);
        dao.insertOrReplace(card);
        return true;
    }


    public static AdvertisementCard getAdvertisementCard(long downloadId) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null) {
            return null;
        }

        List<AdvertisementCard> list = (List<AdvertisementCard>) dao
                .queryBuilder()
                .where(AdvertisementCardDao.Properties.DownloadId.eq(downloadId))
                .list();

        if (list != null && list.size() > 0) {
            //将数据库中的clickMonitorUrlStr和viewMonitorUrlStr转成实际中使用的Monitor Url数组
            AdvertisementCard card = list.get(0);
            card.clickMonitorUrls = AdvertisementCard.convertStringArray(card.getClickMonitorUrlsStr());
            card.viewMonitorUrls = AdvertisementCard.convertStringArray(card.getViewMonitorUrlsStr());
            return card;
        }
        return null;
    }

    public static ArrayList<AdvertisementCard> getAllDownloadedAdApps() {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null) {
            return new ArrayList<AdvertisementCard>();
        }

        List<AdvertisementCard> result = (List<AdvertisementCard>) dao
                .queryBuilder()
                .where(AdvertisementCardDao.Properties.Event.eq(AdvertisementUtil.EVENT_APP_DOWNLOAD_SUCCESS))
                .list();

        if (result != null) {
            for (AdvertisementCard card : result) {
                //将数据库中的clickMonitorUrlStr和viewMonitorUrlStr转成实际中使用的Monitor Url数组
                card.clickMonitorUrls = AdvertisementCard.convertStringArray(card.getClickMonitorUrlsStr());
                card.viewMonitorUrls = AdvertisementCard.convertStringArray(card.getViewMonitorUrlsStr());
            }
        }

        return (ArrayList<AdvertisementCard>) result;
    }

    public static boolean updateDownloadAdStatus(AdvertisementCard card, long keyDownloadId, int status, int progress) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null || card == null) {
            return false;
        }

        List<AdvertisementCard> list = (List<AdvertisementCard>) dao
                .queryBuilder()
                .where(AdvertisementCardDao.Properties.DownloadId.eq(keyDownloadId))
                .list();

        if (list != null && list.size() > 0) {
            for (AdvertisementCard ad : list) {
                ad.setDownloadStatus(status);
                ad.setDownloadProgress(progress);
                dao.updateInTx(ad);
            }
            return true;
        }
        return false;
    }

    public static void removeAdRecord(long downloadId) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null) {
            return;
        }
        dao.deleteAll();
    }

    public static long getAdvertisementCardDownloadId(AdvertisementCard card) {
        long downloadId = -1;
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null || card == null) {
            return downloadId;
        }

        List<AdvertisementCard> list = (List<AdvertisementCard>) dao
                .queryBuilder()
                .where(AdvertisementCardDao.Properties.Aid.eq(card.getAid()))
                .list();

        if (list != null && list.size() > 0) {
            downloadId = list.get(0).getDownloadId();
        }
        Log.d(ADConstants.ADVERTISEMENT_LOG, "Download id is : " + String.valueOf(downloadId) + " for AdvertisementCard : " + card);
        return downloadId;
    }

    public static boolean deleteAdRecordByDownloadId(long downloadId) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdvertisementCardDao();
        if (dao == null) {
            return false;
        }

        List<AdvertisementCard> list = (List<AdvertisementCard>) dao
                .queryBuilder()
                .where(AdvertisementCardDao.Properties.DownloadId.eq(downloadId))
                .list();
        dao.deleteInTx(list);

        return true;
    }

    public static boolean addDownloadPackageRecord(String packageName, String fileName) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdDownloadFileDao();
        if (dao == null || TextUtils.isEmpty(packageName) || TextUtils.isEmpty(fileName)) {
            return false;
        }

        AdDownloadFile AdDownloadFile = new AdDownloadFile();
        AdDownloadFile.setLocalFile(fileName);
        AdDownloadFile.setPackageName(packageName);

        dao.insertInTx(AdDownloadFile);
        return true;
    }

    public static List<String> getDownloadPackageRecord(String packageName) {
        List<String> result = new ArrayList<>();

        AbstractDaoWrapper dao = AdDaoDBHelper.getAdDownloadFileDao();
        if (dao == null || TextUtils.isEmpty(packageName)) {
            return result;
        }

        List<AdDownloadFile> list = (List<AdDownloadFile>) dao
                .queryBuilder()
                .where(AdDownloadFileDao.Properties.PackageName.eq(packageName))
                .list();

        if (list != null && list.size() > 0) {
            for (AdDownloadFile file : list) {
                result.add(file.getLocalFile());
            }
        }

        return result;
    }

    public static boolean deleteDownloadPackageRecord(String packageName, String fileName) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getAdDownloadFileDao();
        if (dao == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(packageName)) {
            return false;
        }

        List<AdDownloadFile> list = (List<AdDownloadFile>) dao
                .queryBuilder()
                .where(AdDownloadFileDao.Properties.PackageName.eq(packageName))
                .where(AdDownloadFileDao.Properties.LocalFile.eq(fileName))
                .list();
        dao.deleteInTx(list);

        return true;
    }


    /**
     * 创建获取奖励的记录
     */
    public static boolean createRewardRecord(RewardCard rewardCardBean) {
        AbstractDaoWrapper dao = AdDaoDBHelper.getRewardCardDao();
        if (dao == null) {
            return false;
        }
        dao.insertOrReplace(rewardCardBean);
        return true;
    }

    public static String getRewardRecordId(String id) {
        String cardId = "";
        AbstractDaoWrapper dao = AdDaoDBHelper.getRewardCardDao();
        if (dao == null) {
            return cardId;
        }


        List<RewardCard> list = (List<RewardCard>) dao
                .queryBuilder()
                .where(RewardCardDao.Properties.CardId.eq(id))
                .list();
        if (list != null && list.size() > 0) {
            cardId = list.get(0).getCardId();
        }
        return cardId;
    }

}
