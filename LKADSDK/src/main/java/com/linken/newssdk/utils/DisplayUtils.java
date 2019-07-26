package com.linken.newssdk.utils;

import android.app.Activity;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import static android.content.Context.AUDIO_SERVICE;

/**
 * 控制亮度 声音
 */

public class DisplayUtils {

    private static float startLight,currentLight;
    private static float startVolume,currentVolume;

    public static void setLight(Activity context, float brightness, WindowManager.LayoutParams params){
        params.screenBrightness=brightness;
        context.getWindow().setAttributes(params);
    }

    /**
     *
     * @param context
     * @param brightness  需要变化的亮度 0---100
     * @param params
     * @return
     */
    public  static float changeLight(Activity context,float brightness, WindowManager.LayoutParams params){
        currentLight= brightness / 100;
        if(currentLight<0){
            currentLight=0f;
        }
        if(currentLight>1){
            currentLight=1.0f;
        }
        Log.i("light",""+currentLight);
        params.screenBrightness=currentLight;
        context.getWindow().setAttributes(params);
        return currentLight;
    }

    public  static float changeVolume(Activity context, float volume){
        currentVolume= volume / 100;
        if(currentVolume<0){
            currentVolume=0f;
        }
        if(currentVolume>1){
            currentVolume=1.0f;
        }
        AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (maxVolume * currentVolume),0);
        return currentVolume;
    }


    public static float getCurrentLight(){
        startLight=currentLight;
        return currentLight * 100;
    }

    public static float getCurrentVolume(){
        startVolume=currentVolume;
        return currentVolume * 100;
    }

    private static float getSystemVolume(Activity context) {
        //初始化音频管理器
        AudioManager mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        //获取系统最大音量
        float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取设备当前音量
        float currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        startVolume = currentVolume / maxVolume;
        return startVolume;
    }

    public static void init(Activity activity) {
        getSystemVolume(activity);
        getSystemBrightness(activity);
    }

    private static  float getSystemBrightness(Activity context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        startLight=((float)systemBrightness)/255.0f;
        Log.i("startLoading",""+startLight+","+systemBrightness);
        return startLight;
    }
}
