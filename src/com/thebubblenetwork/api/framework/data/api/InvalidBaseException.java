package com.thebubblenetwork.api.framework.data.api;

/**
 * Created by Jacob on 31/12/2015.
 */
public class InvalidBaseException extends Exception {
    public InvalidBaseException() {
    }

    public InvalidBaseException(String s) {
        super(s);
    }

    public InvalidBaseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidBaseException(Throwable throwable) {
        super(throwable);
    }

    public InvalidBaseException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
