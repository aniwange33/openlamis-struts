/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.dhis;

import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.exchange.dhis.ArtSummaryConverter;
import org.fhi360.lamis.exchange.dhis.LabSummaryConverter;

/**
 *
 * @author user1
 */
public class DhisConverterDispatchAction extends ActionSupport{
    private int recordType;
    private String status;
    private String fileName;
    
    public String dispatcher() {       
        switch(recordType) {
            case 1: setFileName(new ArtSummaryConverter().convertXml()); 
                    break;
            case 2: setFileName(new LabSummaryConverter().convertXml()); 
                    break;
            default: break; 
        }
        return SUCCESS;        
    }
    
    /**
     * @return the recordTyp
     */
    public int getRecordType() {
        return recordType;
    }

    /**
     * @param recordTyp the recordTyp to set
     */
    public void setRecordType(int recordType) {
        this.recordType = recordType;
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
