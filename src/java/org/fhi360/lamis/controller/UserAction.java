/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller;

import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.User;
import org.fhi360.lamis.dao.hibernate.UserDAO;
import org.fhi360.lamis.utility.JDBCUtil;

public class UserAction extends ActionSupport implements ModelDriven, Preparable {
    private Long facilityId;
    private Long userId;
    private User user;
    private Set<User> users = new HashSet<User>(0);
    
    private HttpServletRequest request;
    private HttpSession session;
    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @Override    
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
    }

    @Override
    public Object getModel() {
        user = new User();
        return user;
    }    
  
    // Save new user to database
    public String saveUser() {
        try{
        if(session.getAttribute("userGroup") != null) {
            if(((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                user.setFacilityId(facilityId);
                if(request.getParameterMap().containsKey("viewIdentifier")) {
                    user.setViewIdentifier(1);
                } 
                else {
                    user.setViewIdentifier(0);            
                }
		if(request.getParameter("stateIds").trim().isEmpty()) {
                    user.setStateIds("0");
                } 
                user.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                UserDAO.save(user);
                return SUCCESS;
            }
            else {
                return ERROR;
            }
        }
        else {
           return ERROR; 
        }
        }catch(Exception e){
            return ERROR;
        }
    }

    // Update user in database
    public String updateUser() {
        try{
        if(session.getAttribute("userGroup") != null) {
            if(((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                user.setFacilityId(facilityId);
                if(request.getParameterMap().containsKey("viewIdentifier")) {
                    user.setViewIdentifier(1);
                } 
                else {
                    user.setViewIdentifier(0);            
                }
		if(request.getParameter("stateIds").trim().isEmpty()) {
                    user.setStateIds("0");
                } 
                user.setUserId(Long.parseLong(request.getParameter("userId"))); 
                user.setTimeStamp(new java.sql.Timestamp(new java.util.Date().getTime()));
                UserDAO.update(user);
                return SUCCESS;
            }
            else {
                return ERROR;
            }
        }
        else {
           return ERROR; 
        }
        }catch(Exception e){
            return ERROR;   
        }
    }
    
    // Delete user from database
    public String deleteUser() {
        if(session.getAttribute("userGroup") != null) {
            if(((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                userId = Long.parseLong(request.getParameter("userId"));        
                UserDAO.delete(userId);
                return SUCCESS;
            }
            else {
                return ERROR;
            }
        }
        else {
           return ERROR; 
        }
    }
    
    // Retrieve a user in database
    public String findUser() {
        userId = Long.parseLong(request.getParameter("userId"));        
        user = UserDAO.find(userId);          
        return SUCCESS;
    }

   public String login() {
        try {
            query = "SELECT user.user_id, user.username, user.fullname, user.password, user.user_group, user.view_identifier, user.state_ids, user.facility_id FROM user WHERE user.username = ? AND user.password = ?";
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.setString(1, request.getParameter("username"));
            preparedStatement.setString(2, request.getParameter("password"));
            resultSet = preparedStatement.executeQuery();

            //check number of rows returned from ResultSet
            if (resultSet.next()) {
                //set current user id and last facility accessed as a session-scoped attribute
                session.setAttribute("userId", resultSet.getLong("user_id"));                
                session.setAttribute("username", resultSet.getString("username")); 
                session.setAttribute("userGroup", resultSet.getString("user_group")); 
                session.setAttribute("facilityId", resultSet.getLong("facility_id"));
                session.setAttribute("fullname", resultSet.getString("fullname") == null ? "Guest" : resultSet.getString("fullname"));
                Boolean viewIdentifier = (resultSet.getInt("view_identifier") == 1)? true : false;
                session.setAttribute("viewIdentifier", viewIdentifier);                
		session.setAttribute("stateIds", resultSet.getLong("state_ids")); 
                													 
                if(resultSet.getLong("facility_id") == 0L) {
                    session.setAttribute("facilityName", "No Active");                                         
                }
                else {
                    query = "SELECT name FROM facility WHERE facility_id = ?";
                    preparedStatement = jdbcUtil.getStatement(query);
                    preparedStatement.setLong(1, resultSet.getLong("facility_id"));
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        session.setAttribute("facilityName", resultSet.getString("name"));                     
                    } 
                    else {
                        session.setAttribute("facilityName", "No Active");   
                    }
                }
                return SUCCESS;
            } 
            else {
                return INPUT;
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
            return ERROR;
        }
    }

    
    public String logout() {
        if(request.getSession() != null) {
            session.invalidate();
        }
        return SUCCESS;
    }
    
    public String verifyGroup() {
        String userGroup = "";
        if(request.getSession().getAttribute("userGroup") != null) {
            userGroup = (String) request.getSession().getAttribute("userGroup");                        
            if(userGroup.equals("Administrator")) {
                session.setAttribute("facilityId", Long.parseLong(request.getParameter("facilityId"))); 
                session.setAttribute("facilityName", request.getParameter("facilityName")); 
                System.out.println("Verify in UserDAO.....");
                UserDAO.updateFacilityId((Long) request.getSession().getAttribute("userId"), Long.parseLong(request.getParameter("facilityId")));
                return SUCCESS;
            }
        }
        return ERROR;        
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
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the users
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }
}