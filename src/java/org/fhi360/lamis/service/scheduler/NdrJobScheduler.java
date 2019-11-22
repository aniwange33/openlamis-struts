/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.scheduler;

import org.fhi360.lamis.exchange.ndr.NdrConverter;
import org.fhi360.lamis.utility.ConverterUtil;
import org.fhi360.lamis.utility.FileUtil;
import org.fhi360.lamis.utility.JDBCUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author user10
 */
public class NdrJobScheduler extends QuartzJobBean {
    
    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        //This job is executed every morning triggered by the ndrCronTrigger in the applicationContext.xml
        try {         
            System.out.println("Running Automated NDR generation Service.......");
            autoGenerateNDR();
        } 
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    //Get All the states with data in the database
    private void autoGenerateNDR(){
        FileUtil fileUtil = new FileUtil();
        ConverterUtil.performNdrConversion( "", "", fileUtil, false);
        
        /*try {
            String createQuery = "CREATE TEMPORARY TABLE actives (facility_id INT, facility_name VARCHAR(200), current_count INT)";
            executeUpdate(createQuery);
            
            //Start facility Level Conversion...
            ConverterUtil.performNdrConversion( "", "", converter, fileUtil, false);
            
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }*/
    }
    
    
    //Iterate over each State
    private void performExport(Long stateId){
        //Get the List of All Facilities...
        ResultSet resultSet;
         NdrConverter converter = new NdrConverter();
        FileUtil fileUtil = new FileUtil();
        try {
            JDBCUtil jdbcUtil1 = new JDBCUtil();
            String query1 = "SELECT facility_id, name FROM facility WHERE facility_id IN (SELECT DISTINCT facility_id FROM patient) AND state_id = ? ORDER BY name ASC";                                        

            PreparedStatement preparedStatement1 = jdbcUtil1.getStatement(query1);
            preparedStatement1.setLong(1, stateId);
            resultSet = preparedStatement1.executeQuery();
            while(resultSet.next()) {
                String facilityName = resultSet.getString("name");
                Long facilityId = resultSet.getLong("facility_id");
                
                //Export Per Facility...
                System.out.println("Starting for Facility: "+facilityName);
                ConverterUtil.performNdrConversion(stateId.toString(), "", fileUtil, false);
            }
            resultSet = null;            
        }
        catch (Exception exception) {
            resultSet = null;
//            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

}