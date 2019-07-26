package com.yidian.newssdk.utils;

import android.text.TextUtils;
import android.util.Log;


import com.yidian.newssdk.data.card.base.Card;
import com.yidian.newssdk.data.card.base.ContentCard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

public class SerializeUtil {
    private static final String TAG = SerializeUtil.class.getSimpleName();

    public static void saveObjectToFile(Object obj, String filename) {
        if (obj == null || TextUtils.isEmpty(filename)) {
            return;
        }

        // check cache file exist
        File f = new File(filename);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // save
        BufferedOutputStream buf = null;
        ObjectOutputStream out = null;
        try {

            buf = new BufferedOutputStream(new FileOutputStream(f));
            out = new ObjectOutputStream(buf);
            out.writeObject(obj);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "save cache file " + filename + " failed");
        } finally {
            YdUtil.close(out);
            YdUtil.close(buf);
        }
    }

    /**
     * this function read the object from cached files
     */
    public static Object restoreObjectFromCache(String path) {
        // check cache file exist
        File f = new File(path);
        if (!f.exists()) {
            Log.d(TAG, "cache file: " + path + " not exist");
            return null;
        }

        // restore
        ObjectInputStream in = null;
        Object obj = null;
        BufferedInputStream buf = null;
        try {
            buf = new BufferedInputStream(new FileInputStream(path));
            in = new ObjectInputStream(buf);
            obj = in.readObject();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "restore object instance from file " + path + " failed");
        } finally {
            YdUtil.close(in);
            YdUtil.close(buf);
        }

        return obj;
    }

    public static void saveNewsListToFile(List<Card> list, String path) {
        //avoid concurrentModificationException
        ArrayList<Card> tmpList = new ArrayList<>();
        for (int i = 0; i < 40 && i < list.size(); i++) {
            Card item = list.get(i);
            if (item instanceof ContentCard) {
                ContentCard tmpCard = (ContentCard) item;
                if (!TextUtils.isEmpty(tmpCard.fullJsonContent)) {
                    tmpCard.content = null;
                    tmpCard.fullJsonContent = null;
                }
            }
            tmpList.add(item);
        }

        saveObjectToFile(tmpList, path);
    }


}
