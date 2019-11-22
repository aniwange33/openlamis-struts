/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.dhis;

/**
 *
 * @author user1
 */
import java.io.File;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.io.FileOutputStream;
import org.dom4j.io.OutputFormat;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import org.fhi360.lamis.utility.FileUtil;
import org.fhi360.lamis.utility.StringUtil;
import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class DhisExportService {
   private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private static Timestamp timestamp;
    
    public static synchronized String buildXml() {
        timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = ServletActionContext.getRequest().getSession();
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String stcontextPath = ServletActionContext.getServletContext().getInitParameter("stcontextPath");
        String table = "datavalue";
        try {
            ResultSet resultSet = executeQuery("SELECT state, lga, org_unit, SUM(value) FROM datavalue GROUP BY category_option_combo");
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement(StringUtil.pluralize(table));
            ResultSetMetaData metaData = resultSet.getMetaData();
            int colCount = metaData.getColumnCount();
            while(resultSet.next()) {
                Element row = root.addElement(table);
                for(int i = 1; i <= colCount; i++) {
                    String columnName = metaData.getColumnName(i).toLowerCase();
                    Object value = resultSet.getObject(i) == null? "" : resultSet.getObject(i);

                    Element column = row.addElement(columnName);
                    column.setText(value.toString());
                }
            }
            String fileName = contextPath+"exchange/"+table+".xml";
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(outputStream, format);
            writer.write(document);
            writer.flush();

            writer.close();
            outputStream.close();
            resultSet = null;
            document = null;
        }
        catch (Exception exception) {
       
            throw new RuntimeException(exception);
        }
        String[] myFiles = {contextPath+"exchange/datavalue.xml"};
        
        String directory = contextPath+"archive/";
        FileUtil fileUtil = new FileUtil();
        fileUtil.makeDir(directory);
        fileUtil.makeDir(stcontextPath+"archive/");
        fileUtil.makeDir(request.getContextPath()+"/archive/");

        String fileName = "lamis.zip";
        if(session.getAttribute("state") != null) {
            fileName = (String) session.getAttribute("state")+".zip";
        } 
        String zipFile = directory+fileName;
        try {
            fileUtil.zip(myFiles, zipFile);
            //for servlets in the stand alone (webapps) context, copy file to the transfer folder in webapps 
            fileUtil.copyFile(fileName, contextPath+"archive/", stcontextPath+"archive/");
            //for servlets in the default(root) context, copy file to the transfer folder in root 
            if(!contextPath.equalsIgnoreCase(request.getContextPath())) fileUtil.copyFile(fileName, contextPath+"archive/", request.getContextPath()+"/archive/");                    
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return "archive/"+fileName;
    }
    
    private static ResultSet executeQuery(String query) {
      
        try {
             jdbcTemplate.query(query, (resultSet) -> {
                 return resultSet;
             });
        }
        catch (Exception exception) {
          
        }
        return null;
    } 
}
