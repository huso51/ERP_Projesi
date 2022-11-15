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
public class Integration {

    private String email;
    private String password;
    private String code;
    private String partyId;
    private String partyName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String accountCodeList;
    private String pk;
    private String gb;

    private String certificateSerialNumber;
    private String pin;

    public Integration(
            String email,
            String partyId,
            String partyName,
            String firstName,
            String middleName,
            String lastName,
            String accountCodeList,
            String pk,
            String gb,
            String certificateSerialNumber,
            String pin) {
        this.email = email;
        this.partyId = partyId;
        this.partyName = partyName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.accountCodeList = accountCodeList;
        this.pk = pk;
        this.gb = gb;
        this.certificateSerialNumber = certificateSerialNumber;
        this.pin = pin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyName() {
        return partyName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccountCodeList() {
        return accountCodeList;
    }

    public void setAccountCodeList(String accountCodeList) {
        this.accountCodeList = accountCodeList;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getGb() {
        return gb;
    }

    public void setGb(String gb) {
        this.gb = gb;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

}
