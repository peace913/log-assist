package com.peace.log.assist;

public class LogAssist {
    private LogListener mLogListener;
    private boolean mShowArtifact = true;
    private LogAssist() {

    }

    public static LogAssist getInstance() {
        return SingletonHolder.instance;
    }

    public void showArtifactInfo(boolean show) {
        mShowArtifact = show;
    }

    public boolean isShowArtifact() {
        return mShowArtifact;
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
