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

#---------------------------------1.实体类---------------------------------
# https://github.com/alibaba/fastjson/issues/309
-keep class cn.jiiiiiin.vplus.core.webview.event.model.** { *; }
-keep class com.csii.mobilebank.ynrcc.model.** { *; }
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
#-------------------------------------------------------------------------


#---------------------------------2.第三方包-------------------------------

## butterknife start https://blog.csdn.net/say_from_wen/article/details/72831308
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

##### vplus-core #####
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

##glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}

##### PictureSelector 2.0 #####

##### vplus-core #####

##### vplus-ui #####
# 实体类
-keep class cn.jiiiiiin.vplus.ui.recycler.model.** { *; }

##### CymChad/BaseRecyclerViewAdapterHelper #####
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}
##### CymChad/BaseRecyclerViewAdapterHelper #####

## https://github.com/ragunathjawahar/android-saripaar
-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}

##### vplus-ui #####

#---------------------------------1.实体类---------------------------------
#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------
##### event #####
#-keep class com.csii.mobilebank.ynrcc.event.** { *; }
##### event #####
#-------------------------------------------------------------------------

##### uccmawei/FingerprintIdentify #####
# MeiZuFingerprint
-keep class com.fingerprints.service.** { *; }

# SmsungFingerprint
-keep class com.samsung.android.sdk.** { *; }
##### uccmawei/FingerprintIdentify #####

#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
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

# 解决org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':mobilebank:transformClassesAndResourcesWithProguardForRelease'.
# https://stackoverflow.com/questions/41454128/transformclassesandresourceswithproguardforrelease-failed/43319710
#-ignorewarnings
#-keep class * {
#    public private *;
#}
#----------------------------------------------------------------------------

#---------------------------------------------------------------------------------------------------

-ignorewarnings