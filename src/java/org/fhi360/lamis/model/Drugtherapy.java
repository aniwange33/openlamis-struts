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
public class Drugtherapy implements java.io.Serializable {
    private long drugtherapyId;
    private long facilityId;
    private Patient patient;
    private long patientId;
    private long communitypharmId;
    private Date dateVisit;
    private String ois;
    private String therapyProblemScreened;
    private String adherenceIssues;
    private String medicationError;
    private String adrs;
    private String severity;
    private String icsrForm;
    private String adrReferred;
    private Date timeStamp;
    private Integer uploaded;
    private Date timeUploaded;
     private Long deviceconfigId;
    
    public Drugtherapy() {
    }
    
    public Drugtherapy(long drugtherapyId, long facilityId, Patient patient, long communitypharmId, Date dateVisit) {
        this.drugtherapyId = drugtherapyId;
        this.facilityId = facilityId;
        this.patient = patient;
        this.communitypharmId = communitypharmId;
        this.dateVisit = dateVisit;
    }

    public Drugtherapy(long drugtherapyId, long facilityId, Patient patient, long patientId, long communitypharmId, Date dateVisit, String ois, String therapyProblemScreened, String adherenceIssues, String medicationError, String adrs, String severity, String icsrForm, String adrReferred, Date timeStamp, Integer uploaded, Date timeUploaded, Long deviceconfigId) {
        this.drugtherapyId = drugtherapyId;
        this.facilityId = facilityId;
        this.patient = patient;
        this.patientId = patientId;
        this.communitypharmId = communitypharmId;
        this.dateVisit = dateVisit;
        this.ois = ois;
        this.therapyProblemScreened = therapyProblemScreened;
        this.adherenceIssues = adherenceIssues;
        this.medicationError = medicationError;
        this.adrs = adrs;
        this.severity = severity;
        this.icsrForm = icsrForm;
        this.adrReferred = adrReferred;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.deviceconfigId = deviceconfigId;
    }

   

   

    /**
     * @return the drugtherapyId
     */
    public long getDrugtherapyId() {
        return drugtherapyId;
    }

    /**
     * @param drugtherapyId the drugtherapyId to set
     */
    public void setDrugtherapyId(long drugtherapyId) {
        this.drugtherapyId = drugtherapyId;
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
     * @return the dateVisit
     */
    public Date getDateVisit() {
        return dateVisit;
    }

    /**
     * @param dateVisit the dateVisit to set
     */
    public void setDateVisit(Date dateVisit) {
        this.dateVisit = dateVisit;
    }

    /**
     * @return the ois
     */
    public String getOis() {
        return ois;
    }

    /**
     * @param ois the ois to set
     */
    public void setOis(String ois) {
        this.ois = ois;
    }

    /**
     * @return the therapyProblemScreened
     */
    public String getTherapyProblemScreened() {
        return therapyProblemScreened;
    }

    /**
     * @param therapyProblemScreened the therapyProblemScreened to set
     */
    public void setTherapyProblemScreened(String therapyProblemScreened) {
        this.therapyProblemScreened = therapyProblemScreened;
    }

    /**
     * @return the adherenceIssues
     */
    public String getAdherenceIssues() {
        return adherenceIssues;
    }

    /**
     * @param adherenceIssues the adherenceIssues to set
     */
    public void setAdherenceIssues(String adherenceIssues) {
        this.adherenceIssues = adherenceIssues;
    }

    /**
     * @return the medicationError
     */
    public String getMedicationError() {
        return medicationError;
    }

    /**
     * @param medicationError the medicationError to set
     */
    public void setMedicationError(String medicationError) {
        this.medicationError = medicationError;
    }

    /**
     * @return the adrs
     */
    public String getAdrs() {
        return adrs;
    }

    /**
     * @param adrs the adrs to set
     */
    public void setAdrs(String adrs) {
        this.adrs = adrs;
    }

    /**
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * @return the icsrForm
     */
    public String getIcsrForm() {
        return icsrForm;
    }

    /**
     * @param icsrForm the icsrForm to set
     */
    public void setIcsrForm(String icsrForm) {
        this.icsrForm = icsrForm;
    }

    /**
     * @return the adrReferred
     */
    public String getAdrReferred() {
        return adrReferred;
    }

    /**
     * @param adrReferred the adrReferred to set
     */
    public void setAdrReferred(String adrReferred) {
        this.adrReferred = adrReferred;
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
