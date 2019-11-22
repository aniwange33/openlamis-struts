/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.exchange.radet;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;

public class RegimenResolver {

    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    public RegimenResolver() {

    }

    public void initialize() {
        try {
            executeUpdate("DROP TABLE IF EXISTS resolver");
            executeUpdate("CREATE TEMPORARY TABLE resolver (regimensys VARCHAR(100), regimen VARCHAR(100))");

            String fileName = ServletActionContext.getServletContext().getInitParameter("contextPath") + "regimen.csv";
            if (new File(fileName).exists()) {
                excelFile(fileName);
            } else {
                query = "INSERT INTO resolver (regimensys, regimen) SELECT description, description FROM regimen WHERE regimentype_id IN(1, 2, 3, 4, 14)";
                executeUpdate(query);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {

        }

    }

    private void excelFile(String fileName) {
        String[] row = null;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            while ((row = csvReader.readNext()) != null) {
                String regimensys = row[0];
                String regimen = row[1];
                query = "INSERT INTO resolver (regimensys, regimen) VALUES('" + regimensys + "', '" + regimen + "')";
                executeUpdate(query);
            }
            csvReader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void textFile(String fileName) {
        String content = "";
        String[] row = null;
        try {
            File radet = new File(fileName);
            InputStream inputStream = new FileInputStream(radet);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while ((content = bufferedReader.readLine()) != null) {
                row = content.split(",");
                String regimensys = row[0];
                String regimen = row[1];
                query = "INSERT INTO resolver (regimensys, regimen) VALUES('" + regimensys + "', '" + regimen + "')";
                executeUpdate(query);
            }
            bufferedReader.close();
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getRegimen1(String regimensys) {
        String[] regimen = {""};
        query = "SELECT regimen FROM resolver WHERE regimensys = '" + regimensys + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    regimen[0] = resultSet.getString("regimen");
                }
                return null;
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {

        }
        return regimen[0];
    }

    public String getRegimen(String regimensys) {
        String[] regimen = {""};
        query = "SELECT regimen FROM regimenresolver WHERE regimensys = '" + regimensys + "'";
        try {
            jdbcTemplate.query(query, rs -> {
            while (rs.next()) {
                regimen[0] = rs.getString("regimen");
            }
            return null;
            });
        } catch (Exception exception) {
            
        }
        return regimen[0];
    }

    private void executeUpdate(String query) {
        try {
            jdbcTemplate.execute(query);
        } catch (Exception exception) {
            
        }
    }

}
