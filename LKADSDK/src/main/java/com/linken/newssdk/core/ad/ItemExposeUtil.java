package com.linken.newssdk.core.ad;

import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;


import com.linken.newssdk.R;
import com.linken.newssdk.utils.LogUtils;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by lishuo on 2017/11/11.
 */

public class ItemExposeUtil {
    private ItemExposeUtil() {

    }

    public static class ItemInfo {

        public long startTime = System.nanoTime();
        public long exposeTime = 0L;
        public int position;
        public View view;
        public double visibleHeight = 0.0;
        public double visiblePercentage = 0.0;
        public boolean hasBeenSeen = false;

        public ItemInfo(int position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return "position:" + position + " exposeTime:" + exposeTime +
                    " percentage:" + visiblePercentage;
        }
    }

    public static void onScroll(View v, TreeMap<Integer, ItemInfo> visibleItems, TreeMap<Integer, ItemInfo> disappearItems, double visibleSlop) {
        long startTime = System.currentTimeMillis();
        int firstVisibleItemPosition = getFirstVisibleItemPosition(v);
        int lastVisibleItemPosition = getLastVisibleItemPosition(v);
        Iterator<Integer> iterator = visibleItems.keySet().iterator();
        disappearItems.clear();
        //先将不在可见范围内的item移出
        while (iterator.hasNext()) {
            int key = iterator.next();
            if (key < firstVisibleItemPosition || key > lastVisibleItemPosition) {
                if (visibleItems.containsKey(key)) {
                    disappearItems.put(key, visibleItems.get(key));
                }
            }
        }
        for (Integer pos : disappearItems.keySet()) {
            visibleItems.remove(pos);
        }

        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            View view = getViewInPosition(i, v);
            if (view == null) {
                continue;
            }
            double visibleHeight = getViewVisibleHeight(view);
            double visiblePercentage = getViewHeightVisiblePercentage(view, visibleHeight);
            if (visiblePercentage > visibleSlop) {
                if (!visibleItems.containsKey(i)) {
                    visibleItems.put(i, new ItemInfo(i));
                }
            } else {
                //如果可见范围小于阈值，则移出
                if (visibleItems.containsKey(i)) {
                    disappearItems.put(i, visibleItems.remove(i));
                }
            }
            ItemInfo itemInfo = visibleItems.get(i);
            if (itemInfo != null) {
                itemInfo.exposeTime += TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - itemInfo.startTime);
                itemInfo.visiblePercentage = visiblePercentage;
                itemInfo.visibleHeight = visibleHeight;
                itemInfo.view = view;
                itemInfo.startTime = System.nanoTime();
            }
        }
        long time = System.currentTimeMillis() - startTime;
        LogUtils.d("visibleItem", "current All Visible Positions : " + visibleItems.toString());
        if (disappearItems.size() > 0) {
            LogUtils.d("visibleItem", "disappear Items: " + disappearItems.toString());
        }
        LogUtils.d("visibleItem", "findTime:" + time);
    }

    private static int getFirstVisibleItemPosition(View v) {
        int firstVisibleItemPosition = 0;
        if (v instanceof ListView) {
            firstVisibleItemPosition = ((ListView) v).getFirstVisiblePosition();
        } else if (v instanceof RecyclerView) {
            RecyclerView.LayoutManager layoutManager = ((RecyclerView) v).getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            }
        }

        LogUtils.d("visibleItem", "firstVisibleItemPosition:" + firstVisibleItemPosition);
        return firstVisibleItemPosition;
    }

    private static int getLastVisibleItemPosition(View v) {
        int lastVisibleItemPosition = 0;
        if (v instanceof ListView) {
            lastVisibleItemPosition = ((ListView) v).getLastVisiblePosition();
        } else if (v instanceof RecyclerView) {
            RecyclerView.LayoutManager layoutManager = ((RecyclerView) v).getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
        }

        LogUtils.d("visibleItem", "lastVisibleItemPosition:" + lastVisibleItemPosition);
        return lastVisibleItemPosition;
    }

    @Nullable
    private static View getViewInPosition(int position, View view) {
        if (view instanceof ListView) {
            ListView listView = (ListView) view;
            int firstVisibleItemPosition = listView.getFirstVisiblePosition();
            int lastVisibleItemPosition = listView.getLastVisiblePosition();
            if (position < firstVisibleItemPosition || position > lastVisibleItemPosition) {
                return listView.getAdapter().getView(position, null, listView);
            } else {
                final int childIndex = position - firstVisibleItemPosition;
                return listView.getChildAt(childIndex);
            }
        } else if (view instanceof RecyclerView) {
            RecyclerView.LayoutManager layoutManager = ((RecyclerView) view).getLayoutManager();
            return layoutManager.findViewByPosition(position);
        }
        return null;
    }

    private static double getViewVisibleHeight(View view) {
        double visibleHeight = 0.0;
        Rect rect = new Rect();
        if (view.getGlobalVisibleRect(rect)) {
            visibleHeight = rect.height();
            LogUtils.d("visibleItem", "visibleHeight:" + String.valueOf(visibleHeight));
        }
        return visibleHeight;
    }

    private static double getViewHeightVisiblePercentage(View view, double visibleHeight) {
        double height = view.getMeasuredHeight();
        double percentage = 0;
        if (height != 0) {
            percentage = (visibleHeight / height) * 100;
            LogUtils.d("visibleItem", "viewHeight:" + String.valueOf(height));
            LogUtils.d("visibleItem", "percentage:" + String.valueOf(percentage));
        }
        return percentage;
    }

    @Nullable
    public static ItemInfo getFirstVisibleItemInfo(String tag, TreeMap<Integer, ItemInfo> visibleItems) {
        if (tag == null) {
            return null;
        }
        ItemInfo itemInfo = null;
        //查找有没有评论item
        for (ItemInfo item : visibleItems.values()) {
            if (item.view != null && item.view.getTag(R.id.ydsdk_expose_tag) != null
                    && tag.equalsIgnoreCase(item.view.getTag(R.id.ydsdk_expose_tag).toString())) {
                itemInfo = item;
                break;
            }
        }
        return itemInfo;
    }

    public static boolean isItemTagEquals(String tag, ItemInfo itemInfo) {
        if (itemInfo == null || tag == null) {
            return false;
        }
        if (itemInfo.view != null && itemInfo.view.getTag(R.id.ydsdk_expose_tag) != null
                && tag.equalsIgnoreCase(itemInfo.view.getTag(R.id.ydsdk_expose_tag).toString())) {
            return true;
        }
        return false;
    }
}