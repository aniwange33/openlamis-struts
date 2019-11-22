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
public class Appointment {   
    private long appointmentId;
    private long facilityId;
    private Patient patient;
    private long patientId;
    private long communitypharmId;
    private Date dateTracked;
    private String typeTracking;
    private String trackingOutcome;
    private Date dateLastVisit;
    private Date dateNextVisit;
    private Date dateAgreed;
    private Date timeStamp;
    private Integer uploaded;
    private Date timeUploaded;
    private Long deviceconfigId;

    public Appointment() {}

    public Appointment(long appointmentId, long facilityId, Patient patient, long communitypharmId, Date dateTracked) {
        this.appointmentId = appointmentId;
        this.facilityId = facilityId;
        this.patient = patient;
        this.communitypharmId = communitypharmId;
        this.dateTracked = dateTracked;
    }

    public Appointment(long appointmentId, long facilityId, Patient patient, long communitypharmId, Date dateTracked, String typeTracking, String trackingOutcome, Date dateLastVisit, Date dateNextVisit, Date dateAgreed, Date timeStamp, Integer uploaded, Date timeUploaded, Long deviceconfigId) {
        this.appointmentId = appointmentId;
        this.facilityId = facilityId;
        this.patient = patient;
        this.communitypharmId = communitypharmId;
        this.dateTracked = dateTracked;
        this.typeTracking = typeTracking;
        this.trackingOutcome = trackingOutcome;
        this.dateLastVisit = dateLastVisit;
        this.dateNextVisit = dateNextVisit;
        this.dateAgreed = dateAgreed;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.deviceconfigId = deviceconfigId;
    }

  

    /**
     * @return the appointmentId
     */
    public long getAppointmentId() {
        return appointmentId;
    }

    /**
     * @param appointmentId the appointmentId to set
     */
    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * @return the facilityId
     */
    public long getFacilityId() {
        return facilityId;
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

    /**
     * @param facilityId the facilityId to set
     */
    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return the dateLastVisit
     */
    public Date getDateLastVisit() {
        return dateLastVisit;
    }

    /**
     * @param dateLastVisit the dateLastVisit to set
     */
    public void setDateLastVisit(Date dateLastVisit) {
        this.dateLastVisit = dateLastVisit;
    }

    /**
     * @return the dateNextVisit
     */
    public Date getDateNextVisit() {
        return dateNextVisit;
    }

    /**
     * @param dateNextVisit the dateNextVisit to set
     */
    public void setDateNextVisit(Date dateNextVisit) {
        this.dateNextVisit = dateNextVisit;
    }

    
    /**
     * @return the dateTracked
     */
    public Date getDateTracked() {
        return dateTracked;
    }

    /**
     * @param dateTracked the dateTracked to set
     */
    public void setDateTracked(Date dateTracked) {
        this.dateTracked = dateTracked;
    }

    /**
     * @return the typeTracking
     */
    public String getTypeTracking() {
        return typeTracking;
    }

    /**
     * @param typeTracking the typeTracking to set
     */
    public void setTypeTracking(String typeTracking) {
        this.typeTracking = typeTracking;
    }

    /**
     * @return the trackingOutcome
     */
    public String getTrackingOutcome() {
        return trackingOutcome;
    }

    /**
     * @param trackingOutcome the trackingOutcome to set
     */
    public void setTrackingOutcome(String trackingOutcome) {
        this.trackingOutcome = trackingOutcome;
    }

    /**
     * @return the dateAgreed
     */
    public Date getDateAgreed() {
        return dateAgreed;
    }

    /**
     * @param dateAgreed the dateAgreed to set
     */
    public void setDateAgreed(Date dateAgreed) {
        this.dateAgreed = dateAgreed;
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

    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }
    
    
}
