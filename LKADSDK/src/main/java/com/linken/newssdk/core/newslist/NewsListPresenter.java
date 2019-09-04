package com.linken.newssdk.core.newslist;

import android.content.Context;

import com.linken.newssdk.NewsConfig;
import com.linken.newssdk.base.constract.BaseFragmentPresenter;
import com.linken.newssdk.data.card.base.Card;
import com.linken.newssdk.data.card.news.News;
import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.protocol.newNetwork.business.request.imp.RequestConfigPost;
import com.linken.newssdk.protocol.newNetwork.core.AsyncHttpClient;
import com.linken.newssdk.protocol.newNetwork.core.JsonObjectResponseHandler;
import com.linken.newssdk.toutiao.TTADRefreshList;
import com.linken.newssdk.toutiao.TTAdManagerHolder;
import com.linken.newssdk.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.linken.newssdk.data.card.base.Card.CTYPE_NORMAL_NEWS;

/**
 * @author zhangzhun
 * @date 2018/5/21
 */

public class NewsListPresenter extends BaseFragmentPresenter<NewsListContractView> {

    private Context mContext;
    private ArrayList<Card> mSourceList = new ArrayList<>();
    private YdChannel mChannel;
    private int refreshCount;
    private NewsListContractView mContactView;
    private List<IRefreshList<Card>> mIRefreshLists;


    public NewsListPresenter(Context context, NewsListContractView contactView) {
        super(contactView);
        this.mContext = context;
        this.mContactView = contactView;
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(mContext);
    }

    public ArrayList<Card> getTAdapterItems() {
        return this.mSourceList;
    }

    public void setRefreshCount(int count) {
        refreshCount = count;
    }

    public void init(final YdChannel mChannel) {
        this.mChannel = mChannel;
        mIRefreshLists = new ArrayList<>();
        mIRefreshLists.add(new TTADRefreshList(mContactView));
        mIRefreshLists.add(new NewsRefreshList(mContactView, mChannel, refreshCount));
        getTopList();
    }

    /**
     * 获取置顶的列表
     */
    private void getTopList() {
        final RequestConfigPost requestBase = new RequestConfigPost(NewsConfig.CODE_699999);
        new AsyncHttpClient().post(requestBase, new JsonObjectResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response != null && response.has("data")) {
                        JSONObject data = response.getJSONObject("data");
                        List<String> codes = requestBase.getCodes();
                        if (data != null) {
                            for (String code : codes) {
                                if (data.has(code)) {
                                    JSONObject jsonObject = data.getJSONObject(code);
                                    if (jsonObject != null) {
                                        JSONArray paramValues = jsonObject.getJSONArray("paramValue");
                                        List<Card> topList = new ArrayList<>();
                                        if (paramValues != null && paramValues.length() > 0) {
                                            for (int i = 0; i < paramValues.length(); i++) {
                                                if (paramValues.get(i) instanceof JSONObject) {
                                                    JSONObject jsonParam = (JSONObject) paramValues.get(i);
                                                    if (jsonParam != null) {
                                                        News card = new News();
                                                        JsonUtil.readStringFromJson(jsonParam, "id");
                                                        card.id = JsonUtil.readStringFromJson(jsonParam, "id");
                                                        card.title = JsonUtil.readStringFromJson(jsonParam, "title");
                                                        card.channel = JsonUtil.readStringFromJson(jsonParam, "channel");
                                                        card.tag_name = JsonUtil.readStringFromJson(jsonParam, "tag");
                                                        card.url = JsonUtil.readStringFromJson(jsonParam, "url");
                                                        card.source = JsonUtil.readStringFromJson(jsonParam, "author");
                                                        long time = JsonUtil.readLongFromJson(jsonParam, "createTime", 0);
                                                        card.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
                                                        JSONArray jsonCoverImgUrl = jsonParam.getJSONArray("coverImgUrl");
                                                        if(jsonCoverImgUrl != null) {
                                                            String[] coverimgUrl = JsonUtil.parseJSONString(jsonCoverImgUrl);
                                                            if (coverimgUrl != null) {
                                                                card.imageUrls = Arrays.asList(coverimgUrl);
                                                            }
                                                        }
                                                        card.cType = CTYPE_NORMAL_NEWS;
                                                        topList.add(card);
                                                    }
                                                }
                                            }
                                        }
                                        mSourceList.addAll(0,topList);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * 第一次懒加载的情况
     */
    public void firstLazyRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.firstLazyRefresh();
            }
        }
    }

    /**
     * 下拉刷洗的情况
     */
    public void onRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.onRefresh();
            }
        }
    }

    /**
     * 上拉加载更多的情况
     */
    public void onLoadMoreRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.onLoadMoreRefresh();
            }
        }
    }

    /**
     * 错误点击重试的情况
     */
    public void onClickErrorRefresh() {
        if (mIRefreshLists != null && mIRefreshLists.size() > 0) {
            for (IRefreshList<Card> iRefreshList : mIRefreshLists) {
                iRefreshList.onClickErrorRefresh();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
