/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.service;

import java.util.Date;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class DeleteService {

    private String query;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public DeleteService() {
    }

    public synchronized void deletePatient(long facilityId, long patientId) {
        System.out.printf("Delete patient: %s, %s\n", facilityId, patientId);
        //String[] tables = {"ENCOUNTER", "DEVOLVE", "DRUGTHERAPY", "APPOINTMENT",  "MHTC", "VALIDATED","PATIENTCASEMANAGER","EAC","partnerinformation", "dmscreenhistory", "tbscreenhistory", "child", "maternalfollowup", "delivery", "anc", "chroniccare", "adrhistory", "oihistory", "adherehistory", "statushistory", "regimenhistory", "clinic", "pharmacy", "laboratory", "patient"};
        String[] tables = {"ENCOUNTER", "DEVOLVE", "DRUGTHERAPY", "APPOINTMENT", "VALIDATED", "PATIENTCASEMANAGER", "EAC", "partnerinformation", "dmscreenhistory", "tbscreenhistory", "child", "maternalfollowup", "delivery", "anc", "chroniccare", "adrhistory", "oihistory", "adherehistory", "statushistory", "regimenhistory", "clinic", "pharmacy", "laboratory", "PRESCRIPTION", "patient"};
        for (String table : tables) {
            if (table.equalsIgnoreCase("child")) {
                executeUpdate("DELETE FROM childfollowup WHERE child_id IN (SELECT child_id FROM child WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + ")");
            }
            executeUpdate("DELETE FROM " + table + " WHERE facility_id = " + facilityId + " AND patient_id = " + patientId);
        }
        executeUpdate("DELETE FROM biometric where patient_id = (SELECT id_uuid FROM patient where patient_id = " + patientId + ")");
    }

    public synchronized void deleteClinic(long facilityId, long patientId, Date date) {
        System.out.println("Deleting clinic visit");
        deleteAdr(facilityId, patientId, date, 1);
        deleteOi(facilityId, patientId, date);
        query = "DELETE FROM clinic WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deletePharmacy(long facilityId, long patientId, Date date) {
        deleteAdr(facilityId, patientId, date, 2);
        deleteAdhere(facilityId, patientId, date);

        query = "DELETE FROM pharmacy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteLaboratory(long facilityId, long patientId, Date date) {
        query = "DELETE FROM laboratory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_reported = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteHts(long facilityId, long htsId, long assessmentId) {
        executeUpdate("DELETE FROM indexcontact WHERE facility_id = " + facilityId + " AND hts_id = " + htsId);
        executeUpdate("DELETE FROM hts WHERE facility_id = " + facilityId + " AND hts_id = " + htsId);
        executeUpdate("DELETE FROM assessment WHERE facility_id = " + facilityId + " AND assessment_id = " + assessmentId);

    }

    public synchronized void deleteIndexcontact(long facilityId, long indexcontactId) {
        executeUpdate("DELETE FROM indexcontact WHERE facility_id = " + facilityId + " AND indexcontact_id = " + indexcontactId);

    }

    public synchronized void deleteStatus(long facilityId, long patientId, String currentStatus, Date date) {
        query = "DELETE FROM statushistory WHERE facility_id = " + facilityId + 
                " AND patient_id = " + patientId + " AND current_status = '" + currentStatus + 
                "' AND date_current_status = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteOi(long facilityId, long patientId, Date date) {
        query = "DELETE FROM oihistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteAdr(long facilityId, long patientId, Date date, int screener) {
        query = "DELETE FROM adrhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "' AND screener = " + screener;
        executeUpdate(query);
    }

    public synchronized void deleteAdhere(long facilityId, long patientId, Date date) {
        query = "DELETE FROM adherehistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteChroniccare(long facilityId, long patientId, Date date) {
        deleteTbscreen(facilityId, patientId, date);
        deleteDmscreen(facilityId, patientId, date);

        query = "DELETE FROM chroniccare WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteTbscreen(long facilityId, long patientId, Date date) {
        query = "DELETE FROM tbscreenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteDmscreen(long facilityId, long patientId, Date date) {
        query = "DELETE FROM dmscreenhistory WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteSpecimen(long facilityId, String labno) {
        query = "DELETE FROM specimen WHERE facility_id = " + facilityId + " AND labno = '" + labno + "'";
        executeUpdate(query);
        query = "DELETE FROM eid WHERE facility_id = " + facilityId + " AND labno = '" + labno + "'";
        executeUpdate(query);
    }

    public synchronized void deleteAnc(long facilityId, long ancId) {
        query = "DELETE FROM partnerinformation WHERE facility_id = " + facilityId + " AND anc_id = " + ancId;
        executeUpdate(query);
        query = "DELETE FROM childfollowup WHERE facility_id = " + facilityId + " AND child_id IN (SELECT child_id FROM child WHERE facility_id = " + facilityId + " AND anc_id = " + ancId + ")";
        executeUpdate(query);
        query = "DELETE FROM maternalfollowup WHERE facility_id = " + facilityId + " AND anc_id = " + ancId;
        executeUpdate(query);
        query = "DELETE FROM child WHERE facility_id = " + facilityId + " AND anc_id = " + ancId;
        executeUpdate(query);
        query = "DELETE FROM delivery WHERE facility_id = " + facilityId + " AND anc_id = " + ancId;
        executeUpdate(query);
        query = "DELETE FROM anc WHERE facility_id = " + facilityId + " AND anc_id = " + ancId;
        executeUpdate(query);
    }

    public synchronized void deleteDelivery(long facilityId, long deliveryId) {
        query = "DELETE FROM childfollowup WHERE facility_id = " + facilityId + " AND child_id IN (SELECT child_id FROM child WHERE facility_id = " + facilityId + " AND delivery_id = " + deliveryId + ")";
        executeUpdate(query);
        query = "DELETE FROM child WHERE facility_id = " + facilityId + " AND delivery_id = " + deliveryId;
        executeUpdate(query);
        query = "DELETE FROM delivery WHERE facility_id = " + facilityId + " AND delivery_id = " + deliveryId;
        executeUpdate(query);
    }

    public synchronized void deleteMaternalfollowup(long facilityId, long maternalfollowupId) {
        query = "DELETE FROM maternalfollowup WHERE facility_id = " + facilityId + " AND maternalfollowup_id = " + maternalfollowupId;
        executeUpdate(query);
    }

    public synchronized void deleteChildfollowup(long facilityId, long childfollowupId) {
        query = "DELETE FROM childfollowup WHERE facility_id = " + facilityId + " AND childfollowup_id = " + childfollowupId;
        executeUpdate(query);
    }

    public synchronized void deleteChild(long facilityId, long childId) {
        String[] tables = {"childfollowup", "child"};
        for (String table : tables) {
            query = "DELETE FROM " + table + " WHERE facility_id = " + facilityId + " AND child_id = " + childId;
            executeUpdate(query);
        }
    }

    public synchronized void deleteEac(long facilityId, long eacId) {
        query = "DELETE FROM eac WHERE facility_id = " + facilityId + " AND eac_id = " + eacId;
        executeUpdate(query);
    }

    public synchronized void deleteDevolve(long facilityId, long devolveId) {
        query = "DELETE FROM devolve WHERE facility_id = " + facilityId + " AND devolve_id = " + devolveId;
        executeUpdate(query);
    }

    public synchronized void deleteNigqual(long facilityId, int reviewPeriodId) {
        query = "DELETE FROM nigqual WHERE facility_id = " + facilityId + " AND review_period_id = " + reviewPeriodId;
        executeUpdate(query);
    }

    public synchronized void deleteEncounter(long facilityId, long patientId, Date date) {
        query = "DELETE FROM encounter WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);

        query = "DELETE FROM pharmacy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteDrugtherapy(long facilityId, long patientId, Date date) {
        query = "DELETE FROM drugtherapy WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_visit = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteAppointment(long facilityId, long patientId, Date date) {
        query = "DELETE FROM appointment WHERE facility_id = " + facilityId + " AND patient_id = " + patientId + " AND date_tracked = '" + DateUtil.parseDateToString(date, "yyyy-MM-dd") + "'";
        executeUpdate(query);
    }

    public synchronized void deleteMhtc(long communitypharmId, int month, int year) {
        query = "DELETE FROM mhtc WHERE communitypharm_id = " + communitypharmId + " AND month = " + month + " AND year = " + year;
        executeUpdate(query);
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            System.out.printf("Executing [%s]\n", query);
            try{
            jdbcTemplate.execute(query);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }

}
