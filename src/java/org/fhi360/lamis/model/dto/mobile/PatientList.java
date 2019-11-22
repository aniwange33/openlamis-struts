package org.fhi360.lamis.model.dto.mobile;


import java.util.List;
import org.fhi360.lamis.model.Patient;

public class PatientList {
    List<PatientDTO> patient;

    public void setPatient(List<PatientDTO> patient) {
        this.patient = patient;
    }

    public List<PatientDTO> getPatient() {
        return patient;
    }
}
