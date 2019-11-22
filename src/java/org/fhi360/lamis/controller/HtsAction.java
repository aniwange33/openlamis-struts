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
import com.opensymphony.xwork2.util.logging.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.HtsDAO;
import org.fhi360.lamis.dao.jdbc.HtsJDBC;
import org.fhi360.lamis.model.Hts;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;

import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.builder.HtsListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class HtsAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long htsId;
    private Long userId;
    private Hts hts;

    private HttpServletRequest request;
    private HttpSession session;
    private String query;

    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private ArrayList<Map<String, String>> htsList = new ArrayList<>();
    private ArrayList<Map<String, String>> messageList = new ArrayList<>();
    private Map<String, String> htsMap = new HashMap<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
     //    System.out.println("...saving hts"+userId);
    }

    @Override
    public Object getModel() {
        hts = new Hts();
        System.out.println("HTS IN MODEL "+hts);
        return hts;
    }
    // Save new patient to database
    public String saveHts() {
        System.out.println("HTS: " + hts);
        try {
            System.out.println("...saving hts");
            hts.setFacilityId(facilityId);
            hts.setUserId(userId);
            hts.setFacilityName((String) session.getAttribute("facilityName"));
            hts.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            HtsDAO.save(hts);
            session.removeAttribute("assessmentId");
            session.removeAttribute("clientCode");
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Update hts in database
    public String updateHts() {
        System.out.println("...updating hts");
        try {
            hts.setFacilityId(facilityId);
            hts.setFacilityName((String) session.getAttribute("facilityName"));
            hts.setUserId(userId);
            hts.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            HtsDAO.update(hts);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete hts from database
    public String deleteHts() {
        try {
            String entityId = ServletActionContext.getRequest().getParameter("clientCode");
            MonitorService.logEntity(entityId, "hts", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteHts(facilityId, Long.parseLong(request.getParameter("htsId")), Long.parseLong(request.getParameter("assessmentId")));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String findHts() {
        try {
            HtsJDBC htsJDBC = new HtsJDBC();
            htsJDBC.findHts(Long.parseLong(request.getParameter("htsId")));
            htsList = new HtsListBuilder().retrieveHtsList();
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findHtsByNumber() {
        try {
            query = "SELECT * FROM hts WHERE facility_id = " + facilityId + " AND client_code = '" + request.getParameter("clientCode") + "'";
            jdbcTemplate.query(query, resultSet -> {
                new HtsListBuilder().buildHtsList(resultSet);
                htsList = new HtsListBuilder().retrieveHtsList();
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });

        } catch (Exception exception) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String findHtsByNames() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Scrambler scrambler = new Scrambler();
        String surname = scrambler.scrambleCharacters(request.getParameter("surname"));
        String otherNames = scrambler.scrambleCharacters(request.getParameter("otherNames"));
        String dateBirth = dateFormat.format(DateUtil.parseStringToDate(request.getParameter("dateBirth"), "MM/dd/yyyy"));
        String gender = request.getParameter("gender");
        try {
            query = "SELECT * FROM patient WHERE facility_id = ? AND surname = '" + surname + "' AND other_names = '" + otherNames + "' AND gender = '" + gender + "' AND date_birth = '" + dateBirth + "'";
            jdbcTemplate.query(query, resultSet -> {
                Map<String, String> map = new HashMap<>();
                if (resultSet.next()) 
                {
                    map.put("clientCode", resultSet.getString("client_code"));
                    map.put("surname", scrambler.unscrambleCharacters(resultSet.getString("surname")));
                    map.put("otherNames", scrambler.unscrambleCharacters(resultSet.getString("other_names")));
                    map.put("gender", resultSet.getString("gender"));
                    map.put("age", Integer.toString(resultSet.getInt("age")));
                    map.put("ageUnit", resultSet.getString("age_unit"));
                    map.put("address", scrambler.unscrambleCharacters(resultSet.getString("address")));
                    map.put("state", resultSet.getString("state"));
                    map.put("lga", resultSet.getString("lga"));
                }
                messageList.add(map);
                return null;
            }, facilityId);
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String enrollClient() {
        System.out.println(".....hts id: " + request.getParameter("htsId"));

        session.removeAttribute("patientList");
        session.removeAttribute("patient");
        session.setAttribute("htsId", request.getParameter("htsId"));

        return SUCCESS;
    }

    public String htsInfo() {
        try{
        Long htsId = 0L;
        if (session.getAttribute("htsId") != null) {
            htsId = Long.valueOf(session.getAttribute("htsId").toString());
        }
        HtsJDBC htsJDBC = new HtsJDBC();
        htsJDBC.findHts(htsId);
        htsList = new HtsListBuilder().retrieveHtsList();
        session.removeAttribute("htsId");
        }catch(Exception e){
             return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveHtsList() {
        try{
        htsList = new HtsListBuilder().retrieveHtsList();
        }catch(Exception e){
             return ERROR;
        }
        return SUCCESS;
    }

    /**
     * @return the htsList
     */
    public ArrayList<Map<String, String>> getHtsList() {
        return htsList;
    }

    /**
     * @param htsList the htsList to set
     */
    public void setHtsList(ArrayList<Map<String, String>> htsList) {
        this.htsList = htsList;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Long getHtsId() {
        return htsId;
    }

    public void setHtsId(Long htsId) {
        this.htsId = htsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Hts getHts() {
        return hts;
    }

    public void setHts(Hts hts) {
        this.hts = hts;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<Map<String, String>> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<Map<String, String>> messageList) {
        this.messageList = messageList;
    }

    public Map<String, String> getHtsMap() {
        return htsMap;
    }

    public void setHtsMap(Map<String, String> htsMap) {
        this.htsMap = htsMap;
    }

    public static Logger getLOG() {
        return LOG;
    }

    public static void setLOG(Logger LOG) {
        ActionSupport.LOG = LOG;
    }
    
    

}
