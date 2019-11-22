package org.fhi360.lamis.model.dto.mobile;
public class Patient2 {
    private long patientId;
    private String hospitalNum;

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
    
}
