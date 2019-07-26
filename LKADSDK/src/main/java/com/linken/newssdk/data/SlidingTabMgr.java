package com.linken.newssdk.data;

import com.linken.newssdk.data.channel.YdChannel;
import com.linken.newssdk.exception.NoneDataException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 * @date 2018/2/6
 */

public class SlidingTabMgr {

    private static List<YdChannel> channelList = new ArrayList<>(10);

    private static SlidingTabMgr slidingTabMgr;


    public static SlidingTabMgr getInstance() {
        if (slidingTabMgr == null) {
            synchronized (SlidingTabMgr.class) {
                if (slidingTabMgr == null) {
                    slidingTabMgr = new SlidingTabMgr();
                    initChannelsData();
                }
            }
        }
        return slidingTabMgr;
    }

    public List<YdChannel> getChannelList() {
        return channelList;
    }


    public void initCustomChannels(String[] insertChannels) throws NoneDataException {
        if (insertChannels == null || insertChannels.length == 0) {
            throw new NoneDataException("请插入频道数据！");
        }

        if (!channelList.isEmpty()) {
            channelList.clear();
        }

        for (String data : insertChannels) {
            channelList.add(new YdChannel(data));
        }
    }

    /*'推荐' => array('channel_id' => 'best', 'display' => '推荐'),
            '热点' => array('channel_id' => 'hot', 'display' => '热点'),
            '社会' => array('channel_id' => 'c9', 'display' => '社会'),
            '娱乐' => array('channel_id' => 'c3', 'display' => '娱乐'),
            '军事' => array('channel_id' => 'c7', 'display' => '军事'),
            '体育' => array('channel_id' => 'c2', 'display' => '体育'),
            'NBA' => array('channel_id' => 'sc4', 'display' => 'NBA'),
            '财经' => array('channel_id' => 'c5', 'display' => '财经'),
            '科技' => array('channel_id' => 'c6', 'display' => '科技'),
            '民生' => array('channel_id' => 'u299', 'display' => '民生'),
            '美女' => array('channel_id' => 'u241', 'display' => '美女'),
            '段子' => array('channel_id' => 'u12131', 'display' => '段子'),
            '健康' => array('channel_id' => 'c16', 'display' => '健康'),
            '时尚' => array('channel_id' => 'c15', 'display' => '时尚'),
            '汽车' => array('channel_id' => 'c11', 'display' => '汽车'),
            '搞笑' => array('channel_id' => 's10671', 'display' => '搞笑'),
            '视频' => array('channel_id' => 'u13746', 'display' => '视频'),
            '游戏' => array('channel_id' => 'c22', 'display' => '游戏'),
            '旅游' => array('channel_id' => 'c17', 'display' => '旅游'),
            '科学' => array('channel_id' => 'c10', 'display' => '科学'),
            '互联网' => array('channel_id' => 'c4', 'display' => '互联网'),
            '趣图' => array('channel_id' => 'u11392', 'display' => '趣图'),
            '美食' => array('channel_id' => 'u9365', 'display' => '美食'),
            '星座' => array('channel_id' => 't77', 'display' => '星座'),
            '育儿' => array('channel_id' => 't1020', 'display' => '育儿'),
            '情感' => array('channel_id' => 't9439', 'display' => '情感'),
            '房产' => array('channel_id' => 'c8', 'display' => '房产'),*/

    private static void initChannelsData() {
        if (!channelList.isEmpty()) {
            channelList.clear();
        }

        channelList.add(new YdChannel("推荐"));
        channelList.add(new YdChannel("热点"));
        channelList.add(new YdChannel("视频"));
        channelList.add(new YdChannel("体育"));
        channelList.add(new YdChannel("美女"));
        channelList.add(new YdChannel("财经图集"));
        channelList.add(new YdChannel("游戏"));
        channelList.add(new YdChannel("旅游"));
        channelList.add(new YdChannel("美食"));
        channelList.add(new YdChannel("趣图"));


    }

}
