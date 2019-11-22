/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.utility.builder;

import org.fhi360.lamis.utility.Scrambler;
import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.lamis.utility.DateUtil;

public class PatientReportListBuilder {

    private HttpSession session;
    private Boolean viewIdentifier;
    private Scrambler scrambler;

    private ArrayList<Map<String, Object>> patientList = new ArrayList<>();
    private Map<String, Map<String, Object>> sortedMaps = new TreeMap<>();

    public PatientReportListBuilder() {
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");
        }
    }

    public ArrayList<Map<String, Object>> buildList(ResultSet resultSet) throws SQLException {
        boolean patientIdColumn = contains(resultSet, "patient_id");
        boolean hospitalNumColumn = contains(resultSet, "hospital_num");
        boolean uniqueIdColumn = contains(resultSet, "unique_id");
        boolean surnameColumn = contains(resultSet, "surname");
        boolean otherNamesColumn = contains(resultSet, "other_names");
        boolean genderColumn = contains(resultSet, "gender");
        boolean dateBirthColumn = contains(resultSet, "date_birth");
        boolean ageColumn = contains(resultSet, "age");
        boolean addressColumn = contains(resultSet, "address");
        boolean phoneColumn = contains(resultSet, "phone");
        boolean stateColumn = contains(resultSet, "state");
        boolean lgaColumn = contains(resultSet, "lga");
        boolean dateRegistrationColumn = contains(resultSet, "date_registration");
        boolean statusRegistrationColumn = contains(resultSet, "status_registration");
        boolean currentStatusColumn = contains(resultSet, "current_status");
        boolean dateCurrentStatusColumn = contains(resultSet, "date_current_status");
        boolean dateStartedColumn = contains(resultSet, "date_started");
        boolean dateVisitColumn = contains(resultSet, "date_visit");
        boolean dateLastClinicColumn = contains(resultSet, "date_last_clinic");
        boolean dateLastRefillColumn = contains(resultSet, "date_last_refill");
        boolean lastRefillDurationColumn = contains(resultSet, "last_refill_duration");
        boolean dateNextClinicColumn = contains(resultSet, "date_next_clinic");
        boolean dateNextRefillColumn = contains(resultSet, "date_next_refill");
        boolean dateLastCd4Column = contains(resultSet, "date_last_cd4");
        boolean dateLastViralLoadColumn = contains(resultSet, "date_last_viral_load");
        boolean lastCd4Column = contains(resultSet, "last_cd4");
        boolean lastCd4pColumn = contains(resultSet, "last_cd4p");
        boolean lastClinicStageColumn = contains(resultSet, "last_clinic_stage");
        boolean lastViralLoadColumn = contains(resultSet, "last_viral_load");
        boolean outcomeColumn = contains(resultSet, "outcome");
        boolean dateTrackedColumn = contains(resultSet, "date_tracked");
        boolean agreedDateColumn = contains(resultSet, "agreed_date");
        boolean causeDeathColumn = contains(resultSet, "cause_death");
        boolean cd4Column = contains(resultSet, "cd4");
        boolean cd4pColumn = contains(resultSet, "cd4p");
        boolean regimentypeColumn = contains(resultSet, "regimentype");
        boolean regimenColumn = contains(resultSet, "regimen");
        boolean pharmacyColumn = contains(resultSet, "pharmacy");
        boolean dateDevolvedColumn = contains(resultSet, "date_devolved");
        boolean originalRegimenColumn = contains(resultSet, "original_regimen");
        boolean clinicStageColumn = contains(resultSet, "clinic_stage");
        boolean tbStatusColumn = contains(resultSet, "tb_status");
        boolean durationColumn = contains(resultSet, "duration");
        boolean nextAppointmentColumn = contains(resultSet, "next_appointment");
        boolean numberOfPatientsColumn = contains(resultSet, "number_of_patients");
        int count = 0;

        try {
            String currentStatusSorting = "";
            String currentPharmacySorting = "";
            while (resultSet.next()) {
                count = count + 1;
                String patientId = "";
                if (patientIdColumn) {
                    patientId = Long.toString(resultSet.getLong("patient_id"));
                }
                String hospitalNum = "";
                if (hospitalNumColumn) {
                    hospitalNum = resultSet.getString("hospital_num");
                }
                String uniqueId = "";
                if (uniqueIdColumn) {
                    uniqueId = resultSet.getObject("unique_id") == null ? "" : resultSet.getString("unique_id");
                }
                String surname = "";
                if (surnameColumn) {
                    surname = resultSet.getObject("surname") == null ? "" : resultSet.getString("surname");
                    surname = (viewIdentifier) ? scrambler.unscrambleCharacters(surname) : surname;
                    surname = StringUtils.upperCase(surname);
                }
                String otherNames = "";
                if (otherNamesColumn) {
                    otherNames = resultSet.getObject("other_names") == null ? "" : resultSet.getString("other_names");
                    otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(otherNames) : otherNames;
                    otherNames = StringUtils.capitalize(otherNames);
                }
                String name = surname + " " + otherNames;
                String gender = "";
                if (genderColumn) {
                    gender = resultSet.getObject("gender") == null ? "" : resultSet.getString("gender");
                }
                String dateBirth = "";
                if (dateBirthColumn) {
                    dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                }
                String age = "";
                if (ageColumn) {
                    age = resultSet.getObject("age") == null ? "" : resultSet.getInt("age") == 0 ? "" : Integer.toString(resultSet.getInt("age"));
                }
                String address = "";
                if (addressColumn) {
                    address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                    address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                    address = StringUtils.capitalize(address);
                }
                String phone = "";
                if (phoneColumn) {
                    phone = resultSet.getObject("phone") == null ? "" : resultSet.getString("phone");
                    phone = (viewIdentifier) ? scrambler.unscrambleNumbers(phone) : phone;
                }
                String state = "";
                if (stateColumn) {
                    state = resultSet.getObject("state") == null ? "" : resultSet.getString("state");
                }
                String lga = "";
                if (lgaColumn) {
                    lga = resultSet.getObject("lga") == null ? "" : resultSet.getString("lga");
                }
                String dateRegistration = "";
                if (dateRegistrationColumn) {
                    dateRegistration = resultSet.getObject("date_registration") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "MM/dd/yyyy");
                }
                String statusRegistration = "";
                if (statusRegistrationColumn) {
                    statusRegistration = resultSet.getObject("status_registration") == null ? "" : resultSet.getString("status_registration");
                }
                String currentStatus = "";
                if (currentStatusColumn) {
                    currentStatus = resultSet.getObject("current_status") == null ? "" : resultSet.getString("current_status");
                }
                String dateCurrentStatus = "";
                if (dateCurrentStatusColumn) {
                    dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "MM/dd/yyyy");
                }
                String dateStarted = "";
                if (dateStartedColumn) {
                    dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                }
                String dateVisit = "";
                if (dateVisitColumn) {
                    dateVisit = resultSet.getObject("date_visit") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_visit"), "MM/dd/yyyy");
                }
                String dateLastClinic = "";
                if (dateLastClinicColumn) {
                    dateLastClinic = resultSet.getObject("date_last_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_clinic"), "MM/dd/yyyy");
                }
                String dateLastRefill = "";
                if (dateLastRefillColumn) {
                    dateLastRefill = resultSet.getObject("date_last_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_refill"), "MM/dd/yyyy");
                }

                String lastRefillDuration = "";
                if (lastRefillDurationColumn) {
                    lastRefillDuration = resultSet.getObject("last_refill_duration") == null ? "" : Integer.toString(resultSet.getInt("last_refill_duration"));
                }

                String outcome = "";
                if (outcomeColumn) {
                    outcome = resultSet.getObject("outcome") == null ? "" : resultSet.getString("outcome");
                }
                String dateTracked = "";
                if (dateTrackedColumn) {
                    dateTracked = resultSet.getObject("date_tracked") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_tracked"), "MM/dd/yyyy");
                }
                String agreedDate = "";
                if (agreedDateColumn) {
                    dateStarted = resultSet.getObject("agreed_date") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("agreed_date"), "MM/dd/yyyy");
                }
                String causeDeath = "";
                if (causeDeathColumn) {
                    causeDeath = resultSet.getObject("cause_death") == null ? "" : resultSet.getString("cause_death");
                }

                String dateNextClinic = "";
                if (dateNextClinicColumn) {
                    dateNextClinic = resultSet.getObject("date_next_clinic") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_next_clinic"), "MM/dd/yyyy");
                }
                String dateNextRefill = "";
                if (dateNextRefillColumn) {
                    dateNextRefill = resultSet.getObject("date_next_refill") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_next_refill"), "MM/dd/yyyy");
                }
                String dateLastViralLoad = "";
                if (dateLastViralLoadColumn) {
                    dateLastViralLoad = resultSet.getObject("date_last_viral_load") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_viral_load"), "MM/dd/yyyy");
                }
                String lastViralLoad = "";
                if (lastViralLoadColumn) {
                    if (!dateLastViralLoad.equalsIgnoreCase("")) {
                        lastViralLoad = resultSet.getObject("last_viral_load") == null ? "" : resultSet.getDouble("last_viral_load") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_viral_load"));
                    }
                }

                String dateLastCd4 = "";
                if (dateLastCd4Column) {
                    dateLastCd4 = resultSet.getObject("date_last_cd4") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_last_cd4"), "MM/dd/yyyy");
                }
                String lastCd4 = "";
                if (lastCd4Column) {
                    if (!dateLastCd4.equalsIgnoreCase("")) {
                        lastCd4 = resultSet.getObject("last_cd4") == null ? "" : resultSet.getDouble("last_cd4") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_cd4"));
                    }
                }
                String lastCd4p = "";
                if (lastCd4pColumn) {
                    if (!dateLastCd4.equalsIgnoreCase("")) {
                        lastCd4p = resultSet.getObject("last_cd4p") == null ? "" : resultSet.getDouble("last_cd4p") == 0.0 ? "0" : Double.toString(resultSet.getDouble("last_cd4p"));
                    }
                }
                String lastClinicStage = "";
                if (lastClinicStageColumn) {
                    lastClinicStage = resultSet.getObject("last_clinic_stage") == null ? "" : resultSet.getString("last_clinic_stage");
                }
                String cd4 = "";
                if (cd4Column) {
                    cd4 = resultSet.getObject("cd4") == null ? "" : resultSet.getDouble("cd4") == 0.0 ? "" : Double.toString(resultSet.getDouble("cd4"));
                }
                String cd4p = "";
                if (cd4pColumn) {
                    cd4p = resultSet.getObject("cd4p") == null ? "" : resultSet.getDouble("cd4p") == 0.0 ? "" : Double.toString(resultSet.getDouble("cd4p"));
                }
                String regimentype = "";
                if (regimentypeColumn) {
                    regimentype = resultSet.getObject("regimentype") == null ? "" : resultSet.getString("regimentype");
                }
                String regimen = "";
                if (regimenColumn) {
                    regimen = resultSet.getObject("regimen") == null ? "" : resultSet.getString("regimen");
                }
                String pharmacy = "";
                if (pharmacyColumn) {
                    pharmacy = resultSet.getObject("pharmacy") == null ? "" : resultSet.getString("pharmacy");
                }
                String dateDevolved = "";
                if (dateDevolvedColumn) {
                    dateDevolved = resultSet.getObject("date_devolved") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_devolved"), "MM/dd/yyyy");
                }
                String originalRegimen = "";
                if (originalRegimenColumn) {
                    originalRegimen = resultSet.getObject("original_regimen") == null ? "" : resultSet.getString("original_regimen");
                }
                String clinicStage = "";
                if (clinicStageColumn) {
                    clinicStage = resultSet.getObject("clinic_stage") == null ? "" : resultSet.getString("clinic_stage");
                }
                String tbStatus = "";
                if (tbStatusColumn) {
                    tbStatus = resultSet.getObject("tb_status") == null ? "" : resultSet.getString("tb_status");
                }
                String duration = "";
                if (durationColumn) {
                    duration = resultSet.getObject("duration") == null ? "" : resultSet.getInt("duration") == 0 ? "" : Integer.toString(resultSet.getInt("duration"));
                }
                String nextAppointment = "";
                if (nextAppointmentColumn) {
                    nextAppointment = resultSet.getObject("next_appointment") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("next_appointment"), "MM/dd/yyyy");
                }
                int numberOfPatients = 0;
                if (numberOfPatientsColumn) {
                    numberOfPatients = resultSet.getInt("number_of_patients");
                }

                // create an array from object properties 
                Map<String, Object> map = new HashMap<>();
                map.put("patientId", patientId);
                map.put("hospitalNum", hospitalNum);
                map.put("uniqueId", uniqueId);
                map.put("surname", surname);
                map.put("otherNames", otherNames);
                map.put("name", name);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("age", age);
                map.put("address", address);
                map.put("phone", phone);
                map.put("state", state);
                map.put("lga", lga);
                map.put("dateRegistration", dateRegistration);
                map.put("statusRegistration", statusRegistration);
                map.put("currentStatus", currentStatus);
                map.put("dateCurrentStatus", dateCurrentStatus);
                map.put("dateStarted", dateStarted);
                map.put("dateVisit", dateVisit);
                map.put("dateLastClinic", dateLastClinic);
                map.put("dateLastRefill", dateLastRefill);
                map.put("lastRefillDuration", lastRefillDuration);
                map.put("dateNextClinic", dateNextClinic);
                map.put("dateNextRefill", dateNextRefill);
                map.put("outcome", outcome);
                map.put("dateTracked", dateTracked);
                map.put("agreedDate", agreedDate);
                map.put("causeDeath", causeDeath);
                map.put("dateLastCd4", dateLastCd4);
                map.put("dateLastViralLoad", dateLastViralLoad);
                map.put("lastCd4", lastCd4);
                map.put("lastCd4p", lastCd4p);
                map.put("lastClinicStage", lastClinicStage);
                map.put("lastViralLoad", lastViralLoad);
                map.put("cd4", cd4);
                map.put("cd4p", cd4p);
                map.put("regimentype", regimentype);
                map.put("regimen", regimen);
                map.put("pharmacy", pharmacy);
                map.put("dateDevolved", dateDevolved);
                map.put("originalRegimen", originalRegimen);
                map.put("clinicStage", clinicStage);
                map.put("tbStatus", tbStatus);
                map.put("duration", duration);
                map.put("nextAppointment", nextAppointment);
                map.put("numberOfPatients", numberOfPatients);

                patientList.add(map);
                // add map to list if resultset do not contain status and surname
                //if(!surnameColumn && !currentStatusColumn) patientList.add(map);
                // add map to sortedMaps if resultset contains surname only
                if (surnameColumn && !currentStatusColumn) {
                    sortedMaps.put(surname + otherNames, map);
                }
                // add map to sortedMaps if resultset contains status and surname
                // if current status changes, add sorted maps to list and then reset currenStatusSorting flag
                System.out.println("Pharmacy Column is: " + pharmacyColumn);
                if (!pharmacyColumn) {
                    if (surnameColumn && currentStatusColumn) {
                        if (!currentStatus.equals(currentStatusSorting)) { // if status changes reset sorter for another ststus group
                            //if(!currentStatusSorting.trim().isEmpty()) addSortedMapsToList();
                            currentStatusSorting = currentStatus;
                        }
                        sortedMaps.put(surname + otherNames, map);
                        //System.out.println("Record count after sorter is ..." + count);
                    } else {
                        //if(surnameColumn || currentStatusColumn) addSortedMapsToList();                    
                    }
                } else {
                    if (surnameColumn && pharmacyColumn) {
                        if (!pharmacy.equals(currentPharmacySorting)) { // if status changes reset sorter for another ststus group
                            //if(!currentStatusSorting.trim().isEmpty()) addSortedMapsToList();
                            currentPharmacySorting = pharmacy;
                        }
                        sortedMaps.put(surname + otherNames, map);
                        //System.out.println("Record count after sorter is ..." + count);
                    } else {
                        //if(surnameColumn || currentStatusColumn) addSortedMapsToList();                    
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw sqlException;
        }
        return patientList;
    }

    private boolean contains(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private void addSortedMapsToList() {
        for (Map.Entry<String, Map<String, Object>> entry : sortedMaps.entrySet()) {
            patientList.add(entry.getValue());
        }
        sortedMaps = new TreeMap<>();
    }
}
