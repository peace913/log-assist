package com.peace.log.assist;

import android.util.Log;

public class LogAssistService {
    public static int v(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.VERBOSE, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.VERBOSE, tag, msg, null, scope, sdkName, className, methodName, lineNum);
    }

    public static int v(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.VERBOSE, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.VERBOSE, tag, msg, tr, scope, sdkName, className, methodName, lineNum);
    }

    public static int d(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.DEBUG, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.DEBUG, tag, msg, null, scope, sdkName, className, methodName, lineNum);
    }

    public static int d(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.DEBUG, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.DEBUG, tag, msg, tr, scope, sdkName, className, methodName, lineNum);
    }

    public static int i(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.INFO, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.INFO, tag, msg, null, scope, sdkName, className, methodName, lineNum);
    }

    public static int i(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.INFO, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.INFO, tag, msg, tr, scope, sdkName, className, methodName, lineNum);
    }

    public static int w(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.WARN, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.WARN, tag, msg, null, scope, sdkName, className, methodName, lineNum);
    }

    public static int w(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.WARN, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.WARN, tag, msg, tr, scope, sdkName, className, methodName, lineNum);
    }

    public static int w(String tag, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.WARN, tag, null, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.WARN, tag, "", tr, scope, sdkName, className, methodName, lineNum);
    }

    public static int e(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.ERROR, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.ERROR, tag, msg, null, scope, sdkName, className, methodName, lineNum);
    }

    public static int e(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.ERROR, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return log(Log.ERROR, tag, msg, tr, scope, sdkName, className, methodName, lineNum);
    }

    private static int log(int logLevel, String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (logLevel >= LogAssist.getInstance().getAssistLogLevel()) {
            msg += " " + getAssistInfo(scope, sdkName, className, methodName, lineNum);
        }

        int result = 0;
        switch (logLevel) {
            case Log.VERBOSE:
                result = tr == null ? Log.v(tag, msg) : Log.v(tag, msg, tr);
                break;
            case Log.DEBUG:
                result = tr == null ? Log.d(tag, msg) : Log.d(tag, msg, tr);
                break;
            case Log.INFO:
                result = tr == null ? Log.i(tag, msg) : Log.i(tag, msg, tr);
                break;
            case Log.WARN:
                result = tr == null ? Log.w(tag, msg) : Log.w(tag, msg, tr);
                break;
            case Log.ERROR:
                result = tr == null ? Log.e(tag, msg) : Log.e(tag, msg, tr);
                break;
        }

        return result;
    }

    private static String getAssistInfo(String scope, String sdkName, String className, String methodName, int lineNum) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        if (LogAssist.getInstance().isShowArtifact()) {
            sb.append("scope: ").append(scope)
                    .append(", name: ").append(sdkName)
                    .append(", ");
        }
        sb.append("class: ").append(className.replace("/", "."))
                .append(", method: ").append(methodName)
                .append(", line: ").append(lineNum)
                .append(')');
        return sb.toString();
    }
}
