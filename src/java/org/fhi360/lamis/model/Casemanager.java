/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author user10
 */
public class Casemanager implements java.io.Serializable {

    private long casemanagerId;
    private long facilityId;
    private long localId;
    private String fullname;
    private String sex;
    private String age;
    private String phoneNumber;
    private String religion;
    private String address;
    private Integer uploaded;
    private Date timeUploaded;
    private Date timeStamp;
    private long userId;
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public Casemanager() {
    }

    public Casemanager(long casemanagerId, long facilityId, long localId, String fullname, String sex, String age, String phoneNumber, String religion, String address, Integer uploaded, Date timeUploaded, Date timeStamp, long userId, String uuid) {
        this.casemanagerId = casemanagerId;
        this.facilityId = facilityId;
        this.localId = localId;
        this.fullname = fullname;
        this.sex = sex;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.religion = religion;
        this.address = address;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uuid = uuid;
    }

  

    public long getCasemanagerId() {
        return casemanagerId;
    }

    public void setCasemanagerId(long casemanagerId) {
        this.casemanagerId = casemanagerId;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "CaseManager{" + "casemanagerId=" + casemanagerId + ", facilityId=" + facilityId + ", localId=" + localId + ", fullname=" + fullname + ", sex=" + sex + ", age=" + age + ", phoneNumber=" + phoneNumber + ", religion=" + religion + ", address=" + address + ", uploaded=" + uploaded + ", timeUploaded=" + timeUploaded + ", timeStamp=" + timeStamp + ", userId=" + userId + '}';
    }

}
