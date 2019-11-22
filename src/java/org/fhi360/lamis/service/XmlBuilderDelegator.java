/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service;

/**
 *
 * @author user1
 */
public class XmlBuilderDelegator {
    private ResultSetToXmlBuilder resultSetToXmlBuilder;
    private EntityIdentifier entityIdentifier;

    public XmlBuilderDelegator() {
        this.resultSetToXmlBuilder = new ResultSetToXmlBuilder();
        this.entityIdentifier = new EntityIdentifier();
    }

    public void delegate(String table, String query, String directory, long facilityId) {
        try {
            if (table.equals("monitor")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("user")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("casemanager")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("communitypharm")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("patient")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("clinic"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("pharmacy"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("laboratory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("adrhistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("oihistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("adherehistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("statushistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("regimenhistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));

            if (table.equals("chroniccare"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("tbscreenhistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("dmscreenhistory"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));

            if (table.equals("anc"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("delivery"))
                resultSetToXmlBuilder.buildDeliveryOrMaternalFollowup(query, table, directory, entityIdentifier.getPatientEntities(facilityId), entityIdentifier.getAncEntities(facilityId));
            if (table.equals("child"))
                resultSetToXmlBuilder.buildChild(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("childfollowup"))
                resultSetToXmlBuilder.buildChildFollowup(query, table, directory, entityIdentifier.getChildEntities(facilityId));
            if (table.equals("maternalfollowup"))
                resultSetToXmlBuilder.buildDeliveryOrMaternalFollowup(query, table, directory, entityIdentifier.getPatientEntities(facilityId), entityIdentifier.getAncEntities(facilityId));
            if (table.equals("partnerinformation"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));

            if (table.equals("specimen")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("eid")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("labno")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("biometric")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("nigqual"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("devolve"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("patientcasemanager"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("eac"))
                resultSetToXmlBuilder.build(query, table, directory, entityIdentifier.getPatientEntities(facilityId));
            if (table.equals("motherinformation")) resultSetToXmlBuilder.build(query, table, directory);

            if (table.equals("assessment")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("hts")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("indexcontact")) resultSetToXmlBuilder.build(query, table, directory);

            if (table.equals("encounter")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("drugtherapy")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("mhtc")) resultSetToXmlBuilder.build(query, table, directory);
            if (table.equals("appointment")) resultSetToXmlBuilder.build(query, table, directory);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }   
}
