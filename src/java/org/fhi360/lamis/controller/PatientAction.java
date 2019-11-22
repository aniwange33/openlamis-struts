/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.dao.hibernate.PatientDAO;

import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.builder.PatientListBuilder;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.commons.lang.StringUtils;
import org.fhi360.lamis.dao.hibernate.AncDAO;
import org.fhi360.lamis.dao.hibernate.MotherInformationDAO;
import org.fhi360.lamis.interceptor.updater.PatientHospitalNumberUpdater;
import org.fhi360.lamis.model.Anc;
import org.fhi360.lamis.model.Motherinformation;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class PatientAction extends ActionSupport implements ModelDriven, Preparable {

    static final Logger LOG = LoggerFactory.getLogger(PatientAction.class);
    private Long facilityId;
    private Facility facility;
    private Long patientId;
    private Long userId;
    private Patient patient;
    private Set<Patient> patients = new HashSet<>(0);
    private List<Object> biometricStatus;
    private String status;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, String>> patientList = new ArrayList<>();
    private ArrayList<Map<String, String>> messageList = new ArrayList<>();
    private Map<String, String> patientMap = new HashMap<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        facility = new Facility();
        patient = new Patient();
        return patient;
    }

    // Save new patient to database
    public List<Object> getBiometricStatus() {
        return biometricStatus;
    }

    public String biometricStatus() {
        if (session.getAttribute("biometrics") != null) {
            biometricStatus = (List<Object>) session.getAttribute("biometrics");
        }
        session.removeAttribute("biometrics");
        return SUCCESS;
    }

    public String savePatient() {

        try {
            if (facilityId > 0L) {
                try {
                    if (request.getParameter("dateBirth").isEmpty()) {
                        java.util.Date date = DateUtil.parseStringToDate(request.getParameter("dateRegistration"), "MM/dd/yyyy");
                        int number = Integer.parseInt(request.getParameter("age"));
                        String unit = request.getParameter("ageUnit");
                        date = DateUtil.addYearMonthDay(date, -number, unit);
                        patient.setDateBirth(date);
                    }
                    patient.setSendMessage(0);
                    facility.setFacilityId(facilityId);
                    patient.setFacility(facility);
                    patient.setPregnant(0);
                    patient.setBreastfeeding(0);
                    if (patient.getEntryPoint().equals("PMTCT") && patient.getGender().equals("Female")) {
                        switch (request.getParameter("pregnancyStatus")) {
                            case "2":
                                patient.setPregnant(1);
                                patient.setBreastfeeding(0);
                                break;
                            case "3":
                                patient.setPregnant(0);
                                patient.setBreastfeeding(1);
                                break;
                            case "1":
                                patient.setPregnant(0);
                                patient.setBreastfeeding(0);
                                break;
                            default:

                                break;
                        }
                    }
                    patient.setUserId(userId);
                    patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                    scrambleIdentifier();

                    Long id = PatientDAO.save(patient);
                    patient.setPatientId(id);

                    //Store the Mother Information if patient is female
                    if (patient.getEntryPoint().equals("PMTCT") && patient.getGender().equals("Female")) {

                        //Save Mother Information...
                        Motherinformation motherInfo = new Motherinformation();
                        motherInfo.setDateConfirmedHiv(patient.getDateConfirmedHiv());
                        motherInfo.setFacility(facility);
                        motherInfo.setHospitalNum(patient.getHospitalNum());
                        motherInfo.setSurname(patient.getSurname());
                        motherInfo.setOtherNames(patient.getOtherNames());
                        motherInfo.setPatientId(patient.getPatientId());
                        motherInfo.setAddress(patient.getAddress());
                        motherInfo.setPhone(patient.getPhone());
                        motherInfo.setInFacility("Yes");
                        motherInfo.setArtStatus(patient.getCurrentStatus());
                        motherInfo.setTimeHivDiagnosis(request.getParameter("timeHivDiagnosis"));
                        motherInfo.setDateEnrolledPmtct(request.getParameter("dateEnrolledPmtct") != null
                                ? DateUtil.parseStringToDate(request.getParameter("dateEnrolledPmtct"), "MM/dd/yyyy") : null);
                        motherInfo.setUniqueId(patient.getUniqueId());
                        motherInfo.setUserId(userId);
                        motherInfo.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                        MotherInformationDAO.save(motherInfo);

                    }

                    session.setAttribute("patient", patient); //store in session for AfterUpdateInterceptor to access

                    String query = "select count(*) as b_count from biometric b "
                            + "inner join patient p on p.id_uuid = b.patient_id where p.patient_id = ? and b.facility_id = ?";

                    jdbcTemplate.query(query, (rs) -> {
                        while (rs.next()) {
                            Long count = rs.getLong("b_count");
                            LOG.info("Biometric status: {}", count);
                            List<Object> biometrics = new ArrayList<>();
                            biometrics.add(id);
                            biometrics.add(count > 0);
                            session.setAttribute("biometrics", biometrics);
                        }
                        return null;
                    }, id, facilityId);

                    return SUCCESS;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception E) {
            return ERROR;
        }
    }

    // Update patient in database
    public String updatePatient() {
        request.getParameterMap().forEach((k, v) -> {
            System.out.println("Key: " + k + ", Value: " + v[0]);
        });
        try {
            if (facilityId > 0L) {
                if (StringUtils.isNotEmpty(request.getParameter("dateBirth"))) {
                    java.util.Date date = DateUtil.parseStringToDate(request.getParameter("dateRegistration"), "MM/dd/yyyy");
                    int number = Integer.parseInt(request.getParameter("age"));
                    String unit = request.getParameter("ageUnit");
                    date = DateUtil.addYearMonthDay(date, -number, unit);
                    patient.setDateBirth(date);
                }
                facility.setFacilityId(facilityId);
                patient.setFacility(facility);
                patient.setPregnant(0);
                patient.setBreastfeeding(0);
                if (StringUtils.equals(request.getParameter("entryPoint"), "PMTCT") && patient.getGender().equals("Female")) {
                    switch (request.getParameter("pregnancyStatus")) {
                        case "2":
                            patient.setPregnant(1);
                            patient.setBreastfeeding(0);
                            break;
                        case "3":
                            patient.setPregnant(0);
                            patient.setBreastfeeding(1);
                            break;
                        case "1":
                            patient.setPregnant(0);
                            patient.setBreastfeeding(0);
                            break;
                        default:
                            break;
                    }
                }
                patientId = Long.parseLong(request.getParameter("patientId"));
                patient.setPatientId(patientId);
                patient.setUserId(userId);
                patient.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                patient = PatientObjHandler.restore(patient);
                scrambleIdentifier();
                PatientDAO.update(patient);
                session.setAttribute("patient", patient); //store in session for AfterUpdateInterceptor to access

                if (patient.getEntryPoint().equals("PMTCT")) {
                    //Save ANC Information
                    Anc anc = AncDAO.findByPatientId(patientId);
                    if (anc != null) {
                        anc.setAncNum(request.getParameter("ancNum"));
                        anc.setPatient(patient);
                        anc.setFacilityId(facilityId);
                        anc.setDateVisit(request.getParameter("dateEnrolledPmtct") != null
                                ? DateUtil.parseStringToDate(request.getParameter("dateEnrolledPmtct"), "MM/dd/yyyy") : null);
                        anc.setTimeHivDiagnosis(request.getParameter("timeHivDiagnosis"));
                        anc.setSourceReferral(request.getParameter("sourceReferral"));
                        anc.setGestationalAge(request.getParameter("gestationalAge") != null
                                ? Integer.parseInt(request.getParameter("gestationalAge")) : null);
                        anc.setGravida(request.getParameter("gravida") != null
                                ? Integer.parseInt(request.getParameter("gravida")) : null);
                        anc.setParity(request.getParameter("gravida") != null
                                ? Integer.parseInt(request.getParameter("parity")) : null);
                        anc.setDateEnrolledPmtct(request.getParameter("dateEnrolledPmtct") != null
                                ? DateUtil.parseStringToDate(request.getParameter("dateEnrolledPmtct"), "MM/dd/yyyy") : null);
                        anc.setLmp(request.getParameter("lmp") != null
                                ? DateUtil.parseStringToDate(request.getParameter("lmp"), "MM/dd/yyyy") : null);
                        anc.setEdd(request.getParameter("edd") != null
                                ? DateUtil.parseStringToDate(request.getParameter("edd"), "MM/dd/yyyy") : null);
                        anc.setDateConfirmedHiv(request.getParameter("dateConfirmedHiv") != null
                                ? DateUtil.parseStringToDate(request.getParameter("dateConfirmedHiv"), "MM/dd/yyyy") : null);
                        AncDAO.update(anc);

                        Motherinformation motherInfo = MotherInformationDAO.findByPatientId(patient.getPatientId());
                        motherInfo.setDateConfirmedHiv(patient.getDateConfirmedHiv());
                        motherInfo.setDateEnrolledPmtct(patient.getDateEnrolledPmtct());
                        motherInfo.setFacility(facility);
                        motherInfo.setHospitalNum(patient.getHospitalNum());
                        motherInfo.setTimeHivDiagnosis(patient.getTimeHivDiagnosis());
                        motherInfo.setUniqueId(patient.getUniqueId());
                        motherInfo.setAddress(patient.getAddress());
                        motherInfo.setPhone(patient.getPhone());
                        motherInfo.setInFacility("Yes");
//                motherInfo.setDateStarted(patient.getDateStarted());
//                motherInfo.setRegimen(patient.getRegimen());
                        motherInfo.setArtStatus(patient.getCurrentStatus());
                        motherInfo.setSurname(patient.getSurname());
                        motherInfo.setOtherNames(patient.getOtherNames());
                        motherInfo.setPatientId(patient.getPatientId());
                        MotherInformationDAO.update(motherInfo);
                    }
                }

                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
    }

    // Delete patient from database
    public String deletePatient() {
        try {
            String patientId = ServletActionContext.getRequest().getParameter("patientId");
            Patient patient = PatientDAO.find(Long.parseLong(patientId));
            MonitorService.logEntity(patient.getHospitalNum(), "patient", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deletePatient(facilityId, Long.parseLong(patientId));
                session.removeAttribute("patientList");
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    // Retrieve a patient in database
    public String findPatient() {
        try {
            if (!"".equals(request.getParameter("patientId"))
                    && request.getParameter("patientId") != null) {
                patientId = Long.parseLong(request.getParameter("patientId"));
                patient = PatientDAO.find(patientId);
                if (patient != null) {
                    PatientObjHandler.store(patient);
                    new PatientListBuilder().buildPatientList(patient);
                    patientList = new PatientListBuilder().retrievePatientList();
                    session.setAttribute("fromPrescription", "false");
                    session.setAttribute("fromLabTest", "false");

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve a patient with drug prescriptions from database
    public String findPatientWithPrescription() {
        try {
            if (!request.getParameter("patientId").equals("") && request.getParameter("patientId") != null) {
                patientId = Long.parseLong(request.getParameter("patientId"));
                patient = PatientDAO.find(patientId);
                PatientObjHandler.store(patient);
                new PatientListBuilder().buildPatientList(patient);
                patientList = new PatientListBuilder().retrievePatientList();
                session.setAttribute("fromPrescription", "true");
            }
        } catch (Exception ex) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve a patient with drug prescriptions from database
    public String findPatientWithLabTest() {
        try {
            if (!request.getParameter("patientId").equals("") && request.getParameter("patientId") != null) {
                patientId = Long.parseLong(request.getParameter("patientId"));
                patient = PatientDAO.find(patientId);
                PatientObjHandler.store(patient);
                new PatientListBuilder().buildPatientList(patient);
                patientList = new PatientListBuilder().retrievePatientList();
                System.out.println(patientList);

                session.setAttribute("fromLabTest", "true");
            }
        } catch (Exception ex) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findPatientByNumber() {
        try {
            query = "SELECT p.*, (select case when count(b.*) > 0 then true else "
                    + "false end from biometric b inner join patient x on "
                    + "x.id_uuid = b.patient_id  where x.patient_id = p.patient_id "
                    + "and x.facility_id = p.facility_id) as biometric FROM patient p "
                    + "WHERE facility_id = " + facilityId
                    + " AND TRIM(LEADING '0' FROM hospital_num) = '"
                    + PatientNumberNormalizer.unpadNumber(request.getParameter("hospitalNum")) + "'";

            jdbcTemplate.query(query, resultSet -> {
                new PatientListBuilder().buildPatientList(resultSet, "");
                patientList = new PatientListBuilder().retrievePatientList();

                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    public String findPatientByNames() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Scrambler scrambler = new Scrambler();
        String surname = scrambler.scrambleCharacters(request.getParameter("surname"));
        String otherNames = scrambler.scrambleCharacters(request.getParameter("otherNames"));
        String dateBirth = dateFormat.format(DateUtil.parseStringToDate(request.getParameter("dateBirth"), "MM/dd/yyyy"));
        String gender = request.getParameter("gender");
        try {
            query = "SELECT * FROM patient WHERE facility_id = '" + facilityId + "'AND surname = '" + surname + "' AND other_names = '" + otherNames + "' AND gender = '" + gender + "' AND date_birth = '" + dateBirth + "'";
            Patient patient = jdbcTemplate.queryForObject(query, Patient.class);
            Map<String, String> map = new HashMap<>();
            map.put("hospitalNum", patient.getHospitalNum());
            map.put("surname", scrambler.unscrambleCharacters(patient.getSurname()));
            map.put("otherNames", scrambler.unscrambleCharacters(patient.getOtherNames()));
            map.put("gender", patient.getGender());
            map.put("age", Integer.toString(patient.getAge()));
            map.put("ageUnit", patient.getAgeUnit());
            map.put("address", scrambler.unscrambleCharacters(patient.getAddress()));
            map.put("state", patient.getState());
            map.put("lga", patient.getLga());
            messageList.add(map);

        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String updateHospitalNumber() {
        try {
            System.out.println("HOSPITAL NUM " + request.getParameter("newHospitalNum"));
            query = "SELECT patient_id FROM patient WHERE facility_id = ? AND hospital_num = ?";
            Map<String, String> map = new HashMap<>();
            List<Long> patientIds = jdbcTemplate.queryForList(query, Long.class, facilityId, request.getParameter("newHospitalNum"));
            if (!patientIds.isEmpty()) {
                map.put("message", "Not Updated");
            } else {
                new PatientHospitalNumberUpdater().changeHospitalNum(request.getParameter("hospitalNum"), request.getParameter("newHospitalNum"), facilityId);
                String entity = request.getParameter("hospitalNum") + "#" + request.getParameter("newHospitalNum");
                MonitorService.logEntity(entity, "patient", 4);
                map.put("message", "Updated");
            }
            messageList.add(map);

        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrievePatientList() {
        patientList = new PatientListBuilder().retrievePatientList();
        return SUCCESS;
    }

    public void scrambleIdentifier() {

        Scrambler scrambler = new Scrambler();
        patient.setSurname(scrambler.scrambleCharacters(patient.getSurname()));
        patient.setOtherNames(scrambler.scrambleCharacters(patient.getOtherNames()));
        patient.setAddress(scrambler.scrambleCharacters(patient.getAddress()));
        patient.setPhone(scrambler.scrambleNumbers(patient.getPhone()));
        patient.setNextKin(scrambler.scrambleCharacters(patient.getNextKin()));
        patient.setAddressKin(scrambler.scrambleCharacters(patient.getAddressKin()));
        patient.setPhoneKin(scrambler.scrambleNumbers(patient.getPhoneKin()));
    }

    public String clearPatientDetail() {
        ServletActionContext.getRequest().getSession().setAttribute("patientDetail", null);
        return SUCCESS;
    }

    public String retrievePatientDetail() {

        if (ServletActionContext.getRequest().getSession().getAttribute("patientDetail") != null) {
            patientMap = (Map) ServletActionContext.getRequest().getSession().getAttribute("patientDetail");
        }
        return SUCCESS;
    }

    /**
     * @return the patientId
     */
    public Long getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * @return the patients
     */
    public Set<Patient> getPatients() {
        return patients;
    }

    /**
     * @param patients the patients to set
     */
    public void setPatients(Set<Patient> patients) {
        this.patients = patients;
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
     * @return the patientList
     */
    public ArrayList<Map<String, String>> getPatientList() {
        return patientList;
    }

    /**
     * @param patientList the patientList to set
     */
    public void setPatientList(ArrayList<Map<String, String>> patientList) {
        this.patientList = patientList;
    }

    /**
     * @return the messageList
     */
    public ArrayList<Map<String, String>> getMessageList() {
        return messageList;
    }

    /**
     * @param messageList the messageList to set
     */
    public void setMessageList(ArrayList<Map<String, String>> messageList) {
        this.messageList = messageList;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param patients the patients to set
     */
    /**
     * @return the patientMap
     */
    public Map<String, String> getPatientMap() {
        return patientMap;
    }

    /**
     * @param patientMap the patientMap to set
     */
    public void setPatientMap(Map<String, String> patientMap) {
        this.patientMap = patientMap;
    }

}
