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
 * @author user10
 */
public class Assessment implements Serializable {

    private Long assessmentId;
    private Long facilityId;
    private Date dateVisit;
    private String clientCode;

    private String question1;
    private String question2;
    private Integer question3;
    private Integer question4;
    private Integer question5;
    private Integer question6;
    private Integer question7;
    private Integer question8;
    private Integer question9;
    private Integer question10;
    private Integer question11;
    private Integer question12;
  
    private Integer sti1;
    private Integer sti2;
    private Integer sti3;
    private Integer sti4;
    private Integer sti5;
    private Integer sti6;
    private Integer sti7;
    private Integer sti8;
    private Long deviceconfigId;
    private Long userId;
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

  

    public Assessment() {
    }

    public Assessment(Long assessmentId, Long facilityId, Date dateVisit) {
        this.assessmentId = assessmentId;
        this.facilityId = facilityId;
        this.dateVisit = dateVisit;
     
    }

    public Assessment(Long assessmentId, Long facilityId, Date dateVisit, String clientCode, String clientAssessmentCode, String question1, String question2, Integer question3, Integer question4, Integer question5, Integer question6, Integer question7, Integer question8, Integer question9, Integer question10, Integer question11, Integer question12, Integer sti1, Integer sti2, Integer sti3, Integer sti4, Integer sti5, Integer sti6, Integer sti7, Integer sti8, Long deviceconfigId, Long userId, Date timeStamp, Integer uploaded, Date timeUploaded, String uuid) {
        this.assessmentId = assessmentId;
        this.facilityId = facilityId;
        this.dateVisit = dateVisit;
        this.clientCode = clientCode;
    
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
        this.question4 = question4;
        this.question5 = question5;
        this.question6 = question6;
        this.question7 = question7;
        this.question8 = question8;
        this.question9 = question9;
        this.question10 = question10;
        this.question11 = question11;
        this.question12 = question12;
        this.sti1 = sti1;
        this.sti2 = sti2;
        this.sti3 = sti3;
        this.sti4 = sti4;
        this.sti5 = sti5;
        this.sti6 = sti6;
        this.sti7 = sti7;
        this.sti8 = sti8;
        this.deviceconfigId = deviceconfigId;
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.uploaded = uploaded;
        this.timeUploaded = timeUploaded;
        this.uuid = uuid;
    }


  
    /**
     * @return the assessmentId
     */
    public Long getAssessmentId() {
        return assessmentId;
    }

    /**
     * @param assessmentId the assessmentId to set
     */
    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    /**
     * @return the facilityId
     */
    public Long getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId the facilityId to set
     */
    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
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
     * @return the clientCode
     */
    public String getClientCode() {
        return clientCode;
    }

    /**
     * @param clientCode the clientCode to set
     */
    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
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
    public Integer getQuestion3() {
        return question3;
    }

    /**
     * @param question3 the question3 to set
     */
    public void setQuestion3(Integer question3) {
        this.question3 = question3;
    }

    /**
     * @return the question4
     */
    public Integer getQuestion4() {
        return question4;
    }

    /**
     * @param question4 the question4 to set
     */
    public void setQuestion4(Integer question4) {
        this.question4 = question4;
    }

    /**
     * @return the question5
     */
    public Integer getQuestion5() {
        return question5;
    }

    /**
     * @param question5 the question5 to set
     */
    public void setQuestion5(Integer question5) {
        this.question5 = question5;
    }

    /**
     * @return the question6
     */
    public Integer getQuestion6() {
        return question6;
    }

    /**
     * @param question6 the question6 to set
     */
    public void setQuestion6(Integer question6) {
        this.question6 = question6;
    }

    /**
     * @return the question7
     */
    public Integer getQuestion7() {
        return question7;
    }

    /**
     * @param question7 the question7 to set
     */
    public void setQuestion7(Integer question7) {
        this.question7 = question7;
    }

    /**
     * @return the question8
     */
    public Integer getQuestion8() {
        return question8;
    }

    /**
     * @param question8 the question8 to set
     */
    public void setQuestion8(Integer question8) {
        this.question8 = question8;
    }

    /**
     * @return the question9
     */
    public Integer getQuestion9() {
        return question9;
    }

    /**
     * @param question9 the question9 to set
     */
    public void setQuestion9(Integer question9) {
        this.question9 = question9;
    }

    /**
     * @return the question10
     */
    public Integer getQuestion10() {
        return question10;
    }

    /**
     * @param question10 the question10 to set
     */
    public void setQuestion10(Integer question10) {
        this.question10 = question10;
    }

    /**
     * @return the question11
     */
    public Integer getQuestion11() {
        return question11;
    }

    /**
     * @param question11 the question11 to set
     */
    public void setQuestion11(Integer question11) {
        this.question11 = question11;
    }

    /**
     * @return the question12
     */
    public Integer getQuestion12() {
        return question12;
    }

    /**
     * @param question12 the question12 to set
     */
    public void setQuestion12(Integer question12) {
        this.question12 = question12;
    }

    /**
     * @return the sti1
     */
    public Integer getSti1() {
        return sti1;
    }

    /**
     * @param sti1 the sti1 to set
     */
    public void setSti1(Integer sti1) {
        this.sti1 = sti1;
    }

    /**
     * @return the sti2
     */
    public Integer getSti2() {
        return sti2;
    }

    /**
     * @param sti2 the sti2 to set
     */
    public void setSti2(Integer sti2) {
        this.sti2 = sti2;
    }

    /**
     * @return the sti3
     */
    public Integer getSti3() {
        return sti3;
    }

    /**
     * @param sti3 the sti3 to set
     */
    public void setSti3(Integer sti3) {
        this.sti3 = sti3;
    }

    /**
     * @return the sti4
     */
    public Integer getSti4() {
        return sti4;
    }

    /**
     * @param sti4 the sti4 to set
     */
    public void setSti4(Integer sti4) {
        this.sti4 = sti4;
    }

    /**
     * @return the sti5
     */
    public Integer getSti5() {
        return sti5;
    }

    /**
     * @param sti5 the sti5 to set
     */
    public void setSti5(Integer sti5) {
        this.sti5 = sti5;
    }

    /**
     * @return the sti6
     */
    public Integer getSti6() {
        return sti6;
    }

    /**
     * @param sti6 the sti6 to set
     */
    public void setSti6(Integer sti6) {
        this.sti6 = sti6;
    }

    /**
     * @return the sti7
     */
    public Integer getSti7() {
        return sti7;
    }

    /**
     * @param sti7 the sti7 to set
     */
    public void setSti7(Integer sti7) {
        this.sti7 = sti7;
    }

    /**
     * @return the sti8
     */
    public Integer getSti8() {
        return sti8;
    }

    /**
     * @param sti8 the sti8 to set
     */
    public void setSti8(Integer sti8) {
        this.sti8 = sti8;
    }

    /**
     * @return the deviceconfigId
     */
    public Long getDeviceconfigId() {
        return deviceconfigId;
    }

    /**
     * @param deviceconfigId the deviceconfigId to set
     */
    public void setDeviceconfigId(Long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
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

    


}
