/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.converter.DataProfileConverter;
import org.fhi360.lamis.dao.hibernate.ValidatedDAO;
import org.fhi360.lamis.model.Validated;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class ValidationAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Validated validatedData;
    private HttpServletRequest request;
    private HttpSession session;
    private Long userId;
    private String validatorName = "";
    private final ArrayList<String> patientIds = new ArrayList<>();
    private final ArrayList<String> recordIds = new ArrayList<>();
    private final ArrayList<String> createdByIds = new ArrayList<>();
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private int recordType;
    private String fileName;

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        validatedData = new Validated();
        return validatedData;
    }

    public String saveValidation() {

        String temp1 = request.getParameter("patient_ids");
        String temp2 = request.getParameter("records_ids");
        String temp3 = request.getParameter("creators");

        for (String tmp1 : temp1.split(",")) {
            patientIds.add(tmp1);
        }
        for (String tmp2 : temp2.split(",")) {
            recordIds.add(tmp2);
        }
        for (String tmp3 : temp3.split(",")) {
            createdByIds.add(tmp3);
        }

        String returned = ERROR;
        try {
            int entity_id = (Integer) session.getAttribute("entity_id");

            String query = "SELECT fullname FROM user WHERE user_id = '" + userId + "'";
            jdbcTemplate.query(query, (resultSet) -> {
                while (resultSet.next()) {
                    validatorName = resultSet.getString("fullname") != "" ? resultSet.getString("fullname") : "No User";
                }
                return null;
            });
            if (entity_id > 0) {
                if (saveRecords(entity_id) != null) {
                    returned = SUCCESS;
                }
            } else {
                returned = ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            returned = ERROR;
        }

        return returned;
    }

    // Save new records to database
    
    private Long saveRecords(Integer entity_id) {
           Long resultId = null;
        try{
     
        //Loop through the patient records to save the data for each Patient records...
        for (int i = 0; i < patientIds.size(); i++) {

            //Check if the record has been saved before and skip it...
            validatedData.setPatientId(Integer.parseInt(patientIds.get(i)));
            validatedData.setFacilityId(facilityId);
            validatedData.setDateValidated(new java.sql.Timestamp(new java.util.Date().getTime()));
            //Get the Record ID and the Table Validated from the Entity ID
            if (entity_id == 1) {
                validatedData.setTableValidated("patient");
            } else if (entity_id == 2) {
                validatedData.setTableValidated("clinic");
            } else if (entity_id == 3) {
                validatedData.setTableValidated("pharmacy");
            } else if (entity_id == 4) {
                validatedData.setTableValidated("laboratory");
            }
            validatedData.setRecordId(recordIds.get(i));
            validatedData.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            validatedData.setTimeUploaded(null);
            validatedData.setUploaded(null);
            validatedData.setUserId(null);
            validatedData.setValidatedBy(validatorName);
            validatedData.setCreatedBy(createdByIds.get(i));
            //validatedData.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

            //System.out.println("Entity ID is: "+validatedData.getTableValidated()+", Record Id: "+validatedData.getRecordId()+" and patient ID: "+validatedData.getPatientId()+" and createdById : "+validatedData.getCreatedBy()
            //+" Validator Name: "+validatedData.getValidatedBy());
            resultId = ValidatedDAO.save(validatedData);
        }
        }catch(Exception e){
          
        }
        return resultId;
    }

    public String downloadProfile() {
        switch (recordType) {
            case 1:
                setFileName(new DataProfileConverter().convertExcel());
                break;
            default:
                break;
        }
        return SUCCESS;
    }

    // Retrieve a user in database
    public String validation() {
        return SUCCESS;
    }

    /**
     * @return the recordTyp
     */
    public int getRecordType() {
        return recordType;
    }

    /**
     * @param recordTyp the recordTyp to set
     */
    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
