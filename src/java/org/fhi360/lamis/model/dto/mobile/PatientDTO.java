package org.fhi360.lamis.model.dto.mobile;

import org.fhi360.lamis.model.Facility;

public class PatientDTO {
    private long patientId;
    private Facility facility;
    private long htsId;
    private String hospitalNum;
    private String uniqueId;
    private String surname;
    private String otherNames;
    private String gender;
    private String dateBirth;
    private String ageUnit;
    private int age;
    private String maritalStatus;
    private String education;
    private String occupation;
    private String address;
    private String phone;
    private String state;
    private String lga;
    private String nextKin;
    private String addressKin;
    private String phoneKin;
    private String relationKin;
    private String entryPoint;
    private String targetGroup;
    private String dateConfirmedHiv;
    private String tbStatus;
    private int pregnant;
    private int userId;
    private int breastfeeding;
    private String timeStamp;
    private String uploaded;
    private String timeUploaded;
    private String dateRegistration;
    private String statusRegistration;
    private long deviceconfigId;

    /**
     * @return the patientId
     */
    public long getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the facility
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * @param facility the facility to set
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * @return the htsId
     */
    public long getHtsId() {
        return htsId;
    }

    /**
     * @param htsId the htsId to set
     */
    public void setHtsId(long htsId) {
        this.htsId = htsId;
    }

    /**
     * @return the hospitalNum
     */
    public String getHospitalNum() {
        return hospitalNum;
    }

    /**
     * @param hospitalNum the hospitalNum to set
     */
    public void setHospitalNum(String hospitalNum) {
        this.hospitalNum = hospitalNum;
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * @param uniqueId the uniqueId to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return the otherNames
     */
    public String getOtherNames() {
        return otherNames;
    }

    /**
     * @param otherNames the otherNames to set
     */
    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the dateBirth
     */
    public String getDateBirth() {
        return dateBirth;
    }

    /**
     * @param dateBirth the dateBirth to set
     */
    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
    }

    /**
     * @return the ageUnit
     */
    public String getAgeUnit() {
        return ageUnit;
    }

    /**
     * @param ageUnit the ageUnit to set
     */
    public void setAgeUnit(String ageUnit) {
        this.ageUnit = ageUnit;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the maritalStatus
     */
    public String getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * @param maritalStatus the maritalStatus to set
     */
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * @return the education
     */
    public String getEducation() {
        return education;
    }

    /**
     * @param education the education to set
     */
    public void setEducation(String education) {
        this.education = education;
    }

    /**
     * @return the occupation
     */
    public String getOccupation() {
        return occupation;
    }

    /**
     * @param occupation the occupation to set
     */
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the lga
     */
    public String getLga() {
        return lga;
    }

    /**
     * @param lga the lga to set
     */
    public void setLga(String lga) {
        this.lga = lga;
    }

    /**
     * @return the nextKin
     */
    public String getNextKin() {
        return nextKin;
    }

    /**
     * @param nextKin the nextKin to set
     */
    public void setNextKin(String nextKin) {
        this.nextKin = nextKin;
    }

    /**
     * @return the addressKin
     */
    public String getAddressKin() {
        return addressKin;
    }

    /**
     * @param addressKin the addressKin to set
     */
    public void setAddressKin(String addressKin) {
        this.addressKin = addressKin;
    }

    /**
     * @return the phoneKin
     */
    public String getPhoneKin() {
        return phoneKin;
    }

    /**
     * @param phoneKin the phoneKin to set
     */
    public void setPhoneKin(String phoneKin) {
        this.phoneKin = phoneKin;
    }

    /**
     * @return the relationKin
     */
    public String getRelationKin() {
        return relationKin;
    }

    /**
     * @param relationKin the relationKin to set
     */
    public void setRelationKin(String relationKin) {
        this.relationKin = relationKin;
    }

    /**
     * @return the entryPoint
     */
    public String getEntryPoint() {
        return entryPoint;
    }

    /**
     * @param entryPoint the entryPoint to set
     */
    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    /**
     * @return the targetGroup
     */
    public String getTargetGroup() {
        return targetGroup;
    }

    /**
     * @param targetGroup the targetGroup to set
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    /**
     * @return the dateConfirmedHiv
     */
    public String getDateConfirmedHiv() {
        return dateConfirmedHiv;
    }

    /**
     * @param dateConfirmedHiv the dateConfirmedHiv to set
     */
    public void setDateConfirmedHiv(String dateConfirmedHiv) {
        this.dateConfirmedHiv = dateConfirmedHiv;
    }

    /**
     * @return the tbStatus
     */
    public String getTbStatus() {
        return tbStatus;
    }

    /**
     * @param tbStatus the tbStatus to set
     */
    public void setTbStatus(String tbStatus) {
        this.tbStatus = tbStatus;
    }

    /**
     * @return the pregnant
     */
    public int getPregnant() {
        return pregnant;
    }

    /**
     * @param pregnant the pregnant to set
     */
    public void setPregnant(int pregnant) {
        this.pregnant = pregnant;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the breastfeeding
     */
    public int getBreastfeeding() {
        return breastfeeding;
    }

    /**
     * @param breastfeeding the breastfeeding to set
     */
    public void setBreastfeeding(int breastfeeding) {
        this.breastfeeding = breastfeeding;
    }

    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the uploaded
     */
    public String getUploaded() {
        return uploaded;
    }

    /**
     * @param uploaded the uploaded to set
     */
    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    /**
     * @return the timeUploaded
     */
    public String getTimeUploaded() {
        return timeUploaded;
    }

    /**
     * @param timeUploaded the timeUploaded to set
     */
    public void setTimeUploaded(String timeUploaded) {
        this.timeUploaded = timeUploaded;
    }

    /**
     * @return the dateRegistration
     */
    public String getDateRegistration() {
        return dateRegistration;
    }

    /**
     * @param dateRegistration the dateRegistration to set
     */
    public void setDateRegistration(String dateRegistration) {
        this.dateRegistration = dateRegistration;
    }

    /**
     * @return the statusRegistration
     */
    public String getStatusRegistration() {
        return statusRegistration;
    }

    /**
     * @param statusRegistration the statusRegistration to set
     */
    public void setStatusRegistration(String statusRegistration) {
        this.statusRegistration = statusRegistration;
    }

    /**
     * @return the deviceconfigId
     */
    public long getDeviceconfigId() {
        return deviceconfigId;
    }

    /**
     * @param deviceconfigId the deviceconfigId to set
     */
    public void setDeviceconfigId(long deviceconfigId) {
        this.deviceconfigId = deviceconfigId;
    }

   }
