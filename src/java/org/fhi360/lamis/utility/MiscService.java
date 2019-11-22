package org.fhi360.lamis.utility;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.fhi360.lamis.dao.jdbc.PharmacyJDBC;
import org.fhi360.lamis.dao.jdbc.RegimenIntrospector;
import org.fhi360.lamis.dao.jdbc.RegimenJDBC;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.service.indicator.FacilityPerformanceService;
import org.fhi360.lamis.service.indicator.IndicatorService;
import org.fhi360.lamis.service.indicator.TreatmentIndicatorService;
import org.springframework.jdbc.core.JdbcTemplate;

public class MiscService {

    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private String query;
    private JDBCUtil jdbcUtil;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public void task() {
        //new QRCodeService().generate("Alex Ugo, B183 Penthouse Estate Lugbe");
        //clinic();
//        new EmailService().send("", "", "", "");
//        byte[] bytes = WebserviceRequestHandlerMobile.getInitializationData();
//        new SyncAnalyzer().analyze()

//new SyncService().startTransaction();
        //new FacilityPerformanceService().process();
//      new CleanupService().updateTrackingOutcome();
//        new TreatmentRetentionConverter().convertExcel();
//        new SmsService().runSchedule();
//        try {
//            new AsyncTaskService().doFingerPrintService();            
//        }
//        catch (Exception exception) {
//            exception.printStackTrace();
//        }
//        log();
//        new RadetDataParser().parse();
 //      new FileMakerConverter().convert();
//       new CleanupService().cleanNullFields();        
//        scramble();
        //testStart();
        //uploadReport();
        //lastCd4();
//   syncFolder();
//        importservice();
//      datim();
//        updateLastRefill();
//ndrlogclear();
// ppr();
performance();
    }

    private void identifiers() {
        System.out.println("Processing");
        String fileName = "C:/LAMIS2/patient.csv";
        String hospitalNum = "";
        String surname = "";
        String otherNames = "";
        String address = "";
        String nextKin = "";
        String addressKin = "";
        String phone = "";
        String phoneKin = "";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    hospitalNum = normalize(row[0]);
                    System.out.println("Cleaning...." + hospitalNum);

                    surname = row[1];
                    otherNames = row[2];
                    address = row[3];
                    nextKin = row[4];
                    addressKin = row[5];
                    phone = row[6];
                    phoneKin = row[7];
                    query = "UPDATE patient SET surname = '" + surname + "', other_names = '" + otherNames + "', address = '" + address + "', next_kin = '" + nextKin + "', address_kin = '" + addressKin + "', phone = '" + phone + "', phone_kin = '" + phoneKin + "' WHERE hospital_num = '" + hospitalNum + "'";
                    executeUpdate(query);
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void uploadReport() {
        try {
            executeUpdate("DROP TABLE IF EXISTS synced");
            executeUpdate("DROP TABLE IF EXISTS expected");
            executeUpdate("DROP TABLE IF EXISTS temp");

            String query1 = "CREATE TEMPORARY TABLE IF NOT EXISTS synced AS SELECT DISTINCT facility_id FROM patient WHERE time_stamp BETWEEN DATEADD('DAY', -30, CURDATE()) AND CURDATE() UNION SELECT DISTINCT facility_id FROM clinic WHERE time_stamp BETWEEN DATEADD('DAY', -30, CURDATE()) AND CURDATE() UNION SELECT DISTINCT facility_id FROM pharmacy WHERE time_stamp BETWEEN DATEADD('DAY', -30, CURDATE()) AND CURDATE() UNION SELECT DISTINCT facility_id FROM laboratory WHERE time_stamp BETWEEN DATEADD('DAY', -30, CURDATE()) AND CURDATE() UNION SELECT DISTINCT facility_id FROM statushistory WHERE time_stamp BETWEEN DATEADD('DAY', -30, CURDATE()) AND CURDATE()";
            executeUpdate(query1);
            String query2 = "CREATE TEMPORARY TABLE IF NOT EXISTS expected AS SELECT DISTINCT facility_id FROM patient";
            executeUpdate(query2);
            String query21 = "CREATE TEMPORARY TABLE IF NOT EXISTS temp (name varchar(90), expected int(3), synced int(3), percentage varchar(20))";
            executeUpdate(query21);

            String query3 = "SELECT state_id, name from state";
            resultSet = executeQuery(query3);
            while (resultSet.next()) {
                Integer count_expected = 0, count_synced = 0;
                Long state_id = resultSet.getLong("state_id");
                String state_name = resultSet.getString("name");

                String query4 = "SELECT COUNT(*) AS sync_count FROM facility where state_id = '" + state_id + "' AND facility_id IN (SELECT facility_id FROM synced)";
                ResultSet rs1 = executeQuery(query4);
                if (rs1.next()) {
                    count_synced = rs1.getInt("sync_count");
                }

                String query5 = "SELECT COUNT(*) AS expected_count FROM facility where state_id = '" + state_id + "' AND facility_id IN (SELECT facility_id FROM expected)";
                ResultSet rs2 = executeQuery(query5);
                if (rs2.next()) {
                    count_expected = rs2.getInt("expected_count");
                }

                double percent = count_synced > 0 ? 100.0 * count_synced / count_expected : 0.0;
                System.out.println("......" + count_synced);
                System.out.println("......" + count_expected);
                System.out.println("......" + percent);

                percent = Math.round(percent * 10.0) / 10.0;  // round percentage to 1 decimal point
                String percentage = percent + "%";

                String query6 = "INSERT INTO TEMP ( name, expected, synced, percentage) VALUES ('" + state_name + "', '" + count_expected + "', '" + count_synced + "', '" + percentage + "')";
                executeUpdate(query6);
            }

            String query7 = "call CSVWRITE('C:/lamis2/sync.csv', 'SELECT * FROM temp')";
            executeUpdate(query7);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void dt() {
        Scrambler scrambler = new Scrambler();
        executeUpdate("DROP TABLE IF EXISTS dt");
        executeUpdate("CREATE TEMPORARY TABLE dt (facility_id bigint, patient_id bigint, hospital_num varchar(100), surname varchar(100), other_names varchar(100), regimentype varchar(100), regimen varchar(100), date_last_regimen date, current_status varchar(100), date_current_status date)");

        System.out.println("Processing");
        String fileName = "C:/LAMIS2/active.csv";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    int facilityId = Integer.parseInt(row[0]);
                    String hospitalNum = normalize(row[1].trim());

                    query = "select * from patient where facility_id = " + facilityId + " and hospital_num = '" + hospitalNum + "'";
                    resultSet = executeQuery(query);
                    if (resultSet.next()) {
                        long patientId = resultSet.getLong("patient_id");
                        String surname = resultSet.getString("surname") == null ? "" : resultSet.getString("surname");
                        surname = scrambler.unscrambleCharacters(surname);
                        String otherNames = resultSet.getString("other_names") == null ? "" : resultSet.getString("other_names");
                        otherNames = scrambler.unscrambleCharacters(otherNames);
                        String currentStatus = resultSet.getString("current_status");
                        String dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "yyyy-MM-dd");
                        System.out.println(".....date: " + dateCurrentStatus);

                        String regimentype = "";
                        String regimen = "";
                        String dateLastRegimen = "";
                        query = new PharmacyJDBC().getLastRefillVisit(patientId);
                        /*
                        jdbcTemplate.query(query, rs -> {
                            long regimentypeId = rs.getLong("regimentype_id");
                            long regimenId = rs.getLong("regimen_id");
                            dateLastRegimen = rs.getObject("date_visit") == null ? "" : DateUtil.parseDateToString(rs.getDate("date_visit"), "yyyy-MM-dd");
                            System.out.println(".....date: " + dateLastRegimen);
                            regimentype = RegimenJDBC.getRegimentype(regimentypeId);
                            regimen = RegimenJDBC.getRegimen(regimenId);
                        });
                        */
                        executeUpdate("insert into dt (facility_id, patient_id, hospital_num, surname, other_names, regimentype, regimen, date_last_regimen, current_status, date_current_status) values(" + facilityId + ", " + patientId + ", '" + hospitalNum + "', '" + surname + "', '" + otherNames + "', '" + regimentype + "', '" + regimen + "', '" + dateLastRegimen + "', '" + currentStatus + "', '" + dateCurrentStatus + "')");
                        System.out.println("Inserted...." + hospitalNum);
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void testStart() {
        executeUpdate("DROP TABLE IF EXISTS dt");
        executeUpdate("CREATE TABLE dt (sno int, facility_id bigint, facility varchar(100), hospital_num varchar(100), regimentype varchar(100), regimen varchar(100), date_last_regimen varchar(15), current_status varchar(100), date_current_status varchar(15))");
        System.out.println("Processing");
        String fileName = "C:/LAMIS2/start.csv";
        String[] row = null;

        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                int sno = 0;
                long facilityId = 0;
                String facility = "";
                String hospitalNum = "";
                String regimentype = "";
                String regimen = "";
                String dateLastRegimen = "";
                String currentStatus = "";
                String dateCurrentStatus = "";

                rowcount++;
                if (rowcount > 1) {
                    sno = Integer.parseInt(row[0]);
                    facility = row[3].trim().replace("'", "");
                    hospitalNum = normalize(row[5].trim().replace("'", ""));
                    String num = row[5].trim().replace("'", "");

                    query = "select facility_id from facility where name like '" + facility + "%'";
                    ResultSet rs = executeQuery(query);
                    if (rs.next()) {
                        facilityId = rs.getLong("facility_id");
                        System.out.println("....... facility id: " + facilityId);

                        query = "select * from patient where facility_id = " + facilityId + " and (hospital_num = '" + hospitalNum + "' or hospital_num = '" + num + "')";
                        resultSet = executeQuery(query);
                        if (resultSet.next()) {
                            long patientId = resultSet.getLong("patient_id");
                            currentStatus = resultSet.getString("current_status");
                            dateCurrentStatus = resultSet.getObject("date_current_status") == null ? "" : DateUtil.parseDateToString(resultSet.getDate("date_current_status"), "yyyy-MM-dd");

                            query = new PharmacyJDBC().getLastRefillVisit(patientId);
                            /*
                            if (pharm.next()) {
                                long regimentypeId = pharm.getLong("regimentype_id");
                                long regimenId = pharm.getLong("regimen_id");
                                dateLastRegimen = pharm.getObject("date_visit") == null ? "" : DateUtil.parseDateToString(pharm.getDate("date_visit"), "yyyy-MM-dd");
                                regimentype = RegimenJDBC.getRegimentype(regimentypeId);
                                regimen = RegimenJDBC.getRegimen(regimenId);
                            }
*/
                        }
                        executeUpdate("insert into dt (sno, facility_id, facility, hospital_num, regimentype, regimen, date_last_regimen, current_status, date_current_status) values(" + sno + ", " + facilityId + ", '" + facility + "', '" + hospitalNum + "', '" + regimentype + "', '" + regimen + "', '" + dateLastRegimen + "', '" + currentStatus + "', '" + dateCurrentStatus + "')");
                        System.out.println("Inserted...." + rowcount);
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String normalize(String hospitalNum) {
        String zeros = "";
        int MAX_LENGTH = 7;
        if (hospitalNum.length() < MAX_LENGTH) {
            for (int i = 0; i < MAX_LENGTH - hospitalNum.length(); i++) {
                zeros = zeros + "0";
            }
        }
        return zeros + hospitalNum;
    }

    private void regimencommence() {
        System.out.println("Processing");
        String fileName = "C:/LAMIS2/original.csv";
        String hospitalNum = "";
        String regimentype = "";
        String regimen = "";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    hospitalNum = normalize(row[0].trim());
                    regimentype = row[1];
                    regimen = row[2];

                    query = "SELECT patient_id FROM patient WHERE hospital_num = '" + hospitalNum + "'";
                    resultSet = executeQuery(query);
                    while (resultSet.next()) {
                        long patientId = resultSet.getLong("patient_id");
                        query = "UPDATE clinic SET regimentype = '" + regimentype + "', regimen = '" + regimen + "' WHERE patient_id = " + patientId + " AND commence = 1";
                        executeUpdate(query);
                        System.out.println("Updated...." + hospitalNum);
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void clinic() {
        System.out.println("Processing");
        String fileName = "C:/LAMIS2/commence.csv";
        long facilityId = 2262;
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    long patientId = Long.parseLong(row[1].trim());
                    String dateVisit = row[3].trim();
                    dateVisit = DateUtil.formatDateString(dateVisit, "MM/dd/yyyy", "yyyy-MM-dd");
                    String clinicStage = row[4];
                    String funcStatus = row[5];
                    Integer cd4 = isInteger(row[8]) ? Integer.parseInt(row[8]) : null;
                    String regimentype = row[10];
                    String regimen = row[11];
                    Integer bodyWeight = isInteger(row[12]) ? Integer.parseInt(row[12]) : null;
                    System.out.println("Body weigt..." + bodyWeight);

                    executeUpdate("UPDATE patient SET date_started = '" + dateVisit + "' WHERE patient_id = " + patientId);
                    query = "SELECT clinic_id FROM clinic WHERE patient_id = " + patientId + " AND date_visit = '" + dateVisit + "'";
                    resultSet = executeQuery(query);
                    System.out.println("Patient Id...." + patientId);
                    System.out.println("Date...." + dateVisit);
                    System.out.println("CD4...." + cd4 + "   regimen..." + regimen);
                    if (resultSet.next()) {
                        long clinicId = resultSet.getLong("clinic_id");
                        executeUpdate("UPDATE clinic SET clinic_stage = '" + clinicStage + "', func_status = '" + funcStatus + "', cd4 = " + cd4 + ", body_weight = " + bodyWeight + ", regimentype = '" + regimentype + "', regimen = '" + regimen + "', commence = 1, time_stamp = NOW() WHERE clinic_id = " + clinicId);
                        System.out.println("Updated....");
                    } else {
                        executeUpdate("INSERT INTO clinic (patient_id, facility_id, date_visit, clinic_stage, func_status, cd4, body_weight, regimentype, regimen, commence, time_stamp) VALUES(" + patientId + ", " + facilityId + ", '" + dateVisit + "', '" + clinicStage + "', '" + funcStatus + "', " + cd4 + ", " + bodyWeight + ", '" + regimentype + "', '" + regimen + "', 1, NOW())");
                        System.out.println("Inserted....");
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    //Fill the regimen at ART commencement with the first ARV dispensed for a client
    private void regimen() {
        try {
            query = "select distinct pharmacy.patient_id, pharmacy.regimentype_id, regimen.description, min(pharmacy.date_visit) from pharmacy inner join regimen on regimen.regimen_id = pharmacy.regimen_id where pharmacy.regimentype_id = 1 or pharmacy.regimentype_id =  3 group by pharmacy.patient_id, pharmacy.regimentype_id, pharmacy.regimen_id";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long regimentypeId = resultSet.getLong("regimentype_id");
                String regimentype = regimentypeId == 1 ? "ART First Line Adult" : "ART First Line Children";
                String regimen = resultSet.getString("description");
                query = "UPDATE clinic SET regimentype = '" + regimentype + "', regimen = '" + regimen + "', time_stamp = now() WHERE patient_id = " + patientId + " AND commence = 1";
                executeUpdate(query);
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void fill() {
        try {
            query = "select patient_id, communitypharm_id from devolve ";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long communitypharmId = resultSet.getLong("communitypharm_id");
                query = "UPDATE patient SET communitypharm_id = " + communitypharmId + ", time_stamp = now() WHERE patient_id = " + patientId;
                executeUpdate(query);
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void lastCd4() {
        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT facility_id FROM facility";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long facilityId = resultSet.getLong("facility_id");
                executeUpdate("DROP TABLE IF EXISTS lab");
                executeUpdate("CREATE TEMPORARY TABLE lab AS SELECT * FROM laboratory WHERE facility_id = " + facilityId + " AND (labtest_id = 1 OR labtest_id = 16)");
                executeUpdate("CREATE INDEX idx_lab ON lab(patient_id)");

                query = "SELECT * FROM patient WHERE facility_id = " + facilityId;
                ResultSet ps = executeQuery(query);
                while (ps.next()) {
                    long patientId = ps.getLong("patient_id");
                    query = "SELECT resultab, resultpc, date_reported FROM lab WHERE patient_id = " + patientId + " AND labtest_id = 16 ORDER BY date_reported DESC LIMIT 1";
                    //query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND date_reported = (SELECT MAX(date_reported) FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 16)"; 
                    ResultSet rs = executeQuery(query);
                    if (rs.next()) {
                        String dateLast = (rs.getDate("date_reported") == null) ? "" : DateUtil.parseDateToString(rs.getDate("date_reported"), "yyyy-MM-dd");
                        String resultab = rs.getString("resultab");
                        Double lastViralLoad = 0.0;
                        if (!dateLast.isEmpty()) {
                            if (isInteger(resultab)) {
                                lastViralLoad = Double.valueOf(resultab);
                            }
                            executeUpdate("UPDATE patient SET last_viral_load = " + lastViralLoad + ", date_last_viral_load = '" + dateLast + "', time_stamp = NOW() WHERE patient_id = " + patientId);
                        }
                    } else {
                        executeUpdate("UPDATE patient SET last_viral_load = null, date_last_viral_load = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                    }

                    query = "SELECT resultab, resultpc, date_reported FROM lab WHERE patient_id = " + patientId + " AND labtest_id = 1 ORDER BY date_reported DESC LIMIT 1";
                    //query = "SELECT resultab, resultpc, date_reported FROM laboratory WHERE patient_id = " + patientId + " AND date_reported = (SELECT MAX(date_reported) FROM laboratory WHERE patient_id = " + patientId + " AND labtest_id = 1)"; 
                    rs = executeQuery(query);
                    if (rs.next()) {
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
                    } else {
                        executeUpdate("UPDATE patient SET last_cd4 = null, last_cd4p = null, date_last_cd4 = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                    }
                }
                System.out.println("Done...." + facilityId);
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void scramble() {
        Scrambler scrambler = new Scrambler();
        try {
            jdbcUtil = new JDBCUtil();
            query = "SELECT * FROM patient";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String surname = resultSet.getString("surname") == null ? "" : resultSet.getString("surname");
                surname = scrambler.scrambleCharacters(surname);
                String otherNames = resultSet.getString("other_names") == null ? "" : resultSet.getString("other_names");
                otherNames = scrambler.scrambleCharacters(otherNames);
                String address = resultSet.getString("address") == null ? "" : resultSet.getString("address");
                address = scrambler.scrambleCharacters(address);
                String phone = resultSet.getString("phone") == null ? "" : resultSet.getString("phone");
                phone = scrambler.scrambleNumbers(phone);
                String nextKin = resultSet.getString("next_kin") == null ? "" : resultSet.getString("next_kin");
                nextKin = scrambler.scrambleCharacters(nextKin);
                String addressKin = resultSet.getString("address_kin") == null ? "" : resultSet.getString("address_kin");
                addressKin = scrambler.scrambleCharacters(addressKin);
                String phoneKin = resultSet.getString("phone_kin") == null ? "" : resultSet.getString("phone_kin");
                phoneKin = scrambler.scrambleNumbers(phoneKin);

                query = "UPDATE patient SET surname = '" + surname + "', other_names = '" + otherNames + "', address = '" + address + "', phone = '" + phone + "', next_kin = '" + nextKin + "', address_kin = '" + addressKin + "', phone_kin = '" + phoneKin + "' WHERE patient_id = " + patientId;
                executeUpdate(query);
                System.out.println("Saved....");
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }

    }

    //Task is the method invoked by the MiscService controller. You can perform any temporary task in this method
    private void importservice() {
        System.out.println("............uploading");
        try {
//          String[] tables = {"monitor", "user", "casemanager", "patient", "clinic", "pharmacy", "laboratory", "adrhistory", "oihistory", "adherehistory", "statushistory", "regimenhistory", "chroniccare", "dmscreenhistory", "tbscreenhistory", "anc", "delivery", "child", "childfollowup", "maternalfollowup", "partnerinformation", "specimen", "eid", "labno", "nigqual", "devolve", "patientcasemanager", "eac"};
            String t = "devolve";
            String[] tables = t.split(",");
            for (String table : tables) {
                String fileName = "C:/LAMIS2/web/exchange/" + table + ".xml";
                if (new File(fileName).exists()) {
                   // new XmlParserDelegator().delegate(table, fileName, "");
                    new File(fileName).delete();
                    //new CleanupService().cleanNullFields(table);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void syncFolder() {
        executeUpdate("DROP TABLE IF EXISTS sync");
        executeUpdate("CREATE TABLE sync (facility_id int, facility_name varchar(100))");
        final String name = "C:/LAMIS2/web/exchange/sync/";
        File directory = new File(name);

        //get all the files from a directory
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isDirectory()) {
                System.out.println(file.getName());
                String facilityId = file.getName();
                try {
                    jdbcUtil = new JDBCUtil();
                    query = "SELECT name FROM facility WHERE facility_id = " + facilityId;
                    resultSet = executeQuery(query);
                    while (resultSet.next()) {
                        String facilityName = resultSet.getString("name");

                        executeUpdate("INSERT INTO sync (facility_id, facility_name) VALUES(" + facilityId + ", '" + facilityName + "')");
                        System.out.println("Inserted....");
                    }
                } catch (Exception exception) {
                    jdbcUtil.disconnectFromDatabase();  //disconnect from database
                }
                executeUpdate("call CSVWRITE('C:/lamis2/sync.csv', 'select * from sync')");
            }
        }

    }

    private void miss() {
        executeUpdate("DROP TABLE IF EXISTS miss");
        executeUpdate("CREATE TABLE miss (facility_name varchar(100))");

        System.out.println("Processing");
        String fileName = "C:/LAMIS2/dat.csv";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    String name = row[0];

                    query = "SELECT facility_id FROM facility WHERE name = '" + name + "' AND datim_id is not null and datim_id != ''";
                    resultSet = executeQuery(query);
                    if (resultSet.next()) {
                        System.out.println("found....");
                    } else {
                        executeUpdate("INSERT INTO miss (facility_name) VALUES('" + name + "')");
                        System.out.println("Inserted....");
                    }
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        executeUpdate("call CSVWRITE('C:/lamis2/miss.csv', 'select * from miss')");
    }

    private void log() {
        executeUpdate("DROP TABLE IF EXISTS log");
        executeUpdate("CREATE TABLE log (facility_id int)");

        System.out.println("Processing");
        String fileName = "C:/LAMIS2/log.csv";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    long id = Long.parseLong(row[0].trim());
                    executeUpdate("INSERT INTO log (facility_id) VALUES(" + id + ")");
                    System.out.println("Inserted....");
                }
            }
            csvReader.close();
            executeUpdate("DROP TABLE IF EXISTS ndrlog");
            executeUpdate("CREATE TEMPORARY TABLE ndrlog AS SELECT * FROM ndrmessagelog WHERE facility_id IN (SELECT facility_id FROM log)");
            executeUpdate("call CSVWRITE('C:/lamis2/ndrlog.csv', 'select * from ndrlog')");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void executeUpdate(String query) {
        try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            preparedStatement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void ndrlogclear() {
        try {
            jdbcUtil = new JDBCUtil();

            query = "SELECT DISTINCT patient_id, facility_id FROM patient";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long facilityId = resultSet.getLong("facility_id");

                executeUpdate("UPDATE ndr SET facility_id = " + facilityId + ", file_name = 'FHI360_20072018_190430.430' WHERE patient_id = " + patientId);
                //executeUpdate("UPADTE INTO ndr SELECT * FROM ndrmessagelog WHERE patient_id = " + patientId + " ORDER BY time_stamp DESC LIMIT 1");                
                System.out.println("Updated....");
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    }

    private void datim() {
        System.out.println("Processing");
        String fileName = "C:/LAMIS2/datim.csv";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    long facilityId = Long.parseLong(row[0].trim());
                    String datimId = row[1];
                    System.out.println("..." + datimId);
                    executeUpdate("UPDATE facility SET datim_id = '" + datimId + "' WHERE facility_id = " + facilityId);
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private void datim1() {
        System.out.println("Processing");
        String fileName = "C:/LAMIS2/facility.csv";
        String[] row = null;
        int rowcount = 0;
        try {
            CSVReader csvReader = new CSVReader(new FileReader(fileName));
            System.out.println("File read..");
            while ((row = csvReader.readNext()) != null) {
                rowcount++;
                if (rowcount > 1) {
                    long facilityId = Long.parseLong(row[0].trim());
                    String datimId = row[1];
                    System.out.println("..." + facilityId);
                    executeUpdate("UPDATE facility SET datim_id = '" + datimId + "' WHERE facility_id = " + facilityId);
                }
            }
            csvReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private void ppr() {
        try {

            executeUpdate("DROP TABLE IF EXISTS ppr");
            executeUpdate("CREATE TEMPORARY TABLE ppr AS SELECT facility_id, regimen, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, COUNT(*) AS total FROM patient WHERE current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND regimen IS NOT NULL GROUP BY facility_id, regimen, age");

            resultSet = executeQuery("SELECT regimen FROM ppr");
            while (resultSet.next()) {
                String regimen = RegimenIntrospector.resolveRegimen(resultSet.getString("regimen"));

                query = "UPDATE ppr SET regimen = '" + regimen + "' WHERE regimen = '" + resultSet.getString("regimen") + "'";
                executeUpdate(query);
            }
            executeUpdate("call CSVWRITE('c:/lamis2/ppr.csv', 'select facility.name, ppr.regimen, ppr.age, ppr.total from ppr join facility on ppr.facility_id = facility.facility_id order by facility.name')");
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();
        }

    }

    private void dr() {

        try {

            String query = "SELECT d.period, d.value, i.description, c.description, f.name FROM dhisvalue d JOIN indicator i ON d.data_element_id = i.data_element_id JOIN categorycomb c ON d.category_id = c.category_id JOIN facility f ON d.facility_id = f.facility_id WHERE d.period IN ('WR', 'WR')";

            executeUpdate("DROP TABLE IF EXISTS dr");
            executeUpdate("CREATE TEMPORARY TABLE dr AS SELECT facility_id, regimen, DATEDIFF(YEAR, date_birth, CURDATE()) AS age, COUNT(*) AS total FROM patient WHERE current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND regimen IS NOT NULL GROUP BY facility_id, regimen, age");

            resultSet = executeQuery("SELECT regimen FROM ppr");
            while (resultSet.next()) {
                String regimen = RegimenIntrospector.resolveRegimen(resultSet.getString("regimen"));

                query = "UPDATE ppr SET regimen = '" + regimen + "' WHERE regimen = '" + resultSet.getString("regimen") + "'";
                executeUpdate(query);
            }
            executeUpdate("call CSVWRITE('c:/lamis2/ppr.csv', 'select facility.name, ppr.regimen, ppr.age, ppr.total from ppr join facility on ppr.facility_id = facility.facility_id order by facility.name')");
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();
        }

    }

    private void dhis() {
        try {
            query = "SELECT DISTINCT facility_id FROM dhisvalue WHERE facility_id IS NULL OR facility_id = ''";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long facilityId = resultSet.getLong("facility_id");
                query = "UPDATE dhisvalue SET dhisvalue.facility_id_dhis = (SELECT DISTINCT dhis_id FROM dhiscodeset WHERE UPPER(dhiscodeset.code_set_nm) = 'FACILITY' AND dhiscodeset.lamis_id = " + facilityId + ") WHERE dhisvalue.facility_id = " + facilityId;
                executeUpdate(query);
            }

            query = "SELECT DISTINCT data_element_id FROM dhisvalue WHERE data_element_id IS NULL OR data_element_id = '' AND LENGTH(period) = 7";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long dataElementId = resultSet.getLong("data_element_id");
                query = "UPDATE dhisvalue SET dhisvalue.data_element_id_dhis = (SELECT DISTINCT dhis_id FROM dhiscodeset WHERE UPPER(dhiscodeset.code_set_nm) = 'DATA ELEMENT WR' AND dhiscodeset.lamis_id = " + dataElementId + ") WHERE dhisvalue.data_element_id = " + dataElementId + " AND LENGTH(dhisvalue.period) = 7";
                executeUpdate(query);
            }

            query = "SELECT DISTINCT data_element_id FROM dhisvalue WHERE data_element_id IS NULL OR data_element_id = '' AND LENGTH(period) = 8";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long dataElementId = resultSet.getLong("data_element_id");
                query = "UPDATE dhisvalue SET dhisvalue.data_element_id_dhis = (SELECT DISTINCT dhis_id FROM dhiscodeset WHERE UPPER(dhiscodeset.code_set_nm) = 'DATA ELEMENT DR' AND dhiscodeset.lamis_id = " + dataElementId + ") WHERE dhisvalue.data_element_id = " + dataElementId + " AND LENGTH(dhisvalue.period) = 8";
                executeUpdate(query);
            }

            query = "SELECT DISTINCT category_id FROM dhisvalue WHERE category_id IS NULL OR category_id = ''";
            resultSet = executeQuery(query);
            while (resultSet.next()) {
                long categoryId = resultSet.getLong("category_id");
                query = "UPDATE dhisvalue SET dhisvalue.category_id_dhis = (SELECT DISTINCT dhis_id FROM dhiscodeset WHERE UPPER(dhiscodeset.code_set_nm) = 'CATEGORY COMBO' AND dhiscodeset.lamis_id = " + categoryId + ") WHERE dhisvalue.category_id = " + categoryId;
                executeUpdate(query);
            }

        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();
        }
    }

    private void performance() {
        System.out.println("Here.............................");
      IndicatorService service = new TreatmentIndicatorService();
        FacilityPerformanceService performanceService = new FacilityPerformanceService(service);
        performanceService.process();
    }
    
    
private void refill() {
        try {
            resultSet = executeQuery("SELECT patient_id, facility_id FROM patient WHERE date_started IS NOT NULL AND current_status NOT IN ('Pre-ART Transfer Out', 'ART Transfer Out', 'Lost to Follow Up', 'Stopped Treatment', 'Known Death') AND TIMESTAMPDIFF(DAY, DATE_ADD(date_last_refill, INTERVAL last_refill_duration DAY), CURDATE()) > 28");
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long facilityId = resultSet.getLong("facility_id");

                query = "SELECT date_visit, regimen_id, regimentype_id, duration, next_appointment FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
                ResultSet rs = executeQuery(query);
                if (rs.next()) {
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
                    rs = executeQuery("SELECT patient_id FROM regimenhistory WHERE patient_id = " + patientId + " AND regimentype = '" + regimentype + "' AND regimen = '" + regimen + "'");
                    if (!rs.next()) {
                        executeUpdate("INSERT INTO regimenhistory (patient_id, facility_id, regimentype, regimen, date_visit, reason_switched_subs, time_stamp) VALUES(" + patientId + ", " + facilityId + ", '" + regimentype + "', '" + regimen + "', '" + dateVisit + "', '', NOW())");
                    }
                } else {
                    executeUpdate("UPDATE patient SET regimentype = '', regimen = '', date_last_refill = null, date_next_refill = null, last_refill_duration = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                    executeUpdate("DELETE FROM regimenhistory WHERE patient_id = " + patientId);
                }
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
    
}
    
    private void updateLastRefill() {
        System.out.println("Updating last refill......");
        try {
            resultSet = executeQuery("SELECT patient_id, facility_id FROM patient");
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                long facilityId = resultSet.getLong("facility_id");

                query = "SELECT date_visit, regimen_id, regimentype_id, duration, next_appointment FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
                ResultSet rs = executeQuery(query);
                if (rs.next()) {
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
                    rs = executeQuery("SELECT patient_id FROM regimenhistory WHERE patient_id = " + patientId + " AND regimentype = '" + regimentype + "' AND regimen = '" + regimen + "'");
                    if (!rs.next()) {
                        executeUpdate("INSERT INTO regimenhistory (patient_id, facility_id, regimentype, regimen, date_visit, reason_switched_subs, time_stamp) VALUES(" + patientId + ", " + facilityId + ", '" + regimentype + "', '" + regimen + "', '" + dateVisit + "', '', NOW())");
                    }
                } else {
                    executeUpdate("UPDATE patient SET regimentype = '', regimen = '', date_last_refill = null, date_next_refill = null, last_refill_duration = null, time_stamp = NOW() WHERE patient_id = " + patientId);
                    executeUpdate("DELETE FROM regimenhistory WHERE patient_id = " + patientId);
                }
//                
//                //Remove second line regimen from log id no second line is found in the dispensing table
//                query = "SELECT regimentype_id FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id IN (2, 4)";
//                rs = executeQuery(query);
//                if(!rs.next()) {
//                    executeUpdate("DELETE FROM regimenhistory WHERE regimen LIKE 'ART Second Line%' AND patient_id = " + patientId);
//                }
//
//                //Remove salvage line regimen from log id no salvage line is found in the dispensing table
//                query = "SELECT regimentype_id FROM pharmacy WHERE patient_id = " + patientId + " AND regimentype_id = 14";
//                rs = executeQuery(query);
//                if(!rs.next()) {
//                    executeUpdate("DELETE FROM regimenhistory WHERE regimen LIKE 'Third Line%' OR regimen LIKE 'Salvage%' AND patient_id = " + patientId);
//                }
            }
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }

    }

    private ResultSet executeQuery(String query) {
        ResultSet rs = null;
        try {
            jdbcUtil = new JDBCUtil();
            preparedStatement = jdbcUtil.getStatement(query);
            rs = preparedStatement.executeQuery();
        } catch (Exception exception) {
            jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        return rs;
    }

    public static boolean isInteger(String s) {
        s = s.replace(".", "").replace(",", "");
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
