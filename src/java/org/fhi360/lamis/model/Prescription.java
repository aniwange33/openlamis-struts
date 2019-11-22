/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.sql.Timestamp;
import java.sql.Date;

/**
 *
 * @author user10
 */
public class Prescription {
    
    private Long prescriptionId;
    private Long facilityId;
    private Long patientId;
    private String prescriptionType;
    private Long labtestId;
    private Integer status;
    private Long regimenTypeId;
    private Long regimenId;
    private Integer duration;
    private Date dateVisit;
    private Date timeStamp;
    private Long userId;
    private Integer uploaded;
    private Date timeUploaded;

    public Prescription() {
    }

    public Prescription(Long prescriptionId, Long facilityId, Long patientId, String prescriptionType, Long labtestId, Integer status, Long regimenTypeId, Long regimenId, Integer duration, Date dateVisit, Date timeStamp, Long userId, Integer uploaded, Date timeUploaded) {
        this.prescriptionId = prescriptionId;
        this.facilityId = facilityId;
        this.patientId = patientId;
        this.prescriptionType = prescriptionType;
        this.labtestId = labtestId;
        this.status = status;
        this.regimenTypeId = regimenTypeId;
        this.regimenId = regimenId;
        this.duration = duration;
        this.dateVisit = dateVisit;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPrescriptionType() {
        return prescriptionType;
    }

    public void setPrescriptionType(String prescriptionType) {
        this.prescriptionType = prescriptionType;
    }

    public Long getLabtestId() {
        return labtestId;
    }

    public void setLabtestId(Long labtestId) {
        this.labtestId = labtestId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRegimenTypeId() {
        return regimenTypeId;
    }

    public void setRegimenTypeId(Long regimenTypeId) {
        this.regimenTypeId = regimenTypeId;
    }

    public Long getRegimenId() {
        return regimenId;
    }

    public void setRegimenId(Long regimenId) {
        this.regimenId = regimenId;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getDateVisit() {
        return dateVisit;
    }

    public void setDateVisit(Date dateVisit) {
        this.dateVisit = dateVisit;
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

    @Override
    public String toString() {
        return "Prescription{" + "prescriptionId=" + prescriptionId + ", facilityId=" + facilityId + ", patientId=" + patientId + ", prescriptionType=" + prescriptionType + ", labtestId=" + labtestId + ", status=" + status + ", regimenTypeId=" + regimenTypeId + ", regimenId=" + regimenId + ", duration=" + duration + ", dateVisit=" + dateVisit + ", timeStamp=" + timeStamp + ", userId=" + userId + ", uploaded=" + uploaded + ", timeUploaded=" + timeUploaded + '}';
    }

    
}