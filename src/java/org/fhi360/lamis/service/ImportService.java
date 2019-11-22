/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service;

import java.io.File;
import org.fhi360.lamis.utility.FileUtil;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.Constants;

public class ImportService {

    public static synchronized void processXml() {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        HttpSession session = ServletActionContext.getRequest().getSession();
        session.setAttribute("isImport", true);

        XmlParserDelegator xmlParserDelegator = new XmlParserDelegator();
        FileUtil fileUtil = new FileUtil();
        File file = null;

        String directory = contextPath + "transfer/";
        fileUtil.makeDir(directory);

        String zipFile = directory + session.getAttribute("fileName");
        try {
            fileUtil.unzip(zipFile);
            String[] tables = Constants.Tables.TRANSACTION_TABLES.split(",");
            for (String table : tables) {
                System.out.println(".... importing" + table);
                session.setAttribute("processingStatus", table);
                String fileName = contextPath + "exchange/" + table + ".xml";
                file = new File(fileName);
                if (file.exists()) {
                    xmlParserDelegator.delegate(table, fileName);
                    file.delete();
                }
            }
        } catch (Exception ex) {
            session.setAttribute("processingStatus", "terminated");
            ex.printStackTrace();
        }
        session.setAttribute("isImport", false);
        session.setAttribute("processingStatus", "completed");
    }
}
