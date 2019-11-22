/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DURUANYANWU IFEANYI
 */
public class Patientcasemanager implements Serializable {

    private long patientcasemanagerId;
    private long facilityId;
    private long casemanagerId;
    private Patient patient;
    private long patientId;
    private Date dateAssigned;
    private String action;
    private Integer uploaded;
    private Date timeUploaded;
    private Date timeStamp;
    private long userId;
     private String uuid;

    

    public Patientcasemanager() {
    }

    public Patientcasemanager(long patientcasemanagerId, long facilityId, long casemanagerId, Patient patient, long patientId, Date dateAssigned, String action, Integer uploaded, Date timeUploaded, Date timeStamp, long userId, String uuid) {
        this.patientcasemanagerId = patientcasemanagerId;
        this.facilityId = facilityId;
        this.casemanagerId = casemanagerId;
        this.patient = patient;
        this.patientId = patientId;
        this.dateAssigned = dateAssigned;
        this.action = action;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uuid = uuid;
    }

    

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

  

    public long getPatientcasemanagerId() {
        return patientcasemanagerId;
    }

    public void setPatientcasemanagerId(long patientcasemanagerId) {
        this.patientcasemanagerId = patientcasemanagerId;
    }

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public long getCasemanagerId() {
        return casemanagerId;
    }

    public void setCasemanagerId(long casemanagerId) {
        this.casemanagerId = casemanagerId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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
        return "PatientCaseManager{" + "patientcasemanagerId=" + patientcasemanagerId + ", facilityId=" + facilityId + ", casemanagerId=" + casemanagerId + ", patient=" + patient + ", dateAssigned=" + dateAssigned + ", action=" + action + ", uploaded=" + uploaded + ", timeUploaded=" + timeUploaded + ", timeStamp=" + timeStamp + ", userId=" + userId + '}';
    }

}
