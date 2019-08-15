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
#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#不优化输入的类文件
-dontoptimize

#预校验
-dontpreverify

#混淆时是否记录日志
-verbose

#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification

# Retain generic type information

-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**
-keep class com.linken.newssdk.libraries.bra.** {
*;
}
-keep public class * extends com.linken.newssdk.libraries.bra.BaseQuickAdapter
-keep public class * extends com.linken.newssdk.libraries.bra.BaseViewHolder
-keepclassmembers  class **$** extends com.linken.newssdk.libraries.bra.BaseViewHolder {
     <init>(...);
}

#-keep class com.ch

-keep class com.linken.newssdk.ad.data.**{
    <init>(...);
    *;
}

-keep class com.linken.newssdk.data.ad.db.** {
*;
}
-keep class com.linken.newssdk.data.** {
*;
}

-keep public class com.linken.newssdk.data.ad.db.*{
    *;
}


-keep class com.linken.newssdk.utils.** {
    *;
}

-keep class com.linken.newssdk.export.*{
    *;
}

-keep class com.linken.newssdk.exportui.*{
    public <methods>;
}


-keep class com.linken.newssdk.NewsFeedsSDK$Builder{
    public *;
}

-keep class com.linken.newssdk.NewsFeedsSDK{
    public *;
}

-keep class com.linken.newssdk.theme.ThemeManager{
    public *;
}

-keep class com.linken.newssdk.YdCustomConfigure{
    public *;
}

-keep class com.linken.newssdk.libraries.ydvd.YdMediaInterface{
    public *;
}

#-keep class com.linken.newssdk.libraries.ydvd.YdMediaManager{
#    public *;
#}

-keep class com.linken.newssdk.libraries.ydvd.YdVideoPlayerManager{
    public static com.linken.newssdk.libraries.ydvd.YdVideoPlayer getCurrentJzvd();
}

-keep class com.linken.newssdk.libraries.ydvd.YdMediaManager {
    public static com.linken.newssdk.libraries.ydvd.YdMediaManager instance();
    public int currentVideoWidth;
    public int currentVideoHeight;
}


-keep class com.linken.newssdk.export.IMediaInterface {
    *;
}

-keep class com.linken.newssdk.export.INewsInfoCallback {
    *;
}

-keep class com.linken.newssdk.utils.ThreadUtils {
    *;
}

-keep class com.linken.newssdk.widget.pullRefresh.TipDrawable{
    *;
}

### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties {*;}

# If you do not use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do not use RxJava:
-dontwarn rx.**

### greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties




