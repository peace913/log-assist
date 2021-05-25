package com.peace.log.assist;

public interface LogListener {
    boolean onLogArrived(int level, String tag, String msg, Throwable throwable, String scope, String sdkName,
                         String className, String methodName, int line);
}
