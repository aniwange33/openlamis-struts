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
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.AssessmentDao;
import org.fhi360.lamis.dao.jdbc.AssessmentJDBC;
import org.fhi360.lamis.model.Assessment;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.beans.ContextProvider;

import org.fhi360.lamis.utility.builder.AssessmentListBuilder;
import org.fhi360.lamis.utility.builder.HtsListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class AssessmentAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Assessment assessment;
    private Long userId;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, String>> assessmentList = new ArrayList<>();

    private Map<String, String> assessmentMap = new HashMap<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");

    }

    @Override
    public Object getModel() {
        assessment = new Assessment();

        return assessment;
    }

    public String saveAssessment() {
        try {

            assessment.setFacilityId(facilityId);
            assessment.setUserId(userId);
            assessment.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));

            if (request.getParameterMap().containsKey("sti1")) {
                assessment.setSti1(1);
            } else {
                assessment.setSti1(0);
            }

            if (request.getParameterMap().containsKey("sti2")) {
                assessment.setSti2(1);
            } else {
                assessment.setSti2(0);
            }
            if (request.getParameterMap().containsKey("sti3")) {
                assessment.setSti3(1);
            } else {
                assessment.setSti3(0);
            }
            if (request.getParameterMap().containsKey("sti4")) {
                assessment.setSti4(1);
            } else {
                assessment.setSti4(0);
            }
            if (request.getParameterMap().containsKey("sti5")) {
                assessment.setSti5(1);
            } else {
                assessment.setSti5(0);
            }
            if (request.getParameterMap().containsKey("sti6")) {
                assessment.setSti6(1);
            } else {
                assessment.setSti6(0);
            }
            if (request.getParameterMap().containsKey("sti7")) {
                assessment.setSti7(1);
            } else {
                assessment.setSti7(0);
            }
            if (request.getParameterMap().containsKey("sti8")) {
                assessment.setSti8(1);
            } else {
                assessment.setSti8(0);
            }

            Long assessmentId = AssessmentDao.save(assessment);
            System.out.println("... id: " + assessmentId);
            System.out.println("... code: " + assessment.getClientCode());

            session.setAttribute("clientCode", assessment.getClientCode());
            session.setAttribute("assessmentId", assessmentId);

            new HtsListBuilder().clearHtsList();
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    public String updateAssessment() {
        try {
            assessment.setFacilityId(facilityId);
            assessment.setAssessmentId(Long.parseLong(request.getParameter("assessmentId")));
            assessment.setUserId(userId);
            assessment.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            AssessmentDao.update(assessment);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String deleteAssessment() {
        try {
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                //deleteService.deleteAssessment(facilityId, Long.parseLong(request.getParameter("assessmentId")));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String findAssessment() {
        try {
            new AssessmentJDBC().findAssessment(Long.parseLong(request.getParameter("assessmentId")));
            assessmentList = new AssessmentListBuilder().retrieveAssessmentList();
            System.out.println("..........." + assessmentList);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findAssessmentByNumber() {
        try {
            query = "SELECT * FROM assessment WHERE facility_id = " + facilityId + " AND client_code = '" + request.getParameter("clientCode") + "'";

            jdbcTemplate.query(query, resultSet -> {
                new AssessmentListBuilder().buildAssessmentList(resultSet);
                assessmentList = new HtsListBuilder().retrieveHtsList();
                return null;
            });
        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveHtsList() {
        assessmentList = new AssessmentListBuilder().retrieveAssessmentList();
        return SUCCESS;
    }

    /**
     * @return the assessmentList
     */
    public ArrayList<Map<String, String>> getAssessmentList() {
        return assessmentList;
    }

    /**
     * @param assessmentList the assessmentList to set
     */
    public void setAssessmentList(ArrayList<Map<String, String>> assessmentList) {
        this.assessmentList = assessmentList;
    }

    /**
     * @return the assessmentMap
     */
    public Map<String, String> getAssessmentMap() {
        return assessmentMap;
    }

    /**
     * @param assessmentMap the assessmentMap to set
     */
    public void setAssessmentMap(Map<String, String> assessmentMap) {
        this.assessmentMap = assessmentMap;
    }

}
