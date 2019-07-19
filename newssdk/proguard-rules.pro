# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable


-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
-keep class com.yidian.newssdk.libraries.bra.** {
*;
}
-keep public class * extends com.yidian.newssdk.libraries.bra.BaseQuickAdapter
-keep public class * extends com.yidian.newssdk.libraries.bra.BaseViewHolder
-keepclassmembers  class **$** extends com.yidian.newssdk.libraries.bra.BaseViewHolder {
     <init>(...);
}

#-keep class com.ch

-keep class com.yidian.newssdk.export.*{
    *;
}

-keep class com.yidian.newssdk.exportui.*{
    public <methods>;
}


-keep class com.yidian.newssdk.NewsFeedsSDK$Builder{
    public *;
}

-keep class com.yidian.newssdk.NewsFeedsSDK{
    public *;
}

-keep class com.yidian.newssdk.theme.ThemeManager{
    public *;
}

-keep class com.yidian.newssdk.YdCustomConfigure{
    public *;
}

-keep class com.yidian.newssdk.libraries.ydvd.YdMediaInterface{
    public *;
}

#-keep class com.yidian.newssdk.libraries.ydvd.YdMediaManager{
#    public *;
#}

-keep class com.yidian.newssdk.libraries.ydvd.YdVideoPlayerManager{
    public static com.yidian.newssdk.libraries.ydvd.YdVideoPlayer getCurrentJzvd();
}

-keep class com.yidian.newssdk.libraries.ydvd.YdMediaManager {
    public static com.yidian.newssdk.libraries.ydvd.YdMediaManager instance();
    public int currentVideoWidth;
    public int currentVideoHeight;
}


-keep class com.yidian.newssdk.export.IMediaInterface {
    *;
}

-keep class com.yidian.newssdk.utils.ThreadUtils {
    *;
}

-keep class com.yidian.newssdk.widget.pullRefresh.TipDrawable{
    *;
}




