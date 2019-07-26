package com.linken.newssdk.core.feeds;

import android.text.TextUtils;

import com.linken.newssdk.NewsFeedsSDK;
import com.linken.newssdk.R;
import com.linken.newssdk.base.constract.BaseFragmentPresenter;
import com.linken.newssdk.core.newslist.helper.RefreshErrorCodeHelper;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.data.SlidingTabMgr;
import com.linken.newssdk.data.pref.GlobalConfig;
import com.linken.newssdk.protocol.newNetwork.RequestManager;
import com.linken.newssdk.protocol.newNetwork.business.helper.ChannelResponseHelper;
import com.linken.newssdk.protocol.newNetwork.business.helper.OpenPlatformHelper;
import com.linken.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.linken.newssdk.utils.ContextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/5/19
 */

public class FeedsPresenter extends BaseFragmentPresenter<FeedsContractView> {
    private final ArrayList<YdChannel> mData = new ArrayList();
    private boolean hasCache;

    public FeedsPresenter(FeedsContractView mContactView) {
        super(mContactView);
    }


    public void loadChannels() {
        mContactView.onShowLoading();
        if (TextUtils.isEmpty(GlobalConfig.getOpParams())) {
            RequestManager.requestOpenPlatform(NewsFeedsSDK.getInstance().getAppId(), new JsonObjectResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    OpenPlatformHelper.saveParams(response);
                    readFromServer();
                }

                @Override
                public void onFailure(Throwable e) {
                    mContactView.onHideLoading();
                    mContactView.onShowError(RefreshErrorCodeHelper.code2String2(e));
                }
            });
        } else {
            readFromServer();
        }
    }

    private void readFromServer() {
        RequestManager.requestChannealList(new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                mContactView.onHideLoading();
                ChannelResponseHelper helper = new ChannelResponseHelper();
                wrapChannels(helper.parseChnanelJson(response), false);
            }

            @Override
            public void onFailure(Throwable e) {
                mContactView.onHideLoading();
                mContactView.onShowError(RefreshErrorCodeHelper.code2String2(e));
            }
        });
    }

    private void readChannelsFromDB() {

    }


    public void readFromTest() {
        wrapChannels(SlidingTabMgr.getInstance().getChannelList(), false);
    }

    public ArrayList<YdChannel> getChannels() {
        return this.mData;
    }

    private void wrapChannels(List<YdChannel> channelInfos, boolean hasCache) {
        if(hasCache) {
            this.hasCache = hasCache;
        }

        this.mData.clear();

        for(int index = 0; index < channelInfos.size(); ++index) {
            YdChannel channelInfo = channelInfos.get(index);
            if(null != channelInfo) {
                this.mData.add(channelInfo);
            }
        }

        mContactView.initMagicIndicator();

        if (channelInfos.size() == 0) {
            mContactView.onShowError(ContextUtils.getApplicationContext().getResources().getString(R.string.ydsdk_feed_error_empty));
        } else {
            mContactView.onHideError();
        }
    }

}
