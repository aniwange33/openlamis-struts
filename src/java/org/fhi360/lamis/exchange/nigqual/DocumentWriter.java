/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.nigqual;

/**
 *
 * @author user1
 */

import java.io.File;
import java.io.FileOutputStream;
import org.apache.struts2.ServletActionContext;
import org.dom4j.io.OutputFormat;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.fhi360.lamis.utility.FileUtil;

public class DocumentWriter {
    public static void writeXmlToFile(Document document, String table) {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String fileName = contextPath + "transfer/nigqual/" + table + ".xml";
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(outputStream, format);
            writer.write(document);
            writer.flush();

            writer.close();
            outputStream.close();
            document = null;

            //Copy files to secondary locations
            FileUtil fileUtil = new FileUtil();
            fileUtil.makeDir(contextPath+"transfer/nigqual/");
            fileUtil.makeDir(ServletActionContext.getRequest().getContextPath()+"/transfer/nigqual/");
            fileName = table + ".xml";
            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if(!contextPath.equalsIgnoreCase(ServletActionContext.getRequest().getContextPath())) fileUtil.copyFile(fileName, contextPath + "transfer/nigqual/", ServletActionContext.getRequest().getContextPath() + "/transfer/nigqual/");                    
        
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }  
        
    }    
        
}
