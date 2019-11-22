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
public class Devolve implements java.io.Serializable {

    private long devolveId;
    private Patient patient;
    private long facilityId;
    private long patientId;
    private long communitypharmId;
    private Date dateDevolved;
    private String typeDmoc;
    private String viralLoadAssessed;
    private Double lastViralLoad;
    private Date dateLastViralLoad;
    private String cd4Assessed;
    private Double lastCd4;
    private Date dateLastCd4;
    private String lastClinicStage;
    private Date dateLastClinicStage;
    private String regimentype;
    private String regimen;
    private String arvDispensed;
    private Date dateNextClinic;
    private Date dateNextRefill;
    private Date dateLastClinic;
    private Date dateLastRefill;
    private String notes;
    private Date dateDiscontinued;
    private String reasonDiscontinued;
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

 

    public Devolve() {
    }

    public Devolve(long devolveId, Patient patient, long facilityId, long communitypharmId, Date dateDevolved) {
        this.devolveId = devolveId;
        this.patient = patient;
        this.facilityId = facilityId;
        this.communitypharmId = communitypharmId;
        this.dateDevolved = dateDevolved;
    }

    public Devolve(long devolveId, Patient patient, long facilityId, long patientId, long communitypharmId, Date dateDevolved, String typeDmoc, String viralLoadAssessed, Double lastViralLoad, Date dateLastViralLoad, String cd4Assessed, Double lastCd4, Date dateLastCd4, String lastClinicStage, Date dateLastClinicStage, String regimentype, String regimen, String arvDispensed, Date dateNextClinic, Date dateNextRefill, Date dateLastClinic, Date dateLastRefill, String notes, Date dateDiscontinued, String reasonDiscontinued, Date timeStamp, Long userId, Integer uploaded, Date timeUploaded, String uuid) {
        this.devolveId = devolveId;
        this.patient = patient;
        this.facilityId = facilityId;
        this.patientId = patientId;
        this.communitypharmId = communitypharmId;
        this.dateDevolved = dateDevolved;
        this.typeDmoc = typeDmoc;
        this.viralLoadAssessed = viralLoadAssessed;
        this.lastViralLoad = lastViralLoad;
        this.dateLastViralLoad = dateLastViralLoad;
        this.cd4Assessed = cd4Assessed;
        this.lastCd4 = lastCd4;
        this.dateLastCd4 = dateLastCd4;
        this.lastClinicStage = lastClinicStage;
        this.dateLastClinicStage = dateLastClinicStage;
        this.regimentype = regimentype;
        this.regimen = regimen;
        this.arvDispensed = arvDispensed;
        this.dateNextClinic = dateNextClinic;
        this.dateNextRefill = dateNextRefill;
        this.dateLastClinic = dateLastClinic;
        this.dateLastRefill = dateLastRefill;
        this.notes = notes;
        this.dateDiscontinued = dateDiscontinued;
        this.reasonDiscontinued = reasonDiscontinued;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.uuid = uuid;
    }

    /**
     * @return the devolveId
     */
    public long getDevolveId() {
        return devolveId;
    }

    /**
     * @param devolveId the devolveId to set
     */
    public void setDevolveId(long devolveId) {
        this.devolveId = devolveId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
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
     * @return the dateDevolved
     */
    public Date getDateDevolved() {
        return dateDevolved;
    }

    /**
     * @param dateDevolved the dateDevolved to set
     */
    public void setDateDevolved(Date dateDevolved) {
        this.dateDevolved = dateDevolved;
    }

    /**
     * @return the viralLoadAssessed
     */
    public String getViralLoadAssessed() {
        return viralLoadAssessed;
    }

    /**
     * @param viralLoadAssessed the viralLoadAssessed to set
     */
    public void setViralLoadAssessed(String viralLoadAssessed) {
        this.viralLoadAssessed = viralLoadAssessed;
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
     * @return the cd4Assessed
     */
    public String getCd4Assessed() {
        return cd4Assessed;
    }

    /**
     * @param cd4Assessed the cd4Assessed to set
     */
    public void setCd4Assessed(String cd4Assessed) {
        this.cd4Assessed = cd4Assessed;
    }

    /**
     * @return the lastCd4
     */
    public Double getLastCd4() {
        return lastCd4;
    }

    /**
     * @param lastCd4 the lastCd4 to set
     */
    public void setLastCd4(Double lastCd4) {
        this.lastCd4 = lastCd4;
    }

    /**
     * @return the dateLastCd4
     */
    public Date getDateLastCd4() {
        return dateLastCd4;
    }

    /**
     * @param dateLastCd4 the dateLastCd4 to set
     */
    public void setDateLastCd4(Date dateLastCd4) {
        this.dateLastCd4 = dateLastCd4;
    }

    /**
     * @return the lastClinicStage
     */
    public String getLastClinicStage() {
        return lastClinicStage;
    }

    /**
     * @param lastClinicStage the lastClinicStage to set
     */
    public void setLastClinicStage(String lastClinicStage) {
        this.lastClinicStage = lastClinicStage;
    }

    /**
     * @return the dateLastClinicStage
     */
    public Date getDateLastClinicStage() {
        return dateLastClinicStage;
    }

    /**
     * @param dateLastClinicStage the dateLastClinicStage to set
     */
    public void setDateLastClinicStage(Date dateLastClinicStage) {
        this.dateLastClinicStage = dateLastClinicStage;
    }

    /**
     * @return the regimentype
     */
    public String getRegimentype() {
        return regimentype;
    }

    /**
     * @param regimentype the regimentype to set
     */
    public void setRegimentype(String regimentype) {
        this.regimentype = regimentype;
    }

    /**
     * @return the regimen
     */
    public String getRegimen() {
        return regimen;
    }

    /**
     * @param regimen the regimen to set
     */
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    /**
     * @return the arvDispensed
     */
    public String getArvDispensed() {
        return arvDispensed;
    }

    /**
     * @param arvDispensed the arvDispensed to set
     */
    public void setArvDispensed(String arvDispensed) {
        this.arvDispensed = arvDispensed;
    }

    /**
     * @return the dateNextClinic
     */
    public Date getDateNextClinic() {
        return dateNextClinic;
    }

    /**
     * @param dateNextClinic the dateNextClinic to set
     */
    public void setDateNextClinic(Date dateNextClinic) {
        this.dateNextClinic = dateNextClinic;
    }

    /**
     * @return the dateNextRefill
     */
    public Date getDateNextRefill() {
        return dateNextRefill;
    }

    /**
     * @param dateNextRefill the dateNextRefill to set
     */
    public void setDateNextRefill(Date dateNextRefill) {
        this.dateNextRefill = dateNextRefill;
    }

    /**
     * @return the dateLastClinic
     */
    public Date getDateLastClinic() {
        return dateLastClinic;
    }

    /**
     * @param dateLastClinic the dateLastClinic to set
     */
    public void setDateLastClinic(Date dateLastClinic) {
        this.dateLastClinic = dateLastClinic;
    }

    /**
     * @return the dateLastRefill
     */
    public Date getDateLastRefill() {
        return dateLastRefill;
    }

    /**
     * @param dateLastRefill the dateLastRefill to set
     */
    public void setDateLastRefill(Date dateLastRefill) {
        this.dateLastRefill = dateLastRefill;
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
     * @return the dateDiscontinued
     */
    public Date getDateDiscontinued() {
        return dateDiscontinued;
    }

    /**
     * @param dateDiscontinued the dateDiscontinued to set
     */
    public void setDateDiscontinued(Date dateDiscontinued) {
        this.dateDiscontinued = dateDiscontinued;
    }

    /**
     * @return the reasonDiscontinued
     */
    public String getReasonDiscontinued() {
        return reasonDiscontinued;
    }

    /**
     * @param reasonDiscontinued the reasonDiscontinued to set
     */
    public void setReasonDiscontinued(String reasonDiscontinued) {
        this.reasonDiscontinued = reasonDiscontinued;
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
     * @return the typeDmoc
     */
    public String getTypeDmoc() {
        return typeDmoc;
    }

    /**
     * @param typeDmoc the typeDmoc to set
     */
    public void setTypeDmoc(String typeDmoc) {
        this.typeDmoc = typeDmoc;
    }

}
