package org.fhi360.lamis.service.indicator;

import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.DateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * @author user10
 */
public class HtsIndicatorService implements IndicatorService {

    private long stateId;
    private long lgaId;

    private String query;
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);

    private IndicatorPersister indicatorPersister = new IndicatorPersister();

    private int agem1, agem2, agem3, agem4, agem5, agem6, agem7, agem8, agem9, agem10, agem11, agem12;
    private int agef1, agef2, agef3, agef4, agef5, agef6, agef7, agef8, agef9, agef10, agef11, agef12;

    private int dataElementId = 0;

    public HtsIndicatorService() {
    }

    private void deleteViews(String dbSuffix) {
        executeUpdate("DROP TABLE IF EXISTS hts_" + dbSuffix);
    }

    @Override
    public void process(Long facilityId, Date reportingDate, int dataElementId, String dbSuffix) {
        String reportDate = DateUtil.parseDateToString(reportingDate, "yyyy-MM-dd");

        executeUpdate("CREATE OR REPLACE VIEW hts_" + dbSuffix + " AS SELECT * FROM hts WHERE facility_id = " + facilityId);

        System.out.println("HtsIndicatorService: running report for : " + reportDate);

        getStateId(facilityId); // stateId and lgaId

        switch (dataElementId) {
            case 101:
                indicator1(facilityId, reportDate, dbSuffix);
                break;
            case 102:
                indicator2(facilityId, reportDate, dbSuffix);
                break;
            case 103:
                indicator3(facilityId, reportDate, dbSuffix);
                break;
            case 104:
                indicator4(facilityId, reportDate, dbSuffix);
                break;
            case 105:
                indicator5(facilityId, reportDate, dbSuffix);
                break;
            case 106:
                indicator6(facilityId, reportDate, dbSuffix);
                break;
        }


        System.out.println("Completed");

        deleteViews(dbSuffix);
    }

    @Override
    public int[] dataElementIds() {
        return new int[]{101, 102, 103, 104, 105, 106};
    }

    public void indicator1(Long facilityId, String reportDate, String dbSuffix) {
        //Compute values for HTS total client tested
        System.out.println("Computing HTS Total Client Tested.....");
        initVariables();

        query = "SELECT DISTINCT hts_id, gender, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') AS age FROM hts_"
                + dbSuffix + " WHERE date_visit = '" + reportDate + "'";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age);
                }
                return null;
            });

            //Populate indicatorvalue with values computed for HTS Total client tested
            dataElementId = 101;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indicator2(Long facilityId, String reportDate, String dbSuffix) {
        // Compute values for HTS total client tested positive
        System.out.println("Computing HTS Total Client Tested Positive.....");
        initVariables();

        query = "SELECT DISTINCT hts_id, gender, hiv_test_result, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "') " +
                "AS age FROM hts_" + dbSuffix + " WHERE UPPER(hiv_test_result) = " +
                "'POSITIVE' AND date_visit = '" + reportDate + "' ";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age);
                }
                return null;
            });

            //Populate indicatorvalue table with values computed for HTS total client tested positive
            dataElementId = 102;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indicator3(Long facilityId, String reportDate, String dbSuffix) {
        //Compute values for HTS total client enrolled
        System.out.println("Computing HTS Total Client Enrolled in ART.....");
        initVariables();

        query = "SELECT DISTINCT hts_id, gender, date_started, TIMESTAMPDIFF(YEAR, date_birth, '" +
                reportDate + "') AS age FROM hts_" + dbSuffix + " WHERE date_started = '" + reportDate + "' ";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    disaggregate(gender, age);
                }
                return null;
            });

            //Populate the report parameter map with values computed for HTS total client enrolled
            dataElementId = 103;
            persistDataElements(facilityId, reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indicator4(Long facilityId, String reportDate, String dbSuffix) {
        //Compute values for HTS total client referred by settings
        System.out.println("Computing HTS Total Client Referred by Settings.....");
        initVariables();

        final int[] ct = {0};
        final int[] tb = {0};
        final int[] sti = {0};
        final int[] opd = {0};
        final int[] ward = {0};
        final int[] community = {0};
        final int[] standalone = {0};
        final int[] others = {0};

        query = "SELECT DISTINCT hts_id, gender, testing_setting, TIMESTAMPDIFF(YEAR, date_birth, '" +
                reportDate + "') AS age FROM hts_" + dbSuffix + " WHERE date_visit = '" + reportDate + "' ";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String gender = resultSet.getString("gender");
                    int age = resultSet.getInt("age");
                    String referredFrom = resultSet.getString("testing_setting");
                    if (referredFrom.equalsIgnoreCase("CT")) {
                        ct[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("TB")) {
                        tb[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("STI")) {
                        sti[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("OPD")) {
                        opd[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("WARD")) {
                        ward[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("Community")) {
                        community[0]++;
                    }

                    if (referredFrom.equalsIgnoreCase("Standalone Hts")) {
                        standalone[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("Others")) {
                        others[0]++;
                    }
                }
                return null;
            });

            //Populate the report parameter map with values computed for HTS total client testing settings
            //CT, tb, sti, opd, ward, community, standalone hts, others
            dataElementId = 104;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, ct[0], reportDate);         // ct
            dataElementId = 105;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, tb[0], reportDate);        //tb
            dataElementId = 106;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, sti[0], reportDate);        //sti
            dataElementId = 107;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, opd[0], reportDate);        //opd
            dataElementId = 108;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, ward[0], reportDate);        //ward
            dataElementId = 109;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, community[0], reportDate);        //community
            dataElementId = 110;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, standalone[0], reportDate);        //standalone hts
            dataElementId = 111;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, others[0], reportDate);        //tb
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indicator5(Long facilityId, String reportDate, String dbSuffix) {
        System.out.println("Computing HTS Total Client Referred From.....");
        initVariables();
        final int[] tb = {0};
        final int[] sti = {0};
        final int[] blood = {0};
        final int[] opd = {0};
        final int[] fp = {0};
        final int[] ward = {0};
        final int[] others = {0};
        final int[] self = {0};
        tb[0] = 0;
        sti[0] = 0;
        opd[0] = 0;
        ward[0] = 0;
        others[0] = 0;

        query = "SELECT DISTINCT hts_id, gender, referred_from, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate +
                "') AS age FROM hts_" + dbSuffix + " WHERE  date_visit = '" + reportDate + "' ";
        try {
            jdbcTemplate.query(query, resultSet -> {
                while (resultSet.next()) {
                    String referredFrom = resultSet.getString("referred_from");
                    if (referredFrom.equalsIgnoreCase("SELF")) {
                        self[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("TB")) {
                        tb[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("STI")) {
                        sti[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("FP")) {
                        fp[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("OPD")) {
                        opd[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("WARD")) {
                        ward[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("Blood bank")) {
                        blood[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("Others")) {
                        others[0]++;
                    }
                }
                return null;
            });

            //Referred settings
            //self, tb, sti, fp, opd, ward, blood bank, others
            dataElementId = 112;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, self[0], reportDate);
            dataElementId = 113;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, tb[0], reportDate);
            dataElementId = 114;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, sti[0], reportDate);
            dataElementId = 115;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, fp[0], reportDate);
            dataElementId = 116;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, opd[0], reportDate);
            dataElementId = 117;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, ward[0], reportDate);
            dataElementId = 118;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, blood[0], reportDate);
            dataElementId = 119;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, others[0], reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indicator6(Long facilityId, String reportDate, String dbSuffix) {
        //Index testing
        // biological, sexual, social
        initVariables();
        final int[] biological = {0};
        final int[] sexual = {0};
        final int[] social = {0};
        query = "SELECT DISTINCT hts_id, gender, type_index, TIMESTAMPDIFF(YEAR, date_birth, '" + reportDate + "')" +
                " AS age FROM hts_" + dbSuffix + " WHERE date_visit = '" + reportDate + "' ";
        try {
            jdbcTemplate.query(query, rs -> {
                while (rs.next()) {
                    String referredFrom = rs.getString("type_index");
                    if (referredFrom.equalsIgnoreCase("Biological")) {
                        biological[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("Sexual")) {
                        sexual[0]++;
                    }
                    if (referredFrom.equalsIgnoreCase("Social")) {
                        social[0]++;
                    }

                }
                return null;
            });
            dataElementId = 120;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, biological[0], reportDate);
            dataElementId = 121;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, sexual[0], reportDate);
            dataElementId = 122;
            indicatorPersister.persist(dataElementId, 0, stateId, lgaId, facilityId, social[0], reportDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void getStateId(long facilityId) {
        query = "SELECT state_id, lga_id FROM facility  WHERE facility_id = " + facilityId;
        jdbcTemplate.query(query, rs -> {
            stateId = rs.getLong("state_id");
            lgaId = rs.getLong("lga_id");
        });
    }

    private void executeUpdate(String query) {
        transactionTemplate.execute(ts -> {
            jdbcTemplate.execute(query);
            return null;
        });
    }

    private boolean found(String query) {
        boolean[] found = {false};
        jdbcTemplate.query(query, resultSet -> {
            found[0] = true;
        });
        return found[0];
    }

    private int getCount(String query) {
        int[] count = {0};
        jdbcTemplate.query(query, rs -> {
            count[0] = rs.getInt("count");
        });
        return count[0];
    }

}
