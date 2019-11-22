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
import org.fhi360.lamis.model.Childfollowup;
import org.fhi360.lamis.model.Child;
import org.fhi360.lamis.dao.hibernate.ChildDAO;
import org.fhi360.lamis.dao.hibernate.ChildfollowupDAO;
import org.fhi360.lamis.dao.jdbc.AncJDBC;
import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.utility.builder.ChildfollowupListBuilder;
import org.fhi360.lamis.utility.builder.ChildListBuilder;

public class ChildfollowupAction extends ActionSupport implements ModelDriven, Preparable {
    private Long facilityId;
    private Long childId;
    private Child child;
    private Long childfollowupId;
    private Long userId;
    private Childfollowup childfollowup;
    private Set<Childfollowup> childfollowups = new HashSet<Childfollowup>(0);

    private HttpServletRequest request;
    private HttpSession session;
	
    private ArrayList<Map<String, String>> childfollowupList = new ArrayList<Map<String, String>>();
    
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");                        
        userId = (Long) session.getAttribute("userId");
    }
    
    // Retrieve persistent object and map attribute to form data 
    public Object getModel() {
        child = new Child();
        childfollowup = new Childfollowup();
        return childfollowup;        
    }    


    // Save new Childfollowup to database
    public String saveChildfollowup() {
        try{
        child.setChildId(Long.parseLong(request.getParameter("childId")));        
        childfollowup.setChild(child);
        childfollowup.setFacilityId(facilityId); 
        childfollowup.setUserId(userId);
        childfollowup.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        ChildfollowupDAO.save(childfollowup); 
        }catch(Exception e){
              return ERROR;
        }
        return SUCCESS;
    }
	
	// Update childfollowup in database
    public String updateChildfollowup() {
        try{
        child.setChildId(Long.parseLong(request.getParameter("childId")));        
        childfollowup.setChild(child);
        childfollowup.setFacilityId(facilityId);        
        childfollowup.setChildfollowupId(Long.parseLong(request.getParameter("childfollowupId")));
        childfollowup.setUserId(userId);
        childfollowup.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
        ChildfollowupDAO.update(childfollowup);
        }catch(Exception e){
              return ERROR;
        }
        return SUCCESS;
    }
    
    // Delete childfollowup from database
    public String deleteChildfollowup() {
        try{
        DeleteService deleteService = new DeleteService();
        if(facilityId > 0L) {
            deleteService.deleteChildfollowup(facilityId, Long.parseLong(request.getParameter("childfollowupId")));
            return SUCCESS; 
        }
        else {
            return ERROR;
        }
        }catch(Exception e){
              return ERROR;
        }
    }
	
    
    public String findChildfollowup() {
        try{
        new AncJDBC().findChildfollowup(Long.parseLong(request.getParameter("childId")), request.getParameter("dateVisit"));
        childfollowupList = new ChildfollowupListBuilder().retrieveChildfollowupList();
        findChild();
        }catch(Exception e){
            return ERROR;
        }
        
        return SUCCESS;
    }
	
    public String retrieveChildfollowupList() {
        childfollowupList = new ChildfollowupListBuilder().retrieveChildfollowupList();
        return SUCCESS;
    }
    
	// Retrieve a child in database
    private void findChild() {
        
        childId = Long.parseLong(request.getParameter("childId")); 
        child = ChildDAO.find(childId); 
        new ChildListBuilder().buildChildList(child);
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
     * @return the ChildId
     */
    public Long getChildId() {
        return childId;
    }

    /**
     * @param childId the childId to set
     */
    public void setChildId(Long childId) {
        this.childId = childId;
    }

    /**
     * @return the Child
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
    
    /**
     * @return the ChildfollowupId
     */
    public Long getChildfollowupId() {
        return childfollowupId;
    }

    /**
     * @param ChildfollowupId the ChildfollowupId to set
     */
    public void setChildfollowupId(Long childfollowupId) {
        this.childfollowupId = childfollowupId;
    }
    
    /**
     * @return the childfollowup
     */
    public Childfollowup getChildfollowup() {
        return childfollowup;
    }

    /**
     * @param childfollowup the childfollowup to set
     */
    public void setChildfollowup(Childfollowup childfollowup) {
        this.childfollowup = childfollowup;
    }

    /**
     * @return the childfollowups
     */
    public Set<Childfollowup> getChildfollowups() {
        return childfollowups;
    }

    /**
     * @param childfollowups the childfollowups to set
     */
    public void setChildfollowups(Set<Childfollowup> childfollowups) {
        this.childfollowups = childfollowups;
    }
	
	/**
     * @return the childfollowupList
     */
    public ArrayList<Map<String, String>> getChildfollowupList() {
        return childfollowupList;
    }

    /**
     * @param childfollowupList the childfollowupList to set
     */
    public void setChildfollowupList(ArrayList<Map<String, String>> childfollowupList) {
        this.childfollowupList = childfollowupList;
    }
}