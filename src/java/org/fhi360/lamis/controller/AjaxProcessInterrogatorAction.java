/**
 *
 * @author user1
 */
package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;

public class AjaxProcessInterrogatorAction {

    private String status;

    public String processingStatus() {
        HttpSession session = ServletActionContext.getRequest().getSession();
        if (session.getAttribute("processingStatus") != null) {
            status = (String) session.getAttribute("processingStatus");
            System.out.println("Retrive status: " + status);
        }
        return SUCCESS;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
