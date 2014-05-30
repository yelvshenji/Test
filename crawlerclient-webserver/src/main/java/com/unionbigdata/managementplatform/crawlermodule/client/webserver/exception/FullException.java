package com.unionbigdata.managementplatform.crawlermodule.client.webserver.exception;

/**
 * Created by lwj on 14-5-19.
 */
public class FullException {
    public String getFullException(Exception e) {
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String result = e.toString() + "\n";
        for (int index = stackTraceElements.length - 1; index >= 0; --index) {
            result += "at [" + stackTraceElements[index].getClassName() + ",";
            result += stackTraceElements[index].getFileName() + ",";
            result += stackTraceElements[index].getMethodName() + ",";
            result += stackTraceElements[index].getLineNumber() + "]\n";
        }
        return result;
    }
}
