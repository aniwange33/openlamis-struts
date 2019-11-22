/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.parser.mobile;

import org.fhi360.lamis.service.DeleteService;
import org.fhi360.lamis.utility.DateUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author user10
 */
public class MonitorJsonParser {
    public void parserJson(String content) {
        DeleteService deleteService = new DeleteService();
        try {
            JSONArray monitor = new JSONArray(content); 
            for (int i = 0; i < monitor.length(); i++) {
                JSONObject record = monitor.optJSONObject(i);
                long facilityId = record.getLong("facility_id");
                String[] entityId = record.getString("entity_id").split("#");
                String tableName = record.optString("table_name");
                int operationId = record.getInt("operation_id"); 

                if(operationId == 3) {
                    if(tableName.equals("encounter") && entityId[1] != null) {
                        deleteService.deleteEncounter(facilityId, Long.parseLong(entityId[0]), DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                    }      
                    if(tableName.equals("chroniccare") && entityId[1] != null) {
                        deleteService.deleteChroniccare(facilityId, Long.parseLong(entityId[0]), DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                    }      
                    if(tableName.equals("drugtherapy") && entityId[1] != null) {
                        deleteService.deleteDrugtherapy(facilityId, Long.parseLong(entityId[0]), DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                    }      
                    if(tableName.equals("appointment") && entityId[1] != null) {
                        deleteService.deleteAppointment(facilityId, Long.parseLong(entityId[0]), DateUtil.parseStringToDate(entityId[1], "MM/dd/yyyy"));
                    }      
                    if(tableName.equals("htc") && entityId[1] != null) {
                        deleteService.deleteMhtc(facilityId, Integer.parseInt(entityId[0]), Integer.parseInt(entityId[1]));
                    }      
                }                
            }
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }        

    }
        
}
