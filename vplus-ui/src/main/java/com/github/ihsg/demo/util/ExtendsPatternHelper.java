package com.github.ihsg.demo.util;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.util.List;

/**
 * @author jiiiiiin
 * @version 1.0
 */
public class ExtendsPatternHelper {

    private int mMinSize;

    // ！错误次数依赖后台控制
//    private int times = 0;
//    private int mCheckMaxTimes;

    // TODO 修改这个标识为一个boolean 标识是否设置了第一次完成
    private String tmpPwd;
    private boolean isFinish;
    private boolean isOk;
    private static final int MAX_SIZE = 4;
    private static final int MAX_TIMES = 5;
    private int mCurrentSize = 0;
    private List<Integer> hitList;

    private ExtendsPatternHelper(int minSize) {
        this.mMinSize = minSize;
    }

    public static ExtendsPatternHelper newInstance(int maxSize) {
        if (maxSize <= 0) {
            maxSize = MAX_SIZE;
        }
//        if (checkMaxTimes <= 0) {
//            checkMaxTimes = MAX_TIMES;
//        }
        return new ExtendsPatternHelper(maxSize);
    }

    public interface OnValidateForSettingListener {
        /**
         * 绘制校验（mixSize）失败时候回调
         *
         * @param mixSize
         * @param currentSize
         */
        void checkMixSizeErr(int mixSize, int currentSize);

        /**
         * 第一次绘制完成回调
         *
         * @param currentSize
         */
        void successFistTime(int currentSize);

        /**
         * 第二次绘制完成校验两次绘制一致（成功）时候回调
         *
         * @param currentSize
         */
        void successSecondTime(int currentSize);

        /**
         * 第二次绘制完成校验两次绘制不一致（失败）时候回调
         */
        void failSecondTime();
    }

    public ExtendsPatternHelper validateForSetting(List<Integer> hitList, OnValidateForSettingListener validateForSettingListener) {
        this.isFinish = false;
        this.isOk = false;
        this.hitList = hitList;

        if (hitList == null) {
            this.tmpPwd = null;
            validateForSettingListener.checkMixSizeErr(mMinSize, 0);
        } else {
            mCurrentSize = hitList.size();
            if (mCurrentSize < mMinSize) {
                this.tmpPwd = null;
                validateForSettingListener.checkMixSizeErr(mMinSize, mCurrentSize);
            } else if (TextUtils.isEmpty(this.tmpPwd)) {
                //1. draw first time
                this.tmpPwd = convert2String(hitList);
                this.isOk = true;
                validateForSettingListener.successFistTime(mCurrentSize);
            } else if (this.tmpPwd.equals(convert2String(hitList))) {
                //2. draw second time
                this.tmpPwd = null;
                this.isOk = true;
                this.isFinish = true;
                validateForSettingListener.successSecondTime(mCurrentSize);
            } else {
                this.tmpPwd = null;
                validateForSettingListener.failSecondTime();
            }
        }
        return this;
    }

    public interface OnValidateForCheckingListener {
        /**
         * 校验失败，剩余可以校验的次数
         */
        void checkMixSizeErr(int mixSize, int currentSize);

        /**
         * 第一次绘制完成回调
         */
        void success();

    }

    public ExtendsPatternHelper validateForChecking(List<Integer> hitList, OnValidateForCheckingListener validateForCheckingListener) {

        this.hitList = hitList;
//        this.times++;
//        this.isFinish = this.times >= mCheckMaxTimes;
//        final int remainingTimes = mCheckMaxTimes - times;
        this.isFinish = false;
        this.isOk = false;
        this.tmpPwd = null;
        if (hitList == null) {
            validateForCheckingListener.checkMixSizeErr(mMinSize, 0);
        } else {
            mCurrentSize = hitList.size();
            if (mCurrentSize < mMinSize) {
                validateForCheckingListener.checkMixSizeErr(mMinSize, mCurrentSize);
            } else {
                this.isOk = true;
                this.isFinish = true;
                validateForCheckingListener.success();
            }
        }
        return this;
    }

    public String getTmpPwdStartFrom1(@NonNull List<Integer> hitList) {
        final StringBuilder stringBuffer = new StringBuilder(hitList.size());
        for (int i : hitList) {
            stringBuffer.append(++i);
        }
        return stringBuffer.toString();
    }

    private String convert2String(@NonNull List<Integer> hitList) {
        final StringBuilder stringBuffer = new StringBuilder(hitList.size());
        for (int i : hitList) {
            stringBuffer.append(i);
        }
        return stringBuffer.toString();
    }

    public boolean isOk() {
        return isOk;
    }

    public List<Integer> getHitList() {
        return hitList;
    }
}
