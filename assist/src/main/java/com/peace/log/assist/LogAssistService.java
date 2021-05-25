package com.peace.log.assist;

import android.util.Log;

public class LogAssistService {
    public static int v(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.VERBOSE, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.v(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum));
    }

    public static int v(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.VERBOSE, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.v(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum), tr);
    }

    public static int d(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.DEBUG, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.d(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum));
    }

    public static int d(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.DEBUG, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.d(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum), tr);
    }

    public static int i(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.INFO, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.i(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum));
    }

    public static int i(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.INFO, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.i(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum), tr);
    }

    public static int w(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.WARN, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.w(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum));
    }

    public static int w(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.WARN, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.w(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum), tr);
    }

    public static int w(String tag, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.WARN, tag, null, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.w(tag, getAssistInfo(scope, sdkName, className, methodName, lineNum), tr);
    }

    public static int e(String tag, String msg, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.ERROR, tag, msg, null, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.e(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum));
    }

    public static int e(String tag, String msg, Throwable tr, String scope, String sdkName, String className, String methodName, int lineNum) {
        if (LogAssist.getInstance().onLogArrived(Log.ERROR, tag, msg, tr, scope, sdkName, className, methodName, lineNum)) {
            return 0;
        }

        return Log.e(tag, msg + " " + getAssistInfo(scope, sdkName, className, methodName, lineNum), tr);
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
