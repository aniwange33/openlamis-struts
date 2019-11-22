/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.util.Date;

/**
 *
 * @author user1
 */
public class Communitypharm implements java.io.Serializable {

    private long communitypharmId;
    private long stateId;
    private long lgaId;
    private String pharmacy;
    private String address;
    private String phone;
    private String phone1;
    private String email;
    private String pin;
    private Date timeStamp;
    private Integer uploaded;
    private Date timeUploaded;
     private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public Communitypharm() {
    }

    public Communitypharm(long communitypharmId, long lgaId, long stateId, String pharmacy) {
        this.communitypharmId = communitypharmId;
        this.lgaId = lgaId;
        this.stateId = stateId;
        this.pharmacy = pharmacy;
    }

    public Communitypharm(long communitypharmId, long stateId, long lgaId, String pharmacy, String address, String phone, String phone1, String email, String pin, Date timeStamp, Integer uploaded, Date timeUploaded, String uuid) {
        this.communitypharmId = communitypharmId;
        this.stateId = stateId;
        this.lgaId = lgaId;
        this.pharmacy = pharmacy;
        this.address = address;
        this.phone = phone;
        this.phone1 = phone1;
        this.email = email;
        this.pin = pin;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.uuid = uuid;
    }

  

    public long getCommunitypharmId() {
        return communitypharmId;
    }

    public void setCommunitypharmId(long communitypharmId) {
        this.communitypharmId = communitypharmId;
    }

    public long getStateId() {
        return stateId;
    }

    public void setStateId(long stateId) {
        this.stateId = stateId;
    }

    public long getLgaId() {
        return lgaId;
    }

    public void setLgaId(long lgaId) {
        this.lgaId = lgaId;
    }

    public String getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(String pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getUploaded() {
        return uploaded;
    }

    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    public Date getTimeUploaded() {
        return timeUploaded;
    }

    public void setTimeUploaded(Date timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    @Override
    public String toString() {
        return "Communitypharm{" + "communitypharmId=" + communitypharmId + ", stateId=" + stateId + ", lgaId=" + lgaId + ", pharmacy=" + pharmacy + ", address=" + address + ", phone=" + phone + ", phone1=" + phone1 + ", email=" + email + ", pin=" + pin + ", timeStamp=" + timeStamp + ", uploaded=" + uploaded + ", timeUploaded=" + timeUploaded + '}';
    }

}
