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
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.hibernate.IndexcontactDAO;
import org.fhi360.lamis.dao.jdbc.IndexcontactJDBC;
import org.fhi360.lamis.model.Hts;
import org.fhi360.lamis.model.Indexcontact;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.service.MonitorService;
import org.fhi360.lamis.utility.builder.IndexcontactListBuilder;

/**
 *
 * @author user10
 */
public class IndexcontactAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long htsId;
    private Hts hts;
    private Long indexcontactId;
    private Long userId;
    private Indexcontact indexcontact;

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> indexcontactList = new ArrayList<>();

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");
    }

    @Override
    public Object getModel() {
        hts = new Hts();
        indexcontact = new Indexcontact();
        return indexcontact;
    }

    // Save new patient to database
    public String saveIndexcontact() {
        try {
            indexcontact.setFacilityId(facilityId);

            indexcontact.setUserId(userId);

            hts.setHtsId(Long.parseLong(request.getParameter("htsId")));
            indexcontact.setHts(hts);
            indexcontact.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            IndexcontactDAO.save(indexcontact);
        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    // Update indexcontact in database
    public String updateIndexcontact() {
        try {

            indexcontact.setFacilityId(facilityId);
            hts.setHtsId(Long.parseLong(request.getParameter("htsId")));
            indexcontact.setHts(hts);
            indexcontact.setIndexcontactId(Long.parseLong(request.getParameter("indexcontactId")));

            indexcontact.setUserId(userId);
            indexcontact.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
            IndexcontactDAO.update(indexcontact);
        } catch (Exception e) {
            System.out.println("EXCEPTION:: " + e.getMessage());
            return ERROR;
        }
        return SUCCESS;
    }

    // Delete indexcontact from database
    public String deleteIndexcontact() {
        try {
            String entityId = ServletActionContext.getRequest().getParameter("indexContactCode");
            MonitorService.logEntity(entityId, "indexcontact", 3);
            DeleteService deleteService = new DeleteService();
            if (facilityId > 0L) {
                deleteService.deleteIndexcontact(facilityId, Long.parseLong(request.getParameter("indexcontactId")));
                return SUCCESS;
            } else {
                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String findIndexcontact() {
        try {
            System.out.println("DORCAS " + request.getParameter("indexcontactId"));
            new IndexcontactJDBC().findIndexcontact(Long.parseLong(request.getParameter("indexcontactId")));
            indexcontactList = new IndexcontactListBuilder().retrieveIndexcontactList();
            System.out.println("ALEXARRAY: " + indexcontactList.toString());
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    public String retrieveIndexcontactList() {
        try {
            indexcontactList = new IndexcontactListBuilder().retrieveIndexcontactList();
            System.out.println("indexcontactList " + indexcontactList.toString());
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * @return the htsId
     */
    public Long getHtsId() {
        return htsId;
    }

    /**
     * @param htsId the htsId to set
     */
    public void setHtsId(Long htsId) {
        this.htsId = htsId;
    }

    /**
     * @return the indexcontactId
     */
    public Long getIndexcontactId() {
        return indexcontactId;
    }

    /**
     * @param indexcontactId the indexcontactId to set
     */
    public void setIndexcontactId(Long indexcontactId) {
        this.indexcontactId = indexcontactId;
    }

    /**
     * @return the indexcontactList
     */
    public ArrayList<Map<String, String>> getIndexcontactList() {
        return indexcontactList;
    }

    /**
     * @param indexcontactList the indexcontactList to set
     */
    public void setIndexcontactList(ArrayList<Map<String, String>> indexcontactList) {
        this.indexcontactList = indexcontactList;
    }

//    public void setHts(Hts hts) {
//        this.hts = hts;
//    }
//
//    public Hts getHts() {
//        return hts;
//    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Hts getHts() {
        return hts;
    }

    public void setHts(Hts hts) {
        this.hts = hts;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Indexcontact getIndexcontact() {
        return indexcontact;
    }

    public void setIndexcontact(Indexcontact indexcontact) {
        this.indexcontact = indexcontact;
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
    
}
