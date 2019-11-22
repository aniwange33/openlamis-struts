/**
 *
 * @author user1
 */
package org.fhi360.lamis.service;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.fhi360.lamis.utility.StringUtil;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSetMetaData;
import java.util.Base64;
import java.util.Map;
import org.fhi360.lamis.service.beans.ContextProvider;

public class ResultSetToXmlBuilder {

     private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public ResultSetToXmlBuilder() {
    }

    public void build(String query, String table, String directory) {
        String fileName = directory + table + ".xml";
        jdbcTemplate.query(query, resultSet -> {
            try {
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement(StringUtil.pluralize(table));
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Element row = root.addElement(table);
                    for (int i = 1; i <= colCount; i++) {
                        String columnName = metaData.getColumnName(i).toLowerCase();
                        Object value = resultSet.getObject(i) == null ? "" : resultSet.getObject(i);
                        Element column = row.addElement(columnName);
                        if (columnName.equals("template")) {
                            if (value != null) {
                                try {
                                    String stringVal = Base64.getEncoder().encodeToString((byte[]) value);
                                    column.setText(stringVal);
                                } catch (ClassCastException ignore) {
                                }
                            }
                        } else {
                            column.setText(value.toString());
                        }
                    }
                }
                writeXmlToFile(document, fileName);
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new RuntimeException(exception);
            }
            return null;
        });
    }

    public void build(String query, String table, String directory, Map<Long, String> patientEntityMap) {
        String fileName = directory + table + ".xml";
        jdbcTemplate.query(query, resultSet -> {
            try {
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement(StringUtil.pluralize(table));
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    if (patientEntityMap.get(resultSet.getLong("patient_id")) != null) {
                        long id = 0;
                        Element row = root.addElement(table);
                        for (int i = 1; i <= colCount; i++) {
                            String columnName = metaData.getColumnName(i).toLowerCase();
                            Object value = resultSet.getObject(i) == null ? "" : resultSet.getObject(i);

                            if (columnName.equals("patient_id")) {
                                try {
                                    id = (Long) value;
                                } catch (ClassCastException ignored) {
                                }
                            }
                            Element column = row.addElement(columnName);
                            if (columnName.equals("template")) {
                                if (value != null) {
                                    try {
                                        String stringVal = Base64.getEncoder().encodeToString((byte[]) value);
                                        column.setText(stringVal);
                                    } catch (ClassCastException ignore) {
                                    }
                                }
                            } else {
                                column.setText(value.toString());
                            }
                        }
                        Element column = row.addElement("hospital_num");
                        column.setText(patientEntityMap.get(id));     // column.setText(entityIdentifier.getHospitalNum(id));
                    }
                }
                resultSet = null;
                writeXmlToFile(document, fileName);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
            return null;
        });
    }

    public void buildChild(String query, String table, String directory, Map<Long, String> patientEntityMap) {
        String fileName = directory + table + ".xml";
        ContextProvider.getBean(JdbcTemplate.class).query(query, resultSet -> {
            try {
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement(StringUtil.pluralize(table));
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    if (patientEntityMap.get(resultSet.getLong("patient_id")) != null) {
                        long id = 0;
                        Element row = root.addElement(table);
                        for (int i = 1; i <= colCount; i++) {
                            String columnName = metaData.getColumnName(i).toLowerCase();
                            Object value = resultSet.getObject(i) == null ? "" : resultSet.getObject(i);

                            if (columnName.equals("patient_id")) {
                                id = (Long) value;
                            }
                            Element column = row.addElement(columnName);
                            column.setText(value.toString());
                        }
                        Element column = row.addElement("hospital_num_mother");
                        column.setText(patientEntityMap.get(id));     // column.setText(entityIdentifier.getHospitalNum(id));
                    }
                }
                writeXmlToFile(document, fileName);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
            return null;
        });
    }

    public void buildDeliveryOrMaternalFollowup(String query, String table, String directory, Map<Long, String> patientEntityMap, Map<Long, String> ancEntityMap) {
        String fileName = directory + table + ".xml";
        ContextProvider.getBean(JdbcTemplate.class).query(query, resultSet -> {
            try {
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement(StringUtil.pluralize(table));
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    if (patientEntityMap.get(resultSet.getLong("patient_id")) != null) {
                        long id = 0;
                        long ancId = 0;
                        Element row = root.addElement(table);
                        for (int i = 1; i <= colCount; i++) {
                            String columnName = metaData.getColumnName(i).toLowerCase();
                            Object value = resultSet.getObject(i) == null ? "" : resultSet.getObject(i);

                            if (columnName.equals("patient_id")) {
                                id = (Long) value;
                            }
                            if (columnName.equals("anc_id")) {
                                ancId = (Long) value;
                            }
                            Element column = row.addElement(columnName);
                            column.setText(value.toString());
                        }
                        Element column = row.addElement("hospital_num");
                        column.setText(patientEntityMap.get(id));     // column.setText(entityIdentifier.getHospitalNum(id));
                        column = row.addElement("anc_num");
                        column.setText((ancEntityMap.get(ancId) == null) ? "" : ancEntityMap.get(ancId));
                    }
                }
                writeXmlToFile(document, fileName);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
            return null;
        });
    }

    public void buildChildFollowup(String query, String table, String directory, Map<Long, String> childEntityMap) {
        String fileName = directory + table + ".xml";
        ContextProvider.getBean(JdbcTemplate.class).query(query, resultSet -> {
            try {
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement(StringUtil.pluralize(table));
                ResultSetMetaData metaData = resultSet.getMetaData();
                int colCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    long id = 0;
                    Element row = root.addElement(table);
                    for (int i = 1; i <= colCount; i++) {
                        String columnName = metaData.getColumnName(i).toLowerCase();
                        Object value = resultSet.getObject(i) == null ? "" : resultSet.getObject(i);

                        if (columnName.equals("child_id")) {
                            id = (Long) value;
                        }
                        Element column = row.addElement(columnName);
                        column.setText(value.toString());
                    }
                    Element column = row.addElement("reference_num");
                    column.setText(childEntityMap.get(id) == null ? "" : childEntityMap.get(id));     // column.setText(entityIdentifier.getHospitalNum(id));
                }
                writeXmlToFile(document, fileName);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
            return null;
        });
    }

    private void writeXmlToFile(Document document, String fileName) {
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(outputStream, format);
            writer.write(document);
            writer.flush();

            writer.close();
            outputStream.close();
            document = null;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }
}
