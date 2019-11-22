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
public class Validated implements java.io.Serializable{
    
    private long validatedId;
    private long facilityId;
    private long patientId;
    private String tableValidated;
    private String recordId;
    private String validatedBy;
    private Date dateValidated;
    private Date timeStamp;
    private Integer userId;
    private Integer uploaded;
    private Date timeUploaded;
    private String createdBy;

    public Validated() {
    }

    public Validated(long validatedId, long facilityId, long patientId, String tableValidated, String recordId, String validatedBy, Date dateValidated, Date timeStamp, Integer userId, Integer uploaded, Date timeUploaded, String createdBy) {
        this.validatedId = validatedId;
        this.facilityId = facilityId;
        this.patientId = patientId;
        this.tableValidated = tableValidated;
        this.recordId = recordId;
        this.validatedBy = validatedBy;
        this.dateValidated = dateValidated;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.createdBy = createdBy;
    }

    public long getValidatedId() {
        return validatedId;
    }

    public void setValidatedId(long validatedId) {
        this.validatedId = validatedId;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getTableValidated() {
        return tableValidated;
    }

    public void setTableValidated(String tableValidated) {
        this.tableValidated = tableValidated;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }

    public Date getDateValidated() {
        return dateValidated;
    }

    public void setDateValidated(Date dateValidated) {
        this.dateValidated = dateValidated;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    
}
