/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 * @param <T>
 */
public class ResponseData<T> {

    private static final Logger logger = Logger.getLogger(ResponseData.class.getName());
    private T data;
    private Error error;
    private Success success;

    public class Error {

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public class Success {

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
        if (success == null) {
            Success successs = new Success();
            success = successs;
        }
        success.setCode(200);
    }

    public void setData(T data, String message) {
        this.data = data;
        if (success == null) {
            Success successs = new Success();
            success = successs;
        }
        success.setCode(200);
        success.setMessage(message);
    }

    public Error getError() {
        return error;
    }

    public Success getSuccess() {
        return success;
    }

    public void setError(int code, String message) {
        logger.log(Level.INFO, "#####Client Error Response: '{'status: {0}, Error: {1}'}'", new Object[]{code, message});
        if (error == null) {
            Error errorr = new Error();
            error = errorr;
        }
        error.setCode(code);
        error.setMessage(message);
        success = null;
    }

    public Response finalResponse() {
        Gson gson = new Gson();
        if (error == null) {
            if (data instanceof HashMap) {
                HashMap<String, byte[]> hmap = (HashMap<String, byte[]>) data;
                Set set = hmap.entrySet();
                Iterator iterator = set.iterator();
                Map.Entry<String, byte[]> mentry = (Map.Entry) iterator.next();
                return Response
                        .ok(mentry.getValue(), MediaType.APPLICATION_OCTET_STREAM)
                        .header("content-disposition", "attachment; filename = " + mentry.getKey())
                        .build();
            } else {
                return Response.ok(gson.toJson(this)).build();
            }
        } else {
            Response response;
            response = Response.ok(gson.toJson(this)).build();
            /*switch (getError().getCode()) {
                case 401:
                    response = Response.status(Response.Status.UNAUTHORIZED).entity(gson.toJson(this)).build();
                    break;
                case 406:
                    response = Response.status(Response.Status.NOT_ACCEPTABLE).entity(gson.toJson(this)).build();
                    break;
                case 500:
                    response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(this)).build();
                    break;
                case 403:
                    response = Response.status(Response.Status.FORBIDDEN).entity(gson.toJson(this)).build();
                    break;
                default:
                    response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(gson.toJson(this)).build();
                    break;
            }*/
            return response;
        }

    }

    public void succeed() {
        setData(null);
        error = null;
    }

    public void succeed(String message) {
        setData(null, message);
        error = null;
    }

    public boolean isSucceeded() {
        return success != null;
    }

}
