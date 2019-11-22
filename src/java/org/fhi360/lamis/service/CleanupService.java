/**
 *
 * @author user1
 */
package org.fhi360.lamis.service;

import org.fhi360.lamis.utility.DateUtil;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

public class CleanupService {

    private String query;
    private long facilityId;
    private long userId;
    private final static JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final static TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    public void cleanupData() {
        facilityId = (Long) ServletActionContext.getRequest().getSession().getAttribute("facilityId");
        userId = (Long) ServletActionContext.getRequest().getSession().getAttribute("userId");
        cleanNullFields();
        //removeNullFields();
        commencementDate();
        //removeDuplicates();
        updateLastClinic();
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "check1");
        updateLastRefill();
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "check2");
        //updateLastLab();
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "check3");
        updateViralLoadDue();
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "check4");
       // updateStatus();
        ServletActionContext.getRequest().getSession().setAttribute("processingStatus", "completed");
        //updateTrackingOutcome();
        System.out.println("Done......");
    }

    public void cleanNullFields() {
        String[] tables = {"patient", "clinic", "pharmacy", "laboratory", "adrhistory", "oihistory", "adherehistory", "statushistory", "regimenhistory", "chroniccare", "anc", "delivery", "maternalfollowup", "child", "childfollowup", "partnerinformation", "dmscreenhistory", "tbscreenhistory", "specimen", "eid", "communitypharm", "devolve", "validated", "eac"};
        for (String table : tables) {
            cleanNullFields(table);
        }
    }

    private void removeNullFields() {
        executeUpdate("UPDATE patient SET enrollment_setting = 'Facility' WHERE date_started < '2015-06-01'");
        executeUpdate("UPDATE patient SET regimen = '' WHERE regimentype = '' OR regimentype IS NULL");
        executeUpdate("UPDATE clinic SET regimen = '' WHERE regimentype = '' OR regimentype IS NULL");
        executeUpdate("DELETE FROM pharmacy WHERE duration IS NULL");
        executeUpdate("DELETE FROM laboratory WHERE resultab = '' AND resultpc = ''");
        executeUpdate("UPDATE laboratory SET resultab = REPLACE(resultab, ',', '.') WHERE labtest_id = 1 OR labtest_id = 16");
        executeUpdate("UPDATE laboratory SET resultpc = REPLACE(resultpc, ',', '.') WHERE labtest_id = 1 OR labtest_id = 16");
    }

    public void cleanNullFields(String table, long id) {
        facilityId = id;
        cleanNullFields(table);
    }

    private void cleanNullFields(String table) {
        if (table.equals("patient")) {
            String[] fields = {"unique_id", "enrollment_setting", "surname", "other_names", "marital_status", "education", "occupation", "address", "phone", "state", "lga", "next_kin", "address_kin", "phone_kin", "relation_kin", "entry_point", "tb_status", "regimentype", "regimen", "last_clinic_stage", "outcome", "last_refill_setting"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("clinic")) {
            String[] fields = {"clinic_stage", "func_status", "tb_status", "regimentype", "regimen", "bp", "oi_screened", "oi_ids", "adr_screened", "adr_ids", "adherence_level", "adhere_ids", "notes"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("pharmacy")) {
            String[] fields = {"adr_screened", "adr_ids"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("laboratory")) {
            String[] fields = {"labno", "resultab", "resultpc", "comment"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("adrhistory")) {
            String[] fields = {"adr"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("oihistory")) {
            String[] fields = {"oi"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("adherehistory")) {
            String[] fields = {"reason"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("statushistory")) {
            String[] fields = {"current_status", "reason_interrupt"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("regimenhistory")) {
            String[] fields = {"regimentype", "regimen", "reason_switched_subs"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("chroniccare")) {
            String[] fields = {"client_type", "current_status", "clinic_stage", "pregnancy_status", "eligible_cd4", "eligible_viral_load", "ipt", "inh", "tb_treatment", "tb_referred", "eligible_ipt", "tb_values", "bmi", "muac_pediatrics", "muac_pregnant", "supplementary_food", "nutritional_status_referred", "gbv1", "gbv1_referred", "gbv2", "gbv2_referred", "hypertensive", "first_hypertensive", "bp_above", "bp_referred", "diabetic", "first_diabetic", "dm_referred", "dm_values", "phdp1", "phdp1_services_provided", "phdp2", "phdp2_services_provided", "phdp3", "phdp3_services_provided", "phdp4", "phdp4_services_provided", "phdp5", "phdp5_services_provided", "phdp6_services_provided", "phdp7_services_provided", "phdp8", "phdp8_services_provided", "phdp9_services_provided", "reproductive_intentions1", "reproductive_intentions1_referred", "reproductive_intentions2", "reproductive_intentions2_referred", "reproductive_intentions3", "reproductive_intentions3_referred", "malaria_prevention1", "malaria_prevention1_referred", "malaria_prevention2", "malaria_prevention2_referred"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("motherinformation")) {
            autoPopulateTable();
            //String[] fields = {"unique_id", "source_referral", "time_hiv_diagnosis", "arv_regimen_past", "arv_regimen_current", "clinic_stage", "func_status", "cd4_ordered"};
            //for(String field : fields) executeUpdate(table, field);                
        }
        if (table.equals("anc")) {
            String[] fields = {"unique_id", "source_referral", "time_hiv_diagnosis", "arv_regimen_past", "arv_regimen_current", "clinic_stage", "func_status", "cd4_ordered"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("delivery")) {
            String[] fields = {"rom_delivery_interval", "mode_delivery", "episiotomy", "vaginal_tear", "maternal_outcome", "time_hiv_diagnosis", "arv_regimen_past", "arv_regimen_current", "clinic_stage", "cd4_ordered"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("maternalfollowup")) {
            String[] fields = {"bp", "fetal_presentation", "time_hiv_diagnosis", "arv_regimen_past", "arv_regimen_current", "syphilis_tested", "syphilis_test_result", "syphilis_treated", "cd4_ordered", "family_planning_method"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("child")) {
            String[] fields = {"hospital_num", "surname", "other_names", "gender", "status"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("childfollowup")) {
            String[] fields = {"feeding", "arv", "cotrim", "reason_pcr", "pcr_result", "rapid_test", "rapid_test_result", "caregiver_given_result", "child_outcome", "referred"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("partnerinformation")) {
            String[] fields = {"partner_notification", "partner_hiv_status", "partner_referred"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("dmscreenhistory")) {
            String[] fields = {"description", "value"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("tbscreenhistory")) {
            String[] fields = {"description", "value"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("specimen")) {
            String[] fields = {"barcode", "result", "reason_no_test", "hospital_num", "surname", "other_names", "gender", "age_unit", "address", "phone"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("eid")) {
            String[] fields = {"mother_name", "mother_address", "mother_phone", "sender_name", "sender_designation", "sender_address", "sender_phone", "reason_pcr", "rapid_test_done", "rapid_test_result", "mother_art_received", "mother_prophylax_received", "child_prophylax_received", "breastfed_ever", "feeding_method", "breastfed_now", "cotrim"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
        if (table.equals("devolve")) {
            String[] fields = {"viral_load_assessed", "cd4_assessed", "last_clinic_stage", "arv_dispensed", "regimentype", "regimen", "notes", "reason_discontinued"};
            for (String field : fields) {
                executeUpdate(table, field);
            }
        }
    }

    private void executeUpdate(String table, String field) {
        query = "UPDATE [table] SET [field] = '' WHERE [field] IS NULL AND facility_id = " + facilityId;
        query = query.replace("[table]", table).replace("[field]", field);
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private void commencementDate() {
        System.out.println("Resolving ART commencement......");
        //Set any clinic visit with date_vist the same as date_started in patient table
        //executeUpdate("MERGE into patient (patient_id, date_started) KEY(patient_id) SELECT patient_id, date_visit FROM clinic WHERE commence = 1");
        String query = "SELECT clinic_id, patient_id, date_visit FROM clinic "
                + "WHERE facility_id = " + facilityId + " AND commence = 1";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long clinicId = resultSet.getLong("clinic_id");
                long patientId = resultSet.getLong("patient_id");
                String dateStarted = DateUtil.parseDateToString(resultSet.getDate("date_visit"), "yyyy-MM-dd");
                executeUpdate("UPDATE patient SET date_started = '" + dateStarted + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                executeUpdate("DELETE FROM clinic WHERE patient_id = " + patientId + " AND date_visit = '" + dateStarted + "' AND clinic_id != " + clinicId);
            }
            return null;
        });

        //If patient has ART start date but no ART commencement record delete date of ART start
        query = "SELECT patient_id, hospital_num, date_started FROM patient "
                + "WHERE facility_id = " + facilityId + " AND date_started IS NOT NULL";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String dateStarted = DateUtil.parseDateToString(resultSet.getDate("date_started"), "yyyy-MM-dd");

                String query1 = "SELECT clinic_id FROM clinic WHERE patient_id = "
                        + patientId + " AND date_visit = '" + dateStarted + "'";
                boolean[] found = {false
                };
                jdbcTemplate.query(query1, rs -> {
                    found[0] = true;
                    long clinicId = rs.getLong("clinic_id");
                    executeUpdate("UPDATE clinic SET commence = 1, time_stamp = NOW() WHERE patient_id = " + patientId + " AND clinic_id = " + clinicId);
                    executeUpdate("UPDATE clinic SET commence = 0, time_stamp = NOW() WHERE patient_id = " + patientId + " AND clinic_id != " + clinicId);
                });
                if (!found[0]) {
                    executeUpdate("UPDATE patient SET date_started = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                    executeUpdate("UPDATE patient SET current_status = status_registration WHERE patient_id = " + patientId + " AND current_status NOT IN ('Lost to Follow Up', 'Stopped Treatment', 'Known Death', 'ART Transfer Out', 'Pre-ART Transfer Out')");
                    executeUpdate("DELETE FROM statushistory WHERE patient_id = " + patientId + " AND date_current_status = '" + dateStarted + "'");
                }
            }
            return null;
        });

    }

    private void removeDuplicates() {
        //Revove duplicate visits
        String query = "SELECT DISTINCT clinic_id, patient_id, date_visit "
                + "FROM clinic WHERE facility_id = " + facilityId + " AND commence = 0";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long clinicId = resultSet.getLong("clinic_id");
                String dateVisit = DateUtil.parseDateToString(resultSet.getDate("date_visit"), "yyyy-MM-dd");
                String query1 = "DELETE FROM clinic WHERE patient_id = " + patientId + " AND date_visit = '" + dateVisit + "' AND commence = 0 AND clinic_id != " + clinicId;
                executeUpdate(query1);
            }
            return null;
        });
        //Remove duplicate statuses
        query = "SELECT DISTINCT history_id, patient_id, current_status, "
                + "date_current_status FROM statushistory WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long historyId = resultSet.getLong("history_id");
                String dateVisit = DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "yyyy-MM-dd");
                String currentStatus = resultSet.getString("current_status");
                String query1 = "DELETE FROM statushistory WHERE patient_id = " + patientId + " AND current_status = '" + currentStatus + "' AND date_current_status = '" + dateVisit + "' AND history_id != " + historyId;
                executeUpdate(query1);
            }
            return null;
        });

        //Remove duplicate regimen
        query = "SELECT DISTINCT history_id, patient_id, regimen, date_"
                + "visit FROM regimenhistory WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long historyId = resultSet.getLong("history_id");
                String dateVisit = DateUtil.parseDateToString(resultSet.getDate("date_visit"), "yyyy-MM-dd");
                String regimen = resultSet.getString("regimen");
                String query1 = "DELETE FROM regimenhistory WHERE patient_id = "
                        + patientId + " AND regimen = '" + regimen + "' AND date_visit = '"
                        + dateVisit + "' AND history_id != " + historyId;
                executeUpdate(query1);
            }
            return null;
        });
    }

    private void updateLastClinic() {
        String query = "SELECT patient_id FROM patient WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String query1 = "SELECT date_visit, clinic_stage, next_appointment "
                        + "FROM clinic WHERE patient_id = " + patientId
                        + " ORDER BY date_visit DESC LIMIT 1";
                boolean[] found = {false};
                jdbcTemplate.query(query1, rs -> {
                    found[0] = true;
                    String dateVisit = DateUtil.parseDateToString(rs.getDate("date_visit"), "yyyy-MM-dd");
                    String nextAppointment = (rs.getDate("next_appointment") == null) ? "" : DateUtil.parseDateToString(rs.getDate("next_appointment"), "yyyy-MM-dd");
                    String lastClinicStage = (rs.getString("clinic_stage") == null) ? "" : rs.getString("clinic_stage");
                    if (!nextAppointment.isEmpty()) {
                        executeUpdate("UPDATE patient SET last_clinic_stage = '" + lastClinicStage + "', date_last_clinic = '" + dateVisit + "', date_next_clinic = '" + nextAppointment + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                    } else {
                        executeUpdate("UPDATE patient SET last_clinic_stage = '" + lastClinicStage + "', date_last_clinic = '" + dateVisit + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                    }
                });
                if (!found[0]) {
                    executeUpdate("UPDATE patient SET last_clinic_stage = '', date_last_clinic = null, date_next_clinic = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                }
            }
            return true;
        });
    }

    private void updateLastRefill() {
        String query = "SELECT patient_id FROM patient WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                boolean[] found = {false};
                String query1 = "SELECT date_visit, regimen_id, regimentype_id, duration, next_appointment FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
                jdbcTemplate.query(query1, rs -> {
                    found[0] = true;
                    String dateVisit = DateUtil.parseDateToString(rs.getDate("date_visit"), "yyyy-MM-dd");
                    int duration = rs.getInt("duration");
                    String nextAppointment = (rs.getDate("next_appointment") == null) ? "" : DateUtil.parseDateToString(rs.getDate("next_appointment"), "yyyy-MM-dd");
                    if (nextAppointment.isEmpty()) {
                        nextAppointment = DateUtil.parseDateToString(DateUtil.addDay(rs.getDate("date_visit"), rs.getInt("duration")), "yyyy-MM-dd");
                    }
                    String regimentype = RegimenJDBC.getRegimentype(rs.getLong("regimentype_id"));
                    String regimen = RegimenJDBC.getRegimen(rs.getLong("regimen_id"));
                    executeUpdate("UPDATE patient SET regimentype = '" + regimentype + "', regimen = '" + regimen + "', date_last_refill = '" + dateVisit + "', date_next_refill = '" + nextAppointment + "', last_refill_duration = " + duration + ", time_stamp = NOW() WHERE patient_id = " + patientId);

                    //check if the last regimen was logged and log into the regimen history table if not
                    String q1 = "SELECT patient_id FROM regimenhistory WHERE patient_id = "
                            + patientId + " AND regimentype = '" + regimentype
                            + "' AND regimen = '" + regimen + "'";
                    boolean[] execute = {true};
                    jdbcTemplate.query(q1, rs1 -> {
                        execute[0] = false;
                    });
                    if (execute[0]) {
                        executeUpdate("INSERT INTO regimenhistory (patient_id, facility_id, regimentype, regimen, date_visit, reason_switched_subs, time_stamp) VALUES(" + patientId + ", " + facilityId + ", '" + regimentype + "', '" + regimen + "', '" + dateVisit + "', '', NOW())");
                    }
                });
                if (!found[0]) {
                    executeUpdate("UPDATE patient SET regimentype = '', regimen = '', date_last_refill = null, date_next_refill = null, last_refill_duration = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                    executeUpdate("DELETE FROM regimenhistory WHERE patient_id = " + patientId);
                }

                //Remove second line regimen from log id no second line is found in the dispensing table
                boolean[] execute = {true};
                String query2 = "SELECT regimentype_id FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id IN (2, 4)";
                jdbcTemplate.query(query2, rs -> {
                    execute[0] = false;
                });
                if (execute[0]) {
                    executeUpdate("DELETE FROM regimenhistory WHERE regimen LIKE 'ART Second Line%' AND patient_id = " + patientId);
                }

                //Remove salvage line regimen from log id no salvage line is found in the dispensing table
                execute[0] = true;
                String query3 = "SELECT regimentype_id FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id = 14";
                jdbcTemplate.query(query2, rs -> {
                    execute[0] = false;
                });
                if (execute[0]) {
                    executeUpdate("DELETE FROM regimenhistory WHERE regimen LIKE 'Third Line%' OR regimen LIKE 'Salvage%' AND patient_id = " + patientId);
                }
            }
            return null;
        });
    }

    private void updateLastLab() {
        String query = "SELECT patient_id, facility_id FROM patient WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String query2 = "SELECT resultab, resultpc, date_reported FROM "
                        + "laboratory WHERE patient_id = " + patientId
                        + " AND labtest_id = 16 ORDER BY date_reported DESC LIMIT 1";
                boolean[] executed = {false};
                //query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND date_reported = (SELECT MAX(date_reported) FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 16)"; 
                jdbcTemplate.query(query2, rs -> {
                    executed[0] = true;
                    String dateLast = (rs.getDate("date_reported") == null) ? "" : DateUtil.parseDateToString(rs.getDate("date_reported"), "yyyy-MM-dd");
                    String resultab = rs.getString("resultab");
                    Double lastViralLoad = 0.0;
                    if (!dateLast.isEmpty()) {
                        if (isInteger(resultab)) {
                            lastViralLoad = Double.valueOf(resultab);
                        }
                        executeUpdate("UPDATE patient SET last_viral_load = " + lastViralLoad + ", date_last_viral_load = '" + dateLast + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                    }
                });
                if (!executed[0]) {
                    executeUpdate("UPDATE patient SET last_viral_load = null, date_last_viral_load = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                }

                String query1 = "SELECT resultab, resultpc, date_reported FROM "
                        + "laboratory WHERE patient_id = " + patientId
                        + " AND labtest_id = 1 ORDER BY date_reported DESC LIMIT 1";
                //query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND date_reported = (SELECT MAX(date_reported) FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 1)"; 
                executed[0] = false;
                jdbcTemplate.query(query1, rs -> {
                    executed[0] = true;
                    String dateLast = (rs.getDate("date_reported") == null) ? "" : DateUtil.parseDateToString(rs.getDate("date_reported"), "yyyy-MM-dd");
                    String resultab = rs.getString("resultab");
                    String resultpc = rs.getString("resultpc");
                    Double lastCd4 = 0.0;
                    if (!dateLast.isEmpty()) {
                        if (isInteger(resultab)) {
                            lastCd4 = Double.valueOf(resultab);
                            executeUpdate("UPDATE patient SET last_cd4 = " + lastCd4 + ", date_last_cd4 = '" + dateLast + "' , time_stamp = NOW() WHERE patient_id = " + patientId);
                        } else {
                            if (isInteger(resultpc)) {
                                lastCd4 = Double.valueOf(resultpc);
                                executeUpdate("UPDATE patient SET last_cd4p = " + lastCd4 + ", date_last_cd4 = '" + dateLast + "' , time_stamp = NOW() WHERE patient_id = " + patientId);
                            }
                        }
                    }
                });
                if (!executed[0]) {
                    executeUpdate("UPDATE patient SET last_cd4 = null, last_cd4p = null, date_last_cd4 = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                }
            }
            return null;
        });
    }

    //VIral Load Due    
    private void updateViralLoadDue() {
        String query = "SELECT patient_id, facility_id, date_started FROM patient "
                + "WHERE facility_id = " + facilityId + " AND current_status IN "
                + "('ART Start', 'ART Restart', 'ART Transfer In') AND date_started IS NOT NULL";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                new ViralLoadMontiorService().updateViralLoadDue(resultSet.getLong("patient_id"),
                        DateUtil.parseDateToString(resultSet.getDate("date_started"), "yyyy-MM-dd"));
            }
            return null;
        });
    }

    private void updateStatus() {
        String query = "SELECT patient_id, date_started, current_status, "
                + "date_current_status FROM patient WHERE facility_id = "
                + facilityId + " AND date_started IS NOT NULL AND current_status "
                + "IN ('HIV+ non ART', 'Pre-ART Transfer In', 'Pre-ART Transfer Out')";
        jdbcTemplate.query(query, resultSet1 -> {
            while (resultSet1.next()) {
                long patientId = resultSet1.getLong("patient_id");
                String dateCurrentStatus = (resultSet1.getDate("date_current_status") == null) ? ""
                        : DateUtil.parseDateToString(resultSet1.getDate("date_current_status"), "yyyy-MM-dd");
                if (resultSet1.getString("current_status").equals("Pre-ART Transfer Out")) {
                    executeUpdate("UPDATE patient SET current_status = 'ART Transfer Out', time_stamp = NOW() WHERE patient_id = " + patientId);

                    //Check if status at ART Transfer Out was logged
                    String q = "SELECT patient_id FROM statushistory WHERE patient_id = "
                            + patientId + " AND current_status = 'ART Transfer Out' "
                            + "AND date_current_status = '" + dateCurrentStatus + "'";
                    boolean[] executed = {false};
                    jdbcTemplate.query(q, rs -> {
                        executed[0] = true;
                        executeUpdate("UPDATE statushistory SET current_status = "
                                + "'ART Transfer Out', time_stamp = NOW() WHERE patient_id = "
                                + patientId + " AND current_status = 'Pre-ART Transfer Out'");
                    });
                    if (!executed[0]) {
                        executeUpdate("INSERT INTO statushistory (patient_id, facility_id, current_status, date_current_status, time_stamp) SELECT patient_id, facility_id, 'ART Transfer Out', date_current_status, NOW() FROM patient WHERE patient_id = " + patientId);
                    }
                } else {
                    executeUpdate("UPDATE patient SET current_status = 'ART Start', date_current_status = date_started, time_stamp = NOW() WHERE patient_id = " + patientId);
                }
            }

            //Check for if client restarted ART after loss, stopped or transfer out
            String q2 = "SELECT patient_id, date_last_refill FROM patient WHERE "
                    + "facility_id = " + facilityId + " AND date_started IS NOT "
                    + "NULL AND current_status IN ('Lost to Follow Up', "
                    + "'Stopped Treatment', 'ART Transfer Out') AND date_last_refill "
                    + "> date_current_status";
            jdbcTemplate.query(q2, resultSet0 -> {
                while (resultSet0.next()) {
                    long patientId = resultSet0.getLong("patient_id");
                    String dateLastRefill = (resultSet0.getDate("date_last_refill") == null) ? ""
                            : DateUtil.parseDateToString(resultSet0.getDate("date_last_refill"), "yyyy-MM-dd");

                    executeUpdate("UPDATE patient SET current_status = 'ART Restart', date_current_status = date_last_refill, time_stamp = NOW() WHERE patient_id = " + patientId);
                    //Check if status at restart was logged
                    String q3 = "SELECT patient_id FROM statushistory WHERE patient_id = "
                            + patientId + " AND current_status = 'ART Restart' AND "
                            + "date_current_status = '" + dateLastRefill + "'";
                    boolean[] execute = {true};
                    jdbcTemplate.query(q3, rSet -> {
                        execute[0] = false;
                    });
                    if (execute[0]) {
                        executeUpdate("INSERT INTO statushistory (patient_id, facility_id, current_status, date_current_status, reason_interrupt, time_stamp) SELECT patient_id, facility_id, current_status, date_current_status, '', NOW() FROM patient WHERE patient_id = " + patientId);
                    }
                }

                String query3 = "SELECT * FROM patient WHERE facility_id = " + facilityId;
                jdbcTemplate.query(query3, resultSet -> {
                    while (resultSet.next()) {
                        long patientId = resultSet.getLong("patient_id");
                        String dateRegistration = (resultSet.getDate("date_registration") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_registration"), "yyyy-MM-dd");
                        String statusRegistration = resultSet.getString("status_registration") == null ? "" : resultSet.getString("status_registration");
                        String currentStatus = resultSet.getString("current_status") == null ? "" : resultSet.getString("current_status");
                        String dateCurrentStatus = (resultSet.getDate("date_current_status") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "yyyy-MM-dd");
                        String dateStarted = (resultSet.getDate("date_started") == null) ? "" : DateUtil.parseDateToString(resultSet.getDate("date_started"), "yyyy-MM-dd");

                        if (currentStatus.isEmpty()) {
                            executeUpdate("UPDATE patient SET current_status = status_registration, date_current_status = date_registration, time_stamp = NOW() WHERE patient_id = " + patientId);
                            currentStatus = statusRegistration;
                            dateCurrentStatus = dateRegistration;
                            if (!dateStarted.isEmpty() && !statusRegistration.equalsIgnoreCase("ART Transfer In")) {
                                executeUpdate("UPDATE patient SET current_status = 'ART Start', date_current_status = date_started, time_stamp = NOW() WHERE patient_id = " + patientId);
                            }
                        }

                        //Check if status at registration was logged
                        String q4 = "SELECT patient_id FROM statushistory WHERE patient_id = "
                                + patientId + " AND current_status IN ('HIV+ non ART', "
                                + "'ART Transfer In', 'Pre-ART Transfer In') "
                                + "AND date_current_status = '" + dateRegistration + "'";
                        boolean[] execute = {true};
                        jdbcTemplate.query(q4, r1 -> {
                            execute[0] = false;
                        });
                        if (execute[0]) {
                            executeUpdate("INSERT INTO statushistory (patient_id, facility_id, current_status, date_current_status, reason_interrupt, time_stamp) SELECT patient_id, facility_id, status_registration, date_registration, '', NOW() FROM patient WHERE patient_id = " + patientId);
                        }

                        //Cleanup ART start status
                        if (!dateStarted.isEmpty()) {
                            //Check if status at ART start was logged
                            execute[0] = true;
                            String q5 = "SELECT patient_id FROM statushistory WHERE "
                                    + "patient_id = " + patientId + " AND current_status = "
                                    + "'ART Start' AND date_current_status = '" + dateStarted + "'";
                            jdbcTemplate.query(q5, r1 -> {
                                execute[0] = false;
                            });
                            if (execute[0]) {
                                executeUpdate("INSERT INTO statushistory (patient_id, facility_id, current_status, date_current_status, time_stamp) SELECT patient_id, facility_id, 'ART Start', date_started, NOW() FROM patient WHERE patient_id = " + patientId);
                            }
                        }

                        //Check if current status was logged
                        execute[0] = true;
                        String q6 = "SELECT patient_id FROM statushistory WHERE "
                                + "patient_id = " + patientId + " AND date_current_status = '"
                                + dateCurrentStatus + "'";
                        jdbcTemplate.query(q6, r1 -> {
                            execute[0] = false;
                        });
                        if (execute[0]) {
                            executeUpdate("INSERT INTO statushistory (patient_id, facility_id, current_status, date_current_status, time_stamp) SELECT patient_id, facility_id, current_status, date_current_status, NOW() FROM patient WHERE patient_id = " + patientId);
                        }

                        //Check if regimen is logged
                        if (!dateStarted.isEmpty()) {
                            String q7 = "SELECT patient_id FROM regimenhistory WHERE "
                                    + "patient_id = " + patientId + " AND date_visit = '"
                                    + dateStarted + "'";
                            execute[0] = true;
                            jdbcTemplate.query(q7, r1 -> {
                                execute[0] = false;
                            });
                            if (execute[0]) {
                                executeUpdate("INSERT INTO regimenhistory (patient_id, facility_id, regimentype, regimen, date_visit, time_stamp) SELECT patient_id, facility_id, regimentype, regimen, date_visit, NOW() FROM clinic WHERE patient_id = " + patientId + " AND regimentype IS NOT NULL AND commence = 1");
                            }
                        }
                    }
                    return null;
                });
                return null;
            });
            return null;
        });
    }

    public void updateTrackingOutcome() {
        String query = "SELECT * FROM patient";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");

                //Reverse engineer the current status of patient                
                String query1 = "SELECT * FROM statushistory WHERE patient_id = " + patientId + " AND outcome IS NOT NULL ORDER BY date_current_status DESC LIMIT 1";
                jdbcTemplate.query(query, rs -> {
                    String outcome = rs.getString("outcome") == null ? "" : rs.getString("outcome");
                    String agreedDate = (rs.getDate("agreed_date") == null) ? "" : DateUtil.parseDateToString(rs.getDate("agreed_date"), "yyyy-MM-dd");
                    String causeDeath = rs.getString("cause_death") == null ? "" : rs.getString("cause_death");
                    String dateTracked = (rs.getDate("date_tracked") == null) ? "" : DateUtil.parseDateToString(rs.getDate("date_tracked"), "yyyy-MM-dd");

                    executeUpdate("UPDATE patient SET outcome = '" + outcome
                            + "', agreed_date = " + rs.getDate("agreed_date") + ", cause_death = '"
                            + causeDeath + "', date_tracked = " + rs.getDate("date_tracked")
                            + ", time_stamp = NOW() WHERE patient_id = " + patientId);
                });
            }
            executeUpdate("UPDATE patient SET outcome = 'Did Not Attempt to Trace Patient' WHERE (outcome IS  NULL OR outcome = '') AND CURDATE() > date_next_refill");
            return null;
        });
    }

    public void updateTimestampField(String table, long facilityId) {
        executeUpdate("UPDATE exchange SET " + table + " = NOW() WHERE facility_id = " + facilityId);
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private void autoPopulateTable() {
        String query = "SELECT * FROM motherinformation WHERE facility_id = " + facilityId;
        boolean[] execute = {true};
        jdbcTemplate.query(query, resultSet -> {
            execute[0] = false;
        });
        if (execute[0]) {
            query = "INSERT INTO motherinformation (patient_id, facility_id, hospital_num, unique_id, surname, other_names, date_started, date_confirmed_hiv, time_hiv_diagnosis, date_enrolled_pmtct, in_facility, regimen, user_id) SELECT patient_id, facility_id, hospital_num, unique_id, surname, other_names, date_started, date_confirmed_hiv, time_hiv_diagnosis, date_enrolled_pmtct, 'Yes', regimen, user_id FROM PATIENT WHERE gender = 'Female' AND facility_id = " + facilityId;
            executeUpdate(query);
        }
    }

    private boolean isInteger(String s) {
        int radix = 10;
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }
}
