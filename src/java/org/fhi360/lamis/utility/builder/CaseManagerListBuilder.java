/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility.builder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.Scrambler;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author user10
 */
public class CaseManagerListBuilder {

    private HttpServletRequest request;
    private HttpSession session;
    private Scrambler scrambler;
    private Boolean viewIdentifier;
    private JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private ArrayList<Map<String, String>> caseManagerList = new ArrayList<>();
    private ArrayList<Map<String, String>> caseManagerClientsList = new ArrayList<>();
    private ArrayList<Map<String, String>> clientSearchList = new ArrayList<>();

    public CaseManagerListBuilder() {
        this.request = ServletActionContext.getRequest();
        this.session = ServletActionContext.getRequest().getSession();
        this.scrambler = new Scrambler();
        if (ServletActionContext.getRequest().getSession().getAttribute("viewIdentifier") != null) {
            this.viewIdentifier = (Boolean) session.getAttribute("viewIdentifier");
        }
    }

    public void buildCaseManagerList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String casemanagerId = Long.toString(resultSet.getLong("casemanager_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String fullname = resultSet.getString("fullname");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phone_number");
                String religion = resultSet.getString("religion");
                String sex = resultSet.getString("sex");
                String age = resultSet.getString("age");

                Map<String, String> map = new HashMap<>();
                map.put("casemanagerId", casemanagerId);
                map.put("facilityId", facilityId);
                map.put("fullname", fullname);
                map.put("address", address);
                map.put("phone", phone);
                map.put("religion", religion);
                map.put("sex", sex);
                map.put("age", age);
                caseManagerList.add(map);
            }
            session.setAttribute("caseManagerList", caseManagerList);
            //System.out.println("Got Here!");
            caseManagerList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public String isAssignedToCaseManager(long patientId) {
        String assignedTo[] = {"Not Assigned"};
        String query = "SELECT casemanager_id FROM patient WHERE patient_id =" + patientId;

        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                if (resultSet.getObject("casemanager_id") != null) {
                    Integer caseManagerId = resultSet.getInt("casemanager_id");
                    assignedTo[0] = getCaseManagerAssigned(Long.valueOf(caseManagerId));
                } else {
                    assignedTo[0] = getCaseManagerAssigned(0L);
                }
            }
            return null;
        });
        return assignedTo[0];
    }

    public String getCaseManagerAssigned(long casemanagerId) {
        String casemanagerName[] = {""};
        if (casemanagerId != 0L) {
            String query = "SELECT fullname FROM casemanager WHERE casemanager_id =" + casemanagerId;
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    casemanagerName[0] = resultSet.getString("fullname") != null ? resultSet.getString("fullname") : "Not Assigned";
                }
                return null;
            });
        } else {
            casemanagerName[0] = "Not Assigned";
        }
        return casemanagerName[0];
    }

    public void buildCaseManagerClientsList(ResultSet resultSet) throws SQLException {
        try {
            // loop through resultSet for each row and put into Map
            while (resultSet.next()) {
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String hospitalNum = resultSet.getString("hospital_num");
                String surname = (viewIdentifier) ? scrambler.unscrambleCharacters(resultSet.getString("surname")) : resultSet.getString("surname");
                surname = StringUtils.upperCase(surname);
                String otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(resultSet.getString("other_names")) : resultSet.getString("other_names");
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getString("gender");
                String dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                String address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);
                String dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                String currentViralLoad = resultSet.getString("last_viral_load");
                String currentCd4 = resultSet.getString("last_cd4");
                String currentCd4p = resultSet.getString("last_cd4p");
                String currentStatus = resultSet.getString("current_status");
                //String status = resultSet.getString("status"); 

                Map<String, String> map = new HashMap<>();
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("hospitalNum", hospitalNum);
                map.put("name", surname.concat(" " + otherNames));
                map.put("otherNames", otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("address", address);
                map.put("dateStarted", dateStarted);
                map.put("currentViralLoad", currentViralLoad);
                map.put("currentCd4", currentCd4 != null ? currentCd4 : currentCd4p);
                map.put("currentStatus", currentStatus);
                //map.put("status", status);  
                caseManagerClientsList.add(map);
            }
            session.setAttribute("caseManagerClientsList", caseManagerClientsList);
            caseManagerClientsList = null;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    public void buildCategoryCount(ResultSet resultSet) throws SQLException {
        ArrayList<String> categories = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String count = Integer.toString(resultSet.getInt("count"));

            }
        } catch (Exception ex) {

        }
    }

    public void buildClientSearchList(ResultSet resultSet) throws Exception {
        try {
            while (resultSet.next()) {
                String patientId = Long.toString(resultSet.getLong("patient_id"));
                String facilityId = Long.toString(resultSet.getLong("facility_id"));
                String hospitalNum = resultSet.getString("hospital_num");
                String surname = (viewIdentifier) ? scrambler.unscrambleCharacters(resultSet.getString("surname")) : resultSet.getString("surname");
                surname = StringUtils.upperCase(surname);
                String otherNames = (viewIdentifier) ? scrambler.unscrambleCharacters(resultSet.getString("other_names")) : resultSet.getString("other_names");
                otherNames = StringUtils.capitalize(otherNames);
                String gender = resultSet.getString("gender");
                String dateBirth = resultSet.getObject("date_birth") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_birth"), "MM/dd/yyyy");
                String address = resultSet.getObject("address") == null ? "" : resultSet.getString("address");
                address = (viewIdentifier) ? scrambler.unscrambleCharacters(address) : address;
                address = StringUtils.capitalize(address);
                String dateStarted = resultSet.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "MM/dd/yyyy");
                String currentViralLoad = resultSet.getString("current_viral_load");
                String currentCd4 = resultSet.getString("current_cd4");
                String currentCd4p = resultSet.getString("current_cd4p");
                String currentStatus = resultSet.getString("current_status");
                String status = resultSet.getString("status");

                Map<String, String> map = new HashMap<>();
                map.put("patientId", patientId);
                map.put("facilityId", facilityId);
                map.put("hospitalNum", hospitalNum);
                map.put("name", surname.concat(" " + otherNames));
                map.put("fullName", isAssignedToCaseManager(Long.valueOf(patientId)));
                map.put("otherNames", otherNames);
                map.put("gender", gender);
                map.put("dateBirth", dateBirth);
                map.put("address", address);
                map.put("dateStarted", dateStarted);
                map.put("currentViralLoad", currentViralLoad);
                map.put("currentCd4", currentCd4 != null ? currentCd4 : currentCd4p);
                map.put("currentStatus", currentStatus);
                //if(status == 1)

                map.put("status", status);
                clientSearchList.add(map);
            }
            session.setAttribute("clientSearchList", clientSearchList);
            clientSearchList = null;
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
            throw sqlException;
        }
    }

    public ArrayList<Map<String, String>> retrieveCaseManagerList() {
        // retrieve the casemanager record store in session attribute
        if (session.getAttribute("caseManagerList") != null) {
            caseManagerList = (ArrayList) session.getAttribute("caseManagerList");
        }
        return caseManagerList;
    }

    public ArrayList<Map<String, String>> retrieveCaseManagerClientsList() {
        // retrieve the laboratory record store in session attribute
        if (session.getAttribute("caseManagerClientsList") != null) {
            caseManagerClientsList = (ArrayList) session.getAttribute("caseManagerClientsList");
        }
        return caseManagerClientsList;
    }

    public ArrayList<Map<String, String>> retrieveClientSearchList() {
        // retrieve the laboratory record store in session attribute
        if (session.getAttribute("clientSearchList") != null) {
            clientSearchList = (ArrayList) session.getAttribute("clientSearchList");
        }
        return clientSearchList;
    }

    public void clearCaseMangerList() {
        caseManagerList = retrieveCaseManagerList();
        caseManagerList.clear();
        session.setAttribute("caseManagerList", caseManagerList);
        caseManagerClientsList = retrieveCaseManagerClientsList();
        caseManagerClientsList.clear();
        session.setAttribute("caseManagerClientsList", caseManagerClientsList);
        clientSearchList = retrieveClientSearchList();
        clientSearchList.clear();
        session.setAttribute("clientSearchList", clientSearchList);
    }

    private boolean contains(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
