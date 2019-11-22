/**
 *
 * @author user1
 */

package org.fhi360.lamis.exchange.dhis;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;


public class DataInstanceResolver {
   private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private String query;
    
    public void resolveInstance() {
        ResultSet resolver = null;
        ResultSet indicator = executeQuery("SELECT * FROM indicator");
        try {
            while (indicator.next()) {
                String state = indicator.getString("state");
                String lga = indicator.getString("lga");                
                String orgUnit = indicator.getString("org_unit");
                String dataElement = indicator.getString("data_element");
                String category = indicator.getString("category");
                int value = indicator.getInt("value");
                String dataElementUUID = "";
                String categoryUUID = "";
                
                query = "SELECT data_element_instance, category_instance FROM datainstance WHERE data_element = '" + dataElement + "' AND category = '" + category + "'";
                resolver = executeQuery(query);
                while(resolver.next()) {
                    dataElementUUID = getDataElementUUID(resolver.getString("data_element_instance"));
                    categoryUUID = getCategoryUUID(resolver.getString("category_instance"));                    
                }                
                String orgUnitUUID = getOrgUnitUUID(orgUnit);
                
                //Save indicator into datavalue table
                query = "INSERT INTO datavalue (state, lga, org_unit, data_element, category_option_combo, value, stored_by, time_stamp, follow_up) VALUES('" + state + "', '" + lga + "', '" + orgUnitUUID + "', '" + dataElementUUID + "', '" + categoryUUID + "', " + value + ", NOW(), 'false')";  
                executeUpdate(query);
            }            
        }
        catch (Exception exception) {
            indicator = null;
            resolver = null;
           
        }          
    }
    
    private String getDataElementUUID(String dataElementInstance) {
        query = "SELECT data_element_uuid AS uuid FROM dataelement WHERE data_element = '" + dataElementInstance + "'";
        return getUUID(query);                
    }
    
    private String getCategoryUUID(String categoryInstance) {
        query = "SELECT category_uuid AS uuid FROM category WHERE category = '" + categoryInstance + "'";
        return getUUID(query);                
    }
    
    private String getOrgUnitUUID(String orgUnit) {
        query = "SELECT org_unit_uuid AS uuid FROM orgunit WHERE org_unit = '" + orgUnit + "'";
        return getUUID(query);                
    }


    private String getUUID(String query) {
        String uuid = "";
        try {
             uuid = jdbcTemplate.queryForObject(query, String.class);
        }
        catch (Exception exception) {
            
        }
        return uuid;        
    }   

    private ResultSet executeQuery(String query) {
     
        try {
             jdbcTemplate.query(query, (resultSet) -> {
                 return resultSet;
             });
        }
        catch (Exception exception) {
          
        }
        return null;
    } 
    
       private void executeUpdate(String query) {
        try {
            transactionTemplate.execute((ts) -> {
                jdbcTemplate.execute(query);
                return null; //To change body of generated lambdas, choose Tools | Templates.
            });
        }
        catch (Exception exception) {
          
        }        
    }        
    
}
