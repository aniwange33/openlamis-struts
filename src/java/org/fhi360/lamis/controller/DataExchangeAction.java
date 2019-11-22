/**
 *
 * @author AALOZIE
 */

package org.fhi360.lamis.controller;

import static com.opensymphony.xwork2.Action.SUCCESS;
import com.opensymphony.xwork2.ActionSupport;
import org.fhi360.lamis.service.ExportService;
import org.fhi360.lamis.service.ImportService;
import org.fhi360.lamis.service.InternetConnectionService;
import com.opensymphony.xwork2.Preparable;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.resource.ApplicationUpdatesWebserviceInvocator;
import org.fhi360.lamis.resource.SyncWebserviceInvocator;
import org.fhi360.lamis.resource.UploadWebserviceInvocator;
import org.fhi360.lamis.service.UploadFolderService;
import org.fhi360.lamis.service.barcode.QRCodeService;
import org.fhi360.lamis.utility.FileMakerConverter;

public class DataExchangeAction extends ActionSupport implements Preparable{
    private HttpSession session;
    private String status;
    private String fileName;
    
    @Override    
    public void prepare() {
        session = ServletActionContext.getRequest().getSession();
    }

    public String exportData() {
        try{
        setFileName(ExportService.buildXml());
        }catch(Exception e){
              return ERROR;
        }
        return SUCCESS;
    }
    
    public String importData() {
        try{
        if(session.getAttribute("fileName") != null) {
            ImportService.processXml();            
            session.removeAttribute("fileName");        
        }
        }catch(Exception e){
              return ERROR;
        }
        return SUCCESS;
    }

    public String importNimrData() {
        try{
        if(session.getAttribute("fileName") != null) {
           new FileMakerConverter().convert();
        }
        }catch(Exception e){
              return ERROR;
        }
        return SUCCESS;
    }

    
    public String uploadData() {
        try{
        setStatus(UploadWebserviceInvocator.invokeUploadService());
        }catch(Exception E){
            
        }
        return SUCCESS;
    }

    public String syncData() {
        try{
        setStatus(SyncWebserviceInvocator.invokeSyncService());
        }catch(Exception e){
        }
        return SUCCESS;
    }
    
    
    public String generateBarcode() {
      try{
        setStatus(new QRCodeService().generate());
      }catch(Exception e){
          
      }
        return SUCCESS;
    }

    public String checkUpdates() {
        try{
        setStatus(ApplicationUpdatesWebserviceInvocator.invokeUpdatesService());
        }catch(Exception e){
            
        }
        return SUCCESS;
    }
    
    public String internetConnection() {
        try{
        setStatus(InternetConnectionService.getInstance().getConnectionStatus());
        }catch(Exception E){
            
        }
        return SUCCESS;
    }

    public String getDisableRecordsAll() {
        try{
        setStatus(UploadFolderService.getDisableRecordsAll());
        }catch(Exception e){
            
        }
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