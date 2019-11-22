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
public class Eac implements java.io.Serializable {

    private long eacId;
    private Patient patient;
    private long facilityId;
    private long patientId;
    private Date dateEac1;
    private Date dateEac2;
    private Date dateEac3;
    private Date dateSampleCollected;
    private String notes;
    private Date dateLastViralLoad;
    private Double lastViralLoad;
    private Date timeStamp;
    private Long userId;
    private Integer uploaded;
    private Date timeUploaded;
     private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }



    public Eac() {
    }

    public Eac(long eacId, Patient patient, long facilityId, Date dateEac1) {
        this.eacId = eacId;
        this.patient = patient;
        this.facilityId = facilityId;
        this.dateEac1 = dateEac1;
    }

    public Eac(long eacId, Patient patient, long facilityId, long patientId, Date dateEac1, Date dateEac2, Date dateEac3, Date dateSampleCollected, String notes, Date dateLastViralLoad, Double lastViralLoad, Date timeStamp, Long userId, Integer uploaded, Date timeUploaded, String uuid) {
        this.eacId = eacId;
        this.patient = patient;
        this.facilityId = facilityId;
        this.patientId = patientId;
        this.dateEac1 = dateEac1;
        this.dateEac2 = dateEac2;
        this.dateEac3 = dateEac3;
        this.dateSampleCollected = dateSampleCollected;
        this.notes = notes;
        this.dateLastViralLoad = dateLastViralLoad;
        this.lastViralLoad = lastViralLoad;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.uuid = uuid;
    }


    /**
     * @return the eacId
     */
    public long getEacId() {
        return eacId;
    }

    /**
     * @param eacId the eacId to set
     */
    public void setEacId(long eacId) {
        this.eacId = eacId;
    }

    /**
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    
    /**
     * @return the facilityId
     */
    public long getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public Date getDateEac3() {
        return dateEac3;
    }

    public void setDateEac3(Date dateEac3) {
        this.dateEac3 = dateEac3;
    }

    /**
     * @return the dateEac1
     */
    public Date getDateEac1() {
        return dateEac1;
    }

    /**
     * @param dateEac1 the dateEac1 to set
     */
    public void setDateEac1(Date dateEac1) {
        this.dateEac1 = dateEac1;
    }

    /**
     * @return the dateEac2
     */
    public Date getDateEac2() {
        return dateEac2;
    }

    /**
     * @param dateEac2 the dateEac2 to set
     */
    public void setDateEac2(Date dateEac2) {
        this.dateEac2 = dateEac2;
    }

    /**
     * @return the dateSampleCollected
     */
    public Date getDateSampleCollected() {
        return dateSampleCollected;
    }

    /**
     * @param dateSampleCollected the dateSampleCollected to set
     */
    public void setDateSampleCollected(Date dateSampleCollected) {
        this.dateSampleCollected = dateSampleCollected;
    }

    /**
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return the timeStamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
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

    /**
     * @return the dateLastViralLoad
     */
    public Date getDateLastViralLoad() {
        return dateLastViralLoad;
    }

    /**
     * @param dateLastViralLoad the dateLastViralLoad to set
     */
    public void setDateLastViralLoad(Date dateLastViralLoad) {
        this.dateLastViralLoad = dateLastViralLoad;
    }

    /**
     * @return the lastViralLoad
     */
    public Double getLastViralLoad() {
        return lastViralLoad;
    }

    /**
     * @param lastViralLoad the lastViralLoad to set
     */
    public void setLastViralLoad(Double lastViralLoad) {
        this.lastViralLoad = lastViralLoad;
    }

}
