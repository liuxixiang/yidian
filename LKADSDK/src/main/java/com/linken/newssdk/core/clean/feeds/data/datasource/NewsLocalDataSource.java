package com.linken.newssdk.core.clean.feeds.data.datasource;

import android.os.AsyncTask;

import com.linken.newssdk.core.clean.commmon.NewsDataSource;
import com.linken.newssdk.core.clean.commmon.UseCase;
import com.linken.newssdk.core.clean.commmon.bean.UseCaseParams;
import com.linken.newssdk.core.clean.feeds.data.ChannelDataManager;
import com.linken.newssdk.core.clean.feeds.data.bean.FeedsResponse;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.channel.YdChannel;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author zhangzhun
 * @date 2018/8/28
 */

public class NewsLocalDataSource<Request extends UseCaseParams.Request, Response extends UseCaseParams.Response>
        implements NewsDataSource<Request, Response> {

    private YdChannel mChannel;
    public ArrayList<Card> mCardList = new ArrayList<>();
    private UseCase.Callback<Response> lazyRefreshCallback;

    public NewsLocalDataSource(YdChannel mChannel) {
        this.mChannel = mChannel;
    }

    @Override
    public void getFeedsNews(Request params, UseCase.Callback<Response> responseCallback) {
        execReadCache(params, responseCallback);
    }

    public void execReadCache(Request params, UseCase.Callback<Response> readCacheCallback) {
        this.lazyRefreshCallback = readCacheCallback;
        ReadCachedNewsListTask task = new ReadCachedNewsListTask(mChannel, lazyRefreshCallback);
        task.execute();
    }

    private class ReadCachedNewsListTask extends AsyncTask<Void, Void, Void> {
        private YdChannel channel;
        private UseCase.Callback<Response> readCacheCallback;

        public ReadCachedNewsListTask(YdChannel channel, UseCase.Callback<Response> readCacheCallback) {
            this.readCacheCallback = readCacheCallback;
            this.channel = channel;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (channel == null) {
                return null;
            }
            channel = ChannelDataManager.restoreChannelCachedNewsList(channel.getChannelName());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (channel == null) {
                return;
            }
            if (channel.newsList == null || channel.newsList.isEmpty()) {
                if (mChannel != null && mChannel.newsList != null && mChannel.newsList.size() > 0) {
                    new SaveChannelNewsListTask().execute(mChannel);
                    mCardList = cardList(mChannel.newsList, mChannel.getChannelName() + "");
                    if (readCacheCallback != null) {
                        readCacheCallback.onSuccess((Response) new FeedsResponse(mCardList));
                    }
                    return;
                }

                mCardList = new ArrayList<>();
                channel.newsList = mCardList;
                if (readCacheCallback != null) {
                    readCacheCallback.onSuccess(null);
                }
                return;
            } else {
                // notify fetch complete, let UI hide the loading animation
                mCardList = cardList(channel.newsList, mChannel.getChannelName() + "");
                if (readCacheCallback != null) {
                    readCacheCallback.onSuccess((Response) new FeedsResponse(mCardList));
                }
            }
        }
    }

    private ArrayList<Card> cardList(ArrayList<Card> cards, String channel) {
        if (cards != null && cards.size() > 0) {
            Iterator iterator = cards.iterator();
            while (iterator.hasNext()) {
                Card card = (Card) iterator.next();
                card.channel = channel;
            }
        }
        return cards;
    }

    public void saveNewsListToCache(YdChannel request) {
        if (request == null) {
            //数据没有变化，不用存
            return;
        }
        new SaveChannelNewsListTask().execute(request);
    }

    private static class SaveChannelNewsListTask extends AsyncTask<YdChannel, Void, Void> {
        @Override
        protected Void doInBackground(YdChannel... params) {
            YdChannel channel = params[0];

            if (channel != null) {
                ChannelDataManager.saveChannelToCacheFile(channel);
            }
            return null;
        }
    }

}
