/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.controller;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

public class SessionObjAttributeAction extends ActionSupport {   
    public String persistFormId() {
        ServletActionContext.getRequest().getSession().setAttribute("formId", ServletActionContext.getRequest().getParameter("formId")); 
        return SUCCESS;
    }    
}
