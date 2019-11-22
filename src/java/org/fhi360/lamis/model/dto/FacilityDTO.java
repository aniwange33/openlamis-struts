/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.model.dto;

/**
 *
 * @author user1
 */
public class FacilityDTO {
     private long facilityId;
     private long lgaId;
     private String name;
     private String address1;
     private String address2;
     private String phone1;
     private String phone2;
     private String email;
     private String facilityType;
     private Integer padHospitalNum;
     
    public FacilityDTO() {
    }

     public FacilityDTO(long facilityId, long lgaId, String name) {
        this.facilityId = facilityId;
        this.lgaId = lgaId;
        this.name = name;
    }
    public FacilityDTO(long facilityId,  long lgaId, String name, String address1, String address2, String phone1, String phone2, String email, String facilityType, Integer padHospitalNum) {
       this.facilityId = facilityId;
       this.lgaId = lgaId;
       this.name = name;
       this.address1 = address1;
       this.address2 = address2;
       this.phone1 = phone1;
       this.phone2 = phone2;
       this.email = email;
       this.facilityType = facilityType;
       this.padHospitalNum = padHospitalNum;
    }
   
    public long getFacilityId() {
        return this.facilityId;
    }
    
    public void setFacilityId(long facilityId) {
        this.facilityId = facilityId;
    }
    public long getLgaIdId() {
        return this.lgaId;
    }
    
    public void setLgaId(long lgaId) {
        this.lgaId = lgaId;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress1() {
        return this.address1;
    }
    
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return this.address2;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getPhone1() {
        return this.phone1;
    }
    
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }
    public String getPhone2() {
        return this.phone2;
    }
    
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFacilityType() {
        return this.facilityType;
    }
    
    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }
    public Integer getPadHospitalNum() {
        return this.padHospitalNum;
    }
    
    public void setPadHospitalNum(Integer padHospitalNum) {
        this.padHospitalNum = padHospitalNum;
    }

}
