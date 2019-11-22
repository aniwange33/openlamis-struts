/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service;

import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import java.sql.ResultSet;
import org.fhi360.lamis.utility.FileUtil;
import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;
import org.fhi360.lamis.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Date;
import org.fhi360.lamis.service.beans.ContextProvider;

public class ExportService {

    private static XmlBuilderDelegator xmlBuilderDelegator = new XmlBuilderDelegator();
    private static ResultSet resultSet;
    private static Timestamp timestamp;

    public static synchronized String buildXml() {

        timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String directory = contextPath + "exchange/";
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        String xmlFiles = "";

        String[] tables = Constants.Tables.TRANSACTION_TABLES.split(",");
        try {
            for (String table : tables) {
                session.setAttribute("processingStatus", table);

                xmlFiles += directory + table + ".xml,";

                String query = getDataQuery(table, true, facilityId);
                xmlBuilderDelegator.delegate(table, query, directory, facilityId);
            }
        } catch (Exception exception) {
            session.setAttribute("processingStatus", "terminated");
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        FileUtil fileUtil = new FileUtil();
        fileUtil.makeDir(contextPath + "transfer/");
        fileUtil.makeDir(request.getContextPath() + "/transfer/");

        String fileName = "lamis.zip";
        if (session.getAttribute("facilityName") != null) {
            fileName = (String) session.getAttribute("facilityName") + ".zip";
        }
        String zipFile = contextPath + "transfer/" + fileName;

        try {
            String[] files = xmlFiles.split(",");
            fileUtil.zip(files, zipFile);
            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if (!contextPath.equalsIgnoreCase(request.getContextPath())) {
                fileUtil.copyFile(fileName, contextPath + "transfer/", request.getContextPath() + "/transfer/");
            }
            updateExportTime(facilityId);
        } catch (Exception exception) {
            session.setAttribute("processingStatus", "terminated");
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        session.setAttribute("processingStatus", "completed");
        return "transfer/" + fileName;
    }

    private static String getDataQuery(String table, boolean recordsAll, long facilityId) {
        return (recordsAll) ? "SELECT * FROM " + table + " WHERE facility_id = " + facilityId
                : "SELECT * FROM " + table + " WHERE facility_id = " + facilityId + " AND time_stamp > SELECT export "
                + "FROM exchange WHERE facility_id = " + facilityId;
    }

    private static void updateExportTime(long facilityId) {
        String query = "UPDATE exchange SET export = ? WHERE facility_id = ?";
        ContextProvider.getBean(TransactionTemplate.class).execute(status -> {
            ContextProvider.getBean(JdbcTemplate.class).update(query, new Date(),
                    facilityId);
            return null;
        });
    }
}
//query = query.replace("[table]", table);
