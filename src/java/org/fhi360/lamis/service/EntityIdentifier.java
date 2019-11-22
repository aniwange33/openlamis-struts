/**
 * @author aalozie
 */
package org.fhi360.lamis.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;

public class EntityIdentifier {
    private String dbSuffix;
    private Map<Long, String> map;

    public EntityIdentifier() {
        
    }

    public synchronized void createEntities(long facilityId, String table, String dbSuffix) {
        executeUpdate("DROP VIEW IF EXISTS entity_" + dbSuffix);
        executeUpdate("DROP VIEW IF EXISTS dependant_" + dbSuffix);
        executeUpdate("DROP VIEW IF EXISTS dependant1_" + dbSuffix);
        executeUpdate("DROP VIEW IF EXISTS mapper");
        executeUpdate("DROP VIEW IF EXISTS mapper1_" + dbSuffix);
        executeUpdate("DROP VIEW IF EXISTS motherentity_" + dbSuffix);
//        executeUpdate("DROP INDEX IF EXISTS idx_entity");
//        executeUpdate("DROP INDEX IF EXISTS idx_dependant");
//        executeUpdate("DROP INDEX IF EXISTS idx_dependant1");

        String entities = "patient clinic pharmacy laboratory adrhistory oihistory adherehistory statushistory regimenhistory chroniccare dmscreenhistory tbscreenhistory anc delivery motherinformation child maternalfollowup partnerinformation nigqual devolve patientcasemanager eac";
        if (entities.contains(table.toLowerCase())) {
            String createQuery = "CREATE VIEW entity_" + dbSuffix + " AS SELECT patient_id, patient_id AS id_on_server, hospital_num FROM patient WHERE facility_id = " + facilityId;
            //System.out.println("Entity create query: "+createQuery);
            executeUpdate(createQuery);
//            //executeUpdate("CREATE INDEX idx_entity ON entity(hospital_num)");
        }

        if (table.equals("patient")) {
            executeUpdate("CREATE VIEW mapper AS SELECT casemanager_id AS id_on_server, local_id AS id_on_client, 'casemanager' AS table_name FROM casemanager WHERE facility_id = " + facilityId);
            executeUpdate("CREATE VIEW mapper1_" + dbSuffix + " AS SELECT user_id AS id_on_server, local_id AS id_on_client, 'user' AS table_name FROM user WHERE facility_id = " + facilityId);
        }

        if (table.equals("clinic")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT clinic.clinic_id AS id_on_server, clinic.date_visit, patient.hospital_num FROM clinic JOIN patient ON clinic.patient_id = patient.patient_id WHERE clinic.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit)");
        }
        if (table.equals("pharmacy")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT pharmacy.pharmacy_id AS id_on_server, pharmacy.date_visit, pharmacy.regimendrug_id, patient.hospital_num FROM pharmacy JOIN patient ON pharmacy.patient_id = patient.patient_id WHERE pharmacy.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit, regimendrug_id)");
        }
        if (table.equals("laboratory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT laboratory.laboratory_id AS id_on_server, laboratory.date_reported, laboratory.labtest_id, patient.hospital_num FROM laboratory JOIN patient ON laboratory.patient_id = patient.patient_id WHERE laboratory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_reported, labtest_id)");
        }
        if (table.equals("adrhistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT adrhistory.history_id AS id_on_server, adrhistory.date_visit, adrhistory.adr, adrhistory.screener, patient.hospital_num FROM adrhistory JOIN patient ON adrhistory.patient_id = patient.patient_id WHERE adrhistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit, adr, screener)");
        }
        if (table.equals("oihistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT oihistory.history_id AS id_on_server, oihistory.date_visit, oihistory.oi, patient.hospital_num FROM oihistory JOIN patient ON oihistory.patient_id = patient.patient_id WHERE oihistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit, oi)");
        }
        if (table.equals("adherehistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT adherehistory.history_id AS id_on_server, adherehistory.date_visit, patient.hospital_num FROM adherehistory JOIN patient ON adherehistory.patient_id = patient.patient_id WHERE adherehistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_num ON dependant(hospital_num, date_visit)");
        }
        if (table.equals("regimenhistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT regimenhistory.history_id AS id_on_server, regimenhistory.date_visit, regimenhistory.regimentype, regimenhistory.regimen, patient.hospital_num FROM regimenhistory JOIN patient ON regimenhistory.patient_id = patient.patient_id WHERE regimenhistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_num ON dependant(hospital_num, date_visit, regimentype, regimen)");
        }
        if (table.equals("statushistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT statushistory.history_id AS id_on_server, statushistory.date_current_status, statushistory.current_status, patient.hospital_num FROM statushistory JOIN patient ON statushistory.patient_id = patient.patient_id WHERE statushistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_num ON dependant(hospital_num, date_current_status, current_status)");
        }

        if (table.equals("eac")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT eac.eac_id AS id_on_server, eac.date_eac1, patient.hospital_num FROM eac JOIN patient ON eac.patient_id = patient.patient_id WHERE eac.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_num ON dependant(hospital_num, date_eac1)");
        }

        //Chroniccare section
        if (table.equals("chroniccare")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT chroniccare.chroniccare_id AS id_on_server, chroniccare.date_visit, patient.hospital_num FROM chroniccare JOIN patient ON chroniccare.patient_id = patient.patient_id WHERE chroniccare.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit)");
        }
        if (table.equals("tbscreenhistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT tbscreenhistory.history_id AS id_on_server, tbscreenhistory.date_visit, tbscreenhistory.description, patient.hospital_num FROM tbscreenhistory JOIN patient ON tbscreenhistory.patient_id = patient.patient_id WHERE tbscreenhistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_num ON dependant(hospital_num, date_visit, description)");
        }
        if (table.equals("dmscreenhistory")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT dmscreenhistory.history_id AS id_on_server, dmscreenhistory.date_visit, dmscreenhistory.description, patient.hospital_num FROM dmscreenhistory JOIN patient ON dmscreenhistory.patient_id = patient.patient_id WHERE dmscreenhistory.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_num ON dependant(hospital_num, date_visit, description)");
        }

        //PCR lab section
        if (table.equals("specimen")) {
            executeUpdate("CREATE VIEW entity_" + dbSuffix + " AS SELECT specimen_id, specimen_id AS id_on_server, labno FROM specimen WHERE facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_entity on entity(labno)");
        }
        if (table.equals("eid")) {
            executeUpdate("CREATE VIEW entity_" + dbSuffix + " AS SELECT eid_id, eid_id AS id_on_server, labno FROM eid WHERE facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_entity on entity(labno)");
        }

        //PMTCT section
        if (table.equals("motherinformation")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT motherinformation.motherinformation_id AS id_on_server, patient.hospital_num FROM motherinformation JOIN patient ON motherinformation.patient_id = patient.patient_id WHERE motherinformation.patient_id IS NOT NULL AND motherinformation.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//
            // //executeUpdate("CREATE INDEX idx_dependant ON motherentity (motherinformation_id, hospital_num)");
        }

        if (table.equals("anc")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT anc.anc_id AS id_on_server, anc.date_visit, patient.hospital_num FROM anc JOIN patient ON anc.patient_id = patient.patient_id WHERE anc.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit)");
        }
        if (table.equals("delivery")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT delivery.delivery_id AS id_on_server, delivery.date_delivery, patient.hospital_num FROM delivery JOIN patient ON delivery.patient_id = patient.patient_id WHERE delivery.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_delivery)");

            executeUpdate("CREATE VIEW dependant1_" + dbSuffix + " AS SELECT anc_id AS id_on_server, anc_num FROM anc WHERE facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant1(anc_num)");
        }
        if (table.equals("child")) {
            executeUpdate("CREATE VIEW motherentity_" + dbSuffix + " AS SELECT motherinformation_id, motherinformation_id AS id_on_server, hospital_num, facility_id FROM motherinformation WHERE facility_id = " + facilityId);

            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT child.child_id AS id_on_server, child.reference_num, child.mother_id FROM child WHERE child.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(reference_num, patient_id)");
        }
        if (table.equals("childfollowup")) {
            //Child followup entity table is created from the child table and not from patient
            executeUpdate("CREATE VIEW entity AS SELECT child_id, child_id AS id_on_server, reference_num, mother_id, CONCAT(reference_num,'#', mother_id) as ref_num FROM child WHERE facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_entity on entity(reference_num)");

            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT childfollowup.childfollowup_id AS id_on_server, childfollowup.date_visit, child.reference_num FROM childfollowup JOIN child ON childfollowup.child_id = child.child_id WHERE childfollowup.facility_id = " + facilityId + " AND child.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(reference_num, date_visit)");
        }
        if (table.equals("maternalfollowup")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT maternalfollowup.maternalfollowup_id AS id_on_server, maternalfollowup.date_visit, patient.hospital_num FROM maternalfollowup JOIN patient ON maternalfollowup.maternalfollowup_id = patient.patient_id WHERE maternalfollowup.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_visit)");

            executeUpdate("CREATE VIEW dependant1_" + dbSuffix + " AS SELECT anc_id AS id_on_server, anc_num FROM anc WHERE facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant1(anc_num)");
        }
        if (table.equals("partnerinformation")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT partnerinformation.partnerinformation_id AS id_on_server, patient.hospital_num FROM partnerinformation JOIN patient ON partnerinformation.partnerinformation_id = patient.patient_id WHERE partnerinformation.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num)");
        }

        //NIGQUAL section
        if (table.equals("nigqual")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT nigqual.nigqual_id AS id_on_server, nigqual.review_period_id, patient.hospital_num FROM nigqual JOIN patient ON nigqual.patient_id = patient.patient_id WHERE nigqual.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, review_period_id)");
        }

        //ARV Refill Devolvement section
        if (table.equals("devolve")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT devolve.devolve_id AS id_on_server, devolve.date_devolved, patient.hospital_num FROM devolve JOIN patient ON devolve.patient_id = patient.patient_id WHERE devolve.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_devolved)");
        }

        //Others
        if (table.equals("validated")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT validated.validated_id AS id_on_server, validated.date_validated, patient.hospital_num FROM validated JOIN patient ON validated.patient_id = patient.patient_id WHERE validated.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, date_validated)");
        }

        //Patient Case manager
        if (table.equals("patientcasemanager")) {
            executeUpdate("CREATE VIEW dependant_" + dbSuffix + " AS SELECT patientcasemanager.patientcasemanager_id AS id_on_server, patientcasemanager.date_assigned, patient.hospital_num FROM patientcasemanager JOIN patient ON patientcasemanager.patient_id = patient.patient_id WHERE patientcasemanager.facility_id = " + facilityId + " AND patient.facility_id = " + facilityId);
//            //executeUpdate("CREATE INDEX idx_dependant ON dependant(hospital_num, assigned_date)");
        }

    }

    public void serialize(long facilityId) {
        String directory = ServletActionContext.getServletContext().getInitParameter("contextPath") + "exchange/sync/";
        String fileName = directory + "entity" + Long.toString(facilityId) + ".ser";
        map = getPatientEntities(facilityId);
        try {
            File file = new File(fileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(map);
            out.close();
            fileOut.close();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }

    public Map<Long, String> deserialize(long facilityId) {
        String directory = ServletActionContext.getServletContext().getInitParameter("contextPath") + "exchange/sync/";
        String fileName = directory + "entity" + facilityId + ".ser";
        map = new HashMap<>();
        try {
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            map = (Map<Long, String>) in.readObject();
            in.close();
            fileIn.close();
            return map;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public Map<Long, String> getPatientEntities(long facilityId) {
        Map<Long, String> map1 = new HashMap<>();
        String query1 = "SELECT DISTINCT patient_id, hospital_num FROM patient WHERE facility_id = " + facilityId;
        ContextProvider.getBean(JdbcTemplate.class).query(query1, rs -> {
            while (rs.next()) {
                map1.put(rs.getLong("patient_id"), rs.getString("hospital_num"));
            }
            return null;
        });
        return map1;
    }

    public Map<Long, String> getChildEntities(long facilityId) {
        Map<Long, String> map1 = new HashMap<>();
        String query1 = "SELECT DISTINCT child_id, reference_num FROM child WHERE facility_id = " + facilityId;
        ContextProvider.getBean(JdbcTemplate.class).query(query1, rs -> {
            while (rs.next()) {
                map1.put(rs.getLong("child_id"), rs.getString("reference_num"));
            }
            return null;
        });
        return map1;
    }

    public Map<Long, String> getAncEntities(long facilityId) {
        Map<Long, String> map1 = new HashMap<>();
        String query1 = "SELECT DISTINCT anc_id, anc_num FROM anc WHERE facility_id = " + facilityId;
        ContextProvider.getBean(JdbcTemplate.class).query(query1, rs -> {
            while (rs.next()) {
                map1.put(rs.getLong("anc_id"), rs.getString("anc_num"));
            }
            return null;
        });
        return map1;
    }

    public long getIdOnServer(String query) {
        Long id = ContextProvider.getBean(JdbcTemplate.class).queryForObject(query, Long.class);
        return id != null ? id : 0;
    }

    public String getHospitalNum(long patientId) {
        String query1 = "SELECT hospital_num FROM entity WHERE patient_id = " + patientId;
        return ContextProvider.getBean(JdbcTemplate.class).queryForObject(query1, String.class);
    }

    public void executeUpdate(String query) {
        ContextProvider.getBean(TransactionTemplate.class).execute(status -> {
            ContextProvider.getBean(JdbcTemplate.class).execute(query);
            return null;
        });
    }

    public void dropEntityTables() {
        try {
            System.out.println("Dropping views for " + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS entity_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS dependant_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS dependant1_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS mapper_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS mapper1_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS motherentity_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS pharm_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS lab_" + dbSuffix);
            executeUpdate("DROP VIEW IF EXISTS inh_" + dbSuffix);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
