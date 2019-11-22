/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ModelDriven;
import org.fhi360.lamis.model.Message;
import org.fhi360.lamis.dao.hibernate.MessageDAO;

public class MessageAction extends ActionSupport implements ModelDriven, Preparable {
    private Long messageId;
    private Message message;
    
    private HttpServletRequest request;
    private HttpSession session;

    @Override    
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    @Override
    public Object getModel() {
        message = new Message();
        return message;
    }    
  
    // Save new message to database
    public String saveMessage() {
        try{
        if(session.getAttribute("userGroup") != null) {
            if(((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                MessageDAO.save(message);
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

    // Update Message in database
    public String updateMessage() {
        try{
        if(session.getAttribute("userGroup") != null) {
            if(((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                MessageDAO.update(message);
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
    
    // Delete message from database
    public String deleteMessage() {
        try{
        if(session.getAttribute("userGroup") != null) {
            if(((String) request.getSession().getAttribute("userGroup")).equals("Administrator")) {
                messageId = Long.parseLong(request.getParameter("messageId"));        
                MessageDAO.delete(messageId);
                return SUCCESS;
            }
            else {
                return ERROR;
            }
        }
        else {
           return ERROR; 
        }
        }catch(Exception E){
               return ERROR;  
        }
    }
        
    // Retrieve a message in database
    public String findMessage() {
        messageId = Long.parseLong(request.getParameter("messageId"));        
        message = MessageDAO.find(messageId);          
        return SUCCESS;
    }

    public String retrieveMessage() {
        return SUCCESS;
    }

    /**
     * @return the messageId
     */
    public Long getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }
    

}