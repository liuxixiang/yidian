package com.yidian.newssdk.libraries.ydvd;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Nathen on 2017/11/8.
 * 实现系统的播放引擎
 */
public class YdMediaSystem extends YdMediaInterface implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {

    public MediaPlayer mediaPlayer;

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void prepare() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (dataSourceObjects.length > 1) {
                mediaPlayer.setLooping((boolean) dataSourceObjects[1]);
            }
            mediaPlayer.setOnPreparedListener(YdMediaSystem.this);
            mediaPlayer.setOnCompletionListener(YdMediaSystem.this);
            mediaPlayer.setOnBufferingUpdateListener(YdMediaSystem.this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnSeekCompleteListener(YdMediaSystem.this);
            mediaPlayer.setOnErrorListener(YdMediaSystem.this);
            mediaPlayer.setOnInfoListener(YdMediaSystem.this);
            mediaPlayer.setOnVideoSizeChangedListener(YdMediaSystem.this);
            Class<MediaPlayer> clazz = MediaPlayer.class;
            Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
            if (dataSourceObjects.length > 2) {
                method.invoke(mediaPlayer, currentDataSource.toString(), dataSourceObjects[2]);
            } else {
                method.invoke(mediaPlayer, currentDataSource.toString(), null);
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        try {
            mediaPlayer.seekTo((int) time);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public void setSurface(Surface surface) {
        mediaPlayer.setSurface(surface);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        if (currentDataSource.toString().toLowerCase().contains("mp3")) {
            YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                        YdVideoPlayerManager.getCurrentJzvd().onPrepared();
                    }
                }
            });
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                    YdVideoPlayerManager.getCurrentJzvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, final int percent) {
        YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                    YdVideoPlayerManager.getCurrentJzvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                    YdVideoPlayerManager.getCurrentJzvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, final int what, final int extra) {
        YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                    YdVideoPlayerManager.getCurrentJzvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, final int what, final int extra) {
        YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        if (YdVideoPlayerManager.getCurrentJzvd().currentState == YdVideoPlayer.CURRENT_STATE_PREPARING
                                || YdVideoPlayerManager.getCurrentJzvd().currentState == YdVideoPlayer.CURRENT_STATE_PREPARING_CHANGING_URL) {
                            YdVideoPlayerManager.getCurrentJzvd().onPrepared();
                        }
                    } else {
                        YdVideoPlayerManager.getCurrentJzvd().onInfo(what, extra);
                    }
                }
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        YdMediaManager.instance().currentVideoWidth = width;
        YdMediaManager.instance().currentVideoHeight = height;
        YdMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (YdVideoPlayerManager.getCurrentJzvd() != null) {
                    YdVideoPlayerManager.getCurrentJzvd().onVideoSizeChanged();
                }
            }
        });
    }
}
