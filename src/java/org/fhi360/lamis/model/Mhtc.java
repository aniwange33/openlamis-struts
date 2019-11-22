/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.util.Date;

/**
 *
 * @author user10
 */
public class Mhtc {

    private long mhtcId;
    private long facilityId;
    private long communitypharmId;
    private Integer numTested;
    private Integer numPositive;
    private Integer numReferred;
    private Integer numOnsiteVisit;
    private Integer month;
    private Integer year;
    private Date timeStamp;
    private Integer uploaded;
    private Date timeUploaded;
    private Long deviceconfigId;

    public Mhtc() {
    }

    public Mhtc(long mhtcId, long facilityId, long communitypharmId, Integer month, Integer year) {
        this.mhtcId = mhtcId;
        this.facilityId = facilityId;
        this.communitypharmId = communitypharmId;
        this.month = month;
        this.year = year;
    }

    public Mhtc(long mhtcId, long facilityId, long communitypharmId, Integer numTested, Integer numPositive, Integer numReferred, Integer numOnsiteVisit, Integer month, Integer year, Date timeStamp, Integer uploaded, Date timeUploaded, Long deviceconfigId) {
        this.mhtcId = mhtcId;
        this.facilityId = facilityId;
        this.communitypharmId = communitypharmId;
        this.numTested = numTested;
        this.numPositive = numPositive;
        this.numReferred = numReferred;
        this.numOnsiteVisit = numOnsiteVisit;
        this.month = month;
        this.year = year;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.deviceconfigId = deviceconfigId;
    }

    public long getMhtcId() {
        return mhtcId;
    }

    public void setMhtcId(long mhtcId) {
        this.mhtcId = mhtcId;
    }

    /**
     * @return the communitypharmId
     */
    public long getCommunitypharmId() {
        return communitypharmId;
    }

    /**
     * @param communitypharmId the communitypharmId to set
     */
    public void setCommunitypharmId(long communitypharmId) {
        this.communitypharmId = communitypharmId;
    }

    public Integer getNumTested() {
        return numTested;
    }

    public void setNumTested(Integer numTested) {
        this.numTested = numTested;
    }

    public Integer getNumPositive() {
        return numPositive;
    }

    public void setNumPositive(Integer numPositive) {
        this.numPositive = numPositive;
    }

    public Integer getNumReferred() {
        return numReferred;
    }

    public void setNumReferred(Integer numReferred) {
        this.numReferred = numReferred;
    }

    public Integer getNumOnsiteVisit() {
        return numOnsiteVisit;
    }

    public void setNumOnsiteVisit(Integer numOnsiteVisit) {
        this.numOnsiteVisit = numOnsiteVisit;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the uploaded
     */
    public Integer getUploaded() {
        return uploaded;
    }

    /**
     * @param uploaded the uploaded to set
     */
    public void setUploaded(Integer uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * @return the timeUploaded
     */
    public Date getTimeUploaded() {
        return timeUploaded;
    }

    /**
     * @param timeUploaded the timeUploaded to set
     */
    public void setTimeUploaded(Date timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }

    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

}
