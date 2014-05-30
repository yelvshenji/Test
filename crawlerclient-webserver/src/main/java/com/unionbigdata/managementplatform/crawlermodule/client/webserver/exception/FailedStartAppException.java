package com.unionbigdata.managementplatform.crawlermodule.client.webserver.exception;

/**
 * custom error
 * when start a app failed in the threadRunApp class or other place ,the exception will be throw.
 * Created by lwj on 14-3-29.
 */
public class FailedStartAppException extends Exception {
    public FailedStartAppException(String message) {
        super(message);
    }

    public FailedStartAppException() {
    }
}
