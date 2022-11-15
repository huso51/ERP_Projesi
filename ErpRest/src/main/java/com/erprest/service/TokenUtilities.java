/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
public class TokenUtilities {

    public String getToken(String email, String password) {
        return ("Basic " + new String(Base64.getEncoder().encode((email + ":" + password).getBytes())));
    }

    public List<String> parseToken(String token) {
        final String encodedUserPassword = token.replaceFirst("Basic" + " ", "");
        String usernameAndPassword = null;
        List<String> parsedToken = new ArrayList<>();
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        parsedToken.add(tokenizer.nextToken());
        parsedToken.add(tokenizer.nextToken());
        return parsedToken;
    }
}
