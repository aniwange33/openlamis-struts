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
public class Motherinformation implements java.io.Serializable {
    
     private long motherinformationId;
     private long patientId;
     private Facility facility;
     private String hospitalNum;
     private String uniqueId;
     private String surname;
     private String otherNames;
     private Date dateConfirmedHiv;
     private String timeHivDiagnosis;
     private Date dateEnrolledPmtct;
     private Date dateStarted;
     private String regimen;
     private String address;
     private String phone;
     private String artStatus;
     private String inFacility;
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

    
    public Motherinformation() {
    }

    public Motherinformation(long motherinformationId, long patientId, Facility facility, String hospitalNum, String uniqueId, String surname, String otherNames, Date dateConfirmedHiv, String timeHivDiagnosis, Date dateEnrolledPmtct, Date dateStarted, String regimen, String address, String phone, String artStatus, String inFacility, Date timeStamp, Long userId, Integer uploaded, Date timeUploaded, String uuid) {
        this.motherinformationId = motherinformationId;
        this.patientId = patientId;
        this.facility = facility;
        this.hospitalNum = hospitalNum;
        this.uniqueId = uniqueId;
        this.surname = surname;
        this.otherNames = otherNames;
        this.dateConfirmedHiv = dateConfirmedHiv;
        this.timeHivDiagnosis = timeHivDiagnosis;
        this.dateEnrolledPmtct = dateEnrolledPmtct;
        this.dateStarted = dateStarted;
        this.regimen = regimen;
        this.address = address;
        this.phone = phone;
        this.artStatus = artStatus;
        this.inFacility = inFacility;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.uuid = uuid;
    }

  

    public long getMotherinformationId() {
        return motherinformationId;
    }

    public void setMotherinformationId(long motherinformationId) {
        this.motherinformationId = motherinformationId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getHospitalNum() {
        return hospitalNum;
    }

    public void setHospitalNum(String hospitalNum) {
        this.hospitalNum = hospitalNum;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    public Date getDateConfirmedHiv() {
        return dateConfirmedHiv;
    }

    public void setDateConfirmedHiv(Date dateConfirmedHiv) {
        this.dateConfirmedHiv = dateConfirmedHiv;
    }

    public String getTimeHivDiagnosis() {
        return timeHivDiagnosis;
    }

    public void setTimeHivDiagnosis(String timeHivDiagnosis) {
        this.timeHivDiagnosis = timeHivDiagnosis;
    }

    public Date getDateEnrolledPmtct() {
        return dateEnrolledPmtct;
    }

    public void setDateEnrolledPmtct(Date dateEnrolledPmtct) {
        this.dateEnrolledPmtct = dateEnrolledPmtct;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
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

    public String getArtStatus() {
        return artStatus;
    }

    public void setArtStatus(String artStatus) {
        this.artStatus = artStatus;
    }

    public String getInFacility() {
        return inFacility;
    }

    public void setInFacility(String inFacility) {
        this.inFacility = inFacility;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    
}
