/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller.grid;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;
import org.fhi360.lamis.utility.DateUtil;
import org.fhi360.lamis.utility.PatientNumberNormalizer;
import org.fhi360.lamis.utility.Scrambler;
import org.fhi360.lamis.utility.builder.CaseManagerListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.xwork.StringUtils;

/**
 * @author user10
 */
public class CaseManagerGridAction extends ActionSupport implements Preparable {

    private Integer page;
    private Integer rows;
    private String sidx;
    private String sord;
    private Integer totalpages;
    private Integer currpage;
    private Integer totalrecords;

    private HttpServletRequest request;
    private HttpSession session;
    private String query, final_seive;

    private Long facilityId;

    private ArrayList<Map<String, String>> caseManagerList = new ArrayList<>();
    private ArrayList<Map<String, String>> caseManagerClientsList = new ArrayList<>();
    private ArrayList<Map<String, String>> clientSearchList = new ArrayList<>();
    private Map<String, String> clientsStatusCountMap = new TreeMap<>();
    private Map<String, String> clientsCategoryCountMap = new TreeMap<>();
    private Map<String, String> clientsMap = new TreeMap<>();
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
    }

    public String caseManagerGrid() {
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT * FROM casemanager WHERE facility_id = ?";
        jdbcTemplate.query(query, (ResultSet resultSet) -> {
            new CaseManagerListBuilder().buildCaseManagerList(resultSet);
            caseManagerList = new CaseManagerListBuilder().retrieveCaseManagerList();
            return null;
        }, facilityId);
        return SUCCESS;
    }

    public String caseManagerClientsGrid() {

        try {
            // obtain a JDBC connect and execute query

            // fetch the required records from the database
            facilityId = (Long) session.getAttribute("facilityId");
            String casemanagerId = request.getParameter("casemanagerId");
            if (!casemanagerId.equals("")) {
                if (Long.parseLong(casemanagerId) > 0L) {
                    query = "SELECT * FROM patient WHERE facility_id = " + facilityId
                            + " AND casemanager_id = " + Long.parseLong(casemanagerId);
                    jdbcTemplate.query(query, (ResultSet resultSet) -> {
                        new CaseManagerListBuilder().buildCaseManagerClientsList(resultSet);
                        caseManagerClientsList = new CaseManagerListBuilder().retrieveCaseManagerClientsList();
                        return null;
                    });
                }
            }

        } catch (Exception exception) {
            //  resultSet = null;
            //jdbcUtil.disconnectFromDatabase();  //disconnect from database
            exception.printStackTrace();
        }
        return SUCCESS;
    }

    String internal_query = "";

    public String initClientSearch() {
        //Create a temporary Table to hold values.
        try {
            if (!checkDatabaseTable()) {
                String client_drop = "DROP TABLE clients IF EXISTS";
                transactionTemplate.execute(status -> {
                    jdbcTemplate.execute(client_drop);
                    return null;
                });

                facilityId = (Long) session.getAttribute("facilityId");

                //(
                String client = " CREATE TEMPORARY TABLE IF NOT EXISTS clients "
                        + "(facility_id bigint, patient_id bigint, hospital_num varchar(25),unique_id varchar(25), "
                        + "surname varchar(45), other_names varchar(75), gender varchar(7), "
                        + "state varchar(75), lga varchar(150), date_birth date, age int(11), "
                        + "age_unit varchar(30), address varchar(100), date_started date, "
                        + "current_viral_load double, current_cd4 double, current_cd4p double, "
                        + "current_status varchar(75), status int(11), casemanager_id bigint, "
                        + "pregnant int(11), breastfeeding int(11))";
                //String client = " CREATE VIEW IF NOT EXISTS clients (facility_id INT, patient_id INT, hospital_num TEXT, surname TEXT, other_names TEXT, gender TEXT, date_birth DATE, address TEXT, date_started DATE, current_viral_load INT, current_cd4 INT, current_status TEXT, status INT)";

                transactionTemplate.execute(status -> {
                    jdbcTemplate.execute(client);
                    return null;
                });

                //ACTIVE CLIENTS
                String active_clients = "INSERT INTO clients (facility_id, patient_id, "
                        + "hospital_num,unique_id, surname, other_names, gender, state, lga, "
                        + "date_birth, age, age_unit, address, date_started, "
                        + "current_viral_load, current_cd4, current_cd4p, current_status, "
                        + "status, casemanager_id, pregnant, breastfeeding)"
                        + "SELECT facility_id, patient_id, hospital_num, unique_id, surname, "
                        + "other_names, gender, state, lga, date_birth, age, age_unit, "
                        + "address, date_started, last_viral_load, last_cd4, last_cd4p, "
                        + "current_status, 0, casemanager_id, 0, 0 FROM patient WHERE "
                        + "current_status NOT IN ('Known Death', 'ART Transfer Out', "
                        + "'Pre-ART Transfer Out') AND facility_id =" + facilityId;
                transactionTemplate.execute(status -> {
                    jdbcTemplate.execute(active_clients);
                    return null;
                });

                //Update pregnant and breastfeeding...
                pregnantWomen();
                breastfeedingWomen();

                String query_pregnant = "UPDATE clients SET pregnant = 1 where "
                        + "patient_id IN (SELECT patient_id from pregnants)";

                transactionTemplate.execute(status -> {
                    jdbcTemplate.update(query_pregnant);
                    return null;
                });

                String query_breastfeeding = "UPDATE clients SET breastfeeding = 1 "
                        + "where patient_id IN (SELECT patient_id from breastfeeding)";

                transactionTemplate.execute(status -> {
                    jdbcTemplate.update(query_breastfeeding);
                    return null;
                });

                //GROUP VARIOUS CLIENTS BY STATUS
                String query_client_status = "SELECT * FROM clients";

                jdbcTemplate.query(query_client_status, (ResultSet resultSet1) -> {
                    //Iterate through the result set...
                    while (resultSet1.next()) {
                        long patientId = resultSet1.getLong("patient_id");
                        String currentStatus = resultSet1.getString("current_status");
                        String patient_id = String.valueOf(resultSet1.getLong("patient_id"));
                        String currentViralLoad = resultSet1.getObject("current_viral_load") == null ? "" : resultSet1.getDouble("current_viral_load") == 0.0 ? "" : Double.toString(resultSet1.getDouble("current_viral_load"));
                        String currentCd4 = resultSet1.getObject("current_cd4") == null ? "0" : resultSet1.getDouble("current_cd4") == 0.0 ? "0" : Double.toString(resultSet1.getDouble("current_cd4"));
                        String currentCd4p = resultSet1.getObject("current_cd4p") == null ? "0" : resultSet1.getDouble("current_cd4p") == 0.0 ? "0" : Double.toString(resultSet1.getDouble("current_cd4p"));
                        if (!currentStatus.equals("HIV+ non ART") && !currentStatus.equals("Pre-ART Transfer In")) {
                            String dateStarted = resultSet1.getObject("date_started") == null ? "" : DateUtil.parseDateToString(resultSet1.getDate("date_started"), "yyyy-MM-dd");
                            if (!"".equals(dateStarted)) {
                                LocalDate startDate = LocalDate.parse(dateStarted);
                                LocalDate today = LocalDate.now();
                                Period intervalPeriod = Period.between(startDate, today);
                                if (intervalPeriod.getYears() >= 1) { //START WORKING ON STABLE OR UNSTABLE
                                    if (currentStatus.equals("Stopped Treatment") && currentStatus.equals("Lost to Follow Up")) {
                                        internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                        transactionTemplate.execute((ts) -> {
                                            jdbcTemplate.update(internal_query);
                                            return null;
                                        });

                                    } else {
                                        //Check for the stablilty or unstablility...
                                        if (preceedingOis(patientId) == 0) { //-- Preceeding Ois
                                            if (clinicVisits(patientId) >= 5) { //-- Clinic Visits
                                                if (!"".equals(currentViralLoad)) { //-- Viral Load
                                                    if (Double.parseDouble(currentViralLoad) < 1000) {
                                                        internal_query = "UPDATE clients SET status = " + Constants.CaseManager.STABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                        transactionTemplate.execute((ts) -> {
                                                            jdbcTemplate.update(internal_query);
                                                            return null;
                                                        });

                                                    } else {
                                                        internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                        transactionTemplate.execute((ts) -> {
                                                            jdbcTemplate.update(internal_query);
                                                            return null;
                                                        });
                                                    }
                                                } else {
                                                    if ("0".equals(currentCd4)) {
                                                        if (!"0".equals(currentCd4p)) {
                                                            if (Double.parseDouble(currentCd4p) > 250) {
                                                                internal_query = "UPDATE clients SET status = " + Constants.CaseManager.STABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                                transactionTemplate.execute((ts) -> {
                                                                    jdbcTemplate.update(internal_query);
                                                                    return null;
                                                                });
                                                            } else {
                                                                internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                                transactionTemplate.execute((ts) -> {
                                                                    jdbcTemplate.update(internal_query);
                                                                    return null;
                                                                });
                                                            }
                                                        } else {
                                                            internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                            transactionTemplate.execute((ts) -> {
                                                                jdbcTemplate.update(internal_query);
                                                                return null;
                                                            });
                                                        }
                                                    } else {
                                                        if (Double.parseDouble(currentCd4) > 250) {
                                                            internal_query = "UPDATE clients SET status = " + Constants.CaseManager.STABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                            transactionTemplate.execute((ts) -> {
                                                                jdbcTemplate.update(internal_query);
                                                                return null;
                                                            });
                                                        } else {
                                                            internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                            transactionTemplate.execute((ts) -> {
                                                                jdbcTemplate.update(internal_query);
                                                                return null;
                                                            });
                                                        }
                                                    }
                                                }
                                            } else {//UNSTABLE
                                                internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                                transactionTemplate.execute((ts) -> {
                                                    jdbcTemplate.update(internal_query);
                                                    return null;
                                                });
                                            }
                                        } else {//UNSTABLE
                                            internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " WHERE patient_id = " + patientId;
                                            transactionTemplate.execute((ts) -> {
                                                jdbcTemplate.update(internal_query);
                                                return null;
                                            });
                                        }
                                    }
                                } else { //UNSTABLE NOT ONE YEAR = 3
                                    if (currentStatus.equals("Stopped Treatment") && currentStatus.equals("Lost to Follow Up")) {
                                        internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_NOT_ONE_YEAR + " WHERE patient_id = " + patientId;
                                        transactionTemplate.execute((ts) -> {
                                            jdbcTemplate.update(internal_query);
                                            return null;
                                        });
                                    } else {
                                        internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_NOT_ONE_YEAR + " WHERE patient_id = " + patientId;
                                        transactionTemplate.execute((ts) -> {
                                            jdbcTemplate.update(internal_query);
                                            return null;
                                        });
                                    }
                                }
                            } else { //UNSTABLE NOT ONE YEAR = 3
                                internal_query = "UPDATE clients SET status = " + Constants.CaseManager.UNSTABLE_NOT_ONE_YEAR + " WHERE patient_id = " + patientId;
                                transactionTemplate.execute((ts) -> {
                                    jdbcTemplate.update(internal_query);
                                    return null;
                                });
                            }
                        } else {
                            //Pre-ART = 4
                            internal_query = "UPDATE clients SET status = " + Constants.CaseManager.PRE_ART + " WHERE patient_id = " + patientId;
                            transactionTemplate.execute((ts) -> {
                                jdbcTemplate.update(internal_query);
                                return null;
                            });
                        }

                    }
                    return null;
                });
                clientsMap.put("Clients", "success");
            } else {
                clientsMap.put("Clients", "success");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return SUCCESS;
    }

    private boolean checkDatabaseTable() throws Exception {
        boolean isConnected = false;
        DatabaseMetaData dbm = jdbcTemplate.getDataSource().getConnection().getMetaData();
        // check if "employee" table is there
        ResultSet tables = dbm.getTables(null, null, "CLIENTS", null);
        if (tables.next()) {
            // Table exists
            isConnected = true;
        } else {
            // Table does not exist
            isConnected = false;
        }

        return isConnected;
    }

    public String clientSearchGrid() {

        Map<String, Object> pagerParams = null;
        String seive_query = "";

        try {
            //Initilaize the Client Search
            //initClientSearch();

            if (checkDatabaseTable()) {
                pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "clients");

                int start = (Integer) pagerParams.get("start");
                int numberOfRows = getRows().intValue();
                System.out.println("Retriving clients......");

                //Call for Grid..
                facilityId = (Long) session.getAttribute("facilityId");
                query = "SELECT clients.* FROM clients WHERE facility_id = " + facilityId;

                System.out.println("Retriving clients   2......");
                if (StringUtils.isNotBlank(request.getParameter("gender")) && !request.getParameter("gender").trim().equals("--All--")) {
                    query += " AND gender = '" + request.getParameter("gender") + "'";
                }
                //TODO: Arrange Age Group:
                if (StringUtils.isNotBlank(request.getParameter("ageGroup")) && !request.getParameter("ageGroup").trim().equals("0")) {
                    String ageGroup = request.getParameter("ageGroup").trim();
                    String[] ageRange = ageGroup.split("-");
                    query += " AND TIMESTAMPDIFF(YEAR, date_birth, CURDATE()) >= " + Integer.parseInt(ageRange[0].toString()) + " AND TIMESTAMPDIFF(YEAR, date_birth, CURDATE()) <= " + Integer.parseInt(ageRange[1].toString());
                }
                if (StringUtils.isNotBlank(request.getParameter("state"))) {
                    query += " AND state = '" + request.getParameter("state") + "'";
                }
                if (StringUtils.isNotBlank(request.getParameter("lga"))) {
                    query += " AND lga = '" + request.getParameter("lga") + "'";
                }
                //Pregnancy Status
                if (StringUtils.isNotBlank(request.getParameter("pregnancyStatus")) && !request.getParameter("pregnancyStatus").trim().equals("--All--")) {
                    System.out.println("Pregnancy Status is: " + request.getParameter("pregnancyStatus"));
                    if (Integer.parseInt(request.getParameter("pregnancyStatus")) == 1) {
                        query += " AND pregnant = 1";
                    } else if (Integer.parseInt(request.getParameter("pregnancyStatus")) == 2) {
                        query += " AND breastfeeding = 1";
                    }
                }

                if (StringUtils.isNotBlank(request.getParameter("categoryId"))) {
                    String categoryId = (request.getParameter("categoryId").trim());
                    if (categoryId.equals("0")) {
                        query += " AND (status = " + Constants.CaseManager.STABLE_ONE_YEAR + " OR status = " + Constants.CaseManager.UNSTABLE_NOT_ONE_YEAR + " OR status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR + " OR status = " + Constants.CaseManager.PRE_ART + ")";
                    } else if (categoryId.equals("1")) {
                        query += " AND status = " + Constants.CaseManager.STABLE_ONE_YEAR;
                    } else if (categoryId.equals("2")) {
                        query += " AND status = " + Constants.CaseManager.UNSTABLE_NOT_ONE_YEAR;
                    } else if (categoryId.equals("3")) {
                        query += " AND status = " + Constants.CaseManager.UNSTABLE_ONE_YEAR;
                    } else if (categoryId.equals("4")) {
                        query += " AND status = " + Constants.CaseManager.PRE_ART;
                    }
                }

                if (StringUtils.isNotBlank(request.getParameter("showAssigned"))) {
                    if (Boolean.valueOf(request.getParameter("showAssigned")) == true) {
                        query += " AND (casemanager_id IS NOT NULL AND casemanager_id > 0)";
                    } else {
                        query += " AND (casemanager_id IS NULL OR casemanager_id = 0) ";
                    }
                }
                if (StringUtils.isNotBlank(request.getParameter("hospitalNum"))){
                    String hospitalNum = request.getParameter("hospitalNum");
                    query += " AND ((UPPER(hospital_num) like UPPER('%" + hospitalNum + "%')) OR ("
                            + "UPPER(unique_id) like UPPER('%" + hospitalNum + "%'))) ";
                }
                System.out.println("Retriving clients    3......");

                seive_query = query.replace("clients.*", "status, COUNT (clients.patient_id) AS count");
                seive_query += " GROUP BY status ORDER BY status ASC";

                System.out.println("Sieve Query is: " + seive_query);
                session.setAttribute("query", seive_query);
                query += " ORDER BY hospital_num, address ASC LIMIT " + start + " , " + numberOfRows;
                System.out.println("Retriving clients   4......" + query);

                jdbcTemplate.query(query, (ResultSet resultSet) -> {
                    try {
                        new CaseManagerListBuilder().buildClientSearchList(resultSet);
                          clientSearchList = new CaseManagerListBuilder().retrieveClientSearchList();
                    } catch (Exception ex) {

                    }
                    return null;
                });
              
                System.out.println("Retriving clients   5......");
            }
        } catch (Exception exception) {
            //   resultSet = null;
            //jdbcUtil.disconnectFromDatabase();  //disconnect from database
            exception.printStackTrace();
        }

        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");

        return SUCCESS;
    }

    public String getAssignedClientsTreatmentStatus() {
        try {
            //Initilaize the Client Search
            //initClientSearch();
            //Call for Grid..
            facilityId = (Long) session.getAttribute("facilityId");
            Integer casemanagerId;
            if (request.getParameter("casemanagerId") != null && !request.getParameter("casemanagerId").trim().isEmpty()) {
                casemanagerId = Integer.valueOf(request.getParameter("casemanagerId"));

                String internalQuery = "SELECT DISTINCT status as treatment_status, COUNT(status) as treatment_status_count FROM clients WHERE facility_id = " + facilityId + " AND casemanager_id = " + casemanagerId + " GROUP BY status oRDER BY status ASC";
                jdbcTemplate.query(internalQuery, (ResultSet internalResultSet) -> {
                    while (internalResultSet.next()) {
                        Integer treatmentStatus = internalResultSet.getInt("treatment_status");
                        Integer treatmentStatusCount = internalResultSet.getInt("treatment_status_count");
                        switch (treatmentStatus) {
                            case 1:
                                clientsStatusCountMap.put("stable", Integer.toString(treatmentStatusCount));
                                break;
                            case 2:
                                clientsStatusCountMap.put("unstable_less", Integer.toString(treatmentStatusCount));
                                break;
                            case 3:
                                clientsStatusCountMap.put("unstable_more", Integer.toString(treatmentStatusCount));
                                break;
                            case 4:
                                clientsStatusCountMap.put("preart", Integer.toString(treatmentStatusCount));
                                break;
                            default:
                                break;
                        }
                    }
                    return null;
                });

                //System.out.println("The data is: "+clientsStatusCountMap);
            }
        } catch (Exception exception) {
            // resultSet = null;
            //jdbcUtil.disconnectFromDatabase();  //disconnect from database
            exception.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
    }

    public String clientGrid() {
        long facilityId = (Long) session.getAttribute("facilityId");
        Scrambler scrambler = new Scrambler();
        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "clients");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {

            if (request.getParameterMap().containsKey("name")) {
                String name = scrambler.scrambleCharacters(request.getParameter("name"));
                if (name == null || name.isEmpty()) {
                    query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
                    if (request.getParameterMap().containsKey("female")) {
                        query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " AND gender = 'Female' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
                    }
                } else {
                    query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " AND surname LIKE '" + name + "%' OR other_names LIKE '" + name + "%' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
                    if (request.getParameterMap().containsKey("female")) {
                        query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " AND gender = 'Female' AND surname LIKE '" + name + "%' OR other_names LIKE '" + name + "%' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
                    }
                }
            } else {
                query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
                if (request.getParameterMap().containsKey("female")) {
                    query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " AND gender = 'Female' ORDER BY surname ASC LIMIT " + start + " , " + numberOfRows;
                }
            }

            jdbcTemplate.query(query, (ResultSet resultSet) -> {
                try {
                    new CaseManagerListBuilder().buildClientSearchList(resultSet);
                } catch (Exception ex) {

                }
                clientSearchList = new CaseManagerListBuilder().retrieveClientSearchList();
                System.out.println(clientSearchList);
                return null;
            });

        } catch (Exception exception) {
            //resultSet = null;
            //jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }

        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }

    public String clientGridByNumber() {
        long facilityId = (Long) session.getAttribute("facilityId");
        String hospitalNum = request.getParameter("hospitalNum");

        Map<String, Object> pagerParams = new PaginationUtil().paginateGrid(getPage().intValue(), getRows().intValue(), "clients");
        int start = (Integer) pagerParams.get("start");
        int numberOfRows = getRows().intValue();
        try {
            // fetch the required records from the database

            if (hospitalNum == null || hospitalNum.isEmpty()) {
                query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " ORDER BY current_status ASC LIMIT " + start + " , " + numberOfRows;
            } else {
                query = "SELECT * FROM clients WHERE facility_id = " + facilityId + " AND TRIM(LEADING '0' FROM hospital_num) LIKE '" + PatientNumberNormalizer.unpadNumber(hospitalNum) + "%' ORDER BY current_status ASC LIMIT " + start + " , " + numberOfRows;
            }

            jdbcTemplate.query(query, (ResultSet resultSet) -> {
                try {
                    new CaseManagerListBuilder().buildClientSearchList(resultSet);
                } catch (Exception ex) {
                }
                clientSearchList = new CaseManagerListBuilder().retrieveClientSearchList();
                return null;
            });

        } catch (Exception exception) {
            //resultSet = null;
            //jdbcUtil.disconnectFromDatabase();  //disconnect from database
        }
        page = (Integer) pagerParams.get("page");
        currpage = (Integer) pagerParams.get("page");
        totalpages = (Integer) pagerParams.get("totalpages");
        totalrecords = (Integer) pagerParams.get("totalrecords");
        return SUCCESS;
    }

    double baselineCd4 = 0.0;

    private double baselineCd4(long patientId) {

        String query = "SELECT cd4 FROM clinic WHERE patient_id = " + patientId + " AND commence = 1";
        jdbcTemplate.query(query, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                String baselineCd4Str = resultSet.getObject("cd4") == null ? "0" : resultSet.getDouble("cd4") == 0.0 ? "0" : Double.toString(resultSet.getDouble("cd4"));
                if (!baselineCd4Str.equals("0")) {
                    baselineCd4 = Double.parseDouble(baselineCd4Str);
                }
            }
            clientSearchList = new CaseManagerListBuilder().retrieveClientSearchList();
            return null;
        });

        return baselineCd4;
    }

    private void pregnantWomen() {

        try {
            String client_drop = "DROP TABLE pregnants IF EXISTS";
            transactionTemplate.execute(status -> {
                jdbcTemplate.execute(client_drop);
                return null;
            });

            facilityId = (Long) session.getAttribute("facilityId");

            String pregnants = " CREATE TEMPORARY TABLE IF NOT EXISTS pregnants (patient_id bigint, facility_id int,"
                    + " date_visit date)";
            transactionTemplate.execute(status -> {
                jdbcTemplate.execute(pregnants);
                return null;
            });

            String pregnant_clients = "INSERT INTO pregnants (patient_id, facility_id, date_visit)"
                    + " SELECT DISTINCT patient_id, facility_id, date_visit FROM clinic WHERE pregnant = 1 "
                    + "AND facility_id =" + facilityId + " ORDER BY date_visit DESC";
            transactionTemplate.execute(status -> {
                jdbcTemplate.update(pregnant_clients);
                return null;
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void breastfeedingWomen() {
        String client_drop = "DROP TABLE breastfeeding IF EXISTS";
        transactionTemplate.execute(status -> {
            jdbcTemplate.execute(client_drop);
            return null;
        });
        facilityId = (Long) session.getAttribute("facilityId");

        String client = " CREATE TEMPORARY TABLE IF NOT EXISTS breastfeeding (patient_id bigint, facility_id bigint, "
                + "date_visit date)";

        transactionTemplate.execute(status -> {
            jdbcTemplate.execute(client);
            return null;
        });
        String active_clients = "INSERT INTO breastfeeding ( patient_id, facility_id, date_visit)"
                + "SELECT DISTINCT patient_id, facility_id, date_visit FROM clinic WHERE "
                + "breastfeeding = 1 AND facility_id =" + facilityId + " ORDER BY date_visit DESC";

        transactionTemplate.execute(status -> {
            jdbcTemplate.update(active_clients);
            return null;
        });
    }

    private int clinicVisitCount = 0;

    private int clinicVisits(long patientId) {

        String query = "SELECT count(*) AS count FROM clinic WHERE patient_id = " + patientId + " "
                + "AND date_visit >= DATEADD(MONTH, -12, CURDATE()) AND date_visit <= CURDATE()";

        jdbcTemplate.query(query, ((resultSet) -> {
            while (resultSet.next()) {
                clinicVisitCount = resultSet.getInt("count");
            }
            return null;
        }));

        return clinicVisitCount;
    }

    private int preceedingOis(long patientId) {
        String query = "SELECT count(*) AS count FROM clinic WHERE patient_id = " + patientId + " AND date_visit >= "
                + "DATEADD(MONTH, -16, CURDATE()) AND date_visit <= CURDATE() AND oi_ids IS NOT NULL AND oi_ids != ''";
        jdbcTemplate.query(query, ((resultSet) -> {
            while (resultSet.next()) {
                clinicVisitCount = resultSet.getInt("count");
            }
            return null;
        }));

        return clinicVisitCount;
    }

    boolean recentTxNew = false;

    private boolean isRecentTxNew(long patientId) {

        String query = "SELECT patient_id FROM patient WHERE patient_id = " + patientId + " "
                + "AND TIMESTAMPDIFF(MONTH, date_started, CURDATE()) < 12";

        jdbcTemplate.query(query, ((resultSet) -> {
            while (resultSet.next()) {
                recentTxNew = true;
            }
            return null;
        }));

        return recentTxNew;
    }

    //    private int countStatus(Integer option){
//
//        int counts = 0;
//        String query = "SELECT COUNT(status) AS count FROM clients WHERE status = "+option;
//        JDBCUtil jdbcUtil = null;
//        try{
//            jdbcUtil = new JDBCUtil();
//            PreparedStatement preparedStatement = jdbcUtil.getStatement(query);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                counts = resultSet.getInt("count");
//                if(option == 1)
//                    clientsCategoryCountMap.put("stable", Integer.toString(counts));
//                else if(option == 2)
//                    clientsCategoryCountMap.put("unstable", Integer.toString(counts));
//                else if(option == 3)
//                    clientsCategoryCountMap.put("tx_new", Integer.toString(counts));
//                else if(option == 4)
//                    clientsCategoryCountMap.put("preart", Integer.toString(counts));
//            }
//        }catch(Exception ex){
//            jdbcUtil.disconnectFromDatabase();
//            ex.printStackTrace();
//        }
//
//        return counts;
//    }
    public String countCategories() {
        facilityId = (Long) session.getAttribute("facilityId");

        //String internalQuery = "SELECT DISTINCT status as option, COUNT(status) AS count FROM clients WHERE facility_id = "+facilityId+" GROUP BY status ORDER BY status ASC";
        if (session.getAttribute("query") != null) {
            final_seive = (String) session.getAttribute("query");
            jdbcTemplate.query(final_seive, ((internalResultSet) -> {
                while (internalResultSet.next()) {
                    Integer defaultCount = 0;
                    Integer count = internalResultSet.getInt("count");
                    Integer option = internalResultSet.getInt("status");
                    //System.out.println("Data is: "+count+" and option is: "+option);
                    if (option == 1) {
                        clientsCategoryCountMap.put("stable", Integer.toString(count));
                    } else {
                        if (!clientsCategoryCountMap.containsKey("stable")) {
                            clientsCategoryCountMap.put("stable", Integer.toString(defaultCount));
                        }
                    }
                    if (option == 2) {
                        clientsCategoryCountMap.put("unstable_less", Integer.toString(count));
                    } else {
                        if (!clientsCategoryCountMap.containsKey("unstable_less")) {
                            clientsCategoryCountMap.put("unstable_less", Integer.toString(defaultCount));
                        }
                    }
                    if (option == 3) {
                        clientsCategoryCountMap.put("unstable_more", Integer.toString(count));
                    } else {
                        if (!clientsCategoryCountMap.containsKey("unstable_more")) {
                            clientsCategoryCountMap.put("unstable_more", Integer.toString(defaultCount));
                        }
                    }
                    if (option == 4) {
                        clientsCategoryCountMap.put("preart", Integer.toString(count));
                    } else {
                        if (!clientsCategoryCountMap.containsKey("preart")) {
                            clientsCategoryCountMap.put("preart", Integer.toString(defaultCount));
                        }
                    }
                }
                return null;
            }));

        }
        return SUCCESS;
    }

    /*
     * @return the page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the limit
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @param limit the limit to set
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @return the sidx
     */
    public String getSidx() {
        return sidx;
    }

    /**
     * @param sidx the sidx to set
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    /**
     * @return the sord
     */
    public String getSord() {
        return sord;
    }

    /**
     * @param sord the sord to set
     */
    public void setSord(String sord) {
        this.sord = sord;
    }

    /**
     * @return the totalpages
     */
    public Integer getTotalpages() {
        return totalpages;
    }

    /**
     * @param totalpages the totalpages to set
     */
    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    /**
     * @return the currpage
     */
    public Integer getCurrpage() {
        return currpage;
    }

    /**
     * @param currpage the currpage to set
     */
    public void setCurrpage(Integer currpage) {
        this.currpage = currpage;
    }

    /**
     * @return the totalrecords
     */
    public Integer getTotalrecords() {
        return totalrecords;
    }

    /**
     * @param totalrecords the totalrecords to set
     */
    public void setTotalrecords(Integer totalrecords) {
        this.totalrecords = totalrecords;
    }

    /**
     * @return the laboratoryList
     */
    public ArrayList<Map<String, String>> getCaseManagerList() {
        return caseManagerList;
    }

    /**
     * @param laboratoryList the laboratoryList to set
     */
    public void setCaseManagerList(ArrayList<Map<String, String>> caseManagerList) {
        this.caseManagerList = caseManagerList;
    }

    public ArrayList<Map<String, String>> getCaseManagerClientsList() {
        return caseManagerClientsList;
    }

    public void setCaseManagerClientsList(ArrayList<Map<String, String>> caseManagerClientsList) {
        this.caseManagerClientsList = caseManagerClientsList;
    }

    public ArrayList<Map<String, String>> getClientSearchList() {
        return clientSearchList;
    }

    public void setClientSearchList(ArrayList<Map<String, String>> clientSearchList) {
        this.clientSearchList = clientSearchList;
    }

    public Map<String, String> getClientsStatusCountMap() {
        return clientsStatusCountMap;
    }

    public void setClientsStatusCountMap(Map<String, String> clientsStatusCountMap) {
        this.clientsStatusCountMap = clientsStatusCountMap;
    }

    public Map<String, String> getClientsCategoryCountMap() {
        return clientsCategoryCountMap;
    }

    public void setClientsCategoryCountMap(Map<String, String> clientsCategoryCountMap) {
        this.clientsCategoryCountMap = clientsCategoryCountMap;
    }

    public Map<String, String> getClientsMap() {
        return clientsMap;
    }

    public void setClientsMap(Map<String, String> clientsMap) {
        this.clientsMap = clientsMap;
    }

}
