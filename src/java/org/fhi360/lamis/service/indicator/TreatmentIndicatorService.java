/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.service.indicator;

import org.fhi360.lamis.service.TreatmentCurrentService;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

import static org.fhi360.lamis.utility.StringUtil.isInteger;

/**
 * @author user10
 */
public class TreatmentIndicatorService implements IndicatorService {

    private IndicatorPersister indicatorPersister = new IndicatorPersister();
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private long stateId;
    private long lgaId;

    private String query;
    //private static final Log log = LogFactory.getLog(ArtSummaryProcessor.class);

    private int agem1, agem2, agem3, agem4, agem5, agem6, agem7, agem8, agem9, agem10, agem11, agem12;
    private int agef1, agef2, agef3, agef4, agef5, agef6, agef7, agef8, agef9, agef10, agef11, agef12;
    private int agem13_1, agem14_1, agem15_1, agem13_2, agem14_2, agem15_2;
    private int agef13_1, agef14_1, agef15_1, agef13_2, agef14_2, agef15_2;
    private int preg, feeding, tbm, tbf;

    private int dataElementId = 0;

    public TreatmentIndicatorService() {
    }

    private void deleteViews(String dbSuffix) {
        executeUpdate("DROP VIEW IF EXISTS clinic_appointment_" + dbSuffix);
        executeUpdate("DROP TABLE IF EXISTS patient_" + dbSuffix);
        executeUpdate("DROP TABLE IF EXISTS clinic_" + dbSuffix);
        executeUpdate("DROP TABLE IF EXISTS pharmacy_" + dbSuffix);
        executeUpdate("DROP TABLE IF EXISTS laboratory_" + dbSuffix);
        executeUpdate("DROP TABLE IF EXISTS statushistory_" + dbSuffix);
    }

    @Override
    public void process(Long facilityId, Date reportingDate, int dataElementId, String dbSuffix) {
        String reportDate = DateUtil.parseDateToString(reportingDate, "yyyy-MM-dd");

        executeUpdate("CREATE OR REPLACE VIEW patient_" + dbSuffix + " AS SELECT * FROM patient WHERE facility_id = " + facilityId);
        executeUpdate("CREATE OR REPLACE VIEW statushistory_" + dbSuffix + " AS SELECT * FROM statushistory WHERE facility_id = " + facilityId);
        executeUpdate("CREATE OR REPLACE VIEW clinic_" + dbSuffix + " AS SELECT * FROM clinic WHERE facility_id = " + facilityId + " AND date_visit <= '" + reportDate + "'");
        executeUpdate("CREATE OR REPLACE VIEW pharmacy_" + dbSuffix + " AS SELECT * FROM pharmacy WHERE facility_id = " + facilityId + " AND date_visit <= '" + reportDate + "' AND regimentype_id IN (1, 2, 3, 4, 14)");
        executeUpdate("CREATE OR REPLACE VIEW laboratory_" + dbSuffix + " AS SELECT * FROM laboratory WHERE facility_id = " + facilityId + " AND date_reported <= '" + reportDate + "' AND labtest_id IN (1, 16)");

        System.out.println("ArtIndicatorService: running report for : " + reportDate);

        getStateId(facilityId); // stateId and lgaId

        switch (dataElementId) {
            case 1:
                indicator1(facilityId, reportDate, dbSuffix);
                break;
            case 6:
                indicator2(facilityId, reportingDate, reportDate, dbSuffix);
                break;
            case 8:
                indicator3(facilityId, reportDate, dbSuffix);
                break;
            case 11:
                indicator4(facilityId, reportingDate, reportDate, dbSuffix);
                break;
            case 17:
                indicator5(facilityId, reportDate, dbSuffix);
                break;
            case 18:
                indicator6(facilityId, reportDate, dbSuffix);
                break;
            case 19:
                indicator7(facilityId, reportDate, dbSuffix);
                break;
            case 20:
                indicator8(facilityId, reportDate, dbSuffix);
                break;
            case 21:
                indicator9(facilityId, reportDate, dbSuffix);
                break;
            case 22:
                indicator10(facilityId, reportDate, dbSuffix);
                break;
            case 23:
                indicator11(facilityId, reportDate, dbSuffix);
                break;
            case 28:
                indicator12(facilityId, reportDate, dbSuffix);
                break;
            case 30:
                indicator13(facilityId, reportDate, dbSuffix);
                break;
            case 35:
                indicator14(facilityId, reportDate, dbSuffix);
        }

        //missedAppointments(facilityId, reportDate);
        //numberOfPatientsWhoReturnedToCare(facilityId, reportingDate, reportDate);

        deleteViews(dbSuffix);
    }

    @Override
    public int[] dataElementIds() {
        return new int[]{1, 6, 8, 11, 17, 18, 19, 20, 21, 22, 23, 28, 30, 35};
    }

    private void disaggregate(String gender, int age) {
        if (gender.trim().equalsIgnoreCase("Male")) {
            if (age < 1) {
                agem1++;
            } else {
                if (age >= 1 && age <= 4) {
                    agem2++;
                } else {
                    if (age >= 5 && age <= 9) {
                        agem3++;
                    } else {
                        if (age >= 10 && age <= 14) {
                            agem4++;
                        } else {
                            if (age >= 15 && age <= 19) {
                                agem5++;
                            } else {
                                if (age >= 20 && age <= 24) {
                                    agem6++;
                                } else {
                                    if (age >= 25 && age <= 29) {
                                        agem7++;
                                    } else {
                                        if (age >= 30 && age <= 34) {
                                            agem8++;
                                        } else {
                                            if (age >= 35 && age <= 39) {
                                                agem9++;
                                            } else {
                                                if (age >= 40 && age <= 44) {
                                                    agem10++;
                                                } else {
                                                    if (age >= 45 && age <= 49) {
                                                        agem11++;
                                                    } else {
                                                        if (age >= 50) {
                                                            agem12++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (age < 1) {
                agef1++;
            } else {
                if (age >= 1 && age <= 4) {
                    agef2++;
                } else {
                    if (age >= 5 && age <= 9) {
                        agef3++;
                    } else {
                        if (age >= 10 && age <= 14) {
                            agef4++;
                        } else {
                            if (age >= 15 && age <= 19) {
                                agef5++;
                            } else {
                                if (age >= 20 && age <= 24) {
                                    agef6++;
                                } else {
                                    if (age >= 25 && age <= 29) {
                                        agef7++;
                                    } else {
                                        if (age >= 30 && age <= 34) {
                                            agef8++;
                                        } else {
                                            if (age >= 35 && age <= 39) {
                                                agef9++;
                                            } else {
                                                if (age >= 40 && age <= 44) {
                                                    agef10++;
                                                } else {
                                                    if (age >= 45 && age <= 49) {
                                                        agef11++;
                                                    } else {
                                                        if (age >= 50) {
                                                            agef12++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(status -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private boolean found(String query) {
        final boolean[] found = {false};
        jdbcTemplate.query(query, rs -> {
            found[0] = true;
        });
        return found[0];
    }

    private void initVariables() {
        agem1 = 0;
        agem2 = 0;
        agem3 = 0;
        agem4 = 0;
        agem5 = 0;
        agem6 = 0;
        agem7 = 0;
        agem8 = 0;
        agem9 = 0;
        agem10 = 0;
        agem11 = 0;
        agem12 = 0;

        agef1 = 0;
        agef2 = 0;
        agef3 = 0;
        agef4 = 0;
        agef5 = 0;
        agef6 = 0;
        agef7 = 0;
        agef8 = 0;
        agef9 = 0;
        agef10 = 0;
        agef11 = 0;
        agef12 = 0;

        agem13_1 = 0;
        agem14_1 = 0;
        agem15_1 = 0;
        agem13_2 = 0;
        agem14_2 = 0;
        agem15_2 = 0;
        agef13_1 = 0;
        agef14_1 = 0;
        agef15_1 = 0;
        agef13_2 = 0;
        agef14_2 = 0;
        agef15_2 = 0;
        preg = 0;
        feeding = 0;
        tbm = 0;
        tbf = 0;
    }

    private void getStateId(long facilityId) {
        query = "SELECT state_id, lga_id FROM facility  WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, rs -> {
            stateId = rs.getLong("state_id");
            lgaId = rs.getLong("lga_id");
        });
    }

    private int getCount(String query) {
        final int[] count = {0};
        jdbcTemplate.query(query, rs -> {
            count[0] = rs.getInt("count");
        });
        return count[0];
    }

    private void indicator1(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing ART1.....");
        //ART 1
        //Total Number of HIV-positive newly enrolled in clinical care during the month (excludes transfer-in)
        initVariables();
        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') "
                + "AS age, pregnant, breastfeeding, tb_status FROM patient_" + dbSuffix + " WHERE date_registration = '"
                + reportDate + "' AND status_registration = 'HIV+ non ART'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    int pregnant = resultSet.getInt("pregnant");
                    int breastfeeding = resultSet.getInt("breastfeeding");
                    String tbStatus = resultSet.getString("tb_status") == null ? "" : resultSet.getString("tb_status");

                    disaggregate(gender, age);
                    if (gender.trim().equalsIgnoreCase("Male")) {
                        //check for TB status during enrolmemnt
                        if (tbStatus.equalsIgnoreCase("Currently on TB treatment") || tbStatus.equalsIgnoreCase("TB positive not on TB drugs")) {
                            tbm++;
                        }
                    } else {
                        //check if client is pregnant or breast feeding during enrolment
                        if (pregnant == 1) {
                            preg++;
                        } else {
                            if (breastfeeding == 1) {
                                feeding++;
                            }
                        }
                        //check for TB status during enrolmemnt
                        if (tbStatus.equalsIgnoreCase("Currently on TB treatment") || tbStatus.equalsIgnoreCase("TB positive not on TB drugs")) {
                            tbf++;
                        }
                    }
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 1
            dataElementId = 1;
            persistDataElements(facilityId, reportDate);

            dataElementId = 2;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, preg, reportDate);

            dataElementId = 3;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, feeding, reportDate);

            dataElementId = 4;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, tbm, reportDate);
            dataElementId = 5;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, tbf, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator2(Long facilityId, Date reportingDate, String reportDate, String dbSuffix) {
        System.out.println("Computing ART2.....");
        //ART 2
        //Total number of people living with HIV who are currently in HIV care who received at least one of the following
        //by the end of the month: clinical assessment(WHO staging) OR CD4 count OR viral load OR current on treatment
        initVariables();

        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age, "
                + "current_status, date_started FROM patient_" + dbSuffix + " WHERE current_status IN ("
                + Constants.ClientStatus.ON_TREATMENT + ") AND date_current_status <= '" + reportDate
                + "' AND date_started IS NOT NULL";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    Date dateStarted = resultSet.getDate("date_started");

                    final int[] pregnant = {0};
                    final int[] breastfeeding = {0};

                    boolean count = false;

                    query = "SELECT DISTINCT patient_id FROM clinic_" + dbSuffix + " WHERE patient_id = " + patientId
                            + " AND date_visit >= DATE_ADD('" + reportDate + "', INTERVAL -6 MONTH) AND date_visit <= '" + reportDate + "'";      // DATE_ADD('" + reportDate + "', INTERVAL -6 MONTH)  //DATEADD('MONTH', -6, '" + reportDate + "')
                    if (found(query)) {
                        count = true;
                    } else {
                        query = "SELECT DISTINCT patient_id FROM laboratory_" + dbSuffix + " WHERE patient_id = " + patientId
                                + " AND date_reported >= DATE_ADD('" + reportDate + "', INTERVAL -6 MONTH) AND date_reported <= '" + reportDate + "'";  // DATE_ADD('" + reportDate + "', INTERVAL -6 MONTH)  //DATEADD('MONTH', -6, '" + reportDate + "')
                        if (found(query)) {
                            count = true;
                        } else {
                            if (dateStarted != null) {
                                if (TreatmentCurrentService.isPatientActive(patientId, Constants.LTFU.PEPFAR, reportingDate, dbSuffix)) {
                                    count = true;
                                }
                            }
                        }
                    }

                    if (count) {
                        disaggregate(gender, age);
                        if (gender.trim().equalsIgnoreCase("Female")) {
                            //check if client is pregnant or breast feeding during enrolment
                            query = "SELECT pregnant, breastfeeding FROM clinic_" + dbSuffix + " WHERE patient_id = "
                                    + patientId + " AND date_visit <= '" + reportDate + "' ORDER BY date_visit DESC LIMIT 1";
                            jdbcTemplate.query(query, rs -> {
                                int cols = rs.getMetaData().getColumnCount();
                                for(int i = 0; i < cols; i++)
                                    System.out.println("Column: " + rs.getMetaData().getColumnName(i));
                                //pregnant[0] = rs.getInt("pregnant");
                                breastfeeding[0] = rs.getInt("breastfeeding");
                            });
                            if (pregnant[0] == 1) {
                                preg++;
                            } else {
                                if (breastfeeding[0] == 1) {
                                    feeding++;
                                }
                            }
                        }
                    }
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 2

            dataElementId = 6;
            persistDataElements(facilityId, reportDate);
            dataElementId = 7;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, preg, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator3(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing ART3.....");
        //ART 3
        //Total number of people living with HIV newly started on ART during the month (excludes ART transfer-in)
        initVariables();

        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_" + dbSuffix
                + " WHERE date_started = '" + reportDate + "' AND status_registration != 'ART Transfer In'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    final int[] pregnant = {0};
                    final int[] breastfeeding = {0};

                    disaggregate(gender, age);

                    if (gender.trim().equalsIgnoreCase("Female")) {
                        //check if client is pregnant or breast feeding during visit
                        query = "SELECT pregnant, breastfeeding FROM clinic_" + dbSuffix + " WHERE patient_id = " + patientId
                                + " AND date_visit = '" + reportDate + "' AND commence = 1 ORDER BY date_visit DESC LIMIT 1";
                        jdbcTemplate.query(query, rs -> {
                            pregnant[0] = rs.getInt("pregnant");
                            breastfeeding[0] = rs.getInt("breastfeeding");
                        });
                        if (pregnant[0] == 1) {
                            preg++;
                        } else {
                            if (breastfeeding[0] == 1) {
                                feeding++;
                            }
                        }
                    }
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 3
            dataElementId = 8;
            persistDataElements(facilityId, reportDate);
            dataElementId = 9;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, preg, reportDate);

            dataElementId = 10;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, feeding, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator4(Long facilityId, Date reportingDate, String reportDate, String dbSuffix) {
        System.out.println("Computing ART4.....");
        //ART 4
        //Total number of people living with HIV who are currently receiving ART during the month (All regimen)
        initVariables();

        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_" + dbSuffix
                + " WHERE current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ")  AND date_current_status <= '"
                + reportDate + "' AND date_started IS NOT NULL";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    final int[] pregnant = {0};
                    final int[] breastfeeding = {0};
                    query = "SELECT DISTINCT regimentype_id, regimen_id, date_visit, duration FROM pharmacy_" + dbSuffix + " WHERE patient_id = "
                            + patientId + " AND regimentype_id IN (1, 2, 3, 4, 14) ORDER BY date_visit DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        while (rs.next()) {
                            Date dateLastRefill = rs.getDate("date_visit");
                            int duration = rs.getInt("duration");
                            int monthRefill = duration / 30;
                            if (monthRefill <= 0) {
                                monthRefill = 1;
                            }

                            if (dateLastRefill != null) {
                                if (TreatmentCurrentService.isPatientActive(patientId, Constants.LTFU.PEPFAR, reportingDate, dbSuffix)) {
                                    disaggregate(gender, age);

                                    long regimentypeId = rs.getLong("regimentype_id");
                                    if (gender.trim().equalsIgnoreCase("Male")) {
                                        if (age < 15) {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agem13_1++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agem14_1++;
                                                } else {
                                                    agem15_1++;
                                                }
                                            }
                                        } else {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agem13_2++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agem14_2++;
                                                } else {
                                                    agem15_2++;
                                                }
                                            }
                                        }
                                    } else {
                                        if (age < 15) {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agef13_1++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agef14_1++;
                                                } else {
                                                    agef15_1++;
                                                }
                                            }
                                        } else {
                                            if (regimentypeId == 1 || regimentypeId == 3) {
                                                agef13_2++;
                                            } else {
                                                if (regimentypeId == 2 || regimentypeId == 4) {
                                                    agef14_2++;
                                                } else {
                                                    agef15_2++;
                                                }
                                            }
                                        }
                                    }

                                    if (gender.trim().equalsIgnoreCase("Female")) {
                                        //check if client is pregnant or breast feeding during enrolment
                                        query = "SELECT pregnant, breastfeeding FROM clinic_" + dbSuffix + " WHERE patient_id = " + patientId
                                                + " AND date_visit >=  DATE_ADD('" + reportDate + "', INTERVAL -6 MONTH) AND date_visit = '" + reportDate + "' ORDER BY date_visit DESC LIMIT 1";      // DATE_ADD('" + reportDate + "', INTERVAL -9 MONTH)
                                        jdbcTemplate.query(query, rs1 -> {
                                            pregnant[0] = rs1.getInt("pregnant");
                                            breastfeeding[0] = rs1.getInt("breastfeeding");
                                        });

                                        if (pregnant[0] == 1) {
                                            preg++;
                                        } else {
                                            if (breastfeeding[0] == 1) {
                                                feeding++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    });
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 4
            dataElementId = 11;
            persistDataElements(facilityId, reportDate);

            dataElementId = 12;
            indicatorPersister.persist(dataElementId, 27, stateId, lgaId, facilityId, agem13_1, reportDate);

            dataElementId = 12;
            indicatorPersister.persist(dataElementId, 28, stateId, lgaId, facilityId, agem13_2, reportDate);

            dataElementId = 12;
            indicatorPersister.persist(dataElementId, 25, stateId, lgaId, facilityId, agef13_1, reportDate);

            dataElementId = 12;
            indicatorPersister.persist(dataElementId, 26, stateId, lgaId, facilityId, agef13_2, reportDate);

            dataElementId = 13;
            indicatorPersister.persist(dataElementId, 27, stateId, lgaId, facilityId, agem14_1, reportDate);

            dataElementId = 13;
            indicatorPersister.persist(dataElementId, 28, stateId, lgaId, facilityId, agem14_2, reportDate);

            dataElementId = 13;
            indicatorPersister.persist(dataElementId, 25, stateId, lgaId, facilityId, agef14_1, reportDate);

            dataElementId = 13;
            indicatorPersister.persist(dataElementId, 26, stateId, lgaId, facilityId, agef14_2, reportDate);

            dataElementId = 14;
            indicatorPersister.persist(dataElementId, 27, stateId, lgaId, facilityId, agem15_1, reportDate);

            dataElementId = 14;
            indicatorPersister.persist(dataElementId, 28, stateId, lgaId, facilityId, agem15_2, reportDate);

            dataElementId = 14;
            indicatorPersister.persist(dataElementId, 25, stateId, lgaId, facilityId, agef15_1, reportDate);

            dataElementId = 14;
            indicatorPersister.persist(dataElementId, 26, stateId, lgaId, facilityId, agef15_2, reportDate);

            dataElementId = 15;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, preg, reportDate);

            dataElementId = 16;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, feeding, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator5(Long facilityId, String reportDate, String dbSuffix) {
        //Number of patients eligible for viral load test during the reporting month
        System.out.println(".....Compute eligible for viral load test");
        initVariables();

        query = "SELECT DISTINCT gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_"
                + dbSuffix + " WHERE current_status IN ('ART Start', 'ART Restart', 'ART Transfer In') AND viral_load_due_date = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age);
                }
                return null;
            });
            //Populate the report parameter map with values computed for Eligible for VL
            dataElementId = 17;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator6(Long facilityId, String reportDate, String dbSuffix) {

        System.out.println("Computing ART5.....");
        //ART 5
        //Number of people living with HIV and on ART with a viral load test result during the month
        initVariables();

        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age "
                + "FROM patient_" + dbSuffix + " WHERE date_registration = '" + reportDate
                + "' AND date_started IS NOT NULL AND TIMESTAMPDIFF(MONTH, date_started, '" + reportDate + "') >= 3 AND date_started = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    //Check for viral load this reporting month
                    query = "SELECT patient_id FROM laboratory_" + dbSuffix + " WHERE patient_id = " + patientId
                            + " AND date_reported = '" + reportDate + "' AND labtest_id = 16 ORDER BY date_reported DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        disaggregate(gender, age);
                    });
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 5
            dataElementId = 18;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator7(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing ART7.....");
        //ART 7
        //Total number of people living with HIV who are virologically suppressed
        initVariables();
        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_" + dbSuffix
                + " WHERE date_registration <= '" + reportDate + "' AND date_started IS NOT NULL AND TIMESTAMPDIFF(MONTH, date_started, '"
                + reportDate + "') >= 3 AND date_started = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    //Check if the last viral load before the reporting month is less than 1000
                    query = "SELECT * FROM laboratory_" + dbSuffix + " WHERE patient_id = " + patientId + " AND date_reported = '"
                            + reportDate + "' AND labtest_id = 16 ORDER BY date_reported DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        String resultab = rs.getString("resultab");
                        if (isInteger(resultab)) {
                            if (Double.valueOf(resultab) < 1000) {
                                disaggregate(gender, age);
                            }
                        }
                    });
                }
                return null;
            });
            dataElementId = 19;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator8(Long facilityId, String reportDate, String dbSuffix) {
        //Number of people living with HIV and on ART who are virologically unsuppressed (viral load <>=1000 c/ml) during the month
        initVariables();

        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_" + dbSuffix
                + " WHERE date_registration <= '" + reportDate + "' AND date_started IS NOT NULL AND TIMESTAMPDIFF(MONTH, date_started, '"
                + reportDate + "') >= 3 AND date_started = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    query = "SELECT * FROM laboratory_" + dbSuffix + " WHERE patient_id = " + patientId + " AND date_reported = '"
                            + reportDate + "' AND labtest_id = 16 ORDER BY date_reported DESC LIMIT 1";
                    jdbcTemplate.query(query, rs -> {
                        String resultab = rs.getString("resultab");
                        if (isInteger(resultab)) {
                            if (Double.valueOf(resultab) >= 1000) {
                                disaggregate(gender, age);
                            }
                        }
                    });
                }
                return null;
            });
            dataElementId = 20;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator9(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing ART6.....");
        //ART 7
        //Total number of people living with HIV known to have died during the month
        initVariables();

        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_" + dbSuffix + ""
                + " WHERE current_status = 'Known Death' AND date_current_status = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age);
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 6
            dataElementId = 21;
            indicatorPersister.persist(dataElementId, 13, stateId, lgaId, facilityId, agem1, reportDate);         // male
            indicatorPersister.persist(dataElementId, 1, stateId, lgaId, facilityId, agef1, reportDate);        //female
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator10(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing ART8.....");
        //ART 8
        //Number of People living with HIV who are lost to follow up during the month
        initVariables();
        query = "SELECT DISTINCT gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient_"
                + dbSuffix + " WHERE patient_id IN (SELECT DISTINCT patient_id FROM statushistory_" + dbSuffix + " WHERE facility_id = "
                + facilityId + " AND current_status = 'Lost to Follow Up' AND date_current_status = '" + reportDate + "')";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    if (gender.trim().equalsIgnoreCase("Male")) {
                        agem1++;
                    } else {
                        agef1++;
                    }
                }
                return null;
            });

            dataElementId = 22;
            indicatorPersister.persist(dataElementId, 13, stateId, lgaId, facilityId, agem1, reportDate);         // male
            indicatorPersister.persist(dataElementId, 1, stateId, lgaId, facilityId, agef1, reportDate);        //female
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator11(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("......Computing TB documentation");
        //Clinic visit with documented TB status
        //denominator - all clinic visits during the reporting month
        initVariables();
        try {
            query = "SELECT COUNT(*) AS count FROM clinic_" + dbSuffix + " WHERE date_visit =  '" + reportDate + "'";
            dataElementId = 23;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, getCount(query), reportDate);        //Total clinic visti
        } catch (Exception e) {
            e.printStackTrace();
        }
        //numerator - all clinic visit during the reporting month with TB status not equal to null
        initVariables();
        try {
            query = "SELECT COUNT(*) AS count FROM clinic_" + dbSuffix + " WHERE date_visit =  '" + reportDate
                    + "' AND tb_status != '' AND tb_status IS NOT NULL";
            dataElementId = 24;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, getCount(query), reportDate);        //Total clinic visti
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator12(Long facilityId, String reportDate, String dbSuffix) {
        initVariables();
        //Number of people living with HIV newly initiated on/transitioned to TLD during the month
        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM "
                + "patient_" + dbSuffix + " WHERE facility_id = " + facilityId + " AND  date_started = '" + reportDate + "' AND  regimen LIKE '%DTG%'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age);
                }
                return null;
            });
            dataElementId = 28;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator13(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("......Computing Patient on DMOC");
        initVariables();
        //Number of people living with HIV on DMOC during the month
        int[] mms = {0};
        int[] mmd = {0};
        int[] carc = {0};
        int[] cparp = {0};

        query = "SELECT DISTINCT p.patient_id, p.gender, TIMESTAMPDIFF(YEAR, p.date_birth, '"
                + reportDate + "') AS age, d.type_dmoc FROM patient_" + dbSuffix
                + " p JOIN devolve d ON p.patient_id = d.patient_id WHERE d.date_devolved = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    long patientId = resultSet.getLong("patient_id");
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    String typeDmoc = resultSet.getString("type_dmoc");
                    if (typeDmoc.equalsIgnoreCase("MMS")) {
                        mms[0]++;
                    }
                    if (typeDmoc.equalsIgnoreCase("MMD")) {
                        mmd[0]++;
                    }
                    if (typeDmoc.equalsIgnoreCase("CARC")) {
                        carc[0]++;
                    }
                    if (typeDmoc.equalsIgnoreCase("CPARP")) {
                        cparp[0]++;
                    }
                    disaggregate(gender, age);
                }
                return null;
            });

            dataElementId = 30;
            persistDataElements(facilityId, reportDate);

            dataElementId = 31;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, mms[0], reportDate);         // mms

            dataElementId = 32;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, mmd[0], reportDate);        //mmd

            dataElementId = 33;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, carc[0], reportDate);         // carc

            dataElementId = 34;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, cparp[0], reportDate);        //cparp
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void indicator14(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing ART1.....");
        //ART 1
        //Total Number of HIV-positive newly enrolled in clinical care during the month (excludes transfer-in)
        initVariables();
        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') "
                + "AS age FROM patient_" + dbSuffix + " WHERE date_registration <= '"
                + reportDate + "' AND status_registration = 'HIV+ non ART'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");

                    disaggregate(gender, age);
                }
                return null;
            });
            //Populate the report parameter map with values computed for ART 1
            dataElementId = 35;
            persistDataElements(facilityId, reportDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void missedAppointments(Long facilityId, String reportDate) {
        //Missed appointment
        query = "CREATE OR REPLACE VIEW missed AS SELECT patient_id, date_visit, next_appointment, gender, age FROM appoint WHERE patient_id NOT IN (SELECT patient_id FROM pharm WHERE date_visit =  '" + reportDate + "')";
        executeUpdate(query);

        System.out.println("......Computing number of patient who returned into care");
        query = "SELECT * FROM missed";
        jdbcTemplate.query(query, rs -> {
            while (rs.next()) {
                long patientId = rs.getLong("patient_id");
                String gender = rs.getString("gender");
                int age = rs.getInt("age");

                String nextAppointment = DateUtil.parseDateToString(rs.getDate("next_appointment"), "yyyy-MM-dd");

                Date date = DateUtil.addDay(rs.getDate("next_appointment"), 30);
                String dateEnd = DateUtil.parseDateToString(date, "yyyy-MM-dd");

                System.out.println(".... Next Appointment Database value:  " + rs.getDate("next_appointment"));
                System.out.println(".... Next Appointment " + nextAppointment);

                //Number  of patients who missed apointment and returned to care
                query = "SELECT patient_id FROM pharm WHERE patient_id = " + patientId + " AND  (date_visit >= " + nextAppointment + "  AND date_visit <=  " + dateEnd + ")";
                jdbcTemplate.query(query, rs1 -> {
                    System.out.println(".... ResultSet query successful ");
                    disaggregate(gender, age);
                });
            }
            return null;
        });

    }

    private void numberOfPatientsWhoReturnedToCare(Long facilityId, Date reportingDate, String reportDate) {
        System.out.println("......Computing number of patient who returned into care2");

        //Populate number of patient who returned into care
        dataElementId = 48;
        persistDataElements(facilityId, reportDate);

        //Number of people living with HIV newly initiated on/transitioned to TLD during the month
        query = "SELECT DISTINCT patient_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM patient " +
                "WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") " +
                "AND TIMESTAMPDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) <= " + Constants.LTFU.PEPFAR +
                " AND date_started IS NOT NULL ORDER BY current_status";
        jdbcTemplate.query(query, resultSet -> {
            while (resultSet.next()) {
                long patientId = resultSet.getLong("patient_id");
                String gender = resultSet.getString("gender");
                int age = resultSet.getInt("age");

                query = "SELECT DISTINCT regimentype_id, regimen_id, date_visit, duration FROM pharm WHERE patient_id = "
                        + patientId + " ORDER BY date_visit DESC LIMIT 1";
                jdbcTemplate.query(query, rs -> {
                    while (rs != null && rs.next()) {
                        long regimenId = rs.getLong("regimen_id");
                        Date dateLastRefill = rs.getDate("date_visit");
                        int duration = rs.getInt("duration");
                        int monthRefill = duration / 30;
                        if (monthRefill <= 0) {
                            monthRefill = 1;
                        }

                        if (dateLastRefill != null) {
                            //If the last refill date plus refill duration plus 28 days is before the last day of the reporting month this patient is LTFU     if(DateUtil.addYearMonthDay(lastRefill, duration+90, "day(s)").before(reportingDateEnd
                            if (!DateUtil.addYearMonthDay(dateLastRefill, duration + Constants.LTFU.PEPFAR, "DAY").after(reportingDate)) {

                                if (regimenId >= 116 && regimenId <= 119) {
                                    disaggregate(gender, age);
                                }
                            }
                        }
                    }
                    return null;
                });
            }
            return null;
        });
        dataElementId = 49;
        persistDataElements(facilityId, reportDate);
    }

    private void persistDataElements(Long facilityId, String reportDate) {
        indicatorPersister.persist(dataElementId, 13, stateId, lgaId, facilityId, agem1, reportDate);      // male <1
        indicatorPersister.persist(dataElementId, 1, stateId, lgaId, facilityId, agef1, reportDate);        //female <1

        indicatorPersister.persist(dataElementId, 14, stateId, lgaId, facilityId, agem2, reportDate);         // male 1-4
        indicatorPersister.persist(dataElementId, 2, stateId, lgaId, facilityId, agef2, reportDate);        //female 1-4

        indicatorPersister.persist(dataElementId, 15, stateId, lgaId, facilityId, agem3, reportDate);         // male  5-9
        indicatorPersister.persist(dataElementId, 3, stateId, lgaId, facilityId, agef3, reportDate);        //female 5-9

        indicatorPersister.persist(dataElementId, 16, stateId, lgaId, facilityId, agem4, reportDate);         // male 10-14
        indicatorPersister.persist(dataElementId, 4, stateId, lgaId, facilityId, agef4, reportDate);        //female 10-14

        indicatorPersister.persist(dataElementId, 17, stateId, lgaId, facilityId, agem5, reportDate);         // male 15-19
        indicatorPersister.persist(dataElementId, 5, stateId, lgaId, facilityId, agef5, reportDate);        //female 15-19

        indicatorPersister.persist(dataElementId, 18, stateId, lgaId, facilityId, agem6, reportDate);         // male 20-24
        indicatorPersister.persist(dataElementId, 6, stateId, lgaId, facilityId, agef6, reportDate);        //female 20-24

        indicatorPersister.persist(dataElementId, 19, stateId, lgaId, facilityId, agem7, reportDate);         // male 25-29
        indicatorPersister.persist(dataElementId, 7, stateId, lgaId, facilityId, agef7, reportDate);        //female 25-29

        indicatorPersister.persist(dataElementId, 20, stateId, lgaId, facilityId, agem8, reportDate);         // male 30-34
        indicatorPersister.persist(dataElementId, 8, stateId, lgaId, facilityId, agef8, reportDate);        //female 30-34

        indicatorPersister.persist(dataElementId, 21, stateId, lgaId, facilityId, agem9, reportDate);         // male 35-39
        indicatorPersister.persist(dataElementId, 9, stateId, lgaId, facilityId, agef9, reportDate);        //female 35-39

        indicatorPersister.persist(dataElementId, 22, stateId, lgaId, facilityId, agem10, reportDate);         // male 40-44
        indicatorPersister.persist(dataElementId, 10, stateId, lgaId, facilityId, agef10, reportDate);        //female 40-44

        indicatorPersister.persist(dataElementId, 23, stateId, lgaId, facilityId, agem11, reportDate);         // male 45-49
        indicatorPersister.persist(dataElementId, 11, stateId, lgaId, facilityId, agef11, reportDate);        //female 45-49

        indicatorPersister.persist(dataElementId, 24, stateId, lgaId, facilityId, agem12, reportDate);        // male 50+
        indicatorPersister.persist(dataElementId, 12, stateId, lgaId, facilityId, agef12, reportDate);        //female 50+
    }
}
