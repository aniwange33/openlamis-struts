/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

import java.sql.ResultSet;

/**
 *
 * @author user1
 */
public class JsonBuilderDelegator {
    public byte[] delegate(ResultSet resultSet, String table, long facilityId) {
        byte[] json = null;
        
        try {
            EntityIdentifier entityIdentifier = new EntityIdentifier();
            if(table.equals("monitor")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("patient")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("clinic")) json = new ResultSetToJsonBuilder().build(resultSet, table,entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("pharmacy")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("laboratory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("adrhistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("oihistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("adherehistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("statushistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("regimenhistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            
            if(table.equals("chroniccare")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("tbscreenhistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("dmscreenhistory")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            
            if(table.equals("anc")) json = new ResultSetToJsonBuilder().build(resultSet, table,entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("delivery")) json = new ResultSetToJsonBuilder().buildDeliveryOrMaternalFollowup(resultSet,table, entityIdentifier.getPatientEntities(facilityId), entityIdentifier.getAncEntities(facilityId));
            if(table.equals("child")) json = new ResultSetToJsonBuilder().buildChild(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("childfollowup")) json = new ResultSetToJsonBuilder().buildChildFollowup(resultSet, table, entityIdentifier.getChildEntities(facilityId));
            if(table.equals("maternalfollowup")) json = new ResultSetToJsonBuilder().buildDeliveryOrMaternalFollowup(resultSet,table,  entityIdentifier.getPatientEntities(facilityId), entityIdentifier.getAncEntities(facilityId));
            if(table.equals("partnerinformation")) json = new ResultSetToJsonBuilder().build(resultSet,table,  entityIdentifier.getPatientEntities(facilityId));
            
            if(table.equals("specimen")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("eid")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("labno")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            
            if(table.equals("nigqual")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("devolve")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("patientcasemanager")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("eac")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            
             if(table.equals("encounter")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("drugtherapy"))json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("mhtc")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
            if(table.equals("appointment")) json = new ResultSetToJsonBuilder().build(resultSet,table, entityIdentifier.getPatientEntities(facilityId));
                 
            if(table.equals("assessment")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("hts")) json = new ResultSetToJsonBuilder().build(resultSet,table);
            if(table.equals("indexcontact")) json = new ResultSetToJsonBuilder().build(resultSet,table);

         
        } 
        catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        return json;
    }     
}
