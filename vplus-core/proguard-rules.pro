# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/jiiiiiin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#-------------------------------------------定制化区域----------------------------------------------
# 参考：https://github.com/krschultz/android-proguard-snippets/tree/master/libraries
# 参考：https://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650824879&idx=1&sn=f7d6830cb10046ff61bb461a15353e34&chksm=80b7b431b7c03d271d1d836b586a29f813d738d9b7774075f8b50e7ab0215052c3e075ab71c4&scene=21#wechat_redirect

#---------------------------------1.实体类---------------------------------

# https://github.com/alibaba/fastjson/issues/309
-keep class cn.jiiiiiin.vplus.core.webview.event.model.** { *; }

#-------------------------------------------------------------------------

#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
# https://mp.weixin.qq.com/s/FyxuOuTFyZ_F8D0jQ8w5bg
-keepclassmembers class * extends android.webkit.WebChromeClient{
   public void openFileChooser(...);
}
#----------------------------------------------------------------------------
#---------------------------------3.与js互相调用的类------------------------
# https://mp.weixin.qq.com/s/FyxuOuTFyZ_F8D0jQ8w5bg
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
# 不需要混淆的类名
-keepclassmembers public class cn.jiiiiiin.vplus.core.webview.jsbridgehandler.context{
   public <methods>;
}
#假如是内部类，混淆如下：
#-keepattributes *JavascriptInterface*
#-keep public class org.mq.study.webview.webview.DemoJavaScriptInterface$InnerClass{
#    public <methods>;
#}
#-------------------------------------------------------------------------


#---------------------------------2.第三方包-------------------------------

## butterknife start
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
## butterknife end

#####  tbruyelle/RxPermissions #####

-dontwarn com.tbruyelle.rxpermissions.**

#####  tbruyelle/RxPermissions #####

#####  fastjson proguard rules #####
# https://github.com/alibaba/fastjson

-dontwarn com.alibaba.fastjson.**
-keepattributes Signature
-keepattributes *Annotation*
#####  fastjson proguard rules #####

#####  JoanZapata/android-iconify #####
#-keep class com.joanzapata.iconify.** { *; }
#####  JoanZapata/android-iconify #####

#####  square/okhttp #####
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#####  square/okhttp #####

#####  hongyangAndroid/okhttputils #####
#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}
##okhttp
#-dontwarn okhttp3.**
#-keep class okhttp3.**{*;}
##okio
#-dontwarn okio.**
#-keep class okio.**{*;}
#####  hongyangAndroid/okhttputils #####

##### gyf-dev/ImmersionBar #####
-keep class com.gyf.barlibrary.* {*;}
##### gyf-dev/ImmersionBar #####

##### glide #####
# https://muyangmin.github.io/glide-docs-cn/doc/download-setup.html#proguard
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
##### glide #####

##### PictureSelector 2.0 #####
#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

##### PictureSelector 2.0 #####

# ProGuard configurations for Bugtags
  -keepattributes LineNumberTable,SourceFile

  -keep class com.bugtags.library.** {*;}
  -dontwarn com.bugtags.library.**
  -keep class io.bugtags.** {*;}
  -dontwarn io.bugtags.**
  -dontwarn org.apache.http.**
  -dontwarn android.net.http.AndroidHttpClient
# End Bugtags

#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
# 代码混淆的压缩比例，值在0-7之间
-optimizationpasses 5
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 生成原类名和混淆后的类名的映射文件
-printmapping proguardMapping.txt
# 指定混淆是采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 不混淆Annotation
-keepattributes *Annotation*,InnerClasses
# 不混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# 如果使用了上一行配置，还需要添加如下配置将源文件重命名为SourceFile，以便通过鼠标点击直达源文件：
# https://mp.weixin.qq.com/s?__biz=MzAxMTI4MTkwNQ==&mid=2650824879&idx=1&sn=f7d6830cb10046ff61bb461a15353e34&chksm=80b7b431b7c03d271d1d836b586a29f813d738d9b7774075f8b50e7ab0215052c3e075ab71c4&scene=21#wechat_redirect
-renamesourcefileattribute SourceFile
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}
#----------------------------------------------------------------------------

#---------------------------------------------------------------------------------------------------

-ignorewarnings