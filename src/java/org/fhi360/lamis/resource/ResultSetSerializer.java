/**
 * @author Alozie
 */

package org.fhi360.lamis.resource;

import org.fhi360.lamis.service.ResultSetToXmlBuilder;
import org.fhi360.lamis.service.XmlBuilderDelegator;
import org.fhi360.lamis.utility.FileUtil;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

public class ResultSetSerializer {
    private String query;

    public void ResultSetSerializer() {
    }

    public byte[] serializeAll(String table, String format, long id) {
        query = "SELECT * FROM " + table + " WHERE facility_id = " + id;
        return (format.equalsIgnoreCase("xml")) ? getXml(table, query, id) : getJson(table, query, id);
    }

    public byte[] serializeMobile(String table, String format, long id) {
        query = "SELECT * FROM " + table + " WHERE facility_id = " + id + " AND deviceconfig_id > 0";
        return (format.equalsIgnoreCase("xml")) ? getXml(table, query, id) : getJson(table, query, id);
    }

    public byte[] serialize(String table, String format, long id) {
        query = "SELECT * FROM " + table + " WHERE facility_id = " + id + " AND (uploaded != 1 OR time_stamp >= (SELECT " + 
                table + " FROM exchange WHERE facility_id = " + id + "))";
        if (table.equals("monitor"))
            query = "SELECT * FROM " + table + " WHERE facility_id = " + id + " AND operation_id = 3 AND (uploaded != 1 OR time_stamp >= (SELECT " + table + " FROM exchange WHERE facility_id = " + id + "))";
        ;
        return (format.equalsIgnoreCase("xml")) ? getXml(table, query, id) : getJson(table, query, id);
    }

    //Download system files (not specific to any facility) from the server
    public byte[] serialize(String table, String format) {
        query = "SELECT * FROM " + table;
        return (format.equalsIgnoreCase("xml")) ? getXml(table, query) : getJson(table, query);
    }


    public byte[] getXml(String table, String query) {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String directory = contextPath + "exchange/";

        byte[] bytes;
        try {
            new ResultSetToXmlBuilder().build(query, table, directory);

            String filename = contextPath + "exchange/" + table + ".xml";
            FileUtil fileUtil = new FileUtil();
            fileUtil.deflateFile(filename);  //compress the file before sending
            bytes = FileUtils.readFileToByteArray(new File(filename));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return bytes;
    }

    private byte[] getJson(String table, String query) {
        byte[] bytes = null;
        /*try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
            bytes = new ResultSetToJsonBuilder().build(resultSet, table);

        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
            throw new RuntimeException(exception);
        }*/
        return bytes;
    }


    public byte[] getXml(String table, String query, long id) {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String directory = contextPath + "exchange/";

        byte[] bytes;
        try {
            new XmlBuilderDelegator().delegate(table, query, directory, id);

            String filename = contextPath + "exchange/" + table + ".xml";
            FileUtil fileUtil = new FileUtil();
            fileUtil.deflateFile(filename);  //compress the file before sending
            bytes = FileUtils.readFileToByteArray(new File(filename));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return bytes;
    }

    private byte[] getJson(String table, String query, long id) {
        byte[] bytes = null;
       /* try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            resultSet = preparedStatement.executeQuery();
             bytes = ContextProvider.getBean(JsonBuilderDelegator.class).delegate(resultSet, table, id);

        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
            throw new RuntimeException(exception);
        }*/
        return bytes;
    }


    public byte[] getDhisXml(String table, String period) {
        String contextPath = ServletActionContext.getServletContext().getInitParameter("contextPath");
        String directory = contextPath + "exchange/";

        byte[] bytes = null;
        String query = "SELECT data_element_id_dhis, category_id_dhis, facility_id_dhis, period, value FROM dhisvalue WHERE period = '" + period + "'";
        if (period.trim().equals("0"))
            query = "SELECT data_element_id_dhis, category_id_dhis, facility_id_dhis, period, value FROM dhisvalue";

        new ResultSetToXmlBuilder().build(query, table, directory);

        String filename = contextPath + "exchange/" + table + ".xml";
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.deflateFile(filename);  //compress the file before sending
            bytes = FileUtils.readFileToByteArray(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
