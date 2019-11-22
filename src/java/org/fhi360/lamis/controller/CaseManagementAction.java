/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.CaseManagerDAO;
import org.fhi360.lamis.model.Casemanager;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author DURUANYANWU IFEANYI
 */
public class CaseManagementAction extends ActionSupport implements ModelDriven, Preparable {

    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private Long casemanagerId;
    private Casemanager casemanager;
    private Set<Casemanager> caseManagers = new HashSet<>(0);
    private ArrayList<String> patientIds = new ArrayList<>();

    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private Long facilityId;

    private ArrayList<Map<String, String>> caseManagerList = new ArrayList<>();
    private Map<String, String> caseManagerMap = new TreeMap<>();
    private Map<String, String> caseManagerExceptMap = new TreeMap<>();
    private Map<String, String> caseManagerDetailsMap = new TreeMap<>();
    private Map<String, String> assignmentMap = new TreeMap<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
    }

    @Override
    public Object getModel() {
        casemanager = new Casemanager();
        return casemanager;
    }

    public String saveCaseManager() {
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    casemanager.setFacilityId(facilityId);
                    CaseManagerDAO.save(casemanager);

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }
        //  return ERROR;
    }

    public String updateCaseManager() {
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    CaseManagerDAO.update(casemanager);

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }
        //   return ERROR;
    }

    public String deleteCaseManager() {
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    casemanagerId = Long.parseLong(request.getParameter("casemanagerId"));
                    CaseManagerDAO.delete(casemanagerId);

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }
        //return ERROR;
    }

    public String retrieveCaseManagers() {
        query = "SELECT * FROM casemanager WHERE facility_id = " + facilityId + " ORDER BY fullname ASC";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String casemanagerId = Long.toString(resultSet.getLong("casemanager_id"));
                String fullname = resultSet.getString("fullname");
                caseManagerMap.put(casemanagerId, fullname);
            }
            return null;
        });
        return SUCCESS;
    }

    public String getCaseManagerDetails() {
        Long casemanagerId = Long.parseLong(request.getParameter("casemanagerId"));
        query = "SELECT * FROM casemanager WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId + " ORDER BY fullname ASC";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String caseManagerId = Long.toString(resultSet.getLong("casemanager_id"));
                String fullname = resultSet.getString("fullname");
                String religion = resultSet.getString("religion");
                String sex = resultSet.getString("sex");
                caseManagerDetailsMap.put("casemanagerId", caseManagerId);
                caseManagerDetailsMap.put("fullName", fullname);
                caseManagerDetailsMap.put("religion", religion);
                caseManagerDetailsMap.put("sex", sex);

                //Get the clients assigned to this case manager...
                String internal_query = "SELECT COUNT(patient_id) as count FROM patient WHERE casemanager_id = " + caseManagerId + " AND facility_id= " + facilityId;
                //System.out.println(internal_query);                
                jdbcTemplate.query(internal_query, internalResultSet -> {
                    while (internalResultSet.next()) {
                        Integer clientCount = internalResultSet.getInt("count");
                        caseManagerDetailsMap.put("clientCount", String.valueOf(clientCount));
                    }
                    return null;
                });
            }
            return null;
        });
        return SUCCESS;
    }

    public String assignCaseManager() {

        String patientIds = request.getParameter("patient_ids");
        System.out.println(patientIds);
        Long casemanagerId = Long.parseLong(request.getParameter("casemanagerId"));
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
                    String query = "UPDATE patient SET casemanager_id =" + casemanagerId + ", time_stamp = '" + timestamp + "' WHERE facility_id = " + facilityId + " AND patient_id IN (" + patientIds + ")";
                    transactionTemplate.execute(st -> {
                        System.out.printf("Executing [%s]\n", query);
                        jdbcTemplate.execute(query);
                        return null;
                    });
                    String query_2 = "UPDATE clients SET casemanager_id =" + casemanagerId + " WHERE facility_id = " + facilityId + " AND patient_id IN (" + patientIds + ")";
                    transactionTemplate.execute(st -> {
                        jdbcTemplate.execute(query_2);
                        return null;
                    });
                    session.setAttribute("option", "Assignment");
                    session.removeAttribute("clients");
                    session.setAttribute("clients", patientIds);
                    session.setAttribute("casemanagerId", casemanagerId);
                    assignmentMap.put("response", "success");

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            assignmentMap.put("response", "error");
            return ERROR;
        }
    }

    public String deAssignCaseManager() {
        String patientIds = request.getParameter("patient_ids");
        Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        String query = "UPDATE patient SET casemanager_id = 0, time_stamp = '"
                + timestamp + "' WHERE facility_id = " + facilityId + " AND patient_id IN (" + patientIds + ")";
        String query_2 = "UPDATE clients SET casemanager_id = 0 WHERE facility_id = "
                + facilityId + " AND patient_id IN (" + patientIds + ")";
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    transactionTemplate.execute(st -> {
                        jdbcTemplate.execute(query);
                        return null;
                    });
                    transactionTemplate.execute(st -> {
                        jdbcTemplate.execute(query_2);
                        return null;
                    });
                    session.setAttribute("option", "DeAssignment");
                    session.removeAttribute("clients");
                    session.setAttribute("clients", patientIds);
                    session.setAttribute("casemanagerId", casemanagerId);
                    assignmentMap.put("response", "success");

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            assignmentMap.put("response", "error");//disconnect from database
            return ERROR;
        }
    }

    public String reAssignCaseManager() {

        String patientIds = request.getParameter("patient_ids");
        Long casemanagerId = Long.parseLong(request.getParameter("casemanagerId"));
        try {
            if (session.getAttribute("userGroup") != null) {
                if (((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                    Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
                    String query = "UPDATE patient SET casemanager_id =" + casemanagerId + ", time_stamp = '" + timestamp + "' WHERE facility_id = " + facilityId + " AND patient_id IN (" + patientIds + ")";
                    transactionTemplate.execute(st -> {
                        jdbcTemplate.execute(query);
                        return null;
                    });
                    String query_2 = "UPDATE clients SET casemanager_id =" + casemanagerId + " WHERE facility_id = " + facilityId + " AND patient_id IN (" + patientIds + ")";
                    transactionTemplate.execute(st -> {
                        jdbcTemplate.execute(query_2);
                        return null;
                    });
                    session.setAttribute("option", "ReAssignment");
                    session.removeAttribute("clients");
                    session.setAttribute("clients", patientIds);
                    session.setAttribute("casemanagerId", casemanagerId);
                    assignmentMap.put("response", "success");

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            assignmentMap.put("response", "error"); //disconnect from database
            return ERROR;
        }
    }

    public String retrieveCaseManagersExceptSelected() {
        long casemangerId = Long.parseLong(ServletActionContext.getRequest().getParameter("casemanagerId"));
        query = "SELECT * FROM casemanager WHERE facility_id = " + facilityId + " AND casemanager_id != " + casemangerId + " ORDER BY fullname ASC";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                String casemanagerId = Long.toString(resultSet.getLong("casemanager_id"));
                String fullname = resultSet.getString("fullname");
                caseManagerExceptMap.put(casemanagerId, fullname);
            }
            return null;
        });
        return SUCCESS;
    }

    // Retrieve a CaseManager from the database
    public String findCaseManager() {
        try {
            casemanagerId = Long.parseLong(request.getParameter("casemanagerId"));
            casemanager = CaseManagerDAO.find(casemanagerId);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * @return the caseManagerMap
     */
    public Map<String, String> getCaseManagerMap() {
        return caseManagerMap;
    }

    /**
     * @param caseManagerMap the caseManagerMap to set
     */
    public void setCaseManagerMapMap(Map<String, String> caseManagerMap) {
        this.caseManagerMap = caseManagerMap;
    }

    /**
     * @return the caseManagerList
     */
    public ArrayList<Map<String, String>> getCaseManagerList() {
        return caseManagerList;
    }

    /**
     * @param caseManagerList the caseManagerList to set
     */
    public void setCaseManagerList(ArrayList<Map<String, String>> caseManagerList) {
        this.caseManagerList = caseManagerList;
    }

    public Set<Casemanager> getCaseManagers() {
        return caseManagers;
    }

    public void setCaseManagers(Set<Casemanager> caseManagers) {
        this.caseManagers = caseManagers;
    }

    public Map<String, String> getCaseManagerExceptMap() {
        return caseManagerExceptMap;
    }

    public void setCaseManagerExceptMap(Map<String, String> caseManagerExceptMap) {
        this.caseManagerExceptMap = caseManagerExceptMap;
    }

    public Map<String, String> getCaseManagerDetailsMap() {
        return caseManagerDetailsMap;
    }

    public void setCaseManagerDetailsMap(Map<String, String> caseManagerDetailsMap) {
        this.caseManagerDetailsMap = caseManagerDetailsMap;
    }

    public Map<String, String> getAssignmentMap() {
        return assignmentMap;
    }

    public void setAssignmentMap(Map<String, String> assignmentMap) {
        this.assignmentMap = assignmentMap;
    }

}
