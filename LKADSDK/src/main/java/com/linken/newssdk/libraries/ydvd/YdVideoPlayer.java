package com.linken.newssdk.libraries.ydvd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linken.newssdk.R;
import com.linken.newssdk.export.IMediaInterface;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nathen on 16/7/30.
 */
public abstract class YdVideoPlayer extends FrameLayout implements IMediaInterface, View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    public static final String TAG = "JiaoZiVideoPlayer";
    public static final int THRESHOLD = 80;
    public static final int FULL_SCREEN_NORMAL_DELAY = 300;

    public static final int SCREEN_WINDOW_NORMAL = 0;
    public static final int SCREEN_WINDOW_LIST = 1;
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;
    public static final int SCREEN_WINDOW_TINY = 3;

    public static final int CURRENT_STATE_NORMAL = 0;
    public static final int CURRENT_STATE_PREPARING = 1;
    public static final int CURRENT_STATE_PREPARING_CHANGING_URL = 2;
    public static final int CURRENT_STATE_PLAYING = 3;
    public static final int CURRENT_STATE_PAUSE = 5;
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    public static final int CURRENT_STATE_ERROR = 7;

    public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";//当播放的地址只有一个的时候的key
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER = 0;//default
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 1;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 2;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 3;
    public static boolean ACTION_BAR_EXIST = false;
    public static boolean TOOL_BAR_EXIST = true;
    public static int FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    public static int NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static boolean SAVE_PROGRESS = true;
    public static boolean WIFI_TIP_DIALOG_SHOWED = false;
    public static int VIDEO_IMAGE_DISPLAY_TYPE = 0;
    public static long CLICK_QUIT_FULLSCREEN_TIME = 0;
    public static long lastAutoFullscreenTime = 0;
    public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseAllVideos();
                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    try {
                        if (YdVideoPlayerManager.getCurrentJzvd().currentState == YdVideoPlayer.CURRENT_STATE_PLAYING) {
                            YdVideoPlayerManager.getCurrentJzvd().startButton.performClick();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
                default:
                    break;
            }
        }
    };
    protected static YdUserAction JZ_USER_EVENT;
    protected static Timer UPDATE_PROGRESS_TIMER;
    public int currentState = -1;
    public int currentScreen = -1;
    public Object[] objects = null;
    public long seekToInAdvance = 0;
    public ImageView startButton;
    public SeekBar progressBar;
    public ImageView fullscreenButton;
    public TextView currentTimeTextView, totalTimeTextView;
    public ViewGroup textureViewContainer;
    public ViewGroup topContainer, bottomContainer;
    public int widthRatio = 0;
    public int heightRatio = 0;
    public Object[] dataSourceObjects;//这个参数原封不动直接通过JZMeidaManager传给JZMediaInterface。
    public int currentUrlMapIndex = 0;
    public int positionInList = -1;
    public int videoRotation = 0;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected AudioManager mAudioManager;
    protected ProgressTimerTask mProgressTimerTask;
    protected boolean mTouchingProgressBar;
    protected float mDownX;
    protected float mDownY;
    protected boolean mChangeVolume;
    protected boolean mChangePosition;
    protected boolean mChangeBrightness;
    protected long mGestureDownPosition;
    protected int mGestureDownVolume;
    protected float mGestureDownBrightness;
    protected long mSeekTimePosition;
    boolean tmp_test_back = false;

    public YdVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public YdVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public static void releaseAllVideos() {
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            Log.d(TAG, "releaseAllVideos");
            YdVideoPlayerManager.completeAll();
            YdMediaManager.instance().positionInList = -1;
            YdMediaManager.instance().releaseMediaPlayer();
        }
    }

    public static void startFullscreen(Context context, Class _class, String url, Object... objects) {
        LinkedHashMap map = new LinkedHashMap();
        map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        startFullscreen(context, _class, dataSourceObjects, 0, objects);
    }

    public static void startFullscreen(Context context, Class _class, Object[] dataSourceObjects, int defaultUrlMapIndex, Object... objects) {
        hideSupportActionBar(context);
        YdMediaUtils.setRequestedOrientation(context, FULLSCREEN_ORIENTATION);
        ViewGroup vp = (YdMediaUtils.scanForActivity(context))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.ydsdk_jz_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        try {
            Constructor<YdVideoPlayer> constructor = _class.getConstructor(Context.class);
            final YdVideoPlayer jzVideoPlayer = constructor.newInstance(context);
            jzVideoPlayer.setId(R.id.ydsdk_jz_fullscreen_id);
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(jzVideoPlayer, lp);
//            final Animation ra = AnimationUtils.loadAnimation(context, R.anim.ydsdk_start_fullscreen);
//            jzVideoPlayer.setAnimation(ra);
            jzVideoPlayer.setUp(dataSourceObjects, defaultUrlMapIndex, YdVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            jzVideoPlayer.startButton.performClick();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean backPress() {
        Log.i(TAG, "backPress");
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) < FULL_SCREEN_NORMAL_DELAY)
            return false;

        if (YdVideoPlayerManager.getSecondFloor() != null) {
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            if (YdMediaUtils.dataSourceObjectsContainsUri(YdVideoPlayerManager.getFirstFloor().dataSourceObjects, YdMediaManager.getCurrentDataSource())) {
                YdVideoPlayer jzVideoPlayer = YdVideoPlayerManager.getSecondFloor();
                jzVideoPlayer.onEvent(jzVideoPlayer.currentScreen == YdVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN ?
                        YdUserAction.ON_QUIT_FULLSCREEN :
                        YdUserAction.ON_QUIT_TINYSCREEN);
                YdVideoPlayerManager.getFirstFloor().playOnThisJzvd();
            } else {
                quitFullscreenOrTinyWindow();
            }
            return true;
        } else if (YdVideoPlayerManager.getFirstFloor() != null &&
                (YdVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN ||
                        YdVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_TINY)) {//以前我总想把这两个判断写到一起，这分明是两个独立是逻辑
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            quitFullscreenOrTinyWindow();
            return true;
        }
        return false;
    }

    public static void quitFullscreenOrTinyWindow() {
        //直接退出全屏和小窗
        YdVideoPlayerManager.getFirstFloor().clearFloatScreen();
        YdMediaManager.instance().releaseMediaPlayer();
        YdVideoPlayerManager.completeAll();
    }

    @SuppressLint("RestrictedApi")
    public static void showSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST && YdMediaUtils.getAppCompActivity(context) != null) {
//            ActionBar ab = JZUtils.getAppCompActivity(context).getSupportActionBar();
//            if (ab != null) {
//                ab.setShowHideAnimationEnabled(false);
//                ab.show();
//            }
        }
        if (TOOL_BAR_EXIST) {
            YdMediaUtils.getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void hideSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST && YdMediaUtils.getAppCompActivity(context) != null) {
//            ActionBar ab = JZUtils.getAppCompActivity(context).getSupportActionBar();
//            if (ab != null) {
//                ab.setShowHideAnimationEnabled(false);
//                ab.hide();
//            }
        }
        if (TOOL_BAR_EXIST) {
            YdMediaUtils.getWindow(context).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void clearSavedProgress(Context context, String url) {
        YdMediaUtils.clearSavedProgress(context, url);
    }

    public static void setJzUserAction(YdUserAction jzUserEvent) {
        JZ_USER_EVENT = jzUserEvent;
    }

    public static void goOnPlayOnResume() {
        if (YdVideoPlayerManager.getCurrentJzvd() != null) {
            YdVideoPlayer jzvd = YdVideoPlayerManager.getCurrentJzvd();
            if (jzvd.currentState == YdVideoPlayer.CURRENT_STATE_PAUSE) {
                jzvd.onStatePlaying();
                YdMediaManager.start();
            }
        }
    }

    public static void goOnPlayOnPause() {
        if (YdVideoPlayerManager.getCurrentJzvd() != null) {
            YdVideoPlayer jzvd = YdVideoPlayerManager.getCurrentJzvd();
            if (jzvd.currentState == YdVideoPlayer.CURRENT_STATE_AUTO_COMPLETE ||
                    jzvd.currentState == YdVideoPlayer.CURRENT_STATE_NORMAL ||
                    jzvd.currentState == YdVideoPlayer.CURRENT_STATE_ERROR) {
//                JZVideoPlayer.releaseAllVideos();
            } else {
                jzvd.onStatePause();
                YdMediaManager.pause();
            }
        }
    }

    public static void onScrollAutoTiny(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        int currentPlayPosition = YdMediaManager.instance().positionInList;
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                if (YdVideoPlayerManager.getCurrentJzvd() != null &&
                        YdVideoPlayerManager.getCurrentJzvd().currentScreen != YdVideoPlayer.SCREEN_WINDOW_TINY &&
                        YdVideoPlayerManager.getCurrentJzvd().currentScreen != YdVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                    if (YdVideoPlayerManager.getCurrentJzvd().currentState == YdVideoPlayer.CURRENT_STATE_PAUSE) {
                        YdVideoPlayer.releaseAllVideos();
                    } else {
                        Log.e(TAG, "onScroll: out screen");
                        YdVideoPlayerManager.getCurrentJzvd().startWindowTiny();
                    }
                }
            } else {
                if (YdVideoPlayerManager.getCurrentJzvd() != null &&
                        YdVideoPlayerManager.getCurrentJzvd().currentScreen == YdVideoPlayer.SCREEN_WINDOW_TINY) {
                    Log.e(TAG, "onScroll: into screen");
                    YdVideoPlayer.backPress();
                }
            }
        }
    }

    public static void onScrollReleaseAllVideos(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        int currentPlayPosition = YdMediaManager.instance().positionInList;
        Log.e(TAG, "onScrollReleaseAllVideos: " +
                currentPlayPosition + " " + firstVisibleItem + " " + currentPlayPosition + " " + lastVisibleItem);
        if (currentPlayPosition >= 0) {
            if ((currentPlayPosition < firstVisibleItem || currentPlayPosition > (lastVisibleItem - 1))) {
                if (YdVideoPlayerManager.getCurrentJzvd().currentScreen != YdVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                    YdVideoPlayer.releaseAllVideos();//为什么最后一个视频横屏会调用这个，其他地方不会
                }
            }
        }
    }

    public static void onChildViewAttachedToWindow(View view, int jzvdId) {
        if (YdVideoPlayerManager.getCurrentJzvd() != null && YdVideoPlayerManager.getCurrentJzvd().currentScreen == YdVideoPlayer.SCREEN_WINDOW_TINY) {
            YdVideoPlayer videoPlayer = view.findViewById(jzvdId);
            if (videoPlayer != null && YdMediaUtils.getCurrentFromDataSource(videoPlayer.dataSourceObjects, videoPlayer.currentUrlMapIndex).equals(YdMediaManager.getCurrentDataSource())) {
                YdVideoPlayer.backPress();
            }
        }
    }

    public static void onChildViewDetachedFromWindow(View view) {
        if (YdVideoPlayerManager.getCurrentJzvd() != null && YdVideoPlayerManager.getCurrentJzvd().currentScreen != YdVideoPlayer.SCREEN_WINDOW_TINY) {
            YdVideoPlayer videoPlayer = YdVideoPlayerManager.getCurrentJzvd();
            if (((ViewGroup) view).indexOfChild(videoPlayer) != -1) {
                if (videoPlayer.currentState == YdVideoPlayer.CURRENT_STATE_PAUSE) {
                    YdVideoPlayer.releaseAllVideos();
                } else {
                    videoPlayer.startWindowTiny();
                }
            }
        }
    }

    public static void setTextureViewRotation(int rotation) {
        if (YdMediaManager.textureView != null) {
            YdMediaManager.textureView.setRotation(rotation);
        }
    }

    public static void setVideoImageDisplayType(int type) {
        YdVideoPlayer.VIDEO_IMAGE_DISPLAY_TYPE = type;
        if (YdMediaManager.textureView != null) {
            YdMediaManager.textureView.requestLayout();
        }
    }

    public Object getCurrentUrl() {
        return YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex);
    }

    public abstract int getLayoutId();

    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        startButton = findViewById(R.id.start);
        fullscreenButton = findViewById(R.id.fullscreen);
        progressBar = findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = findViewById(R.id.current);
        totalTimeTextView = findViewById(R.id.total);
        bottomContainer = findViewById(R.id.layout_bottom);
        textureViewContainer = findViewById(R.id.surface_container);
        topContainer = findViewById(R.id.layout_top);

        startButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        bottomContainer.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);
        textureViewContainer.setOnTouchListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        try {
            if (isCurrentPlay()) {
                NORMAL_ORIENTATION = ((Activity) context).getRequestedOrientation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp(String url, int screen, Object... objects) {
        LinkedHashMap map = new LinkedHashMap();
        map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        setUp(dataSourceObjects, 0, screen, objects);
    }

    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        if (this.dataSourceObjects != null && YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) != null &&
                YdMediaUtils.getCurrentFromDataSource(this.dataSourceObjects, currentUrlMapIndex).equals(YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex))) {
            return;
        }
        if (isCurrentJZVD() && YdMediaUtils.dataSourceObjectsContainsUri(dataSourceObjects, YdMediaManager.getCurrentDataSource())) {
            long position = 0;
            try {
                position = YdMediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (position != 0) {
                YdMediaUtils.saveProgress(getContext(), YdMediaManager.getCurrentDataSource(), position);
            }
            YdMediaManager.instance().releaseMediaPlayer();
        } else if (isCurrentJZVD() && !YdMediaUtils.dataSourceObjectsContainsUri(dataSourceObjects, YdMediaManager.getCurrentDataSource())) {
            startWindowTiny();
        } else if (!isCurrentJZVD() && YdMediaUtils.dataSourceObjectsContainsUri(dataSourceObjects, YdMediaManager.getCurrentDataSource())) {
            if (YdVideoPlayerManager.getCurrentJzvd() != null &&
                    YdVideoPlayerManager.getCurrentJzvd().currentScreen == YdVideoPlayer.SCREEN_WINDOW_TINY) {
                //需要退出小窗退到我这里，我这里是第一层级
                tmp_test_back = true;
            }
        } else if (!isCurrentJZVD() && !YdMediaUtils.dataSourceObjectsContainsUri(dataSourceObjects, YdMediaManager.getCurrentDataSource())) {
        }
        this.dataSourceObjects = dataSourceObjects;
        this.currentUrlMapIndex = defaultUrlMapIndex;
        this.currentScreen = screen;
        this.objects = objects;
        onStateNormal();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
            if (dataSourceObjects == null || YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.ydsdk_no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL) {
                if (!YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("file") && !
                        YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).toString().startsWith("/") &&
                        !YdMediaUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog();
                    return;
                }
                startVideo();
                onEvent(YdUserAction.ON_CLICK_START_ICON);
            } else if (currentState == CURRENT_STATE_PLAYING) {
                onEvent(YdUserAction.ON_CLICK_PAUSE);
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                YdMediaManager.pause();
                onStatePause();
            } else if (currentState == CURRENT_STATE_PAUSE) {
                onEvent(YdUserAction.ON_CLICK_RESUME);
                YdMediaManager.start();
                onStatePlaying();
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onEvent(YdUserAction.ON_CLICK_START_AUTO_COMPLETE);
                startVideo();
            }
        } else if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
            if (currentState == CURRENT_STATE_AUTO_COMPLETE) return;
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //quit fullscreen
                backPress();
            } else {
                Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
                onEvent(YdUserAction.ON_ENTER_FULLSCREEN);
                startWindowFullscreen();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "onTouch surfaceContainer actionDown [" + this.hashCode() + "] ");
                    mTouchingProgressBar = true;

                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangePosition = false;
                    mChangeBrightness = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "onTouch surfaceContainer actionMove [" + this.hashCode() + "] ");
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                        if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                            if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                cancelProgressTimer();
                                if (absDeltaX >= THRESHOLD) {
                                    // 全屏模式下的CURRENT_STATE_ERROR状态下,不响应进度拖动事件.
                                    // 否则会因为mediaplayer的状态非法导致App Crash
                                    if (currentState != CURRENT_STATE_ERROR) {
                                        mChangePosition = true;
                                        mGestureDownPosition = getCurrentPositionWhenPlaying();
                                    }
                                } else {
                                    //如果y轴滑动距离超过设置的处理范围，那么进行滑动事件处理
                                    if (mDownX < mScreenWidth * 0.5f) {//左侧改变亮度
                                        mChangeBrightness = true;
                                        WindowManager.LayoutParams lp = YdMediaUtils.getWindow(getContext()).getAttributes();
                                        if (lp.screenBrightness < 0) {
                                            try {
                                                mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                                Log.i(TAG, "current system brightness: " + mGestureDownBrightness);
                                            } catch (Settings.SettingNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            mGestureDownBrightness = lp.screenBrightness * 255;
                                            Log.i(TAG, "current activity brightness: " + mGestureDownBrightness);
                                        }
                                    } else {//右侧改变声音
                                        mChangeVolume = true;
                                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    }
                                }
                            }
                        }
                    }
                    if (mChangePosition) {
                        long totalTimeDuration = getDuration();
                        mSeekTimePosition = (int) (mGestureDownPosition + deltaX * totalTimeDuration / mScreenWidth);
                        if (mSeekTimePosition > totalTimeDuration)
                            mSeekTimePosition = totalTimeDuration;
                        String seekTime = YdMediaUtils.stringForTime(mSeekTimePosition);
                        String totalTime = YdMediaUtils.stringForTime(totalTimeDuration);

                        showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration);
                    }
                    if (mChangeVolume) {
                        deltaY = -deltaY;
                        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mGestureDownVolume + deltaV, 0);
                        //dialog中显示百分比
                        int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
                        showVolumeDialog(-deltaY, volumePercent);
                    }

                    if (mChangeBrightness) {
                        deltaY = -deltaY;
                        int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
                        WindowManager.LayoutParams params = YdMediaUtils.getWindow(getContext()).getAttributes();
                        if (((mGestureDownBrightness + deltaV) / 255) >= 1) {//这和声音有区别，必须自己过滤一下负值
                            params.screenBrightness = 1;
                        } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                            params.screenBrightness = 0.01f;
                        } else {
                            params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
                        }
                        YdMediaUtils.getWindow(getContext()).setAttributes(params);
                        //dialog中显示百分比
                        int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
                        showBrightnessDialog(brightnessPercent);
//                        mDownY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i(TAG, "onTouch surfaceContainer actionUp [" + this.hashCode() + "] ");
                    mTouchingProgressBar = false;
                    dismissProgressDialog();
                    dismissVolumeDialog();
                    dismissBrightnessDialog();
                    if (mChangePosition) {
                        onEvent(YdUserAction.ON_TOUCH_SCREEN_SEEK_POSITION);
                        YdMediaManager.seekTo(mSeekTimePosition);
                        long duration = getDuration();
                        int progress = (int) (mSeekTimePosition * 100 / (duration == 0 ? 1 : duration));
                        progressBar.setProgress(progress);
                    }
                    if (mChangeVolume) {
                        onEvent(YdUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME);
                    }
                    startProgressTimer();
                    break;
                  default:
                      break;
            }
        }
        return false;
    }

    public void startVideo() {
        YdVideoPlayerManager.completeAll();
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        initTextureView();
        addTextureView();
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        YdMediaUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        YdMediaManager.setDataSource(dataSourceObjects);
        YdMediaManager.setCurrentDataSource(YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        YdMediaManager.instance().positionInList = positionInList;
        onStatePreparing();
        YdVideoPlayerManager.setFirstFloor(this);
    }

    @Override
    public void onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ");
        onStatePrepared();
        onStatePlaying();
    }

    public void setState(int state) {
        setState(state, 0, 0);
    }

    public void setState(int state, int urlMapIndex, int seekToInAdvance) {
        switch (state) {
            case CURRENT_STATE_NORMAL:
                onStateNormal();
                break;
            case CURRENT_STATE_PREPARING:
                onStatePreparing();
                break;
            case CURRENT_STATE_PREPARING_CHANGING_URL:
                onStatePreparingChangingUrl(urlMapIndex, seekToInAdvance);
                break;
            case CURRENT_STATE_PLAYING:
                onStatePlaying();
                break;
            case CURRENT_STATE_PAUSE:
                onStatePause();
                break;
            case CURRENT_STATE_ERROR:
                onStateError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                onStateAutoComplete();
                break;
                default:break;
        }
    }

    public void onStateNormal() {
        Log.i(TAG, "onStateNormal " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_NORMAL;
        cancelProgressTimer();
    }

    public void onStatePreparing() {
        Log.i(TAG, "onStatePreparing " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_PREPARING;
        resetProgressAndTime();
    }

    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        currentState = CURRENT_STATE_PREPARING_CHANGING_URL;
        this.currentUrlMapIndex = urlMapIndex;
        this.seekToInAdvance = seekToInAdvance;
        YdMediaManager.setDataSource(dataSourceObjects);
        YdMediaManager.setCurrentDataSource(YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        YdMediaManager.instance().prepare();
    }

    public void onStatePrepared() {//因为这个紧接着就会进入播放状态，所以不设置state
        if (seekToInAdvance != 0) {
            YdMediaManager.seekTo(seekToInAdvance);
            seekToInAdvance = 0;
        } else {
            long position = YdMediaUtils.getSavedProgress(getContext(), YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
            if (position != 0) {
                YdMediaManager.seekTo(position);
            }
        }
    }

    public void onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_PLAYING;
        startProgressTimer();
    }

    public void onStatePause() {
        Log.i(TAG, "onStatePause " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_PAUSE;
        startProgressTimer();
    }

    public void onStateError() {
        Log.i(TAG, "onStateError " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_ERROR;
        cancelProgressTimer();
    }

    public void onStateAutoComplete() {
        Log.i(TAG, "onStateAutoComplete " + " [" + this.hashCode() + "] ");
        currentState = CURRENT_STATE_AUTO_COMPLETE;
        cancelProgressTimer();
        progressBar.setProgress(100);
        currentTimeTextView.setText(totalTimeTextView.getText());
    }

    @Override
    public void onInfo(int what, int extra) {
        Log.d(TAG, "onInfo what - " + what + " extra - " + extra);
    }

    @Override
    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError();
            if (isCurrentPlay()) {
                YdMediaManager.instance().releaseMediaPlayer();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    public void onAutoCompletion() {
        Runtime.getRuntime().gc();
        Log.i(TAG, "onAutoCompletion " + " [" + this.hashCode() + "] ");
        onEvent(YdUserAction.ON_AUTO_COMPLETE);
        dismissVolumeDialog();
        dismissProgressDialog();
        dismissBrightnessDialog();
        onStateAutoComplete();

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            backPress();
        }
        YdMediaManager.instance().releaseMediaPlayer();
        YdMediaUtils.saveProgress(getContext(), YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), 0);
    }

    public void onCompletion() {
        Log.i(TAG, "onCompletion " + " [" + this.hashCode() + "] ");
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            long position = getCurrentPositionWhenPlaying();
            YdMediaUtils.saveProgress(getContext(), YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), position);
        }
        cancelProgressTimer();
        dismissBrightnessDialog();
        dismissProgressDialog();
        dismissVolumeDialog();
        onStateNormal();
        textureViewContainer.removeView(YdMediaManager.textureView);
        YdMediaManager.instance().currentVideoWidth = 0;
        YdMediaManager.instance().currentVideoHeight = 0;

        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        YdMediaUtils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        clearFullscreenLayout();
        YdMediaUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);

        if (YdMediaManager.surface != null) YdMediaManager.surface.release();
        if (YdMediaManager.savedSurfaceTexture != null)
            YdMediaManager.savedSurfaceTexture.release();
        YdMediaManager.textureView = null;
        YdMediaManager.savedSurfaceTexture = null;
    }

    public void release() {
        if (YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex).equals(YdMediaManager.getCurrentDataSource()) &&
                (System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            //在非全屏的情况下只能backPress()
            if (YdVideoPlayerManager.getSecondFloor() != null &&
                    YdVideoPlayerManager.getSecondFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//点击全屏
            } else if (YdVideoPlayerManager.getSecondFloor() == null && YdVideoPlayerManager.getFirstFloor() != null &&
                    YdVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//直接全屏
            } else {
                Log.d(TAG, "releaseMediaPlayer [" + this.hashCode() + "]");
                releaseAllVideos();
            }
        }
    }

    public void initTextureView() {
        removeTextureView();
        YdMediaManager.textureView = new YdResizeTextureView(getContext());
        YdMediaManager.textureView.setSurfaceTextureListener(YdMediaManager.instance());
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        LayoutParams layoutParams =
                new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(YdMediaManager.textureView, layoutParams);
    }

    public void removeTextureView() {
        YdMediaManager.savedSurfaceTexture = null;
        if (YdMediaManager.textureView != null && YdMediaManager.textureView.getParent() != null) {
            ((ViewGroup) YdMediaManager.textureView.getParent()).removeView(YdMediaManager.textureView);
        }
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (YdMediaUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(R.id.ydsdk_jz_fullscreen_id);
        View oldT = vp.findViewById(R.id.ydsdk_jz_tiny_id);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        if (oldT != null) {
            vp.removeView(oldT);
        }
        showSupportActionBar(getContext());
    }

    public void clearFloatScreen() {
        YdMediaUtils.setRequestedOrientation(getContext(), NORMAL_ORIENTATION);
        showSupportActionBar(getContext());
        ViewGroup vp = (YdMediaUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        YdVideoPlayer fullJzvd = vp.findViewById(R.id.ydsdk_jz_fullscreen_id);
        YdVideoPlayer tinyJzvd = vp.findViewById(R.id.ydsdk_jz_tiny_id);

        if (fullJzvd != null) {
            vp.removeView(fullJzvd);
            if (fullJzvd.textureViewContainer != null)
                fullJzvd.textureViewContainer.removeView(YdMediaManager.textureView);
        }
        if (tinyJzvd != null) {
            vp.removeView(tinyJzvd);
            if (tinyJzvd.textureViewContainer != null)
                tinyJzvd.textureViewContainer.removeView(YdMediaManager.textureView);
        }
        YdVideoPlayerManager.setSecondFloor(null);
    }

    @Override
    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        if (YdMediaManager.textureView != null) {
            if (videoRotation != 0) {
                YdMediaManager.textureView.setRotation(videoRotation);
            }
            YdMediaManager.textureView.setVideoSize(YdMediaManager.instance().currentVideoWidth, YdMediaManager.instance().currentVideoHeight);
        }
    }

    public void startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }

    public void setProgressAndText(int progress, long position, long duration) {
//        Log.d(TAG, "setProgressAndText: progress=" + progress + " position=" + position + " duration=" + duration);
        if (!mTouchingProgressBar) {
            if (progress != 0) progressBar.setProgress(progress);
        }
        if (position != 0) currentTimeTextView.setText(YdMediaUtils.stringForTime(position));
        totalTimeTextView.setText(YdMediaUtils.stringForTime(duration));
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) progressBar.setSecondaryProgress(bufferProgress);
    }

    public void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTextView.setText(YdMediaUtils.stringForTime(0));
        totalTimeTextView.setText(YdMediaUtils.stringForTime(0));
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        //TODO 这块的判断应该根据MediaPlayer来
        if (currentState == CURRENT_STATE_PLAYING ||
                currentState == CURRENT_STATE_PAUSE) {
            try {
                position = YdMediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    public long getDuration() {
        long duration = 0;
        //TODO MediaPlayer 判空的问题
//        if (JZMediaManager.instance().mediaPlayer == null) return duration;
        try {
            duration = YdMediaManager.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ");
        cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ");
        onEvent(YdUserAction.ON_SEEK_POSITION);
        startProgressTimer();
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (currentState != CURRENT_STATE_PLAYING &&
                currentState != CURRENT_STATE_PAUSE) return;
        long time = seekBar.getProgress() * getDuration() / 100;
        YdMediaManager.seekTo(time);
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            long duration = getDuration();
            currentTimeTextView.setText(YdMediaUtils.stringForTime(progress * duration / 100));
        }
    }

    public void startWindowFullscreen() {
        Log.i(TAG, "startWindowFullscreen " + " [" + this.hashCode() + "] ");
        hideSupportActionBar(getContext());

        ViewGroup vp = (YdMediaUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.ydsdk_jz_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(YdMediaManager.textureView);
        try {
            Constructor<YdVideoPlayer> constructor = (Constructor<YdVideoPlayer>) YdVideoPlayer.this.getClass().getConstructor(Context.class);
            YdVideoPlayer jzVideoPlayer = constructor.newInstance(getContext());
            jzVideoPlayer.setId(R.id.ydsdk_jz_fullscreen_id);
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(jzVideoPlayer, lp);
            jzVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            jzVideoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, YdVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jzVideoPlayer.setState(currentState);
            jzVideoPlayer.addTextureView();
            YdVideoPlayerManager.setSecondFloor(jzVideoPlayer);
//            final Animation ra = AnimationUtils.loadAnimation(getContext(), R.anim.ydsdk_start_fullscreen);
//            jzVideoPlayer.setAnimation(ra);
            YdMediaUtils.setRequestedOrientation(getContext(), FULLSCREEN_ORIENTATION);

            onStateNormal();
            jzVideoPlayer.progressBar.setSecondaryProgress(progressBar.getSecondaryProgress());
            jzVideoPlayer.startProgressTimer();
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startWindowTiny() {
        Log.i(TAG, "startWindowTiny " + " [" + this.hashCode() + "] ");
        onEvent(YdUserAction.ON_ENTER_TINYSCREEN);
        if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR || currentState == CURRENT_STATE_AUTO_COMPLETE)
            return;
        ViewGroup vp = (YdMediaUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.ydsdk_jz_tiny_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(YdMediaManager.textureView);

        try {
            Constructor<YdVideoPlayer> constructor = (Constructor<YdVideoPlayer>) YdVideoPlayer.this.getClass().getConstructor(Context.class);
            YdVideoPlayer jzVideoPlayer = constructor.newInstance(getContext());
            jzVideoPlayer.setId(R.id.ydsdk_jz_tiny_id);
            LayoutParams lp = new LayoutParams(400, 400);
            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            vp.addView(jzVideoPlayer, lp);
            jzVideoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, YdVideoPlayerStandard.SCREEN_WINDOW_TINY, objects);
            jzVideoPlayer.setState(currentState);
            jzVideoPlayer.addTextureView();
            YdVideoPlayerManager.setSecondFloor(jzVideoPlayer);
            onStateNormal();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentPlay() {
        return isCurrentJZVD()
                && YdMediaUtils.dataSourceObjectsContainsUri(dataSourceObjects, YdMediaManager.getCurrentDataSource());//不仅正在播放的url不能一样，并且各个清晰度也不能一样
    }

    public boolean isCurrentJZVD() {
        return YdVideoPlayerManager.getCurrentJzvd() != null
                && YdVideoPlayerManager.getCurrentJzvd() == this;
    }

    //退出全屏和小窗的方法
    public void playOnThisJzvd() {
        Log.i(TAG, "playOnThisJzvd " + " [" + this.hashCode() + "] ");
        //1.清空全屏和小窗的jzvd
        currentState = YdVideoPlayerManager.getSecondFloor().currentState;
        currentUrlMapIndex = YdVideoPlayerManager.getSecondFloor().currentUrlMapIndex;
        clearFloatScreen();
        //2.在本jzvd上播放
        setState(currentState);
        if (currentState != CURRENT_STATE_PLAYING){
            removeTextureView();
        }
        addTextureView();
    }

    //重力感应的时候调用的函数，
    public void autoFullscreen(float x) {
        if (isCurrentPlay()
                && (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE)
                && currentScreen != SCREEN_WINDOW_FULLSCREEN
                && currentScreen != SCREEN_WINDOW_TINY) {
            if (x > 0) {
                YdMediaUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                YdMediaUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            onEvent(YdUserAction.ON_ENTER_FULLSCREEN);
            startWindowFullscreen();
        }
    }

    public void autoQuitFullscreen() {
        if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000
                && isCurrentPlay()
                && currentState == CURRENT_STATE_PLAYING
                && currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            lastAutoFullscreenTime = System.currentTimeMillis();
            backPress();
        }
    }

    public void onEvent(int type) {
        if (JZ_USER_EVENT != null && isCurrentPlay() && dataSourceObjects != null) {
            JZ_USER_EVENT.onEvent(type, YdMediaUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), currentScreen, objects);
        }
    }

    public static void setMediaInterface(YdMediaInterface mediaInterface) {
        YdMediaManager.instance().jzMediaInterface = mediaInterface;
    }

    public static YdMediaInterface getMediaInterface() {
        return YdMediaManager.instance().jzMediaInterface;
    }



    //TODO 是否有用
    @Override
    public void onSeekComplete() {

    }

    public void showWifiDialog() {
    }

    public void showProgressDialog(float deltaX,
                                   String seekTime, long seekTimePosition,
                                   String totalTime, long totalTimeDuration) {
    }

    public void dismissProgressDialog() {

    }

    public void showVolumeDialog(float deltaY, int volumePercent) {

    }

    public void dismissVolumeDialog() {

    }

    public void showBrightnessDialog(int brightnessPercent) {

    }

    public void dismissBrightnessDialog() {

    }

    public static class JZAutoFullscreenListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
            final float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];
            float z = event.values[SensorManager.DATA_Z];
            //过滤掉用力过猛会有一个反向的大数值
            if (x < -12 || x > 12) {
                if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2000) {
                    if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                        YdVideoPlayerManager.getCurrentJzvd().autoFullscreen(x);
                    }
                    lastAutoFullscreenTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
//                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
                post(new Runnable() {
                    @Override
                    public void run() {
                        long position = getCurrentPositionWhenPlaying();
                        long duration = getDuration();
                        int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                        setProgressAndText(progress, position, duration);
                    }
                });
            }
        }
    }

}
