/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class FileUploadAction extends ActionSupport {

    private File attachment;
    private String attachmentFileName;
    private String attachmentContentType;

    public String uploadFile() {
        try {
            String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
            String directory = contextPath + "transfer/";
            File destination = new File(directory);
            if (!destination.exists()) {
                destination.mkdirs();
            }
            if (attachment != null) {
                InputStream in = new FileInputStream(attachment);
                OutputStream out = new FileOutputStream(new File(directory + attachmentFileName));
                IOUtils.copyLarge(in, out);
                out.close();
                in.close();

                ServletActionContext.getRequest().getSession().setAttribute("fileUploaded", 1);  //Used by the view to determine if upload is successful
                ServletActionContext.getRequest().getSession().setAttribute("fileName", attachmentFileName);  //Used by the controller to know the file in the transfer folder to import/parse
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ERROR;
        }
        return SUCCESS;
    }

    public String resetUploadStatus() {
        try {
            ServletActionContext.getRequest().getSession().setAttribute("fileUploaded", 0);
        } catch (Exception e) {
            return ERROR;
        }

        return SUCCESS;
    }

    /**
     * @return the attachment
     */
    public File getAttachment() {
        return attachment;
    }

    /**
     * @param attachment the attachment to set
     */
    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    /**
     * @return the attachmentFileName
     */
    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    /**
     * @param attachmentFileName the attachmentFileName to set
     */
    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    /**
     * @return the attachmentContentType
     */
    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    /**
     * @param attachmentContentType the attachmentContentType to set
     */
    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

}
