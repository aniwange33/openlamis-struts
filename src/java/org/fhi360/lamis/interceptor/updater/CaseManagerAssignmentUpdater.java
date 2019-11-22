/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.interceptor.updater;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.PatientCaseManagerDAO;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.model.Patientcasemanager;

/**
 *
 * @author user10
 */
public class CaseManagerAssignmentUpdater {

    private long casemanagerId;
    private long facility_id;
    private HttpServletRequest request;
    private Patientcasemanager patientCaseManager;
    private List<String> patientIds = new ArrayList<>();

    //This method is called from the AfterUpdateInterceptor when a new patient is saved or modified
    public void logAssignmentChange() {
        try {
            System.out.println("Got Here in save...");
            request = ServletActionContext.getRequest();

            String option = (String) request.getSession().getAttribute("option");
            casemanagerId = (Long) request.getSession().getAttribute("casemanagerId");
            facility_id = (Long) request.getSession().getAttribute("facilityId");

            //System.out.println("Status Change Here!"+option+" and "+casemanagerId+" and "+facility_id);
            System.out.println("Got Here in save..." + option);
            if (option != null) {
                if (option.equals("Assignment")) {
                    saveAssignment("Assignment");
                } else if (option.equals("DeAssignment")) {
                    saveAssignment("DeAssignment");
                } else if (option.equals("ReAssignment")) {
                    saveAssignment("ReAssignment");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveAssignment(String option) {
        System.out.println("Got Here in save action..." + option);
        String temp1 = (String) request.getSession().getAttribute("clients");
        for (String tmp1 : temp1.split(",")) {
            patientIds.add(tmp1);
        }
        try {
            for (int i = 0; i < patientIds.size(); i++) {

                patientCaseManager = new Patientcasemanager();

                Patient patient = new Patient();
                patient.setPatientId(Long.valueOf(patientIds.get(i)));

                //Set the variables for the case manager...
                patientCaseManager.setFacilityId(facility_id);
                if (!option.equals("DeAssignment")) {
                    patientCaseManager.setCasemanagerId(casemanagerId);
                } else {
                    patientCaseManager.setCasemanagerId(0);
                }
                patientCaseManager.setAction(option);
                patientCaseManager.setDateAssigned(new Date());
                patientCaseManager.setPatient(patient);
                patientCaseManager.setTimeStamp(new Timestamp(System.currentTimeMillis()));

                PatientCaseManagerDAO.save(patientCaseManager);
                request.getSession().removeAttribute("option");
                request.getSession().removeAttribute("clients");
                request.getSession().removeAttribute("casemanagerId");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
