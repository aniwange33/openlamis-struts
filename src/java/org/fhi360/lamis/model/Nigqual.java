/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model;

import java.util.Date;

/**
 *
 * @author user1
 */
public class Nigqual implements java.io.Serializable {

    private long nigqualId;
    private Patient patient;
    private long patientId;
    private long facilityId;
    private long portalId;
    private Date reportingDateBegin;
    private Date reportingDateEnd;
    private Integer reviewPeriodId;
    private String thermaticArea;
    private Integer population;
    private Integer sampleSize;
    private Date timeStamp;
    private Integer uploaded;
    private Date timeUploaded;
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Nigqual() {
    }

    public Nigqual(long nigqualId, Patient patient, long facilityId, long portalId, Integer reviewPeriodId) {
        this.nigqualId = nigqualId;
        this.patient = patient;
        this.facilityId = facilityId;
        this.portalId = portalId;
        this.reviewPeriodId = reviewPeriodId;
    }

    public Nigqual(long nigqualId, Patient patient, long patientId, long facilityId, long portalId, Date reportingDateBegin, Date reportingDateEnd, Integer reviewPeriodId, String thermaticArea, Integer population, Integer sampleSize, Date timeStamp, Integer uploaded, Date timeUploaded, String uuid) {
        this.nigqualId = nigqualId;
        this.patient = patient;
        this.patientId = patientId;
        this.facilityId = facilityId;
        this.portalId = portalId;
        this.reportingDateBegin = reportingDateBegin;
        this.reportingDateEnd = reportingDateEnd;
        this.reviewPeriodId = reviewPeriodId;
        this.thermaticArea = thermaticArea;
        this.population = population;
        this.sampleSize = sampleSize;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.uuid = uuid;
    }

    

    public long getNigqualId() {
        return nigqualId;
    }

    public void setNigqualId(long nigqualId) {
        this.nigqualId = nigqualId;
    }

    public Patient getPatient() {
        return this.patient;
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

    public long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }

    public long getPortalId() {
        return portalId;
    }

    public void setPortalId(long portalId) {
        this.portalId = portalId;
    }

    public Date getReportingDateBegin() {
        return reportingDateBegin;
    }

    public void setReportingDateBegin(Date reportingDateBegin) {
        this.reportingDateBegin = reportingDateBegin;
    }

    public Date getReportingDateEnd() {
        return reportingDateEnd;
    }

    public void setReportingDateEnd(Date reportingDateEnd) {
        this.reportingDateEnd = reportingDateEnd;
    }

    public Integer getReviewPeriodId() {
        return reviewPeriodId;
    }

    public void setReviewPeriodId(Integer reviewPeriodId) {
        this.reviewPeriodId = reviewPeriodId;
    }

    public String getThermaticArea() {
        return thermaticArea;
    }

    public void setThermaticArea(String thermaticArea) {
        this.thermaticArea = thermaticArea;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public Date getTimeStamp() {
        return this.timeStamp;
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

}
