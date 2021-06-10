package com.peace.log.assist;

import android.util.Log;

public class LogAssist {
    private LogListener mLogListener;
    private boolean mShowArtifact = true;
    private int mAssistLogLevel = Log.VERBOSE;

    private LogAssist() {

    }

    public static LogAssist getInstance() {
        return SingletonHolder.instance;
    }

    public LogAssist showArtifactInfo(boolean show) {
        mShowArtifact = show;
        return this;
    }

    public LogAssist setAssistLogLevel(int level) {
        mAssistLogLevel = level;
        return this;
    }

    boolean isShowArtifact() {
        return mShowArtifact;
    }

    int getAssistLogLevel() {
        return mAssistLogLevel;
    }

    public void setLogListener(LogListener listener) {
        mLogListener = listener;
    }

    boolean onLogArrived(int level, String tag, String msg, Throwable throwable, String scope, String sdkName, String className, String methodName, int line) {
        if (mLogListener != null) {
            return mLogListener.onLogArrived(level, tag, msg, throwable, scope, sdkName, className, methodName, line);
        }

        return false;
    }


    private static class SingletonHolder {
        private static final LogAssist instance = new LogAssist();
    }
}
