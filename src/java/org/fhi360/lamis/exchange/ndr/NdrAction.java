/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.ndr;

import com.opensymphony.xwork2.ActionSupport;
import java.util.Arrays;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.UploadFolderService;
import org.fhi360.lamis.utility.ConverterUtil;
import org.fhi360.lamis.utility.FileUtil;

/**
 *
 * @author user1
 */
public class NdrAction extends ActionSupport {
    private String status;
    private String fileName;

    UploadFolderService uploadFolderService = new UploadFolderService();

    public String dispatcher() {
        NdrConverter converter = new NdrConverter();
        FileUtil fileUtil = new FileUtil();
        String stateId = ServletActionContext.getRequest().getParameter("stateId");
        String facilityIds = ServletActionContext.getRequest().getParameter("facilityIds");
        System.out.println("Conversion begin state Id..........."+stateId);
        System.out.println("Conversion begin facility Ids..........."+facilityIds);

        ConverterUtil.performNdrConversion(stateId, facilityIds, fileUtil, true);
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
