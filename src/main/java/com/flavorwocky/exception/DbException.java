package com.flavorwocky.exception;

/**
 * Created by luanne on 11/06/14.
 */
public class DbException extends RuntimeException {

    public DbException(String message, Exception e) {
        super(message, e);
    }
}
