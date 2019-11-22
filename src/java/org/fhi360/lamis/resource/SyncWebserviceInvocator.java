/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.resource;

import java.io.BufferedOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.utility.Constants;

/**
 *
 * @author Alozie
 */
public class SyncWebserviceInvocator {

    private static String message;

    private static int BUFFER_SIZE = 32769;

    public static synchronized String invokeSyncService() {
        int i = 0;
        long facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        String[] tables = null;
        System.out.println("REQUEST " + ServletActionContext.getRequest().getParameterMap().toString());
        if (ServletActionContext.getRequest().getParameterMap().containsKey("Transaction")) {
            tables = Constants.Tables.TRANSACTION_TABLES.split(",");
            try {
                downloadOrSync(tables, facilityId, 1);
                WebserviceResponseHandler.process(tables, facilityId,"xml");
                message = "Download & Sync Completed";
            } catch (Exception exception) {
                ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "terminated");
                message = "Error While Syncing Data";
                exception.printStackTrace();
            }
            ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "completed");
        }

        if (ServletActionContext.getRequest().getParameterMap().containsKey("Auxillary")) {
            tables = Constants.Tables.AUXILLARY_TABLES.split(",");
            try {
                downloadOrSync(tables, facilityId, 2);
                WebserviceResponseHandler.process(tables, facilityId, "json");
                message = "Download & Sync Completed";
            } catch (Exception exception) {
                ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "terminated");
                message = "Error While Syncing Data";
                exception.printStackTrace();

            }
            ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "completed");
        }

        if (ServletActionContext.getRequest().getParameterMap().containsKey("System")) {
            tables = Constants.Tables.SYSTEM_TABLES.split(",");
            try {
                //to be work on as update
                //  downloadOrSync(tables, facilityId, 3, false);
                //  WebserviceResponseHandler.process(tables, facilityId);
                message = "Download & Sync Completed";
            } catch (Exception exception) {
                ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "terminated");
                message = "Error While Syncing Data";
                exception.printStackTrace();
            }
            ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "completed");
        }

        //Upload table records to the server. The server receives the data as deflated bytes and writes the records to table
        //The server returns the entire record in the server table back to the client and inflated after all records have been synchronized
        //the client finally writes the xml files into the correponding database tables
        return message;
    }

    private static void downloadOrSync(String[] tables, long facilityId, int id) {
        Timestamp timestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        String resourceUrl = "";
        URLConnection connection = null;
        
        try {
            
            for (String table : tables) {
                ServletActionContext.getRequest().getSession().setAttribute("processingStatus", table);

                if (id == 1) {
                    resourceUrl = ServletActionContext.getServletContext().getInitParameter("serverUrl") + "resources/webservice/sync/" + table.trim() + "/" + facilityId;
                    connection = getconnectUrl(resourceUrl, "application/xml");
                    BufferedOutputStream output = new BufferedOutputStream(connection.getOutputStream());

                    //Select records from table, convert resultset to xml and return as array of bytes
                    //the read and write until last byte is encountered
                    byte[] buffer = new ResultSetSerializer().serialize(table, "xml", facilityId);
                    for (int i = 0; i < buffer.length; i++) {
                        byte oneByte = buffer[i];
                        output.write(oneByte);
                    }
                    output.close();
                } 
                else {
                    if (id == 2) {
                        resourceUrl = ServletActionContext.getServletContext().getInitParameter("serverUrl") + "resources/webservice/download/mobile/" + table.trim() + "/" + facilityId;
                        connection = getconnectUrl(resourceUrl, "application/json");
                    } 
                    else {
                        resourceUrl = ServletActionContext.getServletContext().getInitParameter("serverUrl") + "resources/webservice/download/" + table.trim();
                        connection = getconnectUrl(resourceUrl, "application/json");
                    }
                }

                //The content of input stream (records) returned by the server should be written to disk
                InputStream stream = connection.getInputStream();
                if(id==1){
                      WebserviceResponseHandler.writeStreamToFile(stream, table, "xml");
                }
                else{
                      WebserviceResponseHandler.writeStreamToFile(stream, table, "json");
                }
              
                stream.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    private static URLConnection getconnectUrl(String resourceUrl, String contentType) {
        URLConnection connection = null;
        try {
            URL url = new URL(resourceUrl);
            connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setConnectTimeout(120000);
            connection.setReadTimeout(0);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return connection;
    }
}
