/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.filter;

/**
 *
 * @author msi_ge72
 */
public class CustomException extends Exception {

    public CustomException() {
    }

    public CustomException(String msg) {
        super(msg);
    }

    public CustomException(Throwable throwable) {
        super(throwable);
    }

    public CustomException(String string, Throwable throwable) {
        super(string, throwable);
    }
}
