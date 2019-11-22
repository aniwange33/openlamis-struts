/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

/**
 *
 * @author user10
 */
public class DatabaseUtil {
    private static BasicDataSource ds_dbcp2 = new BasicDataSource();
    private static ComboPooledDataSource ds_c3p0 = new ComboPooledDataSource();
    
    public DatabaseUtil() throws SQLException, IOException {                              
        try {
            File file = new File("jdbc_setting.properties");
            if (file.exists()) {
                Map<String, Object> map = new PropertyAccessor().getJdbcProperties();
                String url = (String) map.get("dbUrl");
                String user = (String) map.get("dbUser");
                String password = (String) map.get("dbPassword");
                String driver = (String) map.get("dbDriver");

                //C3P0 Connection
                ds_c3p0.setDriverClass(driver);
                ds_c3p0.setJdbcUrl(url);
                ds_c3p0.setUser(user);
                ds_c3p0.setPassword(password);
                ds_c3p0.setMinPoolSize(5);
                ds_c3p0.setAcquireIncrement(5);
                ds_c3p0.setMaxPoolSize(50);                

                //Apache DBCP2 Connection
                ds_dbcp2.setDriverClassName(driver);
                ds_dbcp2.setUrl(url);
                ds_dbcp2.setUsername(user);
                ds_dbcp2.setPassword(password);
                ds_dbcp2.setInitialSize(5);
                System.out.println("JDBC properties file found .....");              
            } 
            else {
                System.out.println("JDBC properties file not found .....");              
            }
        }
        catch (Exception exception) {
           exception.printStackTrace();
        }
    } 

    public Connection getc3p0Connection() throws SQLException {
        return ds_c3p0.getConnection();
    }

    public Connection getDBCP2Connection() throws SQLException {
        return ds_dbcp2.getConnection();
    }
    
}
