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
public class Encounter implements java.io.Serializable {

    private long encounterId;
    private long facilityId;
    private Patient patient;
    private long patientId;
    private long communitypharmId;
    private Date dateVisit;
    private String question1;
    private String question2;
    private String question3;
    private String question4;
    private String question5;
    private String question6;
    private String question7;
    private String question8;
    private String question9;
    private String regimen1;
    private String regimen2;
    private String regimen3;
    private String regimen4;
    private Integer duration1;
    private Integer duration2;
    private Integer duration3;
    private Integer duration4;
    private Integer prescribed1;
    private Integer prescribed2;
    private Integer prescribed3;
    private Integer prescribed4;
    private Integer dispensed1;
    private Integer dispensed2;
    private Integer dispensed3;
    private Integer dispensed4;
    private String notes;
    private Date nextRefill;
    private String regimentype;
    private Date timeStamp;
    private Integer uploaded;
    private Date timeUploaded;
  
    private Long deviceconfigId;
  private String uuid;

    public String getUUID() {
        return getUuid();
    }

    public void setUUID(String uuid) {
        this.setUuid(uuid);
    }

    public Encounter() {
    }

    public Encounter(long encounterId, long facilityId, Patient patient, long communitypharmId, Date dateVisit) {
        this.encounterId = encounterId;
        this.facilityId = facilityId;
        this.patient = patient;
        this.communitypharmId = communitypharmId;
        this.dateVisit = dateVisit;
    }

    public Encounter(long encounterId, long facilityId, Patient patient, long patientId, long communitypharmId, Date dateVisit, String question1, String question2, String question3, String question4, String question5, String question6, String question7, String question8, String question9, String regimen1, String regimen2, String regimen3, String regimen4, Integer duration1, Integer duration2, Integer duration3, Integer duration4, Integer prescribed1, Integer prescribed2, Integer prescribed3, Integer prescribed4, Integer dispensed1, Integer dispensed2, Integer dispensed3, Integer dispensed4, String notes, Date nextRefill, String regimentype, Date timeStamp, Integer uploaded, Date timeUploaded, Long deviceconfigId, String uuid) {
        this.encounterId = encounterId;
        this.facilityId = facilityId;
        this.patient = patient;
        this.patientId = patientId;
        this.communitypharmId = communitypharmId;
        this.dateVisit = dateVisit;
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
        this.question4 = question4;
        this.question5 = question5;
        this.question6 = question6;
        this.question7 = question7;
        this.question8 = question8;
        this.question9 = question9;
        this.regimen1 = regimen1;
        this.regimen2 = regimen2;
        this.regimen3 = regimen3;
        this.regimen4 = regimen4;
        this.duration1 = duration1;
        this.duration2 = duration2;
        this.duration3 = duration3;
        this.duration4 = duration4;
        this.prescribed1 = prescribed1;
        this.prescribed2 = prescribed2;
        this.prescribed3 = prescribed3;
        this.prescribed4 = prescribed4;
        this.dispensed1 = dispensed1;
        this.dispensed2 = dispensed2;
        this.dispensed3 = dispensed3;
        this.dispensed4 = dispensed4;
        this.notes = notes;
        this.nextRefill = nextRefill;
        this.regimentype = regimentype;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.deviceconfigId = deviceconfigId;
        this.uuid = uuid;
    }


   
    /**
     * @return the encounterId
     */
    public long getEncounterId() {
        return encounterId;
    }

    /**
     * @param encounterId the encounterId to set
     */
    public void setEncounterId(long encounterId) {
        this.encounterId = encounterId;
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

    /**
     * @return the patientId
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
     * @return the question1
     */
    public String getQuestion1() {
        return question1;
    }

    /**
     * @param question1 the question1 to set
     */
    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    /**
     * @return the question2
     */
    public String getQuestion2() {
        return question2;
    }

    /**
     * @param question2 the question2 to set
     */
    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    /**
     * @return the question3
     */
    public String getQuestion3() {
        return question3;
    }

    /**
     * @param question3 the question3 to set
     */
    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    /**
     * @return the question4
     */
    public String getQuestion4() {
        return question4;
    }

    /**
     * @param question4 the question4 to set
     */
    public void setQuestion4(String question4) {
        this.question4 = question4;
    }

    /**
     * @return the question5
     */
    public String getQuestion5() {
        return question5;
    }

    /**
     * @param question5 the question5 to set
     */
    public void setQuestion5(String question5) {
        this.question5 = question5;
    }

    /**
     * @return the question6
     */
    public String getQuestion6() {
        return question6;
    }

    /**
     * @param question6 the question6 to set
     */
    public void setQuestion6(String question6) {
        this.question6 = question6;
    }

    /**
     * @return the question7
     */
    public String getQuestion7() {
        return question7;
    }

    /**
     * @param question7 the question7 to set
     */
    public void setQuestion7(String question7) {
        this.question7 = question7;
    }

    /**
     * @return the regimen1
     */
    public String getRegimen1() {
        return regimen1;
    }

    /**
     * @param regimen1 the regimen1 to set
     */
    public void setRegimen1(String regimen1) {
        this.regimen1 = regimen1;
    }

    /**
     * @return the regimen2
     */
    public String getRegimen2() {
        return regimen2;
    }

    /**
     * @param regimen2 the regimen2 to set
     */
    public void setRegimen2(String regimen2) {
        this.regimen2 = regimen2;
    }

    /**
     * @return the regimen3
     */
    public String getRegimen3() {
        return regimen3;
    }

    /**
     * @param regimen3 the regimen3 to set
     */
    public void setRegimen3(String regimen3) {
        this.regimen3 = regimen3;
    }

    /**
     * @return the regimen4
     */
    public String getRegimen4() {
        return regimen4;
    }

    /**
     * @param regimen4 the regimen4 to set
     */
    public void setRegimen4(String regimen4) {
        this.regimen4 = regimen4;
    }

    /**
     * @return the duration1
     */
    public Integer getDuration1() {
        return duration1;
    }

    /**
     * @param duration1 the duration1 to set
     */
    public void setDuration1(Integer duration1) {
        this.duration1 = duration1;
    }

    /**
     * @return the duration2
     */
    public Integer getDuration2() {
        return duration2;
    }

    /**
     * @param duration2 the duration2 to set
     */
    public void setDuration2(Integer duration2) {
        this.duration2 = duration2;
    }

    /**
     * @return the duration3
     */
    public Integer getDuration3() {
        return duration3;
    }

    /**
     * @param duration3 the duration3 to set
     */
    public void setDuration3(Integer duration3) {
        this.duration3 = duration3;
    }

    /**
     * @return the duration4
     */
    public Integer getDuration4() {
        return duration4;
    }

    /**
     * @param duration4 the duration4 to set
     */
    public void setDuration4(Integer duration4) {
        this.duration4 = duration4;
    }

    /**
     * @return the prescribed1
     */
    public Integer getPrescribed1() {
        return prescribed1;
    }

    /**
     * @param prescribed1 the prescribed1 to set
     */
    public void setPrescribed1(Integer prescribed1) {
        this.prescribed1 = prescribed1;
    }

    /**
     * @return the prescribed2
     */
    public Integer getPrescribed2() {
        return prescribed2;
    }

    /**
     * @param prescribed2 the prescribed2 to set
     */
    public void setPrescribed2(Integer prescribed2) {
        this.prescribed2 = prescribed2;
    }

    /**
     * @return the prescribed3
     */
    public Integer getPrescribed3() {
        return prescribed3;
    }

    /**
     * @param prescribed3 the prescribed3 to set
     */
    public void setPrescribed3(Integer prescribed3) {
        this.prescribed3 = prescribed3;
    }

    /**
     * @return the prescribed4
     */
    public Integer getPrescribed4() {
        return prescribed4;
    }

    /**
     * @param prescribed4 the prescribed4 to set
     */
    public void setPrescribed4(Integer prescribed4) {
        this.prescribed4 = prescribed4;
    }

    /**
     * @return the dispensed1
     */
    public Integer getDispensed1() {
        return dispensed1;
    }

    /**
     * @param dispensed1 the dispensed1 to set
     */
    public void setDispensed1(Integer dispensed1) {
        this.dispensed1 = dispensed1;
    }

    /**
     * @return the dispensed2
     */
    public Integer getDispensed2() {
        return dispensed2;
    }

    /**
     * @param dispensed2 the dispensed2 to set
     */
    public void setDispensed2(Integer dispensed2) {
        this.dispensed2 = dispensed2;
    }

    /**
     * @return the dispensed3
     */
    public Integer getDispensed3() {
        return dispensed3;
    }

    /**
     * @param dispensed3 the dispensed3 to set
     */
    public void setDispensed3(Integer dispensed3) {
        this.dispensed3 = dispensed3;
    }

    /**
     * @return the dispensed4
     */
    public Integer getDispensed4() {
        return dispensed4;
    }

    /**
     * @param dispensed4 the dispensed4 to set
     */
    public void setDispensed4(Integer dispensed4) {
        this.dispensed4 = dispensed4;
    }

    /**
     * @return the nextRefill
     */
    public Date getNextRefill() {
        return nextRefill;
    }

    /**
     * @param nextRefill the nextRefill to set
     */
    public void setNextRefill(Date nextRefill) {
        this.nextRefill = nextRefill;
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
     * @return the question8
     */
    public String getQuestion8() {
        return question8;
    }

    /**
     * @param question8 the question8 to set
     */
    public void setQuestion8(String question8) {
        this.question8 = question8;
    }

    /**
     * @return the question9
     */
    public String getQuestion9() {
        return question9;
    }

    /**
     * @param question9 the question9 to set
     */
    public void setQuestion9(String question9) {
        this.question9 = question9;
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

    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
