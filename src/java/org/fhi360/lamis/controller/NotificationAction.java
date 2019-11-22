/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.controller;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.fhi360.lamis.model.Patient;
import org.fhi360.lamis.service.beans.ContextProvider;
import org.fhi360.lamis.utility.Constants;

import org.fhi360.lamis.utility.builder.NotificationListBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author user10
 */
public class NotificationAction extends ActionSupport implements ModelDriven, Preparable {

    private Long facilityId;
    private Long patientId;
    private Patient patient;
    private Long userId;
    private final TransactionTemplate transactionTemplate = ContextProvider.getBean(TransactionTemplate.class);
    private final JdbcTemplate jdbcTemplate = ContextProvider.getBean(JdbcTemplate.class);

    private HttpServletRequest request;
    private HttpSession session;

    private ArrayList<Map<String, String>> notificationList = null;
    private ArrayList<Map<String, String>> notificationListCount = null;
    private ArrayList<Map<String, String>> notificationListReport = null;

    private String query;

    @Override
    public void prepare() {
        request = ServletActionContext.getRequest();
        session = request.getSession();
        facilityId = (Long) session.getAttribute("facilityId");
        userId = (Long) session.getAttribute("userId");

    }

    @Override
    public Object getModel() {
        //patient = new Patient();

        return "";
    }

//    Queries for the Notification count...
    //Enrolled but not on treatment
    public String enrolledNonTx() {
        try {
            notificationList = new ArrayList<>();
            notificationListCount = new ArrayList<>();

            query = "SELECT COUNT(patient_id) AS notification_count FROM patient WHERE facility_id = '" + facilityId + "' AND current_status IN ( " + Constants.ClientStatus.ON_CARE + ") AND date_started IS NULL";

            try {
                jdbcTemplate.query(query, resultSet -> {
                    Map<String, String> map = new HashMap<>();
                    map = new NotificationListBuilder().buildNotificationListCount(1, resultSet);
                    notificationListCount.add(map);
                    if (!map.get("enrolled").equals("0")) {
                        notificationList.add(map);
                    }
                    lostUnconfirmed();
                    return null; //To change body of generated lambdas, choose Tools | Templates.
                });

            } catch (Exception ex) {
                ex.printStackTrace();

                return ERROR;
            }
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    //Lost To Follow Up UnComfirmed     
    public void lostUnconfirmed() {
        try {
            //query="SELECT COUNT(DISTINCT patient_id) as notification_count FROM patient WHERE facility_id = " + facilityId + " AND current_status IN ( " + Constants.ClientStatus.ON_CARE + ") AND TIMESTAMPDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > " + Constants.Reports.LTFU_PEPFAR + " AND date_started IS NOT NULL";
            query = "SELECT COUNT(DISTINCT patient_id) as notification_count FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL";

            jdbcTemplate.query(query, resultSet -> {
                Map<String, String> map = new HashMap<>();
                map = new NotificationListBuilder().buildNotificationListCount(2, resultSet);
                //System.out.println("Map is: " +map);
                notificationListCount.add(map);
                if (!map.get("lostUnconfirmed").equals("0")) {
                    notificationList.add(map);
                }

                treatmentNoVl();
                return null;
            });
        } catch (Exception e) {

        }
    }

    //On treatment but no first ARV dispensed
    public void treatmentNoArv() {
        try {
            query = "SELECT COUNT(patient_id) AS notification_count FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND date_last_refill IS NULL ";

            jdbcTemplate.query(query, resultSet -> {

                Map<String, String> map = new HashMap<>();
                map = new NotificationListBuilder().buildNotificationListCount(3, resultSet);
                //notificationList.add(map);
                notificationListCount.add(map);
                if (!map.get("txNoArv").equals("0")) {
                    notificationList.add(map);
                }
                treatmentNoVl();
                return null;
            });
        } catch (Exception e) {

        }
    }

    //On treatment for 6 months but no VL at Baseline
    public void treatmentNoVl() {
        try {
            query = "SELECT COUNT(patient_id) AS notification_count FROM patient WHERE facility_id = " + facilityId + " AND viral_load_due_date <= CURDATE()";

            jdbcTemplate.query(query, resultSet -> {
                Map<String, String> map = new HashMap<>();
                map = new NotificationListBuilder().buildNotificationListCount(3, resultSet);
                //notificationList.add(map);
                notificationListCount.add(map);
                if (!map.get("txNoVl").equals("0")) {
                    notificationList.add(map);
                }
                viralUnsupressed();
                return null;
            });
        } catch (Exception e) {

        }
    }

    //VL monitoring un-suppressed
    public void viralUnsupressed() {
        try {
            query = "SELECT COUNT(patient_id) AS notification_count FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND last_viral_load >=1000";

            jdbcTemplate.query(query, resultSet -> {
                Map<String, String> map = new HashMap<>();
                map = new NotificationListBuilder().buildNotificationListCount(4, resultSet);

                notificationListCount.add(map);
                if (!map.get("vlUnsupressed").equals("0")) {
                    notificationList.add(map);
                }
                return null;
            });
        } catch (Exception e) {

        }
    }

    /*End of queries for Notification count... */
 /*Beginning of Query for Notification Grid... */
    //Enrolled but not on treatment
    public String enrolledNonTxReport() {

        String returned = ERROR;
        try {
            Integer entity = Integer.parseInt(request.getParameter("entity"));
            notificationListReport = null;

            switch (entity) {
                case 1:
                    notificationListReport = new ArrayList<Map<String, String>>();

                    query = "SELECT * FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_CARE + ") AND date_started IS NULL";

                    try {
                        jdbcTemplate.query(query, resultSet -> {

                            notificationListReport = new NotificationListBuilder().buildNotificationListData(resultSet);
                            return null;
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        returned = ERROR;
                    }
                    returned = SUCCESS;
                    break;

                case 2:
                    query = "SELECT * FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND DATEDIFF(DAY, date_last_refill + last_refill_duration, CURDATE()) > " + Constants.LTFU.PEPFAR + " AND date_started IS NOT NULL";

                    notificationListReport = null;

                    try {
                        jdbcTemplate.query(query, resultSet -> {
                            notificationListReport = new ArrayList<>();

                            notificationListReport = new NotificationListBuilder().buildNotificationListData(resultSet);
                            return null;
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        returned = ERROR;
                    }
                    returned = SUCCESS;
                    break;

                case 3:
                    query = "SELECT * FROM patient WHERE facility_id = " + facilityId + " AND viral_load_due_date <= CURDATE()";

                    notificationListReport = null;
                    try {
                        jdbcTemplate.query(query, resultSet -> {
                            notificationListReport = new ArrayList<>();
                            notificationListReport = new NotificationListBuilder().buildNotificationListData(resultSet);

                            return null;
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        returned = ERROR;
                    }
                    returned = SUCCESS;
                    break;

                case 4:
                    query = "SELECT * FROM patient WHERE facility_id = " + facilityId + " AND current_status IN (" + Constants.ClientStatus.ON_TREATMENT + ") AND last_viral_load >=1000";

                    notificationListReport = null;
                    try {
                        jdbcTemplate.query(query, resultSet -> {
                            notificationListReport = new ArrayList<>();
                            notificationListReport = new NotificationListBuilder().buildNotificationListData(resultSet);
                            //notificationListReport.add(map);

                            return null;
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        returned = ERROR;
                    }
                    returned = SUCCESS;
                    break;
            }
        } catch (Exception e) {
            returned = ERROR;
        }
        return returned;
    }

    //VL monitoring un-suppressed
    public void viralUnsupressedReport() {
    }

    //DRUG AND LABTEST PRESCRIPTIONS...
    //Drug Prescriptions..
    //SELECT p.* FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = 1 AND prescription_type = 'drug'
    public String drugsPrescribedNotification() {
        notificationList = new ArrayList<>();
        notificationListCount = new ArrayList<>();
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT COUNT (DISTINCT CONCAT(p.patient_id, p.facility_id)) AS notification_count FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND pr.prescription_type = 'drug'";

        try {
            jdbcTemplate.query(query, resultSet -> {
                Map<String, String> map = new HashMap<>();
                map = new NotificationListBuilder().buildPrescriptionNotificationCount(1, resultSet);
                //notificationList.add(map);
                notificationListCount.add(map);
                if (!map.get("prescriptions").equals("0")) {
                    notificationList.add(map);
                }

                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
    }

    //Lab Test
    public String labtestPrescribedNotification() {
        notificationList = new ArrayList<>();
        notificationListCount = new ArrayList<>();
        facilityId = (Long) session.getAttribute("facilityId");
        query = "SELECT COUNT (DISTINCT CONCAT(p.patient_id, p.facility_id)) AS notification_count FROM patient p JOIN prescription pr ON p.patient_id = pr.patient_id WHERE p.facility_id = " + facilityId + " AND pr.status = " + Constants.Prescription.PRESCRIBED + " AND pr.prescription_type = 'labtest'";

        try {
            jdbcTemplate.query(query, resultSet -> {
                Map<String, String> map = new HashMap<>();
                map = new NotificationListBuilder().buildPrescriptionNotificationCount(2, resultSet);
                //notificationList.add(map);
                notificationListCount.add(map);
                if (!map.get("labtests").equals("0")) {
                    notificationList.add(map);
                }
                return null;
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return ERROR;
        }

        return SUCCESS;
    }

    //End of Query for Notification Grid...
    public ArrayList<Map<String, String>> getNotificationList() {
        return notificationList;
    }

    public void setNotificaionList(ArrayList<Map<String, String>> notificationList) {
        this.notificationList = notificationList;
    }

    public ArrayList<Map<String, String>> getNotificationListReport() {
        return notificationListReport;
    }

    public void setNotificationListReport(ArrayList<Map<String, String>> notificationListReport) {
        this.notificationListReport = notificationListReport;
    }

    public ArrayList<Map<String, String>> getNotificationListCount() {
        return notificationListCount;
    }

    public void setNotificationListCount(ArrayList<Map<String, String>> notificationListCount) {
        this.notificationListCount = notificationListCount;
    }

}
