package com.linken.newssdk.widget.cardview.adcard.base;

import com.linken.ad.data.AdvertisementCard;

/**
 * Created by patrickleong on 12/2/15.
 */
public interface ContentAdCard {

    void onBind(AdvertisementCard card, String docid);
    void setDivider(boolean hasDivider);
}
