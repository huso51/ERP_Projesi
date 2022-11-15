/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.model;

/**
 *
 * @author msi_ge72
 */
public class Config {

    private String entegratorBaseUrl = "einvoice Url girilecek";
    private String erpAngularUrl = "erp Angular Url girilecek";
    private final Database database;
    private final Entegrator entegrator;

    public Config() {
        this.database = new Database();
        this.entegrator = new Entegrator();
    }

    public class Database {

        private String serverName = "erpjava mysql db sunucu adı";
        private int port = 3306;
        private String user = "user gir";
        private String password = "password gir";
        private String database = "erpjava";

        public String getServerName() {
            return serverName;
        }

        public int getPort() {
            return port;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }

        public String getDatabase() {
            return database;
        }

    }

    public class Entegrator {

        private String email = "destek@java.com.tr";
        private String password = "19992002";
        private String token = "Basic ZGVzdGVrQGphdmEuY29tLnRyOjE5OTkyMDAy";

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getToken() {
            return token;
        }

    }

    public String getErpAngularUrl() {
        return erpAngularUrl;
    }

    public Database getDatabase() {
        return database;
    }

    public Entegrator getEntegrator() {
        return entegrator;
    }

    public String getEntegratorBaseUrl() {
        return entegratorBaseUrl;
    }

}
