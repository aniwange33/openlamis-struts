/**
 *
 * @author aalozie
 */

package org.fhi360.lamis.model.dto;
import java.util.Date;

public class PatientDTO {
    private Date dateRegistration;
    private String statusRegistration;
    private Date dateStarted;
    private String currentStatus;
    private Date dateCurrentStatus;
    private String regimentype;
    private String regimen;
    private double lastViralLoad;
    private double lastCd4;
    private double lastCd4p;
    private String lastClinicStage;
    private Date dateLastCd4;
    private Date dateLastViralLoad;
    private Date dateLastRefill;
    private Date dateNextRefill;
    private Date dateLastClinic;
    private Date dateNextClinic;
    private Date dateTracked;
    private String outcome;
    private Date agreedDate;
    private int sendMessage;
    
    public PatientDTO() {        
    }
    
    public Date getDateRegistration() {
        return this.dateRegistration;
    }    
    public void setDateRegistration(Date dateRegistration) {
        this.dateRegistration = dateRegistration;
    }

    public String getStatusRegistration() {
        return this.statusRegistration;
    }    
    public void setStatusRegistration(String statusRegistration) {
        this.statusRegistration = statusRegistration;
    }
    
    public Date getDateStarted() {
        return this.dateStarted;
    }    
    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }
    
    public String getCurrentStatus() {
        return this.currentStatus;
    }    
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    
    public Date getDateCurrentStatus() {
        return this.dateCurrentStatus;
    }
    public void setDateCurrentStatus(Date dateCurrentStatus) {
        this.dateCurrentStatus = dateCurrentStatus;
    }
    
    public String getRegimentype() {
        return this.regimentype;
    }
    public void setRegimentype(String regimentype) {
        this.regimentype = regimentype;
    }
    
    public String getRegimen() {
        return this.regimen;
    }
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }
    
    public String getLastClinicStage() {
        return this.lastClinicStage;
    }
    public void setLastClinicStage(String lastClinicStage) {
        this.lastClinicStage = lastClinicStage;
    }
   
    public double getLastViralLoad () {
        return this.lastViralLoad ;
    }
    public void setLastViralLoad (double lastViralLoad ) {
        this.lastViralLoad  = lastViralLoad ;
    }

    public double getLastCd4() {
        return this.lastCd4;
    }
    public void setLastCd4(double lastCd4) {
        this.lastCd4 = lastCd4;
    }

    public double getLastCd4p() {
        return this.lastCd4p;
    }
    public void setLastCd4p(double lastCd4p) {
        this.lastCd4p = lastCd4p;
    }
    
    public Date getDateLastCd4() {
        return this.dateLastCd4;
    }
    public void setDateLastCd4(Date dateLastCd4) {
        this.dateLastCd4 = dateLastCd4;
    }

    public Date getDateLastViralLoad() {
        return this.dateLastViralLoad;
    }
    public void setDateLastViralLoad(Date dateLastViralLoad) {
        this.dateLastViralLoad = dateLastViralLoad;
    }
    
    public Date getDateLastRefill() {
        return this.dateLastRefill;
    }
    public void setDateLastRefill(Date dateLastRefill) {
        this.dateLastRefill = dateLastRefill;
    }
    
    public Date getDateNextRefill() {
        return this.dateNextRefill;
    }
    public void setDateNextRefill(Date dateNextRefill) {
        this.dateNextRefill = dateNextRefill;
    }
    
    public Date getDateLastClinic() {
        return this.dateLastClinic;
    }
    public void setDateLastClinic(Date dateLastClinic) {
        this.dateLastClinic = dateLastClinic;
    }
    
    public Date getDateNextClinic() {
        return this.dateNextClinic;
    }
    public void setDateNextClinic(Date dateNextClinic) {
        this.dateNextClinic = dateNextClinic;
    }
    
    public Date getDateTracked() {
        return this.dateTracked;
    }
    public void setDateTracked(Date dateTracked) {
        this.dateTracked = dateTracked;
    }
    
    public String getOutcome() {
        return this.outcome;
    }
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    
    public Date getAgreedDate() {
        return this.agreedDate;
    }
    public void setAgreedDate(Date agreedDate) {
        this.agreedDate = agreedDate;
    }
    
    public int getSendMessage() {
        return sendMessage;
    }
    public void setSendMessage(int sendMessage) {
        this.sendMessage = sendMessage;
    }
}
