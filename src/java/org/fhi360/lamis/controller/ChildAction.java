/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Child;
//import org.fhi360.lamis.report.ClinicQuery;
import org.fhi360.lamis.dao.hibernate.ChildDAO;
import org.fhi360.lamis.dao.hibernate.MotherInformationDAO;
import org.fhi360.lamis.model.Facility;
import org.fhi360.lamis.model.Motherinformation;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;

import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.builder.ChildListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class ChildAction extends ActionSupport implements ModelDriven, Preparable {

    private static final Logger logger = Logger.getLogger(ChildAction.class.getName());

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Facility facility;
    private Long childId;
    private String motherHospitalNumber;
    private Child child;
    private Long userId;
    private Scrambler scrambler;
    private Long motherId;
    private List<Map<String, String>> children = new ArrayList<>();
    Integer childCount = 0;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> childList = new ArrayList<>();
    private Map<String, String> childDTO = new HashMap<>();
    private Map<String, String> motherDTO = new HashMap<>();

    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
        scrambler = new Scrambler();
    }

    // Retrieve persistent object and map attribute to form data 
    public Object getModel() {
        patient = new Patient();
        facility = new Facility();
        child = new Child();
        return child;
    }

    // Save new child to database
    public String saveChild() {
        try {
//            patient.setPatientId(Long.parseLong(request.getParameter("patientId")));   
            facility.setFacilityId(facilityId);
            //Set the mother Information
            if (motherDTO.get("willing").equals("No") || motherDTO.get("willing").equals("")) {
                System.out.println(motherDTO);
                Motherinformation motherInformation = new Motherinformation();
                motherInformation.setFacility(facility);
                motherInformation.setDateConfirmedHiv(motherDTO.get("dateConfirmedHiv") != null
                        ? DateUtil.parseStringToDate(motherDTO.get("dateConfirmedHiv"), "MM/dd/yyyy") : null);
                motherInformation.setUniqueId(motherDTO.get("motherUniqueId"));
                motherInformation.setSurname(scrambler.scrambleCharacters(motherDTO.get("motherSurname")));
                motherInformation.setOtherNames(scrambler.scrambleCharacters(motherDTO.get("motherOtherNames")));
                motherInformation.setAddress(scrambler.scrambleCharacters(motherDTO.get("address")));
                motherInformation.setPhone(scrambler.scrambleCharacters(motherDTO.get("phone")));
                motherInformation.setInFacility(motherDTO.get("inFacility"));
                motherInformation.setHospitalNum(motherDTO.get("motherUniqueId"));
                motherInformation.setDateStarted(motherDTO.get("dateStarted") != null
                        ? DateUtil.parseStringToDate(motherDTO.get("dateStarted"), "MM/dd/yyyy") : null);
                motherInformation.setUserId(userId);
                motherInformation.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                motherId = MotherInformationDAO.save(motherInformation);

            } else {
                patientId = Long.parseLong(motherDTO.get("patientId"));
                Motherinformation motherInformation = MotherInformationDAO.findByPatientId(patientId);
                motherId = motherInformation.getMotherinformationId();
                motherHospitalNumber = motherInformation.getHospitalNum();
            }

            for (Map<String, String> childDto : children) {
                Child aChild = new Child();
                System.out.println("Facility ID: " + facilityId);
                aChild.setFacilityId(facilityId);
                System.out.println("Mother ID: " + motherId);
                aChild.setMotherId(motherId);
                //System.out.println("Facility ID: "+facilityId);
                //aChild.setUserId(userId);
                aChild.setHospitalNumber(childDto.get("hospitalNumber"));
                aChild.setDateBirth(DateUtil.parseStringToDate(childDto.get("dateBirth"), "MM/dd/yyyy"));
                aChild.setGender(childDto.get("gender"));
                aChild.setSurname(scrambler.scrambleCharacters(childDto.get("surname")));
                aChild.setOtherNames(scrambler.scrambleCharacters(childDto.get("otherNames")));
                if (!childDto.get("bodyWeight").equals("") && !childDto.get("bodyWeight").isEmpty()) {
                    aChild.setBodyWeight(Double.parseDouble(childDto.get("bodyWeight")));
                }

                aChild.setRegistrationStatus(childDto.get("registrationStatus"));
                //The reference number uniquely identifies a woman child in the database
                System.out.println("Setting reference no");
                if (motherDTO.get("willing").equals("Yes")) {
                    aChild.setReferenceNum(motherHospitalNumber + "#" + Integer.toString(++childCount));
                }
                aChild.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                aChild.setUserId(userId);
                String childDtoId = childDto.get("childId");
                if ("".equals(childDtoId)) {
                    ChildDAO.save(aChild);
                } else { // an update operation
                    aChild.setChildId(Long.parseLong(childDtoId));
                    ChildDAO.update(aChild);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
    }

    // Update child in database
    public String updateChild() {
        child.setPatientId(Long.parseLong(request.getParameter("patientId")));
//        child.setPatientId(patient);
        child.setFacilityId(facilityId);
        child.setChildId(Long.parseLong(request.getParameter("childId")));
        child.setUserId(userId);
        child.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        ChildDAO.update(child);
        return SUCCESS;
    }

    // Delete child from database
    public String deleteChild() {
        try {
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteChild(facilityId, Long.parseLong(request.getParameter("childId")));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    // Retrieve a child in database
    public String findChild() {
        try {
            childId = Long.parseLong(request.getParameter("childId"));
            child = ChildDAO.find(childId);
            System.out.println(child);
            logger.log(Level.SEVERE, child.toString());
            new ChildListBuilder().buildChildList(child);
            childList = new ChildListBuilder().retrieveChildList();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findChildByNumber() {
        try {

            String query = "SELECT child.*, motherinformation.surname AS surname_mother, motherinformation.other_names as other_names_mother, motherinformation.in_facility FROM child JOIN motherinformation ON child.mother_id = motherinformation.motherinformation_id WHERE child.facility_id = " + facilityId + " AND TRIM(LEADING '0' FROM child.hospital_number) = '" + PatientNumberNormalizer.unpadNumber(request.getParameter("hospitalNum")) + "'";
            jdbcTemplate.query(query, resultSet -> {
                new ChildListBuilder().buildChildListSorted(resultSet);
                childList = new ChildListBuilder().retrieveChildList();
                return null;
            });

        } catch (Exception exception) {
            exception.printStackTrace(); //disconnect from database
//            jdbcUtil.disconnectFromDatabase();
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveChildList() {
        childList = new ChildListBuilder().retrieveChildList();
        return SUCCESS;
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
     * @return the PatientId
     */
    public Long getPateintId() {
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
     * @return the ChildId
     */
    public Long getChildId() {
        return childId;
    }

    /**
     * @param ChildId the ChildId to set
     */
    public void setChildId(Long childId) {
        this.childId = childId;
    }

    /**
     * @return the child
     */
    public Child getChild() {
        return child;
    }

    /**
     * @param child the child to set
     */
    public void setChild(Child child) {
        this.child = child;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getMotherHospitalNumber() {
        return motherHospitalNumber;
    }

    public void setMotherHospitalNumber(String motherHospitalNumber) {
        this.motherHospitalNumber = motherHospitalNumber;
    }

    public Long getMotherId() {
        return motherId;
    }

    public void setMotherId(Long motherId) {
        this.motherId = motherId;
    }

    public List<Map<String, String>> getChildren() {
        return children;
    }

    public void setChildren(List<Map<String, String>> children) {
        this.children = children;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    /**
     * @return the childList
     */
    public ArrayList<Map<String, String>> getChildList() {
        return childList;
    }

    /**
     * @param childList the childList to set
     */
    public void setChildList(ArrayList<Map<String, String>> childList) {
        this.childList = childList;
    }

    public Map<String, String> getChildDTO() {
        return childDTO;
    }

    public void setChildDTO(Map<String, String> childDTO) {
        this.childDTO = childDTO;
    }

    public Map<String, String> getMotherDTO() {
        return motherDTO;
    }

    public void setMotherDTO(Map<String, String> motherDTO) {
        this.motherDTO = motherDTO;
    }

}
