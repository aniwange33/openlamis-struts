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
import java.text.SimpleDateFormat;
import org.fhi360.lamis.dao.hibernate.ChroniccareDAO;
import org.fhi360.lamis.model.Laboratory;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.dao.hibernate.LaboratoryDAO;
import org.fhi360.lamis.dao.hibernate.PatientDAO;
import org.fhi360.lamis.utility.builder.LaboratoryListBuilder;
import org.fhi360.lamis.dao.jdbc.LaboratoryJDBC;
import org.fhi360.lamis.dao.jdbc.PrescriptionJDBC;
import org.fhi360.lamis.interceptor.updater.PatientLabAttributeUpdater;
import org.fhi360.lamis.model.Chroniccare;
import org.fhi360.lamis.model.dto.PatientObjHandler;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.service.ViralLoadMontiorService;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.StringUtil;
import org.fhi360.lamis.utility.builder.PatientListBuilder;

public class LaboratoryAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long laboratoryId;
    private Long userId;
    private Laboratory laboratory;
    private Set<Laboratory> laboratories = new HashSet<>(0);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> laboratoryList = new ArrayList<>();
    private ArrayList<Map<String, String>> labresultList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        patient = new Patient();
        laboratory = new Laboratory();
        return laboratory;
    }

    // Save new lab to database
    public String saveLaboratory() {

        if (facilityId > 0L) {
            saveRecords();
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    // Update lab in database
    public String updateLaboratory() {
        if (facilityId > 0L) {
            deleteRecords();
            saveRecords();
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    // Delete lab from database
    public String deleteLaboratory() {
        if (facilityId > 0L) {
            deleteRecords();
            new PatientLabAttributeUpdater().lastCd4Date();
            new PatientLabAttributeUpdater().lastViralLoadDate();
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    // Save new records to database
    private String saveRecords() {
        try {
            String labtestId;
            String description;
            String resultab;
            String resultpc;
            String comment;

            session.removeAttribute("CD4");
            session.removeAttribute("CD4p");
            session.removeAttribute("VL");
            // retrieve the labresult list store in session attribute
            labresultList = new LaboratoryListBuilder().retrieveLabresultList();
            for (Map map : labresultList) {
                labtestId = (String) map.get("labtestId");
                description = (String) map.get("description");
                if (request.getParameter("testId").equals(labtestId)) {
                    resultab = request.getParameter("resultab");
                    resultpc = request.getParameter("resultpc");
                    if (labtestId.equals("16")) {
                        comment = request.getParameter("indication");
                    } else {
                        comment = request.getParameter("comment");
                    }
                } else {
                    resultab = (String) map.get("resultab");
                    resultpc = (String) map.get("resultpc");
                    if (labtestId.equals("16")) {
                        comment = request.getParameter("indication");
                    } else {
                        comment = request.getParameter("comment");
                    }
                }
                //remove commas from numeric values if any
                if (StringUtil.isInteger(resultab)) {
                    resultab = StringUtil.stripCommas(resultab);
                }
                if (StringUtil.isInteger(resultpc)) {
                    resultpc = StringUtil.stripCommas(resultpc);
                }

                //If this test is CD4 count save value in session object for PatientLabAttributeUpdater to access and update last CD4
                if (labtestId.equals("1")) {
                    session.setAttribute("CD4", resultab);
                    session.setAttribute("CD4p", resultpc);
                }
                //If this test is Viral Load save value in session object for PatientLabAttributeUpdater to access and update last VL
                if (labtestId.equals("16")) {
                    if (comment.trim().toLowerCase().startsWith("r")) {
                        comment = "Routine Monitoring";
                    }
                    if (comment.trim().toLowerCase().startsWith("t")) {
                        comment = "Targeted Monitoring";
                    }
                    session.setAttribute("VL", resultab);
                }
                patientId = Long.parseLong(request.getParameter("patientId"));
                patient.setPatientId(patientId);
                laboratory.setPatient(patient);
                laboratory.setFacilityId(facilityId);
                laboratory.setLabtestId(Long.parseLong(labtestId));
                laboratory.setResultab(resultab);
                laboratory.setResultpc(resultpc);
                laboratory.setComment(comment);
                laboratory.setUserId(userId);
                laboratory.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                laboratoryId = LaboratoryDAO.save(laboratory);

                //Update prescription
                if (session.getAttribute("fromLabTest") != null && session.getAttribute("fromLabTest").equals("true")) {
                    //System.out.println("It is True");
                    PrescriptionJDBC.changeLabtestPrescriptionStatus(patientId, facilityId, Integer.parseInt(labtestId), Constants.Prescription.PRESCRIBED, Constants.Prescription.PRESCRIBED_DISPENSED);
                }
            }
            //Update prescription
            if (session.getAttribute("fromLabTest") != null && session.getAttribute("fromLabTest").equals("true")) {
                //System.out.println("It is Outside");
                PrescriptionJDBC.changePrescriptionStatusUn(patientId, facilityId, Constants.Prescription.PRESCRIBED, Constants.Prescription.PRESCRIBED_NOT_DISPENSED, "labtest");

            }
            session.removeAttribute("labPrescribedList");
            session.removeAttribute("fromLabTest");
            new ViralLoadMontiorService().updateViralLoadDue(Long.parseLong(request.getParameter("patientId")));
        } catch (Exception E) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete records from database
    private void deleteRecords() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Laboratory laboratory = LaboratoryDAO.find(Long.parseLong(ServletActionContext.getRequest().getParameter("laboratoryId")));
            Patient patient = PatientDAO.find(Long.parseLong(ServletActionContext.getRequest().getParameter("patientId")));
            String entityId = patient.getHospitalNum() + "#" + format.format(laboratory.getDateReported());
            MonitorService.logEntity(entityId, "laboratory", 3);
            DeleteService deleteService = new DeleteService();
            deleteService.deleteLaboratory(laboratory.getFacilityId(), Long.parseLong(request.getParameter("patientId")),
                    laboratory.getDateReported());
            laboratory.setLaboratoryId(Long.parseLong("0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String updateViralLoadDue() {
        try {
            new ViralLoadMontiorService().updateViralLoadDue(Long.parseLong(request.getParameter("patientId")));
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve all lab in database
    public String listLaboratory() {
        try {
            laboratories = LaboratoryDAO.list();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve a laboratory record in database
    public String findLaboratory() {
        try {
            new LaboratoryJDBC().findLaboratory(Long.parseLong(request.getParameter("patientId")), 
                    request.getParameter("dateReported"));
            laboratoryList = new LaboratoryListBuilder().retrieveLaboratoryList();
            findPatient();
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveLaboratoryList() {
        try {
            laboratoryList = new LaboratoryListBuilder().retrieveLaboratoryList();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    // Retrieve a patient in database
    private void findPatient() {
        try {
            patientId = Long.parseLong(request.getParameter("patientId"));
            patient = PatientDAO.find(patientId);
            PatientObjHandler.store(patient);
            new PatientListBuilder().buildPatientList(patient);
        } catch (Exception e) {

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
     * @return the laboratoryId
     */
    public Long getLaboratoryId() {
        return laboratoryId;
    }

    /**
     * @param laboratoryId the laboratoryId to set
     */
    public void setLaboratoryId(Long laboratoryId) {
        this.laboratoryId = laboratoryId;
    }

    /**
     * @return the laboratory
     */
    public Laboratory getLaboratory() {
        return laboratory;
    }

    /**
     * @param laboratory the laboratory to set
     */
    public void setLaboratory(Laboratory laboratory) {
        this.laboratory = laboratory;
    }

    /**
     * @return the laboratories
     */
    public Set<Laboratory> getLaboratories() {
        return laboratories;
    }

    /**
     * @param laboratories the laboratories to set
     */
    public void setLaboratories(Set<Laboratory> laboratories) {
        this.laboratories = laboratories;
    }

    /**
     * @return the laboratoryList
     */
    public ArrayList<Map<String, String>> getLaboratoryList() {
        return laboratoryList;
    }

    /**
     * @param laboratoryList the laboratoryList to set
     */
    public void setLaboratoryList(ArrayList<Map<String, String>> laboratoryList) {
        this.laboratoryList = laboratoryList;
    }

    /**
     * @return the labresultList
     */
    public ArrayList<Map<String, String>> getLabresultList() {
        return labresultList;
    }

    /**
     * @param labresultList the labresultList to set
     */
    public void setLabresultList(ArrayList<Map<String, String>> labresultList) {
        this.labresultList = labresultList;
    }

}
