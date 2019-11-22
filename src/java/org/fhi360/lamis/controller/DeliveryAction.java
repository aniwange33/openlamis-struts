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
import org.fhi360.lamis.model.Delivery;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Child;
import org.fhi360.lamis.model.Partnerinformation;
import org.fhi360.lamis.dao.hibernate.ChildDAO;
import org.fhi360.lamis.dao.hibernate.DeliveryDAO;
import org.fhi360.lamis.dao.hibernate.MotherInformationDAO;
import org.fhi360.lamis.dao.hibernate.PartnerinformationDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.dao.jdbc.AncJDBC;
import org.fhi360.lamis.model.Motherinformation;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.builder.DeliveryListBuilder;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

public class DeliveryAction extends ActionSupport implements ModelDriven, Preparable {
    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Partnerinformation partnerinformation;
    private Long deliveryId;
    private Long userId;
    private Delivery delivery;
    private Set<Delivery> deliveries = new HashSet<Delivery>(0);
    private Scrambler scrambler;

    private HttpServletRequest request;
    private HttpSession session;
    private String status;
	
    private ArrayList<Map<String, String>> deliveryList = new ArrayList<>();
    private Map<String, String> deliveryDto = new HashMap<>();
    private Map<String, String> partnerInfoDto = new HashMap<>();
    private List<Map<String, String>> childs = new ArrayList<>();
	
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");  
        scrambler = new Scrambler();
        userId = (Long) session.getAttribute("userId");
    }
    
    // Retrieve persistent object and map attribute to form data 
    public Object getModel() {
        patient = new Patient();
        delivery = new Delivery();
	partnerinformation = new Partnerinformation();
        return delivery;        
    }    

    // Save or update delivery in database
    public String saveOrUpdateDelivery() {
        patientId = Long.parseLong(deliveryDto.get("patientId"));
        patient.setPatientId(patientId);
        try{
            delivery.setPatient(patient);
            delivery.setFacilityId(facilityId); 
            delivery.setUserId(userId);
            delivery.setBookingStatus(Integer.parseInt(deliveryDto.get("bookingStatus")));
            delivery.setDateDelivery(DateUtil.parseStringToDate(deliveryDto.get("dateDelivery"), "MM/dd/yyyy"));
            delivery.setRomDeliveryInterval(deliveryDto.get("romDeliveryInterval"));
            delivery.setModeDelivery(deliveryDto.get("modeDelivery"));
            delivery.setEpisiotomy(deliveryDto.get("episiotomy"));
            delivery.setVaginalTear(deliveryDto.get("vaginalTear"));
            delivery.setMaternalOutcome(deliveryDto.get("maternalOutcome"));
            delivery.setTimeHivDiagnosis(deliveryDto.get("timeHivDiagnosis"));
            delivery.setSourceReferral(deliveryDto.get("sourceReferral"));
            if(!deliveryDto.get("gestationalAge").equals(""))
                delivery.setGestationalAge(Integer.parseInt(deliveryDto.get("gestationalAge")));
            else
               delivery.setGestationalAge(0);
            delivery.setHepatitisBStatus(deliveryDto.get("hepatitisBStatus"));
            delivery.setHepatitisCStatus(deliveryDto.get("hepatitisCStatus"));    
            if(!deliveryDto.get("ancId").equals("") && !deliveryDto.get("ancId").isEmpty()) {
                delivery.setAncId(Long.parseLong(deliveryDto.get("ancId")));
            }
            delivery.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

            String deliveryDtoId = deliveryDto.get("deliveryId");	
            if ("".equals(deliveryDtoId)) { // a save operation
                deliveryId = DeliveryDAO.save(delivery);
                System.out.println("Delivery saved");
            }
            else { // an update operation
                deliveryId = Long.parseLong(deliveryDtoId);
                delivery.setDeliveryId(deliveryId);
                DeliveryDAO.update(delivery);
                System.out.println("Delivery updated");
            }
            // save or update the childs
            int count = new AncJDBC().getChildCount(patientId);  //Get the count of babies born to this women in the database
            System.out.println("Child count retrived "+count);
            Motherinformation motherInformation = MotherInformationDAO.findByPatientId(patientId);
            for (Map<String, String> childDto : childs) {
                Child child = new Child();
                child.setPatientId(patientId);
                child.setFacilityId(facilityId); 
                child.setMotherId(motherInformation.getMotherinformationId());
                child.setUserId(userId);
                child.setHospitalNumber(childDto.get("hospitalNumber"));
                if(!childDto.get("arv").contains("--") && !childDto.get("arv").isEmpty()) {
                    child.setArv(childDto.get("arv"));
                }
                if(!childDto.get("hepb").contains("--") && !childDto.get("hepb").isEmpty()) {
                    child.setHepb(childDto.get("hepb"));
                }
                if(!childDto.get("hbv").contains("--") && !childDto.get("hbv").isEmpty()) {
                    System.out.println("Contains");
                    child.setHbv(childDto.get("hbv"));
                }
                if(!childDto.get("dateBirth").equals("") && !childDto.get("dateBirth").isEmpty()) {   
                    child.setDateBirth(DateUtil.parseStringToDate(childDto.get("dateBirth"), "MM/dd/yyyy"));
                }
                if(!childDto.get("gender").contains("--") && !childDto.get("gender").isEmpty()) {
                    child.setGender(childDto.get("gender"));
                }
                if(!childDto.get("surname").equals("") && !childDto.get("surname").isEmpty()) {
                    child.setSurname(scrambler.scrambleCharacters(childDto.get("surname")));
                }
                if(!childDto.get("otherNames").equals("") && !childDto.get("otherNames").isEmpty()) {  
                    child.setOtherNames(scrambler.scrambleCharacters(childDto.get("otherNames")));
                }
                if(!childDto.get("bodyWeight").equals("") && !childDto.get("bodyWeight").isEmpty()) {
                    child.setBodyWeight(Double.parseDouble(childDto.get("bodyWeight")));
                }
                if(!childDto.get("apgarScore").contains("--") && !childDto.get("apgarScore").isEmpty()) {
                    child.setApgarScore(Integer.parseInt(childDto.get("apgarScore")));
                }
                if(!childDto.get("status").contains("--") && !childDto.get("status").isEmpty()) {
                    child.setStatus(childDto.get("status"));
                }
                child.setDeliveryId(deliveryId);
                if(!deliveryDto.get("ancId").equals("") && !deliveryDto.get("ancId").isEmpty()) {
                    child.setAncId(Long.parseLong(deliveryDto.get("ancId")));
                }

                //The reference number uniquely identifies a woman child in the database
                System.out.println("Setting reference no");
                child.setReferenceNum(deliveryDto.get("hospitalNumMother")+"#"+Integer.toString(++count));
                child.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

                String childDtoId = childDto.get("childId");		
                if ("".equals(childDtoId)) { 
                    ChildDAO.save(child);
                }
                else { // an update operation
                    child.setChildId(Long.parseLong(childDtoId));
                    ChildDAO.update(child);
                }
                System.out.println("Saved");

            }
        }catch(Exception ex){
            ex.printStackTrace();
              return ERROR;
        }
        try{
        // update partner information
        saveOrUpdatePartnerinformation();
        }catch(Exception e){
            
        }
        return SUCCESS;
    }
	
    private void saveOrUpdatePartnerinformation() {
        try{
	partnerinformation.setPatient(patient);
        partnerinformation.setFacilityId(facilityId);
        partnerinformation.setPartnerNotification(partnerInfoDto.get("partnerNotification"));
        partnerinformation.setDateVisit(DateUtil.parseStringToDate(deliveryDto.get("dateDelivery"), "MM/dd/yyyy"));
	partnerinformation.setPartnerHivStatus(partnerInfoDto.get("partnerHivStatus"));
        if (!"".equals(partnerInfoDto.get("partnerReferred"))) partnerinformation.setPartnerReferred(partnerInfoDto.get("partnerReferred"));

        partnerinformation.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        partnerinformation.setUserId(userId);

        if ("".equals(partnerInfoDto.get("partnerinformationId"))) { // a save operation
            PartnerinformationDAO.save(partnerinformation);
        }
        else { // an update operation
            partnerinformation.setPartnerinformationId(Long.parseLong(partnerInfoDto.get("partnerinformationId")));
            PartnerinformationDAO.update(partnerinformation);
        }
        }catch(Exception e){
            
        }
    }
	
    // Delete delivery from database
    public String deleteDelivery() {
        try{
         String entityId = ServletActionContext.getRequest().getParameter("hospitalNum") + "#" + ServletActionContext.getRequest().getParameter("dateDelivery");
            MonitorService.logEntity(entityId, "delivery", 3);
        DeleteService deleteService = new DeleteService();
        if(facilityId > 0L) {
            deleteService.deleteDelivery(facilityId, Long.parseLong(request.getParameter("deliveryId")));
            return SUCCESS; 
        }
        else {
            return ERROR;
        }
        }catch(Exception e){
            return ERROR;
        }
    }
	
    public String findDelivery() {
        try{
        new AncJDBC().findDelivery(Long.parseLong(request.getParameter("patientId")), request.getParameter("dateDelivery"));
        deliveryList = new DeliveryListBuilder().retrieveDeliveryList();
        findPatient(); 
        }catch(Exception e){
           return ERROR;
        }
        return SUCCESS;
    }
	
    public String retrieveDeliveryList() {
        deliveryList = new DeliveryListBuilder().retrieveDeliveryList();
        return SUCCESS;
    }
	
    // Retrieve a patient in database
    private void findPatient() {
        try{
        patientId = Long.parseLong(request.getParameter("patientId")); 
        patient = PatientDAO.find(patientId); 
        PatientObjHandler.store(patient);
        new PatientListBuilder().buildPatientList(patient);
        }catch(Exception e){
            
        }
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
     * @return the deliveryId
     */
    public Long getDeliveryId() {
        return deliveryId;
    }

    /**
     * @param deliveryId the deliveryId to set
     */
    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
    
    /**
     * @return the delivery
     */
    public Delivery getDelivery() {
        return delivery;
    }

    /**
     * @param delivery the delivery to set
     */
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    /**
     * @return the deliveries
     */
    public Set<Delivery> getDeliveries() {
        return deliveries;
    }

    /**
     * @param deliveries the deliveries to set
     */
    public void setDeliveries(Set<Delivery> deliveries) {
        this.deliveries = deliveries;
    }
	
	/**
     * @return the deliveryList
     */
    public ArrayList<Map<String, String>> getDeliveryList() {
        return deliveryList;
    }

    /**
     * @param deliveryList the deliveryList to set
     */
    public void setDeliveryList(ArrayList<Map<String, String>> deliveryList) {
        this.deliveryList = deliveryList;
    }
	
    /**
     * @return the deliveryDto
     */
    public Map<String, String> getDeliveryDto() {
        return deliveryDto;
    }

    /**
     * @param deliveryDto the deliveryDto to set
     */
    public void setDeliveryDto(Map<String, String> deliveryDto) {
        this.deliveryDto = deliveryDto;
    }
	
	/**
     * @return the partnerInfoDto
     */
    public Map<String, String> getPartnerInfoDto() {
        return partnerInfoDto;
    }

    /**
     * @param partnerInfoDto the partnerInfoDto to set
     */
    public void setPartnerInfoDto(Map<String, String> partnerInfoDto) {
        this.partnerInfoDto = partnerInfoDto;
    }
	
	/**
     * @return the childs
     */
    public List<Map<String, String>> getChilds() {
        return childs;
    }

    /**
     * @param childs the childs to set
     */
    public void setChilds(List<Map<String, String>> childs) {
        this.childs = childs;
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
	
}