/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.ndr;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.UploadFolderService;
import org.fhi360.lamis.utility.FileUtil;


public class NdrAction_1 extends ActionSupport {
    private String status;
    private String fileName;

    public String dispatcher() {
        NdrConverter converter = new NdrConverter();
        FileUtil fileUtil = new FileUtil();
        UploadFolderService uploadFolderService = new UploadFolderService();

        String messageOption = ServletActionContext.getRequest().getParameter("messageOption");
        if(messageOption.equals("1")) {
            String stateId = ServletActionContext.getRequest().getParameter("stateId");
            String facilityId = ServletActionContext.getRequest().getParameter("facilityId");

            if(facilityId.trim().isEmpty() && stateId.trim().isEmpty()) {
                System.out.println("Conversion begin all facility...........");
                String query = "SELECT DISTINCT facility_id FROM patient WHERE facility_id IN (SELECT facility_id FROM facility WHERE active = 1 AND datim_id IS NOT NULL AND datim_id != '') ORDER BY facility_id";
                converter.buildMessage(query, true, true);
            }
            else {
                if(!facilityId.trim().isEmpty()) {
                    String folder = ServletActionContext.getServletContext().getInitParameter("contextPath")+ "transfer/temp/" + facilityId + "/";
                    fileUtil.makeDir(folder);
                    if(uploadFolderService.getUploadFolderStatus(folder).equalsIgnoreCase("unlocked")) {
                        fileUtil.deleteFileWithExtension(folder, ".xml");
                        fileUtil.deleteFileWithExtension(folder, ".zip");
                        uploadFolderService.lockUploadFolder(folder);
                        //setFileName(converter.buildMessage(Long.parseLong(facilityId), true));
                        uploadFolderService.unlockUploadFolder(folder);
                    }
                }
                else {
                    if(!stateId.trim().isEmpty()) {
                        String query = "SELECT DISTINCT facility_id FROM patient WHERE facility_id IN (SELECT facility_id FROM facility WHERE state_id = " + stateId + " AND active = 1 AND datim_id IS NOT NULL AND datim_id != '') ORDER BY facility_id";
                        converter.buildMessage(query, false, true);
                    }
                }
            }
        }
        else {
            String directory = ServletActionContext.getServletContext().getInitParameter("contextPath")+"transfer/";
            fileName = directory+ServletActionContext.getRequest().getSession().getAttribute("fileName");
            if(fileName.toLowerCase().endsWith(".csv")) {
                //setFileName(converter.buildMessage(fileName));
            }
            else {
                System.out.println("Invalid file type, file must be in CSV format");
            }
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
