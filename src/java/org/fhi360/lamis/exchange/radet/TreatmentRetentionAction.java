/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

/**
 *
 * @author user1
 */
import com.opensymphony.xwork2.ActionSupport;

public class TreatmentRetentionAction extends ActionSupport {
    private String status;
    private String fileName;
         
    public String dispatcher() {       
        setFileName(new TreatmentRetentionConverter().convertExcel()); 
        return SUCCESS;        
    }
        

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}
